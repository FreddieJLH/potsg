package org.ortix.sg.command;

import com.google.common.collect.ImmutableList;
import net.frozenorb.qlib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.ortix.sg.SGPlugin;
import org.ortix.sg.game.GameState;
import org.ortix.sg.handler.ServerHandler;

public class ForceEndCommand {

    @Command(names = "sg forceend", permission = "op")
    public static void execute(CommandSender sender) {
        final SGPlugin plugin = SGPlugin.getInstance();
        final ServerHandler serverHandler = plugin.getServerHandler();

        if (serverHandler.getGameState() != GameState.IN_GAME) {
            sender.sendMessage(ChatColor.RED + "The game has not started.");
            return;
        }

        serverHandler.getGame().end(ImmutableList.of((Player) sender));
    }

}
