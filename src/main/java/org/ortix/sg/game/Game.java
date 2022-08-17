package org.ortix.sg.game;

//import com.cheatbreaker.api.CheatBreakerAPI;
//import com.cheatbreaker.api.handler.TitleHandler;
//import com.cheatbreaker.api.object.TitleType;
import lombok.Getter;
import lombok.Setter;
import net.frozenorb.qlib.border.Border;
import net.frozenorb.qlib.border.BorderTask;
import net.frozenorb.qlib.border.FrozenBorderHandler;
import net.frozenorb.qlib.boss.FrozenBossBarHandler;
import net.frozenorb.qlib.util.BungeeUtils;
import net.frozenorb.qlib.util.Chat;
import net.frozenorb.qlib.util.ItemBuilder;
import net.frozenorb.qlib.util.fanciful.FancyMessage;
import net.minecraft.util.org.apache.commons.lang3.RandomStringUtils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.ortix.sg.SGPlugin;
import org.ortix.sg.task.CountdownTask;
import org.ortix.sg.task.PvPTimerTask;
import org.ortix.sg.task.WinnerCheckTask;
import org.ortix.sg.visbility.VisibilityUtils;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

@Getter
@Setter
public class Game {

    // The list of spectators
    private List<UUID> spectators = new CopyOnWriteArrayList<>();

    // Tracks all of the kills in game
    private Map<UUID, UUID> kills = new ConcurrentHashMap<>();

    // The unique combination of numbers and letters for the site
    private String matchUuid = RandomStringUtils.random(6, 0, 0, true, true, null, SGPlugin.RANDOM);

    // The game's border object
    private Border border = new Border(new Location(Bukkit.getWorld(SGPlugin.WORLD_NAME), 0, 64, 0), Material.BEDROCK, 500, 12);

    // Check if the border has completed it's first movement
    private boolean hasBorderShifted = false;

    // When the game started
    private long startTime = -1;

    // When the game ends
    private long endedAt = -1;

    // The amount of players in the lobby when the game started
    private int playersOnStart = 0;

    private final PvPTimerTask pvpTask = new PvPTimerTask();
    private final WinnerCheckTask winnerCheckTask = new WinnerCheckTask();

    public void start() {
        final Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
        final SGPlugin plugin = SGPlugin.getInstance();

        this.startTime = System.currentTimeMillis();
        this.playersOnStart = onlinePlayers.size();
        this.spectators = new CopyOnWriteArrayList<>();

        plugin.getServerHandler().setGameState(GameState.IN_GAME);

        onlinePlayers.forEach(player -> {
            player.getInventory().clear();
            player.setGameMode(GameMode.SURVIVAL);
            player.teleport(new Location(Bukkit.getWorld(SGPlugin.WORLD_NAME), 0, 64, 0));
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 30, 1));
            player.sendMessage(ChatColor.RED + "PVP enabled in " + ChatColor.GOLD + "2 minutes" + ChatColor.RED + "!");
        });

        final BorderTask borderTask = this.border.getBorderTask();
        this.border.getBorderConfiguration().setBorderMessage(ChatColor.RED + "The border will shrink to {0}x{0} in {1} {2}.");
        this.border.fill();

        borderTask.setAction(BorderTask.BorderAction.SHRINK);
        borderTask.setBorderChange(50);
        borderTask.setBorderChangeDelay(5, TimeUnit.MINUTES);
        borderTask.startTask();

        FrozenBorderHandler.addBorder(this.border);
        this.pvpTask.runTaskTimer(plugin, 20L, 20L);
        this.winnerCheckTask.runTaskTimer(plugin, 20L, 20L);
    }

    public void addSpectator(Player player) {
        player.spigot().respawn();
        player.teleport(new Location(Bukkit.getWorld(SGPlugin.WORLD_NAME), 0, 64, 0));

        player.setGameMode(GameMode.CREATIVE);

        final PlayerInventory inventory = player.getInventory();
        inventory.clear();
        inventory.setArmorContents(null);

        Bukkit.getScheduler().runTaskLater(SGPlugin.getInstance(), () -> {
            inventory.setItem(0, ItemBuilder.of(Material.PAINTING).name(ChatColor.YELLOW + "Spectate Menu").addToLore("&eSee a list of players", "&ethat you're able to", "&eteleport to and spectate.").build());
            inventory.setItem(1, ItemBuilder.of(Material.EMERALD).name(ChatColor.GREEN + "Teleport To Last PvP").build());
        }, 1L);

        player.updateInventory();

        this.spectators.add(player.getUniqueId());
        player.sendMessage(ChatColor.YELLOW + "You are now a spectator: " + ChatColor.RED + "eliminated");

        VisibilityUtils.updateVisibility(player);
    }

    public void end(List<Player> winners) {
        // todo: make it format to multiple winners etc.
        this.endedAt = System.currentTimeMillis();

        final Player winner = winners.get(0);
        new FancyMessage().text(winner.getName() + " wins!").color(ChatColor.GREEN).send(Bukkit.getOnlinePlayers());

        FancyMessage fancyMessage = new FancyMessage("View this match on our website by clicking ").color(ChatColor.GOLD);
        fancyMessage.then().link("https://ortix.org/match/sg/" + matchUuid).text("here").color(ChatColor.BLUE).style(ChatColor.UNDERLINE);
        fancyMessage.then().text(".").color(ChatColor.GOLD);

        fancyMessage.send(Bukkit.getOnlinePlayers());

//        final TitleHandler titleHandler = CheatBreakerAPI.getInstance().getTitleHandler();
//        Bukkit.getOnlinePlayers().forEach(player -> {
//            titleHandler.sendTitle(
//                    player,
//                    TitleType.TITLE,
//                    Chat.translate(player == winner ? "&6&lCongratulations!" : "&c&lBetter luck next time!"),
//                    Duration.ofMillis(20_000)
//            );
//
//            titleHandler.sendTitle(
//                    player,
//                    TitleType.SUBTITLE,
//                    Chat.translate(player == winner ? "&aYou win!" : "&6&lWinner: &2&l" + winner.getName()),
//                    Duration.ofMillis(20_000)
//            );
//
//            titleHandler.sendTitle(player, TitleType.SUBTITLE, Chat.translate("&6&lWinner: &2&l" + winner.getName()), Duration.ofMillis(20_000));
//        });

        final SGPlugin plugin = SGPlugin.getInstance();

        plugin.getServerHandler().setGameState(GameState.WAITING);
        BungeeUtils.sendAll(plugin.getServerConfig().getLobbyServer());

        this.border.getBorderTask().stopTask();
        this.pvpTask.cancel();
        this.winnerCheckTask.cancel();

        FrozenBorderHandler.removeBorder(this.border);

        plugin.getServerHandler().setGameState(GameState.ENDING);
        plugin.getSiteHandler().pushGame();

        Bukkit.broadcastMessage(ChatColor.GREEN + "Server resetting in 20 seconds..");
        Bukkit.getScheduler().runTaskLater(plugin, Bukkit::shutdown, 400L);
    }

    public List<Player> getRemainingPlayers() {
        List<Player> toReturn = new ArrayList<>(Bukkit.getOnlinePlayers());

        toReturn.removeIf(player -> this.spectators.contains(player.getUniqueId()));

        return toReturn;
    }

}
