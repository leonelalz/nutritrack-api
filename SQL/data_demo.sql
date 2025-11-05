-- ============================================================================
-- SCRIPT DE DATOS DE DEMOSTRACIÓN - NutriTrack API
-- Módulos 1 y 2: Autenticación y Gestión de Perfil
-- ============================================================================

-- Nota: El admin (ID=1) y demo (ID=2) ya están creados por la aplicación
-- Este script solo agrega datos de salud y mediciones

-- ============================================================================
-- PERFIL DE SALUD - ADMIN (Módulo 2 - US-04)
-- ============================================================================
-- Usuario: admin@nutritrack.com
-- Objetivo: Mantener forma física
-- Nivel de actividad: Alto (hace ejercicio 5-6 días/semana)
-- ============================================================================

INSERT INTO usuario_perfil_salud (id_perfil, objetivo_actual, nivel_actividad_actual, fecha_actualizacion) 
VALUES (1, 'MANTENER_FORMA', 'ALTO', NOW())
ON CONFLICT (id_perfil) DO UPDATE 
SET objetivo_actual = 'MANTENER_FORMA', 
    nivel_actividad_actual = 'ALTO',
    fecha_actualizacion = NOW();

-- ============================================================================
-- HISTORIAL DE MEDIDAS - ADMIN (Módulo 2 - US-06)
-- ============================================================================
-- Progresión de peso estable (mantenimiento)
-- Altura: 175 cm
-- Peso inicial: 70 kg → Peso actual: 70.5 kg (mantenimiento exitoso)
-- ============================================================================

DELETE FROM usuario_historial_medidas WHERE id_cliente = 1;

INSERT INTO usuario_historial_medidas (id_cliente, fecha_medicion, peso, altura) VALUES
-- Septiembre 2025 - inicio de seguimiento
(1, '2025-09-01', 70.0, 175),
(1, '2025-09-08', 70.2, 175),
(1, '2025-09-15', 69.8, 175),
(1, '2025-09-22', 70.1, 175),
(1, '2025-09-29', 70.0, 175),

-- Octubre 2025 - continuación
(1, '2025-10-06', 70.3, 175),
(1, '2025-10-13', 70.0, 175),
(1, '2025-10-20', 70.2, 175),
(1, '2025-10-27', 70.4, 175),

-- Noviembre 2025 - hasta la fecha actual
(1, '2025-11-03', 70.5, 175),
(1, '2025-11-04', 70.5, 175);

-- ============================================================================
-- PERFIL DE SALUD - DEMO (Módulo 2 - US-04)
-- ============================================================================
-- Usuario: demo@nutritrack.com
-- Objetivo: Perder peso
-- Nivel de actividad: Moderado (hace ejercicio 3-4 días/semana)
-- ============================================================================

INSERT INTO usuario_perfil_salud (id_perfil, objetivo_actual, nivel_actividad_actual, fecha_actualizacion) 
VALUES (2, 'PERDER_PESO', 'MODERADO', NOW())
ON CONFLICT (id_perfil) DO UPDATE 
SET objetivo_actual = 'PERDER_PESO', 
    nivel_actividad_actual = 'MODERADO',
    fecha_actualizacion = NOW();

-- ============================================================================
-- HISTORIAL DE MEDIDAS - DEMO (Módulo 2 - US-06)
-- ============================================================================
-- Progresión de pérdida de peso exitosa
-- Altura: 168 cm
-- Peso inicial: 78 kg → Peso actual: 72.5 kg (pérdida de 5.5 kg)
-- ============================================================================

DELETE FROM usuario_historial_medidas WHERE id_cliente = 2;

INSERT INTO usuario_historial_medidas (id_cliente, fecha_medicion, peso, altura) VALUES
-- Septiembre 2025 - inicio del programa
(2, '2025-09-01', 78.0, 168),
(2, '2025-09-08', 77.5, 168),
(2, '2025-09-15', 77.0, 168),
(2, '2025-09-22', 76.8, 168),
(2, '2025-09-29', 76.2, 168),

-- Octubre 2025 - progreso continuo
(2, '2025-10-05', 75.5, 168),
(2, '2025-10-12', 75.0, 168),
(2, '2025-10-19', 74.5, 168),
(2, '2025-10-26', 73.8, 168),

-- Noviembre 2025 - acercándose a la meta
(2, '2025-11-02', 73.0, 168),
(2, '2025-11-04', 72.5, 168);

-- ============================================================================
-- MÓDULO 3: PLANES Y RUTINAS (Catálogo de Entrenamiento)
-- ============================================================================
-- RN11: Los planes deben tener nombres únicos
-- RN14: Las rutinas deben tener nombres únicos
-- RN28: Solo elementos activos pueden ser asignados
-- ============================================================================

-- ============================================================================
-- PLANES DE ENTRENAMIENTO (Módulo 3 - US-11 a US-14)
-- ============================================================================

-- Plan 1: Pérdida de Grasa - 8 semanas
INSERT INTO planes (nombre, descripcion, duracion_dias, activo) VALUES
('Plan Pérdida de Grasa Efectiva', 
 'Programa completo de 8 semanas diseñado para maximizar la quema de grasa mediante entrenamiento HIIT y fuerza. Combina ejercicio cardiovascular intenso con trabajo de tonificación muscular.',
 56, true);

-- Plan 2: Aumento de Masa Muscular - 12 semanas
INSERT INTO planes (nombre, descripcion, duracion_dias, activo) VALUES
('Plan Hipertrofia Muscular Avanzado',
 'Programa intensivo de 12 semanas enfocado en ganancia de masa muscular magra. Utiliza técnicas de sobrecarga progresiva con entrenamiento de fuerza dividido por grupos musculares.',
 84, true);

-- Plan 3: Definición Muscular - 6 semanas
INSERT INTO planes (nombre, descripcion, duracion_dias, activo) VALUES
('Plan Definición y Tonificación',
 'Programa de 6 semanas para definir y tonificar el cuerpo. Ideal para quienes buscan reducir grasa corporal mientras mantienen la masa muscular mediante entrenamiento metabólico.',
 42, true);

-- Plan 4: Mantenimiento - 4 semanas (INACTIVO - para demostrar RN28)
INSERT INTO planes (nombre, descripcion, duracion_dias, activo) VALUES
('Plan Mantenimiento Básico',
 'Programa de 4 semanas para mantener la forma física actual. Entrenamiento equilibrado de intensidad moderada.',
 28, false);

-- ============================================================================
-- OBJETIVOS DE LOS PLANES (Relación Many-to-Many)
-- ============================================================================

-- Plan 1: Pérdida de Grasa → Objetivos PERDER_PESO y MEJORAR_RESISTENCIA
INSERT INTO plan_objetivos (id_plan, objetivo) 
SELECT id, 'PERDER_PESO' FROM planes WHERE nombre = 'Plan Pérdida de Grasa Efectiva';

INSERT INTO plan_objetivos (id_plan, objetivo) 
SELECT id, 'MEJORAR_RESISTENCIA' FROM planes WHERE nombre = 'Plan Pérdida de Grasa Efectiva';

-- Plan 2: Hipertrofia → Objetivos GANAR_MUSCULO y MEJORAR_FUERZA
INSERT INTO plan_objetivos (id_plan, objetivo) 
SELECT id, 'GANAR_MUSCULO' FROM planes WHERE nombre = 'Plan Hipertrofia Muscular Avanzado';

INSERT INTO plan_objetivos (id_plan, objetivo) 
SELECT id, 'MEJORAR_FUERZA' FROM planes WHERE nombre = 'Plan Hipertrofia Muscular Avanzado';

-- Plan 3: Definición → Objetivos TONIFICAR y PERDER_PESO
INSERT INTO plan_objetivos (id_plan, objetivo) 
SELECT id, 'TONIFICAR' FROM planes WHERE nombre = 'Plan Definición y Tonificación';

INSERT INTO plan_objetivos (id_plan, objetivo) 
SELECT id, 'PERDER_PESO' FROM planes WHERE nombre = 'Plan Definición y Tonificación';

-- Plan 4: Mantenimiento → Objetivo MANTENER_FORMA
INSERT INTO plan_objetivos (id_plan, objetivo) 
SELECT id, 'MANTENER_FORMA' FROM planes WHERE nombre = 'Plan Mantenimiento Básico';

-- ============================================================================
-- RUTINAS DE EJERCICIO (Módulo 3 - US-11 a US-15)
-- ============================================================================

-- Rutina 1: HIIT Quema Grasa
INSERT INTO rutinas (nombre, descripcion, nivel_dificultad, duracion_estimada_min, activo) VALUES
('HIIT Quema Grasa Intenso',
 'Entrenamiento de intervalos de alta intensidad diseñado para maximizar la quema de calorías. Alterna períodos cortos de esfuerzo máximo con descansos activos.',
 'INTERMEDIO', 30, true);

-- Rutina 2: Fuerza Tren Superior
INSERT INTO rutinas (nombre, descripcion, nivel_dificultad, duracion_estimada_min, activo) VALUES
('Fuerza Tren Superior Completo',
 'Rutina de hipertrofia enfocada en pecho, espalda, hombros y brazos. Utiliza ejercicios compuestos para maximizar la ganancia muscular.',
 'AVANZADO', 60, true);

-- Rutina 3: Fuerza Tren Inferior
INSERT INTO rutinas (nombre, descripcion, nivel_dificultad, duracion_estimada_min, activo) VALUES
('Fuerza Tren Inferior Potencia',
 'Entrenamiento intenso para piernas y glúteos. Combina ejercicios de fuerza con trabajo de potencia para desarrollo muscular completo.',
 'AVANZADO', 55, true);

-- Rutina 4: Cardio Moderado
INSERT INTO rutinas (nombre, descripcion, nivel_dificultad, duracion_estimada_min, activo) VALUES
('Cardio Moderado Resistencia',
 'Sesión cardiovascular de intensidad moderada para mejorar la resistencia aeróbica y quemar calorías de forma sostenida.',
 'PRINCIPIANTE', 40, true);

-- Rutina 5: Core y Abdominales
INSERT INTO rutinas (nombre, descripcion, nivel_dificultad, duracion_estimada_min, activo) VALUES
('Core Funcional y Abdominales',
 'Rutina especializada en fortalecimiento del core, abdominales y zona lumbar. Esencial para estabilidad y prevención de lesiones.',
 'INTERMEDIO', 25, true);

-- Rutina 6: Movilidad (INACTIVA - para demostrar RN28)
INSERT INTO rutinas (nombre, descripcion, nivel_dificultad, duracion_estimada_min, activo) VALUES
('Movilidad y Flexibilidad',
 'Sesión de estiramientos y movilidad articular.',
 'PRINCIPIANTE', 20, false);

-- ============================================================================
-- OBJETIVOS DE LAS RUTINAS (Relación Many-to-Many)
-- ============================================================================

-- Rutina 1: HIIT → PERDER_PESO, MEJORAR_RESISTENCIA
INSERT INTO rutina_objetivos (id_rutina, objetivo) 
SELECT id, 'PERDER_PESO' FROM rutinas WHERE nombre = 'HIIT Quema Grasa Intenso';

INSERT INTO rutina_objetivos (id_rutina, objetivo) 
SELECT id, 'MEJORAR_RESISTENCIA' FROM rutinas WHERE nombre = 'HIIT Quema Grasa Intenso';

-- Rutina 2: Tren Superior → GANAR_MUSCULO, MEJORAR_FUERZA
INSERT INTO rutina_objetivos (id_rutina, objetivo) 
SELECT id, 'GANAR_MUSCULO' FROM rutinas WHERE nombre = 'Fuerza Tren Superior Completo';

INSERT INTO rutina_objetivos (id_rutina, objetivo) 
SELECT id, 'MEJORAR_FUERZA' FROM rutinas WHERE nombre = 'Fuerza Tren Superior Completo';

-- Rutina 3: Tren Inferior → GANAR_MUSCULO, MEJORAR_FUERZA
INSERT INTO rutina_objetivos (id_rutina, objetivo) 
SELECT id, 'GANAR_MUSCULO' FROM rutinas WHERE nombre = 'Fuerza Tren Inferior Potencia';

INSERT INTO rutina_objetivos (id_rutina, objetivo) 
SELECT id, 'MEJORAR_FUERZA' FROM rutinas WHERE nombre = 'Fuerza Tren Inferior Potencia';

-- Rutina 4: Cardio → MEJORAR_RESISTENCIA, PERDER_PESO
INSERT INTO rutina_objetivos (id_rutina, objetivo) 
SELECT id, 'MEJORAR_RESISTENCIA' FROM rutinas WHERE nombre = 'Cardio Moderado Resistencia';

INSERT INTO rutina_objetivos (id_rutina, objetivo) 
SELECT id, 'PERDER_PESO' FROM rutinas WHERE nombre = 'Cardio Moderado Resistencia';

-- Rutina 5: Core → TONIFICAR, MEJORAR_FUERZA
INSERT INTO rutina_objetivos (id_rutina, objetivo) 
SELECT id, 'TONIFICAR' FROM rutinas WHERE nombre = 'Core Funcional y Abdominales';

INSERT INTO rutina_objetivos (id_rutina, objetivo) 
SELECT id, 'MEJORAR_FUERZA' FROM rutinas WHERE nombre = 'Core Funcional y Abdominales';

-- Rutina 6: Movilidad → MANTENER_FORMA
INSERT INTO rutina_objetivos (id_rutina, objetivo) 
SELECT id, 'MANTENER_FORMA' FROM rutinas WHERE nombre = 'Movilidad y Flexibilidad';

-- ============================================================================
-- DÍAS DE LOS PLANES CON RUTINAS ASIGNADAS (US-17, US-21)
-- ============================================================================

-- PLAN 1: PÉRDIDA DE GRASA (8 semanas = 56 días, 6 días/semana)
-- Semana tipo: HIIT-Superior-HIIT-Inferior-HIIT-Core (Descanso domingo)

-- Día 1: HIIT
INSERT INTO plan_dias (id_plan, numero_dia, tipo_actividad, id_rutina) 
SELECT p.id, 1, 'EJERCICIO', r.id 
FROM planes p, rutinas r 
WHERE p.nombre = 'Plan Pérdida de Grasa Efectiva' 
AND r.nombre = 'HIIT Quema Grasa Intenso';

-- Día 2: Tren Superior
INSERT INTO plan_dias (id_plan, numero_dia, tipo_actividad, id_rutina) 
SELECT p.id, 2, 'EJERCICIO', r.id 
FROM planes p, rutinas r 
WHERE p.nombre = 'Plan Pérdida de Grasa Efectiva' 
AND r.nombre = 'Fuerza Tren Superior Completo';

-- Día 3: HIIT
INSERT INTO plan_dias (id_plan, numero_dia, tipo_actividad, id_rutina) 
SELECT p.id, 3, 'EJERCICIO', r.id 
FROM planes p, rutinas r 
WHERE p.nombre = 'Plan Pérdida de Grasa Efectiva' 
AND r.nombre = 'HIIT Quema Grasa Intenso';

-- Día 4: Tren Inferior
INSERT INTO plan_dias (id_plan, numero_dia, tipo_actividad, id_rutina) 
SELECT p.id, 4, 'EJERCICIO', r.id 
FROM planes p, rutinas r 
WHERE p.nombre = 'Plan Pérdida de Grasa Efectiva' 
AND r.nombre = 'Fuerza Tren Inferior Potencia';

-- Día 5: HIIT
INSERT INTO plan_dias (id_plan, numero_dia, tipo_actividad, id_rutina) 
SELECT p.id, 5, 'EJERCICIO', r.id 
FROM planes p, rutinas r 
WHERE p.nombre = 'Plan Pérdida de Grasa Efectiva' 
AND r.nombre = 'HIIT Quema Grasa Intenso';

-- Día 6: Core
INSERT INTO plan_dias (id_plan, numero_dia, tipo_actividad, id_rutina) 
SELECT p.id, 6, 'EJERCICIO', r.id 
FROM planes p, rutinas r 
WHERE p.nombre = 'Plan Pérdida de Grasa Efectiva' 
AND r.nombre = 'Core Funcional y Abdominales';

-- Día 7: Descanso
INSERT INTO plan_dias (id_plan, numero_dia, tipo_actividad, id_rutina) 
SELECT p.id, 7, 'DESCANSO', NULL 
FROM planes p 
WHERE p.nombre = 'Plan Pérdida de Grasa Efectiva';

-- PLAN 2: HIPERTROFIA MUSCULAR (12 semanas, 5 días/semana)
-- Semana tipo: Superior-Inferior-Superior-Inferior-Core (Sáb-Dom descanso)

-- Día 1: Tren Superior
INSERT INTO plan_dias (id_plan, numero_dia, tipo_actividad, id_rutina) 
SELECT p.id, 1, 'EJERCICIO', r.id 
FROM planes p, rutinas r 
WHERE p.nombre = 'Plan Hipertrofia Muscular Avanzado' 
AND r.nombre = 'Fuerza Tren Superior Completo';

-- Día 2: Tren Inferior
INSERT INTO plan_dias (id_plan, numero_dia, tipo_actividad, id_rutina) 
SELECT p.id, 2, 'EJERCICIO', r.id 
FROM planes p, rutinas r 
WHERE p.nombre = 'Plan Hipertrofia Muscular Avanzado' 
AND r.nombre = 'Fuerza Tren Inferior Potencia';

-- Día 3: Tren Superior
INSERT INTO plan_dias (id_plan, numero_dia, tipo_actividad, id_rutina) 
SELECT p.id, 3, 'EJERCICIO', r.id 
FROM planes p, rutinas r 
WHERE p.nombre = 'Plan Hipertrofia Muscular Avanzado' 
AND r.nombre = 'Fuerza Tren Superior Completo';

-- Día 4: Tren Inferior
INSERT INTO plan_dias (id_plan, numero_dia, tipo_actividad, id_rutina) 
SELECT p.id, 4, 'EJERCICIO', r.id 
FROM planes p, rutinas r 
WHERE p.nombre = 'Plan Hipertrofia Muscular Avanzado' 
AND r.nombre = 'Fuerza Tren Inferior Potencia';

-- Día 5: Core
INSERT INTO plan_dias (id_plan, numero_dia, tipo_actividad, id_rutina) 
SELECT p.id, 5, 'EJERCICIO', r.id 
FROM planes p, rutinas r 
WHERE p.nombre = 'Plan Hipertrofia Muscular Avanzado' 
AND r.nombre = 'Core Funcional y Abdominales';

-- Día 6-7: Descanso
INSERT INTO plan_dias (id_plan, numero_dia, tipo_actividad, id_rutina) 
SELECT p.id, 6, 'DESCANSO', NULL 
FROM planes p 
WHERE p.nombre = 'Plan Hipertrofia Muscular Avanzado';

INSERT INTO plan_dias (id_plan, numero_dia, tipo_actividad, id_rutina) 
SELECT p.id, 7, 'DESCANSO', NULL 
FROM planes p 
WHERE p.nombre = 'Plan Hipertrofia Muscular Avanzado';

-- PLAN 3: DEFINICIÓN (6 semanas, rutina mixta)

-- Día 1: HIIT
INSERT INTO plan_dias (id_plan, numero_dia, tipo_actividad, id_rutina) 
SELECT p.id, 1, 'EJERCICIO', r.id 
FROM planes p, rutinas r 
WHERE p.nombre = 'Plan Definición y Tonificación' 
AND r.nombre = 'HIIT Quema Grasa Intenso';

-- Día 2: Cardio Moderado
INSERT INTO plan_dias (id_plan, numero_dia, tipo_actividad, id_rutina) 
SELECT p.id, 2, 'EJERCICIO', r.id 
FROM planes p, rutinas r 
WHERE p.nombre = 'Plan Definición y Tonificación' 
AND r.nombre = 'Cardio Moderado Resistencia';

-- Día 3: Tren Superior
INSERT INTO plan_dias (id_plan, numero_dia, tipo_actividad, id_rutina) 
SELECT p.id, 3, 'EJERCICIO', r.id 
FROM planes p, rutinas r 
WHERE p.nombre = 'Plan Definición y Tonificación' 
AND r.nombre = 'Fuerza Tren Superior Completo';

-- Día 4: Core
INSERT INTO plan_dias (id_plan, numero_dia, tipo_actividad, id_rutina) 
SELECT p.id, 4, 'EJERCICIO', r.id 
FROM planes p, rutinas r 
WHERE p.nombre = 'Plan Definición y Tonificación' 
AND r.nombre = 'Core Funcional y Abdominales';

-- Día 5: Tren Inferior
INSERT INTO plan_dias (id_plan, numero_dia, tipo_actividad, id_rutina) 
SELECT p.id, 5, 'EJERCICIO', r.id 
FROM planes p, rutinas r 
WHERE p.nombre = 'Plan Definición y Tonificación' 
AND r.nombre = 'Fuerza Tren Inferior Potencia';

-- Día 6-7: Descanso
INSERT INTO plan_dias (id_plan, numero_dia, tipo_actividad, id_rutina) 
SELECT p.id, 6, 'DESCANSO', NULL 
FROM planes p 
WHERE p.nombre = 'Plan Definición y Tonificación';

INSERT INTO plan_dias (id_plan, numero_dia, tipo_actividad, id_rutina) 
SELECT p.id, 7, 'DESCANSO', NULL 
FROM planes p 
WHERE p.nombre = 'Plan Definición y Tonificación';

-- ============================================================================
-- EJERCICIOS EN RUTINAS (US-15 - Ensamblar Ejercicios)
-- ============================================================================
-- Nota: Usamos ejercicios existentes del catálogo (Módulo 2)
-- ============================================================================

-- RUTINA 1: HIIT QUEMA GRASA (30 min)
-- Calentamiento + 5 ejercicios + Vuelta a la calma

INSERT INTO rutina_ejercicios (id_rutina, id_ejercicio, orden, series, repeticiones, duracion_segundos, peso_kg, notas)
SELECT r.id, e.id, 1, 1, NULL, 300, NULL, 'Calentamiento dinámico - 5 minutos'
FROM rutinas r, ejercicios e
WHERE r.nombre = 'HIIT Quema Grasa Intenso' AND e.nombre = 'Saltos de tijera (Jumping Jacks)';

INSERT INTO rutina_ejercicios (id_rutina, id_ejercicio, orden, series, repeticiones, duracion_segundos, peso_kg, notas)
SELECT r.id, e.id, 2, 4, 15, NULL, NULL, '30 seg trabajo / 15 seg descanso'
FROM rutinas r, ejercicios e
WHERE r.nombre = 'HIIT Quema Grasa Intenso' AND e.nombre = 'Burpees';

INSERT INTO rutina_ejercicios (id_rutina, id_ejercicio, orden, series, repeticiones, duracion_segundos, peso_kg, notas)
SELECT r.id, e.id, 3, 4, 20, NULL, NULL, '30 seg trabajo / 15 seg descanso'
FROM rutinas r, ejercicios e
WHERE r.nombre = 'HIIT Quema Grasa Intenso' AND e.nombre = 'Mountain Climbers';

INSERT INTO rutina_ejercicios (id_rutina, id_ejercicio, orden, series, repeticiones, duracion_segundos, peso_kg, notas)
SELECT r.id, e.id, 4, 4, NULL, 45, NULL, '45 seg trabajo / 15 seg descanso'
FROM rutinas r, ejercicios e
WHERE r.nombre = 'HIIT Quema Grasa Intenso' AND e.nombre = 'Saltos de tijera (Jumping Jacks)';

INSERT INTO rutina_ejercicios (id_rutina, id_ejercicio, orden, series, repeticiones, duracion_segundos, peso_kg, notas)
SELECT r.id, e.id, 5, 3, 12, NULL, NULL, 'Cada lado - Explosivo'
FROM rutinas r, ejercicios e
WHERE r.nombre = 'HIIT Quema Grasa Intenso' AND e.nombre = 'Zancadas (Lunges)';

INSERT INTO rutina_ejercicios (id_rutina, id_ejercicio, orden, series, repeticiones, duracion_segundos, peso_kg, notas)
SELECT r.id, e.id, 6, 1, NULL, 300, NULL, 'Estiramiento suave - 5 minutos'
FROM rutinas r, ejercicios e
WHERE r.nombre = 'HIIT Quema Grasa Intenso' AND e.nombre = 'Plancha (Plank)';

-- RUTINA 2: FUERZA TREN SUPERIOR (60 min)

INSERT INTO rutina_ejercicios (id_rutina, id_ejercicio, orden, series, repeticiones, duracion_segundos, peso_kg, notas)
SELECT r.id, e.id, 1, 4, 8, NULL, NULL, 'Peso corporal - Forma estricta'
FROM rutinas r, ejercicios e
WHERE r.nombre = 'Fuerza Tren Superior Completo' AND e.nombre = 'Flexiones de pecho (Push-ups)';

INSERT INTO rutina_ejercicios (id_rutina, id_ejercicio, orden, series, repeticiones, duracion_segundos, peso_kg, notas)
SELECT r.id, e.id, 2, 4, 10, NULL, NULL, 'Agarre ancho - Contracción completa'
FROM rutinas r, ejercicios e
WHERE r.nombre = 'Fuerza Tren Superior Completo' AND e.nombre = 'Dominadas (Pull-ups)';

INSERT INTO rutina_ejercicios (id_rutina, id_ejercicio, orden, series, repeticiones, duracion_segundos, peso_kg, notas)
SELECT r.id, e.id, 3, 3, 12, NULL, 15.0, 'Por brazo - Control total'
FROM rutinas r, ejercicios e
WHERE r.nombre = 'Fuerza Tren Superior Completo' AND e.nombre = 'Curl de bíceps con mancuernas';

INSERT INTO rutina_ejercicios (id_rutina, id_ejercicio, orden, series, repeticiones, duracion_segundos, peso_kg, notas)
SELECT r.id, e.id, 4, 3, 15, NULL, NULL, 'Brazos extendidos - Sin balanceo'
FROM rutinas r, ejercicios e
WHERE r.nombre = 'Fuerza Tren Superior Completo' AND e.nombre = 'Fondos en banco (Dips)';

-- RUTINA 3: FUERZA TREN INFERIOR (55 min)

INSERT INTO rutina_ejercicios (id_rutina, id_ejercicio, orden, series, repeticiones, duracion_segundos, peso_kg, notas)
SELECT r.id, e.id, 1, 5, 10, NULL, 60.0, 'Profundidad completa - Barra libre'
FROM rutinas r, ejercicios e
WHERE r.nombre = 'Fuerza Tren Inferior Potencia' AND e.nombre = 'Sentadillas (Squats)';

INSERT INTO rutina_ejercicios (id_rutina, id_ejercicio, orden, series, repeticiones, duracion_segundos, peso_kg, notas)
SELECT r.id, e.id, 2, 4, 12, NULL, 40.0, 'Cada pierna - Paso amplio'
FROM rutinas r, ejercicios e
WHERE r.nombre = 'Fuerza Tren Inferior Potencia' AND e.nombre = 'Zancadas (Lunges)';

INSERT INTO rutina_ejercicios (id_rutina, id_ejercicio, orden, series, repeticiones, duracion_segundos, peso_kg, notas)
SELECT r.id, e.id, 3, 3, 20, NULL, NULL, 'Explosivo - Máxima altura'
FROM rutinas r, ejercicios e
WHERE r.nombre = 'Fuerza Tren Inferior Potencia' AND e.nombre = 'Sentadilla con salto (Jump Squats)';

-- RUTINA 4: CARDIO MODERADO (40 min)

INSERT INTO rutina_ejercicios (id_rutina, id_ejercicio, orden, series, repeticiones, duracion_segundos, peso_kg, notas)
SELECT r.id, e.id, 1, 1, NULL, 600, NULL, 'Calentamiento progresivo'
FROM rutinas r, ejercicios e
WHERE r.nombre = 'Cardio Moderado Resistencia' AND e.nombre = 'Saltos de tijera (Jumping Jacks)';

INSERT INTO rutina_ejercicios (id_rutina, id_ejercicio, orden, series, repeticiones, duracion_segundos, peso_kg, notas)
SELECT r.id, e.id, 2, 3, NULL, 600, NULL, '10 min por serie - Ritmo constante'
FROM rutinas r, ejercicios e
WHERE r.nombre = 'Cardio Moderado Resistencia' AND e.nombre = 'Trote en el lugar (Jogging in Place)';

INSERT INTO rutina_ejercicios (id_rutina, id_ejercicio, orden, series, repeticiones, duracion_segundos, peso_kg, notas)
SELECT r.id, e.id, 3, 2, 30, NULL, NULL, 'Ritmo moderado - 2 series'
FROM rutinas r, ejercicios e
WHERE r.nombre = 'Cardio Moderado Resistencia' AND e.nombre = 'Mountain Climbers';

-- RUTINA 5: CORE Y ABDOMINALES (25 min)

INSERT INTO rutina_ejercicios (id_rutina, id_ejercicio, orden, series, repeticiones, duracion_segundos, peso_kg, notas)
SELECT r.id, e.id, 1, 4, NULL, 60, NULL, 'Forma perfecta - 1 minuto hold'
FROM rutinas r, ejercicios e
WHERE r.nombre = 'Core Funcional y Abdominales' AND e.nombre = 'Plancha (Plank)';

INSERT INTO rutina_ejercicios (id_rutina, id_ejercicio, orden, series, repeticiones, duracion_segundos, peso_kg, notas)
SELECT r.id, e.id, 2, 3, 20, NULL, NULL, 'Contracción completa - Sin impulso'
FROM rutinas r, ejercicios e
WHERE r.nombre = 'Core Funcional y Abdominales' AND e.nombre = 'Abdominales (Crunches)';

INSERT INTO rutina_ejercicios (id_rutina, id_ejercicio, orden, series, repeticiones, duracion_segundos, peso_kg, notas)
SELECT r.id, e.id, 3, 3, 15, NULL, NULL, 'Cada lado - Rotación controlada'
FROM rutinas r, ejercicios e
WHERE r.nombre = 'Core Funcional y Abdominales' AND e.nombre = 'Giros rusos (Russian Twists)';

INSERT INTO rutina_ejercicios (id_rutina, id_ejercicio, orden, series, repeticiones, duracion_segundos, peso_kg, notas)
SELECT r.id, e.id, 4, 3, 15, NULL, NULL, 'Elevación controlada - Sin balanceo'
FROM rutinas r, ejercicios e
WHERE r.nombre = 'Core Funcional y Abdominales' AND e.nombre = 'Elevación de piernas';

-- ============================================================================
-- VERIFICACIÓN DE DATOS
-- ============================================================================
-- Ejecutar estas consultas para confirmar que los datos están correctos
-- ============================================================================

-- ============================================================================
-- VERIFICACIÓN DE DATOS - MÓDULO 3
-- ============================================================================

-- Ver todos los planes con sus objetivos
SELECT 
    p.id,
    p.nombre,
    p.duracion_dias,
    p.activo,
    STRING_AGG(po.objetivo::text, ', ') as objetivos
FROM planes p
LEFT JOIN plan_objetivos po ON p.id = po.id_plan
GROUP BY p.id, p.nombre, p.duracion_dias, p.activo
ORDER BY p.id;

-- Ver todas las rutinas con sus objetivos
SELECT 
    r.id,
    r.nombre,
    r.nivel_dificultad,
    r.duracion_estimada_min,
    r.activo,
    STRING_AGG(ro.objetivo::text, ', ') as objetivos
FROM rutinas r
LEFT JOIN rutina_objetivos ro ON r.id = ro.id_rutina
GROUP BY r.id, r.nombre, r.nivel_dificultad, r.duracion_estimada_min, r.activo
ORDER BY r.id;

-- Ver días del Plan 1 con sus rutinas asignadas
SELECT 
    pd.numero_dia,
    pd.tipo_actividad,
    r.nombre as rutina,
    r.duracion_estimada_min
FROM plan_dias pd
LEFT JOIN rutinas r ON pd.id_rutina = r.id
JOIN planes p ON pd.id_plan = p.id
WHERE p.nombre = 'Plan Pérdida de Grasa Efectiva'
ORDER BY pd.numero_dia;

-- Ver ejercicios de la rutina HIIT
SELECT 
    re.orden,
    e.nombre as ejercicio,
    re.series,
    re.repeticiones,
    re.duracion_segundos,
    re.peso_kg,
    re.notas
FROM rutina_ejercicios re
JOIN ejercicios e ON re.id_ejercicio = e.id
JOIN rutinas r ON re.id_rutina = r.id
WHERE r.nombre = 'HIIT Quema Grasa Intenso'
ORDER BY re.orden;

-- Conteo de días por plan
SELECT 
    p.nombre,
    COUNT(pd.id) as total_dias,
    SUM(CASE WHEN pd.tipo_actividad = 'EJERCICIO' THEN 1 ELSE 0 END) as dias_ejercicio,
    SUM(CASE WHEN pd.tipo_actividad = 'DESCANSO' THEN 1 ELSE 0 END) as dias_descanso
FROM planes p
LEFT JOIN plan_dias pd ON p.id = pd.id_plan
GROUP BY p.nombre
ORDER BY p.nombre;

-- Conteo de ejercicios por rutina
SELECT 
    r.nombre,
    COUNT(re.id) as total_ejercicios,
    SUM(re.series) as total_series
FROM rutinas r
LEFT JOIN rutina_ejercicios re ON r.id = re.id_rutina
GROUP BY r.nombre
ORDER BY r.nombre;

-- ============================================================================
-- VERIFICACIÓN DE DATOS - MÓDULOS 1 Y 2
-- ============================================================================

-- Verificar perfiles de salud
SELECT 
    ca.email,
    pu.nombre,
    pu.apellido,
    ups.objetivo_actual,
    ups.nivel_actividad_actual,
    ups.fecha_actualizacion
FROM cuentas_auth ca
JOIN perfiles_usuario pu ON ca.id = pu.id_usuario
JOIN usuario_perfil_salud ups ON pu.id = ups.id_perfil
ORDER BY ca.id;

-- Verificar cantidad de mediciones por usuario
SELECT 
    ca.email,
    COUNT(uhm.id) as total_mediciones,
    MIN(uhm.fecha_medicion) as primera_medicion,
    MAX(uhm.fecha_medicion) as ultima_medicion,
    MIN(uhm.peso) as peso_inicial,
    MAX(uhm.peso) as peso_final
FROM cuentas_auth ca
JOIN perfiles_usuario pu ON ca.id = pu.id_usuario
LEFT JOIN usuario_historial_medidas uhm ON pu.id = uhm.id_cliente
GROUP BY ca.email
ORDER BY ca.id;

-- Verificar últimas 5 mediciones de cada usuario
SELECT 
    ca.email,
    uhm.fecha_medicion,
    uhm.peso,
    uhm.altura
FROM cuentas_auth ca
JOIN perfiles_usuario pu ON ca.id = pu.id_usuario
JOIN usuario_historial_medidas uhm ON pu.id = uhm.id_cliente
ORDER BY ca.id, uhm.fecha_medicion DESC;

-- ============================================================================
-- RESUMEN DE USUARIOS DE PRUEBA
-- ============================================================================
/*
┌─────────────────────────────────────────────────────────────────────────┐
│ USUARIO ADMIN (Administrador del Sistema)                              │
├─────────────────────────────────────────────────────────────────────────┤
│ Email:        admin@nutritrack.com                                     │
│ Password:     Admin123!                                                │
│ Role:         ROLE_ADMIN                                               │
│ Objetivo:     Mantener forma física                                    │
│ Actividad:    Alto (5-6 días/semana)                                   │
│ Peso inicial: 70.0 kg → Actual: 70.5 kg                               │
│ Altura:       175 cm                                                   │
│ IMC:          23.0 (Peso normal)                                       │
│ Mediciones:   11 registros (Sep-Nov 2025)                             │
└─────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────┐
│ USUARIO DEMO (Usuario Regular)                                         │
├─────────────────────────────────────────────────────────────────────────┤
│ Email:        demo@nutritrack.com                                      │
│ Password:     Demo123!                                                 │
│ Role:         ROLE_USER                                                │
│ Objetivo:     Perder peso                                              │
│ Actividad:    Moderado (3-4 días/semana)                              │
│ Peso inicial: 78.0 kg → Actual: 72.5 kg (-5.5 kg) 🎯                │
│ Altura:       168 cm                                                   │
│ IMC inicial:  27.6 (Sobrepeso) → Actual: 25.7 (Sobrepeso leve)       │
│ Mediciones:   11 registros (Sep-Nov 2025)                             │
└─────────────────────────────────────────────────────────────────────────┘

-- ============================================================================
-- RESUMEN DE USUARIOS DE PRUEBA
-- ============================================================================
/*
┌─────────────────────────────────────────────────────────────────────────┐
│ USUARIO ADMIN (Administrador del Sistema)                              │
├─────────────────────────────────────────────────────────────────────────┤
│ Email:        admin@nutritrack.com                                     │
│ Password:     Admin123!                                                │
│ Role:         ROLE_ADMIN                                               │
│ Objetivo:     Mantener forma física                                    │
│ Actividad:    Alto (5-6 días/semana)                                   │
│ Peso inicial: 70.0 kg → Actual: 70.5 kg                               │
│ Altura:       175 cm                                                   │
│ IMC:          23.0 (Peso normal)                                       │
│ Mediciones:   11 registros (Sep-Nov 2025)                             │
└─────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────┐
│ USUARIO DEMO (Usuario Regular)                                         │
├─────────────────────────────────────────────────────────────────────────┤
│ Email:        demo@nutritrack.com                                      │
│ Password:     Demo123!                                                 │
│ Role:         ROLE_USER                                                │
│ Objetivo:     Perder peso                                              │
│ Actividad:    Moderado (3-4 días/semana)                              │
│ Peso inicial: 78.0 kg → Actual: 72.5 kg (-5.5 kg) 🎯                │
│ Altura:       168 cm                                                   │
│ IMC inicial:  27.6 (Sobrepeso) → Actual: 25.7 (Sobrepeso leve)       │
│ Mediciones:   11 registros (Sep-Nov 2025)                             │
└─────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────┐
│ CATÁLOGO MÓDULO 3 - PLANES Y RUTINAS                                  │
├─────────────────────────────────────────────────────────────────────────┤
│ PLANES ACTIVOS:                                                        │
│  1. Plan Pérdida de Grasa Efectiva (8 semanas)                       │
│     - Objetivos: PERDER_PESO, MEJORAR_RESISTENCIA                     │
│     - 7 días configurados (6 ejercicio + 1 descanso)                  │
│     - Rutinas: HIIT, Tren Superior, Tren Inferior, Core               │
│                                                                         │
│  2. Plan Hipertrofia Muscular Avanzado (12 semanas)                  │
│     - Objetivos: GANAR_MUSCULO, MEJORAR_FUERZA                        │
│     - 7 días configurados (5 ejercicio + 2 descanso)                  │
│     - Rutinas: Tren Superior, Tren Inferior, Core (alternados)        │
│                                                                         │
│  3. Plan Definición y Tonificación (6 semanas)                       │
│     - Objetivos: TONIFICAR, PERDER_PESO                               │
│     - 7 días configurados (5 ejercicio + 2 descanso)                  │
│     - Rutinas: HIIT, Cardio, Tren Superior, Inferior, Core            │
│                                                                         │
│ PLAN INACTIVO (para demostrar RN28):                                  │
│  4. Plan Mantenimiento Básico (4 semanas) - INACTIVO                 │
│                                                                         │
│ RUTINAS ACTIVAS:                                                       │
│  1. HIIT Quema Grasa Intenso (30 min, INTERMEDIO)                    │
│     - 6 ejercicios configurados                                        │
│     - Objetivos: PERDER_PESO, MEJORAR_RESISTENCIA                     │
│                                                                         │
│  2. Fuerza Tren Superior Completo (60 min, AVANZADO)                 │
│     - 4 ejercicios: Flexiones, Dominadas, Curls, Fondos              │
│     - Objetivos: GANAR_MUSCULO, MEJORAR_FUERZA                        │
│                                                                         │
│  3. Fuerza Tren Inferior Potencia (55 min, AVANZADO)                 │
│     - 3 ejercicios: Sentadillas, Zancadas, Jump Squats               │
│     - Objetivos: GANAR_MUSCULO, MEJORAR_FUERZA                        │
│                                                                         │
│  4. Cardio Moderado Resistencia (40 min, PRINCIPIANTE)               │
│     - 3 ejercicios cardiovasculares                                    │
│     - Objetivos: MEJORAR_RESISTENCIA, PERDER_PESO                     │
│                                                                         │
│  5. Core Funcional y Abdominales (25 min, INTERMEDIO)                │
│     - 4 ejercicios: Plancha, Crunches, Russian Twists, Leg Raises    │
│     - Objetivos: TONIFICAR, MEJORAR_FUERZA                            │
│                                                                         │
│ RUTINA INACTIVA (para demostrar RN28):                                │
│  6. Movilidad y Flexibilidad (20 min) - INACTIVA                     │
└─────────────────────────────────────────────────────────────────────────┘

FUNCIONALIDADES DISPONIBLES PARA PRUEBAS:

✅ Módulo 1 - Autenticación y Seguridad
   • POST /api/v1/auth/register   - Registro de nuevos usuarios
   • POST /api/v1/auth/login      - Inicio de sesión con JWT
   • DELETE /api/v1/auth/account  - Eliminación de cuenta (requiere "ELIMINAR")

✅ Módulo 2 - Gestión de Perfil y Salud
   • GET    /api/v1/perfil/salud      - Obtener perfil de salud actual
   • POST   /api/v1/perfil/salud      - Crear perfil por primera vez
   • PUT    /api/v1/perfil/salud      - Actualizar perfil de salud
   • GET    /api/v1/perfil/mediciones - Obtener historial de mediciones
   • POST   /api/v1/perfil/mediciones - Registrar nueva medición
   • DELETE /api/v1/perfil/mediciones/{id} - Eliminar medición

✅ Módulo 3 - Gestor de Catálogo (ADMIN únicamente)
   PLANES:
   • POST   /api/admin/planes          - Crear nuevo plan
   • GET    /api/admin/planes          - Listar todos los planes
   • GET    /api/admin/planes/activos  - Listar solo planes activos
   • GET    /api/admin/planes/{id}     - Obtener plan por ID
   • PUT    /api/admin/planes/{id}     - Actualizar plan
   • DELETE /api/admin/planes/{id}     - Eliminar plan (soft delete)
   • POST   /api/admin/planes/{id}/dias - Agregar día al plan
   • GET    /api/admin/planes/{id}/dias - Listar días del plan
   • DELETE /api/admin/planes/{planId}/dias/{diaId} - Eliminar día
   
   RUTINAS:
   • POST   /api/admin/rutinas         - Crear nueva rutina
   • GET    /api/admin/rutinas         - Listar todas las rutinas
   • GET    /api/admin/rutinas/activas - Listar solo rutinas activas
   • GET    /api/admin/rutinas/{id}    - Obtener rutina por ID
   • PUT    /api/admin/rutinas/{id}    - Actualizar rutina
   • DELETE /api/admin/rutinas/{id}    - Eliminar rutina (soft delete)
   • POST   /api/admin/rutinas/{id}/ejercicios - Agregar ejercicio a rutina
   • GET    /api/admin/rutinas/{id}/ejercicios - Listar ejercicios de rutina
   • PUT    /api/admin/rutinas/{rutinaId}/ejercicios/{ejercicioId} - Actualizar
   • DELETE /api/admin/rutinas/{rutinaId}/ejercicios/{ejercicioId} - Eliminar

NOTAS IMPORTANTES:
• Ambos usuarios tienen datos completos para demostrar todas las funcionalidades
• El usuario admin demuestra mantenimiento de peso estable
• El usuario demo demuestra pérdida de peso progresiva exitosa
• Todos los endpoints requieren autenticación JWT (excepto register y login)
• Las mediciones muestran progresión realista a lo largo de 2 meses
• Módulo 3: Solo ADMIN puede gestionar planes y rutinas (RN11, RN14, RN28)
• Hay 3 planes activos y 1 inactivo para demostrar validación RN28
• Hay 5 rutinas activas y 1 inactiva para demostrar validación RN28
• Cada plan tiene múltiples días configurados con rutinas asignadas
• Cada rutina tiene ejercicios del catálogo ensamblados con detalles
*/
