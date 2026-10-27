package com.rudronil.twox.module;

import com.rudronil.twox.mixin.OptionInstanceAccessor;
import net.minecraft.client.Minecraft;

public class FullbrightModule extends Module {
    private double saved = 1.0;
    private boolean active = false;

    public FullbrightModule() { super("Fullbright", Category.VISUAL, Anchor.NONE, false); }

    @Override
    public void onEnable() {
        saved = Math.min(1.0, Minecraft.getInstance().options.gamma().get());
        active = true;
        set(15.0);
    }

    @Override
    public void onTick() { if (active) set(15.0); }

    @Override
    public void onDisable() {
        if (active) {
            set(saved);
            active = false;
        }
    }

    @SuppressWarnings("unchecked")
    private static void set(double v) {
        ((OptionInstanceAccessor<Double>) (Object) Minecraft.getInstance().options.gamma()).twox$setValue(v);
    }
}
