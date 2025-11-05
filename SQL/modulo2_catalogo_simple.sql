-- ============================================================================
-- DATOS DE DEMOSTRACIÓN - MÓDULO 2 (VERSIÓN CORREGIDA)
-- Catálogo de Comidas, Ingredientes, Ejercicios y Etiquetas
-- ============================================================================

-- ============================================================================
-- ETIQUETAS (Sistema de clasificación) - YA INSERTADAS
-- ============================================================================
-- Las etiquetas ya fueron insertadas exitosamente (34 etiquetas)

-- ============================================================================
-- INGREDIENTES
-- ============================================================================

INSERT INTO ingredientes (nombre, energia, proteinas, carbohidratos, grasas, grupo_alimenticio) VALUES
-- Proteínas
('Pechuga de Pollo', 165.0, 31.0, 0.0, 3.6, 'PROTEINAS_ANIMALES'),
('Salmón', 208.0, 20.0, 0.0, 13.0, 'PROTEINAS_ANIMALES'),
('Atún', 116.0, 26.0, 0.0, 0.6, 'PROTEINAS_ANIMALES'),
('Huevos', 155.0, 13.0, 1.1, 11.0, 'PROTEINAS_ANIMALES'),
('Tofu', 76.0, 8.0, 1.9, 4.8, 'PROTEINAS_VEGETALES'),
('Carne Molida', 176.0, 20.0, 0.0, 10.0, 'PROTEINAS_ANIMALES'),

-- Carbohidratos
('Arroz Integral', 111.0, 2.6, 23.0, 0.9, 'CEREALES'),
('Avena', 389.0, 16.9, 66.3, 6.9, 'CEREALES'),
('Pan Integral', 247.0, 13.0, 41.0, 3.5, 'CEREALES'),
('Pasta Integral', 124.0, 5.0, 26.5, 0.9, 'CEREALES'),
('Papa', 77.0, 2.0, 17.0, 0.1, 'VERDURAS'),
('Batata', 86.0, 1.6, 20.0, 0.1, 'VERDURAS'),
('Quinoa', 120.0, 4.4, 21.3, 1.9, 'CEREALES'),

-- Vegetales
('Brócoli', 34.0, 2.8, 7.0, 0.4, 'VERDURAS'),
('Espinaca', 23.0, 2.9, 3.6, 0.4, 'VERDURAS'),
('Tomate', 18.0, 0.9, 3.9, 0.2, 'VERDURAS'),
('Lechuga', 15.0, 1.4, 2.9, 0.2, 'VERDURAS'),
('Zanahoria', 41.0, 0.9, 10.0, 0.2, 'VERDURAS'),
('Pepino', 16.0, 0.7, 3.6, 0.1, 'VERDURAS'),

-- Frutas
('Plátano', 89.0, 1.1, 23.0, 0.3, 'FRUTAS'),
('Manzana', 52.0, 0.3, 14.0, 0.2, 'FRUTAS'),
('Fresa', 32.0, 0.7, 7.7, 0.3, 'FRUTAS'),
('Naranja', 47.0, 0.9, 12.0, 0.1, 'FRUTAS'),

-- Grasas Saludables
('Aguacate', 160.0, 2.0, 9.0, 15.0, 'GRASAS_SALUDABLES'),
('Almendras', 579.0, 21.0, 22.0, 50.0, 'FRUTOS_SECOS'),
('Aceite de Oliva', 884.0, 0.0, 0.0, 100.0, 'GRASAS_SALUDABLES'),
('Nueces', 654.0, 15.0, 14.0, 65.0, 'FRUTOS_SECOS'),

-- Lácteos
('Leche Descremada', 34.0, 3.4, 5.0, 0.1, 'LACTEOS'),
('Yogur Griego', 59.0, 10.0, 3.6, 0.4, 'LACTEOS'),
('Queso Cottage', 98.0, 11.0, 3.4, 4.3, 'LACTEOS')
ON CONFLICT (nombre) DO NOTHING;

-- ============================================================================
-- COMIDAS
-- ============================================================================

INSERT INTO comidas (nombre, tipo_comida, tiempo_elaboracion) VALUES
-- Desayunos
('Avena con frutas y almendras', 'DESAYUNO', 10),
('Huevos revueltos con pan integral', 'DESAYUNO', 8),
('Yogur griego con nueces', 'DESAYUNO', 3),

-- Almuerzos/Cenas
('Ensalada de pollo a la parrilla', 'ALMUERZO', 15),
('Salmón al horno con verduras', 'CENA', 25),
('Arroz integral con pollo y vegetales', 'ALMUERZO', 30),
('Pescado al horno con verduras', 'CENA', 20),

-- Snacks
('Batido de proteína con plátano', 'SNACK', 5),

-- Vegetarianas/Veganas
('Bowl de quinoa y tofu', 'ALMUERZO', 20),
('Ensalada de quinoa mediterránea', 'ALMUERZO', 15)
ON CONFLICT (nombre) DO NOTHING;

-- ============================================================================
-- RECETAS (Ingredientes por comida)
-- ============================================================================

-- Avena con frutas y almendras
INSERT INTO recetas (id_comida, id_ingrediente, cantidad_ingrediente)
SELECT c.id, i.id, 50.0 FROM comidas c, ingredientes i
WHERE c.nombre = 'Avena con frutas y almendras' AND i.nombre = 'Avena'
ON CONFLICT DO NOTHING;

INSERT INTO recetas (id_comida, id_ingrediente, cantidad_ingrediente)
SELECT c.id, i.id, 100.0 FROM comidas c, ingredientes i
WHERE c.nombre = 'Avena con frutas y almendras' AND i.nombre = 'Plátano'
ON CONFLICT DO NOTHING;

INSERT INTO recetas (id_comida, id_ingrediente, cantidad_ingrediente)
SELECT c.id, i.id, 50.0 FROM comidas c, ingredientes i
WHERE c.nombre = 'Avena con frutas y almendras' AND i.nombre = 'Fresa'
ON CONFLICT DO NOTHING;

INSERT INTO recetas (id_comida, id_ingrediente, cantidad_ingrediente)
SELECT c.id, i.id, 15.0 FROM comidas c, ingredientes i
WHERE c.nombre = 'Avena con frutas y almendras' AND i.nombre = 'Almendras'
ON CONFLICT DO NOTHING;

-- Ensalada de pollo a la parrilla
INSERT INTO recetas (id_comida, id_ingrediente, cantidad_ingrediente)
SELECT c.id, i.id, 150.0 FROM comidas c, ingredientes i
WHERE c.nombre = 'Ensalada de pollo a la parrilla' AND i.nombre = 'Pechuga de Pollo'
ON CONFLICT DO NOTHING;

INSERT INTO recetas (id_comida, id_ingrediente, cantidad_ingrediente)
SELECT c.id, i.id, 80.0 FROM comidas c, ingredientes i
WHERE c.nombre = 'Ensalada de pollo a la parrilla' AND i.nombre = 'Lechuga'
ON CONFLICT DO NOTHING;

INSERT INTO recetas (id_comida, id_ingrediente, cantidad_ingrediente)
SELECT c.id, i.id, 50.0 FROM comidas c, ingredientes i
WHERE c.nombre = 'Ensalada de pollo a la parrilla' AND i.nombre = 'Tomate'
ON CONFLICT DO NOTHING;

INSERT INTO recetas (id_comida, id_ingrediente, cantidad_ingrediente)
SELECT c.id, i.id, 50.0 FROM comidas c, ingredientes i
WHERE c.nombre = 'Ensalada de pollo a la parrilla' AND i.nombre = 'Pepino'
ON CONFLICT DO NOTHING;

INSERT INTO recetas (id_comida, id_ingrediente, cantidad_ingrediente)
SELECT c.id, i.id, 10.0 FROM comidas c, ingredientes i
WHERE c.nombre = 'Ensalada de pollo a la parrilla' AND i.nombre = 'Aceite de Oliva'
ON CONFLICT DO NOTHING;

-- Salmón al horno con verduras
INSERT INTO recetas (id_comida, id_ingrediente, cantidad_ingrediente)
SELECT c.id, i.id, 150.0 FROM comidas c, ingredientes i
WHERE c.nombre = 'Salmón al horno con verduras' AND i.nombre = 'Salmón'
ON CONFLICT DO NOTHING;

INSERT INTO recetas (id_comida, id_ingrediente, cantidad_ingrediente)
SELECT c.id, i.id, 100.0 FROM comidas c, ingredientes i
WHERE c.nombre = 'Salmón al horno con verduras' AND i.nombre = 'Brócoli'
ON CONFLICT DO NOTHING;

INSERT INTO recetas (id_comida, id_ingrediente, cantidad_ingrediente)
SELECT c.id, i.id, 80.0 FROM comidas c, ingredientes i
WHERE c.nombre = 'Salmón al horno con verduras' AND i.nombre = 'Zanahoria'
ON CONFLICT DO NOTHING;

-- Arroz integral con pollo y vegetales
INSERT INTO recetas (id_comida, id_ingrediente, cantidad_ingrediente)
SELECT c.id, i.id, 80.0 FROM comidas c, ingredientes i
WHERE c.nombre = 'Arroz integral con pollo y vegetales' AND i.nombre = 'Arroz Integral'
ON CONFLICT DO NOTHING;

INSERT INTO recetas (id_comida, id_ingrediente, cantidad_ingrediente)
SELECT c.id, i.id, 120.0 FROM comidas c, ingredientes i
WHERE c.nombre = 'Arroz integral con pollo y vegetales' AND i.nombre = 'Pechuga de Pollo'
ON CONFLICT DO NOTHING;

INSERT INTO recetas (id_comida, id_ingrediente, cantidad_ingrediente)
SELECT c.id, i.id, 80.0 FROM comidas c, ingredientes i
WHERE c.nombre = 'Arroz integral con pollo y vegetales' AND i.nombre = 'Brócoli'
ON CONFLICT DO NOTHING;

INSERT INTO recetas (id_comida, id_ingrediente, cantidad_ingrediente)
SELECT c.id, i.id, 50.0 FROM comidas c, ingredientes i
WHERE c.nombre = 'Arroz integral con pollo y vegetales' AND i.nombre = 'Zanahoria'
ON CONFLICT DO NOTHING;

-- Pescado al horno con verduras
INSERT INTO recetas (id_comida, id_ingrediente, cantidad_ingrediente)
SELECT c.id, i.id, 150.0 FROM comidas c, ingredientes i
WHERE c.nombre = 'Pescado al horno con verduras' AND i.nombre = 'Atún'
ON CONFLICT DO NOTHING;

INSERT INTO recetas (id_comida, id_ingrediente, cantidad_ingrediente)
SELECT c.id, i.id, 80.0 FROM comidas c, ingredientes i
WHERE c.nombre = 'Pescado al horno con verduras' AND i.nombre = 'Tomate'
ON CONFLICT DO NOTHING;

INSERT INTO recetas (id_comida, id_ingrediente, cantidad_ingrediente)
SELECT c.id, i.id, 60.0 FROM comidas c, ingredientes i
WHERE c.nombre = 'Pescado al horno con verduras' AND i.nombre = 'Espinaca'
ON CONFLICT DO NOTHING;

-- Batido de proteína con plátano
INSERT INTO recetas (id_comida, id_ingrediente, cantidad_ingrediente)
SELECT c.id, i.id, 150.0 FROM comidas c, ingredientes i
WHERE c.nombre = 'Batido de proteína con plátano' AND i.nombre = 'Yogur Griego'
ON CONFLICT DO NOTHING;

INSERT INTO recetas (id_comida, id_ingrediente, cantidad_ingrediente)
SELECT c.id, i.id, 120.0 FROM comidas c, ingredientes i
WHERE c.nombre = 'Batido de proteína con plátano' AND i.nombre = 'Plátano'
ON CONFLICT DO NOTHING;

INSERT INTO recetas (id_comida, id_ingrediente, cantidad_ingrediente)
SELECT c.id, i.id, 15.0 FROM comidas c, ingredientes i
WHERE c.nombre = 'Batido de proteína con plátano' AND i.nombre = 'Almendras'
ON CONFLICT DO NOTHING;

-- ============================================================================
-- EJERCICIOS
-- ============================================================================

INSERT INTO ejercicios (nombre, tipo_ejercicio, musculo_principal, dificultad, duracion, calorias_estimadas) VALUES
-- Ejercicios de Pecho
('Flexiones de pecho (Push-ups)', 'FUERZA', 'PECHO', 'PRINCIPIANTE', 5, 30.0),
('Press de banca con barra', 'FUERZA', 'PECHO', 'INTERMEDIO', 8, 40.0),

-- Ejercicios de Espalda
('Dominadas (Pull-ups)', 'FUERZA', 'ESPALDA', 'INTERMEDIO', 5, 35.0),
('Remo con barra', 'FUERZA', 'ESPALDA', 'INTERMEDIO', 8, 38.0),

-- Ejercicios de Piernas
('Sentadillas (Squats)', 'FUERZA', 'PIERNAS', 'PRINCIPIANTE', 10, 45.0),
('Zancadas (Lunges)', 'FUERZA', 'PIERNAS', 'PRINCIPIANTE', 8, 40.0),
('Peso muerto (Deadlift)', 'FUERZA', 'PIERNAS', 'AVANZADO', 10, 50.0),

-- Ejercicios de Core
('Plancha (Plank)', 'FUERZA', 'CORE', 'PRINCIPIANTE', 3, 20.0),
('Abdominales (Crunches)', 'FUERZA', 'ABDOMINALES', 'PRINCIPIANTE', 5, 25.0),
('Giros rusos (Russian Twists)', 'FUERZA', 'ABDOMINALES', 'INTERMEDIO', 5, 28.0),
('Elevación de piernas', 'FUERZA', 'ABDOMINALES', 'INTERMEDIO', 5, 27.0),

-- Ejercicios de Cardio
('Saltos de tijera (Jumping Jacks)', 'CARDIO', 'CUERPO_COMPLETO', 'PRINCIPIANTE', 10, 80.0),
('Burpees', 'HIIT', 'CUERPO_COMPLETO', 'INTERMEDIO', 5, 60.0),
('Mountain Climbers', 'HIIT', 'CORE', 'INTERMEDIO', 5, 55.0),
('Trote en el lugar (Jogging in Place)', 'CARDIO', 'CUERPO_COMPLETO', 'PRINCIPIANTE', 10, 70.0),
('Saltos de caja (Box Jumps)', 'HIIT', 'PIERNAS', 'INTERMEDIO', 5, 50.0),

-- Ejercicios de Brazos
('Curl de bíceps con mancuernas', 'FUERZA', 'BICEPS', 'PRINCIPIANTE', 6, 25.0),
('Fondos en banco (Dips)', 'FUERZA', 'TRICEPS', 'INTERMEDIO', 5, 30.0),
('Press militar con mancuernas', 'FUERZA', 'HOMBROS', 'INTERMEDIO', 8, 35.0),

-- Ejercicios de Flexibilidad
('Estiramiento de isquiotibiales', 'FLEXIBILIDAD', 'PIERNAS', 'PRINCIPIANTE', 3, 10.0),
('Estiramiento de cuádriceps', 'FLEXIBILIDAD', 'PIERNAS', 'PRINCIPIANTE', 3, 10.0),

-- Ejercicios Compuestos
('Sentadilla con salto (Jump Squats)', 'HIIT', 'PIERNAS', 'INTERMEDIO', 5, 55.0),
('Thrusters', 'FUERZA', 'CUERPO_COMPLETO', 'AVANZADO', 8, 60.0)
ON CONFLICT (nombre) DO NOTHING;

-- ============================================================================
-- VERIFICACIÓN
-- ============================================================================

SELECT '===== MÓDULO 2 - CATÁLOGO =====' as titulo;

SELECT 'Total Etiquetas:' as tipo, COUNT(*) as cantidad FROM etiquetas;
SELECT 'Total Ingredientes:' as tipo, COUNT(*) as cantidad FROM ingredientes;
SELECT 'Total Comidas:' as tipo, COUNT(*) as cantidad FROM comidas;
SELECT 'Total Ejercicios:' as tipo, COUNT(*) as cantidad FROM ejercicios;

SELECT 'Ingredientes por grupo:' as descripcion, grupo_alimenticio, COUNT(*) as cantidad 
FROM ingredientes 
GROUP BY grupo_alimenticio 
ORDER BY cantidad DESC;

SELECT 'Ejercicios por tipo:' as descripcion, tipo_ejercicio, COUNT(*) as cantidad 
FROM ejercicios 
GROUP BY tipo_ejercicio 
ORDER BY cantidad DESC;

SELECT 'Ejercicios por dificultad:' as descripcion, dificultad, COUNT(*) as cantidad 
FROM ejercicios 
GROUP BY dificultad 
ORDER BY cantidad DESC;
