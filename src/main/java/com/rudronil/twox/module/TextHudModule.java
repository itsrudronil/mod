package com.rudronil.twox.module;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

import java.util.function.Supplier;

/** A HUD module that just shows one line of text. */
public class TextHudModule extends Module {
    private final Supplier<String> text;

    public TextHudModule(String name, Supplier<String> text) {
        super(name, true);
        this.text = text;
    }

    @Override
    public int renderHud(GuiGraphics g, int x, int y) {
        Minecraft mc = Minecraft.getInstance();
        String s = text.get();
        if (s == null) return 0;
        g.drawString(mc.font, s, x, y, 0xFFFFFFFF, true);
        return 12;
    }
}
