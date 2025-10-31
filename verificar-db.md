# Verificación de Base de Datos

## Problema Identificado

Si estás viendo en MongoDB Compass:
- **Base de datos**: `ColdStorages`
- **Colección**: `Readings`

Pero la aplicación espera:
- **Base de datos**: `escapeRoom` (o el valor de `MONGODB_DATABASE` en `.env`)
- **Colección**: `enemies`

## Solución

### 1. Verifica tu archivo `.env`

Asegúrate de que tu `.env` tenga:

```env
MONGODB_URI=mongodb+srv://username:password@cluster.mongodb.net/escapeRoom?retryWrites=true&w=majority
MONGODB_DATABASE=escapeRoom
PORT=4567
```

**Importante**: El nombre de la base de datos en `MONGODB_URI` (después de `.net/`) debe coincidir con `MONGODB_DATABASE`.

### 2. En MongoDB Compass

1. **Conéctate al mismo cluster** que usas en tu `.env`
2. **Busca la base de datos `escapeRoom`** (no `ColdStorages`)
3. **Dentro de `escapeRoom`**, deberías ver la colección `enemies`

### 3. Si la base de datos no existe

Cuando la aplicación se ejecute por primera vez:
- La base de datos `escapeRoom` se creará automáticamente
- La colección `enemies` se creará cuando se inserten los primeros datos

### 4. Verifica los logs al iniciar

Deberías ver:
```
Connecting to MongoDB...
Database name from .env: escapeRoom
✓ Connected to MongoDB database: escapeRoom
✓ Collection 'enemies' will be used for game data
Initialized room 'prison' with 4 enemies in collection 'enemies'
```

## ¿Por qué ves `ColdStorages`?

`ColdStorages` es probablemente **otra base de datos** en el mismo cluster de MongoDB Atlas, pero no es la que usa la aplicación Escape Room.

## Pasos para encontrar la base de datos correcta

1. En MongoDB Compass, en el panel izquierdo:
   - Busca la base de datos `escapeRoom`
   - Si no existe, espera a que la aplicación la cree cuando se ejecute

2. Si tienes múltiples conexiones en Compass:
   - Asegúrate de estar conectado al mismo cluster que configuraste en `.env`
   - El cluster debe coincidir con la parte `@cluster.mongodb.net` de tu `MONGODB_URI`

3. Una vez dentro de `escapeRoom`, busca la colección `enemies`

