package com.escaperoom.services;

import com.escaperoom.models.GameScore;

public class ScoreService {
    private static GameScore currentScore = new GameScore();
    
    public GameScore getCurrentScore() {
        // Count actual defeated enemies from database
        // For now, we'll use the static counter, but could query DB
        return currentScore;
    }
    
    public GameScore incrementDefeated() {
        currentScore.incrementDefeated();
        return currentScore;
    }
    
    public GameScore incrementCollectible() {
        currentScore.incrementCollectibles();
        return currentScore;
    }
    
    public GameScore getScore() {
        return new GameScore(currentScore.getDefeatedCount(), currentScore.getCollectiblesCount());
    }
    
    public void reset() {
        currentScore = new GameScore();
    }
}

