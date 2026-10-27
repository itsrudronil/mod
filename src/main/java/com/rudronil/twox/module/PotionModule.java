package com.rudronil.twox.module;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.effect.MobEffectInstance;

public class PotionModule extends Module {
    public PotionModule() { super("Potion Status", Category.HUD, Anchor.RIGHT, true); }

    @Override
    public int renderHud(GuiGraphicsExtractor g, int x, int y) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return 0;
        int used = 0;
        for (MobEffectInstance e : mc.player.getActiveEffects()) {
            String name = e.getEffect().value().getDisplayName().getString();
            if (e.getAmplifier() > 0) name += " " + (e.getAmplifier() + 1);
            String time;
            if (e.isInfiniteDuration()) {
                time = "**:**";
            } else {
                int sec = e.getDuration() / 20;
                time = (sec / 60) + ":" + String.format("%02d", sec % 60);
            }
            int color = e.getEffect().value().isBeneficial() ? 0xFF55FF55 : 0xFFFF5555;
            used += line(g, name + " " + time, x, y + used, color);
        }
        return used > 0 ? used + 2 : 0;
    }
}
