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
        enemies.add(new Enemy("Warden", 150, true, "prison", true, "firewall-sector-key")); // Key to firewall sector
        enemies.add(new Enemy("Thug", 75, false, "prison", false, null));
        
        return enemies;
    }
    
    public List<Enemy> createFirewallSectorEnemies() {
        List<Enemy> enemies = new ArrayList<>();
        
        // Firewall Sector enemies
        enemies.add(new Enemy("Security Bot", 80, false, "firewall-sector-key", false, null));
        enemies.add(new Enemy("Firewall Guardian", 120, true, "firewall-sector-key", false, null));
        enemies.add(new Enemy("Rule_Admin_Boss", 200, true, "firewall-sector-key", true, "kernel-core-access")); // Boss with key
        enemies.add(new Enemy("Code Sentry", 90, false, "firewall-sector-key", false, null));
        
        return enemies;
    }
    
    public List<Enemy> createKernelCoreEnemies() {
        List<Enemy> enemies = new ArrayList<>();
        
        // Kernel Core enemies - final room
        enemies.add(new Enemy("Core Defender", 100, true, "kernel-core-access", false, null));
        enemies.add(new Enemy("FINAL_BOSS_WARDEN", 300, true, "kernel-core-access", false, null)); // Final boss - no key (game ends)
        enemies.add(new Enemy("System Guard", 80, false, "kernel-core-access", false, null));
        
        return enemies;
    }
    
    public boolean roomExists(String roomName) {
        // Check if any enemies exist in this room or if it's a known room
        if (roomName.equals("prison") || 
            roomName.equals("firewall-sector-key") || 
            roomName.equals("kernel-core-access")) {
            return true;
        }
        return !enemyRepository.findAllByRoom(roomName).isEmpty();
    }
    
    public String getRoomDescription(String roomName) {
        switch (roomName) {
            case "prison":
                return "Prison Cell - Your escape begins here. Defeat the warden to get the firewall sector key.";
            case "firewall-sector-key":
                return "Firewall Sector - A dangerous network zone protected by security bots and rules. Find Rule_Admin_Boss and defeat him to get kernel core access.";
            case "kernel-core-access":
                return "Kernel Core - The final room. Defeat the FINAL_BOSS_WARDEN and escape the system!";
            default:
                return roomName + " Room";
        }
    }
}

