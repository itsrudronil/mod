package com.rudronil.twox.nametag;

import com.rudronil.twox.TwoXClient;
import com.rudronil.twox.module.ModuleManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public final class NametagIcon {
    // U+E000 is mapped to nametag.png by assets/twox_client/font/icon.json
    private static final String ICON_CHAR = "\uE000";
    private static final Style ICON_STYLE = Style.EMPTY
            .withFont(new FontDescription.Resource(Identifier.fromNamespaceAndPath(TwoXClient.MOD_ID, "icon")))
            .withColor(0xFFFFFF);

    private static int logged = 0;

    private NametagIcon() {}

    /** Returns the same component if no icon applies, otherwise "[icon] name". */
    public static Component apply(Entity entity, Component name) {
        if (!(entity instanceof Player player)) return name;

        boolean moduleOn = ModuleManager.nametagIcon != null && ModuleManager.nametagIcon.isEnabled();
        boolean test = ModuleManager.iconTestMode != null && ModuleManager.iconTestMode.isEnabled();
        boolean user = moduleOn && TwoXUsers.isUser(player.getUUID());

        // A few lines in logs/latest.log to help find out why an icon is (not) shown
        if (logged < 6) {
            logged++;
            TwoXClient.LOGGER.info("[2X] nametag hook reached: player={} moduleOn={} testMode={} isTwoXUser={}",
                    player.getName().getString(), moduleOn, test, user);
        }

        // Cheap checks first: most players are not 2X users, so they cost almost nothing per frame.
        if (!moduleOn || (!test && !user)) return name;
        if (name.getString().startsWith(ICON_CHAR)) return name; // already added by the other hook

        MutableComponent icon = Component.literal(ICON_CHAR).setStyle(ICON_STYLE);
        return Component.empty().append(icon).append(Component.literal(" ")).append(name);
    }
}
