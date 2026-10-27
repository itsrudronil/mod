package com.rudronil.twox.module;

import net.minecraft.client.gui.GuiGraphicsExtractor;

import java.util.function.Supplier;

/** HUD module that shows text from a supplier. Use \n for multiple lines. Return null to hide. */
public class TextHudModule extends Module {
    private final Supplier<String> text;

    public TextHudModule(String name, Category cat, Anchor anchor, boolean def, Supplier<String> text) {
        super(name, cat, anchor, def);
        this.text = text;
    }

    @Override
    public int renderHud(GuiGraphicsExtractor g, int x, int y) {
        String s = text.get();
        if (s == null || s.isEmpty()) return 0;
        int used = 0;
        for (String l : s.split("\n")) used += line(g, l, x, y + used, 0xFFFFFFFF);
        return used + 2;
    }
}
