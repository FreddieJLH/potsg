package org.ortix.sg.config;

import lombok.Getter;
import net.frozenorb.qlib.config.RequiredField;
import net.frozenorb.qlib.config.comment.Comment;

@Comment("Configuration file for PotSG [ortix.org]")
@Getter
public class SGConfig {

    @RequiredField
    private final String scoreboardTitle = "&6&lOrtix &7%line% &fSG";

    @RequiredField
    @Comment("Needed players to start the game")
    private final int playersToStart = 16;

    @RequiredField
    @Comment("The name of the server on the tab")
    private final String serverName = "SG-1";

    @RequiredField
    @Comment("The fallback server once the game has ended")
    private final String lobbyServer = "SG-Lobby";

    @RequiredField
    @Comment("Whether the database should be enabled")
    private final boolean databaseEnabled = true;

    @RequiredField
    @Comment("Whether the results should be pushed to the database")
    private final boolean pushResults = false;

    private final int mongoPort = 27017;
    private final String mongoHost = "127.0.0.1";
    private final String mongoDatabase = "Pot-SG";
    private final String mongoUsername = "admin";
    private final String mongoPassword = "admin";

}