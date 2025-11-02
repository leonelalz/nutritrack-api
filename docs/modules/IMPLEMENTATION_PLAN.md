# Plan de Implementación - NutriTrack API 🚀

**Fecha de inicio:** 2 de Noviembre, 2025  
**Base de datos:** PostgreSQL  
**Framework:** Spring Boot 3.x

## 📊 Estrategia de Branches

Cada módulo se implementará en su propio feature branch siguiendo GitFlow:

```
development (base)
    ├── feature/modulo-1-cuentas-preferencias (US-01 a US-05)
    ├── feature/modulo-2-biblioteca-contenido (US-06 a US-10)
    ├── feature/modulo-3-gestor-catalogo (US-11 a US-15)
    ├── feature/modulo-4-exploracion-activacion (US-16 a US-20)
    └── feature/modulo-5-seguimiento-progreso (US-21 a US-25)
```

## 🎯 Módulo 1: Gestión de Cuentas y Preferencias

**Branch:** `feature/modulo-1-cuentas-preferencias`  
**Responsable:** Leonel Alzamora  
**User Stories:** US-01 a US-05

### Entidades a Crear/Actualizar

#### ✅ Ya Existentes
- `CuentaAuth` - Necesita ajustes
- `Rol` - OK
- `PerfilUsuario` - Necesita campo `unidadesMedida`
- `UsuarioPerfilSalud` - Necesita refactoring

#### ❌ Por Crear
- `EtiquetaUsuario` (relación N-N entre PerfilUsuario y Etiqueta para alergias/condiciones)

### Enums a Crear

```java
// Ya existen:
- TipoRol ✅
- ObjetivoGeneral ✅
- NivelActividad ✅

// Por crear:
- UnidadesMedida (KG, LBS)
- TipoEtiqueta (ALERGIA, CONDICION_MEDICA, OBJETIVO, DIETA, DIFICULTAD)
```

### Endpoints a Implementar

| Endpoint | Verbo | Descripción | Status |
|----------|-------|-------------|--------|
| `/api/v1/auth/register` | POST | Crear cuenta (US-01) | ⚠️ Ajustar ruta |
| `/api/v1/auth/login` | POST | Login (US-02) | ⚠️ Ajustar ruta |
| `/api/v1/app/profile` | GET | Obtener perfil (US-04) | ❌ Por crear |
| `/api/v1/app/profile` | PUT | Actualizar perfil (US-03, US-04) | ❌ Por crear |
| `/api/v1/app/profile` | DELETE | Eliminar cuenta (US-05) | ❌ Por crear |

### Reglas de Negocio

- **RN-01:** Email único, validación de formato
- **RN-02:** Autenticación JWT
- **RN-03:** Actualización de preferencias
- **RN-04:** Validación de perfil de salud
- **RN-05:** Eliminación lógica de cuenta

### Commits Planificados

1. **Commit 1:** Crear enums faltantes (UnidadesMedida, TipoEtiqueta)
2. **Commit 2:** Actualizar entidades (PerfilUsuario, UsuarioPerfilSalud)
3. **Commit 3:** Crear DTOs de request/response para perfil
4. **Commit 4:** Implementar AppProfileController (GET, PUT, DELETE)
5. **Commit 5:** Actualizar AuthController con rutas `/api/v1/auth/*`
6. **Commit 6:** Crear tests unitarios
7. **Commit 7:** Actualizar documentación del módulo

---

## 🎯 Módulo 2: Biblioteca de Contenido (Admin)

**Branch:** `feature/modulo-2-biblioteca-contenido`  
**Responsables:** Fabian Rojas, Gonzalo Huaranga, Victor Carranza  
**User Stories:** US-06 a US-10

### Entidades a Crear

```java
- Ingrediente (con campos nutricionales)
- Ejercicio (con tipo, músculo, dificultad)
- Comida (tipo, tiempo elaboración)
- Receta (relación N-N Comida-Ingrediente)
- EtiquetaEjercicio (relación N-N)
```

### Enums a Crear

```java
- TipoEjercicio (CARDIO, FUERZA, FLEXIBILIDAD, etc.)
- Dificultad (BAJO, MEDIO, ALTO)
- TipoComida (DESAYUNO, ALMUERZO, CENA, SNACK)
- GrupoAlimenticio (PROTEINAS, CARBOHIDRATOS, GRASAS, etc.)
```

### Endpoints a Implementar

| Endpoint | Verbo | Descripción | Status |
|----------|-------|-------------|--------|
| `/api/v1/admin/tags` | POST | Crear etiqueta (US-06) | ❌ |
| `/api/v1/admin/tags` | GET | Listar etiquetas (US-06) | ❌ |
| `/api/v1/admin/tags/{id}` | DELETE | Eliminar etiqueta (US-06) | ❌ |
| `/api/v1/admin/ingredients` | POST | Crear ingrediente (US-07) | ❌ |
| `/api/v1/admin/ingredients/{id}` | DELETE | Eliminar ingrediente (US-07) | ❌ |
| `/api/v1/admin/exercises` | POST | Crear ejercicio (US-08) | ❌ |
| `/api/v1/admin/meals` | POST | Crear comida con receta (US-09, US-10) | ❌ |

### Reglas de Negocio

- **RN-06:** Solo ADMIN puede crear etiquetas
- **RN-07:** No eliminar etiqueta en uso
- **RN-08:** Validar duplicados por nombre
- **RN-09:** No eliminar ingrediente en recetas activas
- **RN-10:** Receta debe tener al menos 1 ingrediente

---

## 🎯 Módulo 3: Gestor de Catálogo (Admin)

**Branch:** `feature/modulo-3-gestor-catalogo`  
**Responsables:** Gonzalo Huaranga, Victor Carranza  
**User Stories:** US-11 a US-15

### Entidades a Crear

```java
- CatalogoMeta (nombre, descripción, duración)
- CatalogoActividad (relación con Meta)
- CatalogoPlanNutricion (relación 1-1 con Actividad)
- CatalogoRutina (relación N-N Actividad-Ejercicio)
- CatalogoPlanComidas (relación N-N Plan-Comida)
```

### Enums a Crear

```java
- TipoActividad (EJERCICIO, NUTRICION)
- TipoPlanNutricion (DEFICIT, SUPERAVIT, MANTENIMIENTO)
```

### Endpoints a Implementar

| Endpoint | Verbo | Descripción | Status |
|----------|-------|-------------|--------|
| `/api/v1/admin/catalog/goals` | POST | Crear meta catálogo (US-11) | ❌ |
| `/api/v1/admin/catalog/goals/{id}/tags` | POST | Asignar etiqueta (US-12) | ❌ |
| `/api/v1/admin/catalog/goals/{id}/activities` | POST | Crear actividad (US-12) | ❌ |
| `/api/v1/admin/catalog/activities/{id}/routine` | POST | Ensamblar rutina (US-14) | ❌ |
| `/api/v1/admin/catalog/goals/{id}` | DELETE | Eliminar meta (US-15) | ❌ |

### Reglas de Negocio

- **RN-11:** Solo ADMIN puede gestionar catálogo
- **RN-12:** Meta debe tener al menos 1 etiqueta
- **RN-13:** Rutina requiere ejercicio válido
- **RN-14:** No eliminar meta en uso por clientes

---

## 🎯 Módulo 4: Exploración y Activación (Cliente)

**Branch:** `feature/modulo-4-exploracion-activacion`  
**Responsables:** Gonzalo Huaranga, Victor Carranza  
**User Stories:** US-16 a US-20

### Entidades a Crear

```java
- UsuarioMetaAsignada (relación Usuario-Meta con estado)
```

### Enums a Crear

```java
- EstadoPlan (ACTIVO, PAUSADO, COMPLETADO, CANCELADO)
```

### Endpoints a Implementar

| Endpoint | Verbo | Descripción | Status |
|----------|-------|-------------|--------|
| `/api/v1/app/catalog/goals` | GET | Ver catálogo filtrado (US-16) | ❌ |
| `/api/v1/app/catalog/goals/{id}` | GET | Ver detalle meta (US-17) | ❌ |
| `/api/v1/app/my-plan` | POST | Activar plan (US-18) | ❌ |
| `/api/v1/app/my-plan` | PUT | Actualizar estado plan (US-19, US-20) | ❌ |

### Reglas de Negocio

- **RN-15:** Filtrar por etiquetas del perfil de salud
- **RN-16:** Excluir metas con alergias del usuario
- **RN-17:** Solo 1 plan activo a la vez
- **RN-18:** Validar compatibilidad de meta con perfil
- **RN-19:** Transiciones de estado válidas

---

## 🎯 Módulo 5: Seguimiento de Progreso (Cliente)

**Branch:** `feature/modulo-5-seguimiento-progreso`  
**Responsables:** Gonzalo Huaranga, Jhamil Peña, Victor Carranza  
**User Stories:** US-21 a US-25

### Entidades ya existentes

```java
- UsuarioHistorialMedida ✅ (ya creada)
- UsuarioActividadProgreso (por crear)
```

### Endpoints a Implementar

| Endpoint | Verbo | Descripción | Status |
|----------|-------|-------------|--------|
| `/api/v1/app/my-plan/activities` | GET | Ver actividades plan (US-21) | ❌ |
| `/api/v1/app/my-plan/activities/{id}/complete` | POST | Marcar completada (US-22) | ❌ |
| `/api/v1/app/my-plan/activities/{id}/complete` | DELETE | Desmarcar (US-23) | ❌ |
| `/api/v1/app/measurements` | POST | Registrar medición (US-24) | ❌ |
| `/api/v1/app/measurements` | GET | Ver mediciones (US-24) | ❌ |
| `/api/v1/app/reports/progress-chart` | GET | Datos gráfico (US-25) | ❌ |
| `/api/v1/app/reports/download-pdf` | GET | Descargar PDF (US-25) | ❌ |

### Reglas de Negocio

- **RN-20:** Solo ver actividades de plan activo
- **RN-21:** No duplicar marcas de completado
- **RN-22:** Validar fecha de medición
- **RN-23:** Gráfico últimos 30 días
- **RN-24:** PDF con datos completos del progreso

---

## 🔧 Mejoras Sugeridas al Esquema DB

### 1. Agregar tabla `etiquetas_ejercicios`

```sql
CREATE TABLE "etiquetas_ejercicios" (
  "id_ejercicio" BIGINT,
  "id_etiqueta" BIGINT,
  PRIMARY KEY ("id_ejercicio", "id_etiqueta")
);

ALTER TABLE "etiquetas_ejercicios" ADD FOREIGN KEY ("id_ejercicio") REFERENCES "ejercicios" ("id");
ALTER TABLE "etiquetas_ejercicios" ADD FOREIGN KEY ("id_etiqueta") REFERENCES "etiquetas" ("id");
```

### 2. Agregar campo `unidades_medida` en `perfiles_usuario`

```sql
ALTER TABLE "perfiles_usuario" ADD COLUMN "unidades_medida" VARCHAR(10) DEFAULT 'kg';
```

### 3. Normalizar alergias y condiciones médicas

```sql
-- En lugar de TEXT, usar tabla de relación
CREATE TABLE "usuario_etiquetas_salud" (
  "id_usuario" UUID,
  "id_etiqueta" BIGINT,
  PRIMARY KEY ("id_usuario", "id_etiqueta")
);
```

### 4. Agregar índices para performance

```sql
CREATE INDEX idx_cuentas_email ON cuentas_auth(email);
CREATE INDEX idx_perfil_usuario ON perfiles_usuario(id_usuario);
CREATE INDEX idx_metas_asignadas_cliente ON usuario_metas_asignadas(id_cliente);
CREATE INDEX idx_actividades_progreso_meta ON usuario_actividades_progreso(id_meta_asignada);
```

### 5. Agregar campos de auditoría

```sql
ALTER TABLE catalogo_metas ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE catalogo_metas ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
```

---

## 📅 Cronograma Estimado

| Módulo | Duración Estimada | Commits Aprox. | Estado |
|--------|-------------------|----------------|--------|
| Módulo 1 | 2-3 días | 6-8 | 🟡 En progreso |
| Módulo 2 | 3-4 días | 8-10 | ⚪ Pendiente |
| Módulo 3 | 3-4 días | 7-9 | ⚪ Pendiente |
| Módulo 4 | 2-3 días | 5-7 | ⚪ Pendiente |
| Módulo 5 | 3-4 días | 8-10 | ⚪ Pendiente |

**Total estimado:** 13-18 días de desarrollo

---

## 🧪 Estrategia de Testing

Cada módulo incluirá:

1. **Tests Unitarios** (Service layer)
2. **Tests de Integración** (Controller layer)
3. **Tests de Reglas de Negocio**
4. **Colección Postman actualizada**

---

## 📝 Documentación por Módulo

Cada módulo tendrá su documentación en:
- `docs/modules/modulo-{n}-{nombre}.md`

Con secciones:
- Descripción general
- Entidades y relaciones
- Endpoints implementados
- Reglas de negocio
- Ejemplos de uso
- Troubleshooting

---

**Última actualización:** 2 de Noviembre, 2025
