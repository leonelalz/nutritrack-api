-- ============================================================================
-- CARGA COMPLETA DE DATOS DE PRUEBA - NUTRITRACK API
-- ============================================================================
-- Compatible con: PostgreSQL 16.10
-- Usuarios: admin (ID=1) y demo (ID=2) ya existen (creados por StartupService)
-- Credenciales: admin@nutritrack.com / Admin123!, demo@nutritrack.com / Demo123!
-- ============================================================================

\echo '========================================='
\echo '  CARGA DE DATOS A RENDER - NUTRITRACK'
\echo '========================================='
\echo ''

-- ============================================================================
-- MÓDULO 2: ETIQUETAS (Tags para categorización)
-- ============================================================================
\echo '📌 [1/9] Cargando etiquetas...'

INSERT INTO etiquetas (nombre, tipo_etiqueta, descripcion) VALUES
-- Alergias (RN16 - validación cruzada)
('Nueces', 'ALERGIA', 'Alergia a frutos secos'),
('Lácteos', 'ALERGIA', 'Intolerancia a la lactosa o alergia a proteínas lácteas'),
('Gluten', 'ALERGIA', 'Enfermedad celíaca o sensibilidad al gluten'),
('Mariscos', 'ALERGIA', 'Alergia a crustáceos y moluscos'),
('Soja', 'ALERGIA', 'Alergia a proteína de soja'),

-- Condiciones médicas
('Diabetes Tipo 2', 'CONDICION_MEDICA', 'Control de glucosa en sangre'),
('Hipertensión', 'CONDICION_MEDICA', 'Presión arterial elevada'),
('Colesterol Alto', 'CONDICION_MEDICA', 'Niveles elevados de colesterol LDL'),
('Bajo en sodio', 'CONDICION_MEDICA', 'Dieta baja en sodio para hipertensión'),

-- Objetivos (US-07, US-09 - filtrado por objetivo)
('Perder Peso', 'OBJETIVO', 'Reducción de peso corporal'),
('Ganar Masa Muscular', 'OBJETIVO', 'Aumento de masa muscular'),
('Mantener Forma', 'OBJETIVO', 'Mantener peso y condición física actual'),
('Rehabilitación', 'OBJETIVO', 'Recuperación de lesiones'),
('Controlar Estrés', 'OBJETIVO', 'Reducir niveles de estrés y ansiedad'),

-- Tipos de dieta
('Vegetariana', 'DIETA', 'Sin carne ni pescado'),
('Vegana', 'DIETA', 'Sin productos de origen animal'),
('Mediterránea', 'DIETA', 'Dieta rica en aceite de oliva, frutas y vegetales'),
('Baja en Carbohidratos', 'DIETA', 'Reducción de carbohidratos procesados')
ON CONFLICT (nombre) DO NOTHING;

\echo '  ✅ Etiquetas cargadas'
\echo ''

-- ============================================================================
-- MÓDULO 2: INGREDIENTES (RN07, RN09, RN10)
-- ============================================================================
\echo '🥗 [2/9] Cargando ingredientes...'

INSERT INTO ingredientes (nombre, proteinas, carbohidratos, grasas, energia, fibra, categoria_alimento, descripcion) VALUES
-- Proteínas
('Pollo', 31.00, 0.00, 3.60, 165.00, 0.00, 'PROTEINAS', 'Pechuga de pollo sin piel'),
('Pescado', 22.00, 0.00, 12.00, 206.00, 0.00, 'PROTEINAS', 'Salmón fresco, rico en omega-3'),
('Huevos', 13.00, 1.10, 11.00, 155.00, 0.00, 'PROTEINAS', 'Huevos enteros frescos'),
('Proteína en polvo', 25.00, 3.00, 1.50, 120.00, 0.00, 'PROTEINAS', 'Proteína whey aislada'),
('Atún', 29.00, 0.00, 1.00, 130.00, 0.00, 'PROTEINAS', 'Atún fresco o en agua'),

-- Carbohidratos
('Arroz integral', 7.90, 77.20, 2.90, 370.00, 3.50, 'CEREALES', 'Arroz integral cocido'),
('Avena', 16.90, 66.30, 6.90, 389.00, 10.60, 'CEREALES', 'Avena en hojuelas'),
('Pan integral', 13.00, 41.00, 3.50, 247.00, 7.00, 'CEREALES', 'Pan de trigo integral'),
('Quinoa', 14.10, 64.20, 6.10, 368.00, 7.00, 'CEREALES', 'Quinoa cocida, rica en proteína vegetal'),
('Camote', 1.60, 20.10, 0.10, 86.00, 3.00, 'TUBERCULOS', 'Camote o batata'),

-- Frutas
('Plátano', 1.10, 22.80, 0.30, 89.00, 2.60, 'FRUTAS', 'Plátano maduro'),
('Manzana', 0.30, 13.80, 0.20, 52.00, 2.40, 'FRUTAS', 'Manzana roja'),
('Frutillas', 0.80, 7.70, 0.30, 32.00, 2.00, 'FRUTAS', 'Fresas frescas'),
('Naranja', 0.90, 11.80, 0.10, 47.00, 2.40, 'FRUTAS', 'Naranja fresca'),

-- Vegetales
('Verduras mix', 2.00, 5.00, 0.20, 25.00, 2.80, 'VERDURAS', 'Mezcla de verduras'),
('Brócoli', 2.80, 6.60, 0.40, 34.00, 2.60, 'VERDURAS', 'Brócoli fresco cocido'),
('Espinaca', 2.90, 3.60, 0.40, 23.00, 2.20, 'VERDURAS', 'Espinaca fresca'),
('Tomate', 0.90, 3.90, 0.20, 18.00, 1.20, 'VERDURAS', 'Tomate rojo maduro'),

-- Grasas saludables
('Almendras', 21.20, 21.60, 49.90, 579.00, 12.50, 'FRUTOS_SECOS', 'Almendras naturales - CONTIENE NUECES'),
('Nueces', 15.20, 13.70, 65.20, 654.00, 6.70, 'FRUTOS_SECOS', 'Nueces sin sal - CONTIENE NUECES'),
('Aguacate', 2.00, 8.50, 14.70, 160.00, 6.70, 'FRUTAS', 'Aguacate Hass'),
('Aceite de oliva', 0.00, 0.00, 100.00, 884.00, 0.00, 'GRASAS_SALUDABLES', 'Aceite de oliva extra virgen'),

-- Lácteos
('Yogur', 3.50, 4.70, 3.30, 59.00, 0.00, 'LACTEOS', 'Yogur griego natural - CONTIENE LÁCTEOS'),
('Leche descremada', 3.40, 5.00, 0.10, 34.00, 0.00, 'LACTEOS', 'Leche descremada - CONTIENE LÁCTEOS'),
('Queso cottage', 11.10, 3.40, 4.30, 98.00, 0.00, 'LACTEOS', 'Queso cottage bajo en grasa - CONTIENE LÁCTEOS')
ON CONFLICT (nombre) DO NOTHING;

\echo '  ✅ Ingredientes cargados'
\echo ''

-- ============================================================================
-- ASIGNAR ETIQUETAS A INGREDIENTES (RN16 - validación de alérgenos)
-- ============================================================================
\echo '🏷️  [3/9] Asignando etiquetas a ingredientes...'

-- Ingredientes con nueces
INSERT INTO ingrediente_etiquetas (id_ingrediente, id_etiqueta)
SELECT i.id, e.id
FROM ingredientes i, etiquetas e
WHERE i.nombre IN ('Almendras', 'Nueces') AND e.nombre = 'Nueces'
ON CONFLICT DO NOTHING;

-- Ingredientes con lácteos
INSERT INTO ingrediente_etiquetas (id_ingrediente, id_etiqueta)
SELECT i.id, e.id
FROM ingredientes i, etiquetas e
WHERE i.nombre IN ('Yogur', 'Leche descremada', 'Queso cottage') AND e.nombre = 'Lácteos'
ON CONFLICT DO NOTHING;

\echo '  ✅ Etiquetas asignadas a ingredientes'
\echo ''

-- ============================================================================
-- MÓDULO 2: EJERCICIOS (RN13 - series y repeticiones positivas)
-- ============================================================================
\echo '🏃 [4/9] Cargando ejercicios...'

INSERT INTO ejercicios (nombre, descripcion, tipo_ejercicio, grupo_muscular, nivel_dificultad, calorias_quemadas_por_minuto, duracion_estimada_minutos, equipo_necesario) VALUES
-- Cardio
('Burpees', 'Ejercicio completo que combina sentadilla, plancha y salto', 'CARDIO', 'CUERPO_COMPLETO', 'INTERMEDIO', 12.50, 15, 'Ninguno'),
('Mountain Climbers', 'Cardio intenso en posición de plancha', 'CARDIO', 'CORE', 'INTERMEDIO', 10.00, 10, 'Ninguno'),
('Saltos de tijera', 'Ejercicio cardiovascular básico', 'CARDIO', 'CARDIO', 'PRINCIPIANTE', 8.00, 10, 'Ninguno'),
('Trote en el lugar', 'Cardio de bajo impacto', 'CARDIO', 'CARDIO', 'PRINCIPIANTE', 7.00, 20, 'Ninguno'),

-- Fuerza tren superior
('Flexiones de pecho', 'Ejercicio para pecho y tríceps', 'FUERZA', 'PECHO', 'PRINCIPIANTE', 7.00, 10, 'Ninguno'),
('Dominadas', 'Ejercicio para espalda y bíceps', 'FUERZA', 'ESPALDA', 'AVANZADO', 10.00, 10, 'Barra de dominadas'),
('Fondos en banco', 'Ejercicio para tríceps', 'FUERZA', 'TRICEPS', 'INTERMEDIO', 6.00, 10, 'Banco o silla'),
('Curl de bíceps', 'Ejercicio de aislamiento para bíceps', 'FUERZA', 'BICEPS', 'PRINCIPIANTE', 4.00, 10, 'Mancuernas'),

-- Fuerza tren inferior
('Sentadillas', 'Ejercicio fundamental para piernas', 'FUERZA', 'PIERNAS', 'PRINCIPIANTE', 8.00, 15, 'Barra (opcional)'),
('Zancadas', 'Ejercicio para piernas y equilibrio', 'FUERZA', 'PIERNAS', 'PRINCIPIANTE', 6.00, 12, 'Mancuernas (opcional)'),
('Sentadilla con salto', 'Ejercicio pliométrico para piernas', 'FUNCIONAL', 'PIERNAS', 'INTERMEDIO', 10.00, 12, 'Ninguno'),

-- Core
('Plancha', 'Ejercicio isométrico para core', 'FUERZA', 'CORE', 'PRINCIPIANTE', 5.00, 5, 'Colchoneta'),
('Abdominales', 'Ejercicio para abdomen superior', 'FUERZA', 'ABDOMINALES', 'PRINCIPIANTE', 4.00, 10, 'Colchoneta'),
('Giros rusos', 'Ejercicio para oblicuos', 'FUERZA', 'ABDOMINALES', 'INTERMEDIO', 5.00, 10, 'Disco o mancuerna'),
('Elevación de piernas', 'Ejercicio para abdomen bajo', 'FUERZA', 'ABDOMINALES', 'INTERMEDIO', 4.50, 10, 'Colchoneta'),

-- Ejercicios de FLEXIBILIDAD
('Estiramiento de isquiotibiales', 'Flexibilidad para parte posterior de piernas', 'FLEXIBILIDAD', 'ISQUIOTIBIALES', 'PRINCIPIANTE', 2.00, 3, 'Colchoneta'),
('Estiramiento de cuádriceps', 'Flexibilidad para parte frontal de piernas', 'FLEXIBILIDAD', 'CUADRICEPS', 'PRINCIPIANTE', 2.00, 3, 'Ninguno'),

-- Ejercicios de YOGA
('Postura del perro boca abajo', 'Postura fundamental de yoga', 'YOGA', 'CUERPO_COMPLETO', 'PRINCIPIANTE', 3.00, 5, 'Colchoneta de yoga'),
('Saludo al sol', 'Secuencia de yoga completa', 'YOGA', 'CUERPO_COMPLETO', 'INTERMEDIO', 5.00, 5, 'Colchoneta de yoga'),

-- Ejercicios de PILATES
('Hundred Pilates', 'Ejercicio clásico de pilates para core', 'PILATES', 'CORE', 'INTERMEDIO', 4.00, 10, 'Colchoneta'),

-- Ejercicios de EQUILIBRIO
('Equilibrio en una pierna', 'Mejora estabilidad y propiocepción', 'EQUILIBRIO', 'PIERNAS', 'PRINCIPIANTE', 2.00, 5, 'Ninguno'),

-- Ejercicios DEPORTIVOS
('Sprint intervalos', 'Carrera de alta intensidad', 'DEPORTIVO', 'CARDIO', 'AVANZADO', 12.00, 10, 'Pista o calle'),

-- Ejercicios de REHABILITACION
('Rotación de hombro suave', 'Movilidad para rehabilitación de hombro', 'REHABILITACION', 'HOMBROS', 'PRINCIPIANTE', 1.50, 10, 'Banda elástica'),

-- Ejercicios para grupos musculares faltantes
('Press militar con mancuernas', 'Desarrollo de hombros', 'FUERZA', 'HOMBROS', 'INTERMEDIO', 6.00, 10, 'Mancuernas'),
('Elevaciones laterales', 'Aislamiento de hombros', 'FUERZA', 'HOMBROS', 'PRINCIPIANTE', 4.00, 12, 'Mancuernas'),
('Peso muerto rumano', 'Fortalecimiento de isquiotibiales y glúteos', 'FUERZA', 'ISQUIOTIBIALES', 'INTERMEDIO', 8.00, 10, 'Barra o mancuernas'),
('Hip thrust', 'Ejercicio principal para glúteos', 'FUERZA', 'GLUTEOS', 'INTERMEDIO', 7.00, 12, 'Barra y banco'),
('Elevaciones de gemelos', 'Fortalecimiento de pantorrillas', 'FUERZA', 'GEMELOS', 'PRINCIPIANTE', 3.00, 15, 'Escalón o step')
ON CONFLICT (nombre) DO NOTHING;

\echo '  ✅ Ejercicios cargados'
\echo ''

-- ============================================================================
-- MÓDULO 2: COMIDAS (RN10 - cantidad positiva)
-- ============================================================================
\echo '🍽️  [5/9] Cargando comidas...'

INSERT INTO comidas (nombre, tipo_comida, tiempo_elaboracion) VALUES
-- Desayunos
('Avena con frutas y almendras', 'DESAYUNO', 10),
('Huevos revueltos con pan integral', 'DESAYUNO', 15),
('Yogur griego con frutillas', 'DESAYUNO', 5),
('Tortilla de claras con espinaca', 'DESAYUNO', 12),

-- Almuerzos
('Ensalada de pollo a la parrilla', 'ALMUERZO', 20),
('Arroz integral con pollo y vegetales', 'ALMUERZO', 25),
('Pescado al horno con quinoa', 'ALMUERZO', 30),
('Wrap de atún con verduras', 'ALMUERZO', 15),

-- Cenas
('Pescado al horno con verduras', 'CENA', 30),
('Ensalada verde con huevo', 'CENA', 10),
('Pollo a la plancha con brócoli', 'CENA', 25),

-- Snacks y pre-entrenamiento
('Yogur griego con nueces', 'SNACK', 5),
('Batido de proteína con plátano', 'PRE_ENTRENAMIENTO', 5),
('Frutos secos mix', 'COLACION', 2),
('Manzana con mantequilla de almendras', 'SNACK', 3),

-- Comidas adicionales para POST_ENTRENAMIENTO y SNACK
('Batido recuperación con proteína', 'POST_ENTRENAMIENTO', 10),
('Sándwich de pavo con aguacate', 'POST_ENTRENAMIENTO', 15),
('Té verde con galletas integrales', 'SNACK', 5),
('Smoothie de frutas tropicales', 'SNACK', 8)
ON CONFLICT (nombre) DO NOTHING;

\echo '  ✅ Comidas cargadas'
\echo ''

-- ============================================================================
-- RECETAS (Ingredientes por comida) - RN10: Cantidad positiva
-- ============================================================================
\echo '📝 [6/9] Cargando recetas (ingredientes por comida)...'

-- Avena con frutas y almendras (CON NUECES - para test RN16)
INSERT INTO comida_ingredientes (id_comida, id_ingrediente, cantidad_gramos)
SELECT c.id, i.id, 50.00
FROM comidas c, ingredientes i
WHERE c.nombre = 'Avena con frutas y almendras' AND i.nombre = 'Avena'
ON CONFLICT DO NOTHING;

INSERT INTO comida_ingredientes (id_comida, id_ingrediente, cantidad_gramos)
SELECT c.id, i.id, 100.00
FROM comidas c, ingredientes i
WHERE c.nombre = 'Avena con frutas y almendras' AND i.nombre = 'Plátano'
ON CONFLICT DO NOTHING;

INSERT INTO comida_ingredientes (id_comida, id_ingrediente, cantidad_gramos)
SELECT c.id, i.id, 20.00
FROM comidas c, ingredientes i
WHERE c.nombre = 'Avena con frutas y almendras' AND i.nombre = 'Almendras'
ON CONFLICT DO NOTHING;

-- Ensalada de pollo a la parrilla (SIN alérgenos)
INSERT INTO comida_ingredientes (id_comida, id_ingrediente, cantidad_gramos)
SELECT c.id, i.id, 150.00
FROM comidas c, ingredientes i
WHERE c.nombre = 'Ensalada de pollo a la parrilla' AND i.nombre = 'Pollo'
ON CONFLICT DO NOTHING;

INSERT INTO comida_ingredientes (id_comida, id_ingrediente, cantidad_gramos)
SELECT c.id, i.id, 200.00
FROM comidas c, ingredientes i
WHERE c.nombre = 'Ensalada de pollo a la parrilla' AND i.nombre = 'Verduras mix'
ON CONFLICT DO NOTHING;

INSERT INTO comida_ingredientes (id_comida, id_ingrediente, cantidad_gramos)
SELECT c.id, i.id, 10.00
FROM comidas c, ingredientes i
WHERE c.nombre = 'Ensalada de pollo a la parrilla' AND i.nombre = 'Aceite de oliva'
ON CONFLICT DO NOTHING;

-- Pescado al horno con verduras (SIN alérgenos)
INSERT INTO comida_ingredientes (id_comida, id_ingrediente, cantidad_gramos)
SELECT c.id, i.id, 180.00
FROM comidas c, ingredientes i
WHERE c.nombre = 'Pescado al horno con verduras' AND i.nombre = 'Pescado'
ON CONFLICT DO NOTHING;

INSERT INTO comida_ingredientes (id_comida, id_ingrediente, cantidad_gramos)
SELECT c.id, i.id, 150.00
FROM comidas c, ingredientes i
WHERE c.nombre = 'Pescado al horno con verduras' AND i.nombre = 'Brócoli'
ON CONFLICT DO NOTHING;

-- Yogur griego con nueces (CON LÁCTEOS Y NUECES)
INSERT INTO comida_ingredientes (id_comida, id_ingrediente, cantidad_gramos)
SELECT c.id, i.id, 150.00
FROM comidas c, ingredientes i
WHERE c.nombre = 'Yogur griego con nueces' AND i.nombre = 'Yogur'
ON CONFLICT DO NOTHING;

INSERT INTO comida_ingredientes (id_comida, id_ingrediente, cantidad_gramos)
SELECT c.id, i.id, 30.00
FROM comidas c, ingredientes i
WHERE c.nombre = 'Yogur griego con nueces' AND i.nombre = 'Nueces'
ON CONFLICT DO NOTHING;

-- Batido recuperación con proteína (POST_ENTRENAMIENTO)
INSERT INTO comida_ingredientes (id_comida, id_ingrediente, cantidad_gramos)
SELECT c.id, i.id, 200.00
FROM comidas c, ingredientes i
WHERE c.nombre = 'Batido recuperación con proteína' AND i.nombre = 'Leche'
ON CONFLICT DO NOTHING;

INSERT INTO comida_ingredientes (id_comida, id_ingrediente, cantidad_gramos)
SELECT c.id, i.id, 100.00
FROM comidas c, ingredientes i
WHERE c.nombre = 'Batido recuperación con proteína' AND i.nombre = 'Plátano'
ON CONFLICT DO NOTHING;

-- Sándwich de pavo con aguacate (POST_ENTRENAMIENTO)
INSERT INTO comida_ingredientes (id_comida, id_ingrediente, cantidad_gramos)
SELECT c.id, i.id, 60.00
FROM comidas c, ingredientes i
WHERE c.nombre = 'Sándwich de pavo con aguacate' AND i.nombre = 'Pan integral'
ON CONFLICT DO NOTHING;

INSERT INTO comida_ingredientes (id_comida, id_ingrediente, cantidad_gramos)
SELECT c.id, i.id, 50.00
FROM comidas c, ingredientes i
WHERE c.nombre = 'Sándwich de pavo con aguacate' AND i.nombre = 'Aguacate'
ON CONFLICT DO NOTHING;

-- Té verde con galletas integrales (MERIENDA)
INSERT INTO comida_ingredientes (id_comida, id_ingrediente, cantidad_gramos)
SELECT c.id, i.id, 40.00
FROM comidas c, ingredientes i
WHERE c.nombre = 'Té verde con galletas integrales' AND i.nombre = 'Avena'
ON CONFLICT DO NOTHING;

-- Smoothie de frutas tropicales (MERIENDA)
INSERT INTO comida_ingredientes (id_comida, id_ingrediente, cantidad_gramos)
SELECT c.id, i.id, 100.00
FROM comidas c, ingredientes i
WHERE c.nombre = 'Smoothie de frutas tropicales' AND i.nombre = 'Mango'
ON CONFLICT DO NOTHING;

INSERT INTO comida_ingredientes (id_comida, id_ingrediente, cantidad_gramos)
SELECT c.id, i.id, 80.00
FROM comidas c, ingredientes i
WHERE c.nombre = 'Smoothie de frutas tropicales' AND i.nombre = 'Frutillas'
ON CONFLICT DO NOTHING;

-- Arroz integral con pollo y vegetales (SIN alérgenos)
INSERT INTO comida_ingredientes (id_comida, id_ingrediente, cantidad_gramos)
SELECT c.id, i.id, 80.00
FROM comidas c, ingredientes i
WHERE c.nombre = 'Arroz integral con pollo y vegetales' AND i.nombre = 'Arroz integral'
ON CONFLICT DO NOTHING;

INSERT INTO comida_ingredientes (id_comida, id_ingrediente, cantidad_gramos)
SELECT c.id, i.id, 150.00
FROM comidas c, ingredientes i
WHERE c.nombre = 'Arroz integral con pollo y vegetales' AND i.nombre = 'Pollo'
ON CONFLICT DO NOTHING;

INSERT INTO comida_ingredientes (id_comida, id_ingrediente, cantidad_gramos)
SELECT c.id, i.id, 100.00
FROM comidas c, ingredientes i
WHERE c.nombre = 'Arroz integral con pollo y vegetales' AND i.nombre = 'Verduras mix'
ON CONFLICT DO NOTHING;

-- Batido de proteína con plátano (SIN alérgenos)
INSERT INTO comida_ingredientes (id_comida, id_ingrediente, cantidad_gramos)
SELECT c.id, i.id, 30.00
FROM comidas c, ingredientes i
WHERE c.nombre = 'Batido de proteína con plátano' AND i.nombre = 'Proteína en polvo'
ON CONFLICT DO NOTHING;

INSERT INTO comida_ingredientes (id_comida, id_ingrediente, cantidad_gramos)
SELECT c.id, i.id, 120.00
FROM comidas c, ingredientes i
WHERE c.nombre = 'Batido de proteína con plátano' AND i.nombre = 'Plátano'
ON CONFLICT DO NOTHING;

\echo '  ✅ Recetas cargadas'
\echo ''

-- ============================================================================
-- MÓDULO 3: PLANES NUTRICIONALES (US-11, US-12, RN11, RN14, RN28)
-- ============================================================================
\echo '📋 [7/9] Cargando planes nutricionales...'

INSERT INTO planes (nombre, descripcion, duracion_dias, activo) VALUES
('Plan Pérdida de Peso - 7 días', 'Plan nutricional balanceado para perder peso de forma saludable. Déficit calórico moderado con alta proteína.', 7, true),
('Plan Ganancia Muscular - 7 días', 'Plan alto en proteínas y calorías para ganar masa muscular. Incluye 5 comidas al día.', 7, true),
('Plan Mantenimiento - 7 días', 'Plan equilibrado para mantener peso actual. Balance de macronutrientes.', 7, true)
ON CONFLICT (nombre) DO NOTHING;

-- Asignar etiquetas a planes (para filtrado US-07)
INSERT INTO plan_etiquetas (id_plan, id_etiqueta)
SELECT p.id, e.id
FROM planes p, etiquetas e
WHERE p.nombre = 'Plan Pérdida de Peso - 7 días' AND e.nombre = 'Perder Peso'
ON CONFLICT DO NOTHING;

INSERT INTO plan_etiquetas (id_plan, id_etiqueta)
SELECT p.id, e.id
FROM planes p, etiquetas e
WHERE p.nombre = 'Plan Ganancia Muscular - 7 días' AND e.nombre = 'Ganar Masa Muscular'
ON CONFLICT DO NOTHING;

INSERT INTO plan_etiquetas (id_plan, id_etiqueta)
SELECT p.id, e.id
FROM planes p, etiquetas e
WHERE p.nombre = 'Plan Mantenimiento - 7 días' AND e.nombre = 'Mantener Forma'
ON CONFLICT DO NOTHING;

-- Crear días del Plan Pérdida de Peso (US-12)

-- Días 2-7 del plan (con variaciones)

\echo '  ✅ Planes nutricionales cargados'
\echo ''

-- ============================================================================
-- MÓDULO 3: RUTINAS DE EJERCICIO (US-11, US-15, RN11, RN14, RN28)
-- ============================================================================
\echo '🏋️  [8/9] Cargando rutinas de ejercicio...'

INSERT INTO rutinas (nombre, descripcion, duracion_semanas, patron_semanas, nivel_dificultad, activo) VALUES
('Rutina Principiante - 4 semanas', 'Rutina de inicio para personas sedentarias. Patrón de 1 semana que se repite 4 veces. 3 días por semana con ejercicios básicos.', 4, 1, 'PRINCIPIANTE', true),
('Rutina Intermedia - 6 semanas', 'Rutina de nivel medio con mayor intensidad. Patrón de 2 semanas que se repite 3 veces. 4 días por semana.', 6, 2, 'INTERMEDIO', true),
('Rutina Avanzada - 8 semanas', 'Rutina de alta intensidad para personas con experiencia. Patrón de 2 semanas que se repite 4 veces. 5 días por semana.', 8, 2, 'AVANZADO', true)
ON CONFLICT (nombre) DO NOTHING;

-- Asignar etiquetas a rutinas
INSERT INTO rutina_etiquetas (id_rutina, id_etiqueta)
SELECT r.id, e.id
FROM rutinas r, etiquetas e
WHERE r.nombre = 'Rutina Principiante - 4 semanas' AND e.nombre IN ('Perder Peso', 'Mantener Forma')
ON CONFLICT DO NOTHING;

INSERT INTO rutina_etiquetas (id_rutina, id_etiqueta)
SELECT r.id, e.id
FROM rutinas r, etiquetas e
WHERE r.nombre = 'Rutina Intermedia - 6 semanas' AND e.nombre = 'Ganar Masa Muscular'
ON CONFLICT DO NOTHING;

-- Crear ejercicios de la Rutina Principiante (US-15, RN13)
-- Patrón de 1 semana: Lunes (1), Miércoles (3), Viernes (5)
-- Día 1 (Lunes): Piernas y Core
INSERT INTO rutina_ejercicios (id_rutina, id_ejercicio, semana_base, dia_semana, orden, series, repeticiones, descanso_segundos, notas)
SELECT 
    r.id,
    e.id,
    1, -- semana base 1
    1, -- Lunes
    1, -- orden
    3, -- 3 series
    10, -- 10 repeticiones
    60, -- 60 segundos descanso
    'Mantener la espalda recta'
FROM rutinas r, ejercicios e
WHERE r.nombre = 'Rutina Principiante - 4 semanas' AND e.nombre = 'Sentadillas';

-- Día 3 (Miércoles): Pecho y brazos
INSERT INTO rutina_ejercicios (id_rutina, id_ejercicio, semana_base, dia_semana, orden, series, repeticiones, descanso_segundos, notas)
SELECT 
    r.id,
    e.id,
    1, -- semana base 1
    3, -- Miércoles
    1,
    3,
    8,
    60,
    'Bajar hasta que los codos formen 90 grados'
FROM rutinas r, ejercicios e
WHERE r.nombre = 'Rutina Principiante - 4 semanas' AND e.nombre = 'Flexiones de pecho';

-- Día 5 (Viernes): Core
INSERT INTO rutina_ejercicios (id_rutina, id_ejercicio, semana_base, dia_semana, orden, series, repeticiones, duracion_minutos, descanso_segundos, notas)
SELECT 
    r.id,
    e.id,
    1, -- semana base 1
    5, -- Viernes
    1,
    3,
    1, -- 1 repetición (mantener posición)
    1, -- 1 minuto (ejercicio isométrico)
    60,
    'Mantener cuerpo alineado'
FROM rutinas r, ejercicios e
WHERE r.nombre = 'Rutina Principiante - 4 semanas' AND e.nombre = 'Plancha';

\echo '  ✅ Rutinas de ejercicio cargadas'
\echo ''

-- ============================================================================
-- MÓDULO 1 y 2: PERFILES DE USUARIOS (admin y demo)
-- ============================================================================
\echo '👤 [9/9] Cargando perfiles y mediciones...'

-- Perfil de salud - ADMIN (objetivo: mantener forma)
INSERT INTO usuario_perfil_salud (id_perfil, objetivo_actual, nivel_actividad_actual, fecha_actualizacion) 
VALUES (1, 'MANTENER_FORMA', 'ALTO', '2025-11-05')
ON CONFLICT (id_perfil) DO UPDATE 
SET objetivo_actual = 'MANTENER_FORMA', 
    nivel_actividad_actual = 'ALTO',
    fecha_actualizacion = '2025-11-05';

-- Perfil de salud - DEMO (objetivo: perder peso)
INSERT INTO usuario_perfil_salud (id_perfil, objetivo_actual, nivel_actividad_actual, fecha_actualizacion) 
VALUES (2, 'PERDER_PESO', 'MODERADO', '2025-11-05')
ON CONFLICT (id_perfil) DO UPDATE 
SET objetivo_actual = 'PERDER_PESO', 
    nivel_actividad_actual = 'MODERADO',
    fecha_actualizacion = '2025-11-05';

-- Etiquetas de salud para ADMIN (condición: bajo en sodio)
INSERT INTO usuario_etiquetas_salud (id_perfil, id_etiqueta)
SELECT 1, e.id FROM etiquetas e WHERE e.nombre = 'Bajo en sodio'
ON CONFLICT DO NOTHING;

-- Usuario DEMO tiene alergia a NUECES (para test RN16)
INSERT INTO usuario_etiquetas_salud (id_perfil, id_etiqueta)
SELECT 2, e.id
FROM etiquetas e
WHERE e.nombre = 'Nueces'
ON CONFLICT DO NOTHING;

-- Historial de mediciones - ADMIN (RN22: validación de rangos)
INSERT INTO usuario_historial_medidas (id_cliente, fecha_medicion, peso, altura) VALUES
(1, '2025-09-01', 70.0, 175),
(1, '2025-09-08', 70.2, 175),
(1, '2025-09-15', 69.8, 175),
(1, '2025-09-22', 70.1, 175),
(1, '2025-09-29', 70.0, 175),
(1, '2025-10-06', 70.3, 175),
(1, '2025-10-13', 70.0, 175),
(1, '2025-10-20', 70.2, 175),
(1, '2025-10-27', 70.4, 175),
(1, '2025-11-03', 70.5, 175),
(1, '2025-11-05', 70.5, 175)
ON CONFLICT (id_cliente, fecha_medicion) DO NOTHING;

-- Historial de mediciones - DEMO (progreso positivo, RN23: gráfico)
INSERT INTO usuario_historial_medidas (id_cliente, fecha_medicion, peso, altura) VALUES
(2, '2025-09-01', 78.0, 168),
(2, '2025-09-08', 77.5, 168),
(2, '2025-09-15', 76.8, 168),
(2, '2025-09-22', 76.2, 168),
(2, '2025-09-29', 75.5, 168),
(2, '2025-10-06', 75.0, 168),
(2, '2025-10-13', 74.2, 168),
(2, '2025-10-20', 73.8, 168),
(2, '2025-10-27', 73.0, 168),
(2, '2025-11-03', 72.5, 168),
(2, '2025-11-05', 72.5, 168)
ON CONFLICT (id_cliente, fecha_medicion) DO NOTHING;

\echo '  ✅ Perfiles y mediciones cargados'
\echo ''

-- ============================================================================
-- MÓDULO 4: ASIGNACIONES DE PLANES Y RUTINAS (US-18, US-19, US-20, RN17)
-- ============================================================================
\echo '🎯 Cargando asignaciones activas...'

-- DEMO tiene plan activo (Plan Pérdida de Peso)
INSERT INTO usuarios_planes (id_perfil_usuario, id_plan, fecha_inicio, dia_actual, estado, created_at)
SELECT 
    2, -- perfil demo
    p.id,
    '2025-10-29', -- Inicio hace 1 semana
    7, -- Día 7 (último día)
    'ACTIVO',
    CURRENT_TIMESTAMP
FROM planes p
WHERE p.nombre = 'Plan Pérdida de Peso - 7 días'
ON CONFLICT (id_perfil_usuario, id_plan, estado) DO NOTHING;

-- DEMO tiene rutina activa (Rutina Principiante)
INSERT INTO usuarios_rutinas (id_perfil_usuario, id_rutina, fecha_inicio, semana_actual, estado, created_at)
SELECT 
    2, -- perfil demo
    r.id,
    '2025-10-08', -- Inicio hace 4 semanas
    4, -- Semana 4 (última semana)
    'ACTIVO',
    CURRENT_TIMESTAMP
FROM rutinas r
WHERE r.nombre = 'Rutina Principiante - 4 semanas'
ON CONFLICT (id_perfil_usuario, id_rutina, estado) DO NOTHING;

\echo '  ✅ Asignaciones cargadas'
\echo ''

-- ============================================================================
-- MÓDULO 5: REGISTROS DE ACTIVIDADES (US-21, US-22, US-23)
-- ============================================================================
\echo '✅ Cargando registros de actividades...'

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

-- Registros adicionales para historial completo (últimos 30 días - DEMO)
INSERT INTO registros_comidas (id_perfil_usuario, id_comida, fecha, hora, tipo_comida, porciones, calorias_consumidas)
SELECT p.id, c.id, CURRENT_DATE - INTERVAL '7 days', '08:30:00', 'DESAYUNO', 1.0, 350.00
FROM perfiles_usuario p, comidas c, cuentas_auth ca
WHERE ca.email = 'demo@nutritrack.com' AND p.id_usuario = ca.id AND c.nombre = 'Huevos revueltos con pan integral'
ON CONFLICT DO NOTHING;

INSERT INTO registros_comidas (id_perfil_usuario, id_comida, fecha, hora, tipo_comida, porciones, calorias_consumidas)
SELECT p.id, c.id, CURRENT_DATE - INTERVAL '7 days', '14:00:00', 'ALMUERZO', 1.0, 450.00
FROM perfiles_usuario p, comidas c, cuentas_auth ca
WHERE ca.email = 'demo@nutritrack.com' AND p.id_usuario = ca.id AND c.nombre = 'Arroz integral con pollo y vegetales'
ON CONFLICT DO NOTHING;

INSERT INTO registros_comidas (id_perfil_usuario, id_comida, fecha, hora, tipo_comida, porciones, calorias_consumidas)
SELECT p.id, c.id, CURRENT_DATE - INTERVAL '7 days', '20:00:00', 'CENA', 1.0, 400.00
FROM perfiles_usuario p, comidas c, cuentas_auth ca
WHERE ca.email = 'demo@nutritrack.com' AND p.id_usuario = ca.id AND c.nombre = 'Pescado al horno con verduras'
ON CONFLICT DO NOTHING;

INSERT INTO registros_comidas (id_perfil_usuario, id_comida, fecha, hora, tipo_comida, porciones, calorias_consumidas)
SELECT p.id, c.id, CURRENT_DATE - INTERVAL '14 days', '09:00:00', 'DESAYUNO', 1.0, 320.00
FROM perfiles_usuario p, comidas c, cuentas_auth ca
WHERE ca.email = 'demo@nutritrack.com' AND p.id_usuario = ca.id AND c.nombre = 'Avena con frutas y almendras'
ON CONFLICT DO NOTHING;

INSERT INTO registros_comidas (id_perfil_usuario, id_comida, fecha, hora, tipo_comida, porciones, calorias_consumidas)
SELECT p.id, c.id, CURRENT_DATE - INTERVAL '14 days', '16:00:00', 'SNACK', 1.0, 180.00
FROM perfiles_usuario p, comidas c, cuentas_auth ca
WHERE ca.email = 'demo@nutritrack.com' AND p.id_usuario = ca.id AND c.nombre = 'Té verde con galletas integrales'
ON CONFLICT DO NOTHING;

INSERT INTO registros_comidas (id_perfil_usuario, id_comida, fecha, hora, tipo_comida, porciones, calorias_consumidas)
SELECT p.id, c.id, CURRENT_DATE - INTERVAL '21 days', '07:30:00', 'DESAYUNO', 1.0, 280.00
FROM perfiles_usuario p, comidas c, cuentas_auth ca
WHERE ca.email = 'demo@nutritrack.com' AND p.id_usuario = ca.id AND c.nombre = 'Yogur griego con frutillas'
ON CONFLICT DO NOTHING;

INSERT INTO registros_comidas (id_perfil_usuario, id_comida, fecha, hora, tipo_comida, porciones, calorias_consumidas)
SELECT p.id, c.id, CURRENT_DATE - INTERVAL '21 days', '18:00:00', 'POST_ENTRENAMIENTO', 1.0, 350.00
FROM perfiles_usuario p, comidas c, cuentas_auth ca
WHERE ca.email = 'demo@nutritrack.com' AND p.id_usuario = ca.id AND c.nombre = 'Batido recuperación con proteína'
ON CONFLICT DO NOTHING;

INSERT INTO registros_comidas (id_perfil_usuario, id_comida, fecha, hora, tipo_comida, porciones, calorias_consumidas)
SELECT p.id, c.id, CURRENT_DATE - INTERVAL '28 days', '12:30:00', 'ALMUERZO', 1.0, 420.00
FROM perfiles_usuario p, comidas c, cuentas_auth ca
WHERE ca.email = 'demo@nutritrack.com' AND p.id_usuario = ca.id AND c.nombre = 'Wrap de atún con verduras'
ON CONFLICT DO NOTHING;

-- Registros de ejercicios adicionales (últimos 30 días - DEMO)
INSERT INTO registros_ejercicios (id_perfil_usuario, id_ejercicio, fecha, hora, series_realizadas, repeticiones_realizadas, duracion_minutos, calorias_quemadas)
SELECT p.id, e.id, CURRENT_DATE - INTERVAL '5 days', '07:00:00', 3, 15, 10, 80.00
FROM perfiles_usuario p, ejercicios e, cuentas_auth ca
WHERE ca.email = 'demo@nutritrack.com' AND p.id_usuario = ca.id AND e.nombre = 'Sentadillas'
ON CONFLICT DO NOTHING;

INSERT INTO registros_ejercicios (id_perfil_usuario, id_ejercicio, fecha, hora, series_realizadas, repeticiones_realizadas, duracion_minutos, calorias_quemadas)
SELECT p.id, e.id, CURRENT_DATE - INTERVAL '5 days', '07:15:00', 3, 10, 8, 60.00
FROM perfiles_usuario p, ejercicios e, cuentas_auth ca
WHERE ca.email = 'demo@nutritrack.com' AND p.id_usuario = ca.id AND e.nombre = 'Flexiones de pecho'
ON CONFLICT DO NOTHING;

INSERT INTO registros_ejercicios (id_perfil_usuario, id_ejercicio, fecha, hora, series_realizadas, repeticiones_realizadas, duracion_minutos, calorias_quemadas)
SELECT p.id, e.id, CURRENT_DATE - INTERVAL '10 days', '18:30:00', NULL, NULL, 20, 180.00
FROM perfiles_usuario p, ejercicios e, cuentas_auth ca
WHERE ca.email = 'demo@nutritrack.com' AND p.id_usuario = ca.id AND e.nombre = 'Trote en el lugar'
ON CONFLICT DO NOTHING;

INSERT INTO registros_ejercicios (id_perfil_usuario, id_ejercicio, fecha, hora, series_realizadas, repeticiones_realizadas, duracion_minutos, calorias_quemadas)
SELECT p.id, e.id, CURRENT_DATE - INTERVAL '12 days', '19:00:00', 3, 12, 10, 70.00
FROM perfiles_usuario p, ejercicios e, cuentas_auth ca
WHERE ca.email = 'demo@nutritrack.com' AND p.id_usuario = ca.id AND e.nombre = 'Zancadas'
ON CONFLICT DO NOTHING;

INSERT INTO registros_ejercicios (id_perfil_usuario, id_ejercicio, fecha, hora, series_realizadas, repeticiones_realizadas, duracion_minutos, calorias_quemadas)
SELECT p.id, e.id, CURRENT_DATE - INTERVAL '20 days', '06:30:00', 4, NULL, 5, 50.00
FROM perfiles_usuario p, ejercicios e, cuentas_auth ca
WHERE ca.email = 'demo@nutritrack.com' AND p.id_usuario = ca.id AND e.nombre = 'Saludo al sol'
ON CONFLICT DO NOTHING;

INSERT INTO registros_ejercicios (id_perfil_usuario, id_ejercicio, fecha, hora, series_realizadas, repeticiones_realizadas, duracion_minutos, calorias_quemadas)
SELECT p.id, e.id, CURRENT_DATE - INTERVAL '25 days', '17:00:00', 3, 10, 6, 40.00
FROM perfiles_usuario p, ejercicios e, cuentas_auth ca
WHERE ca.email = 'demo@nutritrack.com' AND p.id_usuario = ca.id AND e.nombre = 'Abdominales'
ON CONFLICT DO NOTHING;

-- Registros para usuario ADMIN (para pruebas de múltiples usuarios)
INSERT INTO registros_comidas (id_perfil_usuario, id_comida, fecha, hora, tipo_comida, porciones, calorias_consumidas)
SELECT p.id, c.id, CURRENT_DATE - INTERVAL '1 day', '08:00:00', 'DESAYUNO', 1.0, 300.00
FROM perfiles_usuario p, comidas c, cuentas_auth ca
WHERE ca.email = 'admin@nutritrack.com' AND p.id_usuario = ca.id AND c.nombre = 'Tortilla de claras con espinaca'
ON CONFLICT DO NOTHING;

INSERT INTO registros_ejercicios (id_perfil_usuario, id_ejercicio, fecha, hora, series_realizadas, repeticiones_realizadas, duracion_minutos, calorias_quemadas)
SELECT p.id, e.id, CURRENT_DATE - INTERVAL '2 days', '06:00:00', NULL, NULL, 30, 300.00
FROM perfiles_usuario p, ejercicios e, cuentas_auth ca
WHERE ca.email = 'admin@nutritrack.com' AND p.id_usuario = ca.id AND e.nombre = 'Sprint intervalos'
ON CONFLICT DO NOTHING;

\echo '✅ Patch aplicado correctamente'

