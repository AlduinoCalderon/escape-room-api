package com.escaperoom;

import com.escaperoom.config.DatabaseConfig;
import com.escaperoom.config.EnvironmentConfig;
import com.escaperoom.controllers.PrisonRoomController;
import com.escaperoom.controllers.RoomController;
import com.escaperoom.services.RoomService;
import spark.Spark;

import static spark.Spark.*;

public class EscapeRoomApplication {
    public static void main(String[] args) {
        // Load environment variables
        EnvironmentConfig.load();
        
        // Initialize database connection
        DatabaseConfig.initialize();
        
        // Set port
        int port = Integer.parseInt(EnvironmentConfig.getProperty("PORT", "4567"));
        port(port);
        
        // Enable CORS
        enableCORS();
        
        // Static files
        staticFiles.location("/public");
        
        // Register room controllers (prison first to avoid route conflicts)
        PrisonRoomController.registerRoutes();
        RoomController.registerRoutes();
        
        // Default route
        get("/", (req, res) -> {
            res.redirect("/prison");
            return null;
        });
        
        // Health check
        get("/health", (req, res) -> "OK");
        
        System.out.println("Escape Room API started on port " + port);
    }
    
    private static void enableCORS() {
        options("/*", (request, response) -> {
            String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
            }
            String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
            if (accessControlRequestMethod != null) {
                response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
            }
            return "OK";
        });

        before((request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Request-Method", "GET,POST,PUT,DELETE,OPTIONS");
            response.header("Access-Control-Allow-Headers", "Content-Type,Authorization,X-Requested-With,Content-Length,Accept,Origin");
        });
    }
}

