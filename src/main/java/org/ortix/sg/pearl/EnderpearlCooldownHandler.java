package org.ortix.sg.pearl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerPearlRefundEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.ortix.sg.SGPlugin;
import org.ortix.sg.game.GameState;
import org.ortix.sg.pearl.event.EnderpearlCooldownAppliedEvent;

@RequiredArgsConstructor
public class EnderpearlCooldownHandler implements Listener {

    private final SGPlugin plugin;

    @Getter
    private static final Map<String, Long> enderpearlCooldown = new ConcurrentHashMap<>();

    public void init() {
        Bukkit.getPluginManager().registerEvents(this, SGPlugin.getInstance());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (!(event.getEntity().getShooter() instanceof Player)) {
            return;
        }

        Player shooter = (Player) event.getEntity().getShooter();

        if (event.getEntity() instanceof EnderPearl) {
            // Store the player's enderpearl in-case we need to remove it prematurely
            shooter.setMetadata("LastEnderPearl", new FixedMetadataValue(this.plugin, event.getEntity()));

            // Call our custom event (time to apply needs to be modifiable)
            EnderpearlCooldownAppliedEvent appliedEvent = new EnderpearlCooldownAppliedEvent(shooter, 16_000L);
            this.plugin.getServer().getPluginManager().callEvent(appliedEvent);

            // Put the player into the cooldown map
            enderpearlCooldown.put(shooter.getName(), System.currentTimeMillis() + appliedEvent.getTimeToApply());
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerInteract(ProjectileLaunchEvent event) {
        if (!(event.getEntity() instanceof EnderPearl)) {
            return;
        }

        if (!(event.getEntity().getShooter() instanceof Player)) {
            return;
        }

        Player thrower = (Player) event.getEntity().getShooter();

        if (this.plugin.getServerHandler().getGameState() != GameState.IN_GAME) {
            event.setCancelled(true);
            thrower.updateInventory();
            return;
        }

        if (enderpearlCooldown.containsKey(thrower.getName()) && enderpearlCooldown.get(thrower.getName()) > System.currentTimeMillis()) {
            long millisLeft = enderpearlCooldown.get(thrower.getName()) - System.currentTimeMillis();

            double value = (millisLeft / 1000D);
            double sec = value > 0.1 ? Math.round(10.0 * value) / 10.0 : 0.1; // don't tell user 0.0

            event.setCancelled(true);
            thrower.sendMessage(ChatColor.RED + "You cannot use this for another " + ChatColor.BOLD + sec + ChatColor.RED + " seconds!");
            thrower.updateInventory();
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (event.getCause() != PlayerTeleportEvent.TeleportCause.ENDER_PEARL)
            return;

        if (!enderpearlCooldown.containsKey(event.getPlayer().getName())) {
            event.setCancelled(true); // only reason for this would be player died before pearl landed, so cancel it!
        }
    }

    @EventHandler
    public void onRefund(PlayerPearlRefundEvent event) {
        Player player = event.getPlayer();

        if (!player.isOnline()) {
            return;
        }

        ItemStack inPlayerHand = player.getItemInHand();
        if (inPlayerHand != null && inPlayerHand.getType() == Material.ENDER_PEARL && inPlayerHand.getAmount() < 16) {
            inPlayerHand.setAmount(inPlayerHand.getAmount() + 1);
            player.updateInventory();
        }

        enderpearlCooldown.remove(player.getName());
    }

    public boolean clippingThrough(Location target, Location from, double thickness) {
        return ((from.getX() > target.getX() && (from.getX() - target.getX() < thickness)) || (target.getX() > from.getX() && (target.getX() - from.getX() < thickness)) ||
                (from.getZ() > target.getZ() && (from.getZ() - target.getZ() < thickness)) || (target.getZ() > from.getZ() && (target.getZ() - from.getZ() < thickness)));
    }

    public static void resetEnderpearlTimer(Player player) {
        enderpearlCooldown.put(player.getName(), System.currentTimeMillis() + 16_000L);
    }

}