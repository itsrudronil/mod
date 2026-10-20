package com.rudronil.twox.module;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public class ArmorHudModule extends Module {
    private static final EquipmentSlot[] SLOTS = {
            EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET,
            EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND
    };

    public ArmorHudModule() { super("Armor Status", Category.HUD, Anchor.LEFT, true); }

    @Override
    public int renderHud(GuiGraphics g, int x, int y) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return 0;
        int cx = x;
        boolean any = false;
        for (EquipmentSlot slot : SLOTS) {
            ItemStack stack = mc.player.getItemBySlot(slot);
            if (stack.isEmpty()) continue;
            g.renderItem(stack, cx, y);
            g.renderItemDecorations(mc.font, stack, cx, y);
            cx += 18;
            any = true;
        }
        if (any) track(x - 1, y - 1, cx, y + 17);
        return any ? 20 : 0;
    }
}
