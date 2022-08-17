package org.ortix.sg.board.impl;

import net.frozenorb.qlib.scoreboard.ScoreFunction;
import net.frozenorb.qlib.util.LinkedList;
import net.frozenorb.qlib.util.TimeUtils;
import org.bukkit.entity.Player;
import org.ortix.sg.SGPlugin;
import org.ortix.sg.config.SGConfig;
import org.ortix.sg.task.CountdownTask;

import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

public class LobbyBoard implements BiConsumer<LinkedList<String>, Player> {

    @Override
    public void accept(LinkedList<String> scores, Player player) {
        final SGPlugin plugin = SGPlugin.getInstance();
        final SGConfig serverConfig = plugin.getServerConfig();

        final int onlineCount = plugin.getServer().getOnlinePlayers().size();
        final int neededToStart = serverConfig.getPlayersToStart() - onlineCount;
        final CountdownTask countdownTask = plugin.getServerHandler().getCountdownTask();

        scores.add("&a&lPre-game");
        scores.add("&e&lPlayers: &f" + onlineCount);

        if (neededToStart > 0) {
            scores.add("&7 » &6" + neededToStart + " &f" + (neededToStart == 1 ? "player" : "players") + " needed.");
        }

        if (countdownTask.isActive()) {
            scores.add("&5&lStarts In: &f" + TimeUtils.formatIntoMMSS(countdownTask.getTotalTime() - countdownTask.getSecondsElapsed()));
        }
    }

}
