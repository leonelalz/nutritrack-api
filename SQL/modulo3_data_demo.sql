-- ============================================================================
-- DATOS DE DEMOSTRACIÓN - MÓDULO 3
-- Planes de Nutrición y Rutinas de Ejercicio
-- Compatible con NutriDB.sql
-- ============================================================================

-- ============================================================================
-- PLANES DE NUTRICIÓN (Módulo 3)
-- ============================================================================

INSERT INTO planes (nombre, descripcion, duracion_dias, activo) VALUES
('Plan Pérdida de Grasa Efectiva', 
 'Programa completo de 8 semanas diseñado para maximizar la quema de grasa mediante déficit calórico controlado. Combina alimentación balanceada con control de macronutrientes.',
 56, true);

INSERT INTO planes (nombre, descripcion, duracion_dias, activo) VALUES
('Plan Ganancia Muscular Avanzado',
 'Programa intensivo de 12 semanas enfocado en ganancia de masa muscular magra. Utiliza superávit calórico controlado con énfasis en proteínas.',
 84, true);

INSERT INTO planes (nombre, descripcion, duracion_dias, activo) VALUES
('Plan Definición y Tonificación',
 'Programa de 6 semanas para definir y tonificar el cuerpo. Ideal para reducir grasa corporal mientras se mantiene la masa muscular.',
 42, true);

INSERT INTO planes (nombre, descripcion, duracion_dias, activo) VALUES
('Plan Mantenimiento Básico',
 'Programa de 4 semanas para mantener la forma física actual. Alimentación equilibrada de mantenimiento.',
 28, false);

-- ============================================================================
-- OBJETIVOS DE PLANES (plan_objetivos - 1 to 1)
-- ============================================================================

INSERT INTO plan_objetivos (id_plan, calorias_objetivo, proteinas_objetivo, carbohidratos_objetivo, grasas_objetivo, descripcion)
SELECT id, 1800.00, 150.00, 180.00, 60.00, 'Déficit calórico moderado para pérdida de grasa sostenible'
FROM planes WHERE nombre = 'Plan Pérdida de Grasa Efectiva';

INSERT INTO plan_objetivos (id_plan, calorias_objetivo, proteinas_objetivo, carbohidratos_objetivo, grasas_objetivo, descripcion)
SELECT id, 3200.00, 200.00, 400.00, 90.00, 'Superávit calórico para hipertrofia muscular con alto contenido proteico'
FROM planes WHERE nombre = 'Plan Ganancia Muscular Avanzado';

INSERT INTO plan_objetivos (id_plan, calorias_objetivo, proteinas_objetivo, carbohidratos_objetivo, grasas_objetivo, descripcion)
SELECT id, 2000.00, 160.00, 200.00, 65.00, 'Balance calórico para definición muscular manteniendo masa magra'
FROM planes WHERE nombre = 'Plan Definición y Tonificación';

INSERT INTO plan_objetivos (id_plan, calorias_objetivo, proteinas_objetivo, carbohidratos_objetivo, grasas_objetivo, descripcion)
SELECT id, 2400.00, 120.00, 300.00, 80.00, 'Mantenimiento de peso y composición corporal actual'
FROM planes WHERE nombre = 'Plan Mantenimiento Básico';

-- ============================================================================
-- RUTINAS DE EJERCICIO (Módulo 3)
-- ============================================================================

INSERT INTO rutinas (nombre, descripcion, duracion_semanas, activo) VALUES
('HIIT Quema Grasa Intenso',
 'Entrenamiento de intervalos de alta intensidad diseñado para maximizar la quema de calorías en sesiones cortas de 30 minutos.',
 4, true);

INSERT INTO rutinas (nombre, descripcion, duracion_semanas, activo) VALUES
('Fuerza Tren Superior Completo',
 'Rutina de hipertrofia enfocada en pecho, espalda, hombros y brazos. Utiliza ejercicios compuestos para máxima ganancia muscular.',
 8, true);

INSERT INTO rutinas (nombre, descripcion, duracion_semanas, activo) VALUES
('Fuerza Tren Inferior Potencia',
 'Entrenamiento intenso para piernas y glúteos. Combina ejercicios de fuerza con trabajo de potencia para desarrollo completo.',
 8, true);

INSERT INTO rutinas (nombre, descripcion, duracion_semanas, activo) VALUES
('Cardio Moderado Resistencia',
 'Sesión cardiovascular de intensidad moderada para mejorar la resistencia aeróbica y quemar calorías de forma sostenida.',
 4, true);

INSERT INTO rutinas (nombre, descripcion, duracion_semanas, activo) VALUES
('Core Funcional y Abdominales',
 'Rutina especializada en fortalecimiento del core, abdominales y zona lumbar. Esencial para estabilidad corporal.',
 6, true);

INSERT INTO rutinas (nombre, descripcion, duracion_semanas, activo) VALUES
('Movilidad y Flexibilidad',
 'Sesión de estiramientos y movilidad articular para recuperación activa.',
 4, false);

-- ============================================================================
-- PLAN DÍAS - Plan 1: Pérdida de Grasa (semana tipo)
-- ============================================================================
-- Nota: Usar comidas ya existentes en el catálogo

-- Día 1: Desayuno
INSERT INTO plan_dias (id_plan, numero_dia, tipo_comida, id_comida, notas)
SELECT p.id, 1, 'DESAYUNO', c.id, 'Desayuno alto en proteínas y fibra'
FROM planes p, comidas c
WHERE p.nombre = 'Plan Pérdida de Grasa Efectiva'
AND c.nombre = 'Avena con frutas y almendras'
LIMIT 1;

-- Día 1: Almuerzo
INSERT INTO plan_dias (id_plan, numero_dia, tipo_comida, id_comida, notas)
SELECT p.id, 1, 'ALMUERZO', c.id, 'Comida principal con proteína magra'
FROM planes p, comidas c
WHERE p.nombre = 'Plan Pérdida de Grasa Efectiva'
AND c.nombre = 'Ensalada de pollo a la parrilla'
LIMIT 1;

-- Día 1: Cena
INSERT INTO plan_dias (id_plan, numero_dia, tipo_comida, id_comida, notas)
SELECT p.id, 1, 'CENA', c.id, 'Cena ligera con vegetales'
FROM planes p, comidas c
WHERE p.nombre = 'Plan Pérdida de Grasa Efectiva'
AND c.nombre = 'Pescado al horno con verduras'
LIMIT 1;

-- Día 1: Snack
INSERT INTO plan_dias (id_plan, numero_dia, tipo_comida, id_comida, notas)
SELECT p.id, 1, 'SNACK', c.id, 'Colación saludable media tarde'
FROM planes p, comidas c
WHERE p.nombre = 'Plan Pérdida de Grasa Efectiva'
AND c.nombre = 'Yogur griego con nueces'
LIMIT 1;

-- Día 2: Repetir estructura similar
INSERT INTO plan_dias (id_plan, numero_dia, tipo_comida, id_comida, notas)
SELECT p.id, 2, 'DESAYUNO', c.id, 'Desayuno energético pre-entrenamiento'
FROM planes p, comidas c
WHERE p.nombre = 'Plan Pérdida de Grasa Efectiva'
AND c.nombre = 'Huevos revueltos con pan integral'
LIMIT 1;

INSERT INTO plan_dias (id_plan, numero_dia, tipo_comida, id_comida, notas)
SELECT p.id, 2, 'ALMUERZO', c.id, 'Almuerzo balanceado con carbohidratos complejos'
FROM planes p, comidas c
WHERE p.nombre = 'Plan Pérdida de Grasa Efectiva'
AND c.nombre = 'Arroz integral con pollo y vegetales'
LIMIT 1;

-- ============================================================================
-- PLAN DÍAS - Plan 2: Ganancia Muscular (día ejemplo)
-- ============================================================================

INSERT INTO plan_dias (id_plan, numero_dia, tipo_comida, id_comida, notas)
SELECT p.id, 1, 'DESAYUNO', c.id, 'Desayuno alto en calorías y proteínas'
FROM planes p, comidas c
WHERE p.nombre = 'Plan Ganancia Muscular Avanzado'
AND c.nombre = 'Avena con frutas y almendras'
LIMIT 1;

INSERT INTO plan_dias (id_plan, numero_dia, tipo_comida, id_comida, notas)
SELECT p.id, 1, 'PRE_ENTRENAMIENTO', c.id, 'Comida pre-entrenamiento energética'
FROM planes p, comidas c
WHERE p.nombre = 'Plan Ganancia Muscular Avanzado'
AND c.nombre = 'Batido de proteína con plátano'
LIMIT 1;

INSERT INTO plan_dias (id_plan, numero_dia, tipo_comida, id_comida, notas)
SELECT p.id, 1, 'POST_ENTRENAMIENTO', c.id, 'Comida post-entrenamiento para recuperación'
FROM planes p, comidas c
WHERE p.nombre = 'Plan Ganancia Muscular Avanzado'
AND c.nombre = 'Arroz integral con pollo y vegetales'
LIMIT 1;

-- ============================================================================
-- RUTINA EJERCICIOS - Rutina 1: HIIT Quema Grasa
-- ============================================================================

INSERT INTO rutina_ejercicios (id_rutina, id_ejercicio, orden, series, repeticiones, duracion_minutos, peso, notas)
SELECT r.id, e.id, 1, 1, 0, 5, NULL, 'Calentamiento dinámico - 5 minutos'
FROM rutinas r, ejercicios e
WHERE r.nombre = 'HIIT Quema Grasa Intenso'
AND e.nombre = 'Saltos de tijera (Jumping Jacks)'
LIMIT 1;

INSERT INTO rutina_ejercicios (id_rutina, id_ejercicio, orden, series, repeticiones, duracion_minutos, peso, notas)
SELECT r.id, e.id, 2, 4, 15, 3, NULL, '30 seg trabajo / 15 seg descanso - 4 rondas'
FROM rutinas r, ejercicios e
WHERE r.nombre = 'HIIT Quema Grasa Intenso'
AND e.nombre = 'Burpees'
LIMIT 1;

INSERT INTO rutina_ejercicios (id_rutina, id_ejercicio, orden, series, repeticiones, duracion_minutos, peso, notas)
SELECT r.id, e.id, 3, 4, 20, 3, NULL, '30 seg trabajo / 15 seg descanso - 4 rondas'
FROM rutinas r, ejercicios e
WHERE r.nombre = 'HIIT Quema Grasa Intenso'
AND e.nombre = 'Mountain Climbers'
LIMIT 1;

INSERT INTO rutina_ejercicios (id_rutina, id_ejercicio, orden, series, repeticiones, duracion_minutos, peso, notas)
SELECT r.id, e.id, 4, 4, 25, 4, NULL, '45 seg trabajo / 15 seg descanso - 4 rondas'
FROM rutinas r, ejercicios e
WHERE r.nombre = 'HIIT Quema Grasa Intenso'
AND e.nombre = 'Saltos de tijera (Jumping Jacks)'
LIMIT 1;

INSERT INTO rutina_ejercicios (id_rutina, id_ejercicio, orden, series, repeticiones, duracion_minutos, peso, notas)
SELECT r.id, e.id, 5, 3, 12, 4, NULL, 'Cada lado - Explosivo - 3 series'
FROM rutinas r, ejercicios e
WHERE r.nombre = 'HIIT Quema Grasa Intenso'
AND e.nombre = 'Zancadas (Lunges)'
LIMIT 1;

INSERT INTO rutina_ejercicios (id_rutina, id_ejercicio, orden, series, repeticiones, duracion_minutos, peso, notas)
SELECT r.id, e.id, 6, 1, 0, 5, NULL, 'Vuelta a la calma - Estiramiento 5 minutos'
FROM rutinas r, ejercicios e
WHERE r.nombre = 'HIIT Quema Grasa Intenso'
AND e.nombre = 'Plancha (Plank)'
LIMIT 1;

-- ============================================================================
-- RUTINA EJERCICIOS - Rutina 2: Fuerza Tren Superior
-- ============================================================================

INSERT INTO rutina_ejercicios (id_rutina, id_ejercicio, orden, series, repeticiones, duracion_minutos, peso, notas)
SELECT r.id, e.id, 1, 4, 12, 8, NULL, 'Peso corporal - Forma estricta - 4x12'
FROM rutinas r, ejercicios e
WHERE r.nombre = 'Fuerza Tren Superior Completo'
AND e.nombre = 'Flexiones de pecho (Push-ups)'
LIMIT 1;

INSERT INTO rutina_ejercicios (id_rutina, id_ejercicio, orden, series, repeticiones, duracion_minutos, peso, notas)
SELECT r.id, e.id, 2, 4, 8, 10, NULL, 'Agarre ancho - Contracción completa - 4x8'
FROM rutinas r, ejercicios e
WHERE r.nombre = 'Fuerza Tren Superior Completo'
AND e.nombre = 'Dominadas (Pull-ups)'
LIMIT 1;

INSERT INTO rutina_ejercicios (id_rutina, id_ejercicio, orden, series, repeticiones, duracion_minutos, peso, notas)
SELECT r.id, e.id, 3, 3, 12, 6, 15.0, 'Por brazo - Control total - 3x12 cada lado'
FROM rutinas r, ejercicios e
WHERE r.nombre = 'Fuerza Tren Superior Completo'
AND e.nombre = 'Curl de bíceps con mancuernas'
LIMIT 1;

INSERT INTO rutina_ejercicios (id_rutina, id_ejercicio, orden, series, repeticiones, duracion_minutos, peso, notas)
SELECT r.id, e.id, 4, 3, 15, 6, NULL, 'Brazos extendidos - Sin balanceo - 3x15'
FROM rutinas r, ejercicios e
WHERE r.nombre = 'Fuerza Tren Superior Completo'
AND e.nombre = 'Fondos en banco (Dips)'
LIMIT 1;

-- ============================================================================
-- RUTINA EJERCICIOS - Rutina 3: Fuerza Tren Inferior
-- ============================================================================

INSERT INTO rutina_ejercicios (id_rutina, id_ejercicio, orden, series, repeticiones, duracion_minutos, peso, notas)
SELECT r.id, e.id, 1, 5, 10, 15, 60.0, 'Profundidad completa - Barra libre - 5x10'
FROM rutinas r, ejercicios e
WHERE r.nombre = 'Fuerza Tren Inferior Potencia'
AND e.nombre = 'Sentadillas (Squats)'
LIMIT 1;

INSERT INTO rutina_ejercicios (id_rutina, id_ejercicio, orden, series, repeticiones, duracion_minutos, peso, notas)
SELECT r.id, e.id, 2, 4, 12, 10, 40.0, 'Cada pierna - Paso amplio - 4x12 cada lado'
FROM rutinas r, ejercicios e
WHERE r.nombre = 'Fuerza Tren Inferior Potencia'
AND e.nombre = 'Zancadas (Lunges)'
LIMIT 1;

INSERT INTO rutina_ejercicios (id_rutina, id_ejercicio, orden, series, repeticiones, duracion_minutos, peso, notas)
SELECT r.id, e.id, 3, 3, 20, 8, NULL, 'Explosivo - Máxima altura - 3x20'
FROM rutinas r, ejercicios e
WHERE r.nombre = 'Fuerza Tren Inferior Potencia'
AND e.nombre = 'Sentadilla con salto (Jump Squats)'
LIMIT 1;

-- ============================================================================
-- RUTINA EJERCICIOS - Rutina 4: Cardio Moderado
-- ============================================================================

INSERT INTO rutina_ejercicios (id_rutina, id_ejercicio, orden, series, repeticiones, duracion_minutos, peso, notas)
SELECT r.id, e.id, 1, 1, 0, 10, NULL, 'Calentamiento progresivo - 10 minutos'
FROM rutinas r, ejercicios e
WHERE r.nombre = 'Cardio Moderado Resistencia'
AND e.nombre = 'Saltos de tijera (Jumping Jacks)'
LIMIT 1;

INSERT INTO rutina_ejercicios (id_rutina, id_ejercicio, orden, series, repeticiones, duracion_minutos, peso, notas)
SELECT r.id, e.id, 2, 3, 0, 20, NULL, 'Ritmo constante - 3 bloques de 10 min'
FROM rutinas r, ejercicios e
WHERE r.nombre = 'Cardio Moderado Resistencia'
AND e.nombre = 'Trote en el lugar (Jogging in Place)'
LIMIT 1;

INSERT INTO rutina_ejercicios (id_rutina, id_ejercicio, orden, series, repeticiones, duracion_minutos, peso, notas)
SELECT r.id, e.id, 3, 2, 30, 5, NULL, 'Ritmo moderado - 2 series de 30 reps'
FROM rutinas r, ejercicios e
WHERE r.nombre = 'Cardio Moderado Resistencia'
AND e.nombre = 'Mountain Climbers'
LIMIT 1;

-- ============================================================================
-- RUTINA EJERCICIOS - Rutina 5: Core y Abdominales
-- ============================================================================

INSERT INTO rutina_ejercicios (id_rutina, id_ejercicio, orden, series, repeticiones, duracion_minutos, peso, notas)
SELECT r.id, e.id, 1, 4, 0, 6, NULL, 'Forma perfecta - 1 minuto hold - 4 series'
FROM rutinas r, ejercicios e
WHERE r.nombre = 'Core Funcional y Abdominales'
AND e.nombre = 'Plancha (Plank)'
LIMIT 1;

INSERT INTO rutina_ejercicios (id_rutina, id_ejercicio, orden, series, repeticiones, duracion_minutos, peso, notas)
SELECT r.id, e.id, 2, 3, 20, 5, NULL, 'Contracción completa - Sin impulso - 3x20'
FROM rutinas r, ejercicios e
WHERE r.nombre = 'Core Funcional y Abdominales'
AND e.nombre = 'Abdominales (Crunches)'
LIMIT 1;

INSERT INTO rutina_ejercicios (id_rutina, id_ejercicio, orden, series, repeticiones, duracion_minutos, peso, notas)
SELECT r.id, e.id, 3, 3, 15, 5, NULL, 'Cada lado - Rotación controlada - 3x15 cada lado'
FROM rutinas r, ejercicios e
WHERE r.nombre = 'Core Funcional y Abdominales'
AND e.nombre = 'Giros rusos (Russian Twists)'
LIMIT 1;

INSERT INTO rutina_ejercicios (id_rutina, id_ejercicio, orden, series, repeticiones, duracion_minutos, peso, notas)
SELECT r.id, e.id, 4, 3, 15, 5, NULL, 'Elevación controlada - Sin balanceo - 3x15'
FROM rutinas r, ejercicios e
WHERE r.nombre = 'Core Funcional y Abdominales'
AND e.nombre = 'Elevación de piernas'
LIMIT 1;

-- ============================================================================
-- VERIFICACIÓN
-- ============================================================================

-- Ver planes creados
SELECT id, nombre, duracion_dias, activo FROM planes ORDER BY id;

-- Ver rutinas creadas
SELECT id, nombre, duracion_semanas, activo FROM rutinas ORDER BY id;

-- Ver objetivos de planes
SELECT p.nombre, po.calorias_objetivo, po.proteinas_objetivo, po.carbohidratos_objetivo, po.grasas_objetivo
FROM planes p
JOIN plan_objetivos po ON p.id = po.id_plan
ORDER BY p.id;

-- Contar días por plan
SELECT p.nombre, COUNT(pd.id) as total_dias
FROM planes p
LEFT JOIN plan_dias pd ON p.id = pd.id_plan
GROUP BY p.nombre
ORDER BY p.nombre;

-- Contar ejercicios por rutina
SELECT r.nombre, COUNT(re.id) as total_ejercicios, SUM(re.series) as total_series
FROM rutinas r
LEFT JOIN rutina_ejercicios re ON r.id = re.id_rutina
GROUP BY r.nombre
ORDER BY r.nombre;
