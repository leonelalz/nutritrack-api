# Organización del Swagger - NutriTrack API 📚

**URL Swagger UI:** http://localhost:8080/api/v1/swagger-ui/index.html

## 📋 Estructura Organizada por Módulos

El Swagger ahora está organizado en **5 módulos principales** con sus respectivos sub-módulos, siguiendo el flujo lógico de uso del API.

---

## 🔵 MÓDULO 1: Autenticación y Perfiles

### 1.1 Autenticación
**Descripción:** Módulo 1 - Registro, login y gestión de sesiones

**Endpoints:**
- `POST /auth/register` - Registrar nuevo usuario
- `POST /auth/login` - Iniciar sesión
- `POST /auth/refresh` - Renovar token
- `GET /auth/profile` - Obtener perfil del usuario autenticado

**Flujo de prueba:**
1. Registrar un nuevo usuario
2. Hacer login para obtener el token JWT
3. Usar el token en los siguientes endpoints (click en "Authorize" 🔒)

---

### 1.2 Gestión de Perfil
**Descripción:** Módulo 1 - Gestión del perfil personal del usuario

**Endpoints:**
- `GET /app/profile` - Obtener mi perfil completo
- `PUT /app/profile` - Actualizar mi perfil
- `DELETE /app/profile` - Eliminar mi cuenta (soft delete)

**Requiere:** Token de usuario (USER)

---

### 1.3 Administración de Cuentas (ADMIN)
**Descripción:** Módulo 1 - Gestión de cuentas de usuario - Solo ADMIN

**Endpoints:**
- `PUT /cuentas/{id}/password` - Cambiar contraseña de usuario
- `GET /cuentas/{id}/roles` - Obtener roles de usuario
- `POST /cuentas/{id}/roles` - Asignar rol a usuario
- `DELETE /cuentas/{id}/roles/{rolId}` - Remover rol de usuario

**Requiere:** Token de administrador (ADMIN)

---

### 1.4 Administración de Perfiles (ADMIN)
**Descripción:** Módulo 1 - Gestión de perfiles de usuario - Solo ADMIN

**Endpoints:**
- `PUT /perfiles/{id}/nombre` - Actualizar nombre del perfil
- `GET /perfiles/{id}` - Obtener perfil por ID
- `GET /perfiles` - Listar todos los perfiles

**Requiere:** Token de administrador (ADMIN)

---

## 🟢 MÓDULO 2: Biblioteca de Contenido

### 2.1 Etiquetas
**Descripción:** Módulo 2 - Gestión de etiquetas para categorización (alergias, dietas, objetivos, etc.)

**Endpoints:**
- `POST /etiquetas` - Crear etiqueta (ADMIN)
- `GET /etiquetas` - Listar todas las etiquetas
- `GET /etiquetas/{id}` - Obtener etiqueta por ID
- `PUT /etiquetas/{id}` - Actualizar etiqueta (ADMIN)
- `DELETE /etiquetas/{id}` - Eliminar etiqueta (ADMIN)
- `GET /etiquetas/tipo/{tipo}` - Buscar etiquetas por tipo

**Tipos de etiquetas:**
- `alergia` - Alergias alimentarias
- `objetivo` - Objetivos de salud
- `dieta` - Tipos de dieta
- `condicion` - Condiciones médicas
- `dificultad` - Nivel de dificultad
- `tipo_ejercicio` - Categorías de ejercicio

---

### 2.2 Ingredientes (ADMIN)
**Descripción:** Módulo 2 - Gestión de ingredientes con información nutricional - Solo ADMIN

**Endpoints:**
- `POST /admin/ingredientes` - Crear ingrediente
- `GET /admin/ingredientes` - Listar todos los ingredientes
- `GET /admin/ingredientes/{id}` - Obtener ingrediente por ID
- `PUT /admin/ingredientes/{id}` - Actualizar ingrediente
- `DELETE /admin/ingredientes/{id}` - Eliminar ingrediente
- `GET /admin/ingredientes/buscar?nombre=` - Buscar por nombre
- `GET /admin/ingredientes/grupo/{grupo}` - Buscar por grupo alimenticio

**Requiere:** Token ADMIN

---

### 2.3 Ejercicios (ADMIN)
**Descripción:** Módulo 2 - Gestión de ejercicios con calorías estimadas - Solo ADMIN

**Endpoints:**
- `POST /admin/ejercicios` - Crear ejercicio
- `GET /admin/ejercicios` - Listar todos los ejercicios
- `GET /admin/ejercicios/{id}` - Obtener ejercicio por ID
- `PUT /admin/ejercicios/{id}` - Actualizar ejercicio
- `DELETE /admin/ejercicios/{id}` - Eliminar ejercicio
- `GET /admin/ejercicios/tipo/{tipo}` - Buscar por tipo
- `GET /admin/ejercicios/musculo/{musculo}` - Buscar por músculo

**Requiere:** Token ADMIN

---

### 2.4 Comidas (ADMIN)
**Descripción:** Módulo 2 - Gestión de comidas con recetas e ingredientes - Solo ADMIN

**Endpoints:**
- `POST /admin/comidas` - Crear comida
- `GET /admin/comidas` - Listar todas las comidas
- `GET /admin/comidas/{id}` - Obtener comida con detalles nutricionales
- `PUT /admin/comidas/{id}` - Actualizar comida
- `DELETE /admin/comidas/{id}` - Eliminar comida
- `POST /admin/comidas/{id}/ingredientes/{ingredienteId}` - Agregar ingrediente
- `DELETE /admin/comidas/{id}/ingredientes/{ingredienteId}` - Remover ingrediente

**Requiere:** Token ADMIN

---

## 🟡 MÓDULO 3: Planes Nutricionales

### 3.1 Planes Nutricionales (ADMIN)
**Descripción:** Módulo 3 - Creación y gestión de planes nutricionales - Solo ADMIN

**Endpoints:**
- `POST /admin/planes` - Crear plan nutricional
- `GET /admin/planes` - Listar todos los planes
- `GET /admin/planes/{id}` - Obtener plan por ID
- `GET /admin/planes/{id}/detalle` - Obtener plan con detalles completos
- `PUT /admin/planes/{id}` - Actualizar plan
- `DELETE /admin/planes/{id}` - Eliminar plan
- `POST /admin/planes/{id}/comidas` - Agregar comida al plan
- `DELETE /admin/planes/{id}/comidas` - Remover comida del plan
- `POST /admin/planes/{id}/etiquetas/{etiquetaId}` - Agregar etiqueta
- `DELETE /admin/planes/{id}/etiquetas/{etiquetaId}` - Remover etiqueta

**Requiere:** Token ADMIN

---

### 3.2 Mis Planes (Usuario)
**Descripción:** Módulo 3 - Consulta y seguimiento de planes nutricionales asignados

**Endpoints:**
- `GET /api/usuario/planes` - Mis planes asignados
- `GET /api/usuario/planes/activos` - Mis planes activos
- `GET /api/usuario/planes/{id}` - Detalle de mi plan
- `POST /api/usuario/planes` - Asignarme un plan
- `PUT /api/usuario/planes/{id}/avanzar-dia` - Avanzar día en el plan
- `PUT /api/usuario/planes/{id}/completar` - Marcar plan como completado
- `PUT /api/usuario/planes/{id}/cancelar` - Cancelar plan

**Requiere:** Token USER

**Regla importante:** Solo puedes tener 1 plan activo a la vez

---

## 🟠 MÓDULO 4: Rutinas de Ejercicio

### 4.1 Rutinas de Ejercicio (ADMIN)
**Descripción:** Módulo 4 - Creación y gestión de rutinas de ejercicio - Solo ADMIN

**Endpoints:**
- `POST /admin/rutinas` - Crear rutina
- `GET /admin/rutinas` - Listar todas las rutinas
- `GET /admin/rutinas/{id}` - Obtener rutina por ID
- `GET /admin/rutinas/{id}/detalle` - Obtener rutina con detalles completos
- `PUT /admin/rutinas/{id}` - Actualizar rutina
- `DELETE /admin/rutinas/{id}` - Eliminar rutina
- `POST /admin/rutinas/{id}/ejercicios` - Agregar ejercicio a la rutina
- `DELETE /admin/rutinas/{id}/ejercicios/{ejercicioId}` - Remover ejercicio
- `POST /admin/rutinas/{id}/etiquetas/{etiquetaId}` - Agregar etiqueta
- `DELETE /admin/rutinas/{id}/etiquetas/{etiquetaId}` - Remover etiqueta

**Requiere:** Token ADMIN

---

### 4.2 Mis Rutinas (Usuario)
**Descripción:** Módulo 4 - Consulta y seguimiento de rutinas de ejercicio asignadas

**Endpoints:**
- `GET /api/usuario/rutinas` - Mis rutinas asignadas
- `GET /api/usuario/rutinas/activos` - Mis rutinas activas
- `GET /api/usuario/rutinas/{id}` - Detalle de mi rutina
- `POST /api/usuario/rutinas` - Asignarme una rutina
- `PUT /api/usuario/rutinas/{id}/avanzar-semana` - Avanzar semana en la rutina
- `PUT /api/usuario/rutinas/{id}/completar` - Marcar rutina como completada
- `PUT /api/usuario/rutinas/{id}/cancelar` - Cancelar rutina

**Requiere:** Token USER

---

## 🔴 MÓDULO 5: Seguimiento y Registros

### 5.1 Registro de Comidas
**Descripción:** Módulo 5 - Registro y seguimiento de comidas consumidas

**Endpoints:**
- `POST /api/usuario/registros/comidas` - Registrar comida consumida
- `GET /api/usuario/registros/comidas` - Mis registros de comidas
- `GET /api/usuario/registros/comidas/{id}` - Detalle de registro
- `GET /api/usuario/registros/comidas/estadisticas` - Estadísticas nutricionales
- `DELETE /api/usuario/registros/comidas/{id}` - Eliminar registro

**Query Parameters:**
- `fechaInicio` - Filtrar desde fecha (YYYY-MM-DD)
- `fechaFin` - Filtrar hasta fecha (YYYY-MM-DD)

**Requiere:** Token USER

---

### 5.2 Registro de Ejercicios
**Descripción:** Módulo 5 - Registro y seguimiento de ejercicios realizados

**Endpoints:**
- `POST /api/usuario/registros/ejercicios` - Registrar ejercicio realizado
- `GET /api/usuario/registros/ejercicios` - Mis registros de ejercicios
- `GET /api/usuario/registros/ejercicios/{id}` - Detalle de registro
- `GET /api/usuario/registros/ejercicios/estadisticas` - Estadísticas de actividad
- `DELETE /api/usuario/registros/ejercicios/{id}` - Eliminar registro

**Query Parameters:**
- `fechaInicio` - Filtrar desde fecha (YYYY-MM-DD)
- `fechaFin` - Filtrar hasta fecha (YYYY-MM-DD)

**Requiere:** Token USER

---

## 🎯 Flujo Recomendado de Pruebas

### Paso 1: Autenticación (Módulo 1.1)
```
1. POST /auth/register - Crear cuenta
2. POST /auth/login - Obtener token
3. Click en "Authorize" 🔒 y pegar el token
```

### Paso 2: Configurar Biblioteca (Módulo 2) - ADMIN
```
1. Crear etiquetas (2.1)
2. Crear ingredientes (2.2)
3. Crear ejercicios (2.3)
4. Crear comidas con ingredientes (2.4)
```

### Paso 3: Crear Planes (Módulo 3.1) - ADMIN
```
1. POST /admin/planes - Crear plan
2. POST /admin/planes/{id}/comidas - Agregar comidas al plan
3. POST /admin/planes/{id}/etiquetas - Etiquetar el plan
```

### Paso 4: Crear Rutinas (Módulo 4.1) - ADMIN
```
1. POST /admin/rutinas - Crear rutina
2. POST /admin/rutinas/{id}/ejercicios - Agregar ejercicios
3. POST /admin/rutinas/{id}/etiquetas - Etiquetar la rutina
```

### Paso 5: Asignar y Seguir (Módulos 3.2, 4.2, 5) - USER
```
1. POST /api/usuario/planes - Asignarme un plan
2. POST /api/usuario/rutinas - Asignarme una rutina
3. POST /api/usuario/registros/comidas - Registrar comidas
4. POST /api/usuario/registros/ejercicios - Registrar ejercicios
5. GET /api/usuario/registros/*/estadisticas - Ver estadísticas
```

---

## 🔒 Autenticación en Swagger

### Obtener Token
1. Ir a **1.1 Autenticación**
2. Expandir `POST /auth/login`
3. Click en "Try it out"
4. Usar credenciales:
   - **Admin:** `admin@fintech.com` / `admin123`
   - **Usuario:** Tu usuario registrado
5. Click en "Execute"
6. Copiar el valor del campo `token`

### Usar Token
1. Click en el botón **"Authorize" 🔒** (arriba a la derecha)
2. Pegar el token en el campo "Value"
3. Click en "Authorize"
4. Click en "Close"

Ahora todos los endpoints protegidos usarán automáticamente tu token.

---

## 📊 Ventajas de esta Organización

✅ **Flujo lógico:** Los módulos están ordenados según el flujo de uso real  
✅ **Fácil navegación:** Numeración clara (1.1, 1.2, 2.1, etc.)  
✅ **Roles visibles:** Se distingue claramente qué es ADMIN y qué es USER  
✅ **Agrupación coherente:** Endpoints relacionados están juntos  
✅ **Testing sistemático:** Puedes probar módulo por módulo en orden  

---

## 🆘 Troubleshooting

### Error 401 Unauthorized
- Verifica que hayas hecho login
- Verifica que hayas usado "Authorize" con el token
- El token expira en 24 horas, vuelve a hacer login

### Error 403 Forbidden
- El endpoint requiere rol ADMIN y tienes rol USER
- Usa las credenciales de admin: `admin@fintech.com` / `admin123`

### No veo los tags organizados
- Actualiza la página (F5)
- Limpia caché del navegador (Ctrl+Shift+R)
- Verifica que la app esté corriendo: http://localhost:8080/api/v1/swagger-ui/index.html

---

**¡Feliz testing! 🚀**
