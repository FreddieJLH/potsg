package org.ortix.sg.board.impl;

import net.frozenorb.qlib.border.Border;
import net.frozenorb.qlib.scoreboard.ScoreFunction;
import net.frozenorb.qlib.util.LinkedList;
import net.frozenorb.qlib.util.TimeUtils;
import org.bukkit.entity.Player;
import org.ortix.sg.SGPlugin;
import org.ortix.sg.game.Game;
import org.ortix.sg.pearl.EnderpearlCooldownHandler;
import org.ortix.sg.task.PvPTimerTask;

import java.util.Map;
import java.util.function.BiConsumer;

public class GameBoard implements BiConsumer<LinkedList<String>, Player> {

    @Override
    public void accept(LinkedList<String> scores, Player player) {
        final SGPlugin plugin = SGPlugin.getInstance();
        final Game game = plugin.getServerHandler().getGame();
        final Border border = game.getBorder();
        final int secondsRemaining = border.getBorderTask().getSecondsRemaining() + 1;

        scores.add("&d&lGame Time: &f" + (game.getStartTime() == -1 ? "N/A" : TimeUtils.formatLongIntoMMSS((System.currentTimeMillis() - game.getStartTime()) / 1000)));
        scores.add("&e&lRemaining: &f" + game.getRemainingPlayers().size() + "/" + game.getPlayersOnStart());

        if (border.getSize() >= 100) {
            scores.add("&5&lBorder: &f" + border.getSize() + (secondsRemaining <= 60 ? " &7(&c" + secondsRemaining + "&7)" : ""));
        }

        /* Timers */
        String enderpearlScore = getEnderpearlScore(player);

        final PvPTimerTask pvpTask = game.getPvpTask();
        if (pvpTask.isActive()) {
            scores.add("&c&lPvP Timer: &f" + TimeUtils.formatIntoMMSS((int) (pvpTask.getTotalTime() - pvpTask.getSecondsElapsed())));
        }

        if (enderpearlScore != null) {
            scores.add("&3&lEnderpearl: &f" + enderpearlScore);
        }
        /* Timers End */
    }

    public String getEnderpearlScore(Player player) {
        final Map<String, Long> enderpearlCooldown = EnderpearlCooldownHandler.getEnderpearlCooldown();

        if (enderpearlCooldown.containsKey(player.getName()) && enderpearlCooldown.get(player.getName()) >= System.currentTimeMillis()) {
            float diff = enderpearlCooldown.get(player.getName()) - System.currentTimeMillis();

            if (diff >= 0) {
                return (ScoreFunction.TIME_FANCY.apply(diff / 1000F));
            }
        }

        return (null);
    }
}
