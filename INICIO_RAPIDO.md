# 🚀 GUÍA DE INICIO RÁPIDO - NUTRITRACK API

Esta guía te ayudará a poner en marcha NutriTrack API en **10 minutos** desde una computadora nueva.

---

## 📋 CHECKLIST INICIAL

Antes de empezar, verifica que tienes instalado:

- [ ] **Java 21** ([Descargar](https://adoptium.net/))
- [ ] **Docker Desktop** ([Descargar](https://www.docker.com/products/docker-desktop))
- [ ] **Git** ([Descargar](https://git-scm.com/downloads))
- [ ] **IntelliJ IDEA** ([Descargar](https://www.jetbrains.com/idea/download/))

### Verificar instalación rápida:

```powershell
# Abrir PowerShell y ejecutar:
java -version        # Debe mostrar "openjdk version 21"
docker --version     # Debe mostrar "Docker version X.X.X"
git --version        # Debe mostrar "git version X.X.X"
```

---

## 🎯 INSTALACIÓN EN 5 PASOS

### PASO 1: Clonar el proyecto

```powershell
# Abrir PowerShell en la carpeta donde quieres el proyecto
cd C:\Users\TuUsuario\Documents

# Clonar repositorio
git clone https://github.com/leonelalz/nutritrack-api.git

# Entrar a la carpeta
cd nutritrack-API
```

### PASO 2: Iniciar base de datos con Docker

```powershell
# Asegúrate de que Docker Desktop esté abierto y corriendo
# Luego ejecuta:
docker-compose up -d

# Verificar que está corriendo
docker ps
# Deberías ver: nutritrack-postgres
```

**¿Qué hace esto?**
- ✅ Descarga PostgreSQL 16.10
- ✅ Crea la base de datos `nutritrack_db`
- ✅ Ejecuta automáticamente el script SQL con 15 tablas
- ✅ Crea usuarios de prueba (admin y demo)

### PASO 3: Abrir proyecto en IntelliJ IDEA

1. **Abrir IntelliJ IDEA**
2. **File → Open**
3. Selecciona la carpeta `nutritrack-API`
4. Click en **"Trust Project"** cuando pregunte
5. **ESPERA** a que IntelliJ descargue las dependencias (⏱️ 3-5 minutos)
   - Observa la barra de progreso en la parte inferior
   - No hagas nada hasta que termine

### PASO 4: Configurar Java 21 en IntelliJ

1. **File → Project Structure** (Ctrl + Alt + Shift + S)
2. En **Project Settings → Project**:
   - **SDK:** Selecciona Java 21 (si no aparece, click en "Add SDK" → "Download JDK")
   - **Language level:** 21
3. Click **OK**

### PASO 5: Ejecutar la aplicación

1. Navega a: `src/main/java/com/example/nutritrackapi/NutritrackApiApplication.java`
2. Click derecho en el archivo → **Run 'NutritrackApiApplication'**
3. O presiona **Shift + F10**

---

## ✅ VERIFICAR QUE FUNCIONA

### Opción 1: Health Check en el navegador

Abre: http://localhost:8080/api/v1/health

**Deberías ver:**
```json
{
  "status": "UP",
  "service": "NutriTrack API",
  "timestamp": "2025-11-04T...",
  "version": "1.0.0",
  "environment": "development"
}
```

### Opción 2: Swagger UI (Documentación Interactiva)

Abre: http://localhost:8080/swagger-ui/index.html

**Deberías ver:**
- Lista completa de todos los endpoints
- Opción para probar cada endpoint
- Esquemas de datos

### Opción 3: PowerShell

```powershell
curl http://localhost:8080/api/v1/health
```

---

## 📝 PASOS PARA EJECUTAR EN INTELLIJ IDEA (Detallado):

## 📝 PASOS PARA EJECUTAR EN INTELLIJ IDEA (Detallado):

### 1. Recargar Maven
- Click derecho en `pom.xml`
- Seleccionar **Maven → Reload Project**
- Espera a que descargue todas las dependencias (⏱️ puede tardar 2-5 minutos)
- **Indicador:** La barra de progreso inferior debe completarse

### 2. Verificar que no hay errores
- Abre `src/main/java/com/example/nutritrackapi/NutritrackApiApplication.java`
- **NO debe haber líneas rojas** en el código
- Si hay errores, ve a **File → Invalidate Caches → Invalidate and Restart**

### 3. Ejecutar la aplicación

**Opción A (Recomendada):**
- Click derecho en `NutritrackApiApplication.java`
- Seleccionar **Run 'NutritrackApiApplication.main()'**

**Opción B:**
- Presiona **Shift + F10**

**Opción C:**
- Click en el botón ▶️ verde junto a la clase `NutritrackApiApplication`

### 4. Verificar que inició correctamente

**En la consola de IntelliJ deberías ver:**

```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::             (v3.5.7)

🚀 Iniciando NutriTrack API...
📝 Creando roles por defecto...
✅ Roles ya existen en la base de datos
✅ Usuario administrador ya existe
✅ Usuario demo ya existe
✅ Datos de demostración ya existen
✅ Aplicación lista!

Started NutritrackApiApplication in 9.844 seconds
Tomcat started on port 8080 (http)
```

**¡Si ves esto, TODO FUNCIONA! 🎉**

---

## 🎮 PROBAR LA API

### 1️⃣ Health Check

**Navegador:** http://localhost:8080/api/v1/health

**PowerShell:**
```powershell
curl http://localhost:8080/api/v1/health
```

### 2️⃣ Swagger UI (Explorar endpoints)

**Navegador:** http://localhost:8080/swagger-ui/index.html

Aquí puedes:
- ✅ Ver todos los 60+ endpoints disponibles
- ✅ Probar cada endpoint interactivamente
- ✅ Ver esquemas de datos (DTOs)
- ✅ Autenticarte y hacer peticiones

### 3️⃣ Login de prueba

**En Swagger UI:**
1. Busca el endpoint: `POST /api/v1/auth/login`
2. Click en **"Try it out"**
3. Usa estas credenciales:

**Usuario Administrador:**
```json
{
  "email": "admin@nutritrack.com",
  "password": "Admin123!"
}
```

**Usuario Demo:**
```json
{
  "email": "user@demo.com",
  "password": "Demo123!"
}
```

4. Click **"Execute"**
5. Copia el `token` de la respuesta
6. Click en **"Authorize"** (arriba a la derecha)
7. Pega el token: `Bearer {tu-token-aquí}`
8. ¡Ahora puedes probar todos los endpoints! 🚀

---

## 🗂️ CONTENIDO DE LA BASE DE DATOS

Al iniciar por primera vez, se crean automáticamente:

### Usuarios
- ✅ **admin@nutritrack.com** - Administrador (puede crear planes, rutinas, etc.)
- ✅ **user@demo.com** - Usuario demo con perfil completo

### Datos Demo
- 📊 **20+ Ingredientes** (pollo, arroz, brócoli, etc.)
- 🍽️ **15+ Comidas** (desayunos, almuerzos, cenas)
- 🏃 **25+ Ejercicios** (cardio, fuerza, flexibilidad)
- 🏷️ **15+ Etiquetas** (Pérdida de peso, Hipertrofia, Vegano, etc.)
- 📅 **5+ Planes alimenticios** completos
- 💪 **5+ Rutinas de ejercicio** completas

---

## 🚨 SOLUCIÓN DE PROBLEMAS

### ❌ Error: "Cannot resolve symbol 'jakarta'"

**Causa:** Maven no descargó las dependencias

**Solución:**
```powershell
# Opción 1: Desde PowerShell
.\mvnw.cmd clean install

# Opción 2: Desde IntelliJ
File → Invalidate Caches → Invalidate and Restart
```

### ❌ Error: "Port 8080 already in use"

**Causa:** Otra aplicación está usando el puerto 8080

**Solución:**
```powershell
# Ver qué está usando el puerto
netstat -ano | findstr :8080

# Matar el proceso (reemplaza XXXX con el PID de la columna derecha)
taskkill /F /PID XXXX
```

### ❌ Error: "Unable to connect to database"

**Causa:** PostgreSQL no está corriendo

**Solución:**
```powershell
# Verificar que Docker Desktop esté abierto

# Ver contenedores activos
docker ps

# Si no aparece nutritrack-postgres, iniciar:
docker-compose up -d

# Ver logs de PostgreSQL
docker-compose logs -f postgres

# Si hay error, reiniciar completamente:
docker-compose down
docker-compose up -d
```

### ❌ Error de compilación en Maven

**Solución:**
```powershell
# Limpiar y recompilar
.\mvnw.cmd clean install

# Si persiste, verificar Java
java -version  # Debe ser Java 21
```

### ❌ Tests fallan

**Solución:**
```powershell
# Asegúrate de que PostgreSQL esté corriendo
docker ps

# Ejecutar tests
.\mvnw.cmd test

# Ver tests específicos
.\mvnw.cmd test -Dtest=AuthServiceTest
```

---

## 📊 ENDPOINTS DISPONIBLES

### 🔐 Autenticación (Público)
- `POST /api/v1/auth/registro` - Crear cuenta
- `POST /api/v1/auth/login` - Iniciar sesión

### 👤 Perfil (Requiere login)
- `GET /api/v1/perfil/mi-perfil` - Ver mi perfil
- `PUT /api/v1/perfil/mi-perfil` - Actualizar perfil
- `GET /api/v1/perfil/salud` - Ver perfil de salud
- `POST /api/v1/perfil/medidas` - Registrar medidas

### 🔍 Catálogo (Requiere login)
- `GET /api/admin/planes/catalogo` - Ver planes disponibles
- `GET /api/admin/planes/catalogo/{id}` - Detalle de plan
- `GET /api/admin/rutinas/catalogo` - Ver rutinas disponibles

### ✅ Mis Planes/Rutinas (USER)
- `POST /api/v1/usuario/planes/activar` - Activar plan
- `GET /api/v1/usuario/planes/activo` - Ver plan activo
- `PATCH /api/v1/usuario/planes/{id}/pausar` - Pausar plan
- `PATCH /api/v1/usuario/planes/{id}/reanudar` - Reanudar plan

### 🛠️ Administración (ADMIN)
- `GET /api/admin/ingredientes` - Listar ingredientes
- `POST /api/admin/ingredientes` - Crear ingrediente
- `GET /api/admin/planes` - Gestionar planes
- `POST /api/admin/planes` - Crear plan

**Ver todos:** http://localhost:8080/swagger-ui/index.html

---

## ✅ Checklist de verificación final:

- [ ] ✅ Java 21 instalado y configurado
- [ ] ✅ Docker Desktop corriendo
- [ ] ✅ PostgreSQL iniciado (`docker ps` muestra nutritrack-postgres)
- [ ] ✅ Proyecto abierto en IntelliJ IDEA
- [ ] ✅ Maven descargó todas las dependencias (sin errores rojos)
- [ ] ✅ Aplicación inicia correctamente
- [ ] ✅ `/api/v1/health` responde: `{"status":"UP"}`
- [ ] ✅ Swagger UI accesible en http://localhost:8080/swagger-ui/index.html
- [ ] ✅ Login exitoso con usuario demo
- [ ] ✅ Tests pasan: 97/97 ✅

---

## 🎯 PRÓXIMOS PASOS

### 1. Explorar la API
- Abre Swagger UI: http://localhost:8080/swagger-ui/index.html
- Prueba hacer login y explorar los endpoints
- Revisa la documentación en la carpeta `docs/`

### 2. Ver la base de datos
- Abre PgAdmin: http://localhost:5050
- Login: `admin@nutritrack.com` / `admin`
- Conecta al servidor PostgreSQL (ya está configurado)
- Explora las 15 tablas creadas

### 3. Ejecutar los tests
```powershell
.\mvnw.cmd test
```

### 4. Leer la documentación
- `docs/COMO_FUNCIONA.MD` - Arquitectura completa
- `docs/USER_STORIES.MD` - 25 historias de usuario
- `docs/REGLAS_NEGOCIO.MD` - 24 reglas de negocio

---

## 📚 RECURSOS ADICIONALES

### Archivos importantes
- `application.properties` - Configuración de la aplicación
- `docker-compose.yml` - Configuración de Docker
- `pom.xml` - Dependencias Maven
- `SQL/NutriDB.sql` - Schema de base de datos

### Postman Collections
La carpeta `postman/` contiene colecciones para probar:
- Módulo 1: Autenticación y Perfiles
- Módulo 2: Biblioteca de Contenido
- Módulo 3: Planes y Rutinas

### Comandos útiles

```powershell
# Detener la aplicación
Ctrl + C (en la terminal donde corre)

# Reiniciar base de datos (ELIMINA TODOS LOS DATOS)
docker-compose down -v
docker-compose up -d

# Ver logs en tiempo real
docker-compose logs -f postgres

# Compilar sin ejecutar tests
.\mvnw.cmd clean install -DskipTests

# Solo compilar
.\mvnw.cmd clean compile

# Ejecutar test específico
.\mvnw.cmd test -Dtest=AuthServiceTest
```

---

## 🆘 SOPORTE

Si encuentras algún problema:

1. **Revisa la sección de solución de problemas** arriba
2. **Verifica los logs** de la aplicación en la consola de IntelliJ
3. **Revisa los logs de Docker**: `docker-compose logs -f postgres`
4. **Busca en la documentación**: Carpeta `docs/`

---

**¡Felicidades! 🎉 Ya tienes NutriTrack API funcionando. ¡Hora de desarrollar!**
