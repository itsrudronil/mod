package com.rudronil.twox.module;

import net.minecraft.client.gui.GuiGraphics;

import java.util.function.Supplier;

/**
 * HUD module that shows text from a supplier. Use \n for multiple lines. Return null to hide.
 * The text is rebuilt only every 200 ms instead of every frame (less string work = more FPS).
 */
public class TextHudModule extends Module {
    private static final long REFRESH_MS = 200;
    private static final String[] EMPTY = new String[0];

    private final Supplier<String> text;
    private String[] lines = EMPTY;
    private long lastUpdate = 0;

    public TextHudModule(String name, Category cat, Anchor anchor, boolean def, Supplier<String> text) {
        super(name, cat, anchor, def);
        this.text = text;
    }

    @Override
    public int renderHud(GuiGraphics g, int x, int y) {
        long now = System.currentTimeMillis();
        if (now - lastUpdate >= REFRESH_MS) {
            lastUpdate = now;
            String s = text.get();
            lines = (s == null || s.isEmpty()) ? EMPTY : s.split("\n");
        }
        if (lines.length == 0) return 0;
        int used = 0;
        for (String l : lines) used += line(g, l, x, y + used, 0xFFFFFFFF);
        return used + 2;
    }
}
