package com.escaperoom.models;

import org.bson.Document;
import org.bson.types.ObjectId;

public class Enemy {
    private String id;
    private String name;
    private int health;
    private int maxHealth;
    private boolean hasShield;
    private boolean shieldDestroyed;
    private String roomName;
    private boolean deleted;
    private boolean hasKey;
    private String keyValue;
    
    public Enemy() {
        this.deleted = false;
        this.shieldDestroyed = false;
    }
    
    public Enemy(String name, int health, boolean hasShield, String roomName, boolean hasKey, String keyValue) {
        this();
        this.name = name;
        this.health = health;
        this.maxHealth = health;
        this.hasShield = hasShield;
        this.roomName = roomName;
        this.hasKey = hasKey;
        this.keyValue = keyValue;
    }
    
    // Convert to MongoDB Document
    public Document toDocument() {
        Document doc = new Document();
        if (id != null) {
            doc.append("_id", new ObjectId(id));
        }
        doc.append("name", name)
           .append("health", health)
           .append("maxHealth", maxHealth)
           .append("hasShield", hasShield)
           .append("shieldDestroyed", shieldDestroyed)
           .append("roomName", roomName)
           .append("deleted", deleted)
           .append("hasKey", hasKey)
           .append("keyValue", keyValue);
        return doc;
    }
    
    // Create from MongoDB Document
    public static Enemy fromDocument(Document doc) {
        Enemy enemy = new Enemy();
        enemy.id = doc.getObjectId("_id").toString();
        enemy.name = doc.getString("name");
        enemy.health = doc.getInteger("health");
        enemy.maxHealth = doc.getInteger("maxHealth");
        enemy.hasShield = doc.getBoolean("hasShield");
        enemy.shieldDestroyed = doc.getBoolean("shieldDestroyed");
        enemy.roomName = doc.getString("roomName");
        enemy.deleted = doc.getBoolean("deleted");
        enemy.hasKey = doc.getBoolean("hasKey");
        enemy.keyValue = doc.getString("keyValue");
        return enemy;
    }
    
    public void destroyShield() {
        if (hasShield && !shieldDestroyed) {
            shieldDestroyed = true;
        }
    }
    
    public void takeDamage(int damage) {
        if (hasShield && !shieldDestroyed) {
            return; // Cannot damage enemy with active shield
        }
        health = Math.max(0, health - damage);
    }
    
    public boolean isDefeated() {
        return health <= 0;
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public int getHealth() { return health; }
    public void setHealth(int health) { this.health = health; }
    
    public int getMaxHealth() { return maxHealth; }
    public void setMaxHealth(int maxHealth) { this.maxHealth = maxHealth; }
    
    public boolean isHasShield() { return hasShield; }
    public void setHasShield(boolean hasShield) { this.hasShield = hasShield; }
    
    public boolean isShieldDestroyed() { return shieldDestroyed; }
    public void setShieldDestroyed(boolean shieldDestroyed) { this.shieldDestroyed = shieldDestroyed; }
    
    public String getRoomName() { return roomName; }
    public void setRoomName(String roomName) { this.roomName = roomName; }
    
    public boolean isDeleted() { return deleted; }
    public void setDeleted(boolean deleted) { this.deleted = deleted; }
    
    public boolean isHasKey() { return hasKey; }
    public void setHasKey(boolean hasKey) { this.hasKey = hasKey; }
    
    public String getKeyValue() { return keyValue; }
    public void setKeyValue(String keyValue) { this.keyValue = keyValue; }
}

