# NutriTrack API 🥗

API REST para la gestión de seguimiento nutricional y planes de salud personalizados.

[![Build Status](https://img.shields.io/badge/build-passing-brightgreen)]()
[![Java](https://img.shields.io/badge/Java-17-orange)]()
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.7-green)]()
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue)]()

## 📋 Tabla de Contenidos

- [Descripción](#descripción)
- [Estado del Proyecto](#estado-del-proyecto)
- [Tecnologías](#tecnologías)
- [Inicio Rápido](#inicio-rápido)
- [Estructura del Proyecto](#estructura-del-proyecto)
- [Módulos del Sistema](#módulos-del-sistema)
- [Documentación](#documentación)
- [Contribuir](#contribuir)
- [Equipo de Desarrollo](#equipo-de-desarrollo)

## 📊 Estado del Proyecto

| Módulo | Estado | Progreso | Tests | Última Actualización |
|--------|--------|----------|-------|----------------------|
| Autenticación y Perfiles | ✅ Completado | 100% | 42/42 ✅ | Nov 2025 |
| Biblioteca de Contenido | ✅ Completado | 100% | 54/54 ✅ | Nov 2025 |
| Planes Nutricionales | ✅ Completado | 100% | 40/40 ✅ | Nov 2025 |
| Rutinas de Ejercicio | ✅ Completado | 100% | 36/36 ✅ | Nov 2025 |
| Seguimiento y Asignaciones | ✅ Completado | 100% | 30/30 ✅ | Nov 2025 |
| **Total** | **✅ Completado** | **100%** | **202/202 ✅** | **Nov 2025** |

## 🛠 Tecnologías

- **Java 17** - Lenguaje de programación
- **Spring Boot 3.5.7** - Framework principal
- **Spring Security 6** - Autenticación y autorización
- **JWT (jjwt 0.12.6)** - Tokens de seguridad
- **Spring Data JPA** - Persistencia de datos con Hibernate 6
- **PostgreSQL 16** - Base de datos relacional
- **Docker & Docker Compose** - Contenedores y orquestación
- **Maven 3.8+** - Gestión de dependencias
- **Lombok** - Reducción de código boilerplate
NutriTrack API es un sistema backend desarrollado en Spring Boot que proporciona servicios para:
- Gestión de usuarios y autenticación
- Seguimiento de medidas corporales e historial
- Gestión de perfiles de salud
- Sistema de etiquetas para ingredientes, ejercicios, metas y planes
- Autenticación y autorización con JWT

## 🛠 Tecnologías

- **Java 17+**
- **Spring Boot 3.x**
- **Spring Security** - Autenticación y autorización
- **JWT** - Tokens de seguridad
- **Spring Data JPA** - Persistencia de datos
- **MySQL/PostgreSQL** - Base de datos
- **Maven** - Gestión de dependencias
- **Lombok** - Reducción de código boilerplate

## 📁 Estructura del Proyecto

```
nutritrack-api/
├── docs/                          # Documentación del proyecto
│   ├── architecture/              # Documentos de arquitectura
│   ├── modules/                   # Documentación de cada módulo
│   ├── deployment/                # Guías de despliegue
│   └── api/                       # Documentación de APIs
├── src/
│   ├── main/
│   │   ├── java/com/nutritrack/nutritrackapi/
│   │   │   ├── config/           # Configuraciones generales
│   │   │   ├── controller/       # Controladores REST
│   │   │   ├── service/          # Lógica de negocio
│   │   │   ├── repository/       # Acceso a datos
│   │   │   ├── model/            # Entidades JPA
│   │   │   ├── dto/              # Data Transfer Objects
│   │   │   ├── exception/        # Manejo de excepciones
│   │   │   └── security/         # Seguridad y JWT
│   │   └── resources/
│   │       └── application.properties
│   └── test/                      # Tests unitarios e integración
├── .github/                       # Templates y workflows
├── pom.xml                        # Dependencias Maven
└── README.md                      # Este archivo
```
## 🚀 Inicio Rápido

### Prerrequisitos

- **JDK 17** - [Descargar](https://adoptium.net/)
- **Docker** y **Docker Compose** - [Descargar](https://www.docker.com/)
- **Git** - [Descargar](https://git-scm.com/)
- **IDE** (recomendado: IntelliJ IDEA o VS Code)

### Instalación en 3 Pasos

#### 1️⃣ Clonar el repositorio
```bash
git clone https://github.com/leonelalz/nutritrack-api.git
cd nutritrack-api
```

#### 2️⃣ Iniciar PostgreSQL con Docker
```bash
docker-compose up -d postgres
```

Esto creará automáticamente:
- Base de datos `nutritrack_db`
- Usuario `nutritrack` / Contraseña `nutritrack123`
- Puerto `5433` (para evitar conflictos con PostgreSQL local)
- Esquema completo con datos de prueba (roles, etiquetas)

#### 3️⃣ Ejecutar la aplicación
```bash
./mvnw spring-boot:run
```

✅ **¡Listo!** La API estará disponible en: `http://localhost:8080/api/v1`

### 🧪 Verificar Instalación

**Swagger UI:** http://localhost:8080/api/v1/swagger-ui/index.html

```bash
# Probar registro de usuario
curl http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"Test123!","nombre":"Test","apellido":"User"}'

# Login con admin por defecto
curl http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@fintech.com","password":"admin123"}'
```

**Usuario Admin por defecto:**
- Email: `admin@fintech.com`
- Password: `admin123`

### 📝 Configuración Adicional

**Archivo:** `src/main/resources/application.properties`

```properties
# Base de datos (ya configurado para Docker)
spring.datasource.url=jdbc:postgresql://localhost:5433/nutritrack_db
spring.datasource.username=nutritrack
spring.datasource.password=nutritrack123

# JWT (ya configurado)
jwt.secret=NutriTrack2025SecretKeyForJWTTokenGenerationAndValidation256Bits
jwt.expiration=86400000

# Servidor
server.servlet.context-path=/api/v1
server.port=8080
```

### 📊 Ejecutar Tests

```bash
# Ejecutar todos los tests
./mvnw test

# Ver resumen de tests
./mvnw test 2>&1 | Select-String -Pattern "(Tests run:|BUILD SUCCESS|BUILD FAILURE)"

# Tests individuales por módulo
./mvnw test -Dtest=AuthServiceTest
./mvnw test -Dtest=ComidaServiceTest
./mvnw test -Dtest=PlanServiceTest
```

**Cobertura de Tests:** 202 tests unitarios ✅

### 🧹 Comandos Útiles

```bash
# Detener base de datos
docker-compose down

# Ver logs de PostgreSQL
docker-compose logs -f postgres

# Ejecutar tests
./mvnw test

# Compilar sin tests
./mvnw clean install -DskipTests

# Reiniciar base de datos (⚠️ elimina datos)
docker-compose down -v
docker-compose up -d postgres
```
La API estará disponible en: `http://localhost:8080`

## 🧩 Módulos del Sistema

El sistema está completamente implementado con 5 módulos principales:

### ✅ 1️⃣ Módulo de Autenticación y Perfiles [COMPLETADO]
**Tests:** 42/42 ✅ | **Última actualización:** Nov 2025

**Funcionalidades:**
- ✅ Registro y autenticación de usuarios con JWT
- ✅ Gestión de perfiles de usuario y salud
- ✅ Configuración de unidades de medida (KG/LBS)
- ✅ Sistema de roles (ADMIN/USER)
- ✅ Soft delete de cuentas

**Endpoints principales:**
- `POST /api/v1/auth/register` - Registro de usuario
- `POST /api/v1/auth/login` - Inicio de sesión
- `GET /api/v1/app/profile` - Obtener perfil
- `PUT /api/v1/app/profile` - Actualizar perfil
- `DELETE /api/v1/app/profile` - Eliminar cuenta
- `GET /api/v1/perfiles` - Listar perfiles (Admin)
- `GET /api/v1/cuentas` - Gestión de cuentas (Admin)

---

### ✅ 2️⃣ Módulo de Biblioteca de Contenido [COMPLETADO]
**Tests:** 54/54 ✅ | **Última actualización:** Nov 2025

**Funcionalidades:**
- ✅ Gestión completa de etiquetas (alergias, dietas, etc.)
- ✅ CRUD de ingredientes con información nutricional
- ✅ CRUD de ejercicios con etiquetas
- ✅ CRUD de comidas con recetas detalladas
- ✅ Sistema de etiquetado flexible

**Endpoints principales:**
- `GET/POST/PUT/DELETE /api/v1/etiquetas` - Gestión de etiquetas
- `GET/POST/PUT/DELETE /api/v1/admin/ingredientes` - Ingredientes
- `GET/POST/PUT/DELETE /api/v1/admin/ejercicios` - Ejercicios
- `GET/POST/PUT/DELETE /api/v1/admin/comidas` - Comidas

---

### ✅ 3️⃣ Módulo de Planes Nutricionales [COMPLETADO]
**Tests:** 40/40 ✅ | **Última actualización:** Nov 2025

**Funcionalidades:**
- ✅ Creación y gestión de planes nutricionales
- ✅ Asignación de planes a usuarios
- ✅ Seguimiento de estado (activo/pausado/completado)
- ✅ Historial de planes por usuario

**Endpoints principales:**
- `GET/POST/PUT/DELETE /api/v1/admin/planes` - Gestión de planes (Admin)
- `GET /api/v1/usuario-planes` - Mis planes
- `POST /api/v1/usuario-planes/{planId}/asignar` - Asignar plan
- `PUT /api/v1/usuario-planes/{id}/estado` - Cambiar estado

---

### ✅ 4️⃣ Módulo de Rutinas de Ejercicio [COMPLETADO]
**Tests:** 36/36 ✅ | **Última actualización:** Nov 2025

**Funcionalidades:**
- ✅ Creación de rutinas de ejercicio personalizadas
- ✅ Asignación de rutinas a usuarios
- ✅ Gestión de series, repeticiones y duración
- ✅ Seguimiento de progreso en rutinas

**Endpoints principales:**
- `GET/POST/PUT/DELETE /api/v1/admin/rutinas` - Gestión de rutinas (Admin)
- `GET /api/v1/usuario-rutinas` - Mis rutinas
- `POST /api/v1/usuario-rutinas/{rutinaId}/asignar` - Asignar rutina
- `PUT /api/v1/usuario-rutinas/{id}/estado` - Cambiar estado

---

### ✅ 5️⃣ Módulo de Seguimiento y Asignaciones [COMPLETADO]
**Tests:** 30/30 ✅ | **Última actualización:** Nov 2025

**Funcionalidades:**
- ✅ Registro de comidas consumidas
- ✅ Registro de ejercicios realizados
- ✅ Seguimiento de progreso diario
- ✅ Historial completo de actividades

**Endpoints principales:**
- `GET/POST /api/v1/registro-comidas` - Registro de comidas
- `GET/POST /api/v1/registro-ejercicios` - Registro de ejercicios
- `GET /api/v1/registro-comidas/usuario/{id}` - Historial de comidas
- `GET /api/v1/registro-ejercicios/usuario/{id}` - Historial de ejercicios

## 📚 Documentación

### 📖 Documentación General
- **[Inicio Rápido](docs/QUICKSTART.md)** - Guía de inicio para nuevos desarrolladores
- **[Guía de Desarrollo](docs/DEVELOPMENT_GUIDE.md)** - Estándares y mejores prácticas
- **[Guía de Contribución](CONTRIBUTING.md)** - Cómo contribuir al proyecto
- **[Arquitectura del Sistema](docs/architecture/)** - Diseño y decisiones técnicas

### 🔌 API y Testing
- **[API Reference](docs/API_REFERENCE.md)** - Referencia completa de endpoints
- **[Testing Manual - Módulo 1](TESTING_MODULE1.md)** - Guía de pruebas del módulo actual
- **[Guía de Postman](docs/testing/POSTMAN_GUIDE.md)** - Pruebas con Postman
- **[Colecciones Postman](postman/)** - Colecciones listas para importar

### 🗄️ Base de Datos
- **[Schema SQL](SQL/NutriDB.sql)** - Script completo de base de datos
- **[SQL README](SQL/README.md)** - Documentación del esquema

### 🚀 Deployment
- **[Guía de Despliegue](docs/deployment/DEPLOYMENT_GUIDE.md)** - Instrucciones de deployment
- **[Docker Setup](docker-compose.yml)** - Configuración de contenedores

## 👥 Equipo de Desarrollo

| Módulo | Estado | Tests | Responsable(s) |
|--------|--------|-------|----------------|
| Autenticación y Perfiles | ✅ Completado | 42/42 | Leonel Alzamora |
| Biblioteca de Contenido | ✅ Completado | 54/54 | Equipo Backend |
| Planes Nutricionales | ✅ Completado | 40/40 | Equipo Backend |
| Rutinas de Ejercicio | ✅ Completado | 36/36 | Equipo Backend |
| Seguimiento y Asignaciones | ✅ Completado | 30/30 | Equipo Backend |

**Total:** 202 tests unitarios pasando ✅

## 🤝 Contribuir

Por favor lee [CONTRIBUTING.md](CONTRIBUTING.md) para detalles sobre nuestro código de conducta y el proceso para enviarnos pull requests.

### Flujo de Trabajo Git

1. Crear una rama desde `development`
2. Hacer commits descriptivos
3. Crear Pull Request
4. Esperar revisión de código
5. Merge después de aprobación

## 📄 Licencia

Este proyecto es privado y confidencial.

## 📞 Contacto

Para preguntas o sugerencias, contactar al líder del proyecto.

---
Desarrollado con ❤️ por el equipo de NutriTrack
