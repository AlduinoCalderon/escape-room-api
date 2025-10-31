package com.escaperoom.repositories;

import com.escaperoom.config.DatabaseConfig;
import com.escaperoom.models.Enemy;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public class EnemyRepository {
    private static final String COLLECTION_NAME = "enemies";
    
    private MongoCollection<Document> getCollection() {
        MongoDatabase database = DatabaseConfig.getDatabase();
        return database.getCollection(COLLECTION_NAME);
    }
    
    public List<Enemy> findAllByRoom(String roomName) {
        List<Enemy> enemies = new ArrayList<>();
        getCollection().find(
            Filters.and(
                Filters.eq("roomName", roomName),
                Filters.eq("deleted", false)
            )
        ).forEach(doc -> enemies.add(Enemy.fromDocument(doc)));
        return enemies;
    }
    
    public List<Enemy> findAllByRoomIncludingDeleted(String roomName) {
        List<Enemy> enemies = new ArrayList<>();
        getCollection().find(
            Filters.eq("roomName", roomName)
        ).forEach(doc -> enemies.add(Enemy.fromDocument(doc)));
        return enemies;
    }
    
    public Enemy findById(String id) {
        Document doc = getCollection().find(
            Filters.and(
                Filters.eq("_id", new ObjectId(id)),
                Filters.eq("deleted", false)
            )
        ).first();
        return doc != null ? Enemy.fromDocument(doc) : null;
    }
    
    public Enemy findByIdAndRoom(String id, String roomName) {
        Document doc = getCollection().find(
            Filters.and(
                Filters.eq("_id", new ObjectId(id)),
                Filters.eq("roomName", roomName),
                Filters.eq("deleted", false)
            )
        ).first();
        return doc != null ? Enemy.fromDocument(doc) : null;
    }
    
    public Enemy save(Enemy enemy) {
        Document doc = enemy.toDocument();
        getCollection().insertOne(doc);
        enemy.setId(doc.getObjectId("_id").toString());
        return enemy;
    }
    
    public Enemy update(Enemy enemy) {
        Document doc = enemy.toDocument();
        getCollection().replaceOne(
            Filters.eq("_id", new ObjectId(enemy.getId())),
            doc
        );
        return enemy;
    }
    
    public void softDelete(String id) {
        getCollection().updateOne(
            Filters.eq("_id", new ObjectId(id)),
            new Document("$set", new Document("deleted", true))
        );
    }
    
    public void initializeRoom(String roomName, List<Enemy> enemies) {
        // Check if room already has enemies
        List<Enemy> existingEnemies = findAllByRoom(roomName);
        if (!existingEnemies.isEmpty()) {
            System.out.println("Room '" + roomName + "' already has " + existingEnemies.size() + " enemies. Skipping initialization.");
            return; // Room already initialized
        }
        
        // Insert enemies for this room
        List<Document> documents = new ArrayList<>();
        for (Enemy enemy : enemies) {
            enemy.setRoomName(roomName);
            documents.add(enemy.toDocument());
        }
        if (!documents.isEmpty()) {
            getCollection().insertMany(documents);
            System.out.println("Initialized room '" + roomName + "' with " + documents.size() + " enemies in collection 'enemies'");
        }
    }
}

