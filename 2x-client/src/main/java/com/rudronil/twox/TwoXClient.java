package com.rudronil.twox;

import com.mojang.blaze3d.platform.InputConstants;
import com.rudronil.twox.module.ModuleManager;
import net.fabricmc.api.ClientModInitializer;
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

    private static KeyMapping openMenuKey;
    private static KeyMapping zoomKey;
    private static boolean zooming = false;
    private static int savedFov = 70;

    @Override
    public void onInitializeClient() {
        LOGGER.info("{} by {} loaded!", NAME, AUTHOR);

        KeyMapping.Category category = KeyMapping.Category.register(Identifier.fromNamespaceAndPath(MOD_ID, "main"));
        openMenuKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.twox_client.menu", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT, category));
        zoomKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.twox_client.zoom", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_C, category));

        ModuleManager.init();

        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            while (openMenuKey.consumeClick()) mc.setScreen(new ModMenuScreen());
            ModuleManager.tick();
            handleZoom(mc);
        });

        HudElementRegistry.attachElementAfter(
                VanillaHudElements.MISC_OVERLAYS,
                Identifier.fromNamespaceAndPath(MOD_ID, "hud"),
                (graphics, tickCounter) -> {
                    Minecraft mc = Minecraft.getInstance();
                    if (mc.options.hideGui) return;
                    graphics.drawString(mc.font, NAME, 4, 4, 0xFF55FFFF, true);
                    ModuleManager.renderHud(graphics);
                });
    }

    /** Hold the zoom key to zoom in (FOV 30); release to restore. */
    private static void handleZoom(Minecraft mc) {
        if (mc.screen != null) return;
        if (zoomKey.isDown() && !zooming) {
            savedFov = mc.options.fov().get();
            mc.options.fov().set(30);
            zooming = true;
        } else if (!zoomKey.isDown() && zooming) {
            mc.options.fov().set(savedFov);
            zooming = false;
        }
    }
}
