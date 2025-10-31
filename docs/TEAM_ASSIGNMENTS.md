# Asignación de Módulos - NutriTrack API

**Fecha de actualización:** Octubre 2025

## 📊 Distribución de Módulos

| # | Módulo | Responsable | Email | Estado | Progreso |
|---|--------|-------------|-------|--------|----------|
| 1 | **Autenticación y Seguridad** | [Nombre] | email@example.com | 🟢 En progreso | 60% |
| 2 | **Perfil de Usuario** | [Nombre] | email@example.com | 🟡 Planificado | 20% |
| 3 | **Salud e Historial** | [Nombre] | email@example.com | 🟢 En progreso | 45% |
| 4 | **Etiquetas** | [Nombre] | email@example.com | 🟡 Planificado | 15% |
| 5 | **Infraestructura y Config** | [Nombre] | email@example.com | 🟢 En progreso | 70% |

**Leyenda:**
- 🟢 En progreso
- 🟡 Planificado
- 🔴 Bloqueado
- ✅ Completado

## 📋 Responsabilidades por Módulo

### 1️⃣ Autenticación y Seguridad

**Responsable:** [Nombre del Desarrollador]

**Componentes:**
- `CuentaAuth` - Gestión de cuentas
- `Rol` - Roles y permisos
- `JwtUtil` - Utilidades JWT
- `JwtAuthenticationFilter` - Filtro de seguridad
- `UserDetailsServiceImpl` - Carga de usuarios
- `SecurityConfig` - Configuración Spring Security
- `AuthController` - Endpoints de autenticación
- `CuentaAuthController` - Gestión de cuentas

**Tareas Principales:**
- [ ] Implementar registro de usuarios
- [ ] Implementar login con JWT
- [ ] Configurar roles (USER, ADMIN, NUTRITIONIST)
- [ ] Implementar refresh token
- [ ] Implementar cambio de contraseña
- [ ] Tests de seguridad

**Documentación:** [docs/modules/auth.md](docs/modules/auth.md)

---

### 2️⃣ Perfil de Usuario

**Responsable:** [Nombre del Desarrollador]

**Componentes:**
- `PerfilUsuario` - Entidad de perfil
- `PerfilUsuarioRepository` - Acceso a datos
- `PerfilUsuarioService` - Lógica de negocio
- `PerfilUsuarioController` - Endpoints REST

**Tareas Principales:**
- [ ] Crear modelo de perfil de usuario
- [ ] CRUD de perfiles
- [ ] Asociación con cuenta de autenticación
- [ ] Validaciones de datos personales
- [ ] Subida de foto de perfil
- [ ] Tests unitarios e integración

**Documentación:** [docs/modules/perfil-usuario.md](docs/modules/perfil-usuario.md)

---

### 3️⃣ Salud e Historial

**Responsable:** [Nombre del Desarrollador]

**Componentes:**
- `UsuarioPerfilSalud` - Perfil de salud
- `UsuarioHistorialMedida` - Historial de medidas
- `UsuarioPerfilSaludRepository`
- `UsuarioHistorialMedidaRepository`
- `UsuarioPerfilSaludService`
- `UsuarioHistorialMedidaService`

**Tareas Principales:**
- [ ] Modelo de perfil de salud (objetivos, nivel actividad)
- [ ] Registro de medidas (peso, altura, IMC)
- [ ] Historial temporal de medidas
- [ ] Cálculo automático de IMC
- [ ] Gráficas de progreso
- [ ] Alertas de objetivos

**Documentación:** [docs/modules/salud-historial.md](docs/modules/salud-historial.md)

---

### 4️⃣ Etiquetas

**Responsable:** [Nombre del Desarrollador]

**Componentes:**
- `Etiqueta` - Entidad principal
- `EtiquetaIngrediente` - Relación con ingredientes
- `EtiquetaEjercicio` - Relación con ejercicios
- `EtiquetaMeta` - Relación con metas
- `EtiquetaPlan` - Relación con planes
- `EtiquetaService` - Lógica de negocio
- `EtiquetaController` - Endpoints REST

**Tareas Principales:**
- [ ] Sistema de etiquetas genérico
- [ ] Asignación de etiquetas a recursos
- [ ] Búsqueda por etiquetas (AND/OR)
- [ ] Categorización por tipos
- [ ] Sistema de colores
- [ ] Gestión de etiquetas

**Documentación:** [docs/modules/etiquetas.md](docs/modules/etiquetas.md)

---

### 5️⃣ Infraestructura y Configuración

**Responsable:** [Nombre del Desarrollador]

**Componentes:**
- `CorsConfig` - Configuración CORS
- `DataInitializer` - Datos iniciales
- `GlobalExceptionHandler` - Manejo global de errores
- Excepciones personalizadas
- DTOs comunes
- Configuración de base de datos
- Logging

**Tareas Principales:**
- [ ] Configuración de CORS
- [ ] Manejo centralizado de excepciones
- [ ] Inicialización de datos de prueba
- [ ] Configuración de ambientes
- [ ] Logging centralizado
- [ ] Health checks
- [ ] Métricas y monitoreo

**Documentación:** [docs/modules/infraestructura.md](docs/modules/infraestructura.md)

---

## 📅 Cronograma General

### Sprint 1 (Semanas 1-2)
- **Infraestructura:** Configuración base, excepciones, CORS
- **Autenticación:** Registro, login, JWT básico
- **Perfil Usuario:** Modelo básico y CRUD

### Sprint 2 (Semanas 3-4)
- **Autenticación:** Refresh token, roles, permisos
- **Perfil Usuario:** Validaciones, foto de perfil
- **Salud:** Modelo de perfil de salud

### Sprint 3 (Semanas 5-6)
- **Salud:** Historial de medidas, cálculos
- **Etiquetas:** Sistema básico de etiquetas
- **Testing:** Tests de integración

### Sprint 4 (Semanas 7-8)
- **Etiquetas:** Asignación y búsqueda avanzada
- **Documentación:** Completar docs de todos los módulos
- **Deployment:** Configuración de ambientes

---

## 🔄 Dependencias entre Módulos

```
┌─────────────────────┐
│  Infraestructura    │ (Base para todos)
└──────────┬──────────┘
           │
    ┌──────┴──────┐
    │             │
┌───▼────┐   ┌───▼────────┐
│  Auth  │   │  Perfil    │
└───┬────┘   └───┬────────┘
    │            │
    └────┬───────┘
         │
    ┌────▼─────┐
    │  Salud   │
    └────┬─────┘
         │
    ┌────▼─────────┐
    │  Etiquetas   │
    └──────────────┘
```

**Orden de desarrollo sugerido:**
1. Infraestructura (base común)
2. Autenticación (requerido para todo)
3. Perfil Usuario (depende de Auth)
4. Salud e Historial (depende de Perfil)
5. Etiquetas (puede desarrollarse en paralelo)

---

## 📞 Comunicación

### Daily Standups
- **Cuándo:** Lunes a Viernes, 9:00 AM
- **Dónde:** Zoom / Presencial
- **Duración:** 15 minutos

### Revisiones de Código
- **Proceso:** Pull Request → Revisión → Aprobación → Merge
- **Revisores:** Mínimo 1 persona del equipo
- **Tiempo de respuesta:** Máximo 24 horas

### Reuniones Semanales
- **Sprint Planning:** Lunes 10:00 AM
- **Sprint Review:** Viernes 3:00 PM
- **Retrospectiva:** Viernes 4:00 PM

---

## 📊 Métricas de Progreso

### Objetivos por Sprint

| Sprint | Objetivo | Métricas |
|--------|----------|----------|
| 1 | Base del sistema | 3 módulos al 50% |
| 2 | Funcionalidad core | 2 módulos al 80% |
| 3 | Features avanzadas | 5 módulos al 60% |
| 4 | Finalización | 5 módulos al 100% |

### Indicadores de Calidad

- **Cobertura de Tests:** Mínimo 70%
- **Code Review:** 100% de PRs revisados
- **Documentación:** Cada módulo documentado
- **Bugs Críticos:** 0 en producción

---

## 🚨 Escalación de Problemas

### Nivel 1: Compañero de Equipo
Consulta directa con otro desarrollador del equipo.

### Nivel 2: Responsable de Módulo
Si el problema es específico de un módulo.

### Nivel 3: Líder Técnico
Para decisiones de arquitectura o problemas complejos.

### Nivel 4: Product Owner
Para cambios de alcance o prioridades.

---

## 📝 Notas

- Actualizar este documento semanalmente
- Reportar bloqueos inmediatamente
- Documentar decisiones importantes
- Mantener comunicación activa

---

**Última actualización:** [Fecha]  
**Próxima revisión:** [Fecha]
