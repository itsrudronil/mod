package com.rudronil.twox.module;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

public class ToggleSprintModule extends Module {
    public ToggleSprintModule() { super("Toggle Sprint", false); }

    @Override
    public void onTick() {
        LocalPlayer p = Minecraft.getInstance().player;
        if (p == null) return;
        boolean movingForward = p.zza > 0;
        if (movingForward && !p.isUsingItem() && !p.horizontalCollision
                && p.getFoodData().getFoodLevel() > 6) {
            p.setSprinting(true);
        }
    }
}
