# Architecture Documentation

## Overview

This Escape Room API follows **MVC (Model-View-Controller)** architecture with **SOLID principles** to ensure modularity, maintainability, and extensibility.

## Project Structure

```
escapeRoom/
├── src/main/java/com/escaperoom/
│   ├── EscapeRoomApplication.java      # Main application entry point
│   ├── config/                         # Configuration classes
│   │   ├── DatabaseConfig.java         # MongoDB connection setup
│   │   └── EnvironmentConfig.java      # Environment variable loader
│   ├── controllers/                    # MVC Controllers
│   │   ├── PrisonRoomController.java   # Prison room controller
│   │   └── RoomController.java         # Dynamic room controller
│   ├── models/                         # Entity models
│   │   └── Enemy.java                  # Enemy entity with business logic
│   ├── repositories/                   # Data access layer (Repository pattern)
│   │   └── EnemyRepository.java        # MongoDB operations for enemies
│   └── services/                       # Business logic layer (Service pattern)
│       ├── EnemyService.java           # Enemy business logic
│       └── RoomService.java            # Room management logic
└── src/main/resources/
    └── templates/                      # Mustache templates
        ├── room.mustache               # Room view template
        └── error.mustache              # Error view template
```

## Design Patterns

### 1. MVC Pattern
- **Models**: Entity classes (`Enemy.java`) - represent data structure
- **Views**: Mustache templates - present data to users
- **Controllers**: Handle HTTP requests (`PrisonRoomController`, `RoomController`)

### 2. Repository Pattern
- `EnemyRepository` abstracts database operations
- Provides clean interface for data access
- Easy to swap database implementations

### 3. Service Pattern
- Business logic separated from controllers
- `EnemyService` handles enemy-related operations
- `RoomService` manages room initialization and creation

## SOLID Principles

### Single Responsibility Principle (SRP)
- Each class has one clear responsibility:
  - `EnemyRepository`: Only database operations
  - `EnemyService`: Only enemy business logic
  - `PrisonRoomController`: Only prison room routes

### Open/Closed Principle (OCP)
- Room system is open for extension:
  - Add new rooms by creating new controllers or extending `RoomService`
  - No need to modify existing code

### Liskov Substitution Principle (LSP)
- Repository interfaces can be swapped without breaking functionality

### Interface Segregation Principle (ISP)
- Services and repositories are focused and specific

### Dependency Inversion Principle (DIP)
- Controllers depend on services (abstractions)
- Services depend on repositories (abstractions)
- Easy to mock for testing

## Modular Room System

### How It Works

1. **Starting Room**: Prison room is initialized with default enemies
2. **Key System**: One enemy per room drops a key (string value)
3. **Room Progression**: Key becomes the next room endpoint
   - Example: Enemy drops key "kitchen"
   - Next room accessible at `/kitchen/enemies`

### Adding New Rooms

To add a new room (e.g., "kitchen"):

1. **Create enemies in a room**:
   ```java
   // In RoomService or new KitchenRoomController
   List<Enemy> kitchenEnemies = new ArrayList<>();
   kitchenEnemies.add(new Enemy("Chef", 80, true, "kitchen", false, null));
   kitchenEnemies.add(new Enemy("Cook", 60, false, "kitchen", true, "dining"));
   roomService.initializeRoom("kitchen", kitchenEnemies);
   ```

2. **Room automatically accessible**:
   - Access at: `/kitchen/enemies`
   - Web UI at: `/kitchen`

3. **Dynamic routing**: `RoomController` handles any room name dynamically

## API Endpoints

### Prison Room (Starting Room)
- `GET /prison` - Web interface
- `GET /prison/enemies` - List all enemies (JSON)
- `DELETE /prison/enemies/:id` - Kill enemy (soft delete)
- `PUT /prison/enemies/:id/shield` - Destroy enemy's shield

### Dynamic Rooms (Any room accessed by key)
- `GET /:roomName` - Web interface
- `GET /:roomName/enemies` - List all enemies (JSON)
- `DELETE /:roomName/enemies/:id` - Kill enemy (soft delete)
- `PUT /:roomName/enemies/:id/shield` - Destroy enemy's shield

## Data Model

### Enemy Model
```java
{
  "id": "ObjectId",
  "name": "String",
  "health": int,
  "maxHealth": int,
  "hasShield": boolean,
  "shieldDestroyed": boolean,
  "roomName": "String",
  "deleted": boolean,          // Soft deletion flag
  "hasKey": boolean,
  "keyValue": "String"         // Next room name
}
```

## Database Schema

**Collection**: `enemies`
- Uses MongoDB with soft deletion
- Queries filter by `deleted: false`
- Room-based queries using `roomName` field

## Security Considerations

1. **Environment Variables**: Sensitive data in `.env` file
2. **CORS**: Enabled for development (adjust for production)
3. **Input Validation**: Add validation in services for production
4. **Error Handling**: Proper error responses with status codes

## Future Enhancements

1. **Authentication**: Add user authentication system
2. **Game State**: Track player progress and inventory
3. **More Rooms**: Kitchen, Dining Hall, Library, etc.
4. **Combat System**: More detailed combat mechanics
5. **Puzzles**: Add puzzle-solving mechanics
6. **Save/Load**: Persist game state per user
7. **Room Factory**: Pattern to create rooms dynamically

