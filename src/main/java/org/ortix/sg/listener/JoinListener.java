package org.ortix.sg.listener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.ortix.sg.SGPlugin;
import org.ortix.sg.game.GameState;
import org.ortix.sg.handler.ServerHandler;
import org.ortix.sg.task.CountdownTask;

public class JoinListener implements Listener {

    private final SGPlugin plugin = SGPlugin.getInstance();

    @EventHandler
    public void onLogin(AsyncPlayerPreLoginEvent event) {
        final GameState gameState = SGPlugin.getInstance().getServerHandler().getGameState();

        if (Bukkit.getOnlinePlayers().size() >= 50) {
            event.disallow(PlayerPreLoginEvent.Result.KICK_OTHER, ChatColor.RED + "The game is currently full");
            return;
        }

        if (gameState != GameState.WAITING) {
            event.disallow(PlayerPreLoginEvent.Result.KICK_OTHER, "You cannot join when the game state is " + gameState.getColor() + gameState.name());
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();

        player.getInventory().clear();
        player.getInventory().setArmorContents(null);

        event.setJoinMessage(null);

        player.updateInventory();

        player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));
        player.setFoodLevel(20);
        player.setHealth(20);

        player.setGameMode(GameMode.CREATIVE);

        player.teleport(new Location(Bukkit.getWorld(SGPlugin.WORLD_NAME), 0, 64, 0));

        final CountdownTask countdownTask = this.plugin.getServerHandler().getCountdownTask();
        if (Bukkit.getOnlinePlayers().size() >= this.plugin.getServerConfig().getPlayersToStart() && !countdownTask.isActive()) {
            countdownTask.setActive(true);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);

        final ServerHandler serverHandler = this.plugin.getServerHandler();
        final Player player = event.getPlayer();

        if (serverHandler.isSpectator(player)) {
            serverHandler.getGame().getSpectators().remove(player.getUniqueId());
        }

        final CountdownTask countdownTask = serverHandler.getCountdownTask();
        if (Bukkit.getOnlinePlayers().size() < this.plugin.getServerConfig().getPlayersToStart() && countdownTask.isActive()) {
            countdownTask.setActive(false);
            Bukkit.broadcastMessage(ChatColor.RED + "There was not enough players to start the game.");
        }
    }

}
