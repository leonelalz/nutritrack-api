# Testing - NutriTrack API 🧪

Documentación y recursos para testing del API.

## 📁 Estructura

```
testing/
├── README.md                           # Este archivo
└── POSTMAN_GUIDE.md                    # Guía completa de Postman

postman/                                # Carpeta en raíz del proyecto
├── collections/                        # Colecciones de Postman
│   ├── Module_1_Cuentas_Preferencias.postman_collection.json
│   ├── Module_2_Biblioteca_Contenido.postman_collection.json
│   ├── Module_3_Gestor_Catalogo.postman_collection.json
│   ├── Module_4_Exploracion_Activacion.postman_collection.json
│   └── Module_5_Seguimiento_Progreso.postman_collection.json
└── environments/                       # Environments de Postman
    ├── Local.postman_environment.json
    ├── Development.postman_environment.json
    ├── Staging.postman_environment.json
    └── Production.postman_environment.json
```

## 🚀 Inicio Rápido

### 1. Importar Colecciones en Postman

```bash
# 1. Abrir Postman
# 2. Click en "Import"
# 3. Seleccionar todos los archivos .json de postman/collections/
# 4. Importar environments de postman/environments/
```

### 2. Configurar Environment

1. Seleccionar "Local" en el dropdown de environments
2. Editar el environment:
   - `baseUrl`: `http://localhost:8080/api/v1`
   - Guardar

### 3. Ejecutar Primera Prueba

1. Abrir Collection "Module 1: Gestión de Cuentas"
2. Ejecutar "POST Register"
3. Verificar que el test pase y se guarde el token automáticamente

## 📚 Documentación

- **[POSTMAN_GUIDE.md](POSTMAN_GUIDE.md)** - Guía completa de testing con Postman
  - 27 endpoints documentados
  - Scripts de automatización
  - Tests de validación
  - Manejo de variables

- **[API_REFERENCE.md](../API_REFERENCE.md)** - Referencia completa del API
  - Request/Response schemas
  - Códigos HTTP
  - Reglas de negocio

## 🧪 Colecciones Disponibles

| Colección | Endpoints | User Stories | Responsable |
|-----------|-----------|--------------|-------------|
| Module 1: Cuentas y Preferencias | 5 | US-01 a US-05 | Leonel Alzamora |
| Module 2: Biblioteca de Contenido | 11 | US-06 a US-10 | Fabian, Gonzalo, Victor |
| Module 3: Gestor de Catálogo | 6 | US-11 a US-15 | Gonzalo, Victor |
| Module 4: Exploración y Activación | 4 | US-16 a US-20 | Gonzalo, Victor |
| Module 5: Seguimiento de Progreso | 7 | US-21 a US-25 | Gonzalo, Jhamil, Victor |

**Total:** 33 requests organizados

## 🌍 Environments

| Environment | URL | Propósito |
|-------------|-----|-----------|
| Local | `http://localhost:8080/api/v1` | Desarrollo local |
| Development | `https://dev-api.nutritrack.com/api/v1` | Servidor de desarrollo |
| Staging | `https://staging-api.nutritrack.com/api/v1` | Pre-producción |
| Production | `https://api.nutritrack.com/api/v1` | Producción |

## ✅ Checklist de Testing

### Antes de Cada Sprint

- [ ] Importar/actualizar colección del módulo a trabajar
- [ ] Configurar environment correspondiente
- [ ] Ejecutar tests de módulos dependientes
- [ ] Verificar que todos los tests base pasen

### Durante Desarrollo

- [ ] Crear request para cada endpoint nuevo
- [ ] Agregar tests de validación
- [ ] Probar casos de error (400, 401, 404, 409)
- [ ] Actualizar variables de environment
- [ ] Documentar en POSTMAN_GUIDE.md

### Antes de Pull Request

- [ ] Ejecutar Collection Runner en todos los módulos
- [ ] 100% de tests pasando
- [ ] Exportar colección actualizada
- [ ] Commitear archivos JSON actualizados
- [ ] Actualizar documentación si hay cambios

## 🔄 Workflow Recomendado

### 1. Setup Inicial (Una vez)

```bash
# Clonar repositorio
git clone https://github.com/leonelalz/nutritrack-api.git
cd nutritrack-api

# Importar en Postman
# - Importar todas las colecciones de docs/testing/postman/collections/
# - Importar environment Local de docs/testing/postman/environments/
```

### 2. Testing Diario

```bash
# 1. Iniciar servidor local
./mvnw spring-boot:run

# 2. En Postman:
# - Seleccionar environment "Local"
# - Ejecutar Collection Runner en la colección del módulo
# - Verificar resultados
```

### 3. Antes de Hacer Push

```bash
# Exportar colecciones actualizadas
# 1. En Postman, click derecho en colección
# 2. Export → Collection v2.1
# 3. Guardar en docs/testing/postman/collections/

# Commitear cambios
git add docs/testing/postman/collections/
git commit -m "test: Actualizar colección Postman para [módulo]"
git push
```

## 🤖 Automatización

### Runner de Postman

1. Click en colección
2. Click en "Run"
3. Seleccionar environment
4. Click "Run [Collection Name]"
5. Ver resultados

### Newman (CLI)

```bash
# Instalar Newman
npm install -g newman

# Ejecutar colección
newman run docs/testing/postman/collections/Module_1_Cuentas_Preferencias.postman_collection.json \
  -e docs/testing/postman/environments/Local.postman_environment.json

# Ejecutar todas las colecciones
./docs/testing/scripts/run-tests.sh
```

## 📊 Métricas de Testing

### Objetivos

- ✅ 100% de endpoints cubiertos
- ✅ Tests para casos exitosos
- ✅ Tests para todos los casos de error
- ✅ Validación de schemas
- ✅ Automatización con scripts

### Estado Actual

| Módulo | Endpoints | Tests | Cobertura |
|--------|-----------|-------|-----------|
| Módulo 1 | 5/5 | ✅ | 100% |
| Módulo 2 | 11/11 | ✅ | 100% |
| Módulo 3 | 6/6 | ✅ | 100% |
| Módulo 4 | 4/4 | ✅ | 100% |
| Módulo 5 | 7/7 | ✅ | 100% |

## 🆘 Troubleshooting

### Error: Token Expirado (401)

**Solución:** Ejecutar nuevamente "POST Login" para obtener un nuevo token.

### Error: CORS

**Solución:** Usar Postman Desktop App, no la versión web.

### Variables no se guardan

**Solución:** Verificar que los scripts de Tests estén usando `pm.environment.set()`.

### Servidor no responde

```bash
# Verificar que el servidor esté corriendo
curl http://localhost:8080/actuator/health

# Reiniciar servidor
./mvnw spring-boot:run
```

## 📞 Soporte

- **Documentación:** [POSTMAN_GUIDE.md](POSTMAN_GUIDE.md)
- **API Reference:** [API_REFERENCE.md](../API_REFERENCE.md)
- **Issues:** Reportar en GitHub con etiqueta `testing`
- **Equipo:** Contactar al responsable del módulo

---

**Última actualización:** Octubre 2025  
**Mantenido por:** Equipo NutriTrack
