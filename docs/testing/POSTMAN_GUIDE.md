# Guía de Testing con Postman 🧪

**Versión:** 1.0  
**Última actualización:** Octubre 2025

## 📋 Tabla de Contenidos

- [Configuración Inicial](#configuración-inicial)
- [Estructura de Colecciones](#estructura-de-colecciones)
- [Environments](#environments)
- [Variables Globales](#variables-globales)
- [Colecciones por Módulo](#colecciones-por-módulo)
- [Scripts de Automatización](#scripts-de-automatización)
- [Importar/Exportar](#importarexportar)

## ⚙️ Configuración Inicial

### Instalación

1. Descargar [Postman](https://www.postman.com/downloads/)
2. Crear una cuenta o iniciar sesión
3. Crear un Workspace llamado "NutriTrack API"

### Importar Colecciones

Los archivos de colección están en: `postman/collections/`

1. Click en "Import" en Postman
2. Seleccionar archivo JSON de la colección
3. Repetir para cada módulo

## 🗂️ Estructura de Colecciones

Organizamos las pruebas en **5 colecciones principales** alineadas con los módulos del proyecto:

```
📁 NutriTrack API Testing/
├── 📂 Collection 1: Gestión de Cuentas y Preferencias
│   ├── 📁 Auth (US-01, US-02)
│   │   ├── POST Register (US-01)
│   │   └── POST Login (US-02)
│   ├── 📁 Profile (US-03, US-04)
│   │   ├── GET My Profile
│   │   └── PUT Update Profile
│   └── 📁 Account (US-05)
│       └── DELETE Account
│
├── 📂 Collection 2: Biblioteca de Contenido (Admin)
│   ├── 📁 Tags (US-06)
│   │   ├── POST Create Tag
│   │   ├── GET All Tags
│   │   └── DELETE Tag
│   ├── 📁 Ingredients (US-07)
│   │   ├── POST Create Ingredient
│   │   └── DELETE Ingredient
│   ├── 📁 Exercises (US-08)
│   │   └── POST Create Exercise
│   └── 📁 Meals (US-09, US-10)
│       └── POST Create Meal with Recipe
│
├── 📂 Collection 3: Gestor de Catálogo (Admin)
│   ├── 📁 Catalog Goals (US-11, US-13, US-14)
│   │   ├── POST Create Goal
│   │   ├── DELETE Goal
│   │   └── POST Assign Tag to Goal
│   ├── 📁 Activities (US-12)
│   │   └── POST Create Activity
│   └── 📁 Routines (US-15)
│       └── POST Assemble Routine
│
├── 📂 Collection 4: Exploración y Activación (Cliente)
│   ├── 📁 Catalog (US-16, US-17)
│   │   ├── GET View Catalog
│   │   └── GET Goal Detail
│   └── 📁 My Plan (US-18, US-19, US-20)
│       ├── POST Activate Plan
│       └── PUT Update Plan Status
│
└── 📂 Collection 5: Seguimiento de Progreso (Cliente)
    ├── 📁 Plan Activities (US-21, US-22, US-23)
    │   ├── GET View Activities
    │   ├── POST Mark Complete
    │   └── DELETE Unmark
    ├── 📁 Measurements (US-24)
    │   ├── POST Register Measurement
    │   └── GET View Measurements
    └── 📁 Reports (US-25)
        ├── GET Progress Chart Data
        └── GET Download PDF
```

## 🌍 Environments

Crear **4 environments** para diferentes entornos:

### 1. Local Environment

```json
{
  "name": "NutriTrack - Local",
  "values": [
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
    }
  ]
}
```

### 2. Development Environment

```json
{
  "name": "NutriTrack - Dev",
  "values": [
    {
      "key": "baseUrl",
      "value": "https://dev-api.nutritrack.com/api/v1",
      "enabled": true
    },
    {
      "key": "authToken",
      "value": "",
      "enabled": true
    }
  ]
}
```

### 3. Staging Environment

```json
{
  "name": "NutriTrack - Staging",
  "values": [
    {
      "key": "baseUrl",
      "value": "https://staging-api.nutritrack.com/api/v1",
      "enabled": true
    },
    {
      "key": "authToken",
      "value": "",
      "enabled": true
    }
  ]
}
```

### 4. Production Environment

```json
{
  "name": "NutriTrack - Production",
  "values": [
    {
      "key": "baseUrl",
      "value": "https://api.nutritrack.com/api/v1",
      "enabled": true
    },
    {
      "key": "authToken",
      "value": "",
      "enabled": true
    }
  ]
}
```

## 🔑 Variables Globales

Variables que se usan en múltiples colecciones:

| Variable | Descripción | Ejemplo |
|----------|-------------|---------|
| `{{baseUrl}}` | URL base del API | `http://localhost:8080/api/v1` |
| `{{authToken}}` | JWT token de autenticación | `eyJhbGci...` |
| `{{userId}}` | ID del usuario autenticado | `a1b2c3d4-e5f6-...` |
| `{{profileId}}` | ID del perfil de usuario | `f6e5d4c3-b2a1-...` |
| `{{adminToken}}` | Token de usuario Admin | `eyJhbGci...` |

## 📦 Colecciones por Módulo

### Collection 1: Gestión de Cuentas y Preferencias

#### 1. POST Register (US-01)
**Endpoint:** `{{baseUrl}}/auth/register`

**Request:**
```json
{
  "email": "leonel@test.com",
  "password": "password123!",
  "nombre": "Leonel"
}
```

**Tests:**
```javascript
// Guardar token y IDs
pm.test("Status 201 Created", function() {
    pm.response.to.have.status(201);
});

pm.test("Response has userId and token", function() {
    var jsonData = pm.response.json();
    pm.expect(jsonData).to.have.property('userId');
    pm.expect(jsonData).to.have.property('token');
    
    // Guardar en environment
    pm.environment.set("authToken", jsonData.token);
    pm.environment.set("userId", jsonData.userId);
    pm.environment.set("profileId", jsonData.profileId);
});
```

**Casos de Error:**
- Duplicate Email (409)
- Invalid Password (400)
- Invalid Email Format (400)

---

#### 2. POST Login (US-02)
**Endpoint:** `{{baseUrl}}/auth/login`

**Headers:**
```
Content-Type: application/json
```

**Request:**
```json
{
  "email": "leonel@test.com",
  "password": "password123!"
}
```

**Tests:**
```javascript
pm.test("Login successful", function() {
    pm.response.to.have.status(200);
    var jsonData = pm.response.json();
    pm.expect(jsonData).to.have.property('token');
    
    // Actualizar token
    pm.environment.set("authToken", jsonData.token);
    pm.environment.set("userId", jsonData.userId);
});
```

---

#### 3. GET My Profile (US-04)
**Endpoint:** `{{baseUrl}}/app/profile`

**Headers:**
```
Authorization: Bearer {{authToken}}
```

**Tests:**
```javascript
pm.test("Profile retrieved", function() {
    pm.response.to.have.status(200);
    var jsonData = pm.response.json();
    pm.expect(jsonData).to.have.property('profileId');
    pm.expect(jsonData).to.have.property('nombre');
});
```

---

#### 4. PUT Update Profile (US-03, US-04)
**Endpoint:** `{{baseUrl}}/app/profile`

**Headers:**
```
Authorization: Bearer {{authToken}}
Content-Type: application/json
```

**Request:**
```json
{
  "nombre": "Leonel",
  "unidades": "lbs",
  "perfil_salud": {
    "objetivo_tag_id": 6,
    "alergia_tag_ids": [10]
  }
}
```

---

#### 5. DELETE Account (US-05)
**Endpoint:** `{{baseUrl}}/app/account`

**Headers:**
```
Authorization: Bearer {{authToken}}
```

**Tests:**
```javascript
pm.test("Account deleted", function() {
    pm.response.to.have.status(200);
    
    // Limpiar variables
    pm.environment.unset("authToken");
    pm.environment.unset("userId");
});
```

---

### Collection 2: Biblioteca de Contenido (Admin)

#### 6. POST Create Tag (US-06)
**Endpoint:** `{{baseUrl}}/admin/tags`

**Headers:**
```
Authorization: Bearer {{adminToken}}
Content-Type: application/json
```

**Request:**
```json
{
  "nombre": "Nueces",
  "tipo_etiqueta": "alergia"
}
```

**Tests:**
```javascript
pm.test("Tag created", function() {
    pm.response.to.have.status(201);
    var jsonData = pm.response.json();
    pm.environment.set("lastTagId", jsonData.id);
});
```

---

#### 7. GET All Tags (US-06)
**Endpoint:** `{{baseUrl}}/admin/tags?tipo=alergia`

**Headers:**
```
Authorization: Bearer {{adminToken}}
```

---

#### 8. DELETE Tag (US-06)
**Endpoint:** `{{baseUrl}}/admin/tags/{{lastTagId}}`

**Headers:**
```
Authorization: Bearer {{adminToken}}
```

---

#### 9. POST Create Ingredient (US-07)
**Endpoint:** `{{baseUrl}}/admin/ingredients`

**Request:**
```json
{
  "nombre": "Maní",
  "tag_ids": [1]
}
```

**Tests:**
```javascript
pm.test("Ingredient created", function() {
    pm.response.to.have.status(201);
    var jsonData = pm.response.json();
    pm.environment.set("lastIngredientId", jsonData.id);
});
```

---

#### 10. POST Create Exercise (US-08)
**Endpoint:** `{{baseUrl}}/admin/exercises`

**Request:**
```json
{
  "nombre": "Burpee",
  "tag_ids": [20, 30]
}
```

---

#### 11. POST Create Meal (US-09, US-10)
**Endpoint:** `{{baseUrl}}/admin/meals`

**Request:**
```json
{
  "nombre": "Batido Vegano",
  "tag_ids": [40],
  "receta": [
    {
      "ingredient_id": 12,
      "cantidad_ingrediente": "150.5"
    }
  ]
}
```

---

### Collection 3: Gestor de Catálogo (Admin)

#### 12. POST Create Goal (US-11)
**Endpoint:** `{{baseUrl}}/admin/catalog/goals`

**Request:**
```json
{
  "nombre": "Perder 10kg en 8 semanas",
  "descripcion": "Un plan intenso..."
}
```

**Tests:**
```javascript
pm.test("Goal created", function() {
    pm.response.to.have.status(201);
    var jsonData = pm.response.json();
    pm.environment.set("currentGoalId", jsonData.id);
});
```

---

#### 13. POST Assign Tag to Goal (US-12)
**Endpoint:** `{{baseUrl}}/admin/catalog/goals/{{currentGoalId}}/tags`

**Request:**
```json
{
  "tag_id": 5
}
```

---

#### 14. POST Create Activity (US-12)
**Endpoint:** `{{baseUrl}}/admin/catalog/goals/{{currentGoalId}}/activities`

**Request:**
```json
{
  "nombre": "Día 1: Pecho",
  "tipo_actividad": "Ejercicio"
}
```

**Tests:**
```javascript
pm.test("Activity created", function() {
    pm.response.to.have.status(201);
    var jsonData = pm.response.json();
    pm.environment.set("currentActivityId", jsonData.id);
});
```

---

#### 15. POST Assemble Routine (US-15)
**Endpoint:** `{{baseUrl}}/admin/catalog/activities/{{currentActivityId}}/routine`

**Request:**
```json
{
  "exercise_id": 15,
  "series": 4,
  "repeticiones": 10
}
```

---

#### 16. DELETE Goal (US-14)
**Endpoint:** `{{baseUrl}}/admin/catalog/goals/{{currentGoalId}}`

---

### Collection 4: Exploración y Activación (Cliente)

#### 17. GET View Catalog (US-16)
**Endpoint:** `{{baseUrl}}/app/catalog/goals`

**Headers:**
```
Authorization: Bearer {{authToken}}
```

**Tests:**
```javascript
pm.test("Catalog retrieved", function() {
    pm.response.to.have.status(200);
    var jsonData = pm.response.json();
    pm.expect(jsonData).to.be.an('array');
});
```

---

#### 18. GET Goal Detail (US-17)
**Endpoint:** `{{baseUrl}}/app/catalog/goals/50`

**Headers:**
```
Authorization: Bearer {{authToken}}
```

---

#### 19. POST Activate Plan (US-18)
**Endpoint:** `{{baseUrl}}/app/my-plan`

**Request:**
```json
{
  "catalog_goal_id": 50
}
```

**Tests:**
```javascript
pm.test("Plan activated", function() {
    pm.response.to.have.status(201);
    var jsonData = pm.response.json();
    pm.environment.set("myPlanId", jsonData.id_asignacion);
});
```

---

#### 20. PUT Update Plan Status (US-19, US-20)
**Endpoint:** `{{baseUrl}}/app/my-plan`

**Request:**
```json
{
  "estado": "pausado"
}
```

---

### Collection 5: Seguimiento de Progreso (Cliente)

#### 21. GET View Activities (US-21)
**Endpoint:** `{{baseUrl}}/app/my-plan/activities`

**Headers:**
```
Authorization: Bearer {{authToken}}
```

---

#### 22. POST Mark Complete (US-22)
**Endpoint:** `{{baseUrl}}/app/my-plan/activities/100/complete`

**Headers:**
```
Authorization: Bearer {{authToken}}
```

---

#### 23. DELETE Unmark (US-23)
**Endpoint:** `{{baseUrl}}/app/my-plan/activities/100/complete`

---

#### 24. POST Register Measurement (US-24)
**Endpoint:** `{{baseUrl}}/app/measurements`

**Request:**
```json
{
  "peso": 69.5,
  "fecha": "2025-10-31"
}
```

---

#### 25. GET View Measurements (US-24)
**Endpoint:** `{{baseUrl}}/app/measurements`

---

#### 26. GET Progress Chart Data (US-25)
**Endpoint:** `{{baseUrl}}/app/reports/progress-chart`

---

#### 27. GET Download PDF (US-25)
**Endpoint:** `{{baseUrl}}/app/reports/download-pdf`

**Tests:**
```javascript
pm.test("PDF downloaded", function() {
    pm.response.to.have.status(200);
    pm.expect(pm.response.headers.get('Content-Type')).to.include('application/pdf');
});
```

---

## 🤖 Scripts de Automatización

### Pre-request Script Global

Agregar en Collection Settings → Pre-request Scripts:

```javascript
// Auto-refresh token si está cerca de expirar
const tokenExpiry = pm.environment.get("tokenExpiry");
const now = new Date().getTime();

if (tokenExpiry && now > tokenExpiry - 300000) { // 5 min antes
    console.log("Token expiring soon, refreshing...");
    // Lógica de refresh
}
```

### Test Script Global

Agregar en Collection Settings → Tests:

```javascript
// Log automático de errores
if (pm.response.code >= 400) {
    console.error("Request failed:", {
        url: pm.request.url,
        status: pm.response.code,
        body: pm.response.json()
    });
}

// Validar estructura de error
if (pm.response.code >= 400) {
    pm.test("Error response has message", function() {
        var jsonData = pm.response.json();
        pm.expect(jsonData).to.have.property('message');
    });
}
```

## 📥 Importar/Exportar

### Exportar Colección

1. Click derecho en colección
2. "Export"
3. Seleccionar "Collection v2.1"
4. Guardar en `docs/testing/postman/collections/`

### Exportar Environment

1. Click en el ícono de ojo (Environment quick look)
2. Click en "..." al lado del environment
3. "Export"
4. Guardar en `docs/testing/postman/environments/`

### Compartir con el Equipo

```bash
# Los archivos están en:
docs/testing/postman/
├── collections/
│   ├── Module_1_Cuentas_Preferencias.postman_collection.json
│   ├── Module_2_Biblioteca_Contenido.postman_collection.json
│   ├── Module_3_Gestor_Catalogo.postman_collection.json
│   ├── Module_4_Exploracion_Activacion.postman_collection.json
│   └── Module_5_Seguimiento_Progreso.postman_collection.json
└── environments/
    ├── Local.postman_environment.json
    ├── Development.postman_environment.json
    ├── Staging.postman_environment.json
    └── Production.postman_environment.json
```

## ✅ Checklist de Testing

### Antes de cada Sprint

- [ ] Importar colección del módulo a trabajar
- [ ] Configurar environment (Local/Dev)
- [ ] Ejecutar tests de módulos dependientes
- [ ] Verificar que todos los tests pasen

### Durante Desarrollo

- [ ] Crear request para cada endpoint nuevo
- [ ] Agregar tests de validación
- [ ] Probar casos de error (400, 401, 404, 409)
- [ ] Actualizar variables de environment

### Antes de PR

- [ ] Ejecutar Collection Runner en todos los módulos
- [ ] 100% de tests pasando
- [ ] Exportar colección actualizada
- [ ] Commitear archivos JSON actualizados

## 🔍 Troubleshooting

### Token Expirado (401)

```javascript
// En Pre-request Script
pm.sendRequest({
    url: pm.environment.get("baseUrl") + "/auth/login",
    method: 'POST',
    header: { 'Content-Type': 'application/json' },
    body: {
        mode: 'raw',
        raw: JSON.stringify({
            email: "test@example.com",
            password: "password123!"
        })
    }
}, function (err, res) {
    pm.environment.set("authToken", res.json().token);
});
```

### Variables No Guardadas

Verificar que estás usando `pm.environment.set()` en los Tests, no en Pre-request.

### CORS Errors

Usar Postman Desktop App, no la versión web.

---

**Próximos pasos:**
1. Importar las colecciones base
2. Configurar tu environment local
3. Ejecutar la colección "Module 1" para crear tu primera cuenta
4. Continuar con los demás módulos

**Soporte:** Reportar problemas en GitHub con etiqueta `testing`
