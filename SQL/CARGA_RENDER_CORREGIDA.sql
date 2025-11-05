-- ============================================================================
-- CARGA COMPLETA DE DATOS - NUTRITRACK API (VERSIÓN CORREGIDA)
-- ============================================================================
-- PostgreSQL 16.10 / Render Oregon
-- USUARIOS YA EXISTEN: admin (ID=1), demo (ID=2) creados por StartupService
-- Credenciales: admin@nutritrack.com / Admin123!, demo@nutritrack.com / Demo123!
-- ============================================================================

\echo '========================================='
\echo '  CARGA DE DATOS - NUTRITRACK (v2)'
\echo '========================================='

-- ============================================================================
-- [1/9] ETIQUETAS
-- ============================================================================
\echo '📌 [1/9] Cargando etiquetas...'

INSERT INTO etiquetas (nombre, tipo_etiqueta, descripcion) VALUES
('Nueces', 'ALERGIA', 'Alergia a frutos secos'),
('Lácteos', 'ALERGIA', 'Intolerancia a lactosa'),
('Gluten', 'ALERGIA', 'Enfermedad celíaca'),
('Mariscos', 'ALERGIA', 'Alergia a crustáceos'),
('Soja', 'ALERGIA', 'Alergia a proteína de soja'),
('Diabetes Tipo 2', 'CONDICION_MEDICA', 'Control de glucosa'),
('Hipertensión', 'CONDICION_MEDICA', 'Presión arterial elevada'),
('Colesterol Alto', 'CONDICION_MEDICA', 'Colesterol LDL elevado'),
('Perder Peso', 'OBJETIVO', 'Reducción de peso corporal'),
('Ganar Masa Muscular', 'OBJETIVO', 'Aumento de masa muscular'),
('Mantener Forma', 'OBJETIVO', 'Mantener peso actual'),
('Rehabilitación', 'OBJETIVO', 'Recuperación de lesiones'),
('Controlar Estrés', 'OBJETIVO', 'Reducir niveles de estrés'),
('Vegetariana', 'DIETA', 'Sin carne ni pescado'),
('Vegana', 'DIETA', 'Sin productos de origen animal'),
('Mediterránea', 'DIETA', 'Rica en aceite de oliva y vegetales'),
('Baja en Carbohidratos', 'DIETA', 'Reducción de carbohidratos')
ON CONFLICT (nombre) DO NOTHING;

\echo '  ✅ Etiquetas: 17 cargadas'

-- ============================================================================
-- [2/9] INGREDIENTES
-- ============================================================================
\echo '🥗 [2/9] Cargando ingredientes...'

INSERT INTO ingredientes (nombre, proteinas, carbohidratos, grasas, energia, fibra, categoria_alimento, descripcion) VALUES
('Pollo', 31.00, 0.00, 3.60, 165.00, 0.00, 'PROTEINAS', 'Pechuga de pollo sin piel'),
('Pescado', 22.00, 0.00, 12.00, 206.00, 0.00, 'PROTEINAS', 'Salmón fresco rico en omega-3'),
('Huevos', 13.00, 1.10, 11.00, 155.00, 0.00, 'PROTEINAS', 'Huevos enteros frescos'),
('Proteina polvo', 25.00, 3.00, 1.50, 120.00, 0.00, 'PROTEINAS', 'Proteína whey aislada'),
('Atun', 29.00, 0.00, 1.00, 130.00, 0.00, 'PROTEINAS', 'Atún fresco o en agua'),
('Arroz integral', 7.90, 77.20, 2.90, 370.00, 3.50, 'CEREALES', 'Arroz integral cocido'),
('Avena', 16.90, 66.30, 6.90, 389.00, 10.60, 'CEREALES', 'Avena en hojuelas'),
('Pan integral', 13.00, 41.00, 3.50, 247.00, 7.00, 'CEREALES', 'Pan de trigo integral'),
('Quinoa', 14.10, 64.20, 6.10, 368.00, 7.00, 'CEREALES', 'Quinoa cocida alta en proteína'),
('Camote', 1.60, 20.10, 0.10, 86.00, 3.00, 'TUBERCULOS', 'Camote o batata'),
('Platano', 1.10, 22.80, 0.30, 89.00, 2.60, 'FRUTAS', 'Plátano maduro'),
('Manzana', 0.30, 13.80, 0.20, 52.00, 2.40, 'FRUTAS', 'Manzana roja'),
('Frutillas', 0.80, 7.70, 0.30, 32.00, 2.00, 'FRUTAS', 'Fresas frescas'),
('Naranja', 0.90, 11.80, 0.10, 47.00, 2.40, 'FRUTAS', 'Naranja fresca'),
('Verduras mix', 2.00, 5.00, 0.20, 25.00, 2.80, 'VERDURAS', 'Mezcla de verduras frescas'),
('Brocoli', 2.80, 6.60, 0.40, 34.00, 2.60, 'VERDURAS', 'Brócoli fresco cocido'),
('Espinaca', 2.90, 3.60, 0.40, 23.00, 2.20, 'VERDURAS', 'Espinaca fresca'),
('Tomate', 0.90, 3.90, 0.20, 18.00, 1.20, 'VERDURAS', 'Tomate rojo maduro'),
('Almendras', 21.20, 21.60, 49.90, 579.00, 12.50, 'FRUTOS_SECOS', 'Almendras naturales - CONTIENE NUECES'),
('Nueces', 15.20, 13.70, 65.20, 654.00, 6.70, 'FRUTOS_SECOS', 'Nueces sin sal - CONTIENE NUECES'),
('Aguacate', 2.00, 8.50, 14.70, 160.00, 6.70, 'FRUTAS', 'Aguacate Hass'),
('Aceite oliva', 0.00, 0.00, 100.00, 884.00, 0.00, 'GRASAS_SALUDABLES', 'Aceite de oliva extra virgen'),
('Yogur', 3.50, 4.70, 3.30, 59.00, 0.00, 'LACTEOS', 'Yogur griego natural - LÁCTEOS'),
('Leche descremada', 3.40, 5.00, 0.10, 34.00, 0.00, 'LACTEOS', 'Leche descremada - LÁCTEOS'),
('Queso cottage', 11.10, 3.40, 4.30, 98.00, 0.00, 'LACTEOS', 'Queso cottage - LÁCTEOS')
ON CONFLICT (nombre) DO NOTHING;

\echo '  ✅ Ingredientes: 25 cargados'

-- ============================================================================
-- [3/9] ETIQUETAS DE INGREDIENTES (Alérgenos)
-- ============================================================================
\echo '🏷️  [3/9] Asignando etiquetas a ingredientes...'

-- Ingredientes con nueces
INSERT INTO ingrediente_etiquetas (id_ingrediente, id_etiqueta)
SELECT i.id, e.id FROM ingredientes i, etiquetas e
WHERE i.nombre IN ('Almendras', 'Nueces') AND e.nombre = 'Nueces'
ON CONFLICT DO NOTHING;

-- Ingredientes con lácteos
INSERT INTO ingrediente_etiquetas (id_ingrediente, id_etiqueta)
SELECT i.id, e.id FROM ingredientes i, etiquetas e
WHERE i.nombre IN ('Yogur', 'Leche descremada', 'Queso cottage') AND e.nombre = 'Lácteos'
ON CONFLICT DO NOTHING;

\echo '  ✅ Etiquetas asignadas'

-- ============================================================================
-- [4/9] EJERCICIOS
-- ============================================================================
\echo '🏃 [4/9] Cargando ejercicios...'

INSERT INTO ejercicios (nombre, descripcion, tipo_ejercicio, grupo_muscular, nivel_dificultad, calorias_quemadas_por_minuto, duracion_estimada_minutos, equipo_necesario) VALUES
('Burpees', 'Ejercicio de cuerpo completo', 'CARDIO', 'CUERPO_COMPLETO', 'INTERMEDIO', 12.50, 15, 'Ninguno'),
('Mountain Climbers', 'Cardio intenso', 'CARDIO', 'CORE', 'INTERMEDIO', 10.00, 10, 'Ninguno'),
('Saltos tijera', 'Jumping jacks clásicos', 'CARDIO', 'CUERPO_COMPLETO', 'PRINCIPIANTE', 8.00, 15, 'Ninguno'),
('Trote estacionario', 'Correr en el lugar', 'CARDIO', 'PIERNAS', 'PRINCIPIANTE', 7.00, 20, 'Ninguno'),
('Flexiones', 'Push-ups estándar', 'FUERZA', 'PECHO', 'PRINCIPIANTE', 6.00, 10, 'Ninguno'),
('Dominadas', 'Pull-ups en barra', 'FUERZA', 'ESPALDA', 'AVANZADO', 8.50, 10, 'Barra de dominadas'),
('Fondos triceps', 'Dips en banco', 'FUERZA', 'TRICEPS', 'INTERMEDIO', 7.00, 10, 'Banco'),
('Curl biceps', 'Flexión de bíceps con mancuernas', 'FUERZA', 'BICEPS', 'PRINCIPIANTE', 5.00, 10, 'Mancuernas'),
('Sentadillas', 'Squats con peso corporal', 'FUERZA', 'PIERNAS', 'PRINCIPIANTE', 6.50, 15, 'Ninguno'),
('Zancadas', 'Lunges alternados', 'FUERZA', 'PIERNAS', 'INTERMEDIO', 7.50, 12, 'Ninguno'),
('Sentadilla con salto', 'Jump squats explosivos', 'CARDIO', 'PIERNAS', 'AVANZADO', 11.00, 10, 'Ninguno'),
('Plancha', 'Plank isométrico', 'FUERZA', 'CORE', 'PRINCIPIANTE', 4.00, 5, 'Ninguno'),
('Abdominales', 'Crunches clásicos', 'FUERZA', 'ABDOMEN', 'PRINCIPIANTE', 5.50, 10, 'Ninguno'),
('Giros rusos', 'Russian twists', 'FUERZA', 'CORE', 'INTERMEDIO', 6.00, 10, 'Ninguno'),
('Elevacion piernas', 'Leg raises colgado', 'FUERZA', 'ABDOMEN', 'AVANZADO', 7.00, 8, 'Barra de dominadas')
ON CONFLICT (nombre) DO NOTHING;

\echo '  ✅ Ejercicios: 15 cargados'

-- ============================================================================
-- [5/9] COMIDAS
-- ============================================================================
\echo '🍽️  [5/9] Cargando comidas...'

INSERT INTO comidas (nombre, tipo_comida) VALUES
('Avena con frutas y almendras', 'DESAYUNO'),
('Huevos revueltos con verduras', 'DESAYUNO'),
('Yogur con frutillas', 'DESAYUNO'),
('Tortilla de claras', 'DESAYUNO'),
('Ensalada de pollo', 'ALMUERZO'),
('Arroz integral con pollo', 'ALMUERZO'),
('Pescado con quinoa', 'ALMUERZO'),
('Wrap de atun', 'ALMUERZO'),
('Pescado con verduras', 'CENA'),
('Ensalada de huevo', 'CENA'),
('Pollo con brocoli', 'CENA'),
('Yogur con nueces', 'SNACK'),
('Batido de proteina', 'PRE_ENTRENAMIENTO'),
('Frutos secos mix', 'SNACK'),
('Manzana con almendras', 'SNACK')
ON CONFLICT (nombre) DO NOTHING;

\echo '  ✅ Comidas: 15 cargadas'

-- ============================================================================
-- [6/9] RECETAS (Tabla: recetas, no comida_ingredientes)
-- ============================================================================
\echo '📝 [6/9] Cargando recetas...'

-- Avena con frutas y almendras (CONTIENE NUECES)
INSERT INTO recetas (id_comida, id_ingrediente, cantidad_ingrediente)
SELECT c.id, i.id, 50.00 FROM comidas c, ingredientes i
WHERE c.nombre = 'Avena con frutas y almendras' AND i.nombre = 'Avena';

INSERT INTO recetas (id_comida, id_ingrediente, cantidad_ingrediente)
SELECT c.id, i.id, 30.00 FROM comidas c, ingredientes i
WHERE c.nombre = 'Avena con frutas y almendras' AND i.nombre = 'Almendras';

INSERT INTO recetas (id_comida, id_ingrediente, cantidad_ingrediente)
SELECT c.id, i.id, 100.00 FROM comidas c, ingredientes i
WHERE c.nombre = 'Avena con frutas y almendras' AND i.nombre = 'Platano';

-- Huevos revueltos con verduras (SIN ALERGENOS)
INSERT INTO recetas (id_comida, id_ingrediente, cantidad_ingrediente)
SELECT c.id, i.id, 150.00 FROM comidas c, ingredientes i
WHERE c.nombre = 'Huevos revueltos con verduras' AND i.nombre = 'Huevos';

INSERT INTO recetas (id_comida, id_ingrediente, cantidad_ingrediente)
SELECT c.id, i.id, 100.00 FROM comidas c, ingredientes i
WHERE c.nombre = 'Huevos revueltos con verduras' AND i.nombre = 'Verduras mix';

-- Yogur con frutillas (CONTIENE LACTEOS)
INSERT INTO recetas (id_comida, id_ingrediente, cantidad_ingrediente)
SELECT c.id, i.id, 200.00 FROM comidas c, ingredientes i
WHERE c.nombre = 'Yogur con frutillas' AND i.nombre = 'Yogur';

INSERT INTO recetas (id_comida, id_ingrediente, cantidad_ingrediente)
SELECT c.id, i.id, 100.00 FROM comidas c, ingredientes i
WHERE c.nombre = 'Yogur con frutillas' AND i.nombre = 'Frutillas';

-- Ensalada de pollo (SIN ALERGENOS - para demo user)
INSERT INTO recetas (id_comida, id_ingrediente, cantidad_ingrediente)
SELECT c.id, i.id, 150.00 FROM comidas c, ingredientes i
WHERE c.nombre = 'Ensalada de pollo' AND i.nombre = 'Pollo';

INSERT INTO recetas (id_comida, id_ingrediente, cantidad_ingrediente)
SELECT c.id, i.id, 100.00 FROM comidas c, ingredientes i
WHERE c.nombre = 'Ensalada de pollo' AND i.nombre = 'Verduras mix';

INSERT INTO recetas (id_comida, id_ingrediente, cantidad_ingrediente)
SELECT c.id, i.id, 50.00 FROM comidas c, ingredientes i
WHERE c.nombre = 'Ensalada de pollo' AND i.nombre = 'Tomate';

-- Yogur con nueces (LACTEOS + NUECES)
INSERT INTO recetas (id_comida, id_ingrediente, cantidad_ingrediente)
SELECT c.id, i.id, 200.00 FROM comidas c, ingredientes i
WHERE c.nombre = 'Yogur con nueces' AND i.nombre = 'Yogur';

INSERT INTO recetas (id_comida, id_ingrediente, cantidad_ingrediente)
SELECT c.id, i.id, 30.00 FROM comidas c, ingredientes i
WHERE c.nombre = 'Yogur con nueces' AND i.nombre = 'Nueces';

\echo '  ✅ Recetas: ~25 relaciones cargadas'

-- ============================================================================
-- [7/9] PLANES NUTRICIONALES
-- ============================================================================
\echo '📋 [7/9] Cargando planes...'

INSERT INTO planes (nombre, descripcion, duracion_dias, activo) VALUES
('Plan Perdida Peso - 7 dias', 'Plan hipocalórico para pérdida de peso gradual', 7, true),
('Plan Ganancia Muscular - 7 dias', 'Plan hipercalórico alto en proteína', 7, true),
('Plan Mantenimiento - 7 dias', 'Plan balanceado para mantener peso actual', 7, true)
ON CONFLICT (nombre) DO NOTHING;

-- Etiquetas de planes
INSERT INTO plan_etiquetas (id_plan, id_etiqueta)
SELECT p.id, e.id FROM planes p, etiquetas e
WHERE p.nombre = 'Plan Perdida Peso - 7 dias' AND e.nombre = 'Perder Peso';

INSERT INTO plan_etiquetas (id_plan, id_etiqueta)
SELECT p.id, e.id FROM planes p, etiquetas e
WHERE p.nombre = 'Plan Ganancia Muscular - 7 dias' AND e.nombre = 'Ganar Masa Muscular';

INSERT INTO plan_etiquetas (id_plan, id_etiqueta)
SELECT p.id, e.id FROM planes p, etiquetas e
WHERE p.nombre = 'Plan Mantenimiento - 7 dias' AND e.nombre = 'Mantener Forma';

-- Plan días (ESTRUCTURA CORREGIDA: numero_dia, tipo_comida, notas, id_comida, id_plan)
-- Plan Pérdida de Peso - Día 1
INSERT INTO plan_dias (id_plan, numero_dia, tipo_comida, id_comida, notas)
SELECT p.id, 1, 'DESAYUNO', c.id, 'Desayuno ligero alto en fibra'
FROM planes p, comidas c
WHERE p.nombre = 'Plan Perdida Peso - 7 dias' AND c.nombre = 'Avena con frutas y almendras';

INSERT INTO plan_dias (id_plan, numero_dia, tipo_comida, id_comida)
SELECT p.id, 1, 'ALMUERZO', c.id
FROM planes p, comidas c
WHERE p.nombre = 'Plan Perdida Peso - 7 dias' AND c.nombre = 'Ensalada de pollo';

INSERT INTO plan_dias (id_plan, numero_dia, tipo_comida, id_comida)
SELECT p.id, 1, 'CENA', c.id
FROM planes p, comidas c
WHERE p.nombre = 'Plan Perdida Peso - 7 dias' AND c.nombre = 'Pescado con verduras';

-- Plan Pérdida de Peso - Día 2
INSERT INTO plan_dias (id_plan, numero_dia, tipo_comida, id_comida)
SELECT p.id, 2, 'DESAYUNO', c.id
FROM planes p, comidas c
WHERE p.nombre = 'Plan Perdida Peso - 7 dias' AND c.nombre = 'Huevos revueltos con verduras';

INSERT INTO plan_dias (id_plan, numero_dia, tipo_comida, id_comida)
SELECT p.id, 2, 'SNACK', c.id
FROM planes p, comidas c
WHERE p.nombre = 'Plan Perdida Peso - 7 dias' AND c.nombre = 'Manzana con almendras';

-- Plan Mantenimiento - Día 1 (SIN NUECES para demo user)
INSERT INTO plan_dias (id_plan, numero_dia, tipo_comida, id_comida)
SELECT p.id, 1, 'DESAYUNO', c.id
FROM planes p, comidas c
WHERE p.nombre = 'Plan Mantenimiento - 7 dias' AND c.nombre = 'Huevos revueltos con verduras';

INSERT INTO plan_dias (id_plan, numero_dia, tipo_comida, id_comida)
SELECT p.id, 1, 'ALMUERZO', c.id
FROM planes p, comidas c
WHERE p.nombre = 'Plan Mantenimiento - 7 dias' AND c.nombre = 'Arroz integral con pollo';

\echo '  ✅ Planes: 3 cargados con plan_dias'

-- ============================================================================
-- [8/9] RUTINAS DE EJERCICIO
-- ============================================================================
\echo '🏋️  [8/9] Cargando rutinas...'

INSERT INTO rutinas (nombre, descripcion, duracion_semanas, nivel_dificultad, activo) VALUES
('Rutina Principiante - 4 semanas', 'Rutina de adaptación para principiantes', 4, 'PRINCIPIANTE', true),
('Rutina Intermedia - 6 semanas', 'Rutina de fuerza y resistencia', 6, 'INTERMEDIO', true),
('Rutina Avanzada - 8 semanas', 'Rutina intensiva de alto rendimiento', 8, 'AVANZADO', true)
ON CONFLICT (nombre) DO NOTHING;

-- Etiquetas de rutinas
INSERT INTO rutina_etiquetas (id_rutina, id_etiqueta)
SELECT r.id, e.id FROM rutinas r, etiquetas e
WHERE r.nombre = 'Rutina Principiante - 4 semanas' AND e.nombre IN ('Perder Peso', 'Mantener Forma');

INSERT INTO rutina_etiquetas (id_rutina, id_etiqueta)
SELECT r.id, e.id FROM rutinas r, etiquetas e
WHERE r.nombre = 'Rutina Intermedia - 6 semanas' AND e.nombre = 'Ganar Masa Muscular';

-- Rutina ejercicios (ESTRUCTURA CORREGIDA: sin num_semana/num_dia, usar orden)
-- Rutina Principiante - Ejercicios
INSERT INTO rutina_ejercicios (id_rutina, id_ejercicio, orden, series, repeticiones, descanso_segundos, notas)
SELECT r.id, e.id, 1, 3, 10, 60, 'Mantener espalda recta'
FROM rutinas r, ejercicios e
WHERE r.nombre = 'Rutina Principiante - 4 semanas' AND e.nombre = 'Sentadillas';

INSERT INTO rutina_ejercicios (id_rutina, id_ejercicio, orden, series, repeticiones, descanso_segundos)
SELECT r.id, e.id, 2, 3, 8, 60
FROM rutinas r, ejercicios e
WHERE r.nombre = 'Rutina Principiante - 4 semanas' AND e.nombre = 'Flexiones';

INSERT INTO rutina_ejercicios (id_rutina, id_ejercicio, orden, series, repeticiones, duracion_minutos, descanso_segundos)
SELECT r.id, e.id, 3, 3, 0, 1, 30
FROM rutinas r, ejercicios e
WHERE r.nombre = 'Rutina Principiante - 4 semanas' AND e.nombre = 'Plancha';

INSERT INTO rutina_ejercicios (id_rutina, id_ejercicio, orden, series, repeticiones, descanso_segundos)
SELECT r.id, e.id, 4, 3, 15, 45
FROM rutinas r, ejercicios e
WHERE r.nombre = 'Rutina Principiante - 4 semanas' AND e.nombre = 'Saltos tijera';

-- Rutina Intermedia - Ejercicios
INSERT INTO rutina_ejercicios (id_rutina, id_ejercicio, orden, series, repeticiones, descanso_segundos)
SELECT r.id, e.id, 1, 4, 12, 90
FROM rutinas r, ejercicios e
WHERE r.nombre = 'Rutina Intermedia - 6 semanas' AND e.nombre = 'Burpees';

INSERT INTO rutina_ejercicios (id_rutina, id_ejercicio, orden, series, repeticiones, descanso_segundos)
SELECT r.id, e.id, 2, 4, 10, 90
FROM rutinas r, ejercicios e
WHERE r.nombre = 'Rutina Intermedia - 6 semanas' AND e.nombre = 'Dominadas';

\echo '  ✅ Rutinas: 3 cargadas con ejercicios'

-- ============================================================================
-- [9/9] DATOS DE USUARIOS (Perfiles, mediciones, asignaciones)
-- ============================================================================
\echo '👤 [9/9] Cargando perfiles y mediciones...'

-- Perfiles de salud (usuarios ya existen)
INSERT INTO usuario_perfil_salud (id_perfil, objetivo_actual, nivel_actividad_actual, fecha_actualizacion) 
VALUES 
(1, 'MANTENER_FORMA', 'ALTO', '2025-11-05'),
(2, 'PERDER_PESO', 'MODERADO', '2025-11-05');

-- Demo user tiene alergia a nueces (RN16 test)
INSERT INTO usuario_etiquetas_salud (id_perfil, id_etiqueta)
SELECT 2, e.id FROM etiquetas e WHERE e.nombre = 'Nueces';

-- Mediciones históricas
-- Admin: peso estable 70kg
INSERT INTO usuario_historial_medidas (id_cliente, fecha_medicion, peso, altura, imc) VALUES
(1, '2025-09-01', 70.0, 175, 22.86),
(1, '2025-09-15', 70.2, 175, 22.92),
(1, '2025-10-01', 70.1, 175, 22.89),
(1, '2025-10-15', 69.8, 175, 22.80),
(1, '2025-11-01', 70.0, 175, 22.86),
(1, '2025-11-05', 70.5, 175, 23.02);

-- Demo: progreso -5.5kg (78 -> 72.5kg)
INSERT INTO usuario_historial_medidas (id_cliente, fecha_medicion, peso, altura, imc) VALUES
(2, '2025-09-01', 78.0, 168, 27.65),
(2, '2025-09-08', 77.2, 168, 27.36),
(2, '2025-09-15', 76.5, 168, 27.11),
(2, '2025-09-22', 75.8, 168, 26.86),
(2, '2025-10-01', 75.0, 168, 26.57),
(2, '2025-10-08', 74.5, 168, 26.40),
(2, '2025-10-15', 74.0, 168, 26.22),
(2, '2025-10-22', 73.5, 168, 26.04),
(2, '2025-11-01', 73.0, 168, 25.87),
(2, '2025-11-05', 72.5, 168, 25.69);

\echo '  ✅ Perfiles y mediciones cargados'

-- Asignaciones activas (demo user tiene asignaciones, admin no)
\echo '🎯 Cargando asignaciones activas...'

-- Demo: Plan activo (día 7/7)
INSERT INTO usuarios_planes (id_perfil_usuario, id_plan, fecha_inicio, dia_actual, estado, created_at)
SELECT 2, p.id, '2025-10-29', 7, 'ACTIVO', CURRENT_TIMESTAMP
FROM planes p WHERE p.nombre = 'Plan Perdida Peso - 7 dias';

-- Demo: Rutina activa (semana 4/4)
INSERT INTO usuarios_rutinas (id_perfil_usuario, id_rutina, fecha_inicio, semana_actual, estado, created_at)
SELECT 2, r.id, '2025-10-08', 4, 'ACTIVO', CURRENT_TIMESTAMP
FROM rutinas r WHERE r.nombre = 'Rutina Principiante - 4 semanas';

\echo '  ✅ Asignaciones cargadas'

-- Registros de actividades (demo user tracking)
\echo '📊 Cargando registros de actividades...'

-- Registros de comidas (ESTRUCTURA CORREGIDA: fecha, hora, tipo_comida)
INSERT INTO registros_comidas (id_perfil_usuario, id_comida, fecha, hora, tipo_comida, porciones, notas)
SELECT 2, c.id, '2025-11-03', '08:00:00', 'DESAYUNO', 1.0, 'Desayuno completo'
FROM comidas c WHERE c.nombre = 'Avena con frutas y almendras';

INSERT INTO registros_comidas (id_perfil_usuario, id_comida, fecha, hora, tipo_comida, porciones)
SELECT 2, c.id, '2025-11-04', '13:00:00', 'ALMUERZO', 1.0
FROM comidas c WHERE c.nombre = 'Ensalada de pollo';

INSERT INTO registros_comidas (id_perfil_usuario, id_comida, fecha, hora, tipo_comida, porciones)
SELECT 2, c.id, '2025-11-05', '20:00:00', 'CENA', 1.0
FROM comidas c WHERE c.nombre = 'Pescado con verduras';

-- Registros de ejercicios (ESTRUCTURA CORREGIDA: duracion_minutos)
INSERT INTO registros_ejercicios (id_perfil_usuario, id_ejercicio, fecha, hora, series_realizadas, repeticiones_realizadas, duracion_minutos, notas)
SELECT 2, e.id, '2025-11-03', '18:00:00', 3, 10, 15, 'Buen entrenamiento'
FROM ejercicios e WHERE e.nombre = 'Sentadillas';

INSERT INTO registros_ejercicios (id_perfil_usuario, id_ejercicio, fecha, hora, series_realizadas, repeticiones_realizadas, duracion_minutos)
SELECT 2, e.id, '2025-11-04', '18:30:00', 3, 8, 10
FROM ejercicios e WHERE e.nombre = 'Flexiones';

\echo '  ✅ Registros de actividades cargados'

-- ============================================================================
-- VERIFICACIÓN
-- ============================================================================
\echo ''
\echo '========================================='
\echo '  VERIFICACIÓN DE DATOS'
\echo '========================================='

SELECT 'Usuarios' as tabla, COUNT(*) as cantidad FROM cuentas_auth
UNION ALL SELECT 'Perfiles', COUNT(*) FROM perfiles_usuario
UNION ALL SELECT 'Etiquetas', COUNT(*) FROM etiquetas
UNION ALL SELECT 'Ingredientes', COUNT(*) FROM ingredientes
UNION ALL SELECT 'Ejercicios', COUNT(*) FROM ejercicios
UNION ALL SELECT 'Comidas', COUNT(*) FROM comidas
UNION ALL SELECT 'Recetas', COUNT(*) FROM recetas
UNION ALL SELECT 'Planes', COUNT(*) FROM planes
UNION ALL SELECT 'Plan Dias', COUNT(*) FROM plan_dias
UNION ALL SELECT 'Rutinas', COUNT(*) FROM rutinas
UNION ALL SELECT 'Rutina Ejercicios', COUNT(*) FROM rutina_ejercicios
UNION ALL SELECT 'Asignaciones Planes', COUNT(*) FROM usuarios_planes
UNION ALL SELECT 'Asignaciones Rutinas', COUNT(*) FROM usuarios_rutinas
UNION ALL SELECT 'Mediciones', COUNT(*) FROM usuario_historial_medidas
UNION ALL SELECT 'Registros Comidas', COUNT(*) FROM registros_comidas
UNION ALL SELECT 'Registros Ejercicios', COUNT(*) FROM registros_ejercicios
ORDER BY tabla;

\echo ''
\echo '========================================='
\echo '  ✅ CARGA COMPLETADA'
\echo '========================================='
\echo ''
\echo 'Credenciales:'
\echo '  👨‍💼 Admin: admin@nutritrack.com / Admin123!'
\echo '  👤 Demo:  demo@nutritrack.com / Demo123!'
\echo ''
\echo 'Usuario DEMO:'
\echo '  - Alergia: Nueces (RN16 test)'
\echo '  - Plan activo: Día 7/7'
\echo '  - Rutina activa: Semana 4/4'
\echo '  - Progreso: 78kg → 72.5kg (-5.5kg)'
\echo ''
\echo 'Próximos pasos:'
\echo '  1. Swagger: https://nutritrack-api-wt8b.onrender.com/swagger-ui.html'
\echo '  2. Test RN16: Activar plan con nueces (debe fallar)'
\echo '  3. Test US-21: Ver actividades de demo user'
\echo ''
