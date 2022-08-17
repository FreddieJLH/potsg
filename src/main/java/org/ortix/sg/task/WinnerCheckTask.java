package org.ortix.sg.task;

import com.google.common.collect.ImmutableList;
import org.bukkit.scheduler.BukkitRunnable;
import org.ortix.sg.SGPlugin;
import org.ortix.sg.game.Game;
import org.ortix.sg.game.GameState;
import org.ortix.sg.handler.ServerHandler;

public class WinnerCheckTask extends BukkitRunnable {

    @Override
    public void run() {
        final ServerHandler serverHandler = SGPlugin.getInstance().getServerHandler();

        if (serverHandler.getGameState() != GameState.IN_GAME)
            return;

        final Game game = serverHandler.getGame();

        if (game.getRemainingPlayers().size() == 1) {
            game.end(ImmutableList.of(
                    game.getRemainingPlayers().get(0)
            ));
        }
    }

}
