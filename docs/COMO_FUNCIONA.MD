# 🎯 CÓMO FUNCIONA EL PROYECTO NUTRITRACK

> **Documento Guía para Reconstrucción del Proyecto**  
> Este documento explica la lógica de negocio, arquitectura y flujo de datos del sistema NutriTrack.  
> Úsalo como referencia cada vez que necesites recordar cómo funciona el proyecto completo.

---

## 📋 TABLA DE CONTENIDOS

1. [Concepto General del Sistema](#concepto-general-del-sistema)
2. [Arquitectura de 5 Módulos](#arquitectura-de-5-módulos)
3. [Flujo de Datos Completo](#flujo-de-datos-completo)
4. [Estructura de Base de Datos](#estructura-de-base-de-datos)
5. [Modelo de Negocio](#modelo-de-negocio)
6. [Casos de Uso Principales](#casos-de-uso-principales)
7. [Diferencias Clave vs Otros Sistemas](#diferencias-clave-vs-otros-sistemas)
8. [Reglas de Negocio Críticas](#reglas-de-negocio-críticas)
9. [Resumen Ejecutivo](#resumen-ejecutivo)
10. [Referencias Técnicas](#referencias-técnicas)

---

## 🚀 FLUJO DE TRABAJO DEL EQUIPO

### Para Iniciar el Proyecto (Orden Recomendado)

1. **LEER ESTA GUÍA COMPLETA** - Entender el modelo de negocio
2. **REVISAR USER_STORIES.MD** - Entender QUÉ debe hacer el sistema
3. **REVISAR REGLAS_NEGOCIO.MD** - Entender CÓMO debe comportarse
4. **VERIFICAR BASE DE DATOS** - Confirmar que NutriDB.sql tiene todas las tablas (ya verificado ✅)
5. **IMPLEMENTAR POR MÓDULOS** - Seguir la asignación de responsabilidades

### Ciclo de Desarrollo por User Story

```
1. Leer User Story (USER_STORIES.MD)
2. Identificar Reglas de Negocio relacionadas (REGLAS_NEGOCIO.MD)
3. Crear DTOs con validaciones Bean Validation
4. Implementar Service con lógica de negocio
5. Crear Controller con endpoints REST
6. Escribir tests unitarios
7. Verificar contra criterios de aceptación
```

### Prioridad de Implementación

🚨 **CRÍTICO** - Implementar primero (riesgo de seguridad/datos):
- RN16: Filtrar catálogo por alergias (SEGURIDAD DE SALUD)
- RN07, RN08, RN09: Integridad de datos (nombres únicos, no eliminar en uso)

🟡 **ALTA** - Implementar segundo:
- RN14: No eliminar planes con usuarios activos
- US-01 a US-05: Sistema de autenticación

🟢 **MEDIA** - Implementar después:
- Módulos 2 y 3: Biblioteca de contenido y catálogo
- Módulos 4 y 5: Funcionalidades de cliente

---

## 🌟 CONCEPTO GENERAL DEL SISTEMA

### ¿Qué es NutriTrack?

**NutriTrack es una plataforma SaaS de coaching nutricional y fitness** que permite a los usuarios:

1. **Configurar su perfil de salud** (objetivos, nivel de actividad, alergias, condiciones médicas)
2. **Escoger PLANES completos** (no actividades sueltas):
    - **Planes de Alimentación** con duración fija (ej: 30 días)
    - **Rutinas de Ejercicio** con duración fija (ej: 12 semanas)
3. **Seguir actividades diarias programadas** que el plan les indica
4. **Registrar lo que realmente hacen** día a día
5. **Ver su progreso** comparando lo planeado vs lo real

### Concepto Clave: Planes como Objetivos con Actividades

```
USUARIO ESCOGE → PLAN (Objetivo con duración fija)
                    ↓
         ┌──────────┴──────────┐
         │                     │
    PLAN ALIMENTACIÓN    RUTINA EJERCICIO
    (30 días)            (12 semanas)
         │                     │
         ↓                     ↓
    ACTIVIDADES DIARIAS  ACTIVIDADES DIARIAS
    (Qué comer cada día) (Qué ejercicios hacer)
         │                     │
         ↓                     ↓
    REGISTRO REAL        REGISTRO REAL
    (Qué comió)          (Qué ejercicio hizo)
         │                     │
         ↓                     ↓
    MÉTRICAS DE PROGRESO
    (Adherencia, calorías, peso)
```

---

## 🏗️ ARQUITECTURA DE 5 MÓDULOS

### MÓDULO 1: AUTENTICACIÓN Y PERFILES

**Propósito:** Gestionar usuarios y su información de salud

```
roles (ROLE_USER, ROLE_ADMIN)
  ↓
cuentas_auth (email, password, activo)
  ↓
perfiles_usuario (nombre, apellido, unidades_medida: KG/LBS)
  ↓
┌─────────────────┴─────────────────┐
│                                   │
usuario_perfil_salud         usuario_historial_medidas
(objetivo, nivel_actividad)  (peso, altura, IMC por fecha)
│
usuario_etiquetas_salud
(alergias, condiciones médicas)
```

**Tablas:**
- `roles`: Solo 2 roles (ROLE_USER, ROLE_ADMIN)
- `cuentas_auth`: Autenticación (email único, password, JWT)
- `perfiles_usuario`: Datos personales (1-to-1 con cuentas_auth)
- `usuario_perfil_salud`: Configuración de salud (1-to-1 con perfiles_usuario)
    - `objetivo_actual`: PERDER_PESO | GANAR_MASA_MUSCULAR | MANTENER_FORMA | REHABILITACION | CONTROLAR_ESTRES
    - `nivel_actividad_actual`: BAJO | MODERADO | ALTO
- `usuario_historial_medidas`: Histórico de medidas corporales
- `usuario_etiquetas_salud`: Etiquetas del usuario (many-to-many con etiquetas)

**Punto Clave:** Un usuario puede tener múltiples registros de medidas (peso, altura) en el tiempo para hacer seguimiento de progreso.

---

### MÓDULO 2: BIBLIOTECA DE CONTENIDO

**Propósito:** Catálogo reutilizable de ingredientes, comidas y ejercicios

```
etiquetas (sistema de categorización universal)
    ↓
┌───┴───────────┬──────────────┐
│               │              │
ingredientes    comidas     ejercicios
    ↓               ↓
etiquetas_      recetas
ingredientes    (comida + ingrediente + cantidad)
```

#### 2.1. Sistema de Etiquetas (Categorización Universal)

**Tabla:** `etiquetas`

**Tipos de Etiquetas (7 categorías):**
```sql
tipo_etiqueta VARCHAR(50) CHECK IN (
  'ALERGIA',           -- Gluten, Lactosa, Maní
  'CONDICION_MEDICA',  -- Diabetes, Hipertensión, Celíaco
  'OBJETIVO',          -- Pérdida de peso, Ganancia muscular
  'DIETA',             -- Vegana, Vegetariana, Keto, Paleo
  'DIFICULTAD',        -- Principiante, Intermedio, Avanzado
  'GRUPO_MUSCULAR',    -- Pecho, Espalda, Piernas
  'TIPO_EJERCICIO'     -- Cardio, Fuerza, HIIT, Yoga
)
```

**Ventaja:** Una sola tabla categoriza usuarios, ingredientes, comidas, ejercicios, planes y rutinas.

#### 2.2. Ingredientes

**Tabla:** `ingredientes`

**Campos Clave:**
- Información nutricional (por 100g): `proteinas`, `carbohidratos`, `grasas`, `energia`
- `grupo_alimenticio`: FRUTAS | VERDURAS | CEREALES | LEGUMBRES | PROTEINAS_ANIMALES | PROTEINAS_VEGETALES | LACTEOS | GRASAS_SALUDABLES | AZUCARES | BEBIDAS | CONDIMENTOS | FRUTOS_SECOS | OTRO

**Relaciones:**
- `etiquetas_ingredientes`: Many-to-many con etiquetas (ej: Leche → etiqueta "Lactosa")

#### 2.3. Comidas

**Tabla:** `comidas`

**Campos Clave:**
- `tipo_comida`: DESAYUNO | ALMUERZO | CENA | SNACK | PRE_ENTRENAMIENTO | POST_ENTRENAMIENTO | COLACION
- `tiempo_elaboracion`: Minutos de preparación

**Tabla:** `recetas` (Composición de comidas)

```
Receta = Comida + Ingrediente + Cantidad
```

**Composite Key:** `(id_comida, id_ingrediente)`

**Ejemplo:**
```sql
-- Comida: "Ensalada César"
-- Receta:
id_comida=1, id_ingrediente=5 (Lechuga),     cantidad=100.00 (gramos)
id_comida=1, id_ingrediente=12 (Pollo),      cantidad=150.00 (gramos)
id_comida=1, id_ingrediente=23 (Parmesano),  cantidad=30.00 (gramos)
id_comida=1, id_ingrediente=45 (Aceite),     cantidad=15.00 (ml)
```

**Punto Clave:** Las calorías de una comida se calculan sumando los macros de todos sus ingredientes según las cantidades.

#### 2.4. Ejercicios

**Tabla:** `ejercicios`

**Campos Clave:**
- `tipo_ejercicio`: CARDIO | FUERZA | FLEXIBILIDAD | EQUILIBRIO | HIIT | YOGA | PILATES | FUNCIONAL | DEPORTIVO | OTRO
- `musculo_principal`: PECHO | ESPALDA | HOMBROS | BRAZOS | BICEPS | TRICEPS | ABDOMINALES | PIERNAS | CUADRICEPS | GLUTEOS | GEMELOS | CARDIO | CUERPO_COMPLETO | CORE | OTRO
- `dificultad`: PRINCIPIANTE | INTERMEDIO | AVANZADO | EXPERTO
- `calorias_estimadas`: Calorías por sesión estándar
- `duracion`: Duración estándar en minutos

**Relaciones:**
- `etiquetas_ejercicios`: Many-to-many con etiquetas

---

### MÓDULO 3: PLANES NUTRICIONALES

**Propósito:** Planes de alimentación con duración fija y actividades diarias programadas

```
planes (objetivo general con duración)
  ↓
┌─────────────┴──────────────┐
│                            │
plan_objetivos          plan_dias
(macros diarios)        (actividades diarias)
│                            │
│                       ┌────┴──────┐
│                       │           │
plan_etiquetas         comida    numero_dia
(clasificación)        (catálogo) (1 a N)
```

#### 3.1. Tabla: `planes`

**Concepto:** Un plan es un OBJETIVO CON DURACIÓN FIJA que contiene actividades diarias programadas.

**Campos Clave:**
- `nombre`: "Plan Pérdida de Peso 30 días"
- `descripcion`: Descripción del plan
- `duracion_dias`: Duración total (ej: 30 días)
- `activo`: Si está disponible para asignar

**NO es:** Un catálogo de "metas sueltas"  
**SÍ es:** Un programa completo día a día

#### 3.2. Tabla: `plan_objetivos` (1-to-1 con planes)

**Concepto:** Objetivos nutricionales DIARIOS del plan.

**Campos Clave:**
- `calorias_objetivo`: Meta diaria de calorías (ej: 1500)
- `proteinas_objetivo`: Gramos diarios (ej: 120)
- `carbohidratos_objetivo`: Gramos diarios (ej: 150)
- `grasas_objetivo`: Gramos diarios (ej: 50)

**Punto Clave:** Son objetivos diarios, NO totales del plan.

#### 3.3. Tabla: `plan_dias` (Actividades diarias)

**Concepto:** Programación diaria del plan. Define QUÉ COMER CADA DÍA.

**Campos Clave:**
- `id_plan`: Plan al que pertenece
- `numero_dia`: Día del plan (1, 2, 3... hasta `duracion_dias`)
- `tipo_comida`: DESAYUNO | ALMUERZO | CENA | SNACK | etc.
- `id_comida`: Qué comida del catálogo se debe consumir
- `notas`: Instrucciones adicionales

**Ejemplo Real:**
```
Plan: "Plan Pérdida de Peso 30 días"

Día 1:
  - DESAYUNO → Avena con frutas (id_comida=15)
  - ALMUERZO → Pollo con ensalada (id_comida=42)
  - CENA → Pescado con verduras (id_comida=78)
  - SNACK → Yogurt griego (id_comida=103)

Día 2:
  - DESAYUNO → Huevos revueltos (id_comida=18)
  - ALMUERZO → Pasta integral con atún (id_comida=55)
  - ...

... (hasta Día 30)
```

**Punto Clave:** Un plan de 30 días con 4 comidas diarias = 120 registros en `plan_dias`.

#### 3.4. Tabla: `plan_etiquetas` (Clasificación)

**Concepto:** Etiquetas del plan para filtrado y búsqueda.

**Ejemplos:**
- Plan → Etiqueta "Pérdida de peso" (tipo: OBJETIVO)
- Plan → Etiqueta "Dieta Vegana" (tipo: DIETA)
- Plan → Etiqueta "1500 calorías" (tipo: OBJETIVO)
- Plan → Etiqueta "Sin gluten" (tipo: ALERGIA)

---

### MÓDULO 4: RUTINAS DE EJERCICIO

**Propósito:** Rutinas de ejercicio con duración fija y actividades programadas

```
rutinas (objetivo general con duración)
  ↓
┌─────────────┴──────────────┐
│                            │
rutina_etiquetas      rutina_ejercicios
(clasificación)       (actividades programadas)
                             │
                        ┌────┴──────┐
                        │           │
                     ejercicio    configuración
                     (catálogo)   (series, reps, peso)
```

#### 4.1. Tabla: `rutinas`

**Concepto:** Una rutina es un PROGRAMA DE EJERCICIO CON DURACIÓN FIJA.

**Campos Clave:**
- `nombre`: "Rutina Hipertrofia 12 Semanas"
- `descripcion`: Descripción del programa
- `duracion_semanas`: Duración total (ej: 12 semanas)
- `activo`: Si está disponible para asignar

**Punto Clave:** A diferencia de planes (días), rutinas usan SEMANAS como unidad.

#### 4.2. Tabla: `rutina_ejercicios` (Actividades programadas)

**Concepto:** Lista de ejercicios que componen la rutina con su configuración.

**Campos Clave:**
- `id_rutina`: Rutina a la que pertenece
- `id_ejercicio`: Ejercicio del catálogo
- `orden`: Secuencia de ejecución (1, 2, 3...)
- `series`: Número de series (ej: 4)
- `repeticiones`: Reps por serie (ej: 12)
- `peso`: Peso a utilizar en kg/lbs (opcional)
- `duracion_minutos`: Duración total del ejercicio
- `notas`: Instrucciones (ej: "Descanso 90 segundos entre series")

**Ejemplo Real:**
```
Rutina: "Rutina Hipertrofia 12 Semanas"

Ejercicio 1:
  - ejercicio: Press Banca (id_ejercicio=5)
  - orden: 1
  - series: 4
  - repeticiones: 12
  - peso: 60.00 kg
  - duracion_minutos: 15
  - notas: "Descanso 90 segundos"

Ejercicio 2:
  - ejercicio: Sentadilla (id_ejercicio=23)
  - orden: 2
  - series: 4
  - repeticiones: 10
  - peso: 100.00 kg
  - duracion_minutos: 20
  - notas: "Bajar hasta 90 grados"

Ejercicio 3:
  - ejercicio: Dominadas (id_ejercicio=8)
  - ...
```

**Punto Clave:** La rutina define el template, pero el usuario puede ajustar peso/reps cuando la ejecute.

#### 4.3. Tabla: `rutina_etiquetas` (Clasificación)

**Concepto:** Etiquetas de la rutina para filtrado.

**Ejemplos:**
- Rutina → Etiqueta "Hipertrofia" (tipo: OBJETIVO)
- Rutina → Etiqueta "Gimnasio" (tipo: TIPO_EJERCICIO)
- Rutina → Etiqueta "Intermedio" (tipo: DIFICULTAD)
- Rutina → Etiqueta "Pecho y Espalda" (tipo: GRUPO_MUSCULAR)

---

### MÓDULO 5: ASIGNACIÓN Y SEGUIMIENTO

**Propósito:** Usuarios activan planes/rutinas y registran su progreso diario

```
ASIGNACIÓN DE PLANES              ASIGNACIÓN DE RUTINAS
usuarios_planes                   usuarios_rutinas
  ↓                                  ↓
registros_comidas              registros_ejercicios
(qué comió realmente)          (qué ejercicio hizo)
```

#### 5.1. Tabla: `usuarios_planes` (Asignación de Planes)

**Concepto:** Cuando un usuario ACTIVA un plan de alimentación.

**Campos Clave:**
- `id_perfil_usuario`: Usuario que activa el plan
- `id_plan`: Plan que se activa
- `fecha_inicio`: Cuándo empieza
- `fecha_fin`: Cuándo termina (calculado: fecha_inicio + duracion_dias)
- `dia_actual`: Progreso (en qué día va: 1, 2, 3... N)
- `estado`: ACTIVO | COMPLETADO | CANCELADO | PAUSADO
- `notas`: Observaciones del usuario

**Flujo:**
```
1. Usuario escoge plan de 30 días
2. Sistema crea usuarios_planes:
   - fecha_inicio: 2025-11-01
   - fecha_fin: 2025-11-30
   - dia_actual: 1
   - estado: ACTIVO

3. Cada día que pasa:
   - dia_actual se incrementa (2, 3, 4...)
   - Sistema muestra plan_dias WHERE numero_dia = dia_actual

4. Al completar 30 días:
   - dia_actual: 30
   - estado: COMPLETADO
```

**Punto Clave:** Un usuario puede tener múltiples asignaciones (historial de planes que ha seguido).

#### 5.2. Tabla: `registros_comidas` (Seguimiento de Alimentación)

**Concepto:** Registro REAL de lo que el usuario comió cada día.

**Campos Clave:**
- `id_perfil_usuario`: Quién comió
- `id_comida`: Qué comió
- `id_usuario_plan`: Plan activo (opcional, puede ser libre)
- `fecha`: Cuándo comió
- `hora`: A qué hora
- `tipo_comida`: DESAYUNO | ALMUERZO | CENA | SNACK
- `porciones`: Cuánto comió (1.0, 1.5, 2.0 porciones)
- `calorias_consumidas`: Calorías totales (calculadas)
- `notas`: Observaciones

**Flujo:**
```
1. Sistema muestra plan del día:
   Día 5 → DESAYUNO: Avena con frutas

2. Usuario come y registra:
   - comida: Avena con frutas
   - fecha: 2025-11-05
   - hora: 08:30
   - tipo_comida: DESAYUNO
   - porciones: 1.0
   - calorias_consumidas: 350 (calculado automáticamente)
   - id_usuario_plan: 42 (vinculado al plan activo)

3. Usuario puede registrar comidas NO planificadas:
   - Mismo flujo pero id_usuario_plan = NULL
```

**Punto Clave:**
- Si `id_usuario_plan` está presente → Comida del plan
- Si `id_usuario_plan` es NULL → Comida libre (fuera del plan)

#### 5.3. Tabla: `usuarios_rutinas` (Asignación de Rutinas)

**Concepto:** Cuando un usuario ACTIVA una rutina de ejercicio.

**Campos Clave:**
- `id_perfil_usuario`: Usuario que activa
- `id_rutina`: Rutina activada
- `fecha_inicio`: Cuándo empieza
- `fecha_fin`: Cuándo termina (fecha_inicio + duracion_semanas * 7)
- `semana_actual`: Progreso (en qué semana va: 1, 2, 3... N)
- `estado`: ACTIVO | COMPLETADO | CANCELADO | PAUSADO
- `notas`: Observaciones

**Flujo:**
```
1. Usuario escoge rutina de 12 semanas
2. Sistema crea usuarios_rutinas:
   - fecha_inicio: 2025-11-01
   - fecha_fin: 2026-01-24 (12 semanas después)
   - semana_actual: 1
   - estado: ACTIVO

3. Cada semana:
   - semana_actual se incrementa (2, 3, 4...)
   - Sistema muestra ejercicios de la rutina

4. Al completar 12 semanas:
   - semana_actual: 12
   - estado: COMPLETADO
```

#### 5.4. Tabla: `registros_ejercicios` (Seguimiento de Ejercicio)

**Concepto:** Registro REAL de los ejercicios que el usuario hizo.

**Campos Clave:**
- `id_perfil_usuario`: Quién hizo el ejercicio
- `id_ejercicio`: Qué ejercicio
- `id_usuario_rutina`: Rutina activa (opcional)
- `fecha`: Cuándo lo hizo
- `hora`: A qué hora
- `series_realizadas`: Cuántas series hizo
- `repeticiones_realizadas`: Cuántas reps por serie
- `peso_utilizado`: Peso usado (kg o lbs)
- `duracion_minutos`: Duración total
- `calorias_quemadas`: Calorías estimadas
- `notas`: Observaciones

**Flujo:**
```
1. Sistema muestra rutina:
   Ejercicio 1: Press Banca (4 series x 12 reps @ 60kg)

2. Usuario entrena y registra:
   - ejercicio: Press Banca
   - fecha: 2025-11-05
   - hora: 18:00
   - series_realizadas: 4
   - repeticiones_realizadas: 12
   - peso_utilizado: 60.00 kg
   - duracion_minutos: 15
   - calorias_quemadas: 180
   - id_usuario_rutina: 15 (vinculado a rutina activa)

3. Usuario puede registrar ejercicios libres:
   - Mismo flujo pero id_usuario_rutina = NULL
```

**Punto Clave:**
- Si `id_usuario_rutina` está presente → Ejercicio de la rutina
- Si `id_usuario_rutina` es NULL → Ejercicio libre

---

## 🔄 FLUJO DE DATOS COMPLETO

### Caso de Uso: Usuario Quiere Perder Peso

#### FASE 1: Registro e Inicialización

```
1. Usuario crea cuenta
   → INSERT INTO cuentas_auth
   → INSERT INTO perfiles_usuario

2. Configurar perfil de salud
   → INSERT INTO usuario_perfil_salud
     - objetivo_actual: PERDER_PESO
     - nivel_actividad_actual: MODERADO

3. Agregar condiciones médicas/alergias
   → INSERT INTO usuario_etiquetas_salud
     - Etiqueta: "Lactosa" (tipo: ALERGIA)
     - Etiqueta: "Diabetes Tipo 2" (tipo: CONDICION_MEDICA)

4. Registrar medidas iniciales
   → INSERT INTO usuario_historial_medidas
     - peso: 85.00 kg
     - altura: 1.75 m
     - imc: 27.76 (calculado)
     - fecha_medicion: 2025-11-01
```

#### FASE 2: Búsqueda y Activación de Plan

```
5. Usuario busca planes
   → SELECT * FROM planes p
     JOIN plan_etiquetas pe ON p.id = pe.id_plan
     JOIN etiquetas e ON pe.id_etiqueta = e.id
     WHERE e.nombre IN ('Pérdida de peso', 'Sin lactosa')
     AND p.activo = true

   Resultado: "Plan Pérdida de Peso 30 días" (id=5)

6. Ver detalle del plan
   → SELECT * FROM plan_objetivos WHERE id_plan = 5
     - calorias_objetivo: 1500
     - proteinas_objetivo: 120
     - carbohidratos_objetivo: 150
     - grasas_objetivo: 50

   → SELECT * FROM plan_dias WHERE id_plan = 5 ORDER BY numero_dia, tipo_comida
     Muestra 30 días x 4 comidas = 120 actividades programadas

7. Usuario activa el plan
   → INSERT INTO usuarios_planes
     - id_plan: 5
     - id_perfil_usuario: 42
     - fecha_inicio: 2025-11-01
     - fecha_fin: 2025-11-30
     - dia_actual: 1
     - estado: ACTIVO
```

#### FASE 3: Seguimiento Día a Día

```
8. Sistema muestra plan del día actual (Día 1)
   → SELECT pd.*, c.nombre, c.tipo_comida
     FROM plan_dias pd
     JOIN comidas c ON pd.id_comida = c.id
     WHERE pd.id_plan = 5 AND pd.numero_dia = 1
     ORDER BY CASE pd.tipo_comida
       WHEN 'DESAYUNO' THEN 1
       WHEN 'ALMUERZO' THEN 2
       WHEN 'CENA' THEN 3
       WHEN 'SNACK' THEN 4
     END

   Resultado:
   - DESAYUNO: Avena con frutas
   - ALMUERZO: Pollo con ensalada
   - CENA: Pescado con verduras
   - SNACK: Yogurt griego

9. Usuario come el desayuno
   → INSERT INTO registros_comidas
     - id_perfil_usuario: 42
     - id_usuario_plan: 15 (plan activo)
     - id_comida: 25 (Avena con frutas)
     - fecha: 2025-11-01
     - hora: 08:30
     - tipo_comida: DESAYUNO
     - porciones: 1.0
     - calorias_consumidas: 350.00
     - notas: "Agregué arándanos extra"

10. Al final del día, incrementar progreso
    → UPDATE usuarios_planes
      SET dia_actual = 2
      WHERE id = 15

11. Día 2: Sistema muestra nuevas actividades
    → SELECT ... WHERE numero_dia = 2
```

#### FASE 4: Búsqueda y Activación de Rutina

```
12. Usuario busca rutinas
    → SELECT * FROM rutinas r
      JOIN rutina_etiquetas re ON r.id = re.id_rutina
      JOIN etiquetas e ON re.id_etiqueta = e.id
      WHERE e.nombre IN ('Pérdida de peso', 'Gimnasio')
      AND r.activo = true

    Resultado: "Rutina Cardio y Fuerza 8 Semanas" (id=3)

13. Ver ejercicios de la rutina
    → SELECT re.*, ej.nombre, ej.tipo_ejercicio
      FROM rutina_ejercicios re
      JOIN ejercicios ej ON re.id_ejercicio = ej.id
      WHERE re.id_rutina = 3
      ORDER BY re.orden

    Resultado:
    - Ejercicio 1: Cinta 30min (Cardio)
    - Ejercicio 2: Press Banca 4x12
    - Ejercicio 3: Sentadilla 4x10
    - ...

14. Usuario activa la rutina
    → INSERT INTO usuarios_rutinas
      - id_rutina: 3
      - id_perfil_usuario: 42
      - fecha_inicio: 2025-11-01
      - fecha_fin: 2025-12-27 (8 semanas)
      - semana_actual: 1
      - estado: ACTIVO

15. Usuario entrena
    → INSERT INTO registros_ejercicios
      - id_perfil_usuario: 42
      - id_usuario_rutina: 8
      - id_ejercicio: 45 (Press Banca)
      - fecha: 2025-11-01
      - hora: 18:00
      - series_realizadas: 4
      - repeticiones_realizadas: 12
      - peso_utilizado: 60.00
      - duracion_minutos: 15
      - calorias_quemadas: 180.00
```

#### FASE 5: Seguimiento de Progreso

```
16. Usuario actualiza peso (cada semana)
    → INSERT INTO usuario_historial_medidas
      - id_cliente: 42
      - peso: 83.00 kg (-2kg en 7 días)
      - altura: 1.75 m
      - imc: 27.10
      - fecha_medicion: 2025-11-08

17. Sistema calcula adherencia al plan
    → SELECT 
        COUNT(DISTINCT fecha) as dias_registrados,
        30 as dias_totales,
        (COUNT(DISTINCT fecha) * 100.0 / 30) as adherencia_porcentaje
      FROM registros_comidas
      WHERE id_usuario_plan = 15
      
    Resultado: 26 días registrados = 86.7% adherencia

18. Sistema calcula calorías promedio
    → SELECT AVG(calorias_consumidas) as promedio_calorias
      FROM registros_comidas
      WHERE id_usuario_plan = 15
      
    Resultado: 1520 calorías vs 1500 objetivo = +20 calorías/día

19. Gráfica de progreso de peso
    → SELECT fecha_medicion, peso, imc
      FROM usuario_historial_medidas
      WHERE id_cliente = 42
      ORDER BY fecha_medicion

    Resultado:
    - 2025-11-01: 85.00 kg, IMC 27.76
    - 2025-11-08: 83.00 kg, IMC 27.10
    - 2025-11-15: 82.00 kg, IMC 26.78
    - 2025-11-22: 81.00 kg, IMC 26.45
    - 2025-11-30: 80.00 kg, IMC 26.12 ✅ (-5kg logrado)

20. Al completar el plan
    → UPDATE usuarios_planes
      SET estado = 'COMPLETADO', dia_actual = 30
      WHERE id = 15
```

---

## 📊 ESTRUCTURA DE BASE DE DATOS

### Jerarquía de Tablas por Módulo

```
📁 MÓDULO 1: AUTENTICACIÓN Y PERFILES
├── roles (2 registros fijos: ROLE_USER, ROLE_ADMIN)
├── cuentas_auth (email, password, JWT)
├── perfiles_usuario (1-to-1 con cuentas_auth)
├── usuario_perfil_salud (1-to-1 con perfiles_usuario)
├── usuario_historial_medidas (many-to-1 con perfiles_usuario)
└── usuario_etiquetas_salud (many-to-many: usuarios ←→ etiquetas)

📁 MÓDULO 2: BIBLIOTECA DE CONTENIDO
├── etiquetas (tabla maestra de categorización)
├── ingredientes
│   └── etiquetas_ingredientes (many-to-many)
├── comidas
│   └── recetas (many-to-many con cantidades)
└── ejercicios
    └── etiquetas_ejercicios (many-to-many)

📁 MÓDULO 3: PLANES NUTRICIONALES
├── planes (objetivos con duración)
├── plan_objetivos (1-to-1 con planes)
├── plan_dias (many-to-1 con planes)
└── plan_etiquetas (many-to-many: planes ←→ etiquetas)

📁 MÓDULO 4: RUTINAS DE EJERCICIO
├── rutinas (programas con duración)
├── rutina_ejercicios (many-to-1 con rutinas)
└── rutina_etiquetas (many-to-many: rutinas ←→ etiquetas)

📁 MÓDULO 5: ASIGNACIÓN Y SEGUIMIENTO
├── usuarios_planes (asignación de planes)
│   └── registros_comidas (seguimiento diario)
└── usuarios_rutinas (asignación de rutinas)
    └── registros_ejercicios (seguimiento diario)
```

### Relaciones Críticas

#### 1. Cascada de Usuario
```
cuentas_auth (DELETE)
  ↓ CASCADE
perfiles_usuario (DELETE)
  ↓ CASCADE
├── usuario_perfil_salud (DELETE)
├── usuario_historial_medidas (DELETE)
├── usuarios_planes (DELETE)
│   ↓ CASCADE
│   registros_comidas (DELETE)
└── usuarios_rutinas (DELETE)
    ↓ CASCADE
    registros_ejercicios (DELETE)
```

**Regla:** Si se elimina un usuario, se eliminan TODOS sus datos (perfil, medidas, planes, registros).

#### 2. Integridad de Catálogos
```
planes (DELETE)
  ↓ RESTRICT (no se puede eliminar si tiene asignaciones)
usuarios_planes (asignaciones activas)

comidas (DELETE)
  ↓ RESTRICT (no se puede eliminar si está en plan_dias o registros)
├── plan_dias (planes que usan esta comida)
└── registros_comidas (usuarios que la comieron)
```

**Regla:** No se pueden eliminar elementos del catálogo si están siendo usados.

#### 3. Composite Keys (Claves Compuestas)

**Tablas con Composite Keys:**
- `recetas`: `(id_comida, id_ingrediente)`
- `usuario_etiquetas_salud`: `(id_perfil, id_etiqueta)`
- `etiquetas_ingredientes`: `(id_ingrediente, id_etiqueta)`
- `etiquetas_ejercicios`: `(id_ejercicio, id_etiqueta)`
- `plan_etiquetas`: `(id_plan, id_etiqueta)`
- `rutina_etiquetas`: `(id_rutina, id_etiqueta)`

**Nota Importante:** En el código Java, estas tablas NO necesitan @IdClass ni entidades separadas si se mapean como @ManyToMany. La relación se gestiona automáticamente.

---

## 💼 MODELO DE NEGOCIO

### Roles de Usuario

#### 1. ROLE_USER (Usuarios Finales)
**Pueden:**
- ✅ Crear cuenta y perfil
- ✅ Ver catálogo de comidas, ingredientes, ejercicios
- ✅ Ver planes y rutinas disponibles
- ✅ Activar planes/rutinas para sí mismos
- ✅ Registrar comidas y ejercicios
- ✅ Ver su propio progreso
- ✅ Actualizar su perfil de salud

**NO pueden:**
- ❌ Crear/editar comidas, ingredientes, ejercicios
- ❌ Crear/editar planes o rutinas
- ❌ Ver datos de otros usuarios
- ❌ Acceder a panel de administración

#### 2. ROLE_ADMIN (Creadores de Contenido)
**Pueden:**
- ✅ Todo lo que puede ROLE_USER
- ✅ Crear/editar/eliminar ingredientes
- ✅ Crear/editar/eliminar comidas y recetas
- ✅ Crear/editar/eliminar ejercicios
- ✅ Crear/editar/eliminar planes nutricionales
- ✅ Crear/editar/eliminar rutinas de ejercicio
- ✅ Crear/editar etiquetas
- ✅ Ver estadísticas globales (cuántos usuarios usan cada plan)

**NO pueden:**
- ❌ Ver datos privados de usuarios (peso, registros personales)
- ❌ Modificar cuentas de otros usuarios

### Funcionalidades por Rol

**ROLE_USER (Usuarios/Clientes):**
```
├── Acceso completo al catálogo de planes y rutinas
├── Activar 1 plan nutricional y 1 rutina simultáneamente
├── Registrar comidas y ejercicios (modo guiado o libre)
├── Historial completo de medidas corporales
├── Visualizar progreso y métricas de adherencia
├── Exportar reportes en PDF
└── Gestionar perfil de salud (objetivos, alergias, condiciones)
```

**ROLE_ADMIN (Administradores/Nutricionistas):**
```
├── Todas las funcionalidades de ROLE_USER
├── Crear/editar/eliminar contenido del catálogo
├── Gestionar etiquetas maestras del sistema
├── Diseñar planes nutricionales completos
├── Diseñar rutinas de ejercicio completas
├── Ver estadísticas agregadas del sistema
└── Panel de administración completo
```

---

## 🎯 CASOS DE USO PRINCIPALES

### Caso 1: Creador de Contenido (Admin) Crea un Plan

```java
// 1. Admin crea un plan
Plan plan = Plan.builder()
    .nombre("Plan Definición Muscular 45 días")
    .descripcion("Plan de alta proteína para definición")
    .duracionDias(45)
    .activo(true)
    .build();
planRepository.save(plan);

// 2. Definir objetivos del plan
PlanObjetivo objetivo = PlanObjetivo.builder()
    .plan(plan)
    .caloriasObjetivo(1800.0)
    .proteinasObjetivo(150.0)
    .carbohidratosObjetivo(120.0)
    .grasasObjetivo(60.0)
    .descripcion("Déficit calórico moderado con alta proteína")
    .build();
planObjetivoRepository.save(objetivo);

// 3. Programar actividades día a día
for (int dia = 1; dia <= 45; dia++) {
    // Desayuno
    PlanDia desayuno = PlanDia.builder()
        .plan(plan)
        .numeroDia(dia)
        .tipoComida(TipoComida.DESAYUNO)
        .comida(getComidaParaDia(dia, TipoComida.DESAYUNO))
        .notas("Tomar con agua tibia")
        .build();
    
    // Almuerzo
    PlanDia almuerzo = PlanDia.builder()
        .plan(plan)
        .numeroDia(dia)
        .tipoComida(TipoComida.ALMUERZO)
        .comida(getComidaParaDia(dia, TipoComida.ALMUERZO))
        .build();
    
    // Cena
    PlanDia cena = PlanDia.builder()
        .plan(plan)
        .numeroDia(dia)
        .tipoComida(TipoComida.CENA)
        .comida(getComidaParaDia(dia, TipoComida.CENA))
        .build();
    
    planDiaRepository.saveAll(Arrays.asList(desayuno, almuerzo, cena));
}

// 4. Agregar etiquetas para búsqueda
Etiqueta etiquetaDefinicion = etiquetaRepository.findByNombre("Definición muscular");
Etiqueta etiquetaAltoProteina = etiquetaRepository.findByNombre("Alta proteína");
plan.agregarEtiqueta(etiquetaDefinicion);
plan.agregarEtiqueta(etiquetaAltoProteina);
planRepository.save(plan);

// Total: 1 plan + 1 objetivo + (45 días x 3 comidas) = 136 registros
```

### Caso 2: Usuario Busca y Activa Plan Personalizado

```java
// 1. Usuario busca planes según sus necesidades
PerfilUsuario perfil = perfilUsuarioRepository.findByIdUsuario(userId);
UsuarioPerfilSalud salud = perfil.getPerfilSalud();

// Obtener etiquetas del usuario (alergias, objetivos)
List<Etiqueta> etiquetasUsuario = usuarioEtiquetasSaludRepository
    .findEtiquetasByPerfilId(perfil.getId());

// Buscar planes compatibles
List<Plan> planesRecomendados = planRepository.findPlanesCompatibles(
    salud.getObjetivoActual(),  // PERDER_PESO
    etiquetasUsuario,           // [Sin lactosa, Dieta mediterránea]
    salud.getNivelActividadActual()
);

// 2. Usuario ve detalle de un plan específico
Plan planSeleccionado = planesRecomendados.get(0);
PlanObjetivo objetivos = planSeleccionado.getObjetivo();

// Mostrar preview de los primeros 3 días
List<PlanDia> preview = planDiaRepository
    .findByPlanIdAndNumeroDiaBetween(planSeleccionado.getId(), 1, 3);

// 3. Usuario activa el plan
UsuarioPlan asignacion = UsuarioPlan.builder()
    .perfilUsuario(perfil)
    .plan(planSeleccionado)
    .fechaInicio(LocalDate.now())
    .fechaFin(LocalDate.now().plusDays(planSeleccionado.getDuracionDias()))
    .diaActual(1)
    .estado(EstadoAsignacion.ACTIVO)
    .notas("Empezando mi proceso de transformación")
    .build();
usuarioPlanRepository.save(asignacion);

// 4. Sistema notifica usuario
notificationService.send(
    userId,
    "¡Plan activado! Hoy es tu día 1. Revisa tu menú del día."
);
```

### Caso 3: Usuario Sigue el Plan Día a Día

```java
// Cada mañana, el sistema muestra el plan del día
UsuarioPlan planActivo = usuarioPlanRepository.findActivoPorUsuario(userId);
int diaActual = planActivo.getDiaActual();

// Obtener actividades del día
List<PlanDia> actividadesHoy = planDiaRepository
    .findByPlanIdAndNumeroDia(planActivo.getPlan().getId(), diaActual);

// Mostrar al usuario (ordenado por tipo de comida)
for (PlanDia actividad : actividadesHoy) {
    System.out.println(
        actividad.getTipoComida() + ": " + 
        actividad.getComida().getNombre() +
        (actividad.getNotas() != null ? " - " + actividad.getNotas() : "")
    );
}

// Usuario come el desayuno y registra
PlanDia desayunoPlaneado = actividadesHoy.stream()
    .filter(a -> a.getTipoComida() == TipoComida.DESAYUNO)
    .findFirst()
    .orElse(null);

RegistroComida registro = RegistroComida.builder()
    .perfilUsuario(planActivo.getPerfilUsuario())
    .usuarioPlan(planActivo)
    .comida(desayunoPlaneado.getComida())
    .fecha(LocalDate.now())
    .hora(LocalTime.now())
    .tipoComida(TipoComida.DESAYUNO)
    .porciones(BigDecimal.ONE)
    .caloriasConsumidas(calcularCalorias(desayunoPlaneado.getComida(), BigDecimal.ONE))
    .notas("Delicioso!")
    .build();
registroComidaRepository.save(registro);

// Al final del día, avanzar al siguiente
if (todosLosRegistrosDelDiaCompletados()) {
    planActivo.setDiaActual(planActivo.getDiaActual() + 1);
    usuarioPlanRepository.save(planActivo);
    
    // Si completó todos los días
    if (planActivo.getDiaActual() > planActivo.getPlan().getDuracionDias()) {
        planActivo.setEstado(EstadoAsignacion.COMPLETADO);
        usuarioPlanRepository.save(planActivo);
        
        notificationService.send(
            userId,
            "¡Felicidades! Completaste el plan. ¿Listo para el siguiente desafío?"
        );
    }
}
```

### Caso 4: Dashboard de Progreso

```java
// Métricas de adherencia
AdherenciaDTO adherencia = calcularAdherencia(userId, planActivo.getId());

class AdherenciaDTO {
    int diasTotales = planActivo.getPlan().getDuracionDias();
    int diasRegistrados = registroComidaRepository.countDiasConRegistros(planActivo.getId());
    int comidasPlaneadas = diasTotales * 4; // 4 comidas por día
    int comidasRegistradas = registroComidaRepository.countByUsuarioPlan(planActivo.getId());
    
    double adherenciaPorcentaje = (diasRegistrados * 100.0) / diasTotales;
    double completitudPorcentaje = (comidasRegistradas * 100.0) / comidasPlaneadas;
}

// Métricas nutricionales
NutricionDTO nutricion = calcularPromedioNutricional(planActivo.getId());

class NutricionDTO {
    BigDecimal caloriasObjetivo = planActivo.getPlan().getObjetivo().getCaloriasObjetivo();
    BigDecimal caloriasPromedio = registroComidaRepository.avgCalorias(planActivo.getId());
    BigDecimal desviacion = caloriasPromedio.subtract(caloriasObjetivo);
    
    BigDecimal proteinasObjetivo = planActivo.getPlan().getObjetivo().getProteinasObjetivo();
    BigDecimal proteinasPromedio = calcularPromedioProteinas();
    
    boolean cumpleObjetivos = 
        caloriasPromedio.compareTo(caloriasObjetivo.multiply(BigDecimal.valueOf(1.05))) <= 0 &&
        caloriasPromedio.compareTo(caloriasObjetivo.multiply(BigDecimal.valueOf(0.95))) >= 0;
}

// Progreso de peso
List<MedidaCorporal> historial = historialMedidasRepository
    .findByPerfilUsuarioIdOrderByFechaMedicionDesc(userId);

if (historial.size() >= 2) {
    MedidaCorporal actual = historial.get(0);
    MedidaCorporal inicial = historial.get(historial.size() - 1);
    
    BigDecimal perdidaPeso = inicial.getPeso().subtract(actual.getPeso());
    BigDecimal diasTranscurridos = ChronoUnit.DAYS.between(inicial.getFechaMedicion(), actual.getFechaMedicion());
    BigDecimal perdidaSemanal = perdidaPeso.divide(diasTranscurridos, 2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(7));
    
    System.out.println("Peso inicial: " + inicial.getPeso() + " kg");
    System.out.println("Peso actual: " + actual.getPeso() + " kg");
    System.out.println("Pérdida total: " + perdidaPeso + " kg");
    System.out.println("Pérdida semanal promedio: " + perdidaSemanal + " kg/semana");
}
```

---

## ⚖️ DIFERENCIAS CLAVE VS OTROS SISTEMAS

### ❌ Lo que NutriTrack NO es:

1. **NO es un contador de calorías simple** (como MyFitnessPal básico)
    - No solo registras lo que comes
    - Te da un PLAN COMPLETO día a día

2. **NO es un catálogo de "metas sueltas"**
    - No escoges metas individuales como: "Perder peso", "Hacer cardio"
    - Escoges PLANES COMPLETOS que ya tienen todo programado

3. **NO permite crear planes personalizados al usuario final**
    - Los usuarios ESCOGEN planes pre-creados por admins
    - No pueden modificar las actividades del plan (solo seguirlas)

4. **NO es un sistema de seguimiento libre sin estructura**
    - Todo registro puede vincularse a un plan activo
    - Hay diferencia entre seguir un plan vs registrar libremente

### ✅ Lo que NutriTrack SÍ es:

1. **Sistema de Coaching Guiado**
   ```
   Usuario: "Quiero perder peso"
   Sistema: "Aquí tienes 5 planes de 30 días que te guiarán día a día"
   Usuario: Escoge uno
   Sistema: "Hoy día 5, come: Desayuno X, Almuerzo Y, Cena Z"
   Usuario: Registra lo que comió
   Sistema: "Llevas 85% de adherencia, vas excelente"
   ```

2. **Separación Clara de Responsabilidades**
   ```
   ADMINS (Nutricionistas/Entrenadores):
   - Crean catálogo de ingredientes, comidas, ejercicios
   - Diseñan planes nutricionales completos
   - Diseñan rutinas de ejercicio completas
   - Definen objetivos y macros
   
   USUARIOS (Clientes):
   - Escogen planes/rutinas pre-diseñados
   - Siguen las actividades día a día
   - Registran su cumplimiento
   - Ven su progreso
   ```

3. **Dual Mode: Guiado vs Libre**
   ```
   MODO GUIADO (recomendado):
   - Usuario tiene plan activo
   - Registros vinculan a usuarios_planes
   - Sistema compara plan vs realidad
   - Métricas de adherencia disponibles
   
   MODO LIBRE:
   - Usuario sin plan activo
   - Registros sin vincular a plan
   - Solo seguimiento básico de calorías
   - Sin métricas de adherencia
   ```

4. **Sistema de Progreso Automático**
   ```
   Planes: dia_actual se incrementa automáticamente
   Rutinas: semana_actual se incrementa
   Sistema siempre sabe en qué punto está el usuario
   ```

---

## 📜 REGLAS DE NEGOCIO CRÍTICAS

### Regla 1: Un Plan es Inmutable una vez Activado

```
PROBLEMA: ¿Qué pasa si un admin edita un plan que ya está siendo usado?

SOLUCIÓN: Versioning implícito
- Cuando usuario activa plan, se guarda una "foto" del plan
- Si admin modifica plan original, NO afecta asignaciones activas
- usuarios_planes apunta a planes.id que no cambia
- plan_dias vincula a plan.id, pero los registros apuntan a usuarios_planes

IMPLEMENTACIÓN:
- NO eliminar plan_dias si hay usuarios_planes activos
- Marcar plan.activo = false en vez de eliminar
- Crear nueva versión del plan si se requiere modificación mayor
```

### Regla 2: Progreso Día/Semana se Incrementa Solo

```
NUNCA permitir que usuario modifique dia_actual o semana_actual manualmente

TRIGGERS PERMITIDOS:
1. Usuario completa todos los registros del día
   → dia_actual++
2. Usuario marca día como "saltado"
   → dia_actual++ pero con nota
3. Administrador puede ajustar en casos excepcionales
4. Sistema automático al cambio de fecha (cron job)
```

### Regla 3: Integridad Nutricional

```
calorias_consumidas DEBE calcularse automáticamente:

calorias_consumidas = SUM(
    ingrediente.energia * 
    receta.cantidad_ingrediente / 100 * 
    registro.porciones
) FOR ALL ingredientes IN comida

NUNCA permitir entrada manual de calorías
```

### Regla 4: Estado de Asignaciones

```
Estados permitidos: ACTIVO | COMPLETADO | CANCELADO | PAUSADO

Transiciones válidas:
  ACTIVO → COMPLETADO (al llegar a duracion_dias/semanas)
  ACTIVO → CANCELADO (usuario abandona)
  ACTIVO → PAUSADO (usuario pausa temporalmente)
  PAUSADO → ACTIVO (usuario reanuda)
  
Transiciones PROHIBIDAS:
  COMPLETADO → ACTIVO (no se puede reactivar un plan completado)
  CANCELADO → ACTIVO (debe crear nueva asignación)
```

### Regla 5: Gestión de Múltiples Planes/Rutinas

```
REGLA DE NEGOCIO:
- Usuario puede tener MÚLTIPLES planes y rutinas activos simultáneamente
- NO puede activar el MISMO plan/rutina dos veces si ya lo tiene ACTIVO
- Para volver a iniciar el MISMO plan/rutina:
  → Debe PAUSAR o CANCELAR la instancia anterior primero
- Puede tener:
  ✅ Plan A (ACTIVO) + Plan B (ACTIVO) → Diferentes planes simultáneos OK
  ✅ Rutina X (ACTIVO) + Rutina Y (ACTIVO) → Diferentes rutinas simultáneas OK
  ✅ Plan A (ACTIVO) + Plan A (PAUSADO) → Mismo plan, diferente estado OK
  ❌ Plan A (ACTIVO) + Plan A (ACTIVO) → Duplicado NO permitido

VALIDACIÓN:
Before INSERT INTO usuarios_planes (nuevo plan):
  // Verificar si el MISMO plan ya está ACTIVO
  IF EXISTS (
    SELECT 1 FROM usuarios_planes up
    WHERE up.id_perfil_usuario = X 
    AND up.id_plan = Y                    // MISMO plan
    AND up.estado = 'ACTIVO'
  ) THEN
    MOSTRAR DIÁLOGO: "Ya tienes este plan activo. ¿Deseas pausarlo/cancelarlo para reiniciarlo?"
    - SI confirma → Cambiar instancia anterior a PAUSADO, crear nueva instancia como ACTIVO
    - NO → No hacer nada
  END IF

Before INSERT INTO usuarios_rutinas (nueva rutina):
  // Verificar si la MISMA rutina ya está ACTIVA
  IF EXISTS (
    SELECT 1 FROM usuarios_rutinas ur
    WHERE ur.id_perfil_usuario = X 
    AND ur.id_rutina = Z                  // MISMA rutina
    AND ur.estado = 'ACTIVO'
  ) THEN
    MOSTRAR DIÁLOGO: "Ya tienes esta rutina activa. ¿Deseas pausarla/cancelarla para reiniciarla?"
    - SI confirma → Cambiar instancia anterior a PAUSADO, crear nueva instancia como ACTIVO
    - NO → No hacer nada
  END IF

EJEMPLOS VÁLIDOS:
Usuario puede tener simultáneamente:
- Plan "Pérdida de Peso 30 días" (ACTIVO)
- Plan "Definición Muscular 45 días" (ACTIVO)
- Rutina "Hipertrofia 12 semanas" (ACTIVO)
- Rutina "Cardio Intensivo 8 semanas" (ACTIVO)

EJEMPLO INVÁLIDO:
Usuario NO puede tener:
- Plan "Pérdida de Peso 30 días" (ACTIVO, día 5)
- Plan "Pérdida de Peso 30 días" (ACTIVO, día 1) ❌ Duplicado
```

### Regla 6: Privacidad de Datos

```
USUARIOS pueden ver:
- ✅ Su propio perfil, medidas, registros
- ✅ Catálogo público (comidas, ejercicios, planes, rutinas)

USUARIOS NO pueden ver:
- ❌ Datos de otros usuarios
- ❌ Estadísticas de uso de planes por otros
- ❌ Panel de administración

ADMINS pueden ver:
- ✅ Catálogo completo (crear/editar)
- ✅ Estadísticas agregadas (cuántos usuarios usan plan X)

ADMINS NO pueden ver:
- ❌ Datos personales de usuarios (peso, registros individuales)
- ❌ Información médica de usuarios
```

### Regla 7: Eliminación de Datos

```
SOFT DELETE (marcar como inactivo):
- Planes: activo = false
- Rutinas: activo = false
- Comidas: soft delete con flag is_deleted
- Ejercicios: soft delete con flag is_deleted

HARD DELETE (eliminación física):
- Registros de usuarios (por solicitud GDPR)
- Cuentas de usuarios inactivos > 2 años

CASCADE DELETE:
- cuentas_auth → perfiles_usuario → TODO lo del usuario
```

### Regla 8: Validación de Recetas

```
Una comida DEBE tener al menos 1 ingrediente:

Before INSERT INTO comidas:
  No validación aquí (comida puede existir sin receta temporalmente)

Before SET comida.activo = true:
  IF COUNT(recetas WHERE id_comida = X) < 1
  THEN REJECT "Una comida debe tener al menos un ingrediente"
```

### Regla 9: Coherencia de Fechas

```
usuarios_planes:
  fecha_fin DEBE SER fecha_inicio + duracion_dias

usuarios_rutinas:
  fecha_fin DEBE SER fecha_inicio + (duracion_semanas * 7)

registros_comidas:
  fecha DEBE estar entre fecha_inicio y fecha_fin del plan
  (si está vinculado a un plan)

usuario_historial_medidas:
  NO permitir fecha_medicion en el futuro
```

### Regla 10: Unidades de Medida Consistentes

```
perfiles_usuario.unidades_medida = 'KG' | 'LBS'

ALL peso fields DEBEN almacenarse en base de datos en KG
ALL altura fields DEBEN almacenarse en CM

CONVERSION en API Layer:
- Input: Usuario envía en su unidad preferida
- Storage: Convertir a KG/CM para almacenar
- Output: Convertir a unidad preferida del usuario
```

---

## 🎓 RESUMEN EJECUTIVO

### Para Desarrolladores Nuevos:

**Cuando construyas o mantengas este proyecto, recuerda:**

1. **Planes y Rutinas son OBJETIVOS COMPLETOS, no actividades sueltas**
    - Un plan contiene TODAS las actividades de todos los días
    - Usuario escoge el plan completo, no arma su propio plan

2. **Hay 3 niveles de datos:**
    - **CATÁLOGO**: Templates reutilizables (planes, rutinas, comidas, ejercicios)
    - **ASIGNACIÓN**: Usuario activa un template (usuarios_planes, usuarios_rutinas)
    - **REGISTRO**: Usuario reporta lo que hizo (registros_comidas, registros_ejercicios)

3. **Sistema de Etiquetas es TRANSVERSAL:**
    - Una tabla `etiquetas` categoriza TODO
    - 7 tipos diferentes de etiquetas
    - Permite búsqueda y filtrado muy flexible

4. **Progreso es AUTOMÁTICO:**
    - `dia_actual` y `semana_actual` se incrementan automáticamente
    - Estados cambian automáticamente (ACTIVO → COMPLETADO)
    - Usuario solo registra, sistema calcula progreso

5. **Modo Dual: Guiado vs Libre:**
    - Con plan activo: registros vinculados, métricas de adherencia
    - Sin plan activo: registro libre, solo seguimiento básico

### Para Product Owners:

**El valor del sistema está en:**

1. **Expertise Encapsulado:** Nutricionistas/entrenadores crean planes profesionales que usuarios finales siguen
2. **Guía Día a Día:** No más "¿qué como hoy?", el plan lo dice claramente
3. **Accountability:** Sistema compara plan vs realidad, genera métricas objetivas
4. **Escalabilidad:** Un plan creado = infinitos usuarios pueden usarlo
5. **Enfoque Educativo:** Proyecto completo con todas las funcionalidades disponibles para aprendizaje práctico

---

## 📚 REFERENCIAS TÉCNICAS

### Stack Tecnológico

- **Backend:** Spring Boot 3.5.7, Java 21
- **ORM:** Hibernate 6.6.33 (JPA)
- **Database:** PostgreSQL 16.10
- **Auth:** JWT Bearer Token
- **API Docs:** Swagger/OpenAPI 3.0
- **Build:** Maven

### Archivos Clave

```
SQL/NutriDB.sql                  → Schema completo de base de datos
src/main/java/com/.../model/     → Entidades JPA
src/main/java/com/.../dto/       → Data Transfer Objects
src/main/java/com/.../service/   → Lógica de negocio
src/main/java/com/.../controller/→ API REST endpoints
```

### Documentos Relacionados

- **[USER_STORIES.MD](./USER_STORIES.MD)** - Las 25 User Stories que definen la funcionalidad del sistema
- **[REGLAS_NEGOCIO.MD](./REGLAS_NEGOCIO.MD)** - Las 24 reglas de negocio que deben implementarse

### Endpoints Principales

```
POST   /api/v1/auth/registro      → Crear cuenta
POST   /api/v1/auth/login         → Iniciar sesión
GET    /api/v1/planes              → Listar planes disponibles
POST   /api/v1/usuarios-planes    → Activar un plan
GET    /api/v1/planes/{id}/dias/{dia} → Ver actividades del día
POST   /api/v1/registros-comidas  → Registrar comida
GET    /api/v1/usuarios-planes/{id}/progreso → Ver progreso
```

---

**Última Actualización:** 4 de Noviembre, 2025  
**Versión del Documento:** 1.0  
**Propósito:** Documentación base del modelo de negocio NutriTrack

---

> **💡 Nota:** Este documento describe el modelo de negocio y la arquitectura del sistema. Es la referencia principal para entender cómo funciona NutriTrack y sus reglas de negocio fundamentales.