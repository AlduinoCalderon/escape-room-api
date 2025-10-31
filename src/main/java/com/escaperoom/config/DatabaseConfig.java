package com.escaperoom.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class DatabaseConfig {
    private static MongoClient mongoClient;
    private static MongoDatabase database;
    
    public static void initialize() {
        String connectionUri = EnvironmentConfig.getProperty("MONGODB_URI");
        String databaseName = EnvironmentConfig.getProperty("MONGODB_DATABASE", "escapeRoom");
        
        mongoClient = MongoClients.create(connectionUri);
        database = mongoClient.getDatabase(databaseName);
        
        System.out.println("Connected to MongoDB: " + databaseName);
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

