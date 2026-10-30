package com.rudronil.twox.module;

import net.minecraft.client.gui.GuiGraphics;

public class CrosshairModule extends Module {
    private static final int COLOR = 0xFF55FFFF;

    public CrosshairModule() { super("Custom Crosshair", Category.VISUAL, Anchor.NONE, true); }

    /** Draws a small gapped cross with a centre dot. */
    public void draw(GuiGraphics g) {
        int cx = g.guiWidth() / 2;
        int cy = g.guiHeight() / 2;
        int gap = 3, len = 5;
        g.fill(cx - gap - len, cy, cx - gap, cy + 1, COLOR);
        g.fill(cx + gap + 1, cy, cx + gap + 1 + len, cy + 1, COLOR);
        g.fill(cx, cy - gap - len, cx + 1, cy - gap, COLOR);
        g.fill(cx, cy + gap + 1, cx + 1, cy + gap + 1 + len, COLOR);
        g.fill(cx, cy, cx + 1, cy + 1, COLOR);
    }
}
