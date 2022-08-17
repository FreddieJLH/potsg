package org.ortix.sg.menu;

import com.google.common.collect.ImmutableList;
import lombok.RequiredArgsConstructor;
import net.frozenorb.qlib.menu.Button;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.List;

@RequiredArgsConstructor
public class SpectatePlayerButton extends Button {

    private final Player target;

    @Override
    public String getName(Player player) {
        return ChatColor.RESET + target.getDisplayName();
    }

    @Override
    public List<String> getDescription(Player player) {
        return ImmutableList.of(
                ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString() + "--------------------------------",
                ChatColor.YELLOW + "Click to teleport to " + ChatColor.RESET + target.getDisplayName() + ChatColor.YELLOW + ".",
                ChatColor.GOLD + "Health" + ChatColor.GRAY + ": " + ChatColor.WHITE + (target.getHealth() / 20d) * 100d + "%",
                ChatColor.GOLD + "Food" + ChatColor.GRAY + ": " + ChatColor.WHITE + ((target.getFoodLevel() / 20d) * 100d) + "%",
                ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString() + "--------------------------------"
        );
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.SKULL_ITEM;
    }

    @Override
    public byte getDamageValue(Player player) {
        return (byte) 3;
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        if (target.isOnline()) {
            player.teleport(target);
        } else {
            player.sendMessage(ChatColor.RED + "That player is no longer online.");
        }
    }
}
