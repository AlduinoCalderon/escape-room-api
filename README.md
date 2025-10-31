# Escape Room API

A modular Java Spark API minigame for an escape room experience. Players fight enemies in prison rooms, defeat them to collect keys, and progress through different rooms.

## Features

- Modular room system - easily add new rooms
- MVC architecture following SOLID principles
- Enemy combat system with soft deletion
- Shield mechanics - some enemies require shield destruction first
- Key-based room progression
- MongoDB Atlas integration
- Mustache templating for web frontend

## Setup

1. Copy `.env.example` to `.env` and configure your MongoDB Atlas connection string
2. Build the project: `mvn clean install`
3. Run the application: `mvn exec:java`

## API Endpoints

### Prison Room (Starting Room)
- `GET /prison/enemies` - List all enemies in the prison
- `DELETE /prison/enemies/:id` - Kill an enemy (soft delete)
- `PUT /prison/enemies/:id/shield` - Destroy an enemy's shield

### Next Rooms
- Use the key string from a defeated enemy as the endpoint prefix
- Example: If key is "kitchen", access `/kitchen/enemies`

## Architecture

- **Models**: Entity classes (Enemy, Room, etc.)
- **Views**: Mustache templates for web frontend
- **Controllers**: Request handlers for each room
- **Services**: Business logic layer
- **Repositories**: Data access layer

