package org.ortix.sg.command;

import net.frozenorb.qlib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.ortix.sg.SGPlugin;
import org.ortix.sg.game.GameState;
import org.ortix.sg.handler.ServerHandler;

public class ForceStartCommand {

    @Command(names = "sg forcestart", permission = "op")
    public static void execute(CommandSender sender) {
        final SGPlugin plugin = SGPlugin.getInstance();
        final ServerHandler serverHandler = plugin.getServerHandler();

        if (serverHandler.getGameState() == GameState.IN_GAME) {
            sender.sendMessage(ChatColor.RED + "The game has already started.");
            return;
        }

        serverHandler.getCountdownTask().setActive(true);
    }

    @Command(names = "sg forcestart updatetimer", permission = "op")
    public static void executeUpdateTimer(CommandSender sender) {
        final SGPlugin plugin = SGPlugin.getInstance();
        final ServerHandler serverHandler = plugin.getServerHandler();

        if (serverHandler.getGameState() == GameState.IN_GAME) {
            sender.sendMessage(ChatColor.RED + "The game has already started.");
            return;
        }

        serverHandler.getCountdownTask().setSecondsElapsed(55);
    }

}
