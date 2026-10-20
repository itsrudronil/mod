package com.rudronil.twox.module;

import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/** Tracks last-hit reach and an approximate "jump reset" success rate. */
public final class CombatTracker {
    private static double lastReach = -1;
    private static long lastReachTime = 0;

    private static int hits = 0, resets = 0;
    private static int lastHurtTime = 0;
    private static boolean armed = false;
    private static int window = 0;

    private CombatTracker() {}

    public static void init() {
        AttackEntityCallback.EVENT.register((player, level, hand, entity, hitResult) -> {
            if (level.isClientSide()) {
                Vec3 eye = player.getEyePosition();
                AABB b = entity.getBoundingBox();
                Vec3 near = new Vec3(
                        Math.max(b.minX, Math.min(eye.x, b.maxX)),
                        Math.max(b.minY, Math.min(eye.y, b.maxY)),
                        Math.max(b.minZ, Math.min(eye.z, b.maxZ)));
                lastReach = eye.distanceTo(near);
                lastReachTime = System.currentTimeMillis();
            }
            return InteractionResult.PASS;
        });
    }

    public static void tick() {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer p = mc.player;
        if (p == null) return;

        // New hit taken: hurtTime jumps from 0 to >0
        if (p.hurtTime > 0 && lastHurtTime == 0) {
            hits++;
            armed = true;
            window = 0;
        }
        lastHurtTime = p.hurtTime;

        if (armed) {
            window++;
            if (mc.options.keyJump.isDown() && p.onGround()) {
                resets++;
                armed = false;
            } else if (window > 6) {
                armed = false;
            }
        }
    }

    public static String reachText() {
        if (lastReach < 0) return "Reach: -";
        boolean fresh = System.currentTimeMillis() - lastReachTime < 5000;
        return String.format("Reach: %.2f%s", lastReach, fresh ? "" : " (old)");
    }

    public static String jumpText() {
        int pct = hits == 0 ? 0 : Math.round(100f * resets / hits);
        return "Jump Reset: " + resets + "/" + hits + " (" + pct + "%)";
    }
}
