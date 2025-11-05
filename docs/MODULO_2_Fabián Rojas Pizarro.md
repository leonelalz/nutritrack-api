# GUÍA DE PRUEBAS - MÓDULO 2: GESTIÓN DE PERFIL Y SALUD

**Integrante:** Fabián Rojas Pizarro  
**Módulo asignado:** Módulo 2 - Gestión de Perfil de Salud y Mediciones  
**Fecha:** Noviembre 2025

---

## 📋 TABLA DE CONTENIDOS

1. [Endpoints del Módulo 2](#endpoints-del-módulo-2)
2. [Usuarios de Prueba](#usuarios-de-prueba)
3. [Proceso de Autenticación](#proceso-de-autenticación)
4. [Casos de Prueba Detallados](#casos-de-prueba-detallados)
5. [Checklist de Capturas](#checklist-de-capturas)

---

## 🎯 ENDPOINTS DEL MÓDULO 2

### Perfil de Salud

| Método | Endpoint | Descripción | Auth |
|--------|----------|-------------|------|
| **POST** | `/api/v1/perfil/salud` | Crear perfil de salud (primera vez) | ✅ |
| **PUT** | `/api/v1/perfil/salud` | Actualizar perfil de salud existente | ✅ |
| **GET** | `/api/v1/perfil/salud` | Obtener perfil de salud actual | ✅ |

### Historial de Mediciones

| Método | Endpoint | Descripción | Auth |
|--------|----------|-------------|------|
| **POST** | `/api/v1/perfil/mediciones` | Registrar nueva medición | ✅ |
| **GET** | `/api/v1/perfil/mediciones` | Obtener historial completo | ✅ |
| **GET** | `/api/v1/perfil/mediciones/{id}` | Obtener medición específica | ✅ |
| **PUT** | `/api/v1/perfil/mediciones/{id}` | Actualizar medición existente | ✅ |
| **DELETE** | `/api/v1/perfil/mediciones/{id}` | Eliminar medición | ✅ |

---

## 👥 USUARIOS DE PRUEBA

### 🔹 Usuario Demo (Usuario Regular)

```json
{
  "email": "demo@nutritrack.com",
  "password": "Demo123!"
}
```

**Datos del usuario:**
- **Rol:** ROLE_USER
- **Objetivo:** Perder peso
- **Actividad:** Moderado (3-4 días/semana)
- **Mediciones:** 11 registros (Sep 1 - Nov 4, 2025)
- **Peso inicial:** 78.0 kg
- **Peso actual:** 72.5 kg
- **Progreso:** -5.5 kg ✅
- **Altura:** 168 cm
- **IMC inicial:** 27.6 (Sobrepeso)
- **IMC actual:** 25.7 (Peso normal)

### 🔸 Usuario Admin (Administrador)

```json
{
  "email": "admin@nutritrack.com",
  "password": "Admin123!"
}
```

**Datos del usuario:**
- **Rol:** ROLE_ADMIN
- **Objetivo:** Mantener forma física
- **Actividad:** Alto (5-6 días/semana)
- **Mediciones:** 11 registros (Sep 1 - Nov 4, 2025)
- **Peso inicial:** 70.0 kg
- **Peso actual:** 70.5 kg
- **Progreso:** +0.5 kg (estable)
- **Altura:** 175 cm
- **IMC:** 23.0 (Peso normal)

---

## 🔐 PROCESO DE AUTENTICACIÓN

### Paso 1: Login

**Endpoint:** `POST /api/v1/auth/login`

**Request (Usuario Demo):**
```json
{
  "email": "demo@nutritrack.com",
  "password": "Demo123!"
}
```

**Response Esperado:**
```json
{
  "success": true,
  "message": "Login exitoso",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "email": "demo@nutritrack.com",
    "userId": 2,
    "perfilId": 2
  },
  "timestamp": "2025-11-04T20:30:00"
}
```

### Paso 2: Copiar Token

Copiar el valor del campo `token` de la respuesta.

### Paso 3: Autorizar en Swagger

1. Hacer clic en el botón **"Authorize"** 🔓 en la parte superior derecha de Swagger
2. Pegar el token en el campo de texto (NO incluir "Bearer ")
3. Hacer clic en **"Authorize"**
4. Hacer clic en **"Close"**

### Paso 4: Verificar Autorización

El botón ahora debe mostrar 🔒 indicando que estás autenticado.

---

## 📝 CASOS DE PRUEBA DETALLADOS

### CASO 1: Obtener Perfil de Salud Existente

**User Story:** US-06 - Consultar datos de salud

**Objetivo:** Verificar que el usuario puede consultar su perfil de salud actual.

#### Request

```http
GET /api/v1/perfil/salud
Authorization: Bearer {TOKEN_COPIADO}
```

#### Response Esperado (Usuario Demo)

```json
{
  "success": true,
  "message": "Perfil de salud obtenido exitosamente",
  "data": {
    "objetivoActual": "PERDER_PESO",
    "nivelActividadActual": "MODERADO",
    "fechaActualizacion": "2025-11-04"
  },
  "timestamp": "2025-11-04T20:30:00"
}
```

#### Response Esperado (Usuario Admin)

```json
{
  "success": true,
  "message": "Perfil de salud obtenido exitosamente",
  "data": {
    "objetivoActual": "MANTENER_FORMA",
    "nivelActividadActual": "ALTO",
    "fechaActualizacion": "2025-11-04"
  },
  "timestamp": "2025-11-04T20:30:00"
}
```

#### ✅ Validaciones
- [ ] HTTP Status: 200 OK
- [ ] Respuesta contiene `objetivoActual` válido
- [ ] Respuesta contiene `nivelActividadActual` válido
- [ ] Campo `success` es `true`
- [ ] Timestamp es reciente

#### 📸 Capturas Requeridas
1. Request en Swagger (mostrar header Authorization)
2. Response exitoso (body completo)
3. Código de respuesta 200

---

### CASO 2: Actualizar Perfil de Salud

**User Story:** US-04 - Editar perfil de salud

**Objetivo:** Verificar que el usuario puede actualizar su objetivo y nivel de actividad.

#### Request

```http
PUT /api/v1/perfil/salud
Authorization: Bearer {TOKEN_COPIADO}
Content-Type: application/json
```

**Body (Cambiar a Ganar Masa Muscular con actividad alta):**
```json
{
  "objetivoActual": "GANAR_MASA_MUSCULAR",
  "nivelActividadActual": "ALTO"
}
```

#### Response Esperado

```json
{
  "success": true,
  "message": "Perfil de salud actualizado exitosamente",
  "data": {
    "objetivoActual": "GANAR_MASA_MUSCULAR",
    "nivelActividadActual": "ALTO",
    "fechaActualizacion": "2025-11-04"
  },
  "timestamp": "2025-11-04T20:32:00"
}
```

#### ✅ Validaciones
- [ ] HTTP Status: 200 OK
- [ ] Los valores fueron actualizados correctamente
- [ ] `fechaActualizacion` se actualizó a la fecha actual
- [ ] El perfil se guardó en la base de datos

#### 📸 Capturas Requeridas
1. Request body en Swagger
2. Response exitoso
3. GET /perfil/salud para verificar cambios persistidos

---

### CASO 3: Obtener Historial de Mediciones

**User Story:** US-24 - Consultar historial de mediciones

**Objetivo:** Verificar que el usuario puede ver todas sus mediciones registradas.

#### Request

```http
GET /api/v1/perfil/mediciones
Authorization: Bearer {TOKEN_COPIADO}
```

#### Response Esperado (Usuario Demo - Primeras 3 mediciones)

```json
{
  "success": true,
  "message": "Historial de mediciones obtenido exitosamente",
  "data": [
    {
      "id": 12,
      "fechaMedicion": "2025-11-04",
      "peso": 72.5,
      "altura": 168,
      "imc": 25.7,
      "clasificacionIMC": "Peso normal"
    },
    {
      "id": 11,
      "fechaMedicion": "2025-11-03",
      "peso": 73.0,
      "altura": 168,
      "imc": 25.9,
      "clasificacionIMC": "Sobrepeso"
    },
    {
      "id": 10,
      "fechaMedicion": "2025-10-27",
      "peso": 74.0,
      "altura": 168,
      "imc": 26.2,
      "clasificacionIMC": "Sobrepeso"
    }
    // ... 8 mediciones más
  ],
  "timestamp": "2025-11-04T20:35:00"
}
```

#### ✅ Validaciones
- [ ] HTTP Status: 200 OK
- [ ] Respuesta contiene array de mediciones
- [ ] Usuario Demo: 11 mediciones total
- [ ] Mediciones ordenadas por fecha descendente (más reciente primero)
- [ ] Cada medición tiene: id, fechaMedicion, peso, altura, imc
- [ ] IMC calculado correctamente: peso / (altura/100)²

#### 📸 Capturas Requeridas
1. Response completo (scroll para mostrar todas las 11 mediciones)
2. Detalle de la medición más reciente
3. Detalle de la medición más antigua (Sep 1, 2025)

---

### CASO 4: Registrar Nueva Medición

**User Story:** US-24 - Registrar medición corporal

**Objetivo:** Verificar que el usuario puede agregar una nueva medición.

#### Request

```http
POST /api/v1/perfil/mediciones
Authorization: Bearer {TOKEN_COPIADO}
Content-Type: application/json
```

**Body (Nueva medición para hoy):**
```json
{
  "fechaMedicion": "2025-11-05",
  "peso": 72.0,
  "altura": 168
}
```

#### Response Esperado

```json
{
  "success": true,
  "message": "Medición registrada exitosamente",
  "data": {
    "id": 23,
    "fechaMedicion": "2025-11-05",
    "peso": 72.0,
    "altura": 168,
    "imc": 25.5,
    "clasificacionIMC": "Peso normal"
  },
  "timestamp": "2025-11-04T20:40:00"
}
```

#### ✅ Validaciones
- [ ] HTTP Status: 200 OK
- [ ] ID generado automáticamente
- [ ] IMC calculado: 72.0 / (1.68)² = 25.5
- [ ] Clasificación IMC correcta
- [ ] Medición guardada en base de datos

#### 📸 Capturas Requeridas
1. Request body en Swagger
2. Response exitoso con ID generado
3. GET /perfil/mediciones mostrando la nueva medición al inicio

---

### CASO 5: Actualizar Medición Existente

**User Story:** US-24 - Editar medición

**Objetivo:** Verificar que el usuario puede corregir una medición ya registrada.

#### Request

```http
PUT /api/v1/perfil/mediciones/23
Authorization: Bearer {TOKEN_COPIADO}
Content-Type: application/json
```

**Body (Corregir peso):**
```json
{
  "peso": 71.8,
  "altura": 168
}
```

#### Response Esperado

```json
{
  "success": true,
  "message": "Medición actualizada exitosamente",
  "data": {
    "id": 23,
    "fechaMedicion": "2025-11-05",
    "peso": 71.8,
    "altura": 168,
    "imc": 25.4,
    "clasificacionIMC": "Peso normal"
  },
  "timestamp": "2025-11-04T20:45:00"
}
```

#### ✅ Validaciones
- [ ] HTTP Status: 200 OK
- [ ] Peso actualizado correctamente
- [ ] IMC recalculado: 71.8 / (1.68)² = 25.4
- [ ] Fecha de medición no cambió
- [ ] ID permanece igual

#### 📸 Capturas Requeridas
1. Request con ID en URL y body
2. Response con valores actualizados
3. Comparación antes/después

---

### CASO 6: Obtener Medición Específica por ID

**User Story:** US-24 - Consultar medición individual

**Objetivo:** Verificar que el usuario puede consultar una medición específica.

#### Request

```http
GET /api/v1/perfil/mediciones/12
Authorization: Bearer {TOKEN_COPIADO}
```

#### Response Esperado

```json
{
  "success": true,
  "message": "Medición obtenida exitosamente",
  "data": {
    "id": 12,
    "fechaMedicion": "2025-11-04",
    "peso": 72.5,
    "altura": 168,
    "imc": 25.7,
    "clasificacionIMC": "Peso normal"
  },
  "timestamp": "2025-11-04T20:50:00"
}
```

#### ✅ Validaciones
- [ ] HTTP Status: 200 OK
- [ ] ID correcto en respuesta
- [ ] Todos los campos presentes

#### 📸 Capturas Requeridas
1. Request con ID en URL
2. Response detallado

---

### CASO 7: Eliminar Medición

**User Story:** US-24 - Eliminar medición

**Objetivo:** Verificar que el usuario puede eliminar una medición registrada.

#### Request

```http
DELETE /api/v1/perfil/mediciones/23
Authorization: Bearer {TOKEN_COPIADO}
```

#### Response Esperado

```json
{
  "success": true,
  "message": "Medición eliminada exitosamente",
  "data": null,
  "timestamp": "2025-11-04T20:55:00"
}
```

#### ✅ Validaciones
- [ ] HTTP Status: 200 OK
- [ ] Mensaje de confirmación recibido
- [ ] GET /perfil/mediciones ya no muestra la medición eliminada
- [ ] Medición eliminada de base de datos

#### 📸 Capturas Requeridas
1. Request DELETE exitoso
2. Response con mensaje de confirmación
3. GET /perfil/mediciones sin la medición eliminada

---

### CASO 8: Crear Perfil de Salud (Primera Vez)

**User Story:** US-04 - Crear perfil inicial

**Objetivo:** Verificar que un usuario nuevo puede crear su perfil de salud por primera vez.

**Nota:** Este caso requiere un usuario que NO tenga perfil de salud. Puedes usar el endpoint de registro para crear uno nuevo.

#### Paso previo: Crear usuario nuevo

```http
POST /api/v1/auth/register
Content-Type: application/json
```

**Body:**
```json
{
  "email": "test@nutritrack.com",
  "password": "Test123!",
  "nombre": "Usuario",
  "apellido": "Prueba"
}
```

#### Request

```http
POST /api/v1/perfil/salud
Authorization: Bearer {TOKEN_USUARIO_NUEVO}
Content-Type: application/json
```

**Body:**
```json
{
  "objetivoActual": "PERDER_PESO",
  "nivelActividadActual": "BAJO"
}
```

#### Response Esperado

```json
{
  "success": true,
  "message": "Perfil de salud creado exitosamente",
  "data": {
    "objetivoActual": "PERDER_PESO",
    "nivelActividadActual": "BAJO",
    "fechaActualizacion": "2025-11-04"
  },
  "timestamp": "2025-11-04T21:00:00"
}
```

#### ✅ Validaciones
- [ ] HTTP Status: 200 OK
- [ ] Perfil creado con datos enviados
- [ ] Fecha de actualización es hoy
- [ ] GET /perfil/salud retorna el perfil creado

#### 📸 Capturas Requeridas
1. Registro del usuario nuevo
2. Login del usuario nuevo
3. POST /perfil/salud exitoso
4. GET /perfil/salud confirmando creación

---

## 🚫 CASOS DE ERROR A PROBAR

### ERROR 1: Acceso sin autenticación

**Request:**
```http
GET /api/v1/perfil/salud
```
(Sin header Authorization)

**Response Esperado:**
```json
{
  "success": false,
  "message": "Token JWT requerido",
  "timestamp": "2025-11-04T21:05:00"
}
```

**HTTP Status:** 401 Unauthorized

---

### ERROR 2: Token inválido

**Request:**
```http
GET /api/v1/perfil/salud
Authorization: Bearer token_invalido_123
```

**Response Esperado:**
```json
{
  "success": false,
  "message": "Token JWT inválido o expirado",
  "timestamp": "2025-11-04T21:06:00"
}
```

**HTTP Status:** 401 Unauthorized

---

### ERROR 3: Medición con fecha duplicada

**Request:**
```http
POST /api/v1/perfil/mediciones
Authorization: Bearer {TOKEN}
Content-Type: application/json
```

**Body (fecha que ya existe):**
```json
{
  "fechaMedicion": "2025-11-04",
  "peso": 70.0,
  "altura": 168
}
```

**Response Esperado:**
```json
{
  "success": false,
  "message": "Ya existe una medición para la fecha 2025-11-04",
  "timestamp": "2025-11-04T21:07:00"
}
```

**HTTP Status:** 400 Bad Request

---

### ERROR 4: Objetivo inválido

**Request:**
```http
PUT /api/v1/perfil/salud
Authorization: Bearer {TOKEN}
Content-Type: application/json
```

**Body:**
```json
{
  "objetivoActual": "OBJETIVO_INVENTADO",
  "nivelActividadActual": "ALTO"
}
```

**Response Esperado:**
```json
{
  "success": false,
  "message": "Objetivo de salud inválido. Valores permitidos: PERDER_PESO, GANAR_MASA_MUSCULAR, MANTENER_FORMA, REHABILITACION, CONTROLAR_ESTRES",
  "timestamp": "2025-11-04T21:08:00"
}
```

**HTTP Status:** 400 Bad Request

---

### ERROR 5: Medición no encontrada

**Request:**
```http
GET /api/v1/perfil/mediciones/99999
Authorization: Bearer {TOKEN}
```

**Response Esperado:**
```json
{
  "success": false,
  "message": "Medición no encontrada con ID: 99999",
  "timestamp": "2025-11-04T21:09:00"
}
```

**HTTP Status:** 404 Not Found

---

## ✅ CHECKLIST COMPLETO DE CAPTURAS

### Casos de Éxito (13 capturas mínimo)

- [ ] **CASO 1:** GET perfil salud (Demo)
- [ ] **CASO 1:** GET perfil salud (Admin)
- [ ] **CASO 2:** PUT perfil salud - Request
- [ ] **CASO 2:** PUT perfil salud - Response
- [ ] **CASO 2:** GET verificación después de PUT
- [ ] **CASO 3:** GET historial completo (11 mediciones)
- [ ] **CASO 4:** POST nueva medición - Request
- [ ] **CASO 4:** POST nueva medición - Response
- [ ] **CASO 4:** GET historial con nueva medición
- [ ] **CASO 5:** PUT actualizar medición
- [ ] **CASO 6:** GET medición específica
- [ ] **CASO 7:** DELETE medición - Response
- [ ] **CASO 7:** GET historial sin medición eliminada
- [ ] **CASO 8:** POST crear perfil primera vez (usuario nuevo)

### Casos de Error (5 capturas mínimo)

- [ ] **ERROR 1:** 401 Sin autenticación
- [ ] **ERROR 2:** 401 Token inválido
- [ ] **ERROR 3:** 400 Fecha duplicada
- [ ] **ERROR 4:** 400 Objetivo inválido
- [ ] **ERROR 5:** 404 Medición no encontrada

---

## 🎯 VALORES VÁLIDOS PARA PRUEBAS

### Objetivos de Salud (ObjetivoSalud)
- `PERDER_PESO`
- `GANAR_MASA_MUSCULAR`
- `MANTENER_FORMA`
- `REHABILITACION`
- `CONTROLAR_ESTRES`

### Niveles de Actividad (NivelActividad)
- `BAJO` - Sedentario, poco o ningún ejercicio
- `MODERADO` - Ejercicio ligero 1-3 días/semana
- `ALTO` - Ejercicio intenso 4-7 días/semana

### Rangos Válidos para Mediciones
- **Peso:** 20 - 300 kg
- **Altura:** 50 - 250 cm
- **Fecha:** No puede ser futura, no duplicada

---

## 📊 CÁLCULO DE IMC Y CLASIFICACIÓN

**Fórmula:** IMC = peso (kg) / (altura (m))²

### Clasificación IMC (Adultos)
- **Bajo peso:** IMC < 18.5
- **Peso normal:** 18.5 ≤ IMC < 25
- **Sobrepeso:** 25 ≤ IMC < 30
- **Obesidad grado I:** 30 ≤ IMC < 35
- **Obesidad grado II:** 35 ≤ IMC < 40
- **Obesidad grado III:** IMC ≥ 40

### Ejemplos de Cálculo

**Usuario Demo (Nov 4, 2025):**
- Peso: 72.5 kg
- Altura: 168 cm = 1.68 m
- IMC = 72.5 / (1.68)² = 72.5 / 2.8224 = **25.7** (Sobrepeso)

**Usuario Admin:**
- Peso: 70.5 kg
- Altura: 175 cm = 1.75 m
- IMC = 70.5 / (1.75)² = 70.5 / 3.0625 = **23.0** (Peso normal)

---

## 🚀 TIPS PARA PRUEBAS EXITOSAS

### 1. Organización
- Prueba primero con usuario Demo (tiene datos precargados)
- Luego prueba con Admin para comparar
- Finalmente crea usuario nuevo para probar creación inicial

### 2. Capturas de Pantalla
- Usa nombres descriptivos: `demo_get_perfil_salud.png`
- Muestra siempre el código de estado HTTP
- Captura el body completo de la respuesta
- Si es scroll largo, haz múltiples capturas

### 3. Documentación
- Anota el timestamp de cada prueba
- Guarda los tokens JWT en un archivo temporal
- Documenta cualquier error inesperado

### 4. Verificación
- Después de cada POST, haz GET para verificar
- Después de cada PUT, haz GET para confirmar cambios
- Después de cada DELETE, haz GET para confirmar eliminación

---

## 📞 SOPORTE

**Documentación adicional:**
- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs

**Archivos de referencia:**
- `docs/USER_STORIES.MD` - Historias de usuario completas
- `docs/REGLAS_NEGOCIO.MD` - Reglas de negocio detalladas
- `SQL/data_demo.sql` - Datos de demostración cargados

---

**Documento preparado por:** Fabián Rojas Pizarro  
**Fecha:** Noviembre 2025  
**Módulo:** Gestión de Perfil de Salud y Mediciones (Módulo 2)  
**Versión:** 1.0
