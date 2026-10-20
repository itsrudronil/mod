package com.rudronil.twox.module;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

public abstract class Module {
    public enum Category {
        HUD("HUD"), COMBAT("Combat"), VISUAL("Visual"), UTILITY("Utility");
        public final String label;
        Category(String label) { this.label = label; }
    }

    /** LEFT/RIGHT stack automatically, FIXED draws itself anywhere, NONE has no HUD. */
    public enum Anchor { LEFT, RIGHT, FIXED, NONE }

    private final String name;
    private final Category category;
    private final Anchor anchor;
    private boolean enabled;

    protected Module(String name, Category category, Anchor anchor, boolean enabledByDefault) {
        this.name = name;
        this.category = category;
        this.anchor = anchor;
        this.enabled = enabledByDefault;
    }

    public String getName() { return name; }
    public Category getCategory() { return category; }
    public Anchor getAnchor() { return anchor; }
    public boolean isEnabled() { return enabled; }
    public void toggle() { setEnabled(!enabled); }

    public void setEnabled(boolean value) {
        if (value == enabled) return;
        enabled = value;
        if (value) onEnable(); else onDisable();
    }

    public void onEnable() {}
    public void onDisable() {}
    /** Called every client tick while enabled. */
    public void onTick() {}

    /** Draw HUD content. Returns the height used (LEFT/RIGHT stacking). */
    public int renderHud(GuiGraphics g, int x, int y) { return 0; }

    /** Draws one line, right-aligned automatically for RIGHT anchored modules. */
    protected int line(GuiGraphics g, String text, int x, int y, int color) {
        Font f = Minecraft.getInstance().font;
        int dx = anchor == Anchor.RIGHT ? x - f.width(text) : x;
        g.drawString(f, text, dx, y, color, true);
        return 11;
    }
}
