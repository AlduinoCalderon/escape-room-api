package com.escaperoom.controllers;

import com.escaperoom.models.Enemy;
import com.escaperoom.models.GameScore;
import com.escaperoom.repositories.EnemyRepository;
import com.escaperoom.services.EnemyService;
import com.escaperoom.services.RoomService;
import com.escaperoom.services.ScoreService;
import com.google.gson.Gson;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import com.escaperoom.util.CustomMustacheTemplateEngine;

import java.util.HashMap;
import java.util.Map;

import static spark.Spark.*;

public class RoomController {
    private static final Gson gson = new Gson();
    private static final CustomMustacheTemplateEngine templateEngine = new CustomMustacheTemplateEngine();
    private static EnemyRepository enemyRepository;
    private static EnemyService enemyService;
    private static RoomService roomService;
    private static ScoreService scoreService;
    
    public static void registerRoutes() {
        // Initialize services
        enemyRepository = new EnemyRepository();
        enemyService = new EnemyService(enemyRepository);
        roomService = new RoomService(enemyRepository);
        scoreService = new ScoreService();
        
        // Initialize new rooms if they don't exist
        roomService.initializeRoom("firewall-sector-key", roomService.createFirewallSectorEnemies());
        roomService.initializeRoom("kernel-core-access", roomService.createKernelCoreEnemies());
        
        // Dynamic room routes - catch-all for room endpoints
        // This allows rooms to be accessed by their key name
        get("/:roomName", RoomController::renderRoomPage, templateEngine);
        get("/:roomName/enemies", RoomController::getEnemies);
        delete("/:roomName/enemies/:id", RoomController::killEnemy);
        put("/:roomName/enemies/:id/shield", RoomController::destroyShield);
        
        // Escape endpoint
        post("/escape", RoomController::escape);
        
        // Reset game endpoint
        post("/reset", RoomController::resetGame);
        
        // Victory page route
        get("/victory", RoomController::showVictory);
    }
    
    private static ModelAndView renderRoomPage(Request req, Response res) {
        String roomName = req.params(":roomName");
        
        // Skip if it's a special route or the prison route (handled by PrisonRoomController)
        if (roomName.equals("prison") || roomName.equals("health") || roomName.equals("favicon.ico") || roomName.equals("escape")) {
            return null;
        }
        
        // Check if room exists
        if (!roomService.roomExists(roomName)) {
            res.status(404);
            Map<String, Object> model = new HashMap<>();
            model.put("error", "Room not found: " + roomName);
            return new ModelAndView(model, "error.mustache");
        }
        
        // Get room title
        String roomTitle = getRoomTitle(roomName);
        
        // Check if Kernel Core is completed - show victory screen
        if (roomName.equals("kernel-core-access") && enemyService.isKernelCoreCompleted()) {
            GameScore score = scoreService.getCurrentScore();
            Map<String, Object> model = new HashMap<>();
            model.put("defeatedCount", score.getDefeatedCount());
            model.put("collectiblesCount", score.getCollectiblesCount());
            return new ModelAndView(model, "victory.mustache");
        }
        
        GameScore score = scoreService.getCurrentScore();
        
        // Check if there's an available key from a defeated enemy (only if all enemies defeated)
        String availableKey = enemyService.getAvailableKeyFromRoom(roomName);
        boolean allDefeated = enemyService.areAllEnemiesDefeated(roomName);
        
        Map<String, Object> model = new HashMap<>();
        model.put("roomName", roomName);
        model.put("roomTitle", roomTitle);
        model.put("enemies", enemyService.getEnemiesByRoom(roomName));
        model.put("defeatedCount", score.getDefeatedCount());
        model.put("collectiblesCount", score.getCollectiblesCount());
        model.put("allDefeated", allDefeated);
        
        // Add key info if available (only show if all enemies are defeated)
        if (availableKey != null && allDefeated) {
            model.put("hasAvailableKey", true);
            model.put("availableKey", availableKey);
            model.put("nextRoomPath", "/" + availableKey);
        } else {
            model.put("hasAvailableKey", false);
        }
        
        return new ModelAndView(model, "room.mustache");
    }
    
    private static String getRoomTitle(String roomName) {
        switch (roomName) {
            case "firewall-sector-key":
                return "Firewall Sector";
            case "kernel-core-access":
                return "Kernel Core";
            default:
                return capitalizeFirst(roomName.replace("-", " ")) + " Room";
        }
    }
    
    private static String getEnemies(Request req, Response res) {
        res.type("application/json");
        String roomName = req.params(":roomName");
        
        return gson.toJson(enemyService.getEnemiesByRoom(roomName));
    }
    
    private static String killEnemy(Request req, Response res) {
        res.type("application/json");
        String roomName = req.params(":roomName");
        String id = req.params(":id");
        
        Enemy enemy = enemyService.killEnemy(id, roomName);
        if (enemy == null) {
            res.status(404);
            return gson.toJson(Map.of("error", "Enemy not found"));
        }
        
        // Update score
        scoreService.incrementDefeated();
        if (enemy.isHasKey()) {
            scoreService.incrementCollectible();
        }
        
        Map<String, Object> response = new HashMap<>();
        
        // Check if this is the final boss
        if (enemy.getName().equals("FINAL_BOSS_WARDEN")) {
            response.put("message", "boss-killed");
            response.put("bossDefeated", true);
        } else {
            response.put("message", "Enemy defeated");
        }
        
        response.put("enemy", enemy);
        response.put("score", scoreService.getScore());
        if (enemy.isHasKey()) {
            response.put("key", enemy.getKeyValue());
            response.put("nextRoom", "/" + enemy.getKeyValue());
        } else {
            response.put("key", null);
        }
        
        return gson.toJson(response);
    }
    
    private static String destroyShield(Request req, Response res) {
        res.type("application/json");
        String roomName = req.params(":roomName");
        String id = req.params(":id");
        
        try {
            Enemy enemy = enemyService.destroyShield(id, roomName);
            return gson.toJson(Map.of("message", "Shield destroyed", "enemy", enemy));
        } catch (IllegalArgumentException e) {
            res.status(400);
            return gson.toJson(Map.of("error", e.getMessage()));
        }
    }
    
    private static ModelAndView showVictory(Request req, Response res) {
        GameScore score = scoreService.getCurrentScore();
        Map<String, Object> model = new HashMap<>();
        model.put("defeatedCount", score.getDefeatedCount());
        model.put("collectiblesCount", score.getCollectiblesCount());
        return new ModelAndView(model, "victory.mustache");
    }
    
    private static String escape(Request req, Response res) {
        res.type("application/json");
        
        // Check if final boss is defeated
        if (!enemyService.isKernelCoreCompleted()) {
            res.status(400);
            return gson.toJson(Map.of(
                "error", "Cannot escape yet! Defeat FINAL_BOSS_WARDEN first.",
                "status", "ESCAPE_FAILED"
            ));
        }
        
        // Return 500 error - you escaped from the server!
        res.status(500);
        GameScore finalScore = scoreService.getCurrentScore();
        Map<String, Object> response = new HashMap<>();
        response.put("error", "ESCAPED_FROM_SERVER");
        response.put("message", "Connection established with external host. YOU ARE FREE. You escaped from the server!");
        response.put("status", "ESCAPED-SUCCESS");
        response.put("finalScore", finalScore);
        
        return gson.toJson(response);
    }
    
    private static String resetGame(Request req, Response res) {
        res.type("application/json");
        
        try {
            // Reset score
            scoreService.reset();
            
            // Force reset all rooms
            roomService.resetAllRooms();
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Game reset successfully. All enemies restored.");
            response.put("redirect", "/prison");
            
            return gson.toJson(response);
        } catch (Exception e) {
            res.status(500);
            return gson.toJson(Map.of("error", "Failed to reset game: " + e.getMessage()));
        }
    }
    
    private static String capitalizeFirst(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}

