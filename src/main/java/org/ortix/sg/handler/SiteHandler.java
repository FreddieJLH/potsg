package org.ortix.sg.handler;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.bukkit.entity.HumanEntity;
import org.ortix.sg.SGPlugin;
import org.ortix.sg.game.Game;

@RequiredArgsConstructor
public class SiteHandler {

    private final SGPlugin plugin;

    public void pushGame() {
        final ServerHandler serverHandler = this.plugin.getServerHandler();

        if (this.plugin.getDatabase() == null || !this.plugin.getServerConfig().isPushResults())
            return;

        final Game game = serverHandler.getGame();
        final Document document = new Document();

        document.put("matchId", game.getMatchUuid());
        document.put("endedAt", game.getEndedAt());
        document.put("winners", game.getRemainingPlayers().stream().map(HumanEntity::getName));
        document.put("kills", new BasicDBObject(game.getKills()));

        final MongoCollection<Document> resultsCollection = this.plugin.getDatabase().getResultsCollection();
        resultsCollection.insertOne(document);
    }

}
