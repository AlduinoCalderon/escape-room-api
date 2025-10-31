# Setting Up Remote Git Repository

## Create Remote Repository

### Option 1: GitHub

1. Go to [GitHub](https://github.com) and sign in
2. Click the "+" icon in the top right corner
3. Select "New repository"
4. Repository name: `escape-room-api` (or your preferred name)
5. Description: "Escape Room Minigame API - Java Spark"
6. Choose Public or Private
7. **DO NOT** initialize with README, .gitignore, or license
8. Click "Create repository"

### Option 2: GitLab

1. Go to [GitLab](https://gitlab.com) and sign in
2. Click "New project" or the "+" button
3. Click "Create blank project"
4. Project name: `escape-room-api`
5. Choose Public or Private
6. **DO NOT** initialize with README
7. Click "Create project"

### Option 3: Bitbucket

1. Go to [Bitbucket](https://bitbucket.org) and sign in
2. Click "Create" → "Repository"
3. Repository name: `escape-room-api`
4. Access level: Public or Private
5. **DO NOT** initialize with README
6. Click "Create repository"

## Connect Local to Remote

After creating the remote repository, run these commands:

```bash
# Add remote repository (replace with your repository URL)
git remote add origin https://github.com/YOUR_USERNAME/escape-room-api.git

# Or for SSH:
# git remote add origin git@github.com:YOUR_USERNAME/escape-room-api.git

# Push to remote
git branch -M main
git push -u origin main
```

## Verify Remote Connection

```bash
# Check remote URL
git remote -v

# Should show:
# origin  https://github.com/YOUR_USERNAME/escape-room-api.git (fetch)
# origin  https://github.com/YOUR_USERNAME/escape-room-api.git (push)
```

## Future Updates

To push future changes:

```bash
git add .
git commit -m "Your commit message"
git push origin main
```

