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

El sistema está dividido en 5 módulos principales basados en las User Stories:

### 1️⃣ Módulo de Gestión de Cuentas y Preferencias
**Responsable:** Leonel Alzamora  
**User Stories:** US-01 a US-05
- Creación y gestión de cuentas (US-01, US-02)
- Configuración de unidades de medida (US-03)
- Edición de perfil de salud (US-04)
- Eliminación de cuenta (US-05)
- **Documentación:** [docs/modules/cuentas-preferencias.md](docs/modules/cuentas-preferencias.md)

### 2️⃣ Módulo de Biblioteca de Contenido (Admin)
**Responsables:** Fabian Rojas, Gonzalo Huaranga, Victor Carranza  
**User Stories:** US-06 a US-10
- Gestión de etiquetas maestras (US-06)
- Gestión de ingredientes (US-07)
- Gestión de ejercicios (US-08)
- Gestión de comidas (US-09)
- Gestión de recetas (US-10)
- **Documentación:** [docs/modules/biblioteca-contenido.md](docs/modules/biblioteca-contenido.md)

### 3️⃣ Módulo de Gestor de Catálogo (Admin)
**Responsables:** Gonzalo Huaranga, Victor Carranza  
**User Stories:** US-11 a US-15
- Crear y gestionar metas del catálogo (US-11, US-12)
- Ver y eliminar metas (US-13, US-14)
- Ensamblar rutinas de ejercicio (US-15)
- **Documentación:** [docs/modules/gestor-catalogo.md](docs/modules/gestor-catalogo.md)

### 4️⃣ Módulo de Exploración y Activación (Cliente)
**Responsables:** Gonzalo Huaranga, Victor Carranza  
**User Stories:** US-16 a US-20
- Ver catálogo con filtros personalizados (US-16)
- Ver detalle de metas (US-17)
- Activar, pausar y gestionar metas (US-18, US-19, US-20)
- **Documentación:** [docs/modules/exploracion-activacion.md](docs/modules/exploracion-activacion.md)

### 5️⃣ Módulo de Seguimiento de Progreso (Cliente)
**Responsables:** Gonzalo Huaranga, Jhamil Peña, Victor Carranza  
**User Stories:** US-21 a US-25
- Ver y marcar actividades del plan (US-21, US-22, US-23)
- Registrar mediciones corporales (US-24)
- Ver gráficos y reportes de progreso (US-25)
- **Documentación:** [docs/modules/seguimiento-progreso.md](docs/modules/seguimiento-progreso.md)

## 📚 Documentación

- **[Guía de Contribución](CONTRIBUTING.md)** - Cómo contribuir al proyecto
- **[Arquitectura del Sistema](docs/architecture/ARCHITECTURE.md)** - Diseño y decisiones técnicas
- **[Guía de Desarrollo](docs/DEVELOPMENT_GUIDE.md)** - Estándares y mejores prácticas
- **[Guía de Despliegue](docs/deployment/DEPLOYMENT_GUIDE.md)** - Instrucciones de deployment
- **[API Reference](docs/API_REFERENCE.md)** - Referencia completa de endpoints
- **[Testing con Postman](docs/testing/POSTMAN_GUIDE.md)** - Guía de pruebas API
- **[Colecciones Postman](postman/)** - Colecciones y environments listos para usar

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
