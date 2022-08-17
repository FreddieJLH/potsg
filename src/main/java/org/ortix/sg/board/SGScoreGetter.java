package org.ortix.sg.board;

import lombok.RequiredArgsConstructor;
import net.frozenorb.qlib.scoreboard.ScoreGetter;
import net.frozenorb.qlib.util.LinkedList;
import net.frozenorb.qlib.util.TimeUtils;
import org.bukkit.entity.Player;
import org.ortix.sg.SGPlugin;
import org.ortix.sg.board.impl.*;
import org.ortix.sg.game.Game;
import org.ortix.sg.game.GameState;
import org.ortix.sg.handler.ServerHandler;

@RequiredArgsConstructor
public class SGScoreGetter implements ScoreGetter {

    private final SGPlugin plugin;
    private final GameBoard gameBoard = new GameBoard();
    private final LobbyBoard lobbyBoard = new LobbyBoard();

    @Override
    public void getScores(LinkedList<String> scores, Player player) {
        scores.add("&a&7&m-------------------");

        final ServerHandler serverHandler = this.plugin.getServerHandler();
        final GameState gameState = serverHandler.getGameState();
        final Game game = serverHandler.getGame();

        switch (gameState) {
            case LOADING:
                scores.add("");
                scores.add("&c&l&oServer is loading..");
                break;
            case WAITING:
                this.lobbyBoard.accept(scores, player);
                break;
            case ENDING:
                scores.add("&d&lGame Time: &f" + (TimeUtils.formatLongIntoMMSS((game.getEndedAt() - game.getStartTime()) / 1000)));
                break;
            case IN_GAME:
                this.gameBoard.accept(scores, player);
                break;
        }

        scores.add("&b&7&m-------------------");
    }

}
