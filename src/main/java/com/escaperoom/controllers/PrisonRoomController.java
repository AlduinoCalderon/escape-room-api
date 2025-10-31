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

public class PrisonRoomController {
    private static final String ROOM_NAME = "prison";
    private static final Gson gson = new Gson();
    private static final CustomMustacheTemplateEngine templateEngine = new CustomMustacheTemplateEngine();
    private static EnemyService enemyService;
    private static RoomService roomService;
    private static ScoreService scoreService;
    
    public static void registerRoutes() {
        // Initialize services
        EnemyRepository enemyRepository = new EnemyRepository();
        enemyService = new EnemyService(enemyRepository);
        roomService = new RoomService(enemyRepository);
        scoreService = new ScoreService();
        
        // Initialize prison room with default enemies
        roomService.initializeRoom(ROOM_NAME, roomService.createPrisonEnemies());
        
        // Web frontend route
        get("/" + ROOM_NAME, PrisonRoomController::renderRoomPage, templateEngine);
        
        // API routes
        get("/" + ROOM_NAME + "/enemies", PrisonRoomController::getEnemies);
        delete("/" + ROOM_NAME + "/enemies/:id", PrisonRoomController::killEnemy);
        put("/" + ROOM_NAME + "/enemies/:id/shield", PrisonRoomController::destroyShield);
    }
    
    private static ModelAndView renderRoomPage(Request req, Response res) {
        Map<String, Object> model = new HashMap<>();
        GameScore score = scoreService.getCurrentScore();
        model.put("roomName", ROOM_NAME);
        model.put("roomTitle", "Prison Cell");
        model.put("enemies", enemyService.getEnemiesByRoom(ROOM_NAME));
        model.put("defeatedCount", score.getDefeatedCount());
        model.put("collectiblesCount", score.getCollectiblesCount());
        return new ModelAndView(model, "room.mustache");
    }
    
    private static String getEnemies(Request req, Response res) {
        res.type("application/json");
        return gson.toJson(enemyService.getEnemiesByRoom(ROOM_NAME));
    }
    
    private static String killEnemy(Request req, Response res) {
        res.type("application/json");
        String id = req.params(":id");
        
        Enemy enemy = enemyService.killEnemy(id, ROOM_NAME);
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
        response.put("message", "Enemy defeated");
        response.put("enemy", enemy);
        response.put("score", scoreService.getScore());
        if (enemy.isHasKey()) {
            response.put("key", enemy.getKeyValue());
            response.put("nextRoom", "/" + enemy.getKeyValue());
        }
        
        return gson.toJson(response);
    }
    
    private static String destroyShield(Request req, Response res) {
        res.type("application/json");
        String id = req.params(":id");
        
        try {
            Enemy enemy = enemyService.destroyShield(id, ROOM_NAME);
            return gson.toJson(Map.of("message", "Shield destroyed", "enemy", enemy));
        } catch (IllegalArgumentException e) {
            res.status(400);
            return gson.toJson(Map.of("error", e.getMessage()));
        }
    }
}

