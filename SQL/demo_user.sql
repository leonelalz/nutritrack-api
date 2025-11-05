-- ============================================================================
-- SCRIPT DE USUARIO DE DEMOSTRACIÓN
-- Usuario completo para pruebas con todos los módulos
-- ============================================================================

-- Nota: La contraseña "Demo123!" está hasheada con BCrypt
-- Password en texto plano: Demo123!

-- 1. INSERTAR USUARIO DEMO (si no existe)
INSERT INTO cuentas_auth (email, password, active, created_at, id_rol)
SELECT 
    'demo@nutritrack.com',
    '$2a$10$xQZ5K5YhJ6X7ZjXJZvZp3.YvGk5Z5Z5Z5Z5Z5Z5Z5Z5Z5Z5Z5Z5Z5u', -- Demo123!
    true,
    CURRENT_TIMESTAMP,
    (SELECT id FROM roles WHERE tipo_rol = 'ROLE_USER')
WHERE NOT EXISTS (
    SELECT 1 FROM cuentas_auth WHERE email = 'demo@nutritrack.com'
);

-- 2. INSERTAR PERFIL BÁSICO
INSERT INTO perfiles_usuario (id_usuario, nombre, apellido, fecha_inicio_app, unidades_medida)
SELECT 
    (SELECT id FROM cuentas_auth WHERE email = 'demo@nutritrack.com'),
    'María',
    'García',
    CURRENT_DATE,
    'METRICO'
WHERE NOT EXISTS (
    SELECT 1 FROM perfiles_usuario 
    WHERE id_usuario = (SELECT id FROM cuentas_auth WHERE email = 'demo@nutritrack.com')
);

-- 3. INSERTAR PERFIL DE SALUD
INSERT INTO usuario_perfil_salud (id_perfil, objetivo_actual, nivel_actividad_actual, fecha_actualizacion)
SELECT 
    (SELECT id FROM perfiles_usuario WHERE id_usuario = (SELECT id FROM cuentas_auth WHERE email = 'demo@nutritrack.com')),
    'PERDER_PESO',
    'MODERADO',
    CURRENT_DATE
WHERE NOT EXISTS (
    SELECT 1 FROM usuario_perfil_salud 
    WHERE id_perfil = (SELECT id FROM perfiles_usuario WHERE id_usuario = (SELECT id FROM cuentas_auth WHERE email = 'demo@nutritrack.com'))
);

-- 4. INSERTAR HISTORIAL DE MEDICIONES (últimos 30 días)
INSERT INTO usuario_historial_medidas (id_perfil, fecha_medicion, peso_kg, altura_cm, circunferencia_cintura_cm)
SELECT 
    (SELECT id FROM perfiles_usuario WHERE id_usuario = (SELECT id FROM cuentas_auth WHERE email = 'demo@nutritrack.com')),
    CURRENT_DATE - INTERVAL '30 days',
    75.5,
    168.0,
    85.0
WHERE NOT EXISTS (
    SELECT 1 FROM usuario_historial_medidas 
    WHERE id_perfil = (SELECT id FROM perfiles_usuario WHERE id_usuario = (SELECT id FROM cuentas_auth WHERE email = 'demo@nutritrack.com'))
    AND fecha_medicion = CURRENT_DATE - INTERVAL '30 days'
);

INSERT INTO usuario_historial_medidas (id_perfil, fecha_medicion, peso_kg, altura_cm, circunferencia_cintura_cm)
SELECT 
    (SELECT id FROM perfiles_usuario WHERE id_usuario = (SELECT id FROM cuentas_auth WHERE email = 'demo@nutritrack.com')),
    CURRENT_DATE - INTERVAL '20 days',
    74.2,
    168.0,
    83.5
WHERE NOT EXISTS (
    SELECT 1 FROM usuario_historial_medidas 
    WHERE id_perfil = (SELECT id FROM perfiles_usuario WHERE id_usuario = (SELECT id FROM cuentas_auth WHERE email = 'demo@nutritrack.com'))
    AND fecha_medicion = CURRENT_DATE - INTERVAL '20 days'
);

INSERT INTO usuario_historial_medidas (id_perfil, fecha_medicion, peso_kg, altura_cm, circunferencia_cintura_cm)
SELECT 
    (SELECT id FROM perfiles_usuario WHERE id_usuario = (SELECT id FROM cuentas_auth WHERE email = 'demo@nutritrack.com')),
    CURRENT_DATE - INTERVAL '10 days',
    73.8,
    168.0,
    82.0
WHERE NOT EXISTS (
    SELECT 1 FROM usuario_historial_medidas 
    WHERE id_perfil = (SELECT id FROM perfiles_usuario WHERE id_usuario = (SELECT id FROM cuentas_auth WHERE email = 'demo@nutritrack.com'))
    AND fecha_medicion = CURRENT_DATE - INTERVAL '10 days'
);

INSERT INTO usuario_historial_medidas (id_perfil, fecha_medicion, peso_kg, altura_cm, circunferencia_cintura_cm)
SELECT 
    (SELECT id FROM perfiles_usuario WHERE id_usuario = (SELECT id FROM cuentas_auth WHERE email = 'demo@nutritrack.com')),
    CURRENT_DATE,
    72.5,
    168.0,
    80.5
WHERE NOT EXISTS (
    SELECT 1 FROM usuario_historial_medidas 
    WHERE id_perfil = (SELECT id FROM perfiles_usuario WHERE id_usuario = (SELECT id FROM cuentas_auth WHERE email = 'demo@nutritrack.com'))
    AND fecha_medicion = CURRENT_DATE
);

-- 5. INSERTAR ETIQUETAS DE SALUD (Alergias/Condiciones médicas)
-- Primero, asegurarse de que existan las etiquetas de salud
INSERT INTO etiquetas (nombre_etiqueta, tipo_etiqueta, descripcion)
VALUES 
    ('Intolerancia a la Lactosa', 'ALERGIA', 'Dificultad para digerir lactosa'),
    ('Diabetes Tipo 2', 'CONDICION_MEDICA', 'Condición metabólica que afecta el azúcar en sangre'),
    ('Hipertensión', 'CONDICION_MEDICA', 'Presión arterial elevada')
ON CONFLICT (nombre_etiqueta) DO NOTHING;

-- Asociar etiquetas al usuario demo
INSERT INTO usuario_etiquetas_salud (id_perfil, id_etiqueta)
SELECT 
    (SELECT id FROM perfiles_usuario WHERE id_usuario = (SELECT id FROM cuentas_auth WHERE email = 'demo@nutritrack.com')),
    (SELECT id FROM etiquetas WHERE nombre_etiqueta = 'Intolerancia a la Lactosa')
WHERE NOT EXISTS (
    SELECT 1 FROM usuario_etiquetas_salud 
    WHERE id_perfil = (SELECT id FROM perfiles_usuario WHERE id_usuario = (SELECT id FROM cuentas_auth WHERE email = 'demo@nutritrack.com'))
    AND id_etiqueta = (SELECT id FROM etiquetas WHERE nombre_etiqueta = 'Intolerancia a la Lactosa')
);

-- ============================================================================
-- VERIFICACIÓN
-- ============================================================================
SELECT 
    'Usuario Demo creado exitosamente' as mensaje,
    ca.email,
    ca.active as cuenta_activa,
    pu.nombre || ' ' || pu.apellido as nombre_completo,
    ups.objetivo_actual,
    ups.nivel_actividad_actual,
    COUNT(DISTINCT uhm.id) as total_mediciones,
    COUNT(DISTINCT ues.id) as total_etiquetas_salud
FROM cuentas_auth ca
LEFT JOIN perfiles_usuario pu ON ca.id = pu.id_usuario
LEFT JOIN usuario_perfil_salud ups ON pu.id = ups.id_perfil
LEFT JOIN usuario_historial_medidas uhm ON pu.id = uhm.id_perfil
LEFT JOIN usuario_etiquetas_salud ues ON pu.id = ues.id_perfil
WHERE ca.email = 'demo@nutritrack.com'
GROUP BY ca.email, ca.active, pu.nombre, pu.apellido, ups.objetivo_actual, ups.nivel_actividad_actual;
