-- ============================================================================
-- SCRIPT DE DATOS DE DEMOSTRACIÓN - NutriTrack API
-- Módulos 1 y 2: Autenticación y Gestión de Perfil
-- ============================================================================

-- Nota: El admin (ID=1) y demo (ID=2) ya están creados por la aplicación
-- Este script solo agrega datos de salud y mediciones

-- ============================================================================
-- PERFIL DE SALUD - ADMIN (Módulo 2 - US-04)
-- ============================================================================
-- Usuario: admin@nutritrack.com
-- Objetivo: Mantener forma física
-- Nivel de actividad: Alto (hace ejercicio 5-6 días/semana)
-- ============================================================================

INSERT INTO usuario_perfil_salud (id_perfil, objetivo_actual, nivel_actividad_actual, fecha_actualizacion) 
VALUES (1, 'MANTENER_FORMA', 'ALTO', NOW())
ON CONFLICT (id_perfil) DO UPDATE 
SET objetivo_actual = 'MANTENER_FORMA', 
    nivel_actividad_actual = 'ALTO',
    fecha_actualizacion = NOW();

-- ============================================================================
-- HISTORIAL DE MEDIDAS - ADMIN (Módulo 2 - US-06)
-- ============================================================================
-- Progresión de peso estable (mantenimiento)
-- Altura: 175 cm
-- Peso inicial: 70 kg → Peso actual: 70.5 kg (mantenimiento exitoso)
-- ============================================================================

DELETE FROM usuario_historial_medidas WHERE id_cliente = 1;

INSERT INTO usuario_historial_medidas (id_cliente, fecha_medicion, peso, altura) VALUES
-- Septiembre 2025 - inicio de seguimiento
(1, '2025-09-01', 70.0, 175),
(1, '2025-09-08', 70.2, 175),
(1, '2025-09-15', 69.8, 175),
(1, '2025-09-22', 70.1, 175),
(1, '2025-09-29', 70.0, 175),

-- Octubre 2025 - continuación
(1, '2025-10-06', 70.3, 175),
(1, '2025-10-13', 70.0, 175),
(1, '2025-10-20', 70.2, 175),
(1, '2025-10-27', 70.4, 175),

-- Noviembre 2025 - hasta la fecha actual
(1, '2025-11-03', 70.5, 175),
(1, '2025-11-04', 70.5, 175);

-- ============================================================================
-- PERFIL DE SALUD - DEMO (Módulo 2 - US-04)
-- ============================================================================
-- Usuario: demo@nutritrack.com
-- Objetivo: Perder peso
-- Nivel de actividad: Moderado (hace ejercicio 3-4 días/semana)
-- ============================================================================

INSERT INTO usuario_perfil_salud (id_perfil, objetivo_actual, nivel_actividad_actual, fecha_actualizacion) 
VALUES (2, 'PERDER_PESO', 'MODERADO', NOW())
ON CONFLICT (id_perfil) DO UPDATE 
SET objetivo_actual = 'PERDER_PESO', 
    nivel_actividad_actual = 'MODERADO',
    fecha_actualizacion = NOW();

-- ============================================================================
-- HISTORIAL DE MEDIDAS - DEMO (Módulo 2 - US-06)
-- ============================================================================
-- Progresión de pérdida de peso exitosa
-- Altura: 168 cm
-- Peso inicial: 78 kg → Peso actual: 72.5 kg (pérdida de 5.5 kg)
-- ============================================================================

DELETE FROM usuario_historial_medidas WHERE id_cliente = 2;

INSERT INTO usuario_historial_medidas (id_cliente, fecha_medicion, peso, altura) VALUES
-- Septiembre 2025 - inicio del programa
(2, '2025-09-01', 78.0, 168),
(2, '2025-09-08', 77.5, 168),
(2, '2025-09-15', 77.0, 168),
(2, '2025-09-22', 76.8, 168),
(2, '2025-09-29', 76.2, 168),

-- Octubre 2025 - progreso continuo
(2, '2025-10-05', 75.5, 168),
(2, '2025-10-12', 75.0, 168),
(2, '2025-10-19', 74.5, 168),
(2, '2025-10-26', 73.8, 168),

-- Noviembre 2025 - acercándose a la meta
(2, '2025-11-02', 73.0, 168),
(2, '2025-11-04', 72.5, 168);

-- ============================================================================
-- VERIFICACIÓN DE DATOS
-- ============================================================================
-- Ejecutar estas consultas para confirmar que los datos están correctos
-- ============================================================================

-- Verificar perfiles de salud
SELECT 
    ca.email,
    pu.nombre,
    pu.apellido,
    ups.objetivo_actual,
    ups.nivel_actividad_actual,
    ups.fecha_actualizacion
FROM cuentas_auth ca
JOIN perfiles_usuario pu ON ca.id = pu.id_usuario
JOIN usuario_perfil_salud ups ON pu.id = ups.id_perfil
ORDER BY ca.id;

-- Verificar cantidad de mediciones por usuario
SELECT 
    ca.email,
    COUNT(uhm.id) as total_mediciones,
    MIN(uhm.fecha_medicion) as primera_medicion,
    MAX(uhm.fecha_medicion) as ultima_medicion,
    MIN(uhm.peso) as peso_inicial,
    MAX(uhm.peso) as peso_final
FROM cuentas_auth ca
JOIN perfiles_usuario pu ON ca.id = pu.id_usuario
LEFT JOIN usuario_historial_medidas uhm ON pu.id = uhm.id_cliente
GROUP BY ca.email
ORDER BY ca.id;

-- Verificar últimas 5 mediciones de cada usuario
SELECT 
    ca.email,
    uhm.fecha_medicion,
    uhm.peso,
    uhm.altura
FROM cuentas_auth ca
JOIN perfiles_usuario pu ON ca.id = pu.id_usuario
JOIN usuario_historial_medidas uhm ON pu.id = uhm.id_cliente
ORDER BY ca.id, uhm.fecha_medicion DESC;

-- ============================================================================
-- RESUMEN DE USUARIOS DE PRUEBA
-- ============================================================================
/*
┌─────────────────────────────────────────────────────────────────────────┐
│ USUARIO ADMIN (Administrador del Sistema)                              │
├─────────────────────────────────────────────────────────────────────────┤
│ Email:        admin@nutritrack.com                                     │
│ Password:     Admin123!                                                │
│ Role:         ROLE_ADMIN                                               │
│ Objetivo:     Mantener forma física                                    │
│ Actividad:    Alto (5-6 días/semana)                                   │
│ Peso inicial: 70.0 kg → Actual: 70.5 kg                               │
│ Altura:       175 cm                                                   │
│ IMC:          23.0 (Peso normal)                                       │
│ Mediciones:   11 registros (Sep-Nov 2025)                             │
└─────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────┐
│ USUARIO DEMO (Usuario Regular)                                         │
├─────────────────────────────────────────────────────────────────────────┤
│ Email:        demo@nutritrack.com                                      │
│ Password:     Demo123!                                                 │
│ Role:         ROLE_USER                                                │
│ Objetivo:     Perder peso                                              │
│ Actividad:    Moderado (3-4 días/semana)                              │
│ Peso inicial: 78.0 kg → Actual: 72.5 kg (-5.5 kg) 🎯                │
│ Altura:       168 cm                                                   │
│ IMC inicial:  27.6 (Sobrepeso) → Actual: 25.7 (Sobrepeso leve)       │
│ Mediciones:   11 registros (Sep-Nov 2025)                             │
└─────────────────────────────────────────────────────────────────────────┘

FUNCIONALIDADES DISPONIBLES PARA PRUEBAS:

✅ Módulo 1 - Autenticación y Seguridad
   • POST /api/v1/auth/register   - Registro de nuevos usuarios
   • POST /api/v1/auth/login      - Inicio de sesión con JWT
   • DELETE /api/v1/auth/account  - Eliminación de cuenta (requiere "ELIMINAR")

✅ Módulo 2 - Gestión de Perfil y Salud
   • GET    /api/v1/perfil/salud      - Obtener perfil de salud actual
   • POST   /api/v1/perfil/salud      - Crear perfil por primera vez
   • PUT    /api/v1/perfil/salud      - Actualizar perfil de salud
   • GET    /api/v1/perfil/mediciones - Obtener historial de mediciones
   • POST   /api/v1/perfil/mediciones - Registrar nueva medición
   • DELETE /api/v1/perfil/mediciones/{id} - Eliminar medición

NOTAS IMPORTANTES:
• Ambos usuarios tienen datos completos para demostrar todas las funcionalidades
• El usuario admin demuestra mantenimiento de peso estable
• El usuario demo demuestra pérdida de peso progresiva exitosa
• Todos los endpoints requieren autenticación JWT (excepto register y login)
• Las mediciones muestran progresión realista a lo largo de 2 meses
*/
