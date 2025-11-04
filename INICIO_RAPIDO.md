# 🚀 GUÍA DE INICIO RÁPIDO - NUTRITRACK API

## ✅ Pre-requisitos verificados:
- ✅ Docker corriendo (PostgreSQL en puerto 5432)
- ✅ Código fuente creado
- ✅ application.properties configurado

## 📝 PASOS PARA EJECUTAR EN INTELLIJ IDEA:

### 1. Recargar Maven
- Click derecho en `pom.xml`
- Seleccionar **Maven → Reload Project**
- Espera a que descargue todas las dependencias (puede tardar 2-5 minutos)

### 2. Verificar que no hay errores
- Abre `src/main/java/com/example/nutritrackapi/NutritrackApiApplication.java`
- NO debe haber líneas rojas
- Si hay errores, presiona `Ctrl + Alt + Shift + S` → Project Settings → SDK debe ser Java 21

### 3. Ejecutar la aplicación
**Opción A (Recomendada):**
- Click derecho en `NutritrackApiApplication.java`
- Seleccionar **Run 'NutritrackApiApplication.main()'**

**Opción B:**
- Presiona `Shift + F10`

**Opción C:**
- Click en el botón ▶️ verde junto a la clase `NutritrackApiApplication`

### 4. Verificar que inició correctamente

Deberías ver en la consola de IntelliJ:

```
🚀 Iniciando NutriTrack API...
📝 Creando roles por defecto...
✅ Roles creados: ROLE_USER, ROLE_ADMIN
✅ Aplicación lista!

Started NutritrackApiApplication in X.XXX seconds
```

### 5. Probar los endpoints

**En tu navegador:**
- Health Check: http://localhost:8080/api/v1/health
- Swagger UI: http://localhost:8080/swagger-ui.html

**En PowerShell:**
```powershell
curl http://localhost:8080/api/v1/health
```

Deberías ver:
```json
{
  "status": "UP",
  "service": "NutriTrack API",
  "timestamp": "2025-11-04T...",
  "version": "1.0.0",
  "environment": "development"
}
```

## 🚨 SOLUCIÓN DE PROBLEMAS:

### Error: "Cannot resolve symbol 'jakarta'"
**Solución:** Maven no descargó las dependencias
- File → Invalidate Caches → Invalidate and Restart

### Error: "Port 8080 already in use"
**Solución:** Hay otra aplicación usando el puerto
```powershell
# Ver qué está usando el puerto
netstat -ano | findstr :8080

# Matar el proceso (cambia XXXX por el PID)
taskkill /PID XXXX /F
```

### Error: "Unable to connect to database"
**Solución:** PostgreSQL no está corriendo
```powershell
# Verificar Docker
docker ps

# Si no está corriendo
docker-compose up -d

# Ver logs de PostgreSQL
docker-compose logs -f postgres
```

### Error de compilación en Maven
**Solución:** Limpiar y recompilar
- Maven tab (lateral derecho) → Lifecycle → clean → install

## 📊 ENDPOINTS DISPONIBLES:

| Método | URL | Descripción |
|--------|-----|-------------|
| GET | `/api/v1/health` | Estado del servidor |
| GET | `/api/v1/health/ping` | Ping simple |
| GET | `/swagger-ui.html` | Documentación interactiva |
| GET | `/api-docs` | OpenAPI JSON |

## ✅ Checklist de verificación:

- [ ] Docker PostgreSQL corriendo
- [ ] Maven descargó todas las dependencias
- [ ] No hay errores de compilación
- [ ] Aplicación inicia sin errores
- [ ] `/api/v1/health` responde correctamente
- [ ] Swagger UI accesible

---

**Si todo funciona, estás listo para empezar a desarrollar! 🎉**
