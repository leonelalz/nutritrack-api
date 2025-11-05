# 🚀 Guía de Despliegue en Render

Esta guía te ayudará a desplegar NutriTrack API en Render (gratis).

## 📋 Requisitos Previos

1. ✅ Cuenta en [Render](https://render.com) (gratis)
2. ✅ Repositorio GitHub público o conectado a Render
3. ✅ Código con todos los commits actualizados

---

## 🎯 Opción 1: Despliegue Automático con render.yaml

### Paso 1: Subir código a GitHub

```bash
# Asegúrate de tener todos los commits
git status
git add -A
git commit -m "chore: Preparar para despliegue en Render"
git push origin main
```

### Paso 2: Crear servicios en Render

1. Ve a [Render Dashboard](https://dashboard.render.com/)
2. Click en **"New +"** → **"Blueprint"**
3. Conecta tu repositorio GitHub `nutritrack-api`
4. Render detectará automáticamente `render.yaml`
5. Revisa la configuración:
   - ✅ **nutritrack-db**: PostgreSQL Free
   - ✅ **nutritrack-api**: Web Service Free
6. Click en **"Apply"**

### Paso 3: Configurar variables de entorno (automático)

Render configurará automáticamente:
- `DATABASE_URL` → De PostgreSQL
- `JWT_SECRET` → Generado automáticamente
- `SPRING_PROFILES_ACTIVE` → `production`

### Paso 4: Ejecutar scripts SQL

Una vez la base de datos esté lista:

1. Ve a **nutritrack-db** en Render Dashboard
2. Click en **"Connect"** → Copia las credenciales
3. Conéctate con un cliente PostgreSQL (DBeaver, pgAdmin)
4. Ejecuta en orden:
   ```sql
   -- 1. Estructura de base de datos
   \i SQL/NutriDB.sql
   
   -- 2. Datos de catálogo básico
   \i SQL/catalogo_basico.sql
   
   -- 3. Datos demo (opcional)
   \i SQL/data_demo.sql
   \i SQL/modulo2_catalogo_demo.sql
   \i SQL/modulo3_data_demo.sql
   \i SQL/modulo4_asignaciones_demo.sql
   \i SQL/modulo5_registros_demo.sql
   ```

### Paso 5: Verificar despliegue

Tu API estará disponible en: `https://nutritrack-api.onrender.com`

Verificaciones:
- ✅ Health Check: `GET /actuator/health`
- ✅ Swagger UI: `GET /swagger-ui.html`
- ✅ Login: `POST /api/v1/auth/login`

---

## 🎯 Opción 2: Despliegue Manual (Paso a Paso)

### 1️⃣ Crear Base de Datos PostgreSQL

1. En Render Dashboard → **"New +"** → **"PostgreSQL"**
2. Configuración:
   - **Name:** `nutritrack-db`
   - **Database:** `nutritrack_db`
   - **User:** (automático)
   - **Region:** Oregon (US West) - gratis
   - **Plan:** Free
3. Click **"Create Database"**
4. Espera 2-3 minutos hasta que esté disponible
5. Copia las credenciales (las necesitarás después)

**Credenciales que Render proporciona:**
```
Host: dpg-xxxxx.oregon-postgres.render.com
Port: 5432
Database: nutritrack_db
Username: nutritrack_db_user
Password: xxxxxxxxxx
Internal Database URL: postgresql://...
External Database URL: postgresql://...
```

### 2️⃣ Cargar Datos Iniciales

**Opción A: Desde DBeaver/pgAdmin**
1. Conecta con las credenciales de arriba
2. Ejecuta los scripts SQL en orden (ver Paso 4 de Opción 1)

**Opción B: Desde psql (línea de comandos)**
```bash
# Conectar a la base de datos
psql postgresql://nutritrack_db_user:password@host/nutritrack_db

# Ejecutar scripts
\i SQL/NutriDB.sql
\i SQL/catalogo_basico.sql
\i SQL/data_demo.sql
```

### 3️⃣ Crear Web Service para la API

1. En Render Dashboard → **"New +"** → **"Web Service"**
2. Conecta tu repositorio GitHub
3. Configuración:

**General:**
- **Name:** `nutritrack-api`
- **Region:** Oregon (US West) - mismo que DB
- **Branch:** `main`
- **Root Directory:** (dejar vacío)
- **Runtime:** Java

**Build & Deploy:**
- **Build Command:**
  ```bash
  ./mvnw clean package -DskipTests
  ```
- **Start Command:**
  ```bash
  java -Dspring.profiles.active=production -Xmx512m -jar target/nutritrack-API-0.0.1-SNAPSHOT.jar
  ```

**Advanced:**
- **Health Check Path:** `/actuator/health`
- **Auto-Deploy:** Yes

4. Click **"Create Web Service"**

### 4️⃣ Configurar Variables de Entorno

En tu Web Service (`nutritrack-api`):

1. Ve a **"Environment"** tab
2. Agrega estas variables:

```bash
# Perfil de Spring Boot
SPRING_PROFILES_ACTIVE=production

# URL de la base de datos (desde nutritrack-db)
DATABASE_URL=postgresql://nutritrack_db_user:PASSWORD@HOST/nutritrack_db

# JWT Secret (genera uno nuevo)
JWT_SECRET=TuClaveSecretaSuperSeguraDeAlMenos32Caracteres

# Memoria Java
JAVA_TOOL_OPTIONS=-Xmx512m
```

**Para obtener DATABASE_URL:**
1. Ve a tu PostgreSQL service (`nutritrack-db`)
2. Copia el **"Internal Database URL"**
3. Pégalo en `DATABASE_URL`

3. Click **"Save Changes"**
4. Render redesplegará automáticamente

### 5️⃣ Verificar Despliegue

**Endpoints públicos:**
```bash
# Tu URL será algo como:
https://nutritrack-api.onrender.com

# Health check
GET https://nutritrack-api.onrender.com/actuator/health

# Swagger UI
https://nutritrack-api.onrender.com/swagger-ui.html

# Login demo
POST https://nutritrack-api.onrender.com/api/v1/auth/login
Body: {
  "email": "admin@nutritrack.com",
  "password": "Admin123!"
}
```

---

## ⚠️ Consideraciones Importantes

### Free Tier de Render

✅ **Ventajas:**
- Gratis para siempre
- PostgreSQL con 1GB storage
- Web Service con 750 horas/mes
- SSL/HTTPS automático
- Deploys automáticos desde GitHub

⚠️ **Limitaciones:**
- La API "duerme" después de 15 minutos de inactividad
- Primer request tarda ~30 segundos en despertar
- Solo 512MB RAM
- PostgreSQL se borra después de 90 días (Free Tier)

### Optimizaciones Recomendadas

**1. Evitar que la API duerma:**
- Usa [UptimeRobot](https://uptimerobot.com/) (gratis)
- Configura un ping cada 14 minutos a `/actuator/health`

**2. Base de datos persistente:**
- Considera PostgreSQL de pago ($7/mes) para datos permanentes
- O exporta/importa dumps regularmente

**3. Logs y monitoreo:**
- Render tiene logs en tiempo real en el Dashboard
- Puedes usar el **"Logs"** tab para debugging

---

## 🔒 Seguridad en Producción

### Cambiar JWT Secret

```bash
# Generar nuevo secret seguro (32+ caracteres)
openssl rand -base64 32

# Agregar en Render Environment Variables
JWT_SECRET=tu_nuevo_secret_generado
```

### Cambiar credenciales de admin

Después del primer despliegue, ejecuta en la BD:

```sql
-- Cambiar password del admin
UPDATE cuentas_auth 
SET password = '$2a$10$NUEVO_HASH_BCRYPT' 
WHERE email = 'admin@nutritrack.com';
```

---

## 📊 Monitoreo Post-Despliegue

### Verificar que todo funciona

```bash
# 1. Health check
curl https://tu-app.onrender.com/actuator/health

# Respuesta esperada:
{"status":"UP"}

# 2. Login
curl -X POST https://tu-app.onrender.com/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@nutritrack.com","password":"Admin123!"}'

# Respuesta esperada:
{"success":true,"data":{"token":"eyJhbGc..."}}

# 3. Swagger
# Abre en navegador: https://tu-app.onrender.com/swagger-ui.html
```

### Ver logs en tiempo real

1. Ve a tu Web Service en Render Dashboard
2. Click en **"Logs"** tab
3. Verás el output de Spring Boot en tiempo real

---

## 🐛 Troubleshooting

### Error: "Application failed to start"

**Posible causa:** DATABASE_URL mal configurada

**Solución:**
1. Ve a Environment Variables
2. Verifica que `DATABASE_URL` empiece con `postgresql://` (no `postgres://`)
3. Si es necesario, modifica:
   ```
   # Cambiar esto:
   postgres://user:pass@host/db
   
   # Por esto:
   postgresql://user:pass@host/db
   ```

### Error: "Connection refused"

**Posible causa:** Base de datos no está lista

**Solución:**
1. Ve a tu PostgreSQL service
2. Espera hasta que el status sea **"Available"**
3. Redespliega el Web Service

### Error: "Out of memory"

**Posible causa:** 512MB no es suficiente

**Solución:**
1. Reduce logs en producción (ya configurado en `application-production.properties`)
2. Agrega `-Xmx512m` en `JAVA_TOOL_OPTIONS` (ya incluido)
3. Considera upgrade a plan de pago si es necesario

---

## ✅ Checklist Final

Antes de considerar el despliegue completo:

- [ ] Código subido a GitHub
- [ ] PostgreSQL creado en Render
- [ ] Scripts SQL ejecutados en orden
- [ ] Web Service creado
- [ ] Variables de entorno configuradas
- [ ] Health check retorna `200 OK`
- [ ] Swagger UI accesible
- [ ] Login funciona con usuario admin
- [ ] Postman collections actualizadas con nueva URL

---

## 📞 Soporte

Si tienes problemas:
1. Revisa los logs en Render Dashboard
2. Verifica las variables de entorno
3. Consulta [Render Docs](https://render.com/docs)
4. Revisa que los scripts SQL se ejecutaron correctamente

---

**¡Tu API está lista para producción!** 🎉
