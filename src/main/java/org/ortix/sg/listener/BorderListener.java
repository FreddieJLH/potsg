package org.ortix.sg.listener;

import lombok.RequiredArgsConstructor;
import net.frozenorb.qlib.border.Border;
import net.frozenorb.qlib.border.event.BorderChangeEvent;
import net.frozenorb.qlib.border.event.PlayerExitBorderEvent;
import net.frozenorb.qlib.cuboid.Cuboid;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.ortix.sg.SGPlugin;
import org.ortix.sg.game.Game;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class BorderListener implements Listener {

    private final SGPlugin plugin;

    @EventHandler
    public void onBorderChange(BorderChangeEvent event) {
        final Game game = this.plugin.getServerHandler().getGame();

        if (game == null || game.getBorder() == null)
            return;

        final Border border = game.getBorder();
        final int size = border.getSize();

        if (border.getSize() == 100)
            border.getBorderTask().stopTask();

        Bukkit.broadcastMessage(ChatColor.RED + "The border has shrunk to " + size + "x" + size);

        if (size != 100) {
            Bukkit.broadcastMessage(ChatColor.RED + "The border will shrink to " + (size - 50) + "x" + (size - 50) + " in 1 minute.");
        }

        if (game.isHasBorderShifted())
            return;

        game.setHasBorderShifted(true);
        border.getBorderTask().setBorderChangeDelay(1, TimeUnit.MINUTES);
    }

    @EventHandler
    public void onBorderExit(PlayerExitBorderEvent event) {
        final Location playerLocation = event.getPlayer().getLocation();
        final Border border = event.getBorder();
        final Cuboid cuboid = border.getPhysicalBounds();

        int minX = cuboid.getLowerX();
        int minZ = cuboid.getLowerZ();
        int maxX = cuboid.getUpperX();
        int maxZ = cuboid.getUpperZ();
        int newX = playerLocation.getBlockX();
        int newZ = playerLocation.getBlockZ();

        if (maxX < playerLocation.getBlockX()) {
            newX = maxX;
        } else if (minX > playerLocation.getBlockX()) {
            newX = minX;
        }

        if (maxZ < playerLocation.getBlockZ()) {
            newZ = maxZ;
        } else if (minZ > playerLocation.getBlockZ()) {
            newZ = minZ;
        }

        event.getPlayer().teleport(new Location(playerLocation.getWorld(), newX, playerLocation.getY(), newZ));
    }

}
