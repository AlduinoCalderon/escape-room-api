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
        // Always refresh room data from DB - check what enemies exist (including deleted)
        List<Enemy> existingEnemies = findAllByRoomIncludingDeleted(roomName);
        
        if (!existingEnemies.isEmpty()) {
            // Room has enemies - check if we need to reset any that were deleted
            // Count how many active (non-deleted) enemies we have
            long activeCount = existingEnemies.stream()
                    .filter(e -> !e.isDeleted())
                    .count();
            
            // If all enemies are deleted or we have fewer than expected, reset the room
            if (activeCount == 0 || activeCount < enemies.size()) {
                System.out.println("Room '" + roomName + "' needs refresh. Resetting " + existingEnemies.size() + " deleted enemies.");
                
                // Delete all existing enemies for this room (hard delete)
                getCollection().deleteMany(Filters.eq("roomName", roomName));
                
                // Re-insert fresh enemies
                List<Document> documents = new ArrayList<>();
                for (Enemy enemy : enemies) {
                    enemy.setRoomName(roomName);
                    enemy.setDeleted(false);
                    enemy.setHealth(enemy.getMaxHealth());
                    enemy.setShieldDestroyed(false);
                    documents.add(enemy.toDocument());
                }
                if (!documents.isEmpty()) {
                    getCollection().insertMany(documents);
                    System.out.println("Refreshed room '" + roomName + "' with " + documents.size() + " enemies");
                }
            } else {
                System.out.println("Room '" + roomName + "' already initialized with " + activeCount + " active enemies.");
            }
        } else {
            // No enemies exist, create them fresh
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
}

