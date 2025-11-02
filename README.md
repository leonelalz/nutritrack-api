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

| Módulo | Estado | Progreso | Última Actualización |
|--------|--------|----------|----------------------|
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

```bash
# Probar endpoint de salud (sin autenticación)
curl http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"Test123!","nombre":"Test User"}'
```

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

> ⚠️ **Nota:** La seguridad JWT está temporalmente deshabilitada para facilitar las pruebas iniciales. Se reactivará en versión 0.2.0.

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

El sistema está dividido en 5 módulos principales basados en las User Stories:

### ✅ 1️⃣ Módulo de Gestión de Cuentas y Preferencias [COMPLETADO]
**Responsable:** Leonel Alzamora  
**User Stories:** US-01 a US-05 (5/5 implementadas)  
**Branch:** `feature/modulo-1-cuentas-preferencias`

**Funcionalidades implementadas:**
- ✅ Registro de usuario (US-01)
- ✅ Inicio de sesión con JWT (US-02)
- ✅ Configuración de unidades de medida KG/LBS (US-03)
- ✅ Edición de perfil y etiquetas de salud (US-04)
- ✅ Eliminación de cuenta (soft delete) (US-05)

**Endpoints:**
- `POST /api/v1/auth/register` - Crear cuenta
- `POST /api/v1/auth/login` - Autenticar usuario
- `GET /api/v1/app/profile` - Obtener perfil
- `PUT /api/v1/app/profile` - Actualizar perfil
- `DELETE /api/v1/app/profile` - Eliminar cuenta

**Testing:** [testing/test-module1.ps1](testing/test-module1.ps1) | [TESTING_MODULE1.md](TESTING_MODULE1.md)

---

### 🚧 2️⃣ Módulo de Biblioteca de Contenido (Admin)
**Responsables:** Fabian Rojas, Gonzalo Huaranga, Victor Carranza  
**User Stories:** US-06 a US-10  
**Estado:** Pendiente

- Gestión de etiquetas maestras (US-06)
- Gestión de ingredientes (US-07)
- Gestión de ejercicios (US-08)
- Gestión de comidas (US-09)
- Gestión de recetas (US-10)

---

### 🚧 3️⃣ Módulo de Gestor de Catálogo (Admin)
**Responsables:** Gonzalo Huaranga, Victor Carranza  
**User Stories:** US-11 a US-15  
**Estado:** Pendiente

- Crear y gestionar metas del catálogo (US-11, US-12)
- Ver y eliminar metas (US-13, US-14)
- Ensamblar rutinas de ejercicio (US-15)

---

### 🚧 4️⃣ Módulo de Exploración y Activación (Cliente)
**Responsables:** Gonzalo Huaranga, Victor Carranza  
**User Stories:** US-16 a US-20  
**Estado:** Pendiente

- Ver catálogo con filtros personalizados (US-16)
- Ver detalle de metas (US-17)
- Activar, pausar y gestionar metas (US-18, US-19, US-20)

---

### 🚧 5️⃣ Módulo de Seguimiento de Progreso (Cliente)
**Responsables:** Gonzalo Huaranga, Jhamil Peña, Victor Carranza  
**User Stories:** US-21 a US-25  
**Estado:** Pendiente

- Ver y marcar actividades del plan (US-21, US-22, US-23)
- Registrar mediciones corporales (US-24)
- Ver gráficos y reportes de progreso (US-25)

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

| Módulo | Responsable(s) | User Stories | Email |
|--------|----------------|--------------|-------|
| Gestión de Cuentas y Preferencias | Leonel Alzamora | US-01 a US-05 | email@example.com |
| Biblioteca de Contenido (Admin) | Fabian Rojas, Gonzalo Huaranga, Victor Carranza | US-06 a US-10 | email@example.com |
| Gestor de Catálogo (Admin) | Gonzalo Huaranga, Victor Carranza | US-11 a US-15 | email@example.com |
| Exploración y Activación | Gonzalo Huaranga, Victor Carranza | US-16 a US-20 | email@example.com |
| Seguimiento de Progreso | Gonzalo Huaranga, Jhamil Peña, Victor Carranza | US-21 a US-25 | email@example.com |

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
