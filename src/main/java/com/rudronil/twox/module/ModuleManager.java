package com.rudronil.twox.module;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.rudronil.twox.TwoXClient;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ModuleManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG = FabricLoader.getInstance().getConfigDir().resolve("2x-client.json");
    private static final List<Module> MODULES = new ArrayList<>();

    public static void init() {
        Minecraft mc = Minecraft.getInstance();

        MODULES.add(new TextHudModule("FPS", () -> mc.getFps() + " FPS"));
        MODULES.add(new TextHudModule("Coordinates", () -> mc.player == null ? null :
                String.format("XYZ: %.1f / %.1f / %.1f", mc.player.getX(), mc.player.getY(), mc.player.getZ())));
        MODULES.add(new TextHudModule("Direction", () -> mc.player == null ? null :
                "Facing: " + mc.player.getDirection().getName()));
        MODULES.add(new TextHudModule("Ping", () -> {
            if (mc.player == null || mc.getConnection() == null) return null;
            var info = mc.getConnection().getPlayerInfo(mc.player.getUUID());
            return info == null ? null : "Ping: " + info.getLatency() + " ms";
        }));
        MODULES.add(new TextHudModule("Clock", () ->
                LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm a"))));
        MODULES.add(new ArmorHudModule());
        MODULES.add(new ToggleSprintModule());

        load();
    }

    public static List<Module> all() { return MODULES; }

    public static void tick() {
        for (Module m : MODULES) if (m.isEnabled()) m.onTick();
    }

    public static void renderHud(GuiGraphics g) {
        int y = 20;
        for (Module m : MODULES) if (m.isEnabled()) y += m.renderHud(g, 4, y);
    }

    public static void save() {
        Map<String, Boolean> data = new LinkedHashMap<>();
        for (Module m : MODULES) data.put(m.getName(), m.isEnabled());
        try {
            Files.writeString(CONFIG, GSON.toJson(data));
        } catch (IOException e) {
            TwoXClient.LOGGER.error("Could not save config", e);
        }
    }

    private static void load() {
        if (!Files.exists(CONFIG)) return;
        try {
            Map<String, Boolean> data = GSON.fromJson(Files.readString(CONFIG),
                    new TypeToken<Map<String, Boolean>>() {}.getType());
            if (data == null) return;
            for (Module m : MODULES) {
                Boolean b = data.get(m.getName());
                if (b != null) m.setEnabled(b);
            }
        } catch (Exception e) {
            TwoXClient.LOGGER.error("Could not load config", e);
        }
    }
}
