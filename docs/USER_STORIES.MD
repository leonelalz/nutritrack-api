# 📋 USER STORIES - NUTRITRACK

> **Documento Fundacional del Proyecto**  
> Este documento establece las 25 User Stories que definen la funcionalidad del sistema NutriTrack.  
> Cada historia describe QUÉ debe hacer el sistema desde la perspectiva del usuario.

---

## 📊 ORGANIZACIÓN POR MÓDULOS

### Distribución de User Stories

| Módulo | User Stories | Total | Descripción |
|--------|--------------|-------|-------------|
| **Módulo 1** | US-01 a US-05 | 5 historias | Gestión de Cuentas y Perfil |
| **Módulo 2** | US-06 a US-10 | 5 historias | Biblioteca de Contenido (Admin) |
| **Módulo 3** | US-11 a US-15 | 5 historias | Gestor de Catálogo - Planes y Rutinas (Admin) |
| **Módulo 4** | US-16 a US-20 | 5 historias | Exploración y Activación (Cliente) |
| **Módulo 5** | US-21 a US-25 | 5 historias | Seguimiento de Progreso (Cliente) |
| **TOTAL** | | **25 historias** | Funcionalidad completa del sistema |

---

## 📋 MÓDULO 1: GESTIÓN DE CUENTAS Y PERFIL

### US-01 - Crear Cuenta
**Como** usuario nuevo  
**Quiero** registrarme en el sistema  
**Para** acceder a las funcionalidades de NutriTrack

**Criterios de Aceptación:**
- Usuario proporciona: email, contraseña, nombre completo
- Email debe ser único en el sistema
- Contraseña debe cumplir requisitos de seguridad (min 8 caracteres)
- Sistema crea `cuentas_auth` y `perfiles_usuario` automáticamente
- Usuario recibe confirmación exitosa

**Endpoint:** `POST /api/v1/auth/registro`  
**Relacionado con:** RN01 (Email único), RN02 (Validación credenciales)

---

### US-02 - Iniciar Sesión
**Como** usuario registrado  
**Quiero** autenticarme en el sistema  
**Para** acceder a mi cuenta personal

**Criterios de Aceptación:**
- Usuario proporciona email y contraseña
- Sistema valida credenciales contra `cuentas_auth`
- Cuenta debe estar activa (`activo = true`)
- Sistema genera y retorna JWT token
- Token tiene validez de 24 horas

**Endpoint:** `POST /api/v1/auth/login`  
**Relacionado con:** RN02 (Login falla si inactivo)

---

### US-03 - Configurar Unidades de Medida
**Como** usuario  
**Quiero** seleccionar mi preferencia de unidades (KG/LBS)  
**Para** ver los datos en mi sistema de medida preferido

**Criterios de Aceptación:**
- Usuario puede elegir entre KG o LBS
- Preferencia se guarda en `perfiles_usuario.unidades_medida`
- Todos los pesos mostrados se convierten a la unidad seleccionada
- Base de datos siempre almacena en KG (conversión en presentación)

**Endpoint:** `PATCH /api/v1/perfil/unidades`  
**Relacionado con:** RN03 (Unidad aplica a todas las vistas), RN27 (Consistencia)

---

### US-04 - Editar Perfil de Salud
**Como** usuario  
**Quiero** configurar mi perfil de salud (objetivos, alergias, condiciones)  
**Para** recibir planes personalizados y seguros

**Criterios de Aceptación:**
- Usuario selecciona objetivos de lista de etiquetas maestras
- Usuario selecciona alergias alimentarias (etiquetas tipo "alergia")
- Usuario selecciona condiciones médicas (etiquetas tipo "condicion")
- Sistema guarda en `usuario_perfil_salud` y `usuario_etiquetas_salud`
- Solo se permiten etiquetas existentes en tabla `etiquetas`

**Endpoint:** `PUT /api/v1/perfil/salud`  
**Relacionado con:** RN04 (Etiquetas maestras), RN16 (Filtrado por alergias)

---

### US-05 - Eliminar Cuenta
**Como** usuario  
**Quiero** eliminar permanentemente mi cuenta  
**Para** retirarme del sistema

**Criterios de Aceptación:**
- Sistema solicita confirmación explícita (escribir "ELIMINAR")
- Usuario debe estar autenticado
- Sistema elimina `cuentas_auth` con CASCADE a todas las tablas relacionadas
- Datos se eliminan permanentemente (no soft delete para cuentas)
- Sistema retorna confirmación de eliminación

**Endpoint:** `DELETE /api/v1/auth/cuenta`  
**Relacionado con:** RN05 (Confirmación explícita)

## 📚 MÓDULO 2: BIBLIOTECA DE CONTENIDO (ADMIN)

### US-06 - Gestionar Etiquetas
**Como** administrador  
**Quiero** crear, editar y eliminar etiquetas maestras  
**Para** categorizar ingredientes, ejercicios y planes

**Criterios de Aceptación:**
- Admin puede crear etiquetas con nombre único
- Admin puede asignar tipo: objetivo, alergia, condicion, categoria
- Admin NO puede eliminar etiqueta si está en uso
- Sistema lista todas las etiquetas activas

**Endpoints:** `POST/GET/PUT/DELETE /api/v1/admin/etiquetas`  
**Relacionado con:** RN06 (Nombre único), RN08 (No eliminar si en uso)

---

### US-07 - Gestionar Ingredientes
**Como** administrador  
**Quiero** crear y mantener catálogo de ingredientes  
**Para** que estén disponibles al crear comidas

**Criterios de Aceptación:**
- Admin crea ingrediente con: nombre, calorías, proteínas, carbohidratos, grasas (por 100g)
- Admin puede asignar etiquetas al ingrediente (ej: "Nueces", "Lácteos")
- Nombre debe ser único
- Admin NO puede eliminar ingrediente si está en recetas activas

**Endpoints:** `POST/GET/PUT/DELETE /api/v1/admin/ingredientes`  
**Relacionado con:** RN07 (Nombre único), RN09 (No eliminar si en uso)

---

### US-08 - Gestionar Ejercicios
**Como** administrador  
**Quiero** crear y mantener catálogo de ejercicios  
**Para** que estén disponibles al crear rutinas

**Criterios de Aceptación:**
- Admin crea ejercicio con: nombre, descripción, grupo muscular, nivel dificultad
- Admin puede asignar etiquetas (ej: "Cardio", "Fuerza")
- Nombre debe ser único
- Admin NO puede eliminar ejercicio si está en rutinas activas

**Endpoints:** `POST/GET/PUT/DELETE /api/v1/admin/ejercicios`  
**Relacionado con:** RN07 (Nombre único), RN09 (No eliminar si en uso)

---

### US-09 - Gestionar Comidas
**Como** administrador  
**Quiero** crear comidas (platos/recetas) combinando ingredientes  
**Para** incluirlas en planes nutricionales

**Criterios de Aceptación:**
- Admin crea comida con nombre y descripción
- Admin debe agregar AL MENOS 1 ingrediente (US-10)
- Sistema calcula calorías totales automáticamente
- Admin puede asignar etiquetas a la comida

**Endpoints:** `POST/GET/PUT/DELETE /api/v1/admin/comidas`  
**Relacionado con:** US-10, RN25 (Cálculo automático)

---

### US-10 - Gestionar Recetas (Ingredientes de Comida)
**Como** administrador  
**Quiero** agregar ingredientes a una comida con cantidades específicas  
**Para** definir la composición nutricional

**Criterios de Aceptación:**
- Admin selecciona ingrediente existente
- Admin especifica cantidad en gramos (valor positivo)
- Sistema calcula calorías: `(ingrediente.calorias * cantidad) / 100`
- Admin puede modificar o eliminar ingredientes de la receta

**Endpoints:** `POST/DELETE /api/v1/admin/comidas/{id}/ingredientes`  
**Relacionado con:** RN10 (Cantidad positiva), RN25 (Cálculo automático)

---

## 🗂️ MÓDULO 3: GESTOR DE CATÁLOGO - PLANES Y RUTINAS (ADMIN)

**Nota Importante:** En la implementación, el concepto "catalogo_metas" se divide en dos entidades separadas:
- **Planes Nutricionales** (tabla `planes`)
- **Rutinas de Ejercicio** (tabla `rutinas`)

Esta separación mejora la arquitectura permitiendo gestión independiente de nutrición y ejercicio.

---

### US-11 - Crear Meta del Catálogo (Plan o Rutina)
**Como** administrador  
**Quiero** crear un plan nutricional o rutina de ejercicio  
**Para** ofrecerlos en el catálogo a los usuarios

**Criterios de Aceptación - Plan Nutricional:**
- Admin crea plan con: nombre, descripción, duración (días), objetivo calorías diarias
- Nombre debe ser único
- Admin puede asignar etiquetas (ej: "Pérdida de peso", "Vegano")
- Plan se guarda en tabla `planes`

**Criterios de Aceptación - Rutina Ejercicio:**
- Admin crea rutina con: nombre, descripción, duración (semanas), nivel dificultad
- Nombre debe ser único
- Admin puede asignar etiquetas (ej: "Principiante", "Cardio")
- Rutina se guarda en tabla `rutinas`

**Endpoints:** `POST /api/v1/admin/planes`, `POST /api/v1/admin/rutinas`  
**Relacionado con:** RN11 (Nombres únicos)

---

### US-12 - Gestionar Meta (Asignar Etiquetas y Actividades)
**Como** administrador  
**Quiero** configurar los días/actividades de un plan o rutina  
**Para** definir qué hará el usuario cada día

**Criterios de Aceptación - Plan:**
- Admin asigna comidas a días específicos (tabla `plan_dias`)
- Cada día puede tener: desayuno, almuerzo, cena, snacks
- Admin especifica porciones recomendadas

**Criterios de Aceptación - Rutina:**
- Admin asigna ejercicios a la rutina (tabla `rutina_ejercicios`)
- Admin especifica series, repeticiones, tiempo descanso
- Admin define día de semana para cada ejercicio

**Endpoints:** `POST /api/v1/admin/planes/{id}/dias`, `POST /api/v1/admin/rutinas/{id}/ejercicios`  
**Relacionado con:** RN12 (Solo etiquetas existentes), RN13 (Series positivas)

---

### US-13 - Ver Catálogo de Metas (Admin)
**Como** administrador  
**Quiero** ver listado completo de planes y rutinas  
**Para** gestionarlos y verificar el catálogo

**Criterios de Aceptación:**
- Admin ve TODOS los planes (activos e inactivos)
- Admin ve TODAS las rutinas (activas e inactivas)
- Listado incluye: nombre, duración, etiquetas, estado (activo/inactivo)
- Admin puede filtrar por etiquetas

**Endpoints:** `GET /api/v1/admin/planes`, `GET /api/v1/admin/rutinas`  
**Relacionado con:** US-16 (Catálogo cliente)

---

### US-14 - Eliminar Meta
**Como** administrador  
**Quiero** eliminar un plan o rutina del catálogo  
**Para** mantener solo contenido relevante

**Criterios de Aceptación:**
- Sistema usa SOFT DELETE (marca como inactivo)
- Admin NO puede eliminar si hay usuarios con asignación ACTIVA
- Sistema retorna error si plan/rutina está en uso
- Planes/rutinas inactivos no aparecen en catálogo de clientes

**Endpoints:** `DELETE /api/v1/admin/planes/{id}`, `DELETE /api/v1/admin/rutinas/{id}`  
**Relacionado con:** RN14 (No eliminar si usuarios activos), RN28 (Soft delete)

---

### US-15 - Ensamblar Rutinas
**Como** administrador  
**Quiero** configurar ejercicios dentro de una rutina con parámetros específicos  
**Para** definir el entrenamiento completo

**Criterios de Aceptación:**
- Admin agrega ejercicio existente a rutina
- Admin especifica: series (entero positivo), repeticiones (entero positivo), peso sugerido, día de semana
- Valores deben ser númericos y positivos
- Admin puede reordenar ejercicios en la rutina

**Endpoints:** `POST /api/v1/admin/rutinas/{id}/ejercicios`  
**Relacionado con:** RN13 (Series y reps positivas)

---

## 🔍 MÓDULO 4: EXPLORACIÓN Y ACTIVACIÓN (CLIENTE)

### US-16 - Ver Catálogo (Cliente)
**Como** usuario cliente  
**Quiero** explorar el catálogo de planes y rutinas disponibles  
**Para** encontrar uno que se ajuste a mis necesidades

**Criterios de Aceptación:**
- Usuario ve solo planes/rutinas ACTIVOS
- Si usuario tiene perfil de salud configurado, ver planes sugeridos según objetivo
- Sistema OCULTA planes que contengan ingredientes a los que usuario es alérgico
- Usuario puede filtrar por etiquetas (ej: "Vegano", "Principiante")
- Listado muestra: nombre, descripción, duración, etiquetas

**Endpoints:** `GET /api/v1/planes/catalogo`, `GET /api/v1/rutinas/catalogo`  
**Relacionado con:** RN15 (Sugerencias según perfil), RN16 (¡CRÍTICO! Filtrar alérgenos)

---

### US-17 - Ver Detalle de Meta
**Como** usuario cliente  
**Quiero** ver los detalles completos de un plan o rutina  
**Para** decidir si activarlo

**Criterios de Aceptación - Plan:**
- Usuario ve: nombre, descripción, duración, objetivo calorías, etiquetas
- Usuario ve comidas programadas por día (desayuno, almuerzo, cena)
- Usuario ve información nutricional total

**Criterios de Aceptación - Rutina:**
- Usuario ve: nombre, descripción, duración, nivel dificultad, etiquetas
- Usuario ve ejercicios con series, repeticiones, día de semana
- Usuario ve grupos musculares trabajados

**Endpoints:** `GET /api/v1/planes/{id}`, `GET /api/v1/rutinas/{id}`  
**Relacionado con:** US-18 (Activar meta)

---

### US-18 - Activar una Meta
**Como** usuario cliente  
**Quiero** activar un plan nutricional o rutina de ejercicio  
**Para** comenzar a seguirlo

**Criterios de Aceptación:**
- Usuario puede activar plan o rutina desde el detalle
- Sistema verifica que usuario NO tiene otro plan/rutina ACTIVO del mismo tipo
- Si ya tiene uno activo, sistema propone reemplazarlo
- Al activar, sistema crea registro en `usuarios_planes` o `usuarios_rutinas` con estado ACTIVO
- Sistema registra fecha inicio y calcula fecha fin estimada

**Endpoints:** `POST /api/v1/usuario/planes/{id}/activar`, `POST /api/v1/usuario/rutinas/{id}/activar`  
**Relacionado con:** RN17 (Solo una activa), RN18 (Proponer reemplazo)

---

### US-19 - Pausar/Reanudar Meta
**Como** usuario cliente  
**Quiero** pausar temporalmente mi plan o rutina  
**Para** retomarlo después sin perder progreso

**Criterios de Aceptación:**
- Usuario puede pausar plan/rutina con estado ACTIVO
- Usuario puede reanudar plan/rutina con estado PAUSADO
- Usuario NO puede pausar si estado es COMPLETADO o CANCELADO
- Al pausar, sistema marca fecha de pausa
- Al reanudar, sistema extiende fecha fin considerando días pausados

**Endpoints:** `PATCH /api/v1/usuario/planes/{id}/pausar`, `PATCH /api/v1/usuario/planes/{id}/reanudar`  
**Relacionado con:** RN19 (No pausar si completado), RN26 (Transiciones válidas)

---

### US-20 - Completar/Cancelar Meta
**Como** usuario cliente  
**Quiero** marcar mi plan/rutina como completado o cancelarlo  
**Para** finalizar el seguimiento

**Criterios de Aceptación:**
- Sistema marca automáticamente como COMPLETADO al llegar a duración total
- Usuario puede marcar manualmente como COMPLETADO antes de tiempo
- Usuario puede CANCELAR plan/rutina en cualquier momento
- Estados finales (COMPLETADO/CANCELADO) no permiten más cambios
- Sistema registra fecha de finalización y motivo (completado/cancelado)

**Endpoints:** `PATCH /api/v1/usuario/planes/{id}/completar`, `PATCH /api/v1/usuario/planes/{id}/cancelar`  
**Relacionado con:** RN26 (Transiciones válidas)

---

## 📊 MÓDULO 5: SEGUIMIENTO DE PROGRESO (CLIENTE)

### US-21 - Ver Actividades de mi Plan
**Como** usuario cliente  
**Quiero** ver las actividades programadas para hoy  
**Para** saber qué comidas/ejercicios debo completar

**Criterios de Aceptación - Plan Nutricional:**
- Usuario ve comidas del día actual de su plan activo
- Sistema calcula: `dia_actual = fecha_hoy - fecha_inicio + 1`
- Usuario ve si ya registró cada comida (marca visual ✓)
- Usuario ve calorías objetivo vs calorías consumidas

**Criterios de Aceptación - Rutina:**
- Usuario ve ejercicios programados para hoy
- Usuario ve si ya registró cada ejercicio (marca visual ✓)
- Usuario ve series/reps completadas vs objetivo

**Endpoints:** `GET /api/v1/usuario/plan-actual/hoy`, `GET /api/v1/usuario/rutina-actual/hoy`  
**Relacionado con:** RN20 (Mostrar checks), US-22 (Marcar actividad)

---

### US-22 - Marcar Actividad como Completada
**Como** usuario cliente  
**Quiero** registrar que completé una comida o ejercicio  
**Para** llevar seguimiento de mi progreso

**Criterios de Aceptación - Comida:**
- Usuario registra comida con: fecha, comida_id, porciones consumidas
- Sistema calcula calorías consumidas automáticamente
- Sistema NO permite registrar si plan está PAUSADO
- Registro se guarda en `registros_comidas`

**Criterios de Aceptación - Ejercicio:**
- Usuario registra ejercicio con: fecha, ejercicio_id, series, reps, peso usado
- Sistema NO permite registrar si rutina está PAUSADA
- Registro se guarda en `registros_ejercicios`

**Endpoints:** `POST /api/v1/usuario/registros/comidas`, `POST /api/v1/usuario/registros/ejercicios`  
**Relacionado con:** RN21 (No marcar si pausado), RN25 (Cálculo automático)

---

### US-23 - Desmarcar Actividad
**Como** usuario cliente  
**Quiero** eliminar un registro de actividad que marqué por error  
**Para** corregir mi seguimiento

**Criterios de Aceptación:**
- Usuario puede eliminar registro de comida del día actual
- Usuario puede eliminar registro de ejercicio del día actual
- Sistema recalcula calorías totales del día
- Solo se pueden eliminar registros propios

**Endpoints:** `DELETE /api/v1/usuario/registros/comidas/{id}`, `DELETE /api/v1/usuario/registros/ejercicios/{id}`  
**Relacionado con:** US-22 (Marcar actividad)

---

### US-24 - Registrar y Ver Mediciones
**Como** usuario cliente  
**Quiero** registrar mis mediciones corporales periódicamente  
**Para** monitorear mi progreso físico

**Criterios de Aceptación:**
- Usuario registra: peso, altura, circunferencia cintura (opcional), fecha
- Valores deben ser numéricos y dentro de rangos realistas (ej: peso 20-600 kg)
- Sistema guarda en `usuario_historial_medidas`
- Usuario puede ver historial completo de mediciones
- Sistema convierte unidades según preferencia del usuario

**Endpoints:** `POST /api/v1/usuario/mediciones`, `GET /api/v1/usuario/mediciones`  
**Relacionado con:** RN22 (Validar rangos), RN27 (Unidades consistentes)

---

### US-25 - Ver Gráfico de Progreso y Reporte
**Como** usuario cliente  
**Quiero** ver un gráfico de mi evolución de peso y exportar reporte  
**Para** visualizar mi progreso en el tiempo

**Criterios de Aceptación:**
- Sistema genera gráfico de peso vs tiempo (requiere mínimo 2 mediciones)
- Usuario puede exportar reporte en PDF con:
  - Datos personales
  - Gráfico de peso
  - Resumen de actividades completadas
  - Adherencia a plan/rutina
- PDF debe tener formato estándar y máximo 5 MB

**Endpoints:** `GET /api/v1/usuario/progreso/grafico`, `GET /api/v1/usuario/progreso/pdf`  
**Relacionado con:** RN23 (Mínimo 2 registros), RN24 (Generación PDF)
---

**Fecha de Creación:** 4 de Noviembre, 2025  
**Versión del Documento:** 1.0  
**Propósito:** Documento fundacional - Define las 25 User Stories del sistema

---

> **💡 Nota sobre Arquitectura:**  
> Las User Stories US-11 a US-20 mencionan "catalogo_metas" como concepto unificado.  
> En la implementación, esto se divide en **Planes Nutricionales** y **Rutinas de Ejercicio** como entidades separadas.  
> Esta separación mejora la arquitectura permitiendo gestión especializada de nutrición y entrenamiento.
