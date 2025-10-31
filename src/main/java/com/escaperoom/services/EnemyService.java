package com.escaperoom.services;

import com.escaperoom.models.Enemy;
import com.escaperoom.repositories.EnemyRepository;

import java.util.List;

public class EnemyService {
    private final EnemyRepository enemyRepository;
    
    public EnemyService(EnemyRepository enemyRepository) {
        this.enemyRepository = enemyRepository;
    }
    
    public List<Enemy> getEnemiesByRoom(String roomName) {
        return enemyRepository.findAllByRoom(roomName);
    }
    
    public Enemy getEnemyById(String id, String roomName) {
        return enemyRepository.findByIdAndRoom(id, roomName);
    }
    
    public Enemy killEnemy(String id, String roomName) {
        Enemy enemy = enemyRepository.findByIdAndRoom(id, roomName);
        if (enemy == null) {
            return null;
        }
        
        // Soft delete the enemy
        enemy.setDeleted(true);
        enemy.setHealth(0);
        enemyRepository.softDelete(id);
        
        return enemy;
    }
    
    public Enemy destroyShield(String id, String roomName) {
        Enemy enemy = enemyRepository.findByIdAndRoom(id, roomName);
        if (enemy == null) {
            return null;
        }
        
        if (!enemy.isHasShield()) {
            throw new IllegalArgumentException("Enemy does not have a shield");
        }
        
        if (enemy.isShieldDestroyed()) {
            throw new IllegalArgumentException("Shield already destroyed");
        }
        
        enemy.destroyShield();
        enemyRepository.update(enemy);
        
        return enemy;
    }
    
    public String getKeyFromEnemy(String id, String roomName) {
        Enemy enemy = enemyRepository.findByIdAndRoom(id, roomName);
        if (enemy == null || !enemy.isHasKey()) {
            return null;
        }
        return enemy.getKeyValue();
    }
}

