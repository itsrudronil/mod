package com.rudronil.twox.module;

import com.rudronil.twox.TwoXClient;
import net.minecraft.client.Minecraft;

public class ZoomModule extends Module {
    private boolean zooming = false;
    private int savedFov = 70;

    public ZoomModule() { super("Zoom", Category.VISUAL, Anchor.NONE, true); }

    @Override
    public void onTick() {
        Minecraft mc = Minecraft.getInstance();
        boolean down = TwoXClient.zoomKey != null && TwoXClient.zoomKey.isDown() && mc.screen == null;
        if (down && !zooming) {
            savedFov = mc.options.fov().get();
            mc.options.fov().set(30);
            zooming = true;
        } else if (!down && zooming) {
            mc.options.fov().set(savedFov);
            zooming = false;
        }
    }

    @Override
    public void onDisable() {
        if (zooming) {
            Minecraft.getInstance().options.fov().set(savedFov);
            zooming = false;
        }
    }
}
