-- ============================================================================
-- MIGRACIÓN: Convertir TipoComida de enum a tabla dinámica
-- ============================================================================
-- Este script convierte el tipo de comida de un enum fijo a una tabla maestra
-- que permite agregar, modificar y eliminar tipos de comida dinámicamente.
-- ============================================================================

-- 1. Crear tabla maestra de tipos de comida
CREATE TABLE IF NOT EXISTS tipos_comida (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE,
    descripcion VARCHAR(255),
    orden_visualizacion INTEGER DEFAULT 0,
    activo BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Insertar tipos de comida predeterminados
INSERT INTO tipos_comida (nombre, descripcion, orden_visualizacion, activo) VALUES
    ('DESAYUNO', 'Primera comida del día', 1, true),
    ('ALMUERZO', 'Comida principal del mediodía', 2, true),
    ('CENA', 'Comida principal de la noche', 3, true),
    ('SNACK', 'Refrigerio ligero entre comidas', 4, true),
    ('MERIENDA', 'Comida ligera de la tarde', 5, true),
    ('COLACION', 'Tentempié o bocadillo pequeño', 6, true),
    ('PRE_ENTRENAMIENTO', 'Comida antes del ejercicio', 7, true),
    ('POST_ENTRENAMIENTO', 'Comida después del ejercicio', 8, true)
ON CONFLICT (nombre) DO NOTHING;

-- 3. Agregar columna id_tipo_comida a la tabla comidas
ALTER TABLE comidas ADD COLUMN IF NOT EXISTS id_tipo_comida INTEGER;

-- 4. Migrar datos existentes en comidas (si existen)
UPDATE comidas SET id_tipo_comida = (
    SELECT id FROM tipos_comida WHERE nombre = comidas.tipo_comida
) WHERE id_tipo_comida IS NULL AND tipo_comida IS NOT NULL;

-- 5. Agregar columna id_tipo_comida a la tabla plan_dias
ALTER TABLE plan_dias ADD COLUMN IF NOT EXISTS id_tipo_comida INTEGER;

-- 6. Migrar datos existentes en plan_dias (si existen)
UPDATE plan_dias SET id_tipo_comida = (
    SELECT id FROM tipos_comida WHERE nombre = plan_dias.tipo_comida
) WHERE id_tipo_comida IS NULL AND tipo_comida IS NOT NULL;

-- 7. Eliminar CHECK constraints antiguos de tipo_comida (si existen)
ALTER TABLE comidas DROP CONSTRAINT IF EXISTS comidas_tipo_comida_check;
ALTER TABLE plan_dias DROP CONSTRAINT IF EXISTS plan_dias_tipo_comida_check;

-- 8. Agregar foreign keys (después de migrar datos)
-- Nota: Ejecutar solo si la migración de datos fue exitosa

-- Para comidas
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints 
        WHERE constraint_name = 'fk_comidas_tipo_comida' 
        AND table_name = 'comidas'
    ) THEN
        ALTER TABLE comidas 
        ADD CONSTRAINT fk_comidas_tipo_comida 
        FOREIGN KEY (id_tipo_comida) 
        REFERENCES tipos_comida(id);
    END IF;
END $$;

-- Para plan_dias
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints 
        WHERE constraint_name = 'fk_plan_dias_tipo_comida' 
        AND table_name = 'plan_dias'
    ) THEN
        ALTER TABLE plan_dias 
        ADD CONSTRAINT fk_plan_dias_tipo_comida 
        FOREIGN KEY (id_tipo_comida) 
        REFERENCES tipos_comida(id);
    END IF;
END $$;

-- 9. Eliminar columnas tipo_comida antiguas (VARCHAR/ENUM)
-- Eliminar índice viejo de comidas si existe
DROP INDEX IF EXISTS idx_comidas_tipo;

-- Eliminar columnas VARCHAR antiguas
ALTER TABLE comidas DROP COLUMN IF EXISTS tipo_comida;
ALTER TABLE plan_dias DROP COLUMN IF EXISTS tipo_comida;

-- ============================================================================
-- VERIFICACIÓN
-- ============================================================================
-- Ejecutar estas consultas para verificar la migración:

-- SELECT * FROM tipos_comida ORDER BY orden_visualizacion;
-- SELECT id, nombre, id_tipo_comida FROM comidas WHERE id_tipo_comida IS NOT NULL LIMIT 5;
-- SELECT id, numero_dia, id_tipo_comida FROM plan_dias WHERE id_tipo_comida IS NOT NULL LIMIT 5;

-- ============================================================================
-- ROLLBACK (si es necesario revertir)
-- ============================================================================
-- ALTER TABLE comidas DROP CONSTRAINT IF EXISTS fk_comidas_tipo_comida;
-- ALTER TABLE plan_dias DROP CONSTRAINT IF EXISTS fk_plan_dias_tipo_comida;
-- ALTER TABLE comidas DROP COLUMN IF EXISTS id_tipo_comida;
-- ALTER TABLE plan_dias DROP COLUMN IF EXISTS id_tipo_comida;
-- DROP TABLE IF EXISTS tipos_comida;
