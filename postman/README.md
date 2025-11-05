# 🧪 Colecciones Postman - NutriTrack API

> **Guía Completa de Pruebas**  
> Esta guía documenta cómo usar las colecciones Postman para validar las reglas de negocio  
> y demostrar la relación con los 175 tests unitarios implementados.

---

## 🌐 Entornos Disponibles

| Archivo | Entorno | URL Base | Uso |
|---------|---------|----------|-----|
| `NutriTrack_Render_Production.postman_environment.json` | **🚀 Producción (Render)** | `https://nutritrack-api-wt8b.onrender.com` | Pruebas en producción |
| `NutriTrack_Local_Development.postman_environment.json` | **💻 Desarrollo Local** | `http://localhost:8080` | Desarrollo local |

### 📥 Cómo Importar Entornos en Postman

1. Abre Postman
2. Click en **Environments** (panel izquierdo) o el icono de ⚙️ arriba a la derecha
3. Click en **Import**
4. Arrastra los archivos `.postman_environment.json`
5. Selecciona el entorno en el dropdown superior derecho

**Variables incluidas en ambos entornos:**
- `baseUrl` - URL base de la API
- `apiVersion` - Versión de la API (v1)
- `authToken` - Token JWT (se guarda automáticamente al hacer login)
- `adminEmail` / `adminPassword` - Credenciales de administrador
- `demoEmail` / `demoPassword` - Credenciales de usuario demo
- `userId`, `perfilId` - IDs que se guardan automáticamente

---

## 📁 Archivos Disponibles

| Colección | Descripción | Tests Unitarios | Reglas |
|-----------|-------------|-----------------|--------|
| `NutriTrack_Unit_Tests_Demo.postman_collection.json` | **🎯 DEMO PARA EXPOSICIÓN** | 13 tests (RN30, RN31, RN32) | 3 reglas críticas |
| `NutriTrack_Modulo1.postman_collection.json` | Autenticación y Perfil | 24 tests | RN01-RN05, RN30-RN31 |
| `NutriTrack_Modulo2.postman_collection.json` | Biblioteca de Contenido | 39 tests | RN06-RN10 |
| `NutriTrack_Modulo3.postman_collection.json` | Gestor de Catálogo | 39 tests | RN11-RN14 |
| `NutriTrack_Modulo4.postman_collection.json` | Asignación de Metas | 72 tests | RN17-RN19, RN26, **RN32** |
| `NutriTrack_Modulo5.postman_collection.json` | Seguimiento de Progreso | 1 test | RN20-RN24 |
| `NutriTrack_API_Complete.postman_collection.json` | Colección completa | 175 tests | Todas (27/39) |

---

## 🎯 COLECCIÓN RECOMENDADA PARA EXPOSICIÓN

### `NutriTrack_Unit_Tests_Demo.postman_collection.json`

**Esta colección está diseñada específicamente para demostrar:**
- ✅ RN30: Validación de Email RFC 5322 + DNS
- ✅ RN31: Política de Contraseñas Robusta (12+ caracteres)
- ✅ RN32: Validación Cruzada de Alérgenos

**Estructura:**
```
NutriTrack - Demo Unit Tests/
├── RN30 - Validación de Email/
│   ├── ❌ Email sin formato válido (sin @)
│   ├── ❌ Email con dominio inexistente (DNS Fail)
│   └── ✅ Email válido con DNS verificado
│
├── RN31 - Política de Contraseñas Robusta/
│   ├── ❌ Contraseña corta (< 12 caracteres)
│   ├── ❌ Contraseña sin complejidad (solo lowercase)
│   ├── ❌ Contraseña común (blacklist)
│   ├── ❌ Contraseña que contiene email
│   └── ✅ Contraseña válida (12+ chars + complejidad)
│
├── RN32 - Validación Cruzada de Alérgenos/
│   ├── 0. Login Usuario Demo
│   ├── ❌ Activar plan con alérgenos (si usuario tiene alergias)
│   └── ✅ Ver Planes del Catálogo (filtrados por alérgenos)
│
└── 📊 Resumen de Unit Tests/
    └── README - Unit Tests Coverage
```

**Tests Automáticos Incluidos:**
- ✅ Validación de status codes (400 para errores, 201 para éxito)
- ✅ Verificación de mensajes de error específicos
- ✅ Assertions de formato de respuesta

---

## 🚀 Importar en Postman

### Método 1: Importación Simple
1. Abre Postman
2. Click en **Import** (esquina superior izquierda)
3. Arrastra `NutriTrack_Unit_Tests_Demo.postman_collection.json`
4. Click en **Import**

### Método 2: Importación desde URL
```
File → Import → Link
```
Pega la URL del repositorio si está publicado

---

## 🧪 CÓMO DEMOSTRAR LOS UNIT TESTS

### Paso 1: Verificar que la aplicación está corriendo
```bash
./mvnw spring-boot:run
```
Espera a ver: `Started NutritrackApiApplication in X seconds`

### Paso 2: Abrir Swagger UI (Documentación Visual)
```
http://localhost:8080/swagger-ui/index.html
```

**Puntos clave en Swagger:**
- Endpoint `POST /api/v1/auth/registro` muestra ejemplos de RN30 y RN31
- Endpoint `POST /api/v1/usuario/planes/activar` documenta RN32
- Cada endpoint muestra los unit tests asociados en su descripción

### Paso 3: Ejecutar colección de demostración en Postman

**Ejecutar carpeta RN30:**
1. Abrir Postman
2. Navegar a colección "NutriTrack - Demo Unit Tests"
3. Click derecho en carpeta "RN30 - Validación de Email"
4. Seleccionar "Run folder"
5. Click "Run NutriTrack..."

**Resultado esperado:**
```
✅ RN30: Rechaza email sin @
✅ Mensaje de error contiene validación
✅ RN30: Rechaza dominio inexistente (DNS lookup)
✅ Mensaje indica dominio no existe
✅ RN30: Acepta email válido RFC 5322 con DNS
```

**Ejecutar carpeta RN31:**
- Mismo proceso, carpeta "RN31 - Política de Contraseñas Robusta"
- 5 tests deben pasar

**Ejecutar carpeta RN32:**
- Mismo proceso, carpeta "RN32 - Validación Cruzada de Alérgenos"
- 3 tests deben pasar

### Paso 4: Ejecutar tests unitarios en terminal (Comparación)

```bash
# Ejecutar todos los tests
./mvnw test

# Ejecutar solo tests de AuthService (RN30, RN31)
./mvnw test -Dtest=AuthServiceTest

# Ejecutar solo tests de UsuarioPlanService (RN32)
./mvnw test -Dtest=UsuarioPlanServiceTest
```

**Mostrar salida:**
```
Tests run: 175, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS ✅
```

### Paso 5: Mostrar código de los tests

**Ubicación de los tests:**
```
src/test/java/com/example/nutritrackapi/service/
├── AuthServiceTest.java          (RN30, RN31 - 13 tests)
├── UsuarioPlanServiceTest.java   (RN32 - 37 tests)
├── PerfilServiceTest.java        (RN03, RN22 - 11 tests)
├── EtiquetaServiceTest.java      (RN06, RN08 - 12 tests)
├── IngredienteServiceTest.java   (RN07, RN09 - 9 tests)
├── PlanServiceTest.java          (RN11, RN14, RN28 - 22 tests)
└── ... (8 clases más)
```

**Abrir en VS Code:**
```bash
code src/test/java/com/example/nutritrackapi/service/AuthServiceTest.java
```

---

## 📊 MAPEO: POSTMAN ↔ UNIT TESTS ↔ REGLAS

### RN30: Validación de Email

| Test Postman | Test Unitario | Método | Status |
|--------------|---------------|--------|--------|
| Email sin @ | `testRegistro_EmailFormatoInvalido()` | `AuthService.validarEmail()` | ✅ |
| Dominio inexistente | `testRegistro_EmailDominioInexistente()` | `InetAddress.getByName()` | ✅ |
| Email válido | `testRegistro_EmailValido()` | `@Email(regexp=...)` | ✅ |

**Documentación:** Ver `docs/UNIT_TESTS_MAPPING.md` línea 32-38

---

### RN31: Política de Contraseñas

| Test Postman | Test Unitario | Validación | Status |
|--------------|---------------|------------|--------|
| Contraseña < 12 chars | `testRegistro_PasswordCorta()` | `@Size(min=12)` | ✅ |
| Sin mayúscula | `testRegistro_PasswordSinMayuscula()` | `@Pattern(...)` | ✅ |
| Sin minúscula | `testRegistro_PasswordSinMinuscula()` | `@Pattern(...)` | ✅ |
| Sin número | `testRegistro_PasswordSinNumero()` | `@Pattern(...)` | ✅ |
| Sin símbolo | `testRegistro_PasswordSinSimbolo()` | `@Pattern(...)` | ✅ |
| Contraseña común | `testRegistro_PasswordComun()` | `validarPasswordSegura()` | ✅ |
| Contiene email | `testRegistro_PasswordContieneEmail()` | `validarPasswordSegura()` | ✅ |

**Blacklist de contraseñas comunes:**
```java
Set.of("password1234", "admin1234567", "123456789012", "qwerty123456", "letmein12345")
```

---

### RN32: Validación Cruzada de Alérgenos

| Test Postman | Test Unitario | Query | Status |
|--------------|---------------|-------|--------|
| Activar con alérgeno | `testActivarPlan_ConAlergenosIncompatibles()` | 5-join query | ✅ |
| Sin alergias | `testActivarPlan_SinAlergias()` | Vacío OK | ✅ |
| Alergias compatibles | `testActivarPlan_AlergiasPeroCompatibles()` | Sin intersección | ✅ |

**Query HQL (5 niveles):**
```sql
SELECT DISTINCT ie.id FROM Plan p
INNER JOIN p.dias pd                    -- Plan → PlanDia
INNER JOIN pd.comida c                  -- PlanDia → Comida
INNER JOIN c.comidaIngredientes ci      -- Comida → ComidaIngrediente
INNER JOIN ci.ingrediente i             -- ComidaIngrediente → Ingrediente
INNER JOIN i.etiquetas ie               -- Ingrediente → Etiqueta
WHERE p.id = :planId
```

**Lógica de validación:**
```java
Set<Long> alergenosEnPlan = alergenosUsuario.stream()
    .filter(etiquetasIngredientesPlan::contains)
    .collect(Collectors.toSet());

if (!alergenosEnPlan.isEmpty()) {
    throw new BusinessException("Plan contiene: " + nombres);
}
```

---

## 🔧 Variables de Colección

| Variable | Descripción | Valor Inicial |
|----------|-------------|---------------|
| `baseUrl` | URL base de la API | `http://localhost:8080` |
| `userToken` | Token JWT del usuario | _(se guarda automáticamente)_ |

---

## 🛡️ Todas las Reglas de Negocio Implementadas

Ver documentación completa en: `docs/REGLAS_NEGOCIO.MD`

### Módulo 1 (RN01-RN05, RN30-RN31)
- ✅ RN01: Email único
- ✅ RN02: Validación de credenciales
- ✅ RN03: Unidades de medida consistentes
- ✅ RN04: Perfil salud con etiquetas maestras
- ✅ RN05: Eliminación con confirmación
- ✅ **RN30: Email RFC 5322 + DNS**
- ✅ **RN31: Contraseña 12+ chars con complejidad**

### Módulo 2 (RN06-RN10)
- ✅ RN06: Etiquetas con nombre único
- ✅ RN07: Ingredientes/Ejercicios únicos
- ✅ RN08: No eliminar etiquetas en uso
- ✅ RN09: No eliminar ingredientes en uso
- ✅ RN10: Cantidad ingrediente positiva

### Módulo 3 (RN11-RN14)
- ✅ RN11: Nombres únicos en catálogo
- ✅ RN12: Solo asignar etiquetas existentes
- ✅ RN13: Series y repeticiones positivas
- ✅ RN14: No eliminar plan con usuarios activos

### Módulo 4 (RN17-RN19, RN26, RN32)
- ✅ RN17: No duplicar mismo plan activo
- ✅ RN18: Proponer reemplazo
- ✅ RN19: No pausar/reanudar en estados finales
- ✅ RN26: Transiciones de estado válidas
- ✅ **RN32: Validación cruzada de alérgenos**

### Módulo 5 (RN20-RN24)
- ✅ RN20: Mostrar checks en actividades
- ✅ RN21: No marcar si plan pausado
- ✅ RN22: Validación de mediciones en rango
- ✅ RN23: Gráfico requiere 2+ registros
- ⚠️ RN24: Reporte PDF (pendiente)

### Reglas Transversales
- ✅ RN25: Cálculo automático de calorías
- ✅ RN26: Transiciones de estado válidas
- ✅ RN27: Unidades consistentes (KG/CM en DB)
- ✅ RN28: Soft delete para planes/rutinas

---

## 📝 Ejemplos de Uso para Demostración

### Demo 1: RN30 - Email con dominio inexistente

**Request Postman:**
```json
POST /api/v1/auth/registro
{
  "email": "usuario@dominioquenoexiste99999.com",
  "password": "TestPass123!",
  "nombre": "Test",
  "apellido": "Error"
}
```

**Response esperado:**
```json
{
  "status": "error",
  "message": "El dominio de email no existe o no es alcanzable",
  "data": null
}
```

**Test Postman automático:**
```javascript
pm.test('RN30: Rechaza dominio inexistente (DNS lookup)', function () {
    pm.response.to.have.status(400);
});

pm.test('Mensaje indica dominio no existe', function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData.message).to.include('dominio').or.include('existe');
});
```

---

### Demo 2: RN31 - Contraseña en blacklist

**Request Postman:**
```json
POST /api/v1/auth/registro
{
  "email": "test@gmail.com",
  "password": "password1234",
  "nombre": "Test",
  "apellido": "Error"
}
```

**Response esperado:**
```json
{
  "status": "error",
  "message": "Contraseña demasiado común. Elige una más segura.",
  "data": null
}
```

---

### Demo 3: RN32 - Plan con alérgenos

**Prerequisito:** Usuario debe tener alergias configuradas en su perfil

**Request Postman:**
```json
POST /api/v1/usuario/planes/activar
Headers: Authorization: Bearer {{userToken}}
{
  "planId": 1
}
```

**Response esperado (si plan contiene alérgenos):**
```json
{
  "status": "error",
  "message": "No se puede activar este plan. Contiene ingredientes con alérgenos: Nueces, Lácteos",
  "data": null
}
```

**Response esperado (si plan es compatible):**
```json
{
  "status": "success",
  "message": "Plan activado exitosamente",
  "data": {
    "id": 123,
    "planId": 1,
    "estado": "ACTIVO",
    "diaActual": 1
  }
}
```

---

## 🔍 Verificación en Swagger UI

**URL:** http://localhost:8080/swagger-ui/index.html

**Qué mostrar:**
1. Buscar endpoint `POST /api/v1/auth/registro`
2. Expandir y mostrar sección "Description"
3. Mostrar ejemplos de:
   - ✅ Registro Válido (RN30 y RN31 cumplidos)
   - ❌ Email Inválido (RN30)
   - ❌ Contraseña Débil (RN31)
   - ❌ Contraseña Común (RN31)

4. Buscar endpoint `POST /api/v1/usuario/planes/activar`
5. Mostrar documentación de RN32 con query 5-join

---

## 💡 Tips para la Exposición

### 1. Preparación Previa
- ✅ Aplicación corriendo en `localhost:8080`
- ✅ Postman abierto con colección importada
- ✅ Swagger UI abierto en navegador
- ✅ Terminal abierta con `./mvnw test` ejecutado
- ✅ VS Code abierto en `AuthServiceTest.java`

### 2. Flujo Recomendado de Demostración
1. **Mostrar resultados de tests (5 min)**
   - Terminal con `./mvnw test` output
   - Resaltar: "Tests run: 175, Failures: 0"

2. **Swagger UI (5 min)**
   - Documentación de RN30, RN31 en `/registro`
   - Documentación de RN32 en `/planes/activar`
   - Mostrar ejemplos de error

3. **Postman - Ejecutar tests (10 min)**
   - Carpeta RN30: 3 tests
   - Carpeta RN31: 5 tests
   - Carpeta RN32: 3 tests
   - Mostrar assertions pasando

4. **Código de tests unitarios (5 min)**
   - Abrir `AuthServiceTest.java`
   - Mostrar método `testRegistro_EmailDominioInexistente()`
   - Mostrar método `testRegistro_PasswordComun()`

5. **Código de implementación (5 min)**
   - Abrir `AuthService.java`
   - Mostrar método `validarEmail()` con DNS lookup
   - Mostrar método `validarPasswordSegura()` con blacklist

6. **Mapeo completo (2 min)**
   - Abrir `docs/UNIT_TESTS_MAPPING.md`
   - Mostrar tabla de 175 tests mapeados

---

## 📚 Documentación Adicional

| Documento | Descripción |
|-----------|-------------|
| `docs/UNIT_TESTS_MAPPING.md` | **Mapeo completo de 175 tests** |
| `docs/REGLAS_NEGOCIO.MD` | Especificación de 39 reglas |
| `CREDENCIALES_ADMIN.md` | Usuarios de prueba |
| `docs/USER_STORIES.MD` | 25 historias de usuario |

---

## 🐛 Troubleshooting

### Error: "Connection refused"
```bash
# Verificar que la app está corriendo
./mvnw spring-boot:run

# Esperar a ver:
# Started NutritrackApiApplication in 11.242 seconds
```

### Error: "Unauthorized" en Postman
```
1. Ejecutar request "0. Login Usuario Demo" primero
2. Verificar que variable {{userToken}} se guardó
3. Ver en: Variables (tab en la colección)
```

### Error: "Email ya existe"
```
Este es el comportamiento esperado (RN01)
Usar otro email o hacer login en su lugar
```

---

## 📞 Soporte

**Documentos técnicos:**
- [REGLAS_NEGOCIO.MD](../docs/REGLAS_NEGOCIO.MD) - 39 reglas con prioridades
- [UNIT_TESTS_MAPPING.md](../docs/UNIT_TESTS_MAPPING.md) - Mapeo completo de 175 tests
- [COMO_FUNCIONA.MD](../docs/COMO_FUNCIONA.MD) - Arquitectura del sistema

**Comandos útiles:**
```bash
# Ejecutar todos los tests
./mvnw test

# Ver cobertura de tests
./mvnw test jacoco:report
# Abrir: target/site/jacoco/index.html

# Ejecutar Newman (Postman CLI)
npm install -g newman
newman run postman/NutriTrack_Unit_Tests_Demo.postman_collection.json
```

---

**Última actualización:** 5 de Noviembre, 2025  
**Versión:** 2.0  
**Responsable:** Equipo NutriTrack
**Tests Totales:** 175/175 ✅ (100%)
**Reglas Implementadas:** 27/39 (69.2%)
