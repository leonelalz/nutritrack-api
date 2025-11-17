# 📊 Scripts de Datos de Demostración - NutriTrack API

Este directorio contiene scripts SQL para poblar la base de datos con datos de demostración.

## 📁 Archivos Principales

### `CARGA_DATOS_COMPLETA.sql` ✅ USAR ESTE
Archivo consolidado con TODOS los datos de prueba. Compatible con modelos JPA.
- Módulo 2: Catálogo (ingredientes, comidas, ejercicios, etiquetas)
- Módulo 3: Planes y rutinas
- Módulo 4: Asignaciones a usuarios
- Módulo 5: Registros de actividades

### `NutriDB.sql`
Esquema de base de datos (tablas, constraints). Solo referencia, Hibernate lo genera automáticamente.

## 📋 Orden de Ejecución

Los scripts deben ejecutarse en el siguiente orden para mantener la integridad referencial:

### 1. **Esquema y Estructura** (Requerido)
```bash
# Crear la estructura completa de la base de datos
psql -U nutritrack_user -d nutritrack_db -f NutriDB.sql
```

### 2. **Usuario de Demostración** (Módulo 1)
```bash
# Crear usuario demo@nutritrack.com con perfil completo
psql -U nutritrack_user -d nutritrack_db -f demo_user.sql
```
- **Usuario:** demo@nutritrack.com
- **Password:** Demo123!
- **Perfil:** María García
- **Objetivo:** Perder Peso
- **Nivel Actividad:** Moderado

### 3. **Catálogo Base** (Módulo 2)
```bash
# Datos del catálogo (ingredientes, comidas, ejercicios, etiquetas)
psql -U nutritrack_user -d nutritrack_db -f modulo2_catalogo_demo.sql
# O versión simplificada:
psql -U nutritrack_user -d nutritrack_db -f modulo2_catalogo_simple.sql
```

### 4. **Planes y Rutinas** (Módulo 3)
```bash
# Crear planes de nutrición y rutinas de ejercicio
psql -U nutritrack_user -d nutritrack_db -f modulo3_data_demo.sql
```
Crea:
- 4 Planes de Nutrición (Pérdida de Grasa, Ganancia Muscular, Definición, Mantenimiento)
- 6 Rutinas de Ejercicio (HIIT, Fuerza Superior/Inferior, Cardio, Core, Movilidad)
- Objetivos nutricionales para cada plan
- Días programados para planes
- Ejercicios configurados para rutinas

### 5. **Asignaciones de Usuario** (Módulo 4) ⭐ NUEVO
```bash
# Asignar planes y rutinas al usuario demo
psql -U nutritrack_user -d nutritrack_db -f modulo4_asignaciones_demo.sql
```
Crea:
- **Planes asignados:**
  - 1 ACTIVO (día 15/56)
  - 1 COMPLETADO
  - 1 PAUSADO
  - 1 CANCELADO
- **Rutinas asignadas:**
  - 2 ACTIVAS (semana 2/4 y semana 1/6)
  - 1 COMPLETADA
  - 1 PAUSADA
  - 1 CANCELADA

### 6. **Registros de Actividades** (Módulo 5) ⭐ NUEVO
```bash
# Registrar comidas y ejercicios realizados
psql -U nutritrack_user -d nutritrack_db -f modulo5_registros_demo.sql
```
Crea:
- **Registros de Comidas:**
  - Hoy: 3 comidas (desayuno, almuerzo, snack)
  - Ayer: 4 comidas completas
  - Últimos 5 días: registros variados
- **Registros de Ejercicios:**
  - Hoy: 2 ejercicios (HIIT matutino)
  - Ayer: 3 ejercicios (sesión vespertina)
  - Últimos 5 días: sesiones de entrenamiento

## 🚀 Ejecución Completa (Todo en uno)

```bash
# Ejecutar todos los scripts en orden
cd SQL

# 1. Estructura
psql -U nutritrack_user -d nutritrack_db -f NutriDB.sql

# 2. Usuario demo
psql -U nutritrack_user -d nutritrack_db -f demo_user.sql

# 3. Catálogo
psql -U nutritrack_user -d nutritrack_db -f modulo2_catalogo_demo.sql

# 4. Planes y rutinas
psql -U nutritrack_user -d nutritrack_db -f modulo3_data_demo.sql

# 5. Asignaciones (Módulo 4)
psql -U nutritrack_user -d nutritrack_db -f modulo4_asignaciones_demo.sql

# 6. Registros (Módulo 5)
psql -U nutritrack_user -d nutritrack_db -f modulo5_registros_demo.sql

echo "✓ Todos los datos de demostración han sido cargados"
```

## 🔍 Verificación de Datos

Después de ejecutar los scripts, puedes verificar los datos:

```sql
-- Ver usuario demo y su perfil
SELECT ca.email, pu.nombre, pu.apellido, ups.objetivo_actual, ups.nivel_actividad_actual
FROM cuentas_auth ca
JOIN perfiles_usuario pu ON ca.id = pu.id_usuario
JOIN usuario_perfil_salud ups ON pu.id = ups.id_perfil
WHERE ca.email = 'demo@nutritrack.com';

-- Ver planes asignados
SELECT p.nombre, up.estado, up.dia_actual, up.fecha_inicio
FROM usuarios_planes up
JOIN planes p ON up.id_plan = p.id
JOIN perfiles_usuario pu ON up.id_perfil_usuario = pu.id
JOIN cuentas_auth ca ON pu.id_usuario = ca.id
WHERE ca.email = 'demo@nutritrack.com';

-- Ver rutinas asignadas
SELECT r.nombre, ur.estado, ur.semana_actual, ur.fecha_inicio
FROM usuarios_rutinas ur
JOIN rutinas r ON ur.id_rutina = r.id
JOIN perfiles_usuario pu ON ur.id_perfil_usuario = pu.id
JOIN cuentas_auth ca ON pu.id_usuario = ca.id
WHERE ca.email = 'demo@nutritrack.com';

-- Ver registros de comidas (últimos 7 días)
SELECT rc.fecha, c.nombre, rc.tipo_comida, rc.porciones, rc.calorias_consumidas
FROM registros_comidas rc
JOIN comidas c ON rc.id_comida = c.id
JOIN perfiles_usuario pu ON rc.id_perfil_usuario = pu.id
JOIN cuentas_auth ca ON pu.id_usuario = ca.id
WHERE ca.email = 'demo@nutritrack.com'
  AND rc.fecha >= CURRENT_DATE - INTERVAL '7 days'
ORDER BY rc.fecha DESC, rc.hora DESC;

-- Ver registros de ejercicios (últimos 7 días)
SELECT re.fecha, e.nombre, re.series_realizadas, re.repeticiones_realizadas, re.calorias_quemadas
FROM registros_ejercicios re
JOIN ejercicios e ON re.id_ejercicio = e.id
JOIN perfiles_usuario pu ON re.id_perfil_usuario = pu.id
JOIN cuentas_auth ca ON pu.id_usuario = ca.id
WHERE ca.email = 'demo@nutritrack.com'
  AND re.fecha >= CURRENT_DATE - INTERVAL '7 days'
ORDER BY re.fecha DESC, re.hora DESC;
```

## 📊 Resumen de Datos Creados

| Módulo | Descripción | Cantidad |
|--------|-------------|----------|
| **Módulo 1** | Usuario demo con perfil y mediciones | 1 usuario, 4 mediciones |
| **Módulo 2** | Catálogo (ingredientes, comidas, ejercicios) | ~50+ items |
| **Módulo 3** | Planes de nutrición y rutinas | 4 planes, 6 rutinas |
| **Módulo 4** | Asignaciones activas/históricas | 4 planes, 5 rutinas |
| **Módulo 5** | Registros de actividades | ~13 comidas, ~11 ejercicios |

## 🧹 Limpiar Datos de Demostración

Si necesitas eliminar solo los datos de demostración (mantener estructura):

```sql
-- Eliminar en orden inverso para respetar foreign keys
DELETE FROM registros_ejercicios WHERE id_perfil_usuario IN (
    SELECT id FROM perfiles_usuario WHERE id_usuario IN (
        SELECT id FROM cuentas_auth WHERE email = 'demo@nutritrack.com'
    )
);

DELETE FROM registros_comidas WHERE id_perfil_usuario IN (
    SELECT id FROM perfiles_usuario WHERE id_usuario IN (
        SELECT id FROM cuentas_auth WHERE email = 'demo@nutritrack.com'
    )
);

DELETE FROM usuarios_rutinas WHERE id_perfil_usuario IN (
    SELECT id FROM perfiles_usuario WHERE id_usuario IN (
        SELECT id FROM cuentas_auth WHERE email = 'demo@nutritrack.com'
    )
);

DELETE FROM usuarios_planes WHERE id_perfil_usuario IN (
    SELECT id FROM perfiles_usuario WHERE id_usuario IN (
        SELECT id FROM cuentas_auth WHERE email = 'demo@nutritrack.com'
    )
);

-- Continuar con el resto de tablas...
```

## 🧪 Uso en Pruebas

Estos datos son ideales para:
- ✅ Pruebas de endpoints REST
- ✅ Desarrollo de UI/UX
- ✅ Demos a stakeholders
- ✅ Validación de reglas de negocio
- ✅ Testing de integración
- ✅ Pruebas de carga de datos históricos

## 📝 Notas Importantes

1. **Contraseña del usuario demo:** `Demo123!` (hasheada con BCrypt en el script)
2. **Fechas dinámicas:** Los scripts usan `CURRENT_DATE` para que los datos siempre sean relevantes
3. **Datos realistas:** Calorías, porciones y mediciones basadas en valores nutricionales reales
4. **Estado de asignaciones:** Incluye todos los estados posibles (ACTIVO, PAUSADO, COMPLETADO, CANCELADO)
5. **Historial de actividades:** Cubre últimos 5-7 días para simular uso real de la aplicación

## 🔗 Dependencias

```
NutriDB.sql
    ↓
demo_user.sql
    ↓
modulo2_catalogo_demo.sql
    ↓
modulo3_data_demo.sql
    ↓
modulo4_asignaciones_demo.sql  ← NUEVO
    ↓
modulo5_registros_demo.sql     ← NUEVO
```

## 📧 Contacto

Para preguntas o problemas con los scripts, contactar al equipo de desarrollo.
