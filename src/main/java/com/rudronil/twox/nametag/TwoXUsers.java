package com.rudronil.twox.nametag;

import net.minecraft.client.Minecraft;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Who counts as a 2X Client user. Right now that is only you.
 * Other players can only be known through a backend service
 * (the way Lunar/Badlion do it) - fill this set from there.
 */
public final class TwoXUsers {
    private static final Set<UUID> OTHERS = ConcurrentHashMap.newKeySet();

    private TwoXUsers() {}

    public static boolean isUser(UUID id) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && mc.player.getUUID().equals(id)) return true;
        return OTHERS.contains(id);
    }

    public static void add(UUID id) { OTHERS.add(id); }
    public static void remove(UUID id) { OTHERS.remove(id); }
    public static void clear() { OTHERS.clear(); }
}
