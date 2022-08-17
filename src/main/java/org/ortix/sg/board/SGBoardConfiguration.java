package org.ortix.sg.board;

import net.frozenorb.qlib.scoreboard.ScoreboardConfiguration;
import net.frozenorb.qlib.scoreboard.TitleGetter;
import net.frozenorb.qlib.util.Chat;
import net.minecraft.util.org.apache.commons.lang3.StringEscapeUtils;
import org.ortix.sg.SGPlugin;

public class SGBoardConfiguration {

    public static ScoreboardConfiguration create(SGPlugin plugin) {
        final ScoreboardConfiguration scoreboardConfiguration = new ScoreboardConfiguration();
        final String scoreboardTitle = plugin.getServerConfig().getScoreboardTitle();

        scoreboardConfiguration.setTitleGetter(TitleGetter.forStaticString(
                Chat.translate(scoreboardTitle.replaceAll("%line%", StringEscapeUtils.unescapeJava("\u2758")))
        ));

        scoreboardConfiguration.setScoreGetter(new SGScoreGetter(plugin));

        return scoreboardConfiguration;
    }

}
