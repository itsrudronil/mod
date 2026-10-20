package com.rudronil.twox.module;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

public class KeystrokesModule extends Module {
    public KeystrokesModule() { super("Keystrokes", Category.HUD, Anchor.FIXED, true); }

    @Override
    public int renderHud(GuiGraphics g, int originX, int originY) {
        Minecraft mc = Minecraft.getInstance();
        int s = 22, gap = 2;
        int x0 = originX, y0 = originY;

        key(g, mc, x0 + s + gap, y0, s, s, "W", mc.options.keyUp);
        key(g, mc, x0, y0 + s + gap, s, s, "A", mc.options.keyLeft);
        key(g, mc, x0 + s + gap, y0 + s + gap, s, s, "S", mc.options.keyDown);
        key(g, mc, x0 + 2 * (s + gap), y0 + s + gap, s, s, "D", mc.options.keyRight);

        int y2 = y0 + 2 * (s + gap);
        mouse(g, mc, x0, y2, 35, "LMB", CpsTracker.left(), mc.options.keyAttack);
        mouse(g, mc, x0 + 37, y2, 35, "RMB", CpsTracker.right(), mc.options.keyUse);

        int y3 = y2 + s + gap;
        g.fill(x0, y3, x0 + 72, y3 + 10, mc.options.keyJump.isDown() ? 0xB0FFFFFF : 0x80000000);
        g.fill(x0 + 26, y3 + 4, x0 + 46, y3 + 5, mc.options.keyJump.isDown() ? 0xFF000000 : 0xFFFFFFFF);
        track(x0, y0, x0 + 72, y3 + 10);
        return 0;
    }

    private void key(GuiGraphics g, Minecraft mc, int x, int y, int w, int h, String label, KeyMapping k) {
        boolean d = k.isDown();
        g.fill(x, y, x + w, y + h, d ? 0xB0FFFFFF : 0x80000000);
        g.drawCenteredString(mc.font, label, x + w / 2, y + (h - 8) / 2, d ? 0xFF000000 : 0xFFFFFFFF);
    }

    private void mouse(GuiGraphics g, Minecraft mc, int x, int y, int w, String label, int cps, KeyMapping k) {
        boolean d = k.isDown();
        int fg = d ? 0xFF000000 : 0xFFFFFFFF;
        g.fill(x, y, x + w, y + 22, d ? 0xB0FFFFFF : 0x80000000);
        g.drawCenteredString(mc.font, label, x + w / 2, y + 3, fg);
        g.drawCenteredString(mc.font, cps + " CPS", x + w / 2, y + 12, fg);
    }
}
