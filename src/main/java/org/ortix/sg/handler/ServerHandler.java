package org.ortix.sg.handler;

import lombok.Data;
import org.bukkit.entity.Player;
import org.ortix.sg.SGPlugin;
import org.ortix.sg.game.Game;
import org.ortix.sg.game.GameState;
import org.ortix.sg.task.CountdownTask;

import java.util.UUID;

@Data
public class ServerHandler {

    private final Game game = new Game();
    private GameState gameState = GameState.LOADING;

    private final CountdownTask countdownTask = new CountdownTask();

    public ServerHandler() {
        this.countdownTask.setActive(false);
        this.countdownTask.runTaskTimer(SGPlugin.getInstance(), 0L, 20L);
    }

    public boolean isSpectator(Player player) {
        return this.isSpectator(player.getUniqueId());
    }

    public boolean isSpectator(UUID uuid) {
        return this.game.getSpectators().contains(uuid);
    }

}
