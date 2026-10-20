package com.rudronil.twox;

import com.mojang.blaze3d.platform.InputConstants;
import com.rudronil.twox.module.CombatTracker;
import com.rudronil.twox.module.CpsTracker;
import com.rudronil.twox.module.ModuleManager;
import com.rudronil.twox.module.WaypointModule;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TwoXClient implements ClientModInitializer {
    public static final String MOD_ID = "twox_client";
    public static final String NAME = "2X Client";
    public static final String AUTHOR = "Its Rudronil";
    public static final Logger LOGGER = LoggerFactory.getLogger(NAME);

    public static KeyMapping openMenuKey;
    public static KeyMapping zoomKey;

    @Override
    public void onInitializeClient() {
        LOGGER.info("{} by {} loaded!", NAME, AUTHOR);

        KeyMapping.Category category = KeyMapping.Category.register(Identifier.fromNamespaceAndPath(MOD_ID, "main"));
        openMenuKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.twox_client.menu", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT, category));
        zoomKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.twox_client.zoom", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_C, category));

        CombatTracker.init();
        WaypointModule.registerCommands();

        // Modules are created once the game has finished starting (options must be loaded).
        ClientLifecycleEvents.CLIENT_STARTED.register(mc -> ModuleManager.init());
        ClientLifecycleEvents.CLIENT_STOPPING.register(mc -> ModuleManager.shutdown());

        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            while (openMenuKey.consumeClick()) mc.setScreen(new ClickGuiScreen());
            ModuleManager.tick();
        });

        // Custom crosshair / hide scoreboard / hide boss bar wrap the vanilla elements
        HudElementRegistry.replaceElement(VanillaHudElements.CROSSHAIR, original -> (g, tick) -> {
            if (ModuleManager.crosshair != null && ModuleManager.crosshair.isEnabled()) {
                ModuleManager.crosshair.draw(g);
            } else {
                original.render(g, tick);
            }
        });
        HudElementRegistry.replaceElement(VanillaHudElements.SCOREBOARD, original -> (g, tick) -> {
            if (ModuleManager.hideScoreboard == null || !ModuleManager.hideScoreboard.isEnabled()) original.render(g, tick);
        });
        HudElementRegistry.replaceElement(VanillaHudElements.BOSS_BAR, original -> (g, tick) -> {
            if (ModuleManager.hideBossBar == null || !ModuleManager.hideBossBar.isEnabled()) original.render(g, tick);
        });

        // Main 2X HUD layer
        HudElementRegistry.attachElementAfter(
                VanillaHudElements.MISC_OVERLAYS,
                Identifier.fromNamespaceAndPath(MOD_ID, "hud"),
                (g, tick) -> {
                    Minecraft mc = Minecraft.getInstance();
                    CpsTracker.update();
                    if (mc.options.hideGui || mc.screen instanceof HudEditorScreen) return;
                    g.drawString(mc.font, NAME.toUpperCase(), 4, 4, 0xFF55FFFF, true);
                    ModuleManager.renderHud(g, false);
                });
    }
}
