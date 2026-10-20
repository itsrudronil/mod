package com.rudronil.twox.module;

import net.minecraft.client.gui.GuiGraphics;

/** Base class for every toggleable feature in 2X Client. */
public abstract class Module {
    private final String name;
    private boolean enabled;

    protected Module(String name, boolean enabledByDefault) {
        this.name = name;
        this.enabled = enabledByDefault;
    }

    public String getName() { return name; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; if (!enabled) onDisable(); }
    public void toggle() { setEnabled(!enabled); }

    /** Called every client tick while the module is enabled. */
    public void onTick() {}
    public void onDisable() {}

    /** HUD modules draw here. Returns the height used so the next module can stack below it. */
    public int renderHud(GuiGraphics g, int x, int y) { return 0; }
}
