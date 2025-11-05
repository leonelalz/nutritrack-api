-- ============================================================================
-- DATOS DE DEMOSTRACIÓN - MÓDULO 2
-- Catálogo de Comidas, Ingredientes, Ejercicios y Etiquetas
-- ============================================================================

-- ============================================================================
-- ETIQUETAS (Sistema de clasificación)
-- ============================================================================

INSERT INTO etiquetas (nombre, tipo_etiqueta, descripcion) VALUES
-- Objetivos de Salud (OBJETIVO)
('Pérdida de Peso', 'OBJETIVO', 'Para personas que buscan reducir su peso corporal'),
('Ganancia Muscular', 'OBJETIVO', 'Para personas que buscan aumentar masa muscular'),
('Mantener Forma', 'OBJETIVO', 'Para personas que buscan mantener su condición física actual'),
('Mejorar Resistencia', 'OBJETIVO', 'Para mejorar la capacidad cardiovascular y resistencia'),
('Mejorar Fuerza', 'OBJETIVO', 'Para aumentar la fuerza muscular'),
('Rehabilitación', 'OBJETIVO', 'Para recuperación de lesiones'),

-- Dificultad de Ejercicios (DIFICULTAD)
('Principiante', 'DIFICULTAD', 'Ejercicios básicos para personas que comienzan'),
('Intermedio', 'DIFICULTAD', 'Ejercicios de nivel medio'),
('Avanzado', 'DIFICULTAD', 'Ejercicios complejos para personas experimentadas'),

-- Grupos Musculares (GRUPO_MUSCULAR)
('Pecho', 'GRUPO_MUSCULAR', 'Músculos pectorales'),
('Espalda', 'GRUPO_MUSCULAR', 'Músculos dorsales y trapecio'),
('Hombros', 'GRUPO_MUSCULAR', 'Deltoides'),
('Brazos', 'GRUPO_MUSCULAR', 'Bíceps y tríceps'),
('Piernas', 'GRUPO_MUSCULAR', 'Cuádriceps, isquiotibiales y glúteos'),
('Core', 'GRUPO_MUSCULAR', 'Abdominales y zona lumbar'),
('Cardio', 'GRUPO_MUSCULAR', 'Ejercicios cardiovasculares'),
('Cuerpo Completo', 'GRUPO_MUSCULAR', 'Ejercicios que trabajan todo el cuerpo'),

-- Tipos de Ejercicio (TIPO_EJERCICIO)
('Fuerza', 'TIPO_EJERCICIO', 'Ejercicios de levantamiento de peso'),
('Cardio', 'TIPO_EJERCICIO', 'Ejercicios aeróbicos'),
('Flexibilidad', 'TIPO_EJERCICIO', 'Estiramientos y movilidad'),
('HIIT', 'TIPO_EJERCICIO', 'Intervalos de alta intensidad'),
('Pliométrico', 'TIPO_EJERCICIO', 'Ejercicios explosivos'),

-- Restricciones Dietéticas (DIETA)
('Vegetariano', 'DIETA', 'Sin carne ni pescado'),
('Vegano', 'DIETA', 'Sin productos de origen animal'),
('Sin Gluten', 'DIETA', 'Libre de gluten'),
('Sin Lactosa', 'DIETA', 'Libre de lactosa'),
('Bajo en Carbohidratos', 'DIETA', 'Reducido en carbohidratos'),
('Alto en Proteínas', 'DIETA', 'Rico en proteínas'),
('Bajo en Grasas', 'DIETA', 'Reducido en grasas'),

-- Alergias Comunes (ALERGIA)
('Alérgeno: Gluten', 'ALERGIA', 'Contiene gluten (trigo, cebada, centeno)'),
('Alérgeno: Lactosa', 'ALERGIA', 'Contiene lactosa (productos lácteos)'),
('Alérgeno: Nueces', 'ALERGIA', 'Contiene frutos secos'),
('Alérgeno: Soja', 'ALERGIA', 'Contiene soja'),
('Alérgeno: Huevo', 'ALERGIA', 'Contiene huevo'),
('Alérgeno: Pescado', 'ALERGIA', 'Contiene pescado o mariscos')
ON CONFLICT (nombre) DO NOTHING;

-- ============================================================================
-- INGREDIENTES (Base de datos nutricional)
-- ============================================================================

INSERT INTO ingredientes (nombre, energia, proteinas, carbohidratos, grasas, grupo_alimenticio) VALUES
-- Proteínas
('Pechuga de Pollo', 165.0, 31.0, 0.0, 3.6, 'PROTEINAS_ANIMALES'),
('Salmón', 208.0, 20.0, 0.0, 13.0, 'PROTEINAS_ANIMALES'),
('Atún', 116.0, 26.0, 0.0, 0.6, 'PROTEINAS_ANIMALES'),
('Huevos', 155.0, 13.0, 1.1, 11.0, 'PROTEINAS_ANIMALES'),
('Tofu', 76.0, 8.0, 1.9, 4.8, 'PROTEINAS_VEGETALES'),
('Carne Molida 90/10', 176.0, 20.0, 0.0, 10.0, 'PROTEINAS_ANIMALES'),

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
-- ASOCIAR ETIQUETAS A INGREDIENTES
-- ============================================================================

-- Asociar restricciones dietéticas
INSERT INTO etiquetas_ingredientes (id_ingrediente, id_etiqueta)
SELECT i.id, e.id FROM ingredientes i, etiquetas e
WHERE i.nombre = 'Tofu' AND e.nombre = 'Vegano'
ON CONFLICT DO NOTHING;

INSERT INTO etiquetas_ingredientes (id_ingrediente, id_etiqueta)
SELECT i.id, e.id FROM ingredientes i, etiquetas e
WHERE i.nombre = 'Tofu' AND e.nombre = 'Vegetariano'
ON CONFLICT DO NOTHING;

INSERT INTO etiquetas_ingredientes (id_ingrediente, id_etiqueta)
SELECT i.id, e.id FROM ingredientes i, etiquetas e
WHERE i.nombre = 'Pechuga de Pollo' AND e.nombre = 'Alto en Proteínas'
ON CONFLICT DO NOTHING;

INSERT INTO etiquetas_ingredientes (id_ingrediente, id_etiqueta)
SELECT i.id, e.id FROM ingredientes i, etiquetas e
WHERE i.nombre = 'Avena' AND e.nombre = 'Sin Gluten'
ON CONFLICT DO NOTHING;

INSERT INTO etiquetas_ingredientes (id_ingrediente, id_etiqueta)
SELECT i.id, e.id FROM ingredientes i, etiquetas e
WHERE i.nombre IN ('Leche Descremada', 'Yogur Griego', 'Queso Cottage') AND e.nombre = 'Alérgeno: Lactosa'
ON CONFLICT DO NOTHING;

INSERT INTO etiquetas_ingredientes (id_ingrediente, id_etiqueta)
SELECT i.id, e.id FROM ingredientes i, etiquetas e
WHERE i.nombre IN ('Pan Integral', 'Pasta Integral') AND e.nombre = 'Alérgeno: Gluten'
ON CONFLICT DO NOTHING;

INSERT INTO etiquetas_ingredientes (id_ingrediente, id_etiqueta)
SELECT i.id, e.id FROM ingredientes i, etiquetas e
WHERE i.nombre IN ('Almendras', 'Nueces') AND e.nombre = 'Alérgeno: Nueces'
ON CONFLICT DO NOTHING;

-- ============================================================================
-- COMIDAS (Recetas completas)
-- ============================================================================

INSERT INTO comidas (nombre, descripcion, instrucciones_preparacion, tiempo_preparacion_min, porciones, activo) VALUES
-- Desayunos
('Avena con frutas y almendras', 
 'Desayuno completo rico en fibra y proteínas',
 '1. Cocinar la avena con agua o leche. 2. Agregar frutas frescas cortadas. 3. Espolvorear almendras picadas. 4. Opcional: agregar miel.',
 10, 1, true),

('Huevos revueltos con pan integral',
 'Desayuno proteico y energético',
 '1. Batir los huevos con sal y pimienta. 2. Cocinar en sartén con aceite de oliva. 3. Servir con pan integral tostado. 4. Acompañar con tomate.',
 8, 1, true),

('Yogur griego con nueces',
 'Snack o desayuno ligero alto en proteínas',
 '1. Servir yogur griego en un bowl. 2. Agregar nueces picadas. 3. Opcional: agregar frutas o miel.',
 3, 1, true),

-- Almuerzos/Cenas
('Ensalada de pollo a la parrilla',
 'Ensalada completa baja en calorías',
 '1. Cocinar pechuga de pollo a la parrilla. 2. Cortar en tiras. 3. Mezclar con lechuga, tomate, pepino. 4. Aliñar con aceite de oliva y limón.',
 15, 1, true),

('Salmón al horno con verduras',
 'Plato rico en omega-3 y vitaminas',
 '1. Marinar el salmón con limón y especias. 2. Colocar en bandeja con brócoli y zanahoria. 3. Hornear a 180°C por 20 minutos.',
 25, 1, true),

('Arroz integral con pollo y vegetales',
 'Comida balanceada completa',
 '1. Cocinar arroz integral. 2. Saltear pollo en trozos. 3. Agregar brócoli y zanahoria. 4. Mezclar todo y sazonar.',
 30, 1, true),

('Pescado al horno con verduras',
 'Cena ligera rica en proteínas',
 '1. Marinar atún con especias. 2. Hornear con tomate y espinaca. 3. Servir caliente.',
 20, 1, true),

-- Snacks
('Batido de proteína con plátano',
 'Snack post-entrenamiento',
 '1. Mezclar yogur griego con plátano. 2. Agregar almendras. 3. Licuar hasta obtener consistencia cremosa.',
 5, 1, true),

-- Vegetarianas/Veganas
('Bowl de quinoa y tofu',
 'Plato vegano completo',
 '1. Cocinar quinoa. 2. Saltear tofu en cubos. 3. Agregar espinaca y aguacate. 4. Servir todo junto.',
 20, 1, true),

('Ensalada de quinoa mediterránea',
 'Ensalada vegetariana nutritiva',
 '1. Cocinar quinoa y enfriar. 2. Mezclar con tomate, pepino, aceitunas. 3. Aliñar con aceite de oliva.',
 15, 1, true)
ON CONFLICT (nombre) DO NOTHING;

-- ============================================================================
-- RECETAS (Ingredientes por comida)
-- ============================================================================

-- Avena con frutas y almendras
INSERT INTO recetas (id_comida, id_ingrediente, cantidad_gramos)
SELECT c.id, i.id, 50.0 FROM comidas c, ingredientes i
WHERE c.nombre = 'Avena con frutas y almendras' AND i.nombre = 'Avena'
ON CONFLICT DO NOTHING;

INSERT INTO recetas (id_comida, id_ingrediente, cantidad_gramos)
SELECT c.id, i.id, 100.0 FROM comidas c, ingredientes i
WHERE c.nombre = 'Avena con frutas y almendras' AND i.nombre = 'Plátano'
ON CONFLICT DO NOTHING;

INSERT INTO recetas (id_comida, id_ingrediente, cantidad_gramos)
SELECT c.id, i.id, 50.0 FROM comidas c, ingredientes i
WHERE c.nombre = 'Avena con frutas y almendras' AND i.nombre = 'Fresa'
ON CONFLICT DO NOTHING;

INSERT INTO recetas (id_comida, id_ingrediente, cantidad_gramos)
SELECT c.id, i.id, 15.0 FROM comidas c, ingredientes i
WHERE c.nombre = 'Avena con frutas y almendras' AND i.nombre = 'Almendras'
ON CONFLICT DO NOTHING;

-- Ensalada de pollo a la parrilla
INSERT INTO recetas (id_comida, id_ingrediente, cantidad_gramos)
SELECT c.id, i.id, 150.0 FROM comidas c, ingredientes i
WHERE c.nombre = 'Ensalada de pollo a la parrilla' AND i.nombre = 'Pechuga de Pollo'
ON CONFLICT DO NOTHING;

INSERT INTO recetas (id_comida, id_ingrediente, cantidad_gramos)
SELECT c.id, i.id, 80.0 FROM comidas c, ingredientes i
WHERE c.nombre = 'Ensalada de pollo a la parrilla' AND i.nombre = 'Lechuga'
ON CONFLICT DO NOTHING;

INSERT INTO recetas (id_comida, id_ingrediente, cantidad_gramos)
SELECT c.id, i.id, 50.0 FROM comidas c, ingredientes i
WHERE c.nombre = 'Ensalada de pollo a la parrilla' AND i.nombre = 'Tomate'
ON CONFLICT DO NOTHING;

INSERT INTO recetas (id_comida, id_ingrediente, cantidad_gramos)
SELECT c.id, i.id, 50.0 FROM comidas c, ingredientes i
WHERE c.nombre = 'Ensalada de pollo a la parrilla' AND i.nombre = 'Pepino'
ON CONFLICT DO NOTHING;

INSERT INTO recetas (id_comida, id_ingrediente, cantidad_gramos)
SELECT c.id, i.id, 10.0 FROM comidas c, ingredientes i
WHERE c.nombre = 'Ensalada de pollo a la parrilla' AND i.nombre = 'Aceite de Oliva'
ON CONFLICT DO NOTHING;

-- Salmón al horno con verduras
INSERT INTO recetas (id_comida, id_ingrediente, cantidad_gramos)
SELECT c.id, i.id, 150.0 FROM comidas c, ingredientes i
WHERE c.nombre = 'Salmón al horno con verduras' AND i.nombre = 'Salmón'
ON CONFLICT DO NOTHING;

INSERT INTO recetas (id_comida, id_ingrediente, cantidad_gramos)
SELECT c.id, i.id, 100.0 FROM comidas c, ingredientes i
WHERE c.nombre = 'Salmón al horno con verduras' AND i.nombre = 'Brócoli'
ON CONFLICT DO NOTHING;

INSERT INTO recetas (id_comida, id_ingrediente, cantidad_gramos)
SELECT c.id, i.id, 80.0 FROM comidas c, ingredientes i
WHERE c.nombre = 'Salmón al horno con verduras' AND i.nombre = 'Zanahoria'
ON CONFLICT DO NOTHING;

-- Arroz integral con pollo y vegetales
INSERT INTO recetas (id_comida, id_ingrediente, cantidad_gramos)
SELECT c.id, i.id, 80.0 FROM comidas c, ingredientes i
WHERE c.nombre = 'Arroz integral con pollo y vegetales' AND i.nombre = 'Arroz Integral'
ON CONFLICT DO NOTHING;

INSERT INTO recetas (id_comida, id_ingrediente, cantidad_gramos)
SELECT c.id, i.id, 120.0 FROM comidas c, ingredientes i
WHERE c.nombre = 'Arroz integral con pollo y vegetales' AND i.nombre = 'Pechuga de Pollo'
ON CONFLICT DO NOTHING;

INSERT INTO recetas (id_comida, id_ingrediente, cantidad_gramos)
SELECT c.id, i.id, 80.0 FROM comidas c, ingredientes i
WHERE c.nombre = 'Arroz integral con pollo y vegetales' AND i.nombre = 'Brócoli'
ON CONFLICT DO NOTHING;

INSERT INTO recetas (id_comida, id_ingrediente, cantidad_gramos)
SELECT c.id, i.id, 50.0 FROM comidas c, ingredientes i
WHERE c.nombre = 'Arroz integral con pollo y vegetales' AND i.nombre = 'Zanahoria'
ON CONFLICT DO NOTHING;

-- ============================================================================
-- EJERCICIOS (Catálogo de ejercicios)
-- ============================================================================

INSERT INTO ejercicios (nombre, descripcion, instrucciones, equipamiento_requerido, activo) VALUES
-- Ejercicios de Pecho
('Flexiones de pecho (Push-ups)',
 'Ejercicio fundamental de peso corporal para pecho, hombros y tríceps',
 '1. Posición de plancha con manos a la altura de los hombros. 2. Bajar el cuerpo manteniendo la espalda recta. 3. Empujar hacia arriba hasta posición inicial. 4. Repetir manteniendo core activo.',
 'Sin equipamiento',
 true),

('Press de banca con barra',
 'Ejercicio principal para desarrollo de pecho',
 '1. Acostarse en banco plano. 2. Tomar barra con agarre ligeramente más ancho que hombros. 3. Bajar barra hasta pecho. 4. Empujar hacia arriba hasta extensión completa.',
 'Barra, discos, banco',
 true),

-- Ejercicios de Espalda
('Dominadas (Pull-ups)',
 'Ejercicio compuesto para espalda y bíceps',
 '1. Colgar de barra con agarre pronado. 2. Tirar hasta que barbilla supere barra. 3. Bajar controladamente. 4. Mantener core activo durante todo el movimiento.',
 'Barra de dominadas',
 true),

('Remo con barra',
 'Ejercicio para espalda media y baja',
 '1. Inclinarse hacia adelante con espalda recta. 2. Tomar barra con agarre pronado. 3. Tirar barra hacia abdomen. 4. Bajar controladamente.',
 'Barra, discos',
 true),

-- Ejercicios de Piernas
('Sentadillas (Squats)',
 'Ejercicio fundamental para tren inferior',
 '1. Pies a la altura de hombros. 2. Bajar doblando rodillas y caderas. 3. Mantener espalda recta. 4. Bajar hasta muslos paralelos al suelo. 5. Empujar hacia arriba.',
 'Opcional: barra, discos',
 true),

('Zancadas (Lunges)',
 'Ejercicio unilateral para piernas y glúteos',
 '1. Dar paso largo hacia adelante. 2. Bajar hasta rodilla trasera casi toca suelo. 3. Rodilla delantera no pasa punta del pie. 4. Empujar hacia arriba y alternar piernas.',
 'Opcional: mancuernas',
 true),

('Peso muerto (Deadlift)',
 'Ejercicio compuesto para posterior de piernas y espalda baja',
 '1. Pies bajo barra a la altura de caderas. 2. Agarre pronado de la barra. 3. Espalda recta, pecho arriba. 4. Levantar extendiendo caderas y rodillas. 5. Bajar controladamente.',
 'Barra, discos',
 true),

-- Ejercicios de Core
('Plancha (Plank)',
 'Ejercicio isométrico para fortalecimiento de core',
 '1. Posición de antebrazo y pies. 2. Cuerpo en línea recta. 3. Contraer abdomen y glúteos. 4. Mantener posición sin dejar caer cadera.',
 'Sin equipamiento',
 true),

('Abdominales (Crunches)',
 'Ejercicio de aislamiento para abdomen superior',
 '1. Acostarse boca arriba con rodillas dobladas. 2. Manos detrás de la cabeza. 3. Elevar hombros del suelo contrayendo abdomen. 4. Bajar controladamente.',
 'Sin equipamiento',
 true),

('Giros rusos (Russian Twists)',
 'Ejercicio para oblicuos y rotación del core',
 '1. Sentarse con rodillas dobladas y pies elevados. 2. Torso inclinado hacia atrás. 3. Girar torso de lado a lado. 4. Opcional: sostener peso.',
 'Opcional: disco, balón medicinal',
 true),

('Elevación de piernas',
 'Ejercicio para abdomen inferior',
 '1. Acostarse boca arriba. 2. Manos bajo glúteos o al lado del cuerpo. 3. Elevar piernas hasta 90 grados. 4. Bajar controladamente sin tocar el suelo.',
 'Sin equipamiento',
 true),

-- Ejercicios de Cardio
('Saltos de tijera (Jumping Jacks)',
 'Ejercicio cardiovascular de calentamiento',
 '1. Posición inicial: pies juntos, brazos a los lados. 2. Saltar abriendo piernas y elevando brazos. 3. Saltar regresando a posición inicial. 4. Mantener ritmo constante.',
 'Sin equipamiento',
 true),

('Burpees',
 'Ejercicio de cuerpo completo de alta intensidad',
 '1. Posición de pie. 2. Bajar a sentadilla y colocar manos en suelo. 3. Saltar piernas hacia atrás a plancha. 4. Flexión (opcional). 5. Saltar piernas hacia adelante. 6. Saltar arriba con brazos extendidos.',
 'Sin equipamiento',
 true),

('Mountain Climbers',
 'Ejercicio cardiovascular que trabaja core',
 '1. Posición de plancha alta. 2. Llevar rodilla derecha al pecho. 3. Regresar y alternar con rodilla izquierda. 4. Mantener ritmo rápido. 5. Cadera estable.',
 'Sin equipamiento',
 true),

('Trote en el lugar (Jogging in Place)',
 'Ejercicio cardiovascular básico',
 '1. Posición de pie. 2. Trotar levantando rodillas. 3. Balancear brazos naturalmente. 4. Mantener ritmo constante.',
 'Sin equipamiento',
 true),

('Saltos de caja (Box Jumps)',
 'Ejercicio pliométrico para potencia de piernas',
 '1. Posición frente a caja/banco. 2. Balancear brazos hacia atrás. 3. Saltar explosivamente hacia arriba. 4. Aterrizar suavemente en caja. 5. Bajar con control.',
 'Caja pliométrica o banco estable',
 true),

-- Ejercicios de Brazos
('Curl de bíceps con mancuernas',
 'Ejercicio de aislamiento para bíceps',
 '1. Posición de pie con mancuernas. 2. Brazos extendidos a los lados. 3. Flexionar codos llevando mancuernas a hombros. 4. Bajar controladamente.',
 'Mancuernas',
 true),

('Fondos en banco (Dips)',
 'Ejercicio para tríceps y pecho inferior',
 '1. Manos en borde de banco. 2. Piernas extendidas al frente. 3. Bajar doblando codos. 4. Empujar hacia arriba.',
 'Banco o silla estable',
 true),

('Press militar con mancuernas',
 'Ejercicio para hombros',
 '1. Posición de pie o sentado. 2. Mancuernas a altura de hombros. 3. Empujar hacia arriba hasta extensión completa. 4. Bajar controladamente.',
 'Mancuernas',
 true),

-- Ejercicios de Flexibilidad
('Estiramiento de isquiotibiales',
 'Estiramiento para parte posterior de piernas',
 '1. Sentarse con piernas extendidas. 2. Inclinar torso hacia adelante. 3. Alcanzar pies con manos. 4. Mantener 20-30 segundos.',
 'Sin equipamiento',
 true),

('Estiramiento de cuádriceps',
 'Estiramiento para parte frontal del muslo',
 '1. Posición de pie. 2. Doblar rodilla llevando talón al glúteo. 3. Sostener pie con mano. 4. Mantener 20-30 segundos. 5. Alternar piernas.',
 'Sin equipamiento',
 true),

-- Ejercicios Compuestos
('Sentadilla con salto (Jump Squats)',
 'Ejercicio pliométrico para potencia de piernas',
 '1. Realizar sentadilla normal. 2. Al subir, saltar explosivamente. 3. Aterrizar suavemente en sentadilla. 4. Repetir inmediatamente.',
 'Sin equipamiento',
 true),

('Thrusters',
 'Ejercicio compuesto de cuerpo completo',
 '1. Sentadilla frontal con mancuernas. 2. Al subir, presionar mancuernas sobre cabeza. 3. Bajar mancuernas mientras se baja a sentadilla. 4. Movimiento fluido.',
 'Mancuernas o barra',
 true)
ON CONFLICT (nombre) DO NOTHING;

-- ============================================================================
-- ASOCIAR ETIQUETAS A EJERCICIOS
-- ============================================================================

-- Flexiones de pecho - Pecho, Fuerza, Principiante
INSERT INTO etiquetas_ejercicios (id_ejercicio, id_etiqueta)
SELECT e.id, et.id FROM ejercicios e, etiquetas et
WHERE e.nombre = 'Flexiones de pecho (Push-ups)' AND et.nombre IN ('Pecho', 'Fuerza', 'Principiante')
ON CONFLICT DO NOTHING;

-- Dominadas - Espalda, Fuerza, Intermedio
INSERT INTO etiquetas_ejercicios (id_ejercicio, id_etiqueta)
SELECT e.id, et.id FROM ejercicios e, etiquetas et
WHERE e.nombre = 'Dominadas (Pull-ups)' AND et.nombre IN ('Espalda', 'Fuerza', 'Intermedio')
ON CONFLICT DO NOTHING;

-- Sentadillas - Piernas, Fuerza, Principiante
INSERT INTO etiquetas_ejercicios (id_ejercicio, id_etiqueta)
SELECT e.id, et.id FROM ejercicios e, etiquetas et
WHERE e.nombre = 'Sentadillas (Squats)' AND et.nombre IN ('Piernas', 'Fuerza', 'Principiante')
ON CONFLICT DO NOTHING;

-- Plancha - Core, Fuerza, Principiante
INSERT INTO etiquetas_ejercicios (id_ejercicio, id_etiqueta)
SELECT e.id, et.id FROM ejercicios e, etiquetas et
WHERE e.nombre = 'Plancha (Plank)' AND et.nombre IN ('Core', 'Fuerza', 'Principiante')
ON CONFLICT DO NOTHING;

-- Burpees - Cuerpo Completo, HIIT, Intermedio
INSERT INTO etiquetas_ejercicios (id_ejercicio, id_etiqueta)
SELECT e.id, et.id FROM ejercicios e, etiquetas et
WHERE e.nombre = 'Burpees' AND et.nombre IN ('Cuerpo Completo', 'HIIT', 'Intermedio', 'Cardio')
ON CONFLICT DO NOTHING;

-- Mountain Climbers - Core, Cardio, HIIT, Intermedio
INSERT INTO etiquetas_ejercicios (id_ejercicio, id_etiqueta)
SELECT e.id, et.id FROM ejercicios e, etiquetas et
WHERE e.nombre = 'Mountain Climbers' AND et.nombre IN ('Core', 'Cardio', 'HIIT', 'Intermedio')
ON CONFLICT DO NOTHING;

-- Peso muerto - Piernas, Espalda, Fuerza, Avanzado
INSERT INTO etiquetas_ejercicios (id_ejercicio, id_etiqueta)
SELECT e.id, et.id FROM ejercicios e, etiquetas et
WHERE e.nombre = 'Peso muerto (Deadlift)' AND et.nombre IN ('Piernas', 'Espalda', 'Fuerza', 'Avanzado')
ON CONFLICT DO NOTHING;

-- Press de banca - Pecho, Fuerza, Intermedio
INSERT INTO etiquetas_ejercicios (id_ejercicio, id_etiqueta)
SELECT e.id, et.id FROM ejercicios e, etiquetas et
WHERE e.nombre = 'Press de banca con barra' AND et.nombre IN ('Pecho', 'Fuerza', 'Intermedio')
ON CONFLICT DO NOTHING;

-- Saltos de caja - Piernas, Pliométrico, Intermedio
INSERT INTO etiquetas_ejercicios (id_ejercicio, id_etiqueta)
SELECT e.id, et.id FROM ejercicios e, etiquetas et
WHERE e.nombre = 'Saltos de caja (Box Jumps)' AND et.nombre IN ('Piernas', 'Pliométrico', 'Intermedio')
ON CONFLICT DO NOTHING;

-- Zancadas - Piernas, Fuerza, Principiante
INSERT INTO etiquetas_ejercicios (id_ejercicio, id_etiqueta)
SELECT e.id, et.id FROM ejercicios e, etiquetas et
WHERE e.nombre = 'Zancadas (Lunges)' AND et.nombre IN ('Piernas', 'Fuerza', 'Principiante')
ON CONFLICT DO NOTHING;

-- ============================================================================
-- VERIFICACIÓN
-- ============================================================================

SELECT 'MÓDULO 2 - CATÁLOGO' as modulo, '============================================' as separador;

SELECT 'Total Etiquetas:' as tipo, COUNT(*) as cantidad FROM etiquetas;
SELECT 'Total Ingredientes:' as tipo, COUNT(*) as cantidad FROM ingredientes;
SELECT 'Total Comidas:' as tipo, COUNT(*) as cantidad FROM comidas;
SELECT 'Total Ejercicios:' as tipo, COUNT(*) as cantidad FROM ejercicios;

SELECT 'Etiquetas por tipo:' as descripcion, tipo_etiqueta, COUNT(*) as cantidad 
FROM etiquetas 
GROUP BY tipo_etiqueta 
ORDER BY tipo_etiqueta;

SELECT 'Top 5 Ingredientes (por proteína):' as descripcion, nombre, proteinas_por_100g 
FROM ingredientes 
ORDER BY proteinas_por_100g DESC 
LIMIT 5;

SELECT 'Top 5 Comidas (más ingredientes):' as descripcion, c.nombre, COUNT(r.id) as ingredientes
FROM comidas c
LEFT JOIN recetas r ON c.id = r.id_comida
GROUP BY c.nombre
ORDER BY ingredientes DESC
LIMIT 5;
