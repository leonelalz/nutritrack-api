# Inicio Rápido - NutriTrack API 🚀

Guía rápida para poner en marcha el proyecto en 5 minutos.

## ⚡ Requisitos Previos

- ✅ Java 17+
- ✅ Maven 3.8+
- ✅ PostgreSQL 16+ (o Docker)

## 🚀 Instalación Rápida

### 1. Clonar el Repositorio

```bash
git clone https://github.com/leonelalz/nutritrack-api.git
cd nutritrack-api
```

### 2. Configurar Base de Datos

**Opción A: Con Docker (Recomendado)**

```bash
# Iniciar PostgreSQL con Docker Compose
docker-compose up -d postgres
```

Esto creará:
- Base de datos: `nutritrack_db`
- Usuario: `nutritrack`
- Contraseña: `nutritrack123`
- Puerto: `5433` (para evitar conflictos)

**Opción B: PostgreSQL Local**

```bash
psql -U postgres
CREATE DATABASE nutritrack_db;
CREATE USER nutritrack WITH PASSWORD 'nutritrack123';
GRANT ALL PRIVILEGES ON DATABASE nutritrack_db TO nutritrack;
```

### 3. Ejecutar

```bash
# Compilar y ejecutar
./mvnw spring-boot:run
```

**¡Listo!** La API está en: `http://localhost:8080/api/v1`

**Swagger UI:** http://localhost:8080/api/v1/swagger-ui/index.html

## 🧪 Verificar Instalación

### 1. Swagger UI (Documentación Interactiva)

Abre tu navegador en: http://localhost:8080/api/v1/swagger-ui/index.html

> ⚠️ **Seguridad JWT Activada:** Todos los endpoints (excepto `/auth/**` y Swagger) requieren autenticación. Usa el botón "Authorize" en Swagger con tu JWT token.

### 2. Login con Admin

```bash
curl http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@fintech.com","password":"admin123"}'
```

**Respuesta:**
```json
{
  "token": "eyJhbGciOiJIUzI1...",
  "email": "admin@fintech.com",
  "nombre": "Admin",
  "roles": ["ADMIN", "USER"]
}
```

**Usuario Admin por defecto:**
- Email: `admin@fintech.com`
- Password: `admin123`

**⚠️ Importante:** Guarda el token JWT para usarlo en las siguientes peticiones:
```bash
curl http://localhost:8080/api/v1/app/profile \
  -H "Authorization: Bearer <tu-token-jwt>"
```

### 3. Registrar nuevo usuario

```bash
curl http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"Test123!","nombre":"Test","apellido":"User"}'
```

## 📚 Próximos Pasos

1. **Explorar Swagger UI:** http://localhost:8080/api/v1/swagger-ui/index.html
2. **Leer documentación:** [README.md](../README.md)
3. **Importar colecciones Postman:** Ver carpeta [postman/](../postman/)
4. **Revisar resúmenes de módulos:** [MODULO2_RESUMEN.md](../MODULO2_RESUMEN.md), etc.

## 🎯 Endpoints Principales

### Autenticación
- `POST /api/v1/auth/register` - Registro
- `POST /api/v1/auth/login` - Login
- `GET /api/v1/app/profile` - Mi perfil

### Admin - Biblioteca de Contenido
- `GET /api/v1/admin/ingredientes` - Ingredientes
- `GET /api/v1/admin/ejercicios` - Ejercicios
- `GET /api/v1/admin/comidas` - Comidas

### Planes y Rutinas
- `GET /api/v1/admin/planes` - Planes nutricionales
- `GET /api/v1/admin/rutinas` - Rutinas de ejercicio
- `GET /api/v1/usuario-planes` - Mis planes
- `GET /api/v1/usuario-rutinas` - Mis rutinas

### Seguimiento
- `GET /api/v1/registro-comidas` - Registro de comidas
- `GET /api/v1/registro-ejercicios` - Registro de ejercicios

## 🆘 Problemas Comunes

### Error: Port 8080 already in use

```bash
# Cambiar puerto en application.properties
server.port=8081
```

### Error: Cannot connect to PostgreSQL

```bash
# Verificar PostgreSQL iniciado
docker-compose ps

# Ver logs
docker-compose logs postgres

# Reiniciar PostgreSQL
docker-compose restart postgres
```

### Error: Database "nutritrack_db" does not exist

```bash
# Recrear base de datos
docker-compose down -v
docker-compose up -d postgres
```

### Error: Authentication failed for user "nutritrack"

```bash
# Verificar credenciales en application.properties
spring.datasource.username=nutritrack
spring.datasource.password=nutritrack123
```

## 💡 Tips

- **Swagger UI:** Documentación interactiva en http://localhost:8080/api/v1/swagger-ui/index.html
- **Ver logs detallados:** Cambiar `logging.level.root=DEBUG` en properties
- **Tests:** `./mvnw test` (202 tests disponibles)
- **Limpiar build:** `./mvnw clean`
- **Reiniciar DB:** `docker-compose down -v && docker-compose up -d`

## � Estado del Proyecto

- ✅ Módulo 1: Autenticación y Perfiles (42 tests)
- ✅ Módulo 2: Biblioteca de Contenido (54 tests)
- ✅ Módulo 3: Planes Nutricionales (40 tests)
- ✅ Módulo 4: Rutinas de Ejercicio (36 tests)
- ✅ Módulo 5: Seguimiento y Asignaciones (30 tests)

**Total:** 202/202 tests pasando ✅

## 📞 Ayuda

¿Tienes problemas? 
- Revisa los logs: `docker-compose logs -f`
- Verifica la base de datos: `docker-compose exec postgres psql -U nutritrack -d nutritrack_db`
- Consulta la documentación: [README.md](../README.md)

---

**¡Feliz desarrollo! 🎉**
