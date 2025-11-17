-- ============================================================================
-- PATCH: Corregir plan_dias, rutina_ejercicios y registros según modelos JPA
-- ============================================================================

-- ============================================================================
-- PLAN_DIAS: Una fila por comida (no múltiples comidas por día)
-- Columnas: id_plan, numero_dia, tipo_comida, id_comida, notas
-- ============================================================================

-- Día 1 del Plan Pérdida de Peso
INSERT INTO plan_dias (id_plan, numero_dia, tipo_comida, id_comida, notas)
SELECT p.id, 1, 'DESAYUNO', c.id, 'Desayuno rico en fibra'
FROM planes p, comidas c
WHERE p.nombre = 'Plan Pérdida de Peso - 7 días' AND c.nombre = 'Avena con frutas y almendras'
ON CONFLICT DO NOTHING;

INSERT INTO plan_dias (id_plan, numero_dia, tipo_comida, id_comida, notas)
SELECT p.id, 1, 'SNACK', c.id, 'Media mañana'
FROM planes p, comidas c
WHERE p.nombre = 'Plan Pérdida de Peso - 7 días' AND c.nombre = 'Manzana con mantequilla de almendras'
ON CONFLICT DO NOTHING;

INSERT INTO plan_dias (id_plan, numero_dia, tipo_comida, id_comida, notas)
SELECT p.id, 1, 'ALMUERZO', c.id, 'Almuerzo alto en proteína'
FROM planes p, comidas c
WHERE p.nombre = 'Plan Pérdida de Peso - 7 días' AND c.nombre = 'Ensalada de pollo a la parrilla'
ON CONFLICT DO NOTHING;

INSERT INTO plan_dias (id_plan, numero_dia, tipo_comida, id_comida, notas)
SELECT p.id, 1, 'SNACK', c.id, 'Merienda'
FROM planes p, comidas c
WHERE p.nombre = 'Plan Pérdida de Peso - 7 días' AND c.nombre = 'Yogur griego con frutillas'
ON CONFLICT DO NOTHING;

INSERT INTO plan_dias (id_plan, numero_dia, tipo_comida, id_comida, notas)
SELECT p.id, 1, 'CENA', c.id, 'Cena ligera'
FROM planes p, comidas c
WHERE p.nombre = 'Plan Pérdida de Peso - 7 días' AND c.nombre = 'Pescado al horno con verduras'
ON CONFLICT DO NOTHING;

-- Días 2-7 (patrón similar)
INSERT INTO plan_dias (id_plan, numero_dia, tipo_comida, id_comida)
SELECT p.id, d.num, 'DESAYUNO', c.id
FROM planes p, (SELECT generate_series(2, 7) AS num) d, comidas c
WHERE p.nombre = 'Plan Pérdida de Peso - 7 días' AND c.nombre = 'Huevos revueltos con pan integral';

INSERT INTO plan_dias (id_plan, numero_dia, tipo_comida, id_comida)
SELECT p.id, d.num, 'ALMUERZO', c.id
FROM planes p, (SELECT generate_series(2, 7) AS num) d, comidas c
WHERE p.nombre = 'Plan Pérdida de Peso - 7 días' AND c.nombre = 'Arroz integral con pollo y vegetales';

INSERT INTO plan_dias (id_plan, numero_dia, tipo_comida, id_comida)
SELECT p.id, d.num, 'CENA', c.id
FROM planes p, (SELECT generate_series(2, 7) AS num) d, comidas c
WHERE p.nombre = 'Plan Pérdida de Peso - 7 días' AND c.nombre = 'Ensalada verde con huevo';

-- ============================================================================
-- RUTINA_EJERCICIOS: Columnas correctas
-- orden, series, repeticiones, peso, duracion_minutos, descanso_segundos, notas, id_ejercicio, id_rutina
-- ============================================================================

-- Rutina Principiante - Día 1
INSERT INTO rutina_ejercicios (id_rutina, id_ejercicio, orden, series, repeticiones, duracion_minutos, descanso_segundos, notas)
SELECT r.id, e.id, 1, 3, 15, NULL, 60, 'Calentar bien antes de empezar'
FROM rutinas r, ejercicios e
WHERE r.nombre = 'Rutina Principiante - 4 semanas' AND e.nombre = 'Sentadillas'
ON CONFLICT DO NOTHING;

INSERT INTO rutina_ejercicios (id_rutina, id_ejercicio, orden, series, repeticiones, duracion_minutos, descanso_segundos, notas)
SELECT r.id, e.id, 2, 3, 10, NULL, 60, 'Mantener espalda recta'
FROM rutinas r, ejercicios e
WHERE r.nombre = 'Rutina Principiante - 4 semanas' AND e.nombre = 'Flexiones de pecho'
ON CONFLICT DO NOTHING;

INSERT INTO rutina_ejercicios (id_rutina, id_ejercicio, orden, series, repeticiones, duracion_minutos, descanso_segundos, notas)
SELECT r.id, e.id, 3, 3, 1, 1, 60, 'Mantener cuerpo alineado'
FROM rutinas r, ejercicios e
WHERE r.nombre = 'Rutina Principiante - 4 semanas' AND e.nombre = 'Plancha'
ON CONFLICT DO NOTHING;

-- ============================================================================
-- REGISTROS_COMIDAS: tipo_comida es NOT NULL
-- ============================================================================

INSERT INTO registros_comidas (id_perfil_usuario, id_comida, fecha, hora, tipo_comida, notas)
SELECT p.id, c.id, CURRENT_DATE - INTERVAL '3 days', '08:00:00', 'DESAYUNO', 'Desayuno nutritivo'
FROM perfiles_usuario p, comidas c, cuentas_auth ca
WHERE ca.email = 'demo@nutritrack.com' AND p.id_usuario = ca.id AND c.nombre = 'Avena con frutas y almendras'
ON CONFLICT DO NOTHING;

INSERT INTO registros_comidas (id_perfil_usuario, id_comida, fecha, hora, tipo_comida)
SELECT p.id, c.id, CURRENT_DATE - INTERVAL '3 days', '13:00:00', 'ALMUERZO'
FROM perfiles_usuario p, comidas c, cuentas_auth ca
WHERE ca.email = 'demo@nutritrack.com' AND p.id_usuario = ca.id AND c.nombre = 'Ensalada de pollo a la parrilla'
ON CONFLICT DO NOTHING;

\echo '✅ Patch aplicado correctamente'
