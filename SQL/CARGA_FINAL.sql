-- ============================================================================
-- CARGA FINAL DE DATOS - NUTRITRACK API
-- ============================================================================
-- PostgreSQL 16.10 / Render Oregon
-- USUARIOS YA EXISTEN: admin (ID=1), demo (ID=2)
-- CORRECCIONES: UTF8, tabla comida_ingredientes, grupo_muscular ABDOMINALES
-- ============================================================================

\echo '========================================='
\echo '  CARGA FINAL - NUTRITRACK'
\echo '========================================='

-- ============================================================================
-- [1/9] ETIQUETAS
-- ============================================================================
\echo '[1/9] Cargando etiquetas...'

INSERT INTO etiquetas (nombre, tipo_etiqueta, descripcion) VALUES
('Nueces', 'ALERGIA', 'Alergia a frutos secos'),
('Lacteos', 'ALERGIA', 'Intolerancia a lactosa'),
('Gluten', 'ALERGIA', 'Enfermedad celiaca'),
('Mariscos', 'ALERGIA', 'Alergia a crustaceos'),
('Soja', 'ALERGIA', 'Alergia a proteina de soja'),
('Diabetes Tipo 2', 'CONDICION_MEDICA', 'Control de glucosa'),
('Hipertension', 'CONDICION_MEDICA', 'Presion arterial elevada'),
('Colesterol Alto', 'CONDICION_MEDICA', 'Colesterol LDL elevado'),
('Perder Peso', 'OBJETIVO', 'Reduccion de peso corporal'),
('Ganar Masa Muscular', 'OBJETIVO', 'Aumento de masa muscular'),
('Mantener Forma', 'OBJETIVO', 'Mantener peso actual'),
('Rehabilitacion', 'OBJETIVO', 'Recuperacion de lesiones'),
('Controlar Estres', 'OBJETIVO', 'Reducir niveles de estres'),
('Vegetariana', 'DIETA', 'Sin carne ni pescado'),
('Vegana', 'DIETA', 'Sin productos de origen animal'),
('Mediterranea', 'DIETA', 'Rica en aceite de oliva y vegetales'),
('Baja en Carbohidratos', 'DIETA', 'Reduccion de carbohidratos')
ON CONFLICT (nombre) DO NOTHING;

\echo 'Etiquetas: OK'

-- ============================================================================
-- [2/9] INGREDIENTES (SIN CARACTERES ESPECIALES)
-- ============================================================================
\echo '[2/9] Cargando ingredientes...'

INSERT INTO ingredientes (nombre, proteinas, carbohidratos, grasas, energia, fibra, categoria_alimento, descripcion) VALUES
('Pollo', 31.00, 0.00, 3.60, 165.00, 0.00, 'PROTEINAS', 'Pechuga de pollo sin piel'),
('Pescado', 22.00, 0.00, 12.00, 206.00, 0.00, 'PROTEINAS', 'Salmon fresco rico en omega-3'),
('Huevos', 13.00, 1.10, 11.00, 155.00, 0.00, 'PROTEINAS', 'Huevos enteros frescos'),
('Proteina polvo', 25.00, 3.00, 1.50, 120.00, 0.00, 'PROTEINAS', 'Proteina whey aislada'),
('Atun', 29.00, 0.00, 1.00, 130.00, 0.00, 'PROTEINAS', 'Atun fresco o en agua'),
('Arroz integral', 7.90, 77.20, 2.90, 370.00, 3.50, 'CEREALES', 'Arroz integral cocido'),
('Avena', 16.90, 66.30, 6.90, 389.00, 10.60, 'CEREALES', 'Avena en hojuelas'),
('Pan integral', 13.00, 41.00, 3.50, 247.00, 7.00, 'CEREALES', 'Pan de trigo integral'),
('Quinoa', 14.10, 64.20, 6.10, 368.00, 7.00, 'CEREALES', 'Quinoa cocida alta en proteina'),
('Camote', 1.60, 20.10, 0.10, 86.00, 3.00, 'TUBERCULOS', 'Camote o batata'),
('Platano', 1.10, 22.80, 0.30, 89.00, 2.60, 'FRUTAS', 'Platano maduro'),
('Manzana', 0.30, 13.80, 0.20, 52.00, 2.40, 'FRUTAS', 'Manzana roja'),
('Frutillas', 0.80, 7.70, 0.30, 32.00, 2.00, 'FRUTAS', 'Fresas frescas'),
('Naranja', 0.90, 11.80, 0.10, 47.00, 2.40, 'FRUTAS', 'Naranja fresca'),
('Verduras mix', 2.00, 5.00, 0.20, 25.00, 2.80, 'VERDURAS', 'Mezcla de verduras frescas'),
('Brocoli', 2.80, 6.60, 0.40, 34.00, 2.60, 'VERDURAS', 'Brocoli fresco cocido'),
('Espinaca', 2.90, 3.60, 0.40, 23.00, 2.20, 'VERDURAS', 'Espinaca fresca'),
('Tomate', 0.90, 3.90, 0.20, 18.00, 1.20, 'VERDURAS', 'Tomate rojo maduro'),
('Almendras', 21.20, 21.60, 49.90, 579.00, 12.50, 'FRUTOS_SECOS', 'Almendras naturales - CONTIENE NUECES'),
('Nueces', 15.20, 13.70, 65.20, 654.00, 6.70, 'FRUTOS_SECOS', 'Nueces sin sal - CONTIENE NUECES'),
('Aguacate', 2.00, 8.50, 14.70, 160.00, 6.70, 'FRUTAS', 'Aguacate Hass'),
('Aceite oliva', 0.00, 0.00, 100.00, 884.00, 0.00, 'GRASAS_SALUDABLES', 'Aceite de oliva extra virgen'),
('Yogur', 3.50, 4.70, 3.30, 59.00, 0.00, 'LACTEOS', 'Yogur griego natural - LACTEOS'),
('Leche descremada', 3.40, 5.00, 0.10, 34.00, 0.00, 'LACTEOS', 'Leche descremada - LACTEOS'),
('Queso cottage', 11.10, 3.40, 4.30, 98.00, 0.00, 'LACTEOS', 'Queso cottage - LACTEOS')
ON CONFLICT (nombre) DO NOTHING;

\echo 'Ingredientes: OK'

-- ============================================================================
-- [3/9] ETIQUETAS DE INGREDIENTES
-- ============================================================================
\echo '[3/9] Asignando etiquetas a ingredientes...'

INSERT INTO ingrediente_etiquetas (id_ingrediente, id_etiqueta)
SELECT i.id, e.id FROM ingredientes i, etiquetas e
WHERE i.nombre IN ('Almendras', 'Nueces') AND e.nombre = 'Nueces'
ON CONFLICT DO NOTHING;

INSERT INTO ingrediente_etiquetas (id_ingrediente, id_etiqueta)
SELECT i.id, e.id FROM ingredientes i, etiquetas e
WHERE i.nombre IN ('Yogur', 'Leche descremada', 'Queso cottage') AND e.nombre = 'Lacteos'
ON CONFLICT DO NOTHING;

\echo 'Etiquetas: OK'

-- ============================================================================
-- [4/9] EJERCICIOS (grupo_muscular: ABDOMINALES, no ABDOMEN)
-- ============================================================================
\echo '[4/9] Cargando ejercicios...'

INSERT INTO ejercicios (nombre, descripcion, tipo_ejercicio, grupo_muscular, nivel_dificultad, calorias_quemadas_por_minuto, duracion_estimada_minutos, equipo_necesario) VALUES
('Burpees', 'Ejercicio de cuerpo completo', 'CARDIO', 'CUERPO_COMPLETO', 'INTERMEDIO', 12.50, 15, 'Ninguno'),
('Mountain Climbers', 'Cardio intenso', 'CARDIO', 'CORE', 'INTERMEDIO', 10.00, 10, 'Ninguno'),
('Saltos tijera', 'Jumping jacks clasicos', 'CARDIO', 'CUERPO_COMPLETO', 'PRINCIPIANTE', 8.00, 15, 'Ninguno'),
('Trote estacionario', 'Correr en el lugar', 'CARDIO', 'PIERNAS', 'PRINCIPIANTE', 7.00, 20, 'Ninguno'),
('Flexiones', 'Push-ups estandar', 'FUERZA', 'PECHO', 'PRINCIPIANTE', 6.00, 10, 'Ninguno'),
('Dominadas', 'Pull-ups en barra', 'FUERZA', 'ESPALDA', 'AVANZADO', 8.50, 10, 'Barra de dominadas'),
('Fondos triceps', 'Dips en banco', 'FUERZA', 'TRICEPS', 'INTERMEDIO', 7.00, 10, 'Banco'),
('Curl biceps', 'Flexion de biceps con mancuernas', 'FUERZA', 'BICEPS', 'PRINCIPIANTE', 5.00, 10, 'Mancuernas'),
('Sentadillas', 'Squats con peso corporal', 'FUERZA', 'PIERNAS', 'PRINCIPIANTE', 6.50, 15, 'Ninguno'),
('Zancadas', 'Lunges alternados', 'FUERZA', 'PIERNAS', 'INTERMEDIO', 7.50, 12, 'Ninguno'),
('Sentadilla con salto', 'Jump squats explosivos', 'CARDIO', 'PIERNAS', 'AVANZADO', 11.00, 10, 'Ninguno'),
('Plancha', 'Plank isometrico', 'FUERZA', 'CORE', 'PRINCIPIANTE', 4.00, 5, 'Ninguno'),
('Abdominales', 'Crunches clasicos', 'FUERZA', 'ABDOMINALES', 'PRINCIPIANTE', 5.50, 10, 'Ninguno'),
('Giros rusos', 'Russian twists', 'FUERZA', 'CORE', 'INTERMEDIO', 6.00, 10, 'Ninguno'),
('Elevacion piernas', 'Leg raises colgado', 'FUERZA', 'ABDOMINALES', 'AVANZADO', 7.00, 8, 'Barra de dominadas')
ON CONFLICT (nombre) DO NOTHING;

\echo 'Ejercicios: OK'

-- ============================================================================
-- [5/9] COMIDAS (sin ON CONFLICT)
-- ============================================================================
\echo '[5/9] Cargando comidas...'

INSERT INTO comidas (nombre, tipo_comida) 
SELECT 'Avena con frutas y almendras', 'DESAYUNO'
WHERE NOT EXISTS (SELECT 1 FROM comidas WHERE nombre = 'Avena con frutas y almendras');

INSERT INTO comidas (nombre, tipo_comida) 
SELECT 'Huevos revueltos con verduras', 'DESAYUNO'
WHERE NOT EXISTS (SELECT 1 FROM comidas WHERE nombre = 'Huevos revueltos con verduras');

INSERT INTO comidas (nombre, tipo_comida) 
SELECT 'Yogur con frutillas', 'DESAYUNO'
WHERE NOT EXISTS (SELECT 1 FROM comidas WHERE nombre = 'Yogur con frutillas');

INSERT INTO comidas (nombre, tipo_comida) 
SELECT 'Tortilla de claras', 'DESAYUNO'
WHERE NOT EXISTS (SELECT 1 FROM comidas WHERE nombre = 'Tortilla de claras');

INSERT INTO comidas (nombre, tipo_comida) 
SELECT 'Ensalada de pollo', 'ALMUERZO'
WHERE NOT EXISTS (SELECT 1 FROM comidas WHERE nombre = 'Ensalada de pollo');

INSERT INTO comidas (nombre, tipo_comida) 
SELECT 'Arroz integral con pollo', 'ALMUERZO'
WHERE NOT EXISTS (SELECT 1 FROM comidas WHERE nombre = 'Arroz integral con pollo');

INSERT INTO comidas (nombre, tipo_comida) 
SELECT 'Pescado con quinoa', 'ALMUERZO'
WHERE NOT EXISTS (SELECT 1 FROM comidas WHERE nombre = 'Pescado con quinoa');

INSERT INTO comidas (nombre, tipo_comida) 
SELECT 'Wrap de atun', 'ALMUERZO'
WHERE NOT EXISTS (SELECT 1 FROM comidas WHERE nombre = 'Wrap de atun');

INSERT INTO comidas (nombre, tipo_comida) 
SELECT 'Pescado con verduras', 'CENA'
WHERE NOT EXISTS (SELECT 1 FROM comidas WHERE nombre = 'Pescado con verduras');

INSERT INTO comidas (nombre, tipo_comida) 
SELECT 'Ensalada de huevo', 'CENA'
WHERE NOT EXISTS (SELECT 1 FROM comidas WHERE nombre = 'Ensalada de huevo');

INSERT INTO comidas (nombre, tipo_comida) 
SELECT 'Pollo con brocoli', 'CENA'
WHERE NOT EXISTS (SELECT 1 FROM comidas WHERE nombre = 'Pollo con brocoli');

INSERT INTO comidas (nombre, tipo_comida) 
SELECT 'Yogur con nueces', 'SNACK'
WHERE NOT EXISTS (SELECT 1 FROM comidas WHERE nombre = 'Yogur con nueces');

INSERT INTO comidas (nombre, tipo_comida) 
SELECT 'Batido de proteina', 'PRE_ENTRENAMIENTO'
WHERE NOT EXISTS (SELECT 1 FROM comidas WHERE nombre = 'Batido de proteina');

INSERT INTO comidas (nombre, tipo_comida) 
SELECT 'Frutos secos mix', 'SNACK'
WHERE NOT EXISTS (SELECT 1 FROM comidas WHERE nombre = 'Frutos secos mix');

INSERT INTO comidas (nombre, tipo_comida) 
SELECT 'Manzana con almendras', 'SNACK'
WHERE NOT EXISTS (SELECT 1 FROM comidas WHERE nombre = 'Manzana con almendras');

\echo 'Comidas: OK'

-- ============================================================================
-- [6/9] RECETAS (Tabla: comida_ingredientes con cantidad_gramos)
-- ============================================================================
\echo '[6/9] Cargando recetas...'

-- Avena con frutas y almendras (CONTIENE NUECES)
INSERT INTO comida_ingredientes (id_comida, id_ingrediente, cantidad_gramos)
SELECT c.id, i.id, 50.00 FROM comidas c, ingredientes i
WHERE c.nombre = 'Avena con frutas y almendras' AND i.nombre = 'Avena'
ON CONFLICT DO NOTHING;

INSERT INTO comida_ingredientes (id_comida, id_ingrediente, cantidad_gramos)
SELECT c.id, i.id, 30.00 FROM comidas c, ingredientes i
WHERE c.nombre = 'Avena con frutas y almendras' AND i.nombre = 'Almendras'
ON CONFLICT DO NOTHING;

INSERT INTO comida_ingredientes (id_comida, id_ingrediente, cantidad_gramos)
SELECT c.id, i.id, 100.00 FROM comidas c, ingredientes i
WHERE c.nombre = 'Avena con frutas y almendras' AND i.nombre = 'Platano'
ON CONFLICT DO NOTHING;

-- Huevos revueltos con verduras (SIN ALERGENOS)
INSERT INTO comida_ingredientes (id_comida, id_ingrediente, cantidad_gramos)
SELECT c.id, i.id, 150.00 FROM comidas c, ingredientes i
WHERE c.nombre = 'Huevos revueltos con verduras' AND i.nombre = 'Huevos'
ON CONFLICT DO NOTHING;

INSERT INTO comida_ingredientes (id_comida, id_ingrediente, cantidad_gramos)
SELECT c.id, i.id, 100.00 FROM comidas c, ingredientes i
WHERE c.nombre = 'Huevos revueltos con verduras' AND i.nombre = 'Verduras mix'
ON CONFLICT DO NOTHING;

-- Yogur con frutillas (CONTIENE LACTEOS)
INSERT INTO comida_ingredientes (id_comida, id_ingrediente, cantidad_gramos)
SELECT c.id, i.id, 200.00 FROM comidas c, ingredientes i
WHERE c.nombre = 'Yogur con frutillas' AND i.nombre = 'Yogur'
ON CONFLICT DO NOTHING;

INSERT INTO comida_ingredientes (id_comida, id_ingrediente, cantidad_gramos)
SELECT c.id, i.id, 100.00 FROM comidas c, ingredientes i
WHERE c.nombre = 'Yogur con frutillas' AND i.nombre = 'Frutillas'
ON CONFLICT DO NOTHING;

-- Ensalada de pollo (SIN ALERGENOS)
INSERT INTO comida_ingredientes (id_comida, id_ingrediente, cantidad_gramos)
SELECT c.id, i.id, 150.00 FROM comidas c, ingredientes i
WHERE c.nombre = 'Ensalada de pollo' AND i.nombre = 'Pollo'
ON CONFLICT DO NOTHING;

INSERT INTO comida_ingredientes (id_comida, id_ingrediente, cantidad_gramos)
SELECT c.id, i.id, 100.00 FROM comidas c, ingredientes i
WHERE c.nombre = 'Ensalada de pollo' AND i.nombre = 'Verduras mix'
ON CONFLICT DO NOTHING;

INSERT INTO comida_ingredientes (id_comida, id_ingrediente, cantidad_gramos)
SELECT c.id, i.id, 50.00 FROM comidas c, ingredientes i
WHERE c.nombre = 'Ensalada de pollo' AND i.nombre = 'Tomate'
ON CONFLICT DO NOTHING;

-- Yogur con nueces (LACTEOS + NUECES)
INSERT INTO comida_ingredientes (id_comida, id_ingrediente, cantidad_gramos)
SELECT c.id, i.id, 200.00 FROM comidas c, ingredientes i
WHERE c.nombre = 'Yogur con nueces' AND i.nombre = 'Yogur'
ON CONFLICT DO NOTHING;

INSERT INTO comida_ingredientes (id_comida, id_ingrediente, cantidad_gramos)
SELECT c.id, i.id, 30.00 FROM comidas c, ingredientes i
WHERE c.nombre = 'Yogur con nueces' AND i.nombre = 'Nueces'
ON CONFLICT DO NOTHING;

\echo 'Recetas: OK'

-- ============================================================================
-- [7/9] PLANES NUTRICIONALES
-- ============================================================================
\echo '[7/9] Cargando planes...'

INSERT INTO planes (nombre, descripcion, duracion_dias, activo) 
SELECT 'Plan Perdida Peso - 7 dias', 'Plan hipocalorico para perdida de peso gradual', 7, true
WHERE NOT EXISTS (SELECT 1 FROM planes WHERE nombre = 'Plan Perdida Peso - 7 dias');

INSERT INTO planes (nombre, descripcion, duracion_dias, activo) 
SELECT 'Plan Ganancia Muscular - 7 dias', 'Plan hipercalorico alto en proteina', 7, true
WHERE NOT EXISTS (SELECT 1 FROM planes WHERE nombre = 'Plan Ganancia Muscular - 7 dias');

INSERT INTO planes (nombre, descripcion, duracion_dias, activo) 
SELECT 'Plan Mantenimiento - 7 dias', 'Plan balanceado para mantener peso actual', 7, true
WHERE NOT EXISTS (SELECT 1 FROM planes WHERE nombre = 'Plan Mantenimiento - 7 dias');

-- Etiquetas de planes
INSERT INTO plan_etiquetas (id_plan, id_etiqueta)
SELECT p.id, e.id FROM planes p, etiquetas e
WHERE p.nombre = 'Plan Perdida Peso - 7 dias' AND e.nombre = 'Perder Peso'
ON CONFLICT DO NOTHING;

INSERT INTO plan_etiquetas (id_plan, id_etiqueta)
SELECT p.id, e.id FROM planes p, etiquetas e
WHERE p.nombre = 'Plan Ganancia Muscular - 7 dias' AND e.nombre = 'Ganar Masa Muscular'
ON CONFLICT DO NOTHING;

INSERT INTO plan_etiquetas (id_plan, id_etiqueta)
SELECT p.id, e.id FROM planes p, etiquetas e
WHERE p.nombre = 'Plan Mantenimiento - 7 dias' AND e.nombre = 'Mantener Forma'
ON CONFLICT DO NOTHING;

-- Plan dias
INSERT INTO plan_dias (id_plan, numero_dia, tipo_comida, id_comida, notas)
SELECT p.id, 1, 'DESAYUNO', c.id, 'Desayuno ligero alto en fibra'
FROM planes p, comidas c
WHERE p.nombre = 'Plan Perdida Peso - 7 dias' AND c.nombre = 'Avena con frutas y almendras'
AND NOT EXISTS (SELECT 1 FROM plan_dias pd WHERE pd.id_plan = p.id AND pd.numero_dia = 1 AND pd.tipo_comida = 'DESAYUNO');

INSERT INTO plan_dias (id_plan, numero_dia, tipo_comida, id_comida)
SELECT p.id, 1, 'ALMUERZO', c.id
FROM planes p, comidas c
WHERE p.nombre = 'Plan Perdida Peso - 7 dias' AND c.nombre = 'Ensalada de pollo'
AND NOT EXISTS (SELECT 1 FROM plan_dias pd WHERE pd.id_plan = p.id AND pd.numero_dia = 1 AND pd.tipo_comida = 'ALMUERZO');

INSERT INTO plan_dias (id_plan, numero_dia, tipo_comida, id_comida)
SELECT p.id, 1, 'CENA', c.id
FROM planes p, comidas c
WHERE p.nombre = 'Plan Perdida Peso - 7 dias' AND c.nombre = 'Pescado con verduras'
AND NOT EXISTS (SELECT 1 FROM plan_dias pd WHERE pd.id_plan = p.id AND pd.numero_dia = 1 AND pd.tipo_comida = 'CENA');

INSERT INTO plan_dias (id_plan, numero_dia, tipo_comida, id_comida)
SELECT p.id, 1, 'DESAYUNO', c.id
FROM planes p, comidas c
WHERE p.nombre = 'Plan Mantenimiento - 7 dias' AND c.nombre = 'Huevos revueltos con verduras'
AND NOT EXISTS (SELECT 1 FROM plan_dias pd WHERE pd.id_plan = p.id AND pd.numero_dia = 1 AND pd.tipo_comida = 'DESAYUNO');

\echo 'Planes: OK'

-- ============================================================================
-- [8/9] RUTINAS DE EJERCICIO
-- ============================================================================
\echo '[8/9] Cargando rutinas...'

INSERT INTO rutinas (nombre, descripcion, duracion_semanas, nivel_dificultad, activo) 
SELECT 'Rutina Principiante - 4 semanas', 'Rutina de adaptacion para principiantes', 4, 'PRINCIPIANTE', true
WHERE NOT EXISTS (SELECT 1 FROM rutinas WHERE nombre = 'Rutina Principiante - 4 semanas');

INSERT INTO rutinas (nombre, descripcion, duracion_semanas, nivel_dificultad, activo) 
SELECT 'Rutina Intermedia - 6 semanas', 'Rutina de fuerza y resistencia', 6, 'INTERMEDIO', true
WHERE NOT EXISTS (SELECT 1 FROM rutinas WHERE nombre = 'Rutina Intermedia - 6 semanas');

INSERT INTO rutinas (nombre, descripcion, duracion_semanas, nivel_dificultad, activo) 
SELECT 'Rutina Avanzada - 8 semanas', 'Rutina intensiva de alto rendimiento', 8, 'AVANZADO', true
WHERE NOT EXISTS (SELECT 1 FROM rutinas WHERE nombre = 'Rutina Avanzada - 8 semanas');

-- Etiquetas de rutinas
INSERT INTO rutina_etiquetas (id_rutina, id_etiqueta)
SELECT r.id, e.id FROM rutinas r, etiquetas e
WHERE r.nombre = 'Rutina Principiante - 4 semanas' AND e.nombre = 'Perder Peso'
ON CONFLICT DO NOTHING;

INSERT INTO rutina_etiquetas (id_rutina, id_etiqueta)
SELECT r.id, e.id FROM rutinas r, etiquetas e
WHERE r.nombre = 'Rutina Principiante - 4 semanas' AND e.nombre = 'Mantener Forma'
ON CONFLICT DO NOTHING;

INSERT INTO rutina_etiquetas (id_rutina, id_etiqueta)
SELECT r.id, e.id FROM rutinas r, etiquetas e
WHERE r.nombre = 'Rutina Intermedia - 6 semanas' AND e.nombre = 'Ganar Masa Muscular'
ON CONFLICT DO NOTHING;

-- Rutina ejercicios
INSERT INTO rutina_ejercicios (id_rutina, id_ejercicio, orden, series, repeticiones, descanso_segundos, notas)
SELECT r.id, e.id, 1, 3, 10, 60, 'Mantener espalda recta'
FROM rutinas r, ejercicios e
WHERE r.nombre = 'Rutina Principiante - 4 semanas' AND e.nombre = 'Sentadillas'
AND NOT EXISTS (SELECT 1 FROM rutina_ejercicios re WHERE re.id_rutina = r.id AND re.orden = 1);

INSERT INTO rutina_ejercicios (id_rutina, id_ejercicio, orden, series, repeticiones, descanso_segundos)
SELECT r.id, e.id, 2, 3, 8, 60
FROM rutinas r, ejercicios e
WHERE r.nombre = 'Rutina Principiante - 4 semanas' AND e.nombre = 'Flexiones'
AND NOT EXISTS (SELECT 1 FROM rutina_ejercicios re WHERE re.id_rutina = r.id AND re.orden = 2);

INSERT INTO rutina_ejercicios (id_rutina, id_ejercicio, orden, series, repeticiones, duracion_minutos, descanso_segundos)
SELECT r.id, e.id, 3, 3, 0, 1, 30
FROM rutinas r, ejercicios e
WHERE r.nombre = 'Rutina Principiante - 4 semanas' AND e.nombre = 'Plancha'
AND NOT EXISTS (SELECT 1 FROM rutina_ejercicios re WHERE re.id_rutina = r.id AND re.orden = 3);

\echo 'Rutinas: OK'

-- ============================================================================
-- [9/9] DATOS DE USUARIOS
-- ============================================================================
\echo '[9/9] Cargando perfiles y mediciones...'

-- Perfiles de salud (con ON CONFLICT)
INSERT INTO usuario_perfil_salud (id_perfil, objetivo_actual, nivel_actividad_actual, fecha_actualizacion) 
VALUES (1, 'MANTENER_FORMA', 'ALTO', '2025-11-05')
ON CONFLICT (id_perfil) DO UPDATE SET objetivo_actual = 'MANTENER_FORMA', nivel_actividad_actual = 'ALTO';

INSERT INTO usuario_perfil_salud (id_perfil, objetivo_actual, nivel_actividad_actual, fecha_actualizacion) 
VALUES (2, 'PERDER_PESO', 'MODERADO', '2025-11-05')
ON CONFLICT (id_perfil) DO UPDATE SET objetivo_actual = 'PERDER_PESO', nivel_actividad_actual = 'MODERADO';

-- Demo user tiene alergia a nueces
INSERT INTO usuario_etiquetas_salud (id_perfil, id_etiqueta)
SELECT 2, e.id FROM etiquetas e WHERE e.nombre = 'Nueces'
ON CONFLICT DO NOTHING;

-- Mediciones (solo si no existen)
INSERT INTO usuario_historial_medidas (id_cliente, fecha_medicion, peso, altura, imc) 
SELECT 1, '2025-09-01', 70.0, 175, 22.86
WHERE NOT EXISTS (SELECT 1 FROM usuario_historial_medidas WHERE id_cliente = 1 AND fecha_medicion = '2025-09-01');

INSERT INTO usuario_historial_medidas (id_cliente, fecha_medicion, peso, altura, imc) 
SELECT 1, '2025-11-05', 70.5, 175, 23.02
WHERE NOT EXISTS (SELECT 1 FROM usuario_historial_medidas WHERE id_cliente = 1 AND fecha_medicion = '2025-11-05');

INSERT INTO usuario_historial_medidas (id_cliente, fecha_medicion, peso, altura, imc) 
SELECT 2, '2025-09-01', 78.0, 168, 27.65
WHERE NOT EXISTS (SELECT 1 FROM usuario_historial_medidas WHERE id_cliente = 2 AND fecha_medicion = '2025-09-01');

INSERT INTO usuario_historial_medidas (id_cliente, fecha_medicion, peso, altura, imc) 
SELECT 2, '2025-11-05', 72.5, 168, 25.69
WHERE NOT EXISTS (SELECT 1 FROM usuario_historial_medidas WHERE id_cliente = 2 AND fecha_medicion = '2025-11-05');

\echo 'Perfiles: OK'

-- Asignaciones activas
\echo 'Cargando asignaciones...'

INSERT INTO usuarios_planes (id_perfil_usuario, id_plan, fecha_inicio, dia_actual, estado, created_at)
SELECT 2, p.id, '2025-10-29', 7, 'ACTIVO', CURRENT_TIMESTAMP
FROM planes p WHERE p.nombre = 'Plan Perdida Peso - 7 dias'
AND NOT EXISTS (SELECT 1 FROM usuarios_planes WHERE id_perfil_usuario = 2 AND estado = 'ACTIVO');

INSERT INTO usuarios_rutinas (id_perfil_usuario, id_rutina, fecha_inicio, semana_actual, estado, created_at)
SELECT 2, r.id, '2025-10-08', 4, 'ACTIVO', CURRENT_TIMESTAMP
FROM rutinas r WHERE r.nombre = 'Rutina Principiante - 4 semanas'
AND NOT EXISTS (SELECT 1 FROM usuarios_rutinas WHERE id_perfil_usuario = 2 AND estado = 'ACTIVO');

\echo 'Asignaciones: OK'

-- Registros de actividades
\echo 'Cargando registros...'

INSERT INTO registros_comidas (id_perfil_usuario, id_comida, fecha, hora, tipo_comida, porciones, notas)
SELECT 2, c.id, '2025-11-03', '08:00:00', 'DESAYUNO', 1.0, 'Desayuno completo'
FROM comidas c WHERE c.nombre = 'Avena con frutas y almendras'
AND NOT EXISTS (SELECT 1 FROM registros_comidas WHERE id_perfil_usuario = 2 AND fecha = '2025-11-03' AND hora = '08:00:00');

INSERT INTO registros_ejercicios (id_perfil_usuario, id_ejercicio, fecha, hora, series_realizadas, repeticiones_realizadas, duracion_minutos, notas)
SELECT 2, e.id, '2025-11-03', '18:00:00', 3, 10, 15, 'Buen entrenamiento'
FROM ejercicios e WHERE e.nombre = 'Sentadillas'
AND NOT EXISTS (SELECT 1 FROM registros_ejercicios WHERE id_perfil_usuario = 2 AND fecha = '2025-11-03' AND hora = '18:00:00');

\echo 'Registros: OK'

-- ============================================================================
-- VERIFICACIÓN
-- ============================================================================
\echo ''
\echo '========================================='
\echo '  RESUMEN DE CARGA'
\echo '========================================='

SELECT 'Usuarios' as tabla, COUNT(*) as cantidad FROM cuentas_auth
UNION ALL SELECT 'Perfiles', COUNT(*) FROM perfiles_usuario
UNION ALL SELECT 'Etiquetas', COUNT(*) FROM etiquetas
UNION ALL SELECT 'Ingredientes', COUNT(*) FROM ingredientes
UNION ALL SELECT 'Ejercicios', COUNT(*) FROM ejercicios
UNION ALL SELECT 'Comidas', COUNT(*) FROM comidas
UNION ALL SELECT 'Recetas', COUNT(*) FROM comida_ingredientes
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
\echo '  CARGA COMPLETADA'
\echo '========================================='
\echo ''
\echo 'Credenciales:'
\echo '  Admin: admin@nutritrack.com / Admin123!'
\echo '  Demo:  demo@nutritrack.com / Demo123!'
\echo ''
\echo 'Demo user:'
\echo '  - Alergia: Nueces (RN16 test)'
\echo '  - Plan activo: Dia 7/7'
\echo '  - Rutina activa: Semana 4/4'
\echo '  - Progreso: 78kg -> 72.5kg'
\echo ''
\echo 'Swagger: https://nutritrack-api-wt8b.onrender.com/swagger-ui.html'
\echo ''
