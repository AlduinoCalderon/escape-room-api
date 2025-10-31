package com.escaperoom.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class DatabaseConfig {
    private static MongoClient mongoClient;
    private static MongoDatabase database;
    
    public static void initialize() {
        String connectionUri = EnvironmentConfig.getProperty("MONGODB_URI");
        String databaseName = EnvironmentConfig.getProperty("MONGODB_DATABASE", "EscapeRoom");
        
        System.out.println("========================================");
        System.out.println("Connecting to MongoDB...");
        System.out.println("Database name from .env: " + databaseName);
        
        try {
            mongoClient = MongoClients.create(connectionUri);
            database = mongoClient.getDatabase(databaseName);
            
            // Verify connection by listing collections
            System.out.println("✓ Connected to MongoDB database: " + databaseName);
            System.out.println("✓ Collection 'enemies' will be used for game data");
            
            // List existing collections for debugging
            for (String collectionName : database.listCollectionNames()) {
                long count = database.getCollection(collectionName).countDocuments();
                System.out.println("  - Found collection '" + collectionName + "' with " + count + " documents");
            }
            
        } catch (Exception e) {
            System.err.println("ERROR connecting to MongoDB: " + e.getMessage());
            throw new RuntimeException("Failed to connect to MongoDB", e);
        }
        
        System.out.println("========================================");
    }
    
    public static MongoDatabase getDatabase() {
        if (database == null) {
            throw new RuntimeException("Database not initialized. Call initialize() first.");
        }
        return database;
    }
    
    public static MongoClient getMongoClient() {
        return mongoClient;
    }
    
    public static void close() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }
}

