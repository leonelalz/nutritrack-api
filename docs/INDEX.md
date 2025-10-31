# Índice de Documentación - NutriTrack API 📚

Guía completa de toda la documentación del proyecto.

## 🚀 Inicio Rápido

- **[Inicio Rápido (QUICKSTART.md)](QUICKSTART.md)** - Poner en marcha el proyecto en 5 minutos
- **[README Principal](../README.md)** - Descripción general del proyecto
- **[Glosario (GLOSSARY.md)](GLOSSARY.md)** - Términos y conceptos importantes

## 👥 Para Colaboradores

- **[Guía de Contribución (CONTRIBUTING.md)](../CONTRIBUTING.md)** - Cómo contribuir al proyecto
- **[Asignación de Módulos (TEAM_ASSIGNMENTS.md)](TEAM_ASSIGNMENTS.md)** - Responsables y tareas por módulo
- **[Guía de Desarrollo (DEVELOPMENT_GUIDE.md)](DEVELOPMENT_GUIDE.md)** - Estándares y mejores prácticas

## 🏗️ Arquitectura

- **[Arquitectura del Sistema (architecture/ARCHITECTURE.md)](architecture/ARCHITECTURE.md)** - Diseño y decisiones técnicas
  - Diagrama de capas
  - Patrones de diseño
  - Flujos de datos
  - Modelo de datos

## 📦 Módulos

### Documentación Detallada por Módulo

Organización basada en **User Stories** (25 historias de usuario en total):

1. **[Gestión de Cuentas y Preferencias](modules/cuentas-preferencias.md)** (Pendiente)
   - US-01 a US-05: Crear cuenta, login, preferencias, perfil de salud
   - Responsable: Leonel Alzamora

2. **[Biblioteca de Contenido (Admin)](modules/biblioteca-contenido.md)** (Pendiente)
   - US-06 a US-10: Etiquetas, ingredientes, ejercicios, comidas, recetas
   - Responsables: Fabian Rojas, Gonzalo Huaranga, Victor Carranza

3. **[Gestor de Catálogo (Admin)](modules/gestor-catalogo.md)** (Pendiente)
   - US-11 a US-15: Metas del catálogo, actividades, rutinas
   - Responsables: Gonzalo Huaranga, Victor Carranza

4. **[Exploración y Activación (Cliente)](modules/exploracion-activacion.md)** (Pendiente)
   - US-16 a US-20: Ver catálogo, activar metas, pausar/reanudar
   - Responsables: Gonzalo Huaranga, Victor Carranza

5. **[Seguimiento de Progreso (Cliente)](modules/seguimiento-progreso.md)** (Pendiente)
   - US-21 a US-25: Actividades, mediciones, gráficos, reportes
   - Responsables: Gonzalo Huaranga, Jhamil Peña, Victor Carranza

### Documentación de Referencia (Técnica)

- **[Autenticación y Seguridad (auth.md)](modules/auth.md)** - Implementación JWT y seguridad
- **[Sistema de Etiquetas (etiquetas.md)](modules/etiquetas.md)** - Sistema de etiquetado genérico

### Plantilla para Nuevos Módulos

- **[Plantilla de Módulo (modules/MODULE_TEMPLATE.md)](modules/MODULE_TEMPLATE.md)** - Template para documentar módulos

## 🚀 Despliegue

- **[Guía de Despliegue (deployment/DEPLOYMENT_GUIDE.md)](deployment/DEPLOYMENT_GUIDE.md)**
  - Configuración de ambientes
  - Despliegue local
  - Despliegue en desarrollo
  - Despliegue en producción
  - Docker y contenedores
  - Monitoreo y logs
  - Rollback y troubleshooting

## 🧪 Testing

- **[Guía de Testing con Postman (testing/POSTMAN_GUIDE.md)](testing/POSTMAN_GUIDE.md)**
  - Configuración de colecciones por módulo
  - Environments (Local, Dev, Staging, Prod)
  - Scripts de automatización
  - 27 endpoints documentados
  
- **[Colecciones Postman (../postman/)](../postman/)**
  - 5 colecciones organizadas por módulo
  - Environments configurados
  - Listos para importar y usar

- **Guía de Testing Unitario** (Pendiente)
  - Tests unitarios con JUnit
  - Tests de integración
  - Cobertura de código
  - Mocks y fixtures

## 📋 Templates GitHub

### Pull Requests

- **[Template de Pull Request (.github/PULL_REQUEST_TEMPLATE.md)](../.github/PULL_REQUEST_TEMPLATE.md)**
  - Estructura de PRs
  - Checklist de revisión
  - Información requerida

### Issues

- **[Bug Report (.github/ISSUE_TEMPLATE/bug_report.md)](../.github/ISSUE_TEMPLATE/bug_report.md)**
  - Reporte de errores
  - Información de reproducción

- **[Feature Request (.github/ISSUE_TEMPLATE/feature_request.md)](../.github/ISSUE_TEMPLATE/feature_request.md)**
  - Solicitud de nuevas funcionalidades
  - Criterios de aceptación

## 📊 Gestión de Proyecto

- **[Asignación de Módulos (TEAM_ASSIGNMENTS.md)](TEAM_ASSIGNMENTS.md)**
  - Distribución de responsabilidades
  - Cronograma de desarrollo
  - Dependencias entre módulos
  - Métricas de progreso

## 🔧 Configuración

### Archivos de Configuración

```
src/main/resources/
├── application.properties              # Configuración base
├── application-local.properties        # Desarrollo local
├── application-dev.properties          # Ambiente desarrollo
├── application-staging.properties      # Ambiente staging
└── application-prod.properties         # Producción
```

### Variables de Entorno

Ver sección de configuración en [DEPLOYMENT_GUIDE.md](deployment/DEPLOYMENT_GUIDE.md#configuración)

## 📖 API Reference

- **[Referencia de API (API_REFERENCE.md)](API_REFERENCE.md)**
  - 27 endpoints documentados (US-01 a US-25)
  - Schemas de request/response completos
  - Códigos HTTP y errores
  - Ejemplos con curl
  - Autenticación y headers
  - Reglas de negocio aplicables

## 🔍 Búsqueda Rápida

### Por Tema

| Tema | Documento |
|------|-----------|  
| Instalar proyecto | [QUICKSTART.md](QUICKSTART.md) |
| Crear Pull Request | [CONTRIBUTING.md](../CONTRIBUTING.md) |
| Configurar seguridad | [modules/auth.md](modules/auth.md) |
| Desplegar en producción | [deployment/DEPLOYMENT_GUIDE.md](deployment/DEPLOYMENT_GUIDE.md) |
| Estándares de código | [DEVELOPMENT_GUIDE.md](DEVELOPMENT_GUIDE.md) |
| Arquitectura del sistema | [architecture/ARCHITECTURE.md](architecture/ARCHITECTURE.md) |
| Etiquetas y categorización | [modules/etiquetas.md](modules/etiquetas.md) |
| Templates de GitHub | [.github/](../.github/) |
| **Probar API con Postman** | **[testing/POSTMAN_GUIDE.md](testing/POSTMAN_GUIDE.md)** |
| **Referencia de API REST** | **[API_REFERENCE.md](API_REFERENCE.md)** |### Por Rol

#### Nuevo Desarrollador
1. [QUICKSTART.md](QUICKSTART.md) - Configurar proyecto
2. [README.md](../README.md) - Entender el proyecto
3. [CONTRIBUTING.md](../CONTRIBUTING.md) - Cómo contribuir
4. [TEAM_ASSIGNMENTS.md](TEAM_ASSIGNMENTS.md) - Ver tu módulo asignado
5. [DEVELOPMENT_GUIDE.md](DEVELOPMENT_GUIDE.md) - Estándares de código

#### Responsable de Módulo
1. [modules/MODULE_TEMPLATE.md](modules/MODULE_TEMPLATE.md) - Plantilla de documentación
2. [DEVELOPMENT_GUIDE.md](DEVELOPMENT_GUIDE.md) - Guía de desarrollo
3. [architecture/ARCHITECTURE.md](architecture/ARCHITECTURE.md) - Arquitectura
4. Documentación específica de tu módulo en [modules/](modules/)

#### DevOps / Deployment
1. [deployment/DEPLOYMENT_GUIDE.md](deployment/DEPLOYMENT_GUIDE.md) - Guía completa de despliegue
2. [architecture/ARCHITECTURE.md](architecture/ARCHITECTURE.md) - Arquitectura del sistema
3. Configuraciones en `src/main/resources/`

#### Project Manager
1. [TEAM_ASSIGNMENTS.md](TEAM_ASSIGNMENTS.md) - Estado del equipo
2. [README.md](../README.md) - Visión general
3. [.github/](../.github/) - Templates de issues y PRs

## 📝 Checklist de Documentación

### Al Crear un Nuevo Módulo
- [ ] Copiar [MODULE_TEMPLATE.md](modules/MODULE_TEMPLATE.md)
- [ ] Documentar API endpoints
- [ ] Documentar modelo de datos
- [ ] Añadir ejemplos de uso
- [ ] Documentar validaciones
- [ ] Añadir casos de prueba
- [ ] Actualizar [TEAM_ASSIGNMENTS.md](TEAM_ASSIGNMENTS.md)

### Al Modificar Funcionalidad Existente
- [ ] Actualizar documentación del módulo
- [ ] Actualizar CHANGELOG (si existe)
- [ ] Actualizar ejemplos si cambian
- [ ] Revisar links rotos

### Antes de Release
- [ ] Verificar toda la documentación
- [ ] Actualizar versiones
- [ ] Generar documentación de API
- [ ] Revisar guía de despliegue
- [ ] Actualizar README con cambios importantes

## 🔄 Mantenimiento de Documentación

### Responsabilidades

- **Cada Desarrollador:** Documentar su módulo
- **Tech Lead:** Revisar arquitectura y decisiones técnicas
- **DevOps:** Mantener guías de despliegue actualizadas
- **Todos:** Reportar documentación desactualizada

### Frecuencia de Revisión

- **Semanal:** Actualizar TEAM_ASSIGNMENTS.md
- **Por Sprint:** Revisar documentación de módulos
- **Por Release:** Revisión completa de toda la documentación
- **Cuando sea necesario:** Correcciones y mejoras

## 📞 Contacto y Soporte

### Reportar Problemas en Documentación

- Crear issue en GitHub con label `documentation`
- Mencionar al responsable del módulo
- Sugerir corrección si es posible

### Proponer Mejoras

- Discutir en reuniones de equipo
- Crear PR con los cambios propuestos
- Solicitar revisión

## 🎯 Próximos Pasos

### Documentación Pendiente

- [ ] Guía de Testing completa
- [ ] Documentación de API (Swagger/OpenAPI)
- [ ] Módulo de Perfil de Usuario
- [ ] Módulo de Salud e Historial
- [ ] Módulo de Infraestructura
- [ ] Guía de Monitoreo y Observabilidad
- [ ] Guía de Performance
- [ ] CHANGELOG

### Mejoras Planificadas

- [ ] Diagramas interactivos de arquitectura
- [ ] Videos tutoriales
- [ ] Ejemplos de código más completos
- [ ] FAQ por módulo
- [ ] Guía de troubleshooting común

---

## 📂 Estructura de Carpetas

```
nutritrack-api/
├── README.md                          # Descripción general
├── CONTRIBUTING.md                    # Guía de contribución
├── .github/                           # Templates GitHub
│   ├── PULL_REQUEST_TEMPLATE.md
│   └── ISSUE_TEMPLATE/
│       ├── bug_report.md
│       └── feature_request.md
├── docs/                              # Documentación principal
│   ├── INDEX.md                       # Este archivo
│   ├── QUICKSTART.md                  # Inicio rápido
│   ├── GLOSSARY.md                    # Glosario
│   ├── DEVELOPMENT_GUIDE.md           # Guía de desarrollo
│   ├── TEAM_ASSIGNMENTS.md            # Asignaciones de equipo
│   ├── architecture/
│   │   └── ARCHITECTURE.md            # Arquitectura del sistema
│   ├── modules/                       # Documentación por módulo
│   │   ├── MODULE_TEMPLATE.md         # Template
│   │   ├── auth.md                    # Autenticación
│   │   ├── etiquetas.md               # Etiquetas
│   │   ├── perfil-usuario.md          # Perfil (pendiente)
│   │   ├── salud-historial.md         # Salud (pendiente)
│   │   └── infraestructura.md         # Infra (pendiente)
│   ├── deployment/
│   │   └── DEPLOYMENT_GUIDE.md        # Guía de despliegue
│   └── api/                           # Referencia API (pendiente)
└── src/                               # Código fuente
    └── main/
        └── resources/
            └── application*.properties # Configuraciones
```

---

**Última actualización:** Octubre 2025  
**Mantenido por:** Equipo NutriTrack

**¿Falta algo?** Crea un issue o PR para mejorar esta documentación.
