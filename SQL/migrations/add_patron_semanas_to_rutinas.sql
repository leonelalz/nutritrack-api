-- Migration: Agregar sistema de semanas base a rutinas
-- Fecha: 2025-11-19
-- Descripción: Permite crear rutinas con patrones de semanas que se repiten

-- 1. Agregar columna patron_semanas a rutinas
ALTER TABLE rutinas ADD COLUMN IF NOT EXISTS patron_semanas INTEGER NOT NULL DEFAULT 1;

-- 2. Agregar columnas semana_base y dia_semana a rutina_ejercicios
ALTER TABLE rutina_ejercicios ADD COLUMN IF NOT EXISTS semana_base INTEGER NOT NULL DEFAULT 1;
ALTER TABLE rutina_ejercicios ADD COLUMN IF NOT EXISTS dia_semana INTEGER NOT NULL DEFAULT 1;

-- 3. Actualizar datos existentes (si los hay)
-- Asumimos que los ejercicios existentes pertenecen a la semana 1, día 1
UPDATE rutina_ejercicios
SET semana_base = 1, dia_semana = 1
WHERE semana_base IS NULL OR dia_semana IS NULL;

-- 4. Agregar constraints
ALTER TABLE rutina_ejercicios
ADD CONSTRAINT chk_semana_base_positive CHECK (semana_base >= 1);

ALTER TABLE rutina_ejercicios
ADD CONSTRAINT chk_dia_semana_range CHECK (dia_semana >= 1 AND dia_semana <= 7);

ALTER TABLE rutinas
ADD CONSTRAINT chk_patron_semanas_positive CHECK (patron_semanas >= 1);

ALTER TABLE rutinas
ADD CONSTRAINT chk_patron_no_excede_duracion CHECK (patron_semanas <= duracion_semanas);

-- 5. Crear índice para mejorar búsquedas
CREATE INDEX IF NOT EXISTS idx_rutina_ejercicios_semana_dia 
ON rutina_ejercicios(id_rutina, semana_base, dia_semana, orden);

-- Comentarios en las columnas para documentación
COMMENT ON COLUMN rutinas.patron_semanas IS 'Número de semanas base que se repiten cíclicamente (ej: 2 = patrón de 2 semanas)';
COMMENT ON COLUMN rutina_ejercicios.semana_base IS 'Semana base del patrón (1 a patron_semanas)';
COMMENT ON COLUMN rutina_ejercicios.dia_semana IS 'Día de la semana (1=Lunes, 2=Martes, 3=Miércoles, 4=Jueves, 5=Viernes, 6=Sábado, 7=Domingo)';
COMMENT ON COLUMN rutina_ejercicios.orden IS 'Orden de ejecución dentro del mismo día';
