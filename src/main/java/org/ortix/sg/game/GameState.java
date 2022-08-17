package org.ortix.sg.game;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;

@RequiredArgsConstructor
@Getter
public enum GameState {

    LOADING(ChatColor.DARK_RED),
    WAITING(ChatColor.GOLD),
    IN_GAME(ChatColor.GREEN),
    ENDING(ChatColor.RED);

    private final ChatColor color;

}
