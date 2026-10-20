package com.rudronil.twox.module;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public class ArmorHudModule extends Module {
    private static final EquipmentSlot[] SLOTS = {
            EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET, EquipmentSlot.MAINHAND
    };

    public ArmorHudModule() { super("Armor Status", true); }

    @Override
    public int renderHud(GuiGraphics g, int x, int y) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return 0;
        int cx = x;
        for (EquipmentSlot slot : SLOTS) {
            ItemStack stack = mc.player.getItemBySlot(slot);
            if (stack.isEmpty()) continue;
            g.renderItem(stack, cx, y);
            g.renderItemDecorations(mc.font, stack, cx, y);
            cx += 18;
        }
        return 20;
    }
}
