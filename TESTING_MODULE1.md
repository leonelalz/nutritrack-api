# 🧪 Testing Módulo 1 - NutriTrack API

## ✅ Endpoints Implementados

### Autenticación (Ya existente)
- `POST /api/v1/auth/register` - Registrar usuario
- `POST /api/v1/auth/login` - Iniciar sesión

### Gestión de Perfil (NUEVO - Módulo 1)
- `GET /api/v1/app/profile` - Obtener mi perfil
- `PUT /api/v1/app/profile` - Actualizar mi perfil
- `DELETE /api/v1/app/profile` - Eliminar mi cuenta

## 🚀 Preparar Base de Datos

```bash
# 1. Crear base de datos PostgreSQL
psql -U postgres
CREATE DATABASE nutritrack_db;
\c nutritrack_db
\i SQL/NutriDB.sql
\q

# 2. Configurar variables de entorno
cp .env.example .env
# Editar .env con tus credenciales
```

## 🧪 Pruebas con cURL

### 1. Registrar Usuario

```bash
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@nutritrack.com",
    "password": "SecurePassword123!",
    "nombre": "Usuario Test"
  }'
```

**Respuesta esperada:**
```json
{
  "userId": "uuid-aqui",
  "profileId": "uuid-aqui",
  "email": "test@nutritrack.com",
  "token": "jwt.token.string",
  "message": "Usuario registrado exitosamente"
}
```

### 2. Login

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@nutritrack.com",
    "password": "SecurePassword123!"
  }'
```

**Guarda el token para las siguientes pruebas!**

### 3. Obtener Mi Perfil (Requiere autenticación)

```bash
curl -X GET http://localhost:8080/api/v1/app/profile \
  -H "Authorization: Bearer TU_TOKEN_AQUI"
```

**Respuesta esperada:**
```json
{
  "success": true,
  "message": "Perfil obtenido exitosamente",
  "data": {
    "profileId": "uuid",
    "nombre": "Usuario Test",
    "unidadesMedida": "KG",
    "fechaInicioApp": "2025-11-02",
    "perfilSalud": null
  }
}
```

### 4. Actualizar Mi Perfil

```bash
curl -X PUT http://localhost:8080/api/v1/app/profile \
  -H "Authorization: Bearer TU_TOKEN_AQUI" \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Nuevo Nombre",
    "unidadesMedida": "LBS",
    "objetivoActual": "PERDER_PESO",
    "nivelActividadActual": "MODERADO",
    "etiquetasSaludIds": [10, 12]
  }'
```

**Respuesta esperada:**
```json
{
  "success": true,
  "message": "Perfil actualizado exitosamente",
  "data": {
    "profileId": "uuid",
    "nombre": "Nuevo Nombre",
    "unidadesMedida": "LBS",
    "fechaInicioApp": "2025-11-02",
    "perfilSalud": {
      "objetivoActual": "PERDER_PESO",
      "nivelActividadActual": "MODERADO",
      "etiquetasSalud": [
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

### 5. Eliminar Mi Cuenta

```bash
curl -X DELETE http://localhost:8080/api/v1/app/profile \
  -H "Authorization: Bearer TU_TOKEN_AQUI"
```

**Respuesta esperada:**
```json
{
  "success": true,
  "message": "Tu cuenta ha sido eliminada permanentemente",
  "data": null
}
```

## ✅ Checklist de Pruebas

- [ ] Base de datos creada con el script SQL
- [ ] Aplicación arranca sin errores
- [ ] POST /auth/register funciona (US-01)
- [ ] POST /auth/login funciona (US-02)
- [ ] GET /app/profile funciona (US-04)
- [ ] PUT /app/profile funciona (US-03)
- [ ] DELETE /app/profile funciona (US-05)
- [ ] Etiquetas de salud se asignan correctamente
- [ ] Unidades de medida se actualizan (KG/LBS)

## 🐛 Problemas Conocidos

- Hay warnings de null-safety (no afectan funcionalidad)
- EtiquetaService usa String en lugar de TipoEtiqueta enum (legacy, no afecta Módulo 1)
- DataInitializer tiene un error menor (corregir después)

## 📝 Notas

- El token JWT expira en 24 horas
- La eliminación de cuenta es lógica (setActive=false)
- Los datos de prueba incluyen etiquetas con IDs 1-32

---

**¿Todo funciona?** ✅ Proceder con merge a development
**¿Hay errores?** 🐛 Reportar para corrección
