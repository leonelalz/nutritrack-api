# 🥗 NutriTrack API

API REST para sistema de coaching nutricional y fitness desarrollada con Spring Boot 3.5.7 y Java 21.

## 📋 Tabla de Contenidos

- [Requisitos Previos](#requisitos-previos)
- [Instalación](#instalación)
- [Configuración de Base de Datos](#configuración-de-base-de-datos)
- [Ejecución del Proyecto](#ejecución-del-proyecto)
- [Documentación API](#documentación-api)
- [Estructura del Proyecto](#estructura-del-proyecto)
- [Stack Tecnológico](#stack-tecnológico)

## 🔧 Requisitos Previos

- **Java 21** o superior
- **Maven 3.8+**
- **Docker** y **Docker Compose** (para base de datos)
- **IntelliJ IDEA** (recomendado) o cualquier IDE Java

## 🚀 Instalación

### 1. Clonar el repositorio

```bash
git clone <url-del-repositorio>
cd nutritrack-API
```

### 2. Configurar variables de entorno (opcional)

```bash
cp .env.example .env
# Editar .env con tus credenciales personalizadas
```

## 🗄️ Configuración de Base de Datos

### Opción 1: Docker Compose (Recomendado)

Inicia PostgreSQL y PgAdmin con un solo comando:

```bash
docker-compose up -d
```

Esto creará:
- **PostgreSQL 16.10** en `localhost:5432`
- **PgAdmin 4** en `http://localhost:5050`

El script `SQL/NutriDB.sql` se ejecutará automáticamente al iniciar el contenedor.

#### Credenciales por defecto:

**PostgreSQL:**
- Host: `localhost`
- Puerto: `5432`
- Database: `nutritrack_db`
- Usuario: `postgres`
- Password: `postgres`

**PgAdmin:**
- URL: `http://localhost:5050`
- Email: `admin@nutritrack.com`
- Password: `admin`

#### Comandos útiles:

```bash
# Ver logs de PostgreSQL
docker-compose logs -f postgres

# Detener contenedores
docker-compose down

# Reiniciar base de datos (elimina datos)
docker-compose down -v
docker-compose up -d
```

### Opción 2: PostgreSQL Local

Si prefieres instalar PostgreSQL localmente:

1. Instala PostgreSQL 16+
2. Crea la base de datos:
   ```sql
   CREATE DATABASE nutritrack_db;
   ```
3. Ejecuta el script SQL:
   ```bash
   psql -U postgres -d nutritrack_db -f SQL/NutriDB.sql
   ```

## ▶️ Ejecución del Proyecto

### Desde IntelliJ IDEA:

1. Abre el proyecto en IntelliJ IDEA
2. Espera a que Maven descargue las dependencias
3. Ejecuta `NutritrackApiApplication.java`

### Desde línea de comandos:

```bash
# Compilar el proyecto
mvn clean install

# Ejecutar la aplicación
mvn spring-boot:run
```

La aplicación estará disponible en: `http://localhost:8080`

## 📚 Documentación API

### Swagger UI (Interfaz Interactiva)

Una vez iniciada la aplicación, accede a:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI Docs**: http://localhost:8080/api-docs

### Endpoints Principales

```
POST   /api/v1/auth/registro       - Crear cuenta
POST   /api/v1/auth/login          - Iniciar sesión
GET    /api/v1/planes/catalogo     - Ver planes disponibles
POST   /api/v1/usuario/planes/{id}/activar - Activar plan
GET    /api/v1/usuario/plan-actual/hoy     - Ver actividades del día
POST   /api/v1/usuario/registros/comidas   - Registrar comida
```

## 📁 Estructura del Proyecto

```
nutritrack-API/
├── docs/                          # Documentación del proyecto
│   ├── COMO_FUNCIONA.MD          # Modelo de negocio completo
│   ├── USER_STORIES.MD           # 25 User Stories
│   ├── REGLAS_NEGOCIO.MD         # 24 Reglas de negocio
│   ├── ENTIDADES.MD              # Entidades JPA
│   ├── DTOs.md                   # Data Transfer Objects
│   └── TESTING_GUIDE.md          # Guía de testing
├── SQL/
│   └── NutriDB.sql               # Schema completo de PostgreSQL
├── src/
│   ├── main/
│   │   ├── java/com/example/nutritrackapi/
│   │   │   ├── config/           # Configuración (Security, JWT)
│   │   │   ├── model/            # Entidades JPA
│   │   │   ├── repository/       # Repositorios Spring Data
│   │   │   ├── service/          # Lógica de negocio
│   │   │   ├── controller/       # REST Controllers
│   │   │   ├── dto/              # Data Transfer Objects
│   │   │   ├── exception/        # Manejo de excepciones
│   │   │   └── util/             # Utilidades
│   │   └── resources/
│   │       └── application.properties
│   └── test/                     # Tests unitarios e integración
├── docker-compose.yml            # Configuración Docker
├── .env.example                  # Variables de entorno ejemplo
└── pom.xml                       # Dependencias Maven
```

## 🛠️ Stack Tecnológico

### Backend
- **Spring Boot 3.5.7** - Framework principal
- **Spring Data JPA** - ORM y persistencia
- **Spring Security** - Autenticación y autorización
- **Spring Validation** - Validación de datos
- **Hibernate 6.6.33** - Implementación JPA

### Base de Datos
- **PostgreSQL 16.10** - Base de datos relacional

### Autenticación
- **JWT (JSON Web Tokens)** - Autenticación stateless
- **BCrypt** - Encriptación de contraseñas

### Documentación
- **SpringDoc OpenAPI 3** - Generación automática de docs
- **Swagger UI** - Interfaz interactiva de API

### Herramientas
- **Lombok** - Reducción de boilerplate
- **Maven** - Gestión de dependencias
- **Docker Compose** - Orquestación de contenedores

## 📖 Documentación Adicional

Para entender el modelo de negocio completo, consulta:

1. **[COMO_FUNCIONA.MD](docs/COMO_FUNCIONA.MD)** - Arquitectura y flujos del sistema
2. **[USER_STORIES.MD](docs/USER_STORIES.MD)** - 25 historias de usuario
3. **[REGLAS_NEGOCIO.MD](docs/REGLAS_NEGOCIO.MD)** - 24 reglas de negocio críticas

## 🧪 Testing

```bash
# Ejecutar todos los tests
mvn test

# Ejecutar tests con cobertura
mvn test jacoco:report
```

## 👥 Equipo de Desarrollo

- **Módulo 1 (Auth y Perfiles)**: Leonel Alzamora
- **Módulo 2 (Biblioteca Contenido)**: Fabian Rojas
- **Módulo 3 (Planes y Rutinas)**: Jhamil Peña
- **Módulo 4 (Exploración)**: Gonzalo Huaranga, Victor Carranza
- **Módulo 5 (Seguimiento)**: Leonel Alzamora

## 📝 Licencia

Este es un proyecto académico desarrollado en la Universidad Peruana de Ciencias.

---

**Última actualización:** Noviembre 2025
