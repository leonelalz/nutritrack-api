-- ============================================================================
-- RUTINAS DE EJERCICIO
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

-- Verificar
SELECT id, nombre, duracion_semanas, activo FROM rutinas ORDER BY id;
