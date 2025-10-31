# NutriTrack API 🥗

API REST para la gestión de seguimiento nutricional y planes de salud personalizados.

## 📋 Tabla de Contenidos

- [Descripción](#descripción)
- [Tecnologías](#tecnologías)
- [Estructura del Proyecto](#estructura-del-proyecto)
- [Configuración del Entorno](#configuración-del-entorno)
- [Instalación y Ejecución](#instalación-y-ejecución)
- [Módulos del Sistema](#módulos-del-sistema)
- [Documentación](#documentación)
- [Contribuir](#contribuir)
- [Equipo de Desarrollo](#equipo-de-desarrollo)

## 🎯 Descripción

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

## ⚙️ Configuración del Entorno

### Prerrequisitos

- JDK 17 o superior
- Maven 3.8+
- MySQL 8.0+ o PostgreSQL 13+
- IDE (IntelliJ IDEA, Eclipse, VS Code)

### Variables de Entorno

Crear archivo `application-local.properties` en `src/main/resources/`:

```properties
# Base de datos
spring.datasource.url=jdbc:mysql://localhost:3306/nutritrack_db
spring.datasource.username=tu_usuario
spring.datasource.password=tu_password

# JWT
jwt.secret=tu_clave_secreta_muy_larga_y_segura
jwt.expiration=86400000

# Puerto
server.port=8080
```

## 🚀 Instalación y Ejecución

### Clonar el repositorio
```bash
git clone https://github.com/leonelalz/nutritrack-api.git
cd nutritrack-api
```

### Compilar el proyecto
```bash
./mvnw clean install
```

### Ejecutar la aplicación
```bash
./mvnw spring-boot:run
```

### Ejecutar tests
```bash
./mvnw test
```

La API estará disponible en: `http://localhost:8080`

## 🧩 Módulos del Sistema

El sistema está dividido en 5 módulos principales:

### 1️⃣ Módulo de Autenticación y Seguridad
**Responsable:** [Nombre del desarrollador]
- Gestión de cuentas de usuario
- Autenticación JWT
- Control de roles y permisos
- **Documentación:** [docs/modules/auth.md](docs/modules/auth.md)

### 2️⃣ Módulo de Perfil de Usuario
**Responsable:** [Nombre del desarrollador]
- Gestión de perfiles
- Información personal
- **Documentación:** [docs/modules/perfil-usuario.md](docs/modules/perfil-usuario.md)

### 3️⃣ Módulo de Salud e Historial
**Responsable:** [Nombre del desarrollador]
- Perfiles de salud
- Historial de medidas corporales
- **Documentación:** [docs/modules/salud-historial.md](docs/modules/salud-historial.md)

### 4️⃣ Módulo de Etiquetas
**Responsable:** [Nombre del desarrollador]
- Etiquetas de ingredientes
- Etiquetas de ejercicios
- Etiquetas de metas y planes
- **Documentación:** [docs/modules/etiquetas.md](docs/modules/etiquetas.md)

### 5️⃣ Módulo de Infraestructura y Configuración
**Responsable:** [Nombre del desarrollador]
- Configuración general
- CORS y seguridad global
- Manejo de excepciones
- **Documentación:** [docs/modules/infraestructura.md](docs/modules/infraestructura.md)

## 📚 Documentación

- **[Guía de Contribución](CONTRIBUTING.md)** - Cómo contribuir al proyecto
- **[Arquitectura del Sistema](docs/architecture/ARCHITECTURE.md)** - Diseño y decisiones técnicas
- **[Guía de Desarrollo](docs/DEVELOPMENT_GUIDE.md)** - Estándares y mejores prácticas
- **[Guía de Despliegue](docs/deployment/DEPLOYMENT_GUIDE.md)** - Instrucciones de deployment
- **[API Documentation](docs/api/API_REFERENCE.md)** - Referencia de endpoints

## 👥 Equipo de Desarrollo

| Módulo | Responsable | Email |
|--------|-------------|-------|
| Autenticación y Seguridad | [Nombre] | email@example.com |
| Perfil de Usuario | [Nombre] | email@example.com |
| Salud e Historial | [Nombre] | email@example.com |
| Etiquetas | [Nombre] | email@example.com |
| Infraestructura | [Nombre] | email@example.com |

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
