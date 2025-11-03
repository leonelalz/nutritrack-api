# API REST Reference Completa - NutriTrack 📡

**Versión:** 1.0  
**Base URL:** `http://localhost:8080/api/v1`  
**Autenticación:** Bearer Token (JWT)  
**Swagger UI:** http://localhost:8080/api/v1/swagger-ui/index.html

## 📋 Tabla de Contenidos

- [Autenticación](#autenticación)
- [Gestión de Perfil](#gestión-de-perfil)
- [Biblioteca de Contenido](#biblioteca-de-contenido)
  - [Etiquetas](#etiquetas)
  - [Ingredientes](#ingredientes)
  - [Ejercicios](#ejercicios)
  - [Comidas](#comidas)
- [Planes Nutricionales](#planes-nutricionales)
- [Rutinas de Ejercicio](#rutinas-de-ejercicio)
- [Seguimiento](#seguimiento)
  - [Registro de Comidas](#registro-de-comidas)
  - [Registro de Ejercicios](#registro-de-ejercicios)
- [Códigos de Estado](#códigos-de-estado)

---

## 🔐 Autenticación

### Registro de Usuario

```http
POST /api/v1/auth/register
Content-Type: application/json
```

**Request Body:**
```json
{
  "email": "usuario@example.com",
  "password": "Password123!",
  "nombre": "Juan",
  "apellido": "Pérez"
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "email": "usuario@example.com",
  "nombre": "Juan",
  "apellido": "Pérez",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Validaciones:**
- Email debe ser válido y único
- Password mínimo 8 caracteres
- Nombre y apellido obligatorios

---

### Iniciar Sesión

```http
POST /api/v1/auth/login
Content-Type: application/json
```

**Request Body:**
```json
{
  "email": "usuario@example.com",
  "password": "Password123!"
}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "email": "usuario@example.com",
  "nombre": "Juan",
  "apellido": "Pérez",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "rol": "ROLE_USER"
}
```

**Credenciales Admin por defecto:**
- Email: `admin@fintech.com`
- Password: `admin123`

---

## 👤 Gestión de Perfil

### Obtener Mi Perfil

```http
GET /api/v1/app/profile
Authorization: Bearer {token}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "nombre": "Juan",
  "apellido": "Pérez",
  "email": "usuario@example.com",
  "unidadesMedida": "kg",
  "fechaInicioApp": "2025-11-01",
  "perfilSalud": {
    "id": 1,
    "peso": 75.5,
    "altura": 175.0,
    "edad": 30,
    "genero": "M",
    "nivelActividad": "moderado"
  }
}
```

---

### Actualizar Perfil

```http
PUT /api/v1/app/profile
Authorization: Bearer {token}
Content-Type: application/json
```

**Request Body:**
```json
{
  "nombre": "Juan Carlos",
  "apellido": "Pérez García",
  "unidadesMedida": "lbs"
}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "nombre": "Juan Carlos",
  "apellido": "Pérez García",
  "unidadesMedida": "lbs",
  "message": "Perfil actualizado exitosamente"
}
```

---

### Eliminar Cuenta (Soft Delete)

```http
DELETE /api/v1/app/profile
Authorization: Bearer {token}
```

**Response (204 No Content)**

La cuenta se marca como inactiva pero no se elimina de la base de datos.

---

## 📚 Biblioteca de Contenido

### Etiquetas

#### Listar Etiquetas

```http
GET /api/v1/etiquetas
Authorization: Bearer {token}
```

**Query Parameters:**
- `tipo` (opcional): Filtrar por tipo (alergia, objetivo, dieta, etc.)

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "nombre": "Vegano",
    "tipoEtiqueta": "dieta"
  },
  {
    "id": 2,
    "nombre": "Sin Gluten",
    "tipoEtiqueta": "alergia"
  }
]
```

---

#### Crear Etiqueta (Admin)

```http
POST /api/v1/etiquetas
Authorization: Bearer {adminToken}
Content-Type: application/json
```

**Request Body:**
```json
{
  "nombre": "Keto",
  "tipoEtiqueta": "dieta"
}
```

**Tipos válidos:**
- `alergia`
- `objetivo`
- `dieta`
- `condicion`
- `dificultad`
- `tipo_ejercicio`

**Response (201 Created):**
```json
{
  "id": 10,
  "nombre": "Keto",
  "tipoEtiqueta": "dieta"
}
```

---

### Ingredientes

#### Listar Ingredientes

```http
GET /api/v1/admin/ingredientes
Authorization: Bearer {token}
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "nombre": "Pollo",
    "calorias": 165,
    "proteinas": 31.0,
    "carbohidratos": 0.0,
    "grasas": 3.6,
    "etiquetas": [
      {
        "id": 5,
        "nombre": "Alto en Proteína"
      }
    ]
  }
]
```

---

#### Crear Ingrediente (Admin)

```http
POST /api/v1/admin/ingredientes
Authorization: Bearer {adminToken}
Content-Type: application/json
```

**Request Body:**
```json
{
  "nombre": "Quinoa",
  "calorias": 120,
  "proteinas": 4.4,
  "carbohidratos": 21.3,
  "grasas": 1.9,
  "etiquetaIds": [1, 2]
}
```

**Response (201 Created):**
```json
{
  "id": 25,
  "nombre": "Quinoa",
  "calorias": 120,
  "proteinas": 4.4,
  "carbohidratos": 21.3,
  "grasas": 1.9
}
```

---

#### Actualizar Ingrediente (Admin)

```http
PUT /api/v1/admin/ingredientes/{id}
Authorization: Bearer {adminToken}
Content-Type: application/json
```

**Request Body:**
```json
{
  "nombre": "Quinoa Orgánica",
  "calorias": 125
}
```

---

#### Eliminar Ingrediente (Admin)

```http
DELETE /api/v1/admin/ingredientes/{id}
Authorization: Bearer {adminToken}
```

**Response (204 No Content)**

---

### Ejercicios

#### Listar Ejercicios

```http
GET /api/v1/admin/ejercicios
Authorization: Bearer {token}
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "nombre": "Sentadillas",
    "descripcion": "Ejercicio para piernas",
    "caloriasPorMinuto": 8.5,
    "etiquetas": [
      {
        "id": 10,
        "nombre": "Fuerza"
      },
      {
        "id": 11,
        "nombre": "Piernas"
      }
    ]
  }
]
```

---

#### Crear Ejercicio (Admin)

```http
POST /api/v1/admin/ejercicios
Authorization: Bearer {adminToken}
Content-Type: application/json
```

**Request Body:**
```json
{
  "nombre": "Burpees",
  "descripcion": "Ejercicio de cuerpo completo",
  "caloriasPorMinuto": 10.0,
  "etiquetaIds": [10, 12]
}
```

**Response (201 Created):**
```json
{
  "id": 15,
  "nombre": "Burpees",
  "descripcion": "Ejercicio de cuerpo completo",
  "caloriasPorMinuto": 10.0
}
```

---

### Comidas

#### Listar Comidas

```http
GET /api/v1/admin/comidas
Authorization: Bearer {token}
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "nombre": "Ensalada César",
    "descripcion": "Ensalada clásica",
    "tipoComida": "almuerzo",
    "calorias": 350,
    "etiquetas": [
      {
        "id": 2,
        "nombre": "Vegetariano"
      }
    ],
    "ingredientes": [
      {
        "ingredienteId": 5,
        "nombre": "Lechuga",
        "cantidad": 100.0
      },
      {
        "ingredienteId": 8,
        "nombre": "Pollo",
        "cantidad": 150.0
      }
    ]
  }
]
```

---

#### Crear Comida (Admin)

```http
POST /api/v1/admin/comidas
Authorization: Bearer {adminToken}
Content-Type: application/json
```

**Request Body:**
```json
{
  "nombre": "Smoothie Verde",
  "descripcion": "Batido nutritivo",
  "tipoComida": "desayuno",
  "etiquetaIds": [1, 3],
  "ingredientes": [
    {
      "ingredienteId": 10,
      "cantidad": 100.0
    },
    {
      "ingredienteId": 15,
      "cantidad": 50.0
    }
  ]
}
```

**Response (201 Created):**
```json
{
  "id": 20,
  "nombre": "Smoothie Verde",
  "descripcion": "Batido nutritivo",
  "tipoComida": "desayuno",
  "calorias": 180
}
```

---

## 🎯 Planes Nutricionales

### Listar Planes (Admin)

```http
GET /api/v1/admin/planes
Authorization: Bearer {adminToken}
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "nombre": "Plan Keto 30 días",
    "descripcion": "Plan cetogénico para 30 días",
    "duracionDias": 30,
    "etiquetas": [
      {
        "id": 5,
        "nombre": "Keto"
      }
    ]
  }
]
```

---

### Crear Plan (Admin)

```http
POST /api/v1/admin/planes
Authorization: Bearer {adminToken}
Content-Type: application/json
```

**Request Body:**
```json
{
  "nombre": "Plan Vegano 21 días",
  "descripcion": "Plan vegano balanceado",
  "duracionDias": 21,
  "etiquetaIds": [1, 2]
}
```

**Response (201 Created):**
```json
{
  "id": 5,
  "nombre": "Plan Vegano 21 días",
  "descripcion": "Plan vegano balanceado",
  "duracionDias": 21
}
```

---

### Mis Planes (Usuario)

```http
GET /api/v1/usuario-planes
Authorization: Bearer {token}
```

**Response (200 OK):**
```json
[
  {
    "id": 10,
    "plan": {
      "id": 1,
      "nombre": "Plan Keto 30 días"
    },
    "fechaInicio": "2025-11-01",
    "fechaFin": null,
    "estado": "activo",
    "progreso": 45.5
  }
]
```

---

### Asignar Plan a Usuario

```http
POST /api/v1/usuario-planes/{planId}/asignar
Authorization: Bearer {token}
```

**Response (201 Created):**
```json
{
  "id": 12,
  "planId": 1,
  "estado": "activo",
  "fechaInicio": "2025-11-02",
  "message": "Plan asignado exitosamente"
}
```

---

### Cambiar Estado del Plan

```http
PUT /api/v1/usuario-planes/{id}/estado
Authorization: Bearer {token}
Content-Type: application/json
```

**Request Body:**
```json
{
  "estado": "pausado"
}
```

**Estados válidos:**
- `activo`
- `pausado`
- `completado`
- `cancelado`

**Response (200 OK):**
```json
{
  "id": 12,
  "estado": "pausado",
  "message": "Estado actualizado"
}
```

---

## 🏋️ Rutinas de Ejercicio

### Listar Rutinas (Admin)

```http
GET /api/v1/admin/rutinas
Authorization: Bearer {adminToken}
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "nombre": "Rutina Fullbody",
    "descripcion": "Rutina de cuerpo completo",
    "duracionDias": 14,
    "etiquetas": [
      {
        "id": 10,
        "nombre": "Fuerza"
      }
    ],
    "ejercicios": [
      {
        "ejercicioId": 1,
        "nombre": "Sentadillas",
        "series": 4,
        "repeticiones": 12,
        "duracionMinutos": null
      }
    ]
  }
]
```

---

### Crear Rutina (Admin)

```http
POST /api/v1/admin/rutinas
Authorization: Bearer {adminToken}
Content-Type: application/json
```

**Request Body:**
```json
{
  "nombre": "Rutina HIIT Intenso",
  "descripcion": "Alta intensidad intervalos",
  "duracionDias": 7,
  "etiquetaIds": [10, 11],
  "ejercicios": [
    {
      "ejercicioId": 5,
      "series": 3,
      "repeticiones": 20,
      "duracionMinutos": null
    },
    {
      "ejercicioId": 8,
      "series": null,
      "repeticiones": null,
      "duracionMinutos": 15
    }
  ]
}
```

**Response (201 Created):**
```json
{
  "id": 8,
  "nombre": "Rutina HIIT Intenso",
  "descripcion": "Alta intensidad intervalos",
  "duracionDias": 7
}
```

---

### Mis Rutinas (Usuario)

```http
GET /api/v1/usuario-rutinas
Authorization: Bearer {token}
```

**Response (200 OK):**
```json
[
  {
    "id": 15,
    "rutina": {
      "id": 1,
      "nombre": "Rutina Fullbody"
    },
    "fechaInicio": "2025-11-01",
    "fechaFin": null,
    "estado": "activo",
    "progreso": 60.0
  }
]
```

---

### Asignar Rutina a Usuario

```http
POST /api/v1/usuario-rutinas/{rutinaId}/asignar
Authorization: Bearer {token}
```

**Response (201 Created):**
```json
{
  "id": 18,
  "rutinaId": 1,
  "estado": "activo",
  "fechaInicio": "2025-11-02",
  "message": "Rutina asignada exitosamente"
}
```

---

## 📊 Seguimiento

### Registro de Comidas

#### Registrar Comida Consumida

```http
POST /api/v1/registro-comidas
Authorization: Bearer {token}
Content-Type: application/json
```

**Request Body:**
```json
{
  "comidaId": 5,
  "fecha": "2025-11-02",
  "cantidad": 1.0,
  "notas": "Desayuno completo"
}
```

**Response (201 Created):**
```json
{
  "id": 100,
  "comida": {
    "id": 5,
    "nombre": "Smoothie Verde"
  },
  "fecha": "2025-11-02",
  "cantidad": 1.0,
  "caloriasConsumidas": 180,
  "notas": "Desayuno completo"
}
```

---

#### Obtener Mi Registro de Comidas

```http
GET /api/v1/registro-comidas
Authorization: Bearer {token}
```

**Query Parameters:**
- `fechaInicio` (opcional): Fecha inicio filtro (formato: YYYY-MM-DD)
- `fechaFin` (opcional): Fecha fin filtro

**Response (200 OK):**
```json
[
  {
    "id": 100,
    "comida": {
      "id": 5,
      "nombre": "Smoothie Verde"
    },
    "fecha": "2025-11-02",
    "cantidad": 1.0,
    "caloriasConsumidas": 180,
    "notas": "Desayuno completo"
  },
  {
    "id": 99,
    "comida": {
      "id": 8,
      "nombre": "Ensalada César"
    },
    "fecha": "2025-11-01",
    "cantidad": 1.5,
    "caloriasConsumidas": 525
  }
]
```

---

#### Historial de Comidas de Usuario (Admin)

```http
GET /api/v1/registro-comidas/usuario/{usuarioId}
Authorization: Bearer {adminToken}
```

**Response (200 OK):** Similar al endpoint anterior

---

### Registro de Ejercicios

#### Registrar Ejercicio Realizado

```http
POST /api/v1/registro-ejercicios
Authorization: Bearer {token}
Content-Type: application/json
```

**Request Body:**
```json
{
  "ejercicioId": 1,
  "fecha": "2025-11-02",
  "duracionMinutos": 30,
  "series": 4,
  "repeticiones": 12,
  "notas": "Buen rendimiento"
}
```

**Response (201 Created):**
```json
{
  "id": 200,
  "ejercicio": {
    "id": 1,
    "nombre": "Sentadillas"
  },
  "fecha": "2025-11-02",
  "duracionMinutos": 30,
  "series": 4,
  "repeticiones": 12,
  "caloriasQuemadas": 255,
  "notas": "Buen rendimiento"
}
```

---

#### Obtener Mi Registro de Ejercicios

```http
GET /api/v1/registro-ejercicios
Authorization: Bearer {token}
```

**Query Parameters:**
- `fechaInicio` (opcional): Fecha inicio filtro
- `fechaFin` (opcional): Fecha fin filtro

**Response (200 OK):**
```json
[
  {
    "id": 200,
    "ejercicio": {
      "id": 1,
      "nombre": "Sentadillas"
    },
    "fecha": "2025-11-02",
    "duracionMinutos": 30,
    "series": 4,
    "repeticiones": 12,
    "caloriasQuemadas": 255,
    "notas": "Buen rendimiento"
  }
]
```

---

#### Historial de Ejercicios de Usuario (Admin)

```http
GET /api/v1/registro-ejercicios/usuario/{usuarioId}
Authorization: Bearer {adminToken}
```

**Response (200 OK):** Similar al endpoint anterior

---

## 📋 Códigos de Estado HTTP

| Código | Significado | Uso |
|--------|-------------|-----|
| **2xx - Éxito** | | |
| 200 | OK | Operación exitosa (GET, PUT, DELETE) |
| 201 | Created | Recurso creado exitosamente (POST) |
| 204 | No Content | Eliminación exitosa sin contenido |
| **4xx - Error del Cliente** | | |
| 400 | Bad Request | Datos inválidos o faltantes |
| 401 | Unauthorized | Token ausente, inválido o expirado |
| 403 | Forbidden | Sin permisos suficientes |
| 404 | Not Found | Recurso no encontrado |
| 409 | Conflict | Conflicto (ej: email duplicado) |
| **5xx - Error del Servidor** | | |
| 500 | Internal Server Error | Error inesperado del servidor |

---

## 🔑 Autenticación

### Headers Requeridos

**Para endpoints públicos:**
```http
Content-Type: application/json
```

**Para endpoints protegidos:**
```http
Authorization: Bearer {jwt_token}
Content-Type: application/json
```

### Obtener Token

1. Hacer login en `/api/v1/auth/login`
2. Copiar el `token` de la respuesta
3. Incluir en header: `Authorization: Bearer {token}`

### Expiración de Token

Los tokens JWT expiran después de 24 horas (86400000 ms). Cuando expire, deberás hacer login nuevamente.

---

## 📝 Formato de Errores

Todos los errores siguen este formato:

```json
{
  "timestamp": "2025-11-02T15:30:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "El formato de email es inválido",
  "path": "/api/v1/auth/register"
}
```

**Errores de Validación:**
```json
{
  "timestamp": "2025-11-02T15:30:00Z",
  "status": 400,
  "error": "Validation Failed",
  "errors": {
    "email": "Email es requerido",
    "password": "La contraseña debe tener al menos 8 caracteres"
  },
  "path": "/api/v1/auth/register"
}
```

---

## 🧪 Testing

### Colecciones Postman Disponibles

- `NutriTrack_API_Complete.postman_collection.json` - Colección completa
- `Modulo2_BibliotecaContenido.postman_collection.json`
- `Modulo3_PlanesNutricionales.postman_collection.json`
- `Modulo4_RutinasEjercicio.postman_collection.json`
- `Modulo5_SeguimientoAsignaciones.postman_collection.json`

Ubicación: `postman/` en el repositorio

### Ejecutar Tests Unitarios

```bash
# Todos los tests
./mvnw test

# Tests específicos
./mvnw test -Dtest=AuthServiceTest
./mvnw test -Dtest=ComidaServiceTest
```

**Total:** 202 tests unitarios ✅

---

## 🔗 Enlaces Útiles

- **Swagger UI:** http://localhost:8080/api/v1/swagger-ui/index.html
- **GitHub:** https://github.com/leonelalz/nutritrack-api
- **Documentación:** [README.md](../README.md)

---

**Última actualización:** Noviembre 2025  
**Versión del API:** 1.0  
**Mantenido por:** Equipo NutriTrack

