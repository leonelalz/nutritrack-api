# Base de Datos NutriTrack - Documentación SQL 🗄️

**Última actualización:** 2 de Noviembre, 2025  
**Motor:** PostgreSQL  
**Versión:** Mejorada y optimizada

## 📊 Mejoras Implementadas

### 1. ✅ Normalización de Datos

#### Antes:
```sql
CREATE TABLE "usuario_perfil_salud" (
  "alergias" TEXT,
  "condiciones_medicas" TEXT
);
```

#### Después:
```sql
CREATE TABLE "usuario_etiquetas_salud" (
  "id_perfil" UUID,
  "id_etiqueta" BIGINT,
  PRIMARY KEY ("id_perfil", "id_etiqueta")
);
```

**Beneficio:** Permite búsquedas eficientes, filtros por etiquetas y evita duplicación de datos.

---

### 2. ✅ Sistema de Unidades de Medida

#### Nuevo campo en `perfiles_usuario`:
```sql
"unidades_medida" VARCHAR(10) DEFAULT 'KG'
```

**Beneficio:** Soporta US-03 - Preferencias del usuario (KG vs LBS).

---

### 3. ✅ Tabla de Etiquetas para Ejercicios

```sql
CREATE TABLE "etiquetas_ejercicios" (
  "id_ejercicio" BIGINT,
  "id_etiqueta" BIGINT,
  PRIMARY KEY ("id_ejercicio", "id_etiqueta")
);
```

**Beneficio:** Clasificación de ejercicios por grupo muscular, tipo, dificultad, etc.

---

### 4. ✅ Cambio de ENUMs a VARCHAR

#### Antes:
```sql
"tipo_etiqueta" "ENUM" NOT NULL
```

#### Después:
```sql
"tipo_etiqueta" VARCHAR(50) NOT NULL
```

**Beneficio:** 
- Mayor flexibilidad en Spring Boot
- Compatibilidad con JPA @Enumerated(EnumType.STRING)
- Fácil extensión sin modificar schema

---

### 5. ✅ Timestamps de Auditoría

Agregados en tablas principales:
```sql
"created_at" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
"updated_at" TIMESTAMP DEFAULT CURRENT_TIMESTAMP
```

Tablas con auditoría:
- `cuentas_auth`
- `etiquetas`
- `ejercicios`
- `comidas`
- `ingredientes`
- `catalogo_metas`
- `catalogo_actividades`
- `catalogo_planes_nutricion`
- `usuario_metas_asignadas`
- `usuario_historial_medidas`

**Beneficio:** Trazabilidad completa de cambios.

---

### 6. ✅ Índices para Performance

```sql
CREATE INDEX idx_cuentas_email ON cuentas_auth(email);
CREATE INDEX idx_metas_asignadas_cliente ON usuario_metas_asignadas(id_cliente);
CREATE INDEX idx_historial_medidas_perfil ON usuario_historial_medidas(id_perfil);
-- ... y más
```

**Beneficio:** Mejora significativa en velocidad de consultas frecuentes.

---

### 7. ✅ Sequences para IDs

```sql
CREATE SEQUENCE seq_etiquetas START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE seq_ingredientes START WITH 1 INCREMENT BY 1;
-- ... etc
```

**Beneficio:** Gestión automática de IDs en PostgreSQL compatible con JPA.

---

### 8. ✅ Datos Iniciales

Se insertan automáticamente:
- 2 roles (ADMIN, USER)
- Etiquetas de ejemplo:
  - Objetivos (Perder Peso, Ganar Músculo, Mantener Forma)
  - Alergias (Lácteos, Nueces, Gluten, Mariscos)
  - Condiciones Médicas (Diabetes, Hipertensión, Colesterol)
  - Dificultad (Principiante, Intermedio, Avanzado)

**Beneficio:** La aplicación arranca con datos base funcionales.

---

## 🗂️ Estructura de Tablas

### Módulo 1: Gestión de Cuentas y Preferencias
- `cuentas_auth` - Credenciales y autenticación
- `roles` - Roles del sistema (ADMIN, USER)
- `perfiles_usuario` - Información del perfil
- `usuario_perfil_salud` - Preferencias de salud y fitness
- `usuario_etiquetas_salud` - ⭐ **NUEVA** - Alergias y condiciones

### Módulo 2: Biblioteca de Contenido
- `etiquetas` - Sistema de etiquetado universal
- `ingredientes` - Ingredientes con info nutricional
- `ejercicios` - Catálogo de ejercicios
- `comidas` - Catálogo de comidas
- `recetas` - Relación comidas-ingredientes
- `etiquetas_ingredientes` - Clasificación de ingredientes
- `etiquetas_ejercicios` - ⭐ **NUEVA** - Clasificación de ejercicios

### Módulo 3: Gestor de Catálogo
- `catalogo_metas` - Metas del catálogo
- `catalogo_actividades` - Actividades por meta
- `catalogo_planes_nutricion` - Planes nutricionales
- `catalogo_rutinas` - Rutinas de ejercicios
- `catalogo_plan_comidas` - Comidas por plan
- `etiquetas_metas` - Clasificación de metas
- `etiquetas_planes` - Clasificación de planes

### Módulo 4: Exploración y Activación
- `usuario_metas_asignadas` - Planes activos del usuario

### Módulo 5: Seguimiento de Progreso
- `usuario_actividades_progreso` - Progreso de actividades
- `usuario_historial_medidas` - Historial de mediciones

---

## 🔑 Relaciones Principales

```
cuentas_auth (1) ─────> (1) perfiles_usuario
                              │
                              ├─> (1) usuario_perfil_salud
                              ├─> (N) usuario_etiquetas_salud ─> etiquetas
                              ├─> (N) usuario_historial_medidas
                              └─> (N) usuario_metas_asignadas ─> catalogo_metas
                                       │
                                       └─> (N) usuario_actividades_progreso
```

---

## 📝 Tipos de Etiquetas (Enum)

```java
public enum TipoEtiqueta {
    ALERGIA,              // Alergias alimentarias
    CONDICION_MEDICA,     // Condiciones de salud
    OBJETIVO,             // Objetivos de fitness
    DIETA,                // Tipos de dieta (vegana, keto, etc.)
    DIFICULTAD,           // Nivel de dificultad
    GRUPO_MUSCULAR,       // Clasificación de ejercicios
    TIPO_EJERCICIO        // Cardio, fuerza, etc.
}
```

---

## 🚀 Cómo Usar

### 1. Crear la Base de Datos

```bash
psql -U postgres
CREATE DATABASE nutritrack_db;
\c nutritrack_db
\i SQL/NutriDB.sql
```

### 2. Verificar Tablas Creadas

```sql
\dt
```

### 3. Verificar Datos Iniciales

```sql
SELECT * FROM roles;
SELECT * FROM etiquetas WHERE tipo_etiqueta = 'OBJETIVO';
```

---

## 🔄 Migración desde Versión Anterior

Si ya tienes datos en la versión antigua:

```sql
-- 1. Backup de datos
pg_dump nutritrack_db > backup.sql

-- 2. Crear nueva DB
CREATE DATABASE nutritrack_db_v2;

-- 3. Ejecutar nuevo schema
\i SQL/NutriDB.sql

-- 4. Migrar datos manualmente o con scripts
```

---

## 📊 Cambios de Nomenclatura

| Tabla | Campo Antiguo | Campo Nuevo | Razón |
|-------|---------------|-------------|-------|
| `usuario_perfil_salud` | `id_cliente` | `id_perfil` | Consistencia con FK |
| `usuario_historial_medidas` | `id_cliente` | `id_perfil` | Consistencia |
| `perfiles_usuario` | - | `unidades_medida` | Nueva funcionalidad |
| `usuario_perfil_salud` | `alergias` (TEXT) | Tabla `usuario_etiquetas_salud` | Normalización |
| `usuario_perfil_salud` | `condiciones_medicas` (TEXT) | Tabla `usuario_etiquetas_salud` | Normalización |

---

## ⚠️ Validaciones Importantes

### En Aplicación (Spring Boot):

```java
// Validar que usuario solo tenga 1 plan activo
@Query("SELECT COUNT(u) FROM UsuarioMetaAsignada u WHERE u.cliente.id = :idCliente AND u.estado = 'ACTIVO'")
int countActivePlans(@Param("idCliente") UUID idCliente);

// Validar etiquetas de salud según tipo
Set<Etiqueta> alergias = perfil.getEtiquetasSalud().stream()
    .filter(e -> e.getTipoEtiqueta() == TipoEtiqueta.ALERGIA)
    .collect(Collectors.toSet());
```

---

## 🧪 Queries de Ejemplo

### Obtener perfil completo con etiquetas de salud:
```sql
SELECT 
    p.nombre,
    p.unidades_medida,
    ups.objetivo_actual,
    ups.nivel_actividad_actual,
    array_agg(e.nombre) FILTER (WHERE e.tipo_etiqueta = 'ALERGIA') as alergias,
    array_agg(e.nombre) FILTER (WHERE e.tipo_etiqueta = 'CONDICION_MEDICA') as condiciones
FROM perfiles_usuario p
JOIN usuario_perfil_salud ups ON p.id = ups.id_perfil
LEFT JOIN usuario_etiquetas_salud ues ON p.id = ues.id_perfil
LEFT JOIN etiquetas e ON ues.id_etiqueta = e.id
WHERE p.id = 'UUID_DEL_PERFIL'
GROUP BY p.id, ups.objetivo_actual, ups.nivel_actividad_actual;
```

### Catálogo filtrado por perfil de usuario:
```sql
SELECT DISTINCT cm.*
FROM catalogo_metas cm
JOIN etiquetas_metas em ON cm.id = em.id_catalogo_meta
JOIN usuario_perfil_salud ups ON ups.id_perfil = 'UUID_DEL_PERFIL'
WHERE em.id_etiqueta IN (
    SELECT id FROM etiquetas 
    WHERE tipo_etiqueta = 'OBJETIVO' 
    AND nombre = ups.objetivo_actual
)
AND cm.id NOT IN (
    -- Excluir metas con ingredientes alérgicos
    SELECT DISTINCT cm2.id
    FROM catalogo_metas cm2
    JOIN catalogo_actividades ca ON cm2.id = ca.id_catalogo_meta
    JOIN catalogo_planes_nutricion cpn ON ca.id = cpn.id_catalogo_actividad
    JOIN catalogo_plan_comidas cpc ON cpn.id = cpc.id_catalogo_plan
    JOIN recetas r ON cpc.id_comida = r.id_comida
    JOIN etiquetas_ingredientes ei ON r.id_ingrediente = ei.id_ingrediente
    JOIN usuario_etiquetas_salud ues ON ei.id_etiqueta = ues.id_etiqueta
    WHERE ues.id_perfil = 'UUID_DEL_PERFIL'
);
```

---

## 📚 Referencias

- [PostgreSQL Data Types](https://www.postgresql.org/docs/current/datatype.html)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [NutriTrack Implementation Plan](../docs/modules/IMPLEMENTATION_PLAN.md)

---

**Última actualización:** 2 de Noviembre, 2025  
**Mantenedor:** Equipo NutriTrack
