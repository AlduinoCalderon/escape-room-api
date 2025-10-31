# Solución: Base de Datos EscapeRoom

## Situación Actual

Has creado la base de datos `EscapeRoom` (con mayúscula) en MongoDB Compass, pero:

1. **El código espera**: `escapeRoom` (minúsculas en la variable de entorno)
2. **Estás viendo**: la colección `Kitchen` 
3. **El código busca**: la colección `enemies`

## Solución Rápida

Tienes **2 opciones**:

### Opción 1: Usar el nombre que creaste (EscapeRoom)

Actualiza tu archivo `.env`:

```env
MONGODB_URI=mongodb+srv://username:password@cluster.mongodb.net/EscapeRoom?retryWrites=true&w=majority
MONGODB_DATABASE=EscapeRoom
PORT=4567
```

**Nota**: Cambia `escapeRoom` por `EscapeRoom` (con mayúscula) en ambas líneas.

### Opción 2: Crear/Renombrar a escapeRoom (recomendado)

1. En MongoDB Compass, crea una nueva base de datos llamada `escapeRoom` (todo minúsculas)
2. O renombra `EscapeRoom` a `escapeRoom` (si es posible)
3. Mantén tu `.env` como está (con `escapeRoom` en minúsculas)

## Importante: La Colección Correcta

En MongoDB Compass, dentro de `EscapeRoom` (o `escapeRoom`), necesitas buscar:

- ✅ **Colección**: `enemies` (esta es la que usa el código)
- ❌ NO `Kitchen` (esa es otra colección diferente)

La colección `Kitchen` probablemente se creó cuando probaste acceder a `/kitchen` en la aplicación, pero los enemigos se guardan en la colección `enemies`.

## Verificación

Cuando ejecutes la aplicación, deberías ver:

```
Connecting to MongoDB...
Database name from .env: EscapeRoom (o escapeRoom)
✓ Connected to MongoDB database: EscapeRoom
✓ Collection 'enemies' will be used for game data
Initialized room 'prison' with 4 enemies in collection 'enemies'
```

Luego en Compass:
1. Ve a la base de datos `EscapeRoom` (o `escapeRoom`)
2. Busca la colección **`enemies`** (no `Kitchen`)
3. Deberías ver 4 documentos con los enemigos del prison room

## Resumen

- ✅ Base de datos: `EscapeRoom` o `escapeRoom` (debe coincidir con tu `.env`)
- ✅ Colección: `enemies` (esta es la importante)
- ❌ `Kitchen` es otra colección (probablemente creada por el room controller)

