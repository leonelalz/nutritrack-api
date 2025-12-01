-- Script para eliminar constraints problemáticas en PostgreSQL
-- Ejecutar en la base de datos local (Docker) o Render

-- 1. Eliminar constraints de usuarios_planes y usuarios_rutinas
ALTER TABLE usuarios_planes DROP CONSTRAINT IF EXISTS uk_usuario_plan_estado;
ALTER TABLE usuarios_rutinas DROP CONSTRAINT IF EXISTS uk_usuario_rutina_estado;

-- 2. Eliminar constraints de unicidad en rutina_ejercicios (si existen)
-- Esto permite editar rutinas sin conflictos
ALTER TABLE rutina_ejercicios DROP CONSTRAINT IF EXISTS uk_rutina_ejercicio_orden;
ALTER TABLE rutina_ejercicios DROP CONSTRAINT IF EXISTS rutina_ejercicios_unique;

-- 3. Eliminar constraints de unicidad en plan_dias (si existen)
-- Esto permite editar planes sin conflictos
ALTER TABLE plan_dias DROP CONSTRAINT IF EXISTS uk_plan_dia_tipo;
ALTER TABLE plan_dias DROP CONSTRAINT IF EXISTS plan_dias_unique;

-- Verificar que se eliminaron
SELECT conname FROM pg_constraint WHERE conname LIKE 'uk_%' OR conname LIKE '%_unique';
