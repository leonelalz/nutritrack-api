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

**Fecha de Creación:** 4 de Noviembre, 2025  
**Versión del Documento:** 1.0  
**Propósito:** Documento fundacional - Establece las reglas de negocio que DEBEN implementarse

---

> **💡 Instrucciones para el Equipo:**  
> - Cada integrante es responsable de implementar las reglas asignadas a su módulo
> - Las reglas marcadas como 🔴/🚨 tienen prioridad máxima
> - Antes de marcar una tarea como completada, verificar que cumple EXACTAMENTE con la regla especificada
> - Consultar con el equipo si una regla entra en conflicto con otra funcionalidad
