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

    /** User-dragged offset from the default position (set by the HUD editor). */
    public int offX, offY;

    private int bx0, by0, bx1, by1;
    private boolean hasBounds;

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

    public void resetBounds() { hasBounds = false; }
    public boolean hasBounds() { return hasBounds; }
    public int boundsX0() { return bx0; }
    public int boundsY0() { return by0; }
    public int boundsX1() { return bx1; }
    public int boundsY1() { return by1; }

    /** Grows the module's on-screen rectangle (used by the HUD editor for hit-testing). */
    protected void track(int x0, int y0, int x1, int y1) {
        if (!hasBounds) {
            bx0 = x0; by0 = y0; bx1 = x1; by1 = y1;
            hasBounds = true;
        } else {
            bx0 = Math.min(bx0, x0); by0 = Math.min(by0, y0);
            bx1 = Math.max(bx1, x1); by1 = Math.max(by1, y1);
        }
    }

    /** Grey label shown in the HUD editor when the module has nothing to display right now. */
    public void placeholder(GuiGraphics g, int x, int y) {
        line(g, "[" + name + "]", x, y, 0xFF777777);
    }

    /** Draws one line, right-aligned automatically for RIGHT anchored modules. */
    protected int line(GuiGraphics g, String text, int x, int y, int color) {
        Font f = Minecraft.getInstance().font;
        int dx = anchor == Anchor.RIGHT ? x - f.width(text) : x;
        g.drawString(f, text, dx, y, color, true);
        track(dx - 1, y - 1, dx + f.width(text) + 1, y + 9);
        return 11;
    }
}
