# ============================================================================
# Script para cargar CATÁLOGO a Base de Datos de Render (PostgreSQL)
# Solo carga ingredientes, comidas, ejercicios (NO usuarios - ya existen)
# ============================================================================

param(
    [switch]$SkipConfirmation
)

# ============================================================================
# CONFIGURACIÓN DE BASE DE DATOS RENDER
# ============================================================================
$DB_HOST = "dpg-d45r1fc9c44c73c9vvug-a.oregon-postgres.render.com"
$DB_PORT = "5432"
$DB_NAME = "nutritrack_db_vfbh"
$DB_USER = "nutritrack_db_vfbh_user"
$DB_PASSWORD = "WUYtwFHOgDJ1CoD93hiTbmVMm40puYLA"

Write-Host "=========================================" -ForegroundColor Cyan
Write-Host "  CARGA DE CATÁLOGO A RENDER" -ForegroundColor Cyan
Write-Host "=========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "📦 Base de Datos:" -ForegroundColor Yellow
Write-Host "   Host: $DB_HOST" -ForegroundColor Gray
Write-Host "   Database: $DB_NAME" -ForegroundColor Gray
Write-Host "   User: $DB_USER" -ForegroundColor Gray
Write-Host ""

# ============================================================================
# VERIFICACIÓN: ¿psql instalado?
# ============================================================================
Write-Host "🔍 Verificando psql..." -ForegroundColor Yellow
$psqlPath = Get-Command psql -ErrorAction SilentlyContinue

if (-not $psqlPath) {
    Write-Host "❌ ERROR: psql no encontrado" -ForegroundColor Red
    Write-Host ""
    Write-Host "Instala PostgreSQL client:" -ForegroundColor Yellow
    Write-Host "  winget install PostgreSQL.PostgreSQL" -ForegroundColor Cyan
    Write-Host "  O descarga de: https://www.postgresql.org/download/windows/" -ForegroundColor Cyan
    exit 1
}

Write-Host "✅ psql encontrado: $($psqlPath.Source)" -ForegroundColor Green
Write-Host ""

# ============================================================================
# VERIFICAR CONEXIÓN
# ============================================================================
Write-Host "🔌 Probando conexión a Render..." -ForegroundColor Yellow

$env:PGPASSWORD = $DB_PASSWORD
$testQuery = "SELECT version();"

$result = psql -h $DB_HOST -U $DB_USER -d $DB_NAME -p $DB_PORT -t -c $testQuery 2>&1

if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ ERROR: No se pudo conectar a la base de datos" -ForegroundColor Red
    Write-Host "Detalles: $result" -ForegroundColor Gray
    exit 1
}

Write-Host "✅ Conexión exitosa" -ForegroundColor Green
Write-Host "📊 PostgreSQL version:" -ForegroundColor Gray
Write-Host "   $($result.Trim())" -ForegroundColor White
Write-Host ""

# ============================================================================
# VERIFICAR ESTADO ACTUAL DE LAS TABLAS
# ============================================================================
Write-Host "📊 Verificando estado actual de las tablas..." -ForegroundColor Yellow

$checkQuery = @"
SELECT 
    (SELECT COUNT(*) FROM ingredientes) as ingredientes,
    (SELECT COUNT(*) FROM comidas) as comidas,
    (SELECT COUNT(*) FROM ejercicios) as ejercicios,
    (SELECT COUNT(*) FROM comida_ingredientes) as recetas,
    (SELECT COUNT(*) FROM cuentas_auth) as usuarios;
"@

$counts = psql -h $DB_HOST -U $DB_USER -d $DB_NAME -p $DB_PORT -t -c $checkQuery

Write-Host "Estado actual:" -ForegroundColor Cyan
Write-Host "   $counts" -ForegroundColor White
Write-Host ""

# ============================================================================
# CONFIRMACIÓN DEL USUARIO
# ============================================================================
if (-not $SkipConfirmation) {
    Write-Host "⚠️  ADVERTENCIA:" -ForegroundColor Yellow
    Write-Host "   - Se cargarán ingredientes, comidas y ejercicios" -ForegroundColor Gray
    Write-Host "   - NO se modificarán los usuarios (admin y demo ya existen)" -ForegroundColor Gray
    Write-Host "   - Los datos duplicados serán ignorados (ON CONFLICT DO NOTHING)" -ForegroundColor Gray
    Write-Host ""
    
    $confirmation = Read-Host "¿Continuar con la carga? (si/no)"
    
    if ($confirmation -ne "si") {
        Write-Host "❌ Operación cancelada" -ForegroundColor Red
        exit 0
    }
}

Write-Host ""
Write-Host "=========================================" -ForegroundColor Cyan
Write-Host "  INICIANDO CARGA DE DATOS" -ForegroundColor Cyan
Write-Host "=========================================" -ForegroundColor Cyan
Write-Host ""

# ============================================================================
# CARGA DE CATÁLOGO BÁSICO
# ============================================================================
Write-Host "📦 1. Cargando catálogo básico (ingredientes, comidas, ejercicios)..." -ForegroundColor Yellow

$catalogPath = Join-Path $PSScriptRoot "SQL\catalogo_basico.sql"

if (-not (Test-Path $catalogPath)) {
    Write-Host "❌ ERROR: No se encontró $catalogPath" -ForegroundColor Red
    exit 1
}

$result = psql -h $DB_HOST -U $DB_USER -d $DB_NAME -p $DB_PORT -f $catalogPath 2>&1

if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ ERROR al cargar catálogo básico" -ForegroundColor Red
    Write-Host "Detalles: $result" -ForegroundColor Gray
    exit 1
}

Write-Host "✅ Catálogo básico cargado" -ForegroundColor Green
Write-Host ""

# ============================================================================
# CREAR SCRIPT TEMPORAL SIN DATOS DE USUARIOS
# ============================================================================
Write-Host "📦 2. Preparando datos de demostración (sin usuarios)..." -ForegroundColor Yellow

$originalDataDemo = Join-Path $PSScriptRoot "SQL\data_demo.sql"
$tempDataDemo = Join-Path $env:TEMP "data_demo_catalog_only.sql"

# Leer el archivo original y filtrar solo las secciones que NO son de usuarios
$content = Get-Content $originalDataDemo -Raw

# Crear versión filtrada que SOLO incluye perfiles y mediciones
# (Los usuarios ya fueron creados por StartupService en la app)
$filteredContent = @"
-- ============================================================================
-- DATOS DE DEMOSTRACIÓN - SOLO PERFILES Y MEDICIONES
-- Los usuarios admin (ID=1) y demo (ID=2) ya fueron creados por la aplicación
-- ============================================================================

-- ============================================================================
-- PERFIL DE SALUD - ADMIN (Usuario ID=1)
-- ============================================================================
INSERT INTO usuario_perfil_salud (id_perfil, objetivo_actual, nivel_actividad_actual, fecha_actualizacion) 
VALUES (1, 'MANTENER_FORMA', 'ALTO', '2025-11-05')
ON CONFLICT (id_perfil) DO UPDATE 
SET objetivo_actual = 'MANTENER_FORMA', 
    nivel_actividad_actual = 'ALTO',
    fecha_actualizacion = '2025-11-05';

-- ============================================================================
-- HISTORIAL DE MEDIDAS - ADMIN
-- ============================================================================
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
(1, '2025-11-04', 70.5, 175)
ON CONFLICT (id_cliente, fecha_medicion) DO NOTHING;

-- ============================================================================
-- PERFIL DE SALUD - DEMO (Usuario ID=2)
-- ============================================================================
INSERT INTO usuario_perfil_salud (id_perfil, objetivo_actual, nivel_actividad_actual, fecha_actualizacion) 
VALUES (2, 'PERDER_PESO', 'MODERADO', '2025-11-05')
ON CONFLICT (id_perfil) DO UPDATE 
SET objetivo_actual = 'PERDER_PESO', 
    nivel_actividad_actual = 'MODERADO',
    fecha_actualizacion = '2025-11-05';

-- ============================================================================
-- HISTORIAL DE MEDIDAS - DEMO
-- ============================================================================
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
(2, '2025-11-04', 72.5, 168)
ON CONFLICT (id_cliente, fecha_medicion) DO NOTHING;

-- ============================================================================
-- VERIFICACIÓN FINAL
-- ============================================================================
SELECT 
    'Datos de Demostración Cargados' as mensaje,
    (SELECT COUNT(*) FROM usuario_perfil_salud) as perfiles_salud,
    (SELECT COUNT(*) FROM usuario_historial_medidas) as mediciones;
"@

$filteredContent | Out-File -FilePath $tempDataDemo -Encoding UTF8

Write-Host "✅ Script temporal creado: $tempDataDemo" -ForegroundColor Green
Write-Host ""

# ============================================================================
# CARGA DE DATOS DE DEMOSTRACIÓN (SOLO PERFILES Y MEDICIONES)
# ============================================================================
Write-Host "📦 3. Cargando perfiles y mediciones de usuarios..." -ForegroundColor Yellow

$result = psql -h $DB_HOST -U $DB_USER -d $DB_NAME -p $DB_PORT -f $tempDataDemo 2>&1

if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ ERROR al cargar datos de demostración" -ForegroundColor Red
    Write-Host "Detalles: $result" -ForegroundColor Gray
    exit 1
}

Write-Host "✅ Perfiles y mediciones cargadas" -ForegroundColor Green
Write-Host ""

# Limpiar archivo temporal
Remove-Item $tempDataDemo -ErrorAction SilentlyContinue

# ============================================================================
# VERIFICACIÓN FINAL
# ============================================================================
Write-Host "=========================================" -ForegroundColor Cyan
Write-Host "  VERIFICACIÓN FINAL" -ForegroundColor Cyan
Write-Host "=========================================" -ForegroundColor Cyan
Write-Host ""

$finalQuery = @"
SELECT 
    '📦 Usuarios' as tipo, COUNT(*) as cantidad FROM cuentas_auth
UNION ALL
SELECT '🥗 Ingredientes', COUNT(*) FROM ingredientes
UNION ALL
SELECT '🍽️  Comidas', COUNT(*) FROM comidas
UNION ALL
SELECT '📋 Recetas', COUNT(*) FROM comida_ingredientes
UNION ALL
SELECT '🏃 Ejercicios', COUNT(*) FROM ejercicios
UNION ALL
SELECT '❤️  Perfiles Salud', COUNT(*) FROM usuario_perfil_salud
UNION ALL
SELECT '📊 Mediciones', COUNT(*) FROM usuario_historial_medidas;
"@

Write-Host "Estado final de la base de datos:" -ForegroundColor Yellow
Write-Host ""

$finalCounts = psql -h $DB_HOST -U $DB_USER -d $DB_NAME -p $DB_PORT -t -A -F ' | ' -c $finalQuery

$finalCounts -split "`n" | ForEach-Object {
    if ($_ -match '(.+) \| (\d+)') {
        $tipo = $matches[1]
        $cantidad = $matches[2]
        Write-Host "   $tipo : $cantidad" -ForegroundColor White
    }
}

Write-Host ""
Write-Host "=========================================" -ForegroundColor Cyan
Write-Host "  ✅ CARGA COMPLETADA EXITOSAMENTE" -ForegroundColor Green
Write-Host "=========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "🎯 Próximos pasos:" -ForegroundColor Yellow
Write-Host "   1. Verifica Swagger: https://nutritrack-api-wt8b.onrender.com/swagger-ui.html" -ForegroundColor Gray
Write-Host "   2. Login con admin@nutritrack.com / Admin123!" -ForegroundColor Gray
Write-Host "   3. O login con demo@nutritrack.com / Demo123!" -ForegroundColor Gray
Write-Host "   4. Prueba endpoints en Postman con el entorno de Render" -ForegroundColor Gray
Write-Host ""

# Limpiar variable de entorno de password
$env:PGPASSWORD = $null
