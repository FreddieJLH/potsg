package org.ortix.sg.visbility;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.ortix.sg.SGPlugin;
import org.ortix.sg.game.Game;
import org.ortix.sg.handler.ServerHandler;

import java.util.List;
import java.util.UUID;

@UtilityClass
public final class VisibilityUtils {

    public static void updateVisibilityFlicker(Player target) {
        for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
            target.hidePlayer(otherPlayer);
            otherPlayer.hidePlayer(target);
        }

        Bukkit.getScheduler().runTaskLater(SGPlugin.getInstance(), () -> updateVisibility(target), 10L);
    }

    public static void updateVisibility(Player target) {
        for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
            if (shouldSeePlayer(otherPlayer, target)) {
                otherPlayer.showPlayer(target);
            } else {
                otherPlayer.hidePlayer(target);
            }

            if (shouldSeePlayer(target, otherPlayer)) {
                target.showPlayer(otherPlayer);
            } else {
                target.hidePlayer(otherPlayer);
            }
        }
    }

    private static boolean shouldSeePlayer(Player viewer, Player target) {
        final ServerHandler serverHandler = SGPlugin.getInstance().getServerHandler();
        final Game game = serverHandler.getGame();
        final List<UUID> spectators = game.getSpectators();

        if (spectators.contains(target.getUniqueId()) && !spectators.contains(viewer.getUniqueId())) {
            return false;
        }

        if (spectators.contains(target.getUniqueId()) && spectators.contains(viewer.getUniqueId())) {
            return true;
        }

        return true;
    }

}