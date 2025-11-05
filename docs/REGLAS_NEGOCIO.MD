# 📜 REGLAS DE NEGOCIO - NUTRITRACK

> **Documento Fundacional del Proyecto**  
> Este documento establece las 24 reglas de negocio que DEBEN implementarse en el sistema NutriTrack.  
> Sirve como guía de desarrollo para cada integrante del equipo.

---

## 📊 DISTRIBUCIÓN DE RESPONSABILIDADES

### Asignación de Reglas por Integrante

| Módulo | Responsable | Reglas Asignadas | Prioridad Crítica |
|--------|-------------|------------------|-------------------|
| **Módulo 1: Gestión de Cuentas y Perfil** | Leonel Alzamora | RN01 - RN05 (5 reglas) | - |
| **Módulo 2: Biblioteca de Contenido** | Fabian Rojas | RN06 - RN10 (5 reglas) | RN07, RN08, RN09 🔴 |
| **Módulo 3: Gestor de Catálogo** | Jhamil Peña | RN11 - RN14 (4 reglas) | RN14 🟡 |
| **Módulo 4: Exploración y Activación** | Gonzalo Huaranga | RN15 - RN17 (3 reglas) | RN16 🚨🔴 |
| **Módulo 4: Exploración y Activación** | Victor Carranza | RN18 - RN19 (2 reglas) | - |
| **Módulo 5: Seguimiento de Progreso** | Leonel Alzamora | RN20 - RN24 (5 reglas) | - |
| **TOTAL** | **Equipo Completo** | **24 reglas** | **5 críticas** |

### 🚨 Reglas de Implementación Crítica (Prioridad Máxima)

Estas reglas deben implementarse PRIMERO por riesgo de seguridad o integridad de datos:

1. 🚨 **RN16** - Filtrar catálogo por alergias del usuario (**SEGURIDAD DE SALUD** - Gonzalo Huaranga)
2. 🔴 **RN07** - Ingredientes/Ejercicios con nombre único (**INTEGRIDAD** - Fabian Rojas)
3. 🔴 **RN08** - No eliminar etiquetas en uso (**INTEGRIDAD** - Fabian Rojas)
4. 🔴 **RN09** - No eliminar ingredientes/ejercicios en uso (**INTEGRIDAD** - Fabian Rojas)
5. 🟡 **RN14** - No eliminar plan si tiene usuarios activos (**CONSISTENCIA** - Jhamil Peña)

---

## 📋 MÓDULO 1: GESTIÓN DE CUENTAS Y PERFIL
**Responsable:** Leonel Alzamora

### RN01 - Email único en `cuentas_auth`
- **Regla:** Un usuario no puede crear más de una cuenta con el mismo correo electrónico (el email debe ser UNIQUE en cuentas_auth).
- **Historia de Usuario:** US-01 – Crear cuenta
- **Responsable:** Leonel Alzamora
- **Implementación Requerida:**
  - Constraint en JPA: `@Column(unique = true)` en `CuentaAuth.email`
  - Constraint en SQL: `UNIQUE` en tabla `cuentas_auth`
  - Validación en Service: Verificar email no existe antes de crear cuenta

### RN02 - Login falla si credenciales incorrectas o cuenta inactiva
- **Regla:** El inicio de sesión debe fallar si las credenciales son incorrectas o si la cuenta tiene el estado = 'inactivo'.
- **Historia de Usuario:** US-02 – Iniciar Sesión
- **Responsable:** Leonel Alzamora
- **Implementación Requerida:**
  - Validar contraseña con BCrypt en `AuthService.login()`
  - Verificar `cuentaAuth.activo == true` antes de generar token JWT
  - Retornar HTTP 401 si credenciales incorrectas o cuenta inactiva

### RN03 - Unidad de medida aplica a todas las vistas
- **Regla:** La unidad de medida (ej. "kg" o "lbs") seleccionada por el usuario debe aplicarse a todas las vistas donde se muestre peso.
- **Historia de Usuario:** US-03 – Configurar unidades de medida
- **Responsable:** Leonel Alzamora
- **Implementación Requerida:**
  - Campo `perfiles_usuario.unidades_medida` (ENUM: KG/LBS)
  - DTOs de respuesta deben convertir peso según preferencia del usuario
  - Guardar SIEMPRE en KG en base de datos, convertir solo en presentación

### RN04 - Perfil salud usa etiquetas maestras
- **Responsable:** Leonel Alzamora
- **Implementación Requerida:**
  - Tabla `usuario_etiquetas_salud` con FK a `etiquetas` y FK a `perfiles_usuario`
  - Solo permitir seleccionar etiquetas existentes (validación en Service)
  - Categorías: objetivos, alergias, condiciones médicasAlzamora
- **Implementación:** `usuario_etiquetas_salud` FK a tabla `etiquetas`
- **Estado:** ✅ **CUMPLE**

### RN05 - Eliminación requiere confirmación explícita
- **Responsable:** Leonel Alzamora
- **Implementación Requerida:**
```javaado:** ⚠️ **PARCIAL** (confirmación solo en frontend, falta validación en backend)
- **Acción Requerida:**
```java
@DeleteMapping("/cuenta")
public ResponseEntity<?> eliminarCuenta(
    @RequestParam String confirmacion // Usuario debe escribir "ELIMINAR"
) {
    if (!"ELIMINAR".equals(confirmacion)) {
        throw new BadRequestException("Texto de confirmación incorrecto");
    }
    // Proceder con eliminación
}
```

---

## 📚 MÓDULO 2: BIBLIOTECA DE CONTENIDO - ADMIN
**Responsable:** Fabian Rojas

### RN06 - Etiquetas con nombre único
- **Regla:** No pueden existir dos etiquetas con el mismo nombre (restricción UNIQUE).
- **Historia de Usuario:** US-06 – Gestionar Etiquetas
- **Responsable:** Fabian Rojas
- **Implementación Requerida:**
  - Constraint en JPA: `@Column(unique = true)` en `Etiqueta.nombre`
  - Constraint en SQL: `UNIQUE` en tabla `etiquetas`

### RN07 - Ingredientes/Ejercicios con nombre único
- **Regla:** No pueden existir dos ingredientes o ejercicios con el mismo nombre (restricción UNIQUE).
- **Historias de Usuario:** US-07 – Gestionar Ingredientes, US-08 – Gestionar Ejercicios
- **Responsable:** Fabian Rojas
- **Responsable:** Fabian Rojas
- **Prioridad:** 🔴 **CRÍTICA**
- **Implementación Requerida:**
```sqlTABLE ingredientes ADD CONSTRAINT uk_ingredientes_nombre UNIQUE (nombre);
ALTER TABLE ejercicios ADD CONSTRAINT uk_ejercicios_nombre UNIQUE (nombre);
```

### RN08 - No eliminar etiqueta si está en uso
- **Regla:** El sistema debe impedir la eliminación (DELETE) de una etiqueta si esta está siendo usada (en etiquetas_ingredientes, etiquetas_metas, etc.).
- **Historia de Usuario:** US-06 – Gestionar Etiquetas
- **Responsable:** Fabian Rojas
- **Implementación:** No implementado - falta validación en `EtiquetaService.eliminar()`
- **Responsable:** Fabian Rojas
- **Prioridad:** 🔴 **CRÍTICA**
- **Implementación Requerida:**
  - Query en `EtiquetaRepository` para verificar si está en uso
  - Verificar en: `plan_etiquetas`, `rutina_etiquetas`, `usuario_etiquetas_salud`, `ingrediente_etiquetas`, `ejercicio_etiquetas`
  - Si está en uso, retornar HTTP 409 Conflict con mensaje claro
- **Regla:** El sistema debe impedir la eliminación de un ingrediente o ejercicio si este está siendo usado en una receta o catalogo_rutinas.
- **Historias de Usuario:** US-07 – Gestionar Ingredientes, US-08 – Gestionar Ejercicios
- **Responsable:** Fabian Rojas
- **Implementación:** No implementado - falta validación en servicios
- **Responsable:** Fabian Rojas
- **Prioridad:** 🔴 **CRÍTICA**
- **Implementación Requerida:**
  - Validar ingrediente no está en `comida_ingredientes` antes de eliminar
  - Validar ejercicio no está en `rutina_ejercicios` antes de eliminar
  - Retornar HTTP 409 Conflict si está en uso
- **Regla:** Al crear recetas, la cantidad_ingrediente debe validarse como un valor numérico positivo.
- **Historia de Usuario:** US-10 – Gestionar Recetas
- **Responsable:** Fabian Rojas
- **Implementación:** SQL: `NUMERIC(5,2) NOT NULL`, validación en DTO con `@Positive`
- **Estado:** ✅ **CUMPLE**
- **Responsable:** Fabian Rojas
- **Implementación Requerida:**
  - Campo SQL: `cantidad NUMERIC(5,2) NOT NULL`
  - DTO con validación: `@Positive` y `@NotNull`
  - Rechazar valores negativos o cero
## 🗂️ MÓDULO 3: GESTOR DE CATÁLOGO - ADMIN
**Responsable:** Jhamil Peña

### RN11 - Nombres únicos en `catalogo_metas`
- **Regla:** No pueden existir dos catalogo_metas con el mismo nombre (restricción UNIQUE).
- **Historia de Usuario:** US-11 – Crear Meta del Catálogo
- **Responsable:** Jhamil Peña
- **Nota:** En la implementación, `catalogo_metas` corresponde a tablas `planes` y `rutinas`
- **Implementación Requerida:**
```sql
ALTER TABLE planes ADD CONSTRAINT uk_planes_nombre UNIQUE (nombre);
ALTER TABLE rutinas ADD CONSTRAINT uk_rutinas_nombre UNIQUE (nombre);
```

### RN12 - Solo asignar etiquetas existentes
- **Regla:** El sistema solo debe permitir asignar etiquetas a una meta (etiquetas_metas) si estas ya existen en la tabla etiquetas.
- **Historia de Usuario:** US-12 – Gestionar Meta
- **Responsable:** Jhamil Peña
- **Implementación Requerida:**
  - FK en tabla `plan_etiquetas` → `etiquetas.id`
  - FK en tabla `rutina_etiquetas` → `etiquetas.id`
  - Validar que etiqueta existe antes de asignar

### RN13 - Series y repeticiones numéricas positivas
- **Regla:** Al ensamblar una catalogo_rutinas, los campos series y repeticiones deben validarse como valores numéricos enteros y positivos.
- **Historia de Usuario:** US-14 – Ensamblar Rutinas
- **Responsable:** Jhamil Peña
- **Implementación Requerida:**
  - Campos SQL: `series INTEGER NOT NULL`, `repeticiones INTEGER NOT NULL`
  - Validación DTO: `@Positive @NotNull`
  - Rechazar valores negativos o cero

### RN14 - No eliminar meta si tiene clientes activos
- **Regla:** El sistema debe impedir la eliminación (DELETE) de una catalogo_metas si esta tiene clientes activos (estado = 'activo' en usuario_metas_asignadas).
- **Historia de Usuario:** US-15 – Eliminar Meta
- **Responsable:** Jhamil Peña
- **Prioridad:** 🟡 **ALTA**
- **Implementación Requerida:**
```java
@Service
public class PlanService {
    public void eliminar(Long id) {
        long usuariosActivos = usuarioPlanRepository
            .countByPlanIdAndEstado(id, EstadoAsignacion.ACTIVO);
        
## 🔍 MÓDULO 4: EXPLORACIÓN Y ACTIVACIÓN - CLIENTE
**Responsables:** Gonzalo Huaranga (RN15-RN17), Victor Carranza (RN18-RN19)

### RN15 - Catálogo muestra metas sugeridas según perfil
- **Regla:** La vista del "Catálogo" debe mostrar metas sugeridas (basadas en etiquetas) si el usuario_perfil_salud existe; de lo contrario, debe mostrar la lista general.
- **Historia de Usuario:** US-16 – Ver Catálogo (Cliente)
- **Responsable:** Gonzalo Huaranga
- **Implementación Requerida:**
  - Endpoint `GET /api/v1/planes/catalogo?sugeridos=true`
  - Query que filtre por etiquetas de `usuario_perfil_salud.objetivo_actual`
  - Si usuario no tiene perfil, mostrar lista completa

### RN16 - Ocultar del catálogo planes con alérgenos del usuario
- **Regla:** El sistema debe OCULTAR del catálogo (US-16) cualquier meta que contenga ingredientes o ejercicios que choquen con las etiquetas de alergia o condición del usuario_perfil_salud.
- **Historias de Usuario:** US-16 – Ver Catálogo (Cliente), US-04 – Editar perfil de salud
- **Responsable:** Gonzalo Huaranga
- **Prioridad:** 🚨 **CRÍTICA - SEGURIDAD DE SALUD**
- **Implementación Requerida:**
  - Query que identifique ingredientes/ejercicios con etiquetas de alergia del usuario
  - Excluir planes que contengan esos ingredientes del catálogo
  - Ejemplo: Usuario alérgico a "Nueces" NO ve planes con ingredientes etiquetados "Nueces"

### RN17 - No duplicar el mismo plan/rutina activo
- **Regla:** Un usuario NO puede activar el MISMO plan o rutina si ya lo tiene ACTIVO. Puede tener múltiples planes/rutinas diferentes activos simultáneamente, pero no duplicados del mismo.
- **Historia de Usuario:** US-18 – Activar una Meta
- **Responsable:** Gonzalo Huaranga
- **Prioridad:** 🟡 **ALTA**
- **Implementación Requerida:**
```java
// Para PLANES
Optional<UsuarioPlan> mismoPlanActivo = usuarioPlanRepository
    .findByPerfilUsuarioIdAndPlanIdAndEstado(
        perfilId, 
        planId, 
        EstadoAsignacion.ACTIVO
    );

if (mismoPlanActivo.isPresent()) {
    throw new BusinessException(
        "Ya tienes este plan activo. Debes pausarlo/cancelarlo para reiniciarlo."
    );
}

// Para RUTINAS
Optional<UsuarioRutina> mismaRutinaActiva = usuarioRutinaRepository
    .findByPerfilUsuarioIdAndRutinaIdAndEstado(
        perfilId, 
        rutinaId, 
        EstadoAsignacion.ACTIVO
    );

if (mismaRutinaActiva.isPresent()) {
    throw new BusinessException(
        "Ya tienes esta rutina activa. Debes pausarla/cancelarla para reiniciarla."
    );
}
```

**Escenarios válidos:**
- ✅ Plan A (ACTIVO) + Plan B (ACTIVO) → Diferentes planes OK
- ✅ Rutina X (ACTIVO) + Rutina Y (ACTIVO) → Diferentes rutinas OK
- ✅ Plan A (ACTIVO) + Plan A (PAUSADO) → Mismo plan, diferente estado OK
- ❌ Plan A (ACTIVO) + Plan A (ACTIVO) → Duplicado NO permitido

### RN18 - Proponer reemplazo al activar el mismo plan/rutina
- **Regla:** Si un usuario intenta activar el MISMO plan/rutina que ya tiene ACTIVO, el sistema debe proponer pausarlo/cancelarlo primero y luego reiniciarlo.
- **Historia de Usuario:** US-18 – Activar una Meta
- **Responsable:** Victor Carranza
- **Prioridad:** 🟡 **ALTA**
- **Implementación Requerida:**
  - Endpoint debe verificar duplicados según RN17
  - Si existe, retornar HTTP 409 Conflict con mensaje específico
  - Frontend debe mostrar diálogo: "Ya tienes este plan activo en el día X. ¿Deseas pausarlo y reiniciarlo desde el día 1?"
  - Si usuario acepta: pausar instancia actual, crear nueva instancia como ACTIVO

### RN19 - No pausar/reanudar meta completada/cancelada
- **Regla:** Un cliente no puede "Pausar" o "Reanudar" una meta que ya está en un estado final ('completado' o 'cancelado').
- **Historia de Usuario:** US-19 – Pausar/Reanudar Meta
- **Responsable:** Victor Carranza
- **Implementación Requerida:**
  - Validar que estado actual sea ACTIVO o PAUSADO antes de cambiar
  - Si estado es COMPLETADO o CANCELADO, retornar HTTP 400 Bad Request
  - Transiciones permitidas: ACTIVO ↔ PAUSADO
}
```

### RN18 - Proponer reemplazo al activar nueva meta
## 📈 MÓDULO 5: SEGUIMIENTO DE PROGRESO - CLIENTE
**Responsable:** Leonel Alzamora

### RN20 - Mostrar checks en actividades completadas
- **Regla:** La lista de "Mi Plan" (US-21) debe mostrar visualmente (ej. con un check) las catalogo_actividades que ya tienen un registro en usuario_actividades_progreso.
- **Historia de Usuario:** US-21 – Ver Actividades de mi Plan
- **Responsable:** Leonel Alzamora
- **Nota:** El sistema usa `registros_comidas` y `registros_ejercicios` (no checklist de actividades)
- **Implementación Requerida:**
  - DTO de respuesta debe incluir campo `completado: boolean`
  - Marcar `completado=true` si existe registro para esa fecha
  - Frontend muestra ícono visual (✓) en actividades completadas

### RN21 - No marcar actividad si plan pausado
- **Regla:** Un cliente no puede marcar una actividad como 'completada' si su meta asignada está estado = 'pausado'.
- **Historias de Usuario:** US-22 – Marcar Actividad como Completada, US-21 – Ver Actividades
- **Responsable:** Leonel Alzamora
- **Implementación Requerida:**
  - `RegistroComidaService` debe validar estado del plan antes de crear registro
  - `RegistroEjercicioService` debe validar estado del plan antes de crear registro
  - Si estado = PAUSADO, retornar HTTP 400 Bad Request con mensaje claro

### RN22 - Validar mediciones numéricas en rango
- **Regla:** El registro de una nueva medición (usuario_historial_medidas) debe validarse como numérico y dentro de un rango realista (ej. > 0 y < 600).
- **Historia de Usuario:** US-24 – Registrar y Ver Mediciones
- **Responsable:** Leonel Alzamora
- **Implementación Requerida:**
```java
@DecimalMin(value = "20.0", message = "El peso debe ser mayor a 20 kg")
@DecimalMax(value = "600.0", message = "El peso debe ser menor a 600 kg")
private BigDecimal peso;

@DecimalMin(value = "50.0", message = "La altura debe ser mayor a 50 cm")
@DecimalMax(value = "300.0", message = "La altura debe ser menor a 300 cm")
private BigDecimal altura;
```

### RN23 - Gráfico requiere 2+ registros
- **Regla:** El gráfico de progreso (evolución de peso) solo debe generarse si existen 2 o más registros en usuario_historial_medidas para ese cliente.
- **Historia de Usuario:** US-25 – Ver Gráfico de Progreso
- **Responsable:** Leonel Alzamora
- **Implementación Requerida:**
  - Query `COUNT(*)` en `usuario_historial_medidas` por usuario
  - Si count < 2, retornar HTTP 400 Bad Request
  - Mensaje: "Se necesitan al menos 2 mediciones para generar el gráfico"

### RN24 - Reporte PDF con datos del usuario
- **Regla:** El reporte PDF debe generarse con los datos del usuario (progreso, mediciones) y estar en un formato estándar legible.
- **Historia de Usuario:** US-25 – Ver Gráfico de Progreso y Reporte
- **Responsable:** Leonel Alzamora
- **Prioridad:** 🟡 **MEDIA**
- **Implementación Requerida:**
  - Librería: iText o Apache PDFBox
  - Endpoint: `GET /api/v1/progreso/reporte/pdf`
  - Incluir: datos del usuario, gráfico de peso, resumen de actividades completadas
  - Formato: PDF/A estándar, máximo 5 MBbe ser menor a 300 cm")
private BigDecimal altura;
```

### RN23 - Gráfico requiere 2+ registros
- **User Story:** US-25
- **Implementación:** Validación en frontend/servicio
- **Estado:** ✅ **CUMPLE**

### RN24 - Reporte PDF con datos del usuario
- **User Story:** US-25
- **Implementación:** No implementado - pendiente
- **Estado:** ❌ **NO CUMPLE**
- **Impacto:** Usuario no puede exportar su progreso

---

## 🎯 REGLAS TRANSVERSALES

Estas reglas aplican a TODOS los módulos del sistema.

### RN25 - Integridad Nutricional (Cálculo Automático)

**Regla:** Las calorías consumidas DEBEN calcularse automáticamente, NUNCA entrada manual.

**Fórmula de Cálculo:**
```
calorias_consumidas = SUM(
    ingrediente.energia * 
    receta.cantidad_ingrediente / 100 * 
    registro.porciones
) FOR ALL ingredientes IN comida
```

**Implementación Requerida:**
- Service layer calcula calorías basado en ingredientes
- No permitir campo manual de calorías en DTOs de registro
- Recalcular si usuario cambia cantidad de porciones

---

### RN26 - Estado de Asignaciones (Transiciones Válidas)

**Estados permitidos:** `ACTIVO | COMPLETADO | CANCELADO | PAUSADO`

**Transiciones Válidas:**
- ✅ `ACTIVO → COMPLETADO` (al llegar a duracion_dias)
- ✅ `ACTIVO → CANCELADO` (usuario abandona)
- ✅ `ACTIVO → PAUSADO` (usuario pausa temporalmente)
- ✅ `PAUSADO → ACTIVO` (usuario reanuda)

**Transiciones PROHIBIDAS:**
- ❌ `COMPLETADO → ACTIVO` (no reactivar plan completado)
- ❌ `CANCELADO → ACTIVO` (crear nueva asignación en su lugar)
- ❌ `COMPLETADO → PAUSADO` (no tiene sentido)
- ❌ `CANCELADO → PAUSADO` (no tiene sentido)

**Implementación Requerida:**
- Validar transición antes de cambiar estado
- Retornar HTTP 400 si transición inválida
- Enum con máquina de estados en Service

---

### RN27 - Unidades de Medida Consistentes

**Regla:** TODO peso/altura debe almacenarse en KG/CM en base de datos (unidad estándar).

**Flujo de Conversión:**
1. **Input:** Usuario envía en su unidad preferida (KG o LBS)
2. **Storage:** Convertir a KG/CM para almacenar
3. **Output:** Convertir de KG/CM a unidad preferida del usuario

**Implementación Requerida:**
- Métodos utilitarios: `convertirAKg()`, `convertirDesdeLbs()`, etc.
- DTOs aceptan valor + unidad
- Base de datos solo KG/CM
- Response DTOs usan preferencia del usuario

---

### RN28 - Soft Delete para Planes y Rutinas

**Regla:** Planes y rutinas NO se eliminan físicamente (DELETE), se marcan como inactivos (UPDATE).

**Implementación Requerida:**
```java
// NO hacer esto:
planRepository.deleteById(id);

// Hacer esto:
plan.setActivo(false);
planRepository.save(plan);
```

**Razón:** Preservar historial de usuarios que usaron el plan, incluso si el admin lo "elimina".

**Consultas:** Filtrar por `activo = true` en queries públicas.

---

## 🔒 REGLAS DE SEGURIDAD Y PROTECCIÓN

### RN29 - Rate Limiting para Protección DDoS
- **Regla:** El sistema debe limitar la cantidad de peticiones por usuario/IP para prevenir ataques de denegación de servicio (DDoS) y brute force en endpoints críticos.
- **Módulos Afectados:** Módulo 1 (Autenticación)
- **Prioridad:** 🚨 **CRÍTICA - SEGURIDAD**
- **Implementación Requerida:**
```java
// Configuración con Spring Cloud Resilience4j
@Configuration
public class RateLimitConfig {
    @Bean
    public RateLimiter loginRateLimiter() {
        return RateLimiter.of("login", RateLimiterConfig.custom()
            .limitRefreshPeriod(Duration.ofMinutes(1))
            .limitForPeriod(5) // 5 intentos por minuto
            .timeoutDuration(Duration.ofSeconds(0))
            .build());
    }
}

@PostMapping("/login")
@RateLimiter(name = "login", fallbackMethod = "loginFallback")
public ResponseEntity<?> login(@RequestBody LoginRequest request) {
    // Lógica de login
}

public ResponseEntity<?> loginFallback(Exception e) {
    return ResponseEntity.status(429)
        .body("Demasiados intentos de login. Intenta de nuevo en 1 minuto.");
}
```
- **Límites Recomendados:**
  - Login: 5 intentos/minuto por IP
  - Registro: 3 intentos/minuto por IP
  - Endpoints generales: 100 requests/minuto por usuario autenticado
- **Estado:** ❌ **NO IMPLEMENTADO**

### ✅ RN30 - Validación de Formato de Email
- **Regla:** El sistema debe validar que el email proporcionado tiene un formato válido RFC 5322 antes de crear una cuenta.
- **Módulos Afectados:** Módulo 1 (Gestión de Cuentas)
- **Prioridad:** 🔴 **ALTA - SEGURIDAD**
- **Implementación:**
  - Regex RFC 5322 en `RegisterRequest.email` con anotación `@Email`
  - Método `validarEmail()` en `AuthService` con verificación DNS usando `InetAddress.getByName()`
  - Validación ejecutada antes de crear cuenta en `register()`
- **Ubicación:** `dto/auth/RegisterRequest.java`, `service/AuthService.java`
- **Estado:** ✅ **IMPLEMENTADO** (Noviembre 2025)

### ✅ RN31 - Política de Contraseñas Robusta
- **Regla:** Las contraseñas deben cumplir con estándares de seguridad robustos para prevenir cuentas vulnerables.
- **Módulos Afectados:** Módulo 1 (Gestión de Cuentas)
- **Prioridad:** 🚨 **CRÍTICA - SEGURIDAD**
- **Implementación:**
  - Regex pattern en `RegisterRequest.password` con `@Pattern` y `@Size(min=12)`
  - Requisitos: 12+ caracteres, mayúscula, minúscula, número, símbolo especial
  - Método `validarPasswordSegura()` en `AuthService` que verifica:
    - No contraseñas comunes (blacklist)
    - No contiene email del usuario
  - Lista blacklist: "password1234", "admin1234567", "123456789012", "qwerty123456", "letmein12345"
- **Ubicación:** `dto/auth/RegisterRequest.java`, `service/AuthService.java`
- **Estado:** ✅ **IMPLEMENTADO** (Noviembre 2025)

---

## 🛡️ REGLAS DE INTEGRIDAD DE DATOS

### ✅ RN32 - Validación Cruzada de Alérgenos en Asignación
- **Regla:** Cuando un usuario activa un plan, el sistema debe validar que el plan NO contenga ingredientes con etiquetas de alérgenos del perfil de salud del usuario.
- **Módulos Afectados:** Módulo 4 (Exploración y Activación)
- **Prioridad:** 🚨 **CRÍTICA - SEGURIDAD DE SALUD**
- **Implementación:**
  - Método `validarAlergenosUsuario()` en `UsuarioPlanService` ejecutado antes de activar plan
  - Query `findEtiquetasAlergenosByPerfilUsuarioId()` en `UsuarioEtiquetasSaludRepository` para obtener alergias del usuario
  - Query `findEtiquetasIngredientesByPlanId()` en `PlanRepository` usando 5-join query:
    ```
    Plan → PlanDia → Comida → ComidaIngrediente → Ingrediente → Etiqueta
    ```
  - Validación por intersección de conjuntos (alergias usuario ∩ etiquetas ingredientes plan)
  - Si hay coincidencias, lanza `BusinessException` con nombres de alérgenos usando `EtiquetaRepository.findNombresByIds()`
  - Previene activación de planes incompatibles con salud del usuario
- **Ubicación:** 
  - `service/UsuarioPlanService.java`
  - `repository/UsuarioEtiquetasSaludRepository.java`
  - `repository/PlanRepository.java`
  - `repository/EtiquetaRepository.java`
- **Estado:** ✅ **IMPLEMENTADO** (Noviembre 2025)

### RN33 - Validación Lógica de Fechas en Registros
- **Regla:** Los registros de progreso (comidas, ejercicios, mediciones) deben validar que la fecha no sea futura y sea posterior o igual a la fecha de inicio del plan activo.
- **Módulos Afectados:** Módulo 5 (Seguimiento de Progreso)
- **Prioridad:** 🔴 **ALTA - INTEGRIDAD**
- **Implementación Requerida:**
```java
@Service
public class RegistroComidaService {
    
    public void registrarComida(RegistroComidaRequest request, Long perfilUsuarioId) {
        LocalDate fechaRegistro = request.getFecha();
        LocalDate hoy = LocalDate.now();
        
        // 1. No permitir fechas futuras
        if (fechaRegistro.isAfter(hoy)) {
            throw new ValidationException(
                "No puedes registrar comidas en el futuro. Fecha máxima: " + hoy
            );
        }
        
        // 2. Verificar que fecha esté dentro del rango del plan activo
        UsuarioPlan planActivo = usuarioPlanRepository
            .findActivoByPerfilUsuarioId(perfilUsuarioId)
            .orElseThrow(() -> new BusinessException("No tienes un plan activo"));
        
        LocalDate inicioPlan = planActivo.getFechaInicio();
        if (fechaRegistro.isBefore(inicioPlan)) {
            throw new ValidationException(
                "La fecha del registro debe ser posterior al inicio de tu plan (" 
                + inicioPlan + ")"
            );
        }
        
        // 3. Validar que no hayan pasado más días de la duración del plan
        int diasTranscurridos = (int) ChronoUnit.DAYS.between(inicioPlan, fechaRegistro);
        if (diasTranscurridos >= planActivo.getPlan().getDuracionDias()) {
            throw new ValidationException(
                "Tu plan tiene duración de " + planActivo.getPlan().getDuracionDias() 
                + " días. Este registro excede la duración."
            );
        }
        
        // Proceder con registro...
    }
}
```
- **Validaciones DTO:**
```java
@PastOrPresent(message = "La fecha no puede ser futura")
@NotNull(message = "La fecha es obligatoria")
private LocalDate fecha;
```
- **Estado:** ⚠️ **PARCIAL** (solo valida @PastOrPresent, falta validación contra plan)

### RN34 - Límite de Registros Diarios para Prevenir Spam
- **Regla:** Un usuario no puede registrar más de 10 comidas ni más de 10 ejercicios en un mismo día para prevenir spam/errores.
- **Módulos Afectados:** Módulo 5 (Seguimiento de Progreso)
- **Prioridad:** 🟡 **MEDIA - INTEGRIDAD**
- **Implementación Requerida:**
```java
@Service
public class RegistroComidaService {
    
    private static final int MAX_REGISTROS_DIA = 10;
    
    public void registrarComida(RegistroComidaRequest request, Long perfilUsuarioId) {
        LocalDate fecha = request.getFecha();
        
        // Contar registros existentes para esta fecha
        long registrosHoy = registroComidaRepository
            .countByPerfilUsuarioIdAndFecha(perfilUsuarioId, fecha);
        
        if (registrosHoy >= MAX_REGISTROS_DIA) {
            throw new ValidationException(
                "Has alcanzado el límite de " + MAX_REGISTROS_DIA 
                + " comidas por día. Si cometiste un error, elimina un registro anterior."
            );
        }
        
        // Proceder con registro...
    }
}

// Query en Repository
@Query("SELECT COUNT(r) FROM RegistroComida r " +
       "WHERE r.perfilUsuarioId = :perfilUsuarioId " +
       "AND r.fecha = :fecha")
long countByPerfilUsuarioIdAndFecha(
    @Param("perfilUsuarioId") Long perfilUsuarioId, 
    @Param("fecha") LocalDate fecha
);
```
- **Estado:** ❌ **NO IMPLEMENTADO**

---

## 📢 REGLAS DE EXPERIENCIA DE USUARIO

### RN35 - Sistema de Notificaciones de Progreso
- **Regla:** El sistema debe enviar notificaciones al usuario para mantener engagement: recordatorios diarios, celebración de logros, alertas de desviación de metas.
- **Módulos Afectados:** Módulo 5 (Seguimiento de Progreso)
- **Prioridad:** 🟡 **MEDIA - UX**
- **Tipos de Notificaciones:**
  - **Recordatorio Diario:** "¡Recuerda registrar tus comidas de hoy!" (8 AM)
  - **Celebración Logro:** "🎉 ¡Completaste 7 días seguidos!" (al alcanzar streak)
  - **Alerta Desviación:** "⚠️ Hoy consumiste 500 calorías más que tu meta" (al final del día)
  - **Plan Próximo a Completar:** "¡Te faltan 3 días para completar tu plan!"
- **Implementación Requerida:**
```java
@Service
public class NotificacionService {
    
    @Scheduled(cron = "0 0 8 * * *") // Todos los días a las 8 AM
    public void enviarRecordatoriosDiarios() {
        List<PerfilUsuario> usuariosActivos = perfilUsuarioRepository
            .findWithPlanesActivos();
        
        for (PerfilUsuario usuario : usuariosActivos) {
            // Verificar si ya registró algo hoy
            boolean registroHoy = registroComidaRepository
                .existsByPerfilUsuarioIdAndFecha(usuario.getId(), LocalDate.now());
            
            if (!registroHoy) {
                // Enviar notificación push/email
                notificationSender.send(usuario.getCuentaAuth().getEmail(),
                    "Recordatorio NutriTrack",
                    "¡Recuerda registrar tus comidas de hoy! 🍎"
                );
            }
        }
    }
    
    public void celebrarLogro(Long perfilUsuarioId, TipoLogro logro) {
        PerfilUsuario usuario = perfilUsuarioRepository.findById(perfilUsuarioId)
            .orElseThrow();
        
        String mensaje = switch (logro) {
            case STREAK_7_DIAS -> "🎉 ¡Increíble! Completaste 7 días seguidos registrando tu progreso.";
            case PLAN_COMPLETADO -> "🏆 ¡Felicitaciones! Completaste tu plan nutricional.";
            case META_PESO -> "🎯 ¡Lograste tu meta de peso! Eres disciplinado.";
            default -> "¡Gran trabajo en NutriTrack!";
        };
        
        notificationSender.send(usuario.getCuentaAuth().getEmail(),
            "¡Logro Desbloqueado!", mensaje
        );
    }
}

enum TipoLogro {
    STREAK_7_DIAS, PLAN_COMPLETADO, META_PESO
}
```
- **Estado:** ❌ **NO IMPLEMENTADO**

---

## 📊 REGLAS DE AUDITORÍA Y CUMPLIMIENTO

### RN36 - Versionado de Planes para Trazabilidad
- **Regla:** Cuando un usuario activa un plan, el sistema debe crear una copia snapshot (JSON) del contenido del plan en ese momento para preservar el historial incluso si el admin modifica el plan original.
- **Módulos Afectados:** Módulo 4 (Exploración y Activación)
- **Prioridad:** 🟡 **MEDIA - AUDITORÍA**
- **Implementación Requerida:**
```java
@Entity
@Table(name = "usuario_planes")
public class UsuarioPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // ... otros campos ...
    
    @Column(columnDefinition = "JSONB")
    @Type(JsonBinaryType.class)
    private String planSnapshot; // Copia JSON del plan al momento de activación
    
    private Integer versionPlan; // Número de versión del plan
}

@Service
public class UsuarioPlanService {
    
    public UsuarioPlan activarPlan(Long perfilUsuarioId, Long planId) {
        Plan plan = planRepository.findById(planId)
            .orElseThrow(() -> new NotFoundException("Plan no encontrado"));
        
        // Crear snapshot JSON del plan
        PlanSnapshotDTO snapshot = new PlanSnapshotDTO();
        snapshot.setNombre(plan.getNombre());
        snapshot.setDescripcion(plan.getDescripcion());
        snapshot.setDuracionDias(plan.getDuracionDias());
        snapshot.setCaloriasDiarias(plan.getCaloriasDiarias());
        snapshot.setComidas(plan.getComidas()); // Incluir todas las comidas
        
        String snapshotJson = objectMapper.writeValueAsString(snapshot);
        
        UsuarioPlan usuarioPlan = new UsuarioPlan();
        usuarioPlan.setPlanId(planId);
        usuarioPlan.setPlanSnapshot(snapshotJson);
        usuarioPlan.setVersionPlan(plan.getVersion()); // Incrementar en cada modificación
        usuarioPlan.setEstado(EstadoAsignacion.ACTIVO);
        
        return usuarioPlanRepository.save(usuarioPlan);
    }
}
```
- **Beneficios:**
  - Historial inmutable de qué plan exacto siguió el usuario
  - Auditoría: comparar progreso contra plan original, no contra versión actual
  - Cumplimiento: evidencia de qué se recomendó al usuario
- **Estado:** ❌ **NO IMPLEMENTADO**

### RN37 - Exportación GDPR de Datos de Usuario
- **Regla:** Un usuario debe poder descargar TODOS sus datos personales en formato portable (JSON/PDF) para cumplir con normativas de protección de datos (GDPR, LGPD).
- **Módulos Afectados:** Módulo 1 (Gestión de Cuentas), Transversal
- **Prioridad:** 🔴 **ALTA - CUMPLIMIENTO LEGAL**
- **Implementación Requerida:**
```java
@RestController
@RequestMapping("/api/v1/cuenta")
public class CuentaController {
    
    @GetMapping("/exportar-datos")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ExportacionDatosDTO> exportarDatos(Authentication auth) {
        String email = auth.getName();
        
        ExportacionDatosDTO exportacion = cuentaService.exportarDatosCompletos(email);
        
        return ResponseEntity.ok()
            .header("Content-Disposition", 
                "attachment; filename=nutritrack_datos_" + LocalDate.now() + ".json")
            .body(exportacion);
    }
}

@Service
public class CuentaService {
    
    public ExportacionDatosDTO exportarDatosCompletos(String email) {
        CuentaAuth cuenta = cuentaAuthRepository.findByEmail(email)
            .orElseThrow();
        PerfilUsuario perfil = cuenta.getPerfilUsuario();
        
        ExportacionDatosDTO dto = new ExportacionDatosDTO();
        
        // 1. Datos de cuenta
        dto.setEmail(cuenta.getEmail());
        dto.setRol(cuenta.getRol().name());
        dto.setFechaCreacion(cuenta.getFechaCreacion());
        
        // 2. Datos de perfil
        dto.setPerfil(new PerfilDTO(
            perfil.getNombre(),
            perfil.getApellido(),
            perfil.getFechaNacimiento(),
            perfil.getGenero(),
            perfil.getUnidadesMedida()
        ));
        
        // 3. Etiquetas de salud (objetivos, alergias)
        dto.setEtiquetasSalud(usuarioEtiquetaSaludRepository
            .findByPerfilUsuarioId(perfil.getId()));
        
        // 4. Historial de planes
        dto.setPlanesAsignados(usuarioPlanRepository
            .findByPerfilUsuarioId(perfil.getId()));
        
        // 5. Registros de comidas
        dto.setRegistrosComidas(registroComidaRepository
            .findByPerfilUsuarioId(perfil.getId()));
        
        // 6. Registros de ejercicios
        dto.setRegistrosEjercicios(registroEjercicioRepository
            .findByPerfilUsuarioId(perfil.getId()));
        
        // 7. Historial de mediciones
        dto.setHistorialMediciones(medicionRepository
            .findByPerfilUsuarioId(perfil.getId()));
        
        return dto;
    }
}
```
- **Contenido Exportado:**
  - Datos de cuenta (email, rol, fecha creación)
  - Perfil personal (nombre, apellido, fecha nacimiento)
  - Objetivos y alergias
  - Historial completo de planes y rutinas
  - Todos los registros de comidas y ejercicios
  - Historial de mediciones y peso
- **Estado:** ❌ **NO IMPLEMENTADO**

---

## ⚡ REGLAS DE RENDIMIENTO Y ESCALABILIDAD

### RN38 - Caché de Consultas de Catálogo
- **Regla:** Las consultas de catálogo público (planes, rutinas, ingredientes, ejercicios) deben estar cacheadas para reducir carga en base de datos.
- **Módulos Afectados:** Módulo 3 (Gestor de Catálogo), Módulo 4 (Exploración)
- **Prioridad:** 🟢 **BAJA - OPTIMIZACIÓN**
- **Implementación Requerida:**
```java
@Configuration
@EnableCaching
public class CacheConfig {
    
    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager(
            "planes", "rutinas", "ingredientes", "ejercicios"
        );
    }
}

@Service
public class PlanService {
    
    @Cacheable(value = "planes", key = "'catalogo'", unless = "#result.isEmpty()")
    public List<PlanDTO> verCatalogo() {
        return planRepository.findAllActivosWithComidas();
    }
    
    @CacheEvict(value = "planes", allEntries = true)
    public Plan crearPlan(CrearPlanRequest request) {
        // Crear plan... cache se invalida automáticamente
    }
    
    @CacheEvict(value = "planes", allEntries = true)
    public Plan actualizarPlan(Long id, ActualizarPlanRequest request) {
        // Actualizar plan... cache se invalida
    }
}
```
- **Estrategia:**
  - TTL: 5 minutos para catálogos públicos
  - Invalidación: al crear/modificar/eliminar entidad
  - Caché por usuario: para catálogos personalizados (con filtro de alergias)
- **Estado:** ❌ **NO IMPLEMENTADO**

### RN39 - Búsqueda Full-Text en Catálogos
- **Regla:** Los usuarios deben poder buscar planes y rutinas por nombre, descripción o etiquetas usando búsqueda de texto completo.
- **Módulos Afectados:** Módulo 4 (Exploración y Activación)
- **Prioridad:** 🟡 **MEDIA - UX**
- **Implementación Requerida:**
```java
@Repository
public interface PlanRepository extends JpaRepository<Plan, Long> {
    
    @Query("""
        SELECT DISTINCT p FROM Plan p
        LEFT JOIN p.etiquetas e
        WHERE p.activo = true
        AND (
            LOWER(p.nombre) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
            OR LOWER(p.descripcion) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
            OR LOWER(e.nombre) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
        )
        ORDER BY p.nombre
    """)
    List<Plan> buscarEnCatalogo(@Param("searchTerm") String searchTerm);
}

@RestController
@RequestMapping("/api/v1/planes")
public class PlanController {
    
    @GetMapping("/buscar")
    public ResponseEntity<List<PlanDTO>> buscar(
        @RequestParam String q, // query
        @RequestParam(required = false) String categoria
    ) {
        if (q.length() < 3) {
            throw new ValidationException("Búsqueda debe tener mínimo 3 caracteres");
        }
        
        List<Plan> resultados = planRepository.buscarEnCatalogo(q);
        
        // Filtrar por categoría si se especifica
        if (categoria != null) {
            resultados = resultados.stream()
                .filter(p -> p.getEtiquetas().stream()
                    .anyMatch(e -> e.getCategoria().equals(categoria)))
                .collect(Collectors.toList());
        }
        
        return ResponseEntity.ok(
            resultados.stream()
                .map(planMapper::toDTO)
                .collect(Collectors.toList())
        );
    }
}
```
- **Características:**
  - Búsqueda case-insensitive
  - Búsqueda en nombre, descripción y etiquetas
  - Mínimo 3 caracteres para buscar
  - Filtro opcional por categoría de etiqueta
- **Estado:** ❌ **NO IMPLEMENTADO**

---

**Fecha de Última Actualización:** 5 de Noviembre, 2025  
**Versión del Documento:** 2.1  
**Cambios en esta Versión:**
- ✅ **IMPLEMENTADAS:** RN30 (Email RFC 5322 + DNS), RN31 (Contraseña 12+ chars + complejidad), RN32 (Validación alérgenos)
- Sprint 1 de seguridad completado parcialmente (3/5 reglas)
- Cobertura de implementación incrementada de 61.5% a 69.2%
- Total: **39 reglas de negocio** (27 implementadas, 12 pendientes)

---

## 📊 RESUMEN DE COBERTURA

### Estado de Implementación por Prioridad

| Prioridad | Implementadas | No Implementadas | Total |
|-----------|---------------|------------------|-------|
| 🚨 **Crítica** | 4 (RN16, RN30, RN31, RN32) | 2 (RN29, RN37) | 6 |
| 🔴 **Alta** | 8 (RN07-RN09, RN14, RN17-RN19) | 1 (RN33) | 9 |
| 🟡 **Media** | 14 (RN01-RN06, RN10-RN13, RN20-RN23, RN25-RN28) | 3 (RN24, RN34-RN36) | 17 |
| 🟢 **Baja** | 0 | 2 (RN38-RN39) | 2 |
| **TOTAL** | **27/39** (69.2%) | **12/39** (30.8%) | **39** |

### Roadmap de Implementación Recomendado

**Sprint 1 - Seguridad (1 semana):** ✅ **COMPLETADO (3/5)**
- ❌ RN29: Rate Limiting (4 horas) - PENDIENTE
- ✅ RN30: Validación Email con DNS (2 horas) - **COMPLETADO**
- ✅ RN31: Política Contraseñas Robusta (2 horas) - **COMPLETADO**
- ✅ RN32: Validación Cruzada Alérgenos (6 horas) - **COMPLETADO**
- ❌ RN37: Exportación GDPR (6 horas) - PENDIENTE

**Sprint 2 - Integridad (1 semana):**
- ❌ RN33: Validación Lógica Fechas (4 horas)
- ❌ RN34: Límites Registros Diarios (3 horas)
- ❌ RN36: Versionado de Planes (8 horas)

**Sprint 3 - Experiencia Usuario (1 semana):**
- ❌ RN35: Sistema Notificaciones (16 horas)
- ❌ RN24: Reporte PDF (8 horas)

**Sprint 4 - Optimización (1 semana):**
- ❌ RN38: Caché de Catálogos (4 horas)
- ❌ RN39: Búsqueda Full-Text (6 horas)

**Total Estimado:** 4 semanas (69 horas de desarrollo)  
**Completado:** 10 horas (14.5%)  
**Pendiente:** 59 horas (85.5%)

---

> **💡 Instrucciones para el Equipo:**  
> - Cada integrante es responsable de implementar las reglas asignadas a su módulo
> - Las reglas marcadas como 🔴/🚨 tienen prioridad máxima
> - Antes de marcar una tarea como completada, verificar que cumple EXACTAMENTE con la regla especificada
> - Consultar con el equipo si una regla entra en conflicto con otra funcionalidad
> - **Las nuevas reglas RN29-RN39 son recomendaciones para alcanzar calidad de producción del 100%**
