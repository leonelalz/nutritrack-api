# 🚀 Despliegue Rápido en Render

## Opción 1: Despliegue Automático (Recomendado) ⚡

### 1. Subir a GitHub
```bash
git push origin main
```

### 2. Crear Blueprint en Render
1. Ve a [Render Dashboard](https://dashboard.render.com/)
2. **New +** → **Blueprint**
3. Conecta tu repo: `leonelalz/nutritrack-api`
4. Render detectará `render.yaml`
5. **Apply** → Espera 5-10 minutos

### 3. Cargar Datos SQL
Una vez la BD esté lista:
1. Copia credenciales de PostgreSQL desde Render
2. Conéctate con DBeaver/pgAdmin
3. Ejecuta en orden:
   ```sql
   \i SQL/NutriDB.sql
   \i SQL/catalogo_basico.sql
   \i SQL/data_demo.sql
   ```

### 4. ¡Listo! 🎉
- **API:** `https://nutritrack-api.onrender.com`
- **Swagger:** `https://nutritrack-api.onrender.com/swagger-ui.html`
- **Health:** `https://nutritrack-api.onrender.com/actuator/health`

---

## Opción 2: Despliegue Manual 🔧

Ver guía completa en [DEPLOY_RENDER.md](./DEPLOY_RENDER.md)

---

## ⚠️ Importante

**Free Tier de Render:**
- API duerme después de 15 min de inactividad
- Primer request tarda ~30s en despertar
- PostgreSQL se borra después de 90 días

**Solución:** Usa [UptimeRobot](https://uptimerobot.com/) para ping cada 14 min

---

## 🔒 Después del Despliegue

### Cambiar credenciales de admin
```sql
UPDATE cuentas_auth 
SET password = '$2a$10$NUEVO_HASH_BCRYPT' 
WHERE email = 'admin@nutritrack.com';
```

### Configurar JWT Secret
En Render Environment Variables:
```bash
JWT_SECRET=TuNuevoSecretSuperSeguroDeAlMenos32Caracteres
```

---

## 📊 URLs Útiles

- Dashboard Render: https://dashboard.render.com/
- Logs en tiempo real: Dashboard → Tu servicio → Logs tab
- Connect to DB: Dashboard → nutritrack-db → Connect

---

## 🆘 Problemas Comunes

**API no inicia:** Verifica `DATABASE_URL` en Environment Variables

**Connection refused:** Espera que PostgreSQL esté "Available"

**Out of memory:** Ya optimizado con `-Xmx512m`

---

**Más detalles:** [DEPLOY_RENDER.md](./DEPLOY_RENDER.md)
