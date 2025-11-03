# Guía de Testing - Postman y Swagger UI

## 🎯 Herramientas de Testing para NutriTrack API

Este documento describe cómo usar **Postman** y **Swagger UI** para probar los endpoints de la API.

---

## 📮 Postman

### Importar Colección

1. Abre Postman
2. Click en **Import**
3. Selecciona el archivo: `testing/NutriTrack-Postman-Collection.json`
4. La colección "NutriTrack API - Módulo 1" aparecerá en tu workspace

### Variables de Colección

La colección incluye variables predefinidas:

| Variable | Valor por Defecto | Descripción |
|----------|------------------|-------------|
| `baseUrl` | `http://localhost:8080/api/v1` | URL base de la API |
| `token` | `""` | Token JWT (se actualiza automáticamente) |
| `testEmail` | `test@nutritrack.com` | Email de prueba |

### Secuencia de Testing Recomendada

#### 1️⃣ Registrar Usuario
**Request:** `POST {{baseUrl}}/auth/register`

```json
{
  "email": "test@nutritrack.com",
  "password": "Test123!@#",
  "nombre": "Usuario de Prueba Postman"
}
```

**Response esperado (201 Created):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "type": "Bearer",
  "email": "test@nutritrack.com",
  "name": "Usuario de Prueba Postman"
}
```

✅ El token se guarda automáticamente en la variable `{{token}}`

#### 2️⃣ Login
**Request:** `POST {{baseUrl}}/auth/login`

```json
{
  "email": "test@nutritrack.com",
  "password": "Test123!@#"
}
```

**Response esperado (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "type": "Bearer",
  "email": "test@nutritrack.com",
  "name": "Usuario de Prueba Postman"
}
```

#### 3️⃣ Obtener Perfil
**Request:** `GET {{baseUrl}}/app/profile?email={{testEmail}}`

**Response esperado (200 OK):**
```json
{
  "success": true,
  "message": "Perfil obtenido exitosamente",
  "data": {
    "profileId": "e7c5c1a8-8181-4a7f-b55c-74f7fd36a7d6",
    "nombre": "Usuario de Prueba Postman",
    "unidadesMedida": "KG",
    "fechaInicioApp": "2025-11-02",
    "perfilSalud": null
  }
}
```

#### 4️⃣ Actualizar Perfil
**Request:** `PUT {{baseUrl}}/app/profile?email={{testEmail}}`

```json
{
  "nombre": "Usuario Actualizado Postman",
  "unidadesMedida": "LBS",
  "objetivoActual": "PERDER_PESO",
  "nivelActividadActual": "MODERADO",
  "etiquetasSaludIds": [1, 2, 10]
}
```

**Response esperado (200 OK):**
```json
{
  "success": true,
  "message": "Perfil actualizado exitosamente",
  "data": {
    "profileId": "e7c5c1a8-8181-4a7f-b55c-74f7fd36a7d6",
    "nombre": "Usuario Actualizado Postman",
    "unidadesMedida": "LBS",
    "fechaInicioApp": "2025-11-02",
    "perfilSalud": {
      "objetivoActual": "PERDER_PESO",
      "nivelActividadActual": "MODERADO",
      "etiquetasSalud": [
        {
          "id": 1,
          "nombre": "Perder Peso",
          "tipoEtiqueta": "OBJETIVO"
        },
        {
          "id": 2,
          "nombre": "Ganar Músculo",
          "tipoEtiqueta": "OBJETIVO"
        },
        {
          "id": 10,
          "nombre": "Lácteos",
          "tipoEtiqueta": "ALERGIA"
        }
      ],
      "fechaActualizacion": "2025-11-02"
    }
  }
}
```

#### 5️⃣ Eliminar Cuenta
**Request:** `DELETE {{baseUrl}}/app/profile?email={{testEmail}}`

**Response esperado (200 OK):**
```json
{
  "success": true,
  "message": "Tu cuenta ha sido eliminada permanentemente",
  "data": null
}
```

### Scripts de Postman

La colección incluye scripts automáticos:

**Test Script (Registro y Login):**
```javascript
if (pm.response.code === 201 || pm.response.code === 200) {
    var jsonData = pm.response.json();
    pm.collectionVariables.set("token", jsonData.token);
}
```

Esto extrae automáticamente el token JWT de las respuestas y lo guarda para usarlo en requests futuros.

---

## 📚 Swagger UI

### Acceder a Swagger UI

Una vez que la aplicación esté ejecutándose:

1. **Swagger UI:** http://localhost:8080/swagger-ui.html
2. **OpenAPI JSON:** http://localhost:8080/v3/api-docs
3. **OpenAPI YAML:** http://localhost:8080/v3/api-docs.yaml

### Características de Swagger UI

#### 🔍 Exploración de Endpoints

Swagger UI muestra todos los endpoints organizados por tags:

- **Autenticación** (2 endpoints)
  - `POST /api/v1/auth/register` - Registrar nuevo usuario
  - `POST /api/v1/auth/login` - Iniciar sesión

- **Perfil de Usuario** (3 endpoints)
  - `GET /api/v1/app/profile` - Obtener mi perfil
  - `PUT /api/v1/app/profile` - Actualizar mi perfil
  - `DELETE /api/v1/app/profile` - Eliminar mi cuenta

#### 🔐 Autenticación en Swagger

**Opción 1: Sin JWT (Temporal - Testing)**

Para los endpoints de perfil, usa el parámetro `email`:

```
GET /api/v1/app/profile?email=test@nutritrack.com
```

**Opción 2: Con JWT (Producción)**

1. Ejecuta `POST /auth/register` o `POST /auth/login`
2. Copia el token de la respuesta
3. Click en el botón **Authorize** (🔓 arriba a la derecha)
4. Pega el token (sin "Bearer ")
5. Click en **Authorize**
6. Ahora todos los endpoints con 🔒 usarán automáticamente el token

#### 📝 Probar un Endpoint

1. Click en el endpoint (ej: `POST /auth/register`)
2. Click en **Try it out**
3. Edita el JSON del request body:
   ```json
   {
     "email": "swagger@nutritrack.com",
     "password": "Swagger123!",
     "nombre": "Usuario Swagger"
   }
   ```
4. Click en **Execute**
5. Ver la respuesta abajo con:
   - Código de respuesta (201, 200, 400, etc.)
   - Response body (JSON)
   - Response headers
   - Curl command equivalente

#### 📋 Schemas

Swagger UI muestra los schemas de DTOs en la sección **Schemas** al final:

- `RegistroRequestDTO`
- `LoginRequestDTO`
- `ActualizarPerfilRequest`
- `AuthResponse`
- `PerfilUsuarioResponse`
- `ApiResponse`

Esto te permite ver la estructura exacta de cada objeto.

---

## 🎯 Comparación: Postman vs Swagger

| Característica | Postman | Swagger UI |
|----------------|---------|------------|
| **Ventajas** | Colecciones reutilizables, automatización con scripts, entornos múltiples | Documentación interactiva siempre actualizada, no requiere importación |
| **Casos de uso** | Testing completo, CI/CD, compartir colecciones | Exploración rápida, documentación para desarrolladores |
| **Autenticación** | Variables de colección para tokens | Botón Authorize centralizado |
| **Scripts** | Pre-request y test scripts | No disponible |
| **Offline** | Funciona offline | Requiere aplicación corriendo |

---

## ⚙️ Configuración de Entornos (Postman)

### Crear Entorno de Desarrollo

1. En Postman, click en **Environments**
2. Click **+** para crear nuevo entorno
3. Nombre: `NutriTrack - Development`
4. Variables:

| Variable | Initial Value | Current Value |
|----------|---------------|---------------|
| `baseUrl` | `http://localhost:8080/api/v1` | `http://localhost:8080/api/v1` |
| `dbPort` | `5433` | `5433` |
| `testEmail` | `dev@nutritrack.com` | `dev@nutritrack.com` |

### Crear Entorno de Producción (Futuro)

| Variable | Initial Value |
|----------|---------------|
| `baseUrl` | `https://api.nutritrack.com/v1` |
| `testEmail` | `prod@nutritrack.com` |

---

## 🧪 Testing Automatizado con Newman

Newman es el runner de línea de comandos de Postman.

### Instalación

```bash
npm install -g newman
```

### Ejecutar Colección

```bash
newman run testing/NutriTrack-Postman-Collection.json
```

### Con Entorno

```bash
newman run testing/NutriTrack-Postman-Collection.json \
  -e testing/NutriTrack-Dev-Environment.json
```

### Generar Reporte HTML

```bash
npm install -g newman-reporter-htmlextra

newman run testing/NutriTrack-Postman-Collection.json \
  -r htmlextra \
  --reporter-htmlextra-export testing/reports/report.html
```

---

## 📊 Valores de Referencia

### Enums Disponibles

**UnidadesMedida:**
- `KG` - Kilogramos
- `LBS` - Libras

**ObjetivoGeneral:**
- `PERDER_PESO`
- `GANAR_MASA_MUSCULAR`
- `MANTENER_FORMA`
- `REHABILITACION`
- `CONTROLAR_ESTRES`

**NivelActividad:**
- `BAJO`
- `MODERADO`
- `ALTO`

### IDs de Etiquetas de Prueba

| ID | Nombre | Tipo |
|----|--------|------|
| 1 | Perder Peso | OBJETIVO |
| 2 | Ganar Músculo | OBJETIVO |
| 3 | Mantener Forma | OBJETIVO |
| 10 | Lácteos | ALERGIA |
| 11 | Nueces | ALERGIA |
| 12 | Gluten | ALERGIA |
| 13 | Mariscos | ALERGIA |
| 20 | Diabetes | CONDICION_MEDICA |
| 21 | Hipertensión | CONDICION_MEDICA |
| 22 | Colesterol Alto | CONDICION_MEDICA |

---

## 🐛 Troubleshooting

### Error: "Connection refused"

**Causa:** La aplicación no está ejecutándose.

**Solución:**
```bash
./mvnw spring-boot:run -DskipTests
```

### Error: "Email already registered"

**Causa:** El email ya existe en la base de datos.

**Solución:** Usa un email diferente o limpia la base de datos:
```bash
docker-compose down -v
docker-compose up -d
```

### Swagger UI no carga

**Causa:** Falta la dependencia de SpringDoc.

**Solución:** Verifica que `pom.xml` tenga:
```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.6.0</version>
</dependency>
```

### Token JWT expirado

**Causa:** El token tiene 24 horas de validez.

**Solución:** Ejecuta nuevamente `POST /auth/login` para obtener un token nuevo.

---

## 📝 Notas Importantes

1. **Parámetro `email` es TEMPORAL:** Se usa solo para testing sin JWT. Será removido en v0.2.0.

2. **CORS está habilitado:** La API acepta requests desde `http://localhost:3000` (para frontend React).

3. **Seguridad JWT deshabilitada temporalmente:** Para simplificar el testing inicial. Se re-activará en v0.2.0.

4. **Base de datos en Docker:** Puerto 5433 para evitar conflictos con instalaciones locales de PostgreSQL.

---

## 🚀 Próximos Pasos

1. ✅ Probar todos los endpoints en Postman
2. ✅ Validar documentación en Swagger UI
3. ⏳ Re-activar seguridad JWT
4. ⏳ Agregar tests automatizados con Newman en CI/CD
5. ⏳ Implementar Módulo 2: Biblioteca de Contenido (Admin)

---

**Última actualización:** 2 de Noviembre, 2025  
**Versión:** 0.1.0  
**Autor:** NutriTrack Team
