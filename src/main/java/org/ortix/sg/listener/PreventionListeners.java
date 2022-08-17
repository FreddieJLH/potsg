package org.ortix.sg.listener;

import lombok.RequiredArgsConstructor;
import net.frozenorb.qlib.qLib;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.ortix.sg.SGPlugin;
import org.ortix.sg.game.GameState;
import org.ortix.sg.handler.ServerHandler;

@RequiredArgsConstructor
public class PreventionListeners implements Listener {

    private final SGPlugin plugin;

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        final ServerHandler serverHandler = this.plugin.getServerHandler();

        if (serverHandler.isSpectator(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
            return;
        }

        if (serverHandler.getGameState() != GameState.IN_GAME && !event.getPlayer().hasMetadata("build"))
            event.setCancelled(true);
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        final ServerHandler serverHandler = this.plugin.getServerHandler();

        if (serverHandler.isSpectator(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
            return;
        }

        if (serverHandler.getGameState() != GameState.IN_GAME && !event.getPlayer().hasMetadata("build"))
            event.setCancelled(true);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        final ServerHandler serverHandler = this.plugin.getServerHandler();

        if (serverHandler.getGameState() != GameState.IN_GAME)
            event.setCancelled(true);
    }

    @EventHandler
    public void onDamageByEntity(EntityDamageByEntityEvent event) {
        final ServerHandler serverHandler = this.plugin.getServerHandler();

        if (serverHandler.getGameState() != GameState.IN_GAME) {
            event.setCancelled(true);
            return;
        }

        if (!(event.getDamager() instanceof Player)) {
            event.setCancelled(true);
            return;
        }

        Player damager = (Player) event.getDamager();

        if (serverHandler.isSpectator(damager)) {
            event.setCancelled(true);
            return;
        }

        if (serverHandler.getGame().getPvpTask().isActive()) {
            damager.sendMessage(ChatColor.RED + "You have PVP protection!");
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        final ServerHandler serverHandler = this.plugin.getServerHandler();

        if (serverHandler.isSpectator(event.getEntity().getUniqueId())) {
            event.setCancelled(true);
            return;
        }

        if (serverHandler.getGameState() != GameState.IN_GAME) {
            event.setCancelled(true);
            return;
        }

        if (serverHandler.getGame().getPvpTask().isActive()) {
            event.setCancelled(true);
        }

        if (qLib.RANDOM.nextInt(100) >= 80) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        final ServerHandler serverHandler = this.plugin.getServerHandler();

        if (serverHandler.isSpectator(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
            return;
        }

        if (serverHandler.getGameState() != GameState.IN_GAME && !event.getPlayer().hasMetadata("build"))
            event.setCancelled(true);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        final ServerHandler serverHandler = this.plugin.getServerHandler();

        if (serverHandler.isSpectator(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
            return;
        }

        if (serverHandler.getGameState() != GameState.IN_GAME && !event.getPlayer().hasMetadata("build"))
            event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        final ServerHandler serverHandler = this.plugin.getServerHandler();

        if (serverHandler.isSpectator(event.getWhoClicked().getUniqueId())) {
            event.setCancelled(true);
            return;
        }

        if (serverHandler.getGameState() != GameState.IN_GAME && !event.getWhoClicked().hasMetadata("build"))
            event.setCancelled(true);
    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent event) {
        final ServerHandler serverHandler = this.plugin.getServerHandler();

        if (serverHandler.isSpectator(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
            return;
        }

        if (serverHandler.getGameState() != GameState.IN_GAME && !event.getPlayer().hasMetadata("build"))
            event.setCancelled(true);
    }

    @EventHandler
    public void onPickupInventory(InventoryPickupItemEvent event) {
        if (plugin.getServerHandler().getGameState() != GameState.IN_GAME)
            event.setCancelled(true);
    }

    @EventHandler
    public void onLeaveDecay(LeavesDecayEvent event) {
        event.setCancelled(true);
    }

}
