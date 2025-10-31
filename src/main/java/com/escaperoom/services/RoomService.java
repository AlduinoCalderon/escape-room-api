package com.escaperoom.services;

import com.escaperoom.models.Enemy;
import com.escaperoom.repositories.EnemyRepository;

import java.util.ArrayList;
import java.util.List;

public class RoomService {
    private final EnemyRepository enemyRepository;
    
    public RoomService(EnemyRepository enemyRepository) {
        this.enemyRepository = enemyRepository;
    }
    
    public void initializeRoom(String roomName, List<Enemy> defaultEnemies) {
        enemyRepository.initializeRoom(roomName, defaultEnemies);
    }
    
    public List<Enemy> createPrisonEnemies() {
        List<Enemy> enemies = new ArrayList<>();
        
        // Create enemies with varying attributes
        enemies.add(new Enemy("Prison Guard", 100, true, "prison", false, null));
        enemies.add(new Enemy("Cellmate", 50, false, "prison", false, null));
        enemies.add(new Enemy("Warden", 150, true, "prison", true, "kitchen")); // Key to kitchen
        enemies.add(new Enemy("Thug", 75, false, "prison", false, null));
        
        return enemies;
    }
    
    public boolean roomExists(String roomName) {
        // For now, we'll check if any enemies exist in this room
        // In the future, we could have a rooms collection
        return !enemyRepository.findAllByRoom(roomName).isEmpty() || roomName.equals("prison");
    }
}

