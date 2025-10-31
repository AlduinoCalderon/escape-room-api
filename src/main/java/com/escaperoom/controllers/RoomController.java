package com.escaperoom.controllers;

import com.escaperoom.models.Enemy;
import com.escaperoom.repositories.EnemyRepository;
import com.escaperoom.services.EnemyService;
import com.escaperoom.services.RoomService;
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
    
    public static void registerRoutes() {
        // Initialize services
        enemyRepository = new EnemyRepository();
        enemyService = new EnemyService(enemyRepository);
        roomService = new RoomService(enemyRepository);
        
        // Dynamic room routes - catch-all for room endpoints
        // This allows rooms to be accessed by their key name
        get("/:roomName", RoomController::renderRoomPage);
        get("/:roomName/enemies", RoomController::getEnemies);
        delete("/:roomName/enemies/:id", RoomController::killEnemy);
        put("/:roomName/enemies/:id/shield", RoomController::destroyShield);
    }
    
    private static String renderRoomPage(Request req, Response res) {
        String roomName = req.params(":roomName");
        
        // Skip if it's a special route or the prison route (handled by PrisonRoomController)
        if (roomName.equals("prison") || roomName.equals("health") || roomName.equals("favicon.ico")) {
            return null;
        }
        
        // Check if room exists
        if (!roomService.roomExists(roomName)) {
            res.status(404);
            Map<String, Object> model = new HashMap<>();
            model.put("error", "Room not found: " + roomName);
            return templateEngine.render(new ModelAndView(model, "error.mustache"));
        }
        
        Map<String, Object> model = new HashMap<>();
        model.put("roomName", roomName);
        model.put("roomTitle", capitalizeFirst(roomName) + " Room");
        model.put("enemies", enemyService.getEnemiesByRoom(roomName));
        return templateEngine.render(new ModelAndView(model, "room.mustache"));
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
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Enemy defeated");
        response.put("enemy", enemy);
        if (enemy.isHasKey()) {
            response.put("key", enemy.getKeyValue());
            response.put("nextRoom", "/" + enemy.getKeyValue());
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
    
    private static String capitalizeFirst(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}

