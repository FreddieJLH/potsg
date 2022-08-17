package org.ortix.sg.listener;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.ortix.sg.SGPlugin;
import org.ortix.sg.menu.SpectatorMenu;

public class SpectatorListener implements Listener {

    private final SGPlugin plugin = SGPlugin.getInstance();
    private Location lastDamageLocation;

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            final Player damager = (Player) event.getDamager();

            if (!this.plugin.getServerHandler().isSpectator(damager)) {
                lastDamageLocation = event.getEntity().getLocation();
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerInteract(PlayerInteractEvent event) {
        final Player player = event.getPlayer();

        final ItemStack item = event.getItem();
        if (item == null || item.getType() != Material.EMERALD && item.getType() != Material.PAINTING)
            return;

        if (player.getGameMode() != GameMode.CREATIVE)
            return;

        if (!this.plugin.getServerHandler().isSpectator(player))
            return;

        if (item.getType() == Material.EMERALD) {
            if (lastDamageLocation == null) {
                player.sendMessage(ChatColor.RED + "There are no previous damage locations.");
                return;
            }

            player.teleport(lastDamageLocation);
            return;
        }

        if (item.getType() == Material.PAINTING) {
            new SpectatorMenu().openMenu(player);
        }
    }

}