package com.rudronil.twox.module;

import net.minecraft.client.Minecraft;

import java.util.ArrayDeque;

/** Counts clicks by watching attack/use key presses every rendered frame. */
public final class CpsTracker {
    private static final ArrayDeque<Long> LEFT = new ArrayDeque<>();
    private static final ArrayDeque<Long> RIGHT = new ArrayDeque<>();
    private static boolean lastL, lastR;

    private CpsTracker() {}

    public static void update() {
        Minecraft mc = Minecraft.getInstance();
        long now = System.currentTimeMillis();
        boolean l = mc.options.keyAttack.isDown();
        boolean r = mc.options.keyUse.isDown();
        if (l && !lastL) LEFT.addLast(now);
        if (r && !lastR) RIGHT.addLast(now);
        lastL = l;
        lastR = r;
        while (!LEFT.isEmpty() && now - LEFT.peekFirst() > 1000) LEFT.pollFirst();
        while (!RIGHT.isEmpty() && now - RIGHT.peekFirst() > 1000) RIGHT.pollFirst();
    }

    public static int left() { return LEFT.size(); }
    public static int right() { return RIGHT.size(); }
}
