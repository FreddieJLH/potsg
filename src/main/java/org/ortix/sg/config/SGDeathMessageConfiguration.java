package org.ortix.sg.config;

import net.frozenorb.qlib.deathmessage.DeathMessageConfiguration;
import net.frozenorb.qlib.util.Chat;
import net.frozenorb.qlib.util.UUIDUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class SGDeathMessageConfiguration implements DeathMessageConfiguration {

    @Override
    public boolean shouldShowDeathMessage(UUID uuid, UUID uuid1, UUID uuid2) {
        return true;
    }

    @Override
    public String formatPlayerName(UUID uuid) {
        return ChatColor.WHITE + UUIDUtils.name(uuid);
    }

    @Override
    public String formatPlayerName(UUID playerUuid, UUID formatForUuid) {
        final Player player = Bukkit.getPlayer(playerUuid);
        final Player formatFor = Bukkit.getPlayer(formatForUuid);

        if (formatFor == null || player == null)
            return ChatColor.WHITE + UUIDUtils.name(playerUuid);

        if (playerUuid == formatForUuid) {
            return Chat.translate("&r" + player.getDisplayName() + "&7[&4" + (player.getHealth() / 2) + "❤&7]");
        }

        return ChatColor.RESET + player.getDisplayName();
    }

    @Override
    public boolean hideWeapons() {
        return false;
    }

}
