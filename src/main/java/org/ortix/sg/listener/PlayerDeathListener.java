package org.ortix.sg.listener;

import com.google.common.collect.ImmutableList;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.ortix.sg.SGPlugin;
import org.ortix.sg.game.Game;
import org.ortix.sg.game.GameState;
import org.ortix.sg.handler.ServerHandler;

@RequiredArgsConstructor
public class PlayerDeathListener implements Listener {

    private final SGPlugin plugin;

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        final Player player = event.getEntity();
        final ServerHandler serverHandler = this.plugin.getServerHandler();

        if (serverHandler.getGameState() != GameState.IN_GAME)
            return;

        final Game game = serverHandler.getGame();

        game.addSpectator(player);
        game.getKills().put(player.getUniqueId(), player.getKiller() == null ? null : player.getKiller().getUniqueId());

        final int remainingPlayerCount = game.getRemainingPlayers().size();

        if (remainingPlayerCount == 1 || remainingPlayerCount == 0) {
            game.end(ImmutableList.of(remainingPlayerCount == 0 ? player : game.getRemainingPlayers().get(0)));
        }
    }

}
