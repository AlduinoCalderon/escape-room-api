package com.escaperoom.models;

import org.bson.Document;

public class GameScore {
    private int defeatedCount;
    private int collectiblesCount;
    
    public GameScore() {
        this.defeatedCount = 0;
        this.collectiblesCount = 0;
    }
    
    public GameScore(int defeatedCount, int collectiblesCount) {
        this.defeatedCount = defeatedCount;
        this.collectiblesCount = collectiblesCount;
    }
    
    public void incrementDefeated() {
        this.defeatedCount++;
    }
    
    public void incrementCollectibles() {
        this.collectiblesCount++;
    }
    
    public void addDefeated(int count) {
        this.defeatedCount += count;
    }
    
    public void addCollectibles(int count) {
        this.collectiblesCount += count;
    }
    
    // Convert to MongoDB Document
    public Document toDocument() {
        return new Document()
                .append("defeatedCount", defeatedCount)
                .append("collectiblesCount", collectiblesCount);
    }
    
    // Create from MongoDB Document
    public static GameScore fromDocument(Document doc) {
        GameScore score = new GameScore();
        score.defeatedCount = doc.getInteger("defeatedCount", 0);
        score.collectiblesCount = doc.getInteger("collectiblesCount", 0);
        return score;
    }
    
    // Getters and Setters
    public int getDefeatedCount() {
        return defeatedCount;
    }
    
    public void setDefeatedCount(int defeatedCount) {
        this.defeatedCount = defeatedCount;
    }
    
    public int getCollectiblesCount() {
        return collectiblesCount;
    }
    
    public void setCollectiblesCount(int collectiblesCount) {
        this.collectiblesCount = collectiblesCount;
    }
}

