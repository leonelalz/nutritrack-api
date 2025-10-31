# Environments de Postman

Esta carpeta contiene los environments para diferentes entornos de ejecución.

## 🌍 Environments Disponibles

### 1. Local.postman_environment.json
- **Base URL:** `http://localhost:8080/api/v1`
- **Uso:** Desarrollo local en tu máquina
- **DB:** MySQL/PostgreSQL local

### 2. Development.postman_environment.json
- **Base URL:** `https://dev-api.nutritrack.com/api/v1`
- **Uso:** Servidor de desarrollo compartido
- **DB:** MySQL Dev (compartida)

### 3. Staging.postman_environment.json
- **Base URL:** `https://staging-api.nutritrack.com/api/v1`
- **Uso:** Pre-producción para QA
- **DB:** MySQL Staging (réplica de producción)

### 4. Production.postman_environment.json
- **Base URL:** `https://api.nutritrack.com/api/v1`
- **Uso:** Ambiente de producción
- **DB:** MySQL Production (⚠️ datos reales)

## 🔑 Variables Comunes

Todos los environments deben tener estas variables:

```json
{
  "key": "baseUrl",
  "value": "http://localhost:8080/api/v1",
  "enabled": true
},
{
  "key": "authToken",
  "value": "",
  "enabled": true
},
{
  "key": "userId",
  "value": "",
  "enabled": true
},
{
  "key": "profileId",
  "value": "",
  "enabled": true
},
{
  "key": "adminToken",
  "value": "",
  "enabled": true
}
```

## 🔄 Cómo Usar

### Importar Environment

1. Abrir Postman
2. Click en "Environments" (sidebar izquierdo)
3. Click en "Import"
4. Seleccionar archivo .json
5. El environment aparecerá en la lista

### Cambiar de Environment

1. Click en el dropdown en la esquina superior derecha
2. Seleccionar el environment deseado (ej: "Local")
3. Los requests usarán las variables de ese environment

### Exportar Environment

1. Click en "Environments"
2. Hover sobre el environment
3. Click en los 3 puntos → "Export"
4. Guardar en esta carpeta (sobrescribir)
5. Commitear los cambios

## ⚠️ Seguridad

### ❌ NO Commitear

- Tokens de producción
- Contraseñas reales
- Información sensible de usuarios

### ✅ Usar Valores Placeholder

```json
{
  "key": "adminToken",
  "value": "REPLACE_WITH_YOUR_TOKEN",
  "enabled": true
}
```

### 🔐 Valores Sensibles

Para Production environment:
1. NO incluir valores reales en el archivo commiteado
2. Cada desarrollador debe configurar localmente sus tokens
3. Usar variables de Postman Cloud (opcional)

## 📝 Configuración Inicial

### Local Environment

```bash
# 1. Iniciar servidor local
./mvnw spring-boot:run

# 2. En Postman
# - Importar Local.postman_environment.json
# - Seleccionar "Local"
# - Ejecutar "POST Register" o "POST Login"
# - El token se guardará automáticamente
```

### Development Environment

```bash
# Configurar variables manualmente:
baseUrl: https://dev-api.nutritrack.com/api/v1

# Obtener token:
# 1. Ejecutar POST /auth/login con credenciales de dev
# 2. Copiar token de la respuesta
# 3. Pegarlo en la variable authToken
```

## 🧪 Testing por Environment

### Local
```bash
# Ejecutar todas las colecciones
newman run collection.json -e Local.postman_environment.json
```

### Development
```bash
# Solo smoke tests
newman run collection.json -e Development.postman_environment.json --folder "Smoke Tests"
```

## 📊 Variables por Módulo

Algunas colecciones guardan variables adicionales:

| Variable | Guardada en | Descripción |
|----------|-------------|-------------|
| `lastTagId` | Módulo 2 | ID de última etiqueta creada |
| `lastIngredientId` | Módulo 2 | ID de último ingrediente |
| `currentGoalId` | Módulo 3 | ID de meta actual |
| `currentActivityId` | Módulo 3 | ID de actividad actual |
| `myPlanId` | Módulo 4 | ID de plan asignado |

Estas se setean automáticamente en los scripts de Tests.

---

**Para configurar environments desde cero, ver:** [POSTMAN_GUIDE.md](../POSTMAN_GUIDE.md#environments)
