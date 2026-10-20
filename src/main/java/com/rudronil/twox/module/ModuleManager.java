package com.rudronil.twox.module;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.rudronil.twox.TwoXClient;
import com.rudronil.twox.module.Module.Anchor;
import com.rudronil.twox.module.Module.Category;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ModuleManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG = FabricLoader.getInstance().getConfigDir().resolve("2x-client.json");
    private static final List<Module> MODULES = new ArrayList<>();

    public static CrosshairModule crosshair;
    public static Module hideScoreboard;
    public static Module hideBossBar;

    private static final String[] COMPASS = {"S", "SW", "W", "NW", "N", "NE", "E", "SE"};

    public static void init() {
        Minecraft mc = Minecraft.getInstance();
        ItemStack pearl = new ItemStack(Items.ENDER_PEARL);
        ItemStack gapple = new ItemStack(Items.GOLDEN_APPLE);

        // ---- HUD ----
        add(new TextHudModule("FPS", Category.HUD, Anchor.LEFT, true, () -> mc.getFps() + " FPS"));
        add(new TextHudModule("Ping", Category.HUD, Anchor.LEFT, true, () -> {
            if (mc.player == null || mc.getConnection() == null) return null;
            var info = mc.getConnection().getPlayerInfo(mc.player.getUUID());
            return info == null ? null : "Ping: " + info.getLatency() + " ms";
        }));
        add(new TextHudModule("Coordinates", Category.HUD, Anchor.LEFT, true, () -> mc.player == null ? null :
                String.format("XYZ: %.1f / %.1f / %.1f", mc.player.getX(), mc.player.getY(), mc.player.getZ())));
        add(new TextHudModule("Direction", Category.HUD, Anchor.LEFT, true, () -> {
            if (mc.player == null) return null;
            float yaw = Mth.wrapDegrees(mc.player.getYRot());
            float norm = (yaw % 360 + 360) % 360;
            String dir = COMPASS[Math.round(norm / 45f) % 8];
            return String.format("Facing: %s (%.0f\u00B0)", dir, yaw);
        }));
        add(new TextHudModule("Speed", Category.HUD, Anchor.LEFT, true, () -> mc.player == null ? null :
                String.format("Speed: %.2f b/s", mc.player.getDeltaMovement().horizontalDistance() * 20)));
        add(new TextHudModule("Server IP", Category.HUD, Anchor.LEFT, true, () -> {
            ServerData sd = mc.getCurrentServer();
            return "Server: " + (sd == null ? "Singleplayer" : sd.ip);
        }));
        add(new TextHudModule("Real Time", Category.HUD, Anchor.LEFT, true, () ->
                LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm:ss a"))));
        add(new TextHudModule("CPS", Category.HUD, Anchor.LEFT, true, () ->
                "CPS: " + CpsTracker.left() + " | " + CpsTracker.right()));
        add(new ArmorHudModule());
        add(new TextHudModule("Item Info", Category.HUD, Anchor.LEFT, true, () -> {
            if (mc.player == null) return null;
            ItemStack s = mc.player.getMainHandItem();
            if (s.isEmpty()) return null;
            String t = s.getHoverName().getString();
            if (s.getCount() > 1) t += " x" + s.getCount();
            if (s.isDamageableItem()) t += "\nDurability: " + (s.getMaxDamage() - s.getDamageValue()) + "/" + s.getMaxDamage();
            return t;
        }));
        add(new PotionModule());
        add(new KeystrokesModule());

        // ---- Combat ----
        add(new TextHudModule("Reach Display", Category.COMBAT, Anchor.LEFT, true, CombatTracker::reachText));
        add(new TextHudModule("Jump Reset Tracker", Category.COMBAT, Anchor.LEFT, true, CombatTracker::jumpText));
        add(new TextHudModule("Attack & Cooldowns", Category.COMBAT, Anchor.LEFT, true, () -> {
            LocalPlayer p = mc.player;
            if (p == null) return null;
            String s = "Attack: " + Math.round(p.getAttackStrengthScale(0f) * 100) + "%";
            float pe = p.getCooldowns().getCooldownPercent(pearl, 0f);
            if (pe > 0) s += "\nPearl CD: " + Math.round(pe * 100) + "%";
            float ga = p.getCooldowns().getCooldownPercent(gapple, 0f);
            if (ga > 0) s += "\nGapple CD: " + Math.round(ga * 100) + "%";
            return s;
        }));

        // ---- Visual ----
        add(new FullbrightModule());
        crosshair = new CrosshairModule();
        add(crosshair);
        add(new ZoomModule());
        add(new TextHudModule("Mini F3", Category.VISUAL, Anchor.LEFT, false, () -> {
            LocalPlayer p = mc.player;
            if (p == null || mc.level == null) return null;
            BlockPos bp = p.blockPosition();
            return String.format("XYZ %.1f %.1f %.1f", p.getX(), p.getY(), p.getZ())
                    + "\nBlock " + bp.getX() + " " + bp.getY() + " " + bp.getZ()
                    + "\nChunk " + (bp.getX() >> 4) + " " + (bp.getZ() >> 4)
                    + "\nBiome " + mc.level.getBiome(bp).getRegisteredName()
                    + "\nLight " + mc.level.getMaxLocalRawBrightness(bp);
        }));
        hideScoreboard = new Module("Hide Scoreboard", Category.VISUAL, Anchor.NONE, false) {};
        hideBossBar = new Module("Hide Boss Bar", Category.VISUAL, Anchor.NONE, false) {};
        add(hideScoreboard);
        add(hideBossBar);

        // ---- Utility ----
        add(new ToggleSprintModule());
        add(new WaypointModule());

        load();
    }

    private static void add(Module m) { MODULES.add(m); }

    public static List<Module> all() { return MODULES; }

    public static List<Module> inCategory(Category c) {
        List<Module> out = new ArrayList<>();
        for (Module m : MODULES) if (m.getCategory() == c) out.add(m);
        return out;
    }

    public static void tick() {
        CombatTracker.tick();
        for (Module m : MODULES) if (m.isEnabled()) m.onTick();
    }

    public static void renderHud(GuiGraphics g) {
        int leftY = 16, rightY = 4;
        for (Module m : MODULES) {
            if (!m.isEnabled()) continue;
            switch (m.getAnchor()) {
                case LEFT -> leftY += m.renderHud(g, 4, leftY);
                case RIGHT -> rightY += m.renderHud(g, g.guiWidth() - 4, rightY);
                case FIXED -> m.renderHud(g, 0, 0);
                default -> {}
            }
        }
    }

    /** Restores vanilla settings (gamma, FOV) and saves config. Called when the game closes. */
    public static void shutdown() {
        for (Module m : MODULES) if (m.isEnabled()) m.onDisable();
        save();
    }

    public static void save() {
        Map<String, Boolean> data = new LinkedHashMap<>();
        for (Module m : MODULES) data.put(m.getName(), m.isEnabled());
        try {
            Files.writeString(CONFIG, GSON.toJson(data));
        } catch (Exception e) {
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
