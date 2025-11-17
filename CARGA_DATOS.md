# 📊 Sistema de Carga de Datos - NutriTrack API

## 🎯 Resumen

El proyecto tiene **carga automática de datos inicial** al arrancar. Esto facilita desarrollo y pruebas sin configuración manual.

## 🔄 Cómo Funciona

### 1. **StartupService** (Java - Módulo 1)
Ejecuta al iniciar la aplicación (`CommandLineRunner`):
- ✅ Crea roles (`ROLE_USER`, `ROLE_ADMIN`)
- ✅ Crea usuario **admin@nutritrack.com** / **Admin123!**
- ✅ Crea usuario **demo@nutritrack.com** / **Demo123!**
- ✅ Crea perfiles de salud básicos
- ✅ Carga 11 mediciones históricas para cada usuario

**Ubicación:** `src/main/java/com/example/nutritrackapi/service/StartupService.java`

### 2. **data.sql** (SQL - Módulos 2-5)
Carga datos del catálogo y asignaciones:
- ✅ **Módulo 2:** Etiquetas, ingredientes, comidas, ejercicios
- ✅ **Módulo 3:** Planes nutricionales (4) y rutinas (6)
- ✅ **Módulo 4:** Asignaciones de planes y rutinas a usuarios
- ✅ **Módulo 5:** Registros de comidas y ejercicios (últimos 5 días)

**Ubicación:** `src/main/resources/data.sql`  
**Fuente original:** `SQL/CARGA_COMPLETA_RENDER.sql`

## 📋 Tablas con Modelos JPA Mapeados

### ✅ Módulo 1: Autenticación
| Tabla | Modelo Java | Descripción |
|-------|-------------|-------------|
| `roles` | `Role` | Roles del sistema |
| `cuentas_auth` | `CuentaAuth` | Credenciales de acceso |
| `perfiles_usuario` | `PerfilUsuario` | Datos personales |
| `usuario_perfil_salud` | `UsuarioPerfilSalud` | Objetivo y nivel actividad |
| `usuario_historial_medidas` | `UsuarioHistorialMedidas` | Peso, altura, IMC |
| `usuario_etiquetas_salud` | `UsuarioEtiquetasSalud` | Alergias y condiciones |

### ✅ Módulo 2: Catálogo
| Tabla | Modelo Java | Descripción |
|-------|-------------|-------------|
| `etiquetas` | `Etiqueta` | Tags para clasificación |
| `ingredientes` | `Ingrediente` | Alimentos base |
| `ingrediente_etiquetas` | - | Relación many-to-many |
| `comidas` | `Comida` | Recetas y comidas |
| `comida_ingredientes` | `ComidaIngrediente` | Composición de comidas |
| `ejercicios` | `Ejercicio` | Catálogo de ejercicios |
| `ejercicio_etiquetas` | - | Relación many-to-many |

### ✅ Módulo 3: Planes y Rutinas
| Tabla | Modelo Java | Descripción |
|-------|-------------|-------------|
| `planes` | `Plan` | Planes nutricionales |
| `plan_objetivos` | `PlanObjetivo` | Objetivos del plan |
| `plan_dias` | `PlanDia` | Comidas por día |
| `rutinas` | `Rutina` | Rutinas de ejercicio |
| `rutina_ejercicios` | `RutinaEjercicio` | Ejercicios por semana |

### ✅ Módulo 4: Asignaciones
| Tabla | Modelo Java | Descripción |
|-------|-------------|-------------|
| `usuarios_planes` | `UsuarioPlan` | Planes asignados |
| `usuarios_rutinas` | `UsuarioRutina` | Rutinas asignadas |

### ✅ Módulo 5: Tracking
| Tabla | Modelo Java | Descripción |
|-------|-------------|-------------|
| `registros_comidas` | `RegistroComida` | Comidas realizadas |
| `registros_ejercicios` | `RegistroEjercicio` | Ejercicios completados |

## ⚙️ Configuración Actual

**Archivo:** `src/main/resources/application.properties`

```properties
# Hibernate actualiza el esquema SIN borrar datos
spring.jpa.hibernate.ddl-auto=update

# data.sql deshabilitado para evitar duplicados en cada reinicio
spring.sql.init.mode=never
spring.jpa.defer-datasource-initialization=true
```

**¿Por qué `update` y no `create`?**
- ✅ `update`: Preserva datos ingresados vía API, solo actualiza esquema si cambian los modelos
- ❌ `create`: Borra TODO cada vez que arrancas (pierdes datos de prueba, registros, etc.)
- ❌ `create-drop`: Peor aún, borra al cerrar la app

## 🚀 Uso

### Primera Vez (Setup Inicial)
```powershell
# 1. Levantar base de datos
docker-compose up -d

# 2. Iniciar aplicación (crea esquema y usuarios)
.\mvnw.cmd spring-boot:run
# Espera a ver: ✅ Aplicación lista!

# 3. Cargar catálogo manualmente (SOLO PRIMERA VEZ)
# PowerShell (Windows):
Get-Content SQL\CARGA_COMPLETA_RENDER.sql | docker exec -i nutritrack-postgres psql -U postgres -d nutritrack_db

# Bash/WSL (Linux/Mac):
# cat SQL/CARGA_COMPLETA_RENDER.sql | docker exec -i nutritrack-postgres psql -U postgres -d nutritrack_db
```

### Arranques Posteriores
```bash
# Solo levantar DB y app (datos persisten)
docker-compose up -d
.\mvnw.cmd spring-boot:run
```

La aplicación:
1. Hibernate actualiza tablas si cambiaron modelos JPA (`update`)
2. `StartupService` verifica usuarios y perfiles (NO duplica)
3. Los datos del catálogo y registros **persisten** entre reinicios

### Carga Manual (Opcional)
Si necesitas recargar solo datos:

```bash
# Conectar a la base de datos
psql -U postgres -d nutritrack_db

# Cargar datos
\i SQL/CARGA_COMPLETA_RENDER.sql
```

## 📊 Datos Incluidos

### Usuarios Iniciales
| Usuario | Email | Password | Rol | Objetivo |
|---------|-------|----------|-----|----------|
| Admin | `admin@nutritrack.com` | `Admin123!` | ROLE_ADMIN | Mantener forma |
| Demo | `demo@nutritrack.com` | `Demo123!` | ROLE_USER | Perder peso |

### Catálogo (Módulo 2)
- **Etiquetas:** 17 (alergias, dietas, objetivos)
- **Ingredientes:** ~40 (proteínas, carbohidratos, grasas, frutas)
- **Comidas:** ~30 (desayunos, almuerzos, cenas, snacks)
- **Ejercicios:** ~25 (cardio, fuerza, flexibilidad, HIIT)

### Planes y Rutinas (Módulo 3)
- **Planes:** 4 (Pérdida grasa, Ganancia muscular, Definición, Mantenimiento)
- **Rutinas:** 6 (HIIT, Fuerza superior/inferior, Cardio, Core, Movilidad)

### Asignaciones (Módulo 4)
Usuario demo tiene:
- 4 planes asignados (1 ACTIVO, 1 COMPLETADO, 1 PAUSADO, 1 CANCELADO)
- 5 rutinas asignadas (2 ACTIVAS, 1 COMPLETADA, 1 PAUSADA, 1 CANCELADA)

### Registros (Módulo 5)
Usuario demo tiene registros de:
- **Comidas:** 13 registros (últimos 5 días)
- **Ejercicios:** 11 registros (últimos 5 días)

## 🔍 Verificación

Después de arrancar, verifica los datos:

```bash
# Ver logs de carga
# Busca líneas como:
# ✅ Roles creados
# ✅ Usuario administrador creado
# ✅ Usuario demo creado
# ✅ Datos de demostración cargados

# O consulta en PostgreSQL
docker exec -it nutritrack-db psql -U postgres -d nutritrack_db -c "SELECT COUNT(*) FROM ingredientes;"
docker exec -it nutritrack-db psql -U postgres -d nutritrack_db -c "SELECT COUNT(*) FROM planes;"
```

## 🧹 Limpiar Base de Datos

Si necesitas resetear todo:

```bash
# Opción 1: Recrear contenedor Docker
docker-compose down -v
docker-compose up -d
.\mvnw.cmd spring-boot:run

# Opción 2: Drop manual (mantiene contenedor)
docker exec -it nutritrack-db psql -U postgres -d nutritrack_db -c "DROP SCHEMA public CASCADE; CREATE SCHEMA public;"
.\mvnw.cmd spring-boot:run
```

## 📝 Mantener data.sql Actualizado

Si modificas `SQL/CARGA_COMPLETA_RENDER.sql`, sincroniza:

```powershell
# Copiar cambios a data.sql
Copy-Item "SQL\CARGA_COMPLETA_RENDER.sql" -Destination "src\main\resources\data.sql" -Force

# Limpiar comandos \echo de psql
$content = Get-Content "src\main\resources\data.sql" -Raw
$content = $content -replace "\\\\echo[^\r\n]*[\r\n]+", ""
$content | Set-Content "src\main\resources\data.sql" -NoNewline
```

## 🔒 Producción

En producción (`application-production.properties`):

```properties
# NO recrear esquema en producción
spring.jpa.hibernate.ddl-auto=none

# NO cargar data.sql en producción
spring.sql.init.mode=never
```

Usa migraciones con Flyway o Liquibase para cambios de esquema.

## 📧 Soporte

Para problemas con la carga de datos:
1. Revisa logs de `StartupService`
2. Verifica que Docker esté corriendo
3. Confirma que `spring.jpa.hibernate.ddl-auto=create`
4. Si persiste, ejecuta carga manual desde `SQL/`

---

**Última actualización:** Noviembre 17, 2025  
**Mantenedor:** Equipo NutriTrack
