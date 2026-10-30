package com.rudronil.twox.module;

import net.minecraft.client.CloudStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.ParticleStatus;

/**
 * Applies a few cheap-to-render settings while enabled and puts your own values back
 * when disabled (or when the game closes).
 */
public class FpsBoostModule extends Module {
    private boolean active = false;
    private boolean savedShadows;
    private ParticleStatus savedParticles;
    private CloudStatus savedClouds;
    private int savedBlend;

    public FpsBoostModule() { super("FPS Boost", Category.UTILITY, Anchor.NONE, false); }

    @Override
    public void onEnable() {
        Options o = Minecraft.getInstance().options;
        savedShadows = o.entityShadows().get();
        savedParticles = o.particles().get();
        savedClouds = o.cloudStatus().get();
        savedBlend = o.biomeBlendRadius().get();
        active = true;

        o.entityShadows().set(false);
        o.particles().set(ParticleStatus.MINIMAL);
        o.cloudStatus().set(CloudStatus.OFF);
        o.biomeBlendRadius().set(1);
    }

    @Override
    public void onDisable() {
        if (!active) return;
        active = false;
        Options o = Minecraft.getInstance().options;
        o.entityShadows().set(savedShadows);
        o.particles().set(savedParticles);
        o.cloudStatus().set(savedClouds);
        o.biomeBlendRadius().set(savedBlend);
    }
}
