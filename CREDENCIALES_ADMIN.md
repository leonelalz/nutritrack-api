# 👤 Credenciales de Administrador

## Usuario Administrador Inicial

El sistema crea automáticamente un usuario administrador en el primer arranque.

### 🔐 Credenciales de Acceso

```
Email:    admin@nutritrack.com
Password: Admin123!
Rol:      ROLE_ADMIN
```

### 📝 Cómo usar en Swagger

1. **Abrir Swagger UI**: http://localhost:8080/swagger-ui.html

2. **Login como Admin**:
   - Ve a `POST /auth/login`
   - Click en "Try it out"
   - Usa estas credenciales:
   ```json
   {
     "email": "admin@nutritrack.com",
     "password": "Admin123!"
   }
   ```
   - Click en "Execute"
   - Copia el token de la respuesta

3. **Autorizar en Swagger** (para endpoints protegidos):
   - Click en el botón "Authorize" 🔒 (arriba a la derecha)
   - Pega el token en el campo de autorización
   - Click en "Authorize"

### ⚠️ IMPORTANTE

- **Desarrollo**: Estas credenciales son válidas solo para desarrollo/testing
- **Producción**: DEBES cambiar esta contraseña antes de desplegar a producción
- **Seguridad**: Esta contraseña se genera automáticamente solo si no existe el usuario admin

### 🔄 Cambiar Contraseña (Recomendado)

Cuando implementes el endpoint para cambiar contraseña, úsalo inmediatamente después del primer login en producción.

### 📋 Usuario de Prueba Regular

Para probar funciones de usuario regular, registra un nuevo usuario con:
- `POST /auth/register`
- Email: cualquier email válido (ej: `usuario@test.com`)
- Password: mínimo 8 caracteres (ej: `Test1234`)

Este usuario tendrá `ROLE_USER` por defecto.

---

**Nota**: Si eliminas la base de datos y reinicias la aplicación, este usuario admin se volverá a crear automáticamente.
