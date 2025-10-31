# Setup Instructions

## Prerequisites
- Java 11 or higher
- Maven 3.6 or higher
- MongoDB Atlas account (free tier works)

## Local Setup

1. **Clone or download the repository**
   ```bash
   git clone <repository-url>
   cd escapeRoom
   ```

2. **Configure Environment Variables**
   - Copy `.env.example` to `.env` (if it exists) or create a new `.env` file
   - Add your MongoDB Atlas connection string:
     ```
     MONGODB_URI=mongodb+srv://username:password@cluster.mongodb.net/escapeRoom?retryWrites=true&w=majority
     MONGODB_DATABASE=escapeRoom
     PORT=4567
     ```

3. **Build the project**
   ```bash
   mvn clean install
   ```

4. **Run the application**
   ```bash
   mvn exec:java
   ```

5. **Access the application**
   - Web interface: http://localhost:4567/prison
   - API: http://localhost:4567/prison/enemies

## Git Remote Repository

To create a remote repository:

1. **Create a new repository on GitHub/GitLab/Bitbucket**
   - Go to your preferred Git hosting service
   - Create a new repository (name: `escape-room-api` or similar)
   - Do NOT initialize with README, .gitignore, or license

2. **Add remote and push**
   ```bash
   git remote add origin <your-repository-url>
   git add .
   git commit -m "Initial commit: Escape Room API with prison room"
   git branch -M main
   git push -u origin main
   ```

## MongoDB Atlas Setup

1. **Create a MongoDB Atlas account** (free tier available)
2. **Create a new cluster**
3. **Create a database user** with read/write permissions
4. **Whitelist your IP address** (or use 0.0.0.0/0 for development)
5. **Get your connection string** from the Atlas dashboard
6. **Update `.env` file** with your connection string

## Project Structure

```
escapeRoom/
├── src/
│   ├── main/
│   │   ├── java/com/escaperoom/
│   │   │   ├── config/          # Configuration classes
│   │   │   ├── controllers/     # MVC Controllers
│   │   │   ├── models/          # Entity models
│   │   │   ├── repositories/    # Data access layer
│   │   │   └── services/        # Business logic
│   │   └── resources/
│   │       └── templates/       # Mustache templates
│   └── test/
├── pom.xml
├── .env
└── README.md
```

## Adding New Rooms

To add a new room:

1. Create a new RoomController or extend the existing RoomService
2. Define enemies with keys pointing to the next room
3. The room will be accessible at `/{keyValue}/enemies`
4. Example: If an enemy drops key "kitchen", access `/kitchen/enemies`

