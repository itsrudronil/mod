package com.rudronil.twox.module;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.rudronil.twox.TwoXClient;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * HUD list of nearby waypoints. Commands:
 *  /2x wp add <name>   /2x wp remove <name>   /2x wp list
 */
public class WaypointModule extends Module {
    public static class Waypoint {
        public String name;
        public double x, y, z;
        public String dim;
    }

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("2x-waypoints.json");
    private static final List<Waypoint> LIST = new ArrayList<>();
    private static final String[] ARROWS = {"\u2191", "\u2197", "\u2192", "\u2198", "\u2193", "\u2199", "\u2190", "\u2196"};

    public WaypointModule() {
        super("Waypoints", Category.UTILITY, Anchor.RIGHT, true);
        load();
    }

    public static void registerCommands() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
                dispatcher.register(ClientCommandManager.literal("2x")
                        .then(ClientCommandManager.literal("wp")
                                .then(ClientCommandManager.literal("add")
                                        .then(ClientCommandManager.argument("name", StringArgumentType.word())
                                                .executes(ctx -> {
                                                    String n = StringArgumentType.getString(ctx, "name");
                                                    Minecraft mc = Minecraft.getInstance();
                                                    if (mc.player == null) return 0;
                                                    Waypoint w = new Waypoint();
                                                    w.name = n;
                                                    w.x = mc.player.getX();
                                                    w.y = mc.player.getY();
                                                    w.z = mc.player.getZ();
                                                    w.dim = mc.level.dimension().toString();
                                                    LIST.removeIf(o -> o.name.equalsIgnoreCase(n));
                                                    LIST.add(w);
                                                    save();
                                                    ctx.getSource().sendFeedback(Component.literal("Waypoint '" + n + "' added."));
                                                    return 1;
                                                })))
                                .then(ClientCommandManager.literal("remove")
                                        .then(ClientCommandManager.argument("name", StringArgumentType.word())
                                                .executes(ctx -> {
                                                    String n = StringArgumentType.getString(ctx, "name");
                                                    boolean removed = LIST.removeIf(o -> o.name.equalsIgnoreCase(n));
                                                    save();
                                                    ctx.getSource().sendFeedback(Component.literal(
                                                            removed ? "Waypoint '" + n + "' removed." : "No waypoint named '" + n + "'."));
                                                    return 1;
                                                })))
                                .then(ClientCommandManager.literal("list")
                                        .executes(ctx -> {
                                            if (LIST.isEmpty()) {
                                                ctx.getSource().sendFeedback(Component.literal("No waypoints."));
                                            }
                                            for (Waypoint w : LIST) {
                                                ctx.getSource().sendFeedback(Component.literal(String.format(
                                                        "%s: %.0f %.0f %.0f", w.name, w.x, w.y, w.z)));
                                            }
                                            return 1;
                                        })))));
    }

    @Override
    public int renderHud(GuiGraphics g, int x, int y) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return 0;
        String dim = mc.level.dimension().toString();
        double px = mc.player.getX(), pz = mc.player.getZ();

        List<Waypoint> near = new ArrayList<>();
        for (Waypoint w : LIST) if (dim.equals(w.dim)) near.add(w);
        near.sort(Comparator.comparingDouble(w -> Math.hypot(w.x - px, w.z - pz)));

        int used = 0;
        for (int i = 0; i < Math.min(5, near.size()); i++) {
            Waypoint w = near.get(i);
            double dx = w.x - px, dz = w.z - pz;
            double bearing = Math.toDegrees(Math.atan2(-dx, dz));
            double rel = Mth.wrapDegrees(bearing - mc.player.getYRot());
            int idx = (int) Math.round(((rel + 360) % 360) / 45.0) % 8;
            used += line(g, String.format("%s %s %.0fm", ARROWS[idx], w.name, Math.hypot(dx, dz)), x, y + used, 0xFFFFD155);
        }
        return used > 0 ? used + 2 : 0;
    }

    private static void save() {
        try {
            Files.writeString(FILE, GSON.toJson(LIST));
        } catch (Exception e) {
            TwoXClient.LOGGER.error("Could not save waypoints", e);
        }
    }

    private static void load() {
        try {
            if (!Files.exists(FILE)) return;
            List<Waypoint> data = GSON.fromJson(Files.readString(FILE), new TypeToken<List<Waypoint>>() {}.getType());
            if (data != null) LIST.addAll(data);
        } catch (Exception e) {
            TwoXClient.LOGGER.error("Could not load waypoints", e);
        }
    }
}
