package org.ortix.sg.database;

import com.google.common.collect.ImmutableList;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import lombok.Getter;
import org.bson.Document;

@Getter
public class SGDatabase {

    private final MongoClient mongoClient;
    private final MongoCollection<Document> resultsCollection;

    public SGDatabase(String host, int port, String database, String username, String password) {
        if (username == null || password == null) {
            this.mongoClient = new MongoClient(host, port);
        } else {
            this.mongoClient = new MongoClient(
                    new ServerAddress(host, port),
                    ImmutableList.of(MongoCredential.createCredential(username, database, password.toCharArray()))
            );
        }

        this.resultsCollection = mongoClient.getDatabase(database).getCollection("results");
    }

}
