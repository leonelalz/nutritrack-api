# 🥗 NutriTrack API

API REST para sistema de coaching nutricional y fitness desarrollada con Spring Boot 3.5.7 y Java 21.

[![Java Version](https://img.shields.io/badge/Java-21-orange)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.7-green)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16.10-blue)](https://www.postgresql.org/)
[![Tests](https://img.shields.io/badge/Tests-97%2F97-success)](https://github.com)

## 📋 Tabla de Contenidos

- [Requisitos Previos](#requisitos-previos)
- [Instalación Rápida](#instalación-rápida)
- [Instalación Desde Cero](#instalación-desde-cero)
- [Configuración de Base de Datos](#configuración-de-base-de-datos)
- [Ejecución del Proyecto](#ejecución-del-proyecto)
- [Documentación API](#documentación-api)
- [Módulos Implementados](#módulos-implementados)
- [Estructura del Proyecto](#estructura-del-proyecto)
- [Stack Tecnológico](#stack-tecnológico)
- [Testing](#testing)

## 🔧 Requisitos Previos

Antes de comenzar, asegúrate de tener instalado:

- ✅ **Java 21** o superior ([Descargar OpenJDK](https://adoptium.net/))
- ✅ **Maven 3.8+** (incluido en IntelliJ IDEA)
- ✅ **Docker Desktop** ([Descargar](https://www.docker.com/products/docker-desktop))
- ✅ **Git** ([Descargar](https://git-scm.com/downloads))
- ✅ **IntelliJ IDEA** Community o Ultimate (recomendado)

### Verificar instalación:

```powershell
# Verificar Java
java -version  # Debe mostrar Java 21

# Verificar Maven (desde IntelliJ o instalado)
mvn -version

# Verificar Docker
docker --version
docker-compose --version

# Verificar Git
git --version
```

## ⚡ Instalación Rápida

Si ya tienes todo configurado:

```powershell
# 1. Clonar repositorio
git clone https://github.com/leonelalz/nutritrack-api.git
cd nutritrack-API

# 2. Iniciar base de datos
docker-compose up -d

# 3. Ejecutar aplicación
.\mvnw.cmd spring-boot:run
```

🎉 **La aplicación estará disponible en:** http://localhost:8080

---

## 🔰 Instalación Desde Cero

### Paso 1: Instalar Java 21

1. Descarga [OpenJDK 21](https://adoptium.net/)
2. Ejecuta el instalador
3. Verifica: `java -version`
4. Configura `JAVA_HOME`:
   ```powershell
   # PowerShell (Administrador)
   [System.Environment]::SetEnvironmentVariable('JAVA_HOME', 'C:\Program Files\Eclipse Adoptium\jdk-21.0.8-hotspot', 'Machine')
   ```

### Paso 2: Instalar Docker Desktop

1. Descarga [Docker Desktop](https://www.docker.com/products/docker-desktop)
2. Ejecuta el instalador
3. Reinicia tu computadora
4. Abre Docker Desktop y espera a que inicie
5. Verifica: `docker --version`

### Paso 3: Instalar Git

1. Descarga [Git](https://git-scm.com/downloads)
2. Ejecuta el instalador (usa opciones por defecto)
3. Verifica: `git --version`

### Paso 4: Instalar IntelliJ IDEA (Opcional pero recomendado)

1. Descarga [IntelliJ IDEA](https://www.jetbrains.com/idea/download/)
2. Ejecuta el instalador (Community Edition es suficiente)
3. Abre IntelliJ y completa la configuración inicial

### Paso 5: Clonar el Proyecto

```powershell
# Abrir PowerShell o Git Bash
git clone https://github.com/leonelalz/nutritrack-api.git
cd nutritrack-API
```

### Paso 6: Abrir en IntelliJ IDEA

1. **File → Open** → Selecciona la carpeta `nutritrack-API`
2. Espera a que IntelliJ indexe el proyecto (esquina inferior derecha)
3. Cuando pregunte **"Trust Maven project?"** → Click en **"Trust Project"**
4. Espera a que Maven descargue todas las dependencias (puede tardar 5-10 minutos la primera vez)
5. Ve a **File → Project Structure → Project** y verifica que el **SDK sea Java 21**

## 🗄️ Configuración de Base de Datos

### Opción 1: Docker Compose (Recomendado) ⭐

**Desde PowerShell o Terminal:**

```powershell
# Asegúrate de estar en la carpeta del proyecto
cd nutritrack-API

# Inicia PostgreSQL con Docker
docker-compose up -d

# Verifica que esté corriendo
docker ps
```

Esto creará:
- **PostgreSQL 16.10** en `localhost:5432`
- **PgAdmin 4** en `http://localhost:5050`
- **Volumen persistente** para no perder datos

El script `SQL/NutriDB.sql` se ejecutará automáticamente al iniciar el contenedor por primera vez, creando:
- ✅ 15 tablas del sistema
- ✅ Roles de usuario (ADMIN, USER)
- ✅ Usuario administrador por defecto
- ✅ Usuario demo con datos de prueba

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

### Opción 1: Desde IntelliJ IDEA (Recomendado)

1. Asegúrate de que **Docker esté corriendo** y PostgreSQL iniciado
2. Abre el proyecto en IntelliJ IDEA
3. Espera a que Maven descargue las dependencias (barra inferior)
4. Navega a: `src/main/java/com/example/nutritrackapi/NutritrackApiApplication.java`
5. Click derecho → **Run 'NutritrackApiApplication'** (o presiona `Shift + F10`)

**Deberías ver en la consola:**
```
🚀 Iniciando NutriTrack API...
📝 Creando roles por defecto...
✅ Roles ya existen en la base de datos
✅ Usuario administrador ya existe
✅ Usuario demo ya existe
✅ Datos de demostración ya existen
✅ Aplicación lista!

Started NutritrackApiApplication in 9.844 seconds
Tomcat started on port 8080 (http) with context path '/'
```

### Opción 2: Desde PowerShell/Terminal

```powershell
# Asegúrate de estar en la carpeta del proyecto
cd nutritrack-API

# Compilar y ejecutar tests
.\mvnw.cmd clean install

# Ejecutar la aplicación
.\mvnw.cmd spring-boot:run

# O en una sola línea (sin tests)
.\mvnw.cmd spring-boot:run -DskipTests
```

### Opción 3: Ejecutar JAR compilado

```powershell
# Compilar el JAR
.\mvnw.cmd clean package -DskipTests

# Ejecutar el JAR
java -jar target\nutritrack-API-0.0.1-SNAPSHOT.jar
```

### ✅ Verificar que funciona

**Opción A - Navegador:**
Abre http://localhost:8080/api/v1/health

**Opción B - PowerShell:**
```powershell
curl http://localhost:8080/api/v1/health
```

**Respuesta esperada:**
```json
{
  "status": "UP",
  "service": "NutriTrack API",
  "timestamp": "2025-11-04T...",
  "version": "1.0.0",
  "environment": "development"
}
```

La aplicación estará disponible en: **http://localhost:8080** 🎉

## 📚 Documentación API

### Swagger UI (Interfaz Interactiva) 📖

Una vez iniciada la aplicación, accede a:

- **Swagger UI**: http://localhost:8080/swagger-ui/index.html
- **OpenAPI JSON**: http://localhost:8080/api-docs
- **Actuator Health**: http://localhost:8080/actuator/health

### Endpoints por Módulo

#### 🔐 Módulo 1: Autenticación y Perfiles

| Método | Endpoint | Descripción | Auth |
|--------|----------|-------------|------|
| POST | `/api/v1/auth/registro` | Crear cuenta nueva | ❌ |
| POST | `/api/v1/auth/login` | Iniciar sesión | ❌ |
| DELETE | `/api/v1/auth/eliminar-cuenta` | Eliminar cuenta | ✅ |
| GET | `/api/v1/perfil/mi-perfil` | Ver mi perfil | ✅ |
| PUT | `/api/v1/perfil/mi-perfil` | Actualizar perfil | ✅ |
| GET | `/api/v1/perfil/salud` | Ver perfil de salud | ✅ |
| PUT | `/api/v1/perfil/salud` | Actualizar perfil salud | ✅ |
| POST | `/api/v1/perfil/medidas` | Registrar medidas | ✅ |
| GET | `/api/v1/perfil/historial-medidas` | Ver historial | ✅ |

#### 📚 Módulo 2: Biblioteca de Contenido (Admin)

| Método | Endpoint | Descripción | Rol |
|--------|----------|-------------|-----|
| GET | `/api/admin/ingredientes` | Listar ingredientes | ADMIN |
| POST | `/api/admin/ingredientes` | Crear ingrediente | ADMIN |
| PUT | `/api/admin/ingredientes/{id}` | Actualizar ingrediente | ADMIN |
| DELETE | `/api/admin/ingredientes/{id}` | Eliminar ingrediente | ADMIN |
| GET | `/api/admin/comidas` | Listar comidas | ADMIN |
| POST | `/api/admin/comidas` | Crear comida | ADMIN |
| GET | `/api/admin/ejercicios` | Listar ejercicios | ADMIN |
| POST | `/api/admin/ejercicios` | Crear ejercicio | ADMIN |
| GET | `/api/admin/etiquetas` | Listar etiquetas | ADMIN |
| POST | `/api/admin/etiquetas` | Crear etiqueta | ADMIN |

#### 🎯 Módulo 3: Planes y Rutinas (Admin)

| Método | Endpoint | Descripción | Rol |
|--------|----------|-------------|-----|
| GET | `/api/admin/planes` | Listar planes | ADMIN |
| POST | `/api/admin/planes` | Crear plan | ADMIN |
| GET | `/api/admin/planes/{id}` | Ver detalle plan | ADMIN |
| PUT | `/api/admin/planes/{id}` | Actualizar plan | ADMIN |
| DELETE | `/api/admin/planes/{id}` | Eliminar plan | ADMIN |
| GET | `/api/admin/rutinas` | Listar rutinas | ADMIN |
| POST | `/api/admin/rutinas` | Crear rutina | ADMIN |
| GET | `/api/admin/rutinas/{id}` | Ver detalle rutina | ADMIN |
| PUT | `/api/admin/rutinas/{id}` | Actualizar rutina | ADMIN |
| DELETE | `/api/admin/rutinas/{id}` | Eliminar rutina | ADMIN |

#### 🔍 Módulo 4: Exploración y Activación (Cliente)

| Método | Endpoint | Descripción | Auth |
|--------|----------|-------------|------|
| GET | `/api/admin/planes/catalogo` | Catálogo de planes | ✅ |
| GET | `/api/admin/planes/catalogo/{id}` | Detalle de plan | ✅ |
| GET | `/api/admin/rutinas/catalogo` | Catálogo de rutinas | ✅ |
| GET | `/api/admin/rutinas/catalogo/{id}` | Detalle de rutina | ✅ |
| POST | `/api/v1/usuario/planes/activar` | Activar plan | USER |
| PATCH | `/api/v1/usuario/planes/{id}/pausar` | Pausar plan | USER |
| PATCH | `/api/v1/usuario/planes/{id}/reanudar` | Reanudar plan | USER |
| PATCH | `/api/v1/usuario/planes/{id}/completar` | Completar plan | USER |
| PATCH | `/api/v1/usuario/planes/{id}/cancelar` | Cancelar plan | USER |
| GET | `/api/v1/usuario/planes/activo` | Plan activo actual | USER |
| GET | `/api/v1/usuario/planes` | Mis planes | USER |
| POST | `/api/v1/usuario/rutinas/activar` | Activar rutina | USER |
| PATCH | `/api/v1/usuario/rutinas/{id}/pausar` | Pausar rutina | USER |
| GET | `/api/v1/usuario/rutinas/activa` | Rutina activa actual | USER |

### 👤 Usuarios de Prueba

**Administrador:**
```json
{
  "email": "admin@nutritrack.com",
  "password": "Admin123!"
}
```

**Usuario Demo:**
```json
{
  "email": "user@demo.com",
  "password": "Demo123!"
}
```

## 📦 Módulos Implementados

### ✅ Módulo 1: Autenticación y Gestión de Perfiles
- **Registro y Login** con JWT
- **Gestión de Perfiles** de usuario
- **Perfil de Salud** (objetivo, nivel actividad)
- **Historial de Medidas** corporales
- **Tests:** 13/13 ✅

### ✅ Módulo 2: Biblioteca de Contenido
- **CRUD de Ingredientes** (calorías, macros, vitaminas)
- **CRUD de Comidas** con recetas
- **CRUD de Ejercicios** (tipo, intensidad, quema calórica)
- **CRUD de Etiquetas** (categorización)
- **Tests:** 39/39 ✅

### ✅ Módulo 3: Gestión de Planes y Rutinas
- **CRUD de Planes Alimenticios** (días, comidas, objetivos)
- **CRUD de Rutinas de Ejercicio** (semanas, ejercicios, intensidad)
- **Asignación de Objetivos** a planes
- **Gestión de Días y Semanas**
- **Tests:** 33/33 ✅

### ✅ Módulo 4: Exploración y Activación
- **Catálogo de Planes** con filtros (sugeridos por objetivo)
- **Catálogo de Rutinas** con filtros
- **Activación de Planes/Rutinas** por usuario
- **Gestión de Estados** (ACTIVO, PAUSADO, COMPLETADO, CANCELADO)
- **Validaciones de Negocio** (no duplicados, transiciones)
- **Tests:** En desarrollo 🚧

### 🚧 Módulo 5: Seguimiento Diario (Pendiente)
- Registro de comidas consumidas
- Registro de ejercicios realizados
- Progreso diario del plan
- Cumplimiento de objetivos

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

### Ejecutar Tests

```powershell
# Ejecutar todos los tests
.\mvnw.cmd test

# Ejecutar con cobertura
.\mvnw.cmd test jacoco:report

# Ver reporte de cobertura
start target\site\jacoco\index.html
```

### Estado Actual de Tests

| Módulo | Tests | Estado |
|--------|-------|--------|
| Autenticación (AuthService) | 13/13 | ✅ |
| Biblioteca - Ingredientes | 9/9 | ✅ |
| Biblioteca - Comidas | 9/9 | ✅ |
| Biblioteca - Ejercicios | 9/9 | ✅ |
| Biblioteca - Etiquetas | 12/12 | ✅ |
| Perfiles (PerfilService) | 11/11 | ✅ |
| Planes (PlanService) | 16/16 | ✅ |
| Rutinas (RutinaService) | 17/17 | ✅ |
| Application Context | 1/1 | ✅ |
| **TOTAL** | **97/97** | **✅ 100%** |

## 🚨 Solución de Problemas

### Error: "Port 8080 already in use"

```powershell
# Ver qué proceso usa el puerto
netstat -ano | findstr :8080

# Matar el proceso (reemplaza XXXX con el PID)
taskkill /F /PID XXXX
```

### Error: "Cannot connect to database"

```powershell
# Verificar que Docker esté corriendo
docker ps

# Si no aparece nutritrack-postgres, iniciar:
docker-compose up -d

# Ver logs de PostgreSQL
docker-compose logs -f postgres

# Reiniciar completamente
docker-compose down
docker-compose up -d
```

### Error: "Cannot resolve symbol 'jakarta'"

1. Click derecho en proyecto → **Maven → Reload Project**
2. **File → Invalidate Caches → Invalidate and Restart**
3. Verificar que SDK sea Java 21: **File → Project Structure → Project**

### Error: "Tests failing"

```powershell
# Limpiar y recompilar
.\mvnw.cmd clean install

# Ejecutar tests individualmente
.\mvnw.cmd test -Dtest=AuthServiceTest
```

### Error: "Maven wrapper not found"

```powershell
# Descargar Maven wrapper
mvn -N io.takari:maven:wrapper

# O usar Maven instalado
mvn spring-boot:run
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
