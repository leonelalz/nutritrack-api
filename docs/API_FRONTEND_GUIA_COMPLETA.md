# 🚀 API NutriTrack - Guía Completa para Frontend

## 📌 Información General

**URL Base:** `http://localhost:8080/api/v1`  
**Rol de Usuario:** `ROLE_USER` (Usuario Regular)  
**Autenticación:** Bearer Token JWT  
**Formato de Respuesta:** JSON

---

## 🔐 Estructura de Autenticación

### Headers Requeridos
Todos los endpoints (excepto los públicos) requieren:

```http
Authorization: Bearer {tu_token_jwt}
Content-Type: application/json
```

### Formato de Respuesta Estándar

```json
{
  "success": true | false,
  "message": "Mensaje descriptivo",
  "data": { /* datos de respuesta */ } | null
}
```

---

## 📚 Índice de Módulos

1. [**Módulo 1: Autenticación y Perfil**](#módulo-1-autenticación-y-perfil) (US-01 a US-05)
2. [**Módulo 3: Catálogo**](#módulo-3-catálogo) (US-16, US-17)
3. [**Módulo 4: Asignación de Metas**](#módulo-4-asignación-de-metas) (US-18, US-19, US-20)
4. [**Módulo 5: Tracking de Actividades**](#módulo-5-tracking-de-actividades) (US-21, US-22, US-23)

---

# Módulo 1: Autenticación y Perfil

## 🔓 1. Registro de Usuario (PÚBLICO)

**POST** `/auth/registro`  
**Autenticación:** No requerida

### Request Body:
```json
{
  "email": "usuario@example.com",
  "password": "MiPassword123!Seguro",
  "nombre": "Juan",
  "apellido": "Pérez"
}
```

### Validaciones:
- Email único y formato válido RFC 5322
- Contraseña mínimo 12 caracteres con mayúscula, minúscula, número y símbolo
- Contraseña no puede ser común (blacklist)

### Respuesta Exitosa (201):
```json
{
  "success": true,
  "message": "Usuario registrado exitosamente",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "email": "usuario@example.com",
    "nombre": "Juan",
    "apellido": "Pérez",
    "role": "ROLE_USER"
  }
}
```

### Errores Posibles:
- **400**: Email duplicado, email inválido, contraseña débil
- **500**: Error del servidor

---

## 🔓 2. Iniciar Sesión (PÚBLICO)

**POST** `/auth/login`  
**Autenticación:** No requerida

### Request Body:
```json
{
  "email": "usuario@example.com",
  "password": "MiPassword123!Seguro"
}
```

### Respuesta Exitosa (200):
```json
{
  "success": true,
  "message": "Login exitoso",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "email": "usuario@example.com",
    "nombre": "Juan",
    "apellido": "Pérez",
    "role": "ROLE_USER"
  }
}
```

### Errores Posibles:
- **401**: Credenciales inválidas
- **400**: Cuenta desactivada

---

## 👤 3. Eliminar Cuenta

**DELETE** `/auth/cuenta`  
**Autenticación:** Requerida (USER)

### Sin Request Body

### Respuesta Exitosa (200):
```json
{
  "success": true,
  "message": "Cuenta eliminada exitosamente",
  "data": null
}
```

---

## 👤 4. Obtener Perfil Completo

**GET** `/perfil/completo`  
**Autenticación:** Requerida (USER)

### Sin Parámetros

### Respuesta Exitosa (200):
```json
{
  "success": true,
  "message": "Perfil completo obtenido",
  "data": {
    "id": 1,
    "email": "usuario@example.com",
    "rol": "ROLE_USER",
    "activo": true,
    "fechaRegistro": "2024-01-15",
    "nombre": "Juan",
    "apellido": "Pérez",
    "nombreCompleto": "Juan Pérez",
    "unidadesMedida": "KG",
    "fechaInicioApp": "2024-01-15",
    "perfilSalud": {
      "id": 1,
      "objetivoActual": "PERDER_PESO",
      "nivelActividadActual": "MODERADO",
      "fechaActualizacion": "2024-11-15",
      "etiquetas": [
        {
          "id": 1,
          "nombre": "Diabetes",
          "tipoEtiqueta": "CONDICION_MEDICA",
          "descripcion": "Diabetes tipo 2"
        }
      ]
    },
    "ultimaMedicion": {
      "id": 15,
      "peso": 75.5,
      "altura": 175,
      "imc": 24.65,
      "fechaMedicion": "2025-11-19",
      "unidadPeso": "KG",
      "categoriaIMC": "Peso normal"
    },
    "totalMediciones": 15
  }
}
```

### Notas:
- `perfilSalud` puede ser `null` si no está configurado
- `ultimaMedicion` puede ser `null` si no hay mediciones

---

## 👤 5. Actualizar Unidades de Medida

**PATCH** `/perfil/unidades`  
**Autenticación:** Requerida (USER)

### Request Body:
```json
{
  "unidadesMedida": "KG"
}
```

### Valores Permitidos:
- `KG` - Kilogramos
- `LBS` - Libras

### Respuesta Exitosa (200):
```json
{
  "success": true,
  "message": "Unidades actualizadas exitosamente",
  "data": null
}
```

---

## 👤 6. Crear Perfil de Salud

**POST** `/perfil/salud`  
**Autenticación:** Requerida (USER)

### Request Body:
```json
{
  "objetivoActual": "PERDER_PESO",
  "nivelActividadActual": "MODERADO",
  "etiquetasId": [1, 2, 3]
}
```

### Objetivos Disponibles:
- `PERDER_PESO`
- `GANAR_MUSCULO`
- `MANTENER_PESO`
- `MEJORAR_SALUD`

### Niveles de Actividad:
- `SEDENTARIO`
- `LIGERO`
- `MODERADO`
- `ALTO`
- `MUY_ALTO`

### Respuesta Exitosa (200):
```json
{
  "success": true,
  "message": "Perfil de salud creado exitosamente",
  "data": {
    "id": 1,
    "objetivoActual": "PERDER_PESO",
    "nivelActividadActual": "MODERADO",
    "etiquetas": [
      {
        "id": 1,
        "nombre": "Diabetes",
        "tipoEtiqueta": "CONDICION_MEDICA",
        "descripcion": "Condición médica diabetes"
      }
    ]
  }
}
```

---

## 👤 7. Actualizar Perfil de Salud

**PUT** `/perfil/salud`  
**Autenticación:** Requerida (USER)

### Request Body:
```json
{
  "objetivoActual": "GANAR_MUSCULO",
  "nivelActividadActual": "ALTO",
  "etiquetasId": [2, 4]
}
```

### Respuesta Exitosa (200):
```json
{
  "success": true,
  "message": "Perfil de salud actualizado",
  "data": {
    "id": 1,
    "objetivoActual": "GANAR_MUSCULO",
    "nivelActividadActual": "ALTO",
    "etiquetas": [...]
  }
}
```

---

## 👤 8. Obtener Perfil de Salud

**GET** `/perfil/salud`  
**Autenticación:** Requerida (USER)

### Sin Parámetros

### Respuesta Exitosa (200):
```json
{
  "success": true,
  "message": "Perfil de salud obtenido",
  "data": {
    "id": 1,
    "objetivoActual": "PERDER_PESO",
    "nivelActividadActual": "MODERADO",
    "etiquetas": [...]
  }
}
```

---

## 👤 9. Registrar Medición Corporal

**POST** `/perfil/mediciones`  
**Autenticación:** Requerida (USER)

### Request Body:
```json
{
  "peso": 75.5,
  "altura": 175,
  "fechaMedicion": "2025-11-19",
  "unidadPeso": "KG"
}
```

### Validaciones:
- Peso: 20-300 kg
- Altura: 50-250 cm
- Fecha no puede ser futura
- No puede haber duplicados para la misma fecha

### Respuesta Exitosa (200):
```json
{
  "success": true,
  "message": "Medición registrada exitosamente",
  "data": {
    "id": 1,
    "peso": 75.5,
    "altura": 175,
    "imc": 24.65,
    "fechaMedicion": "2025-11-19",
    "unidadPeso": "KG"
  }
}
```

---

## 👤 10. Obtener Historial de Mediciones

**GET** `/perfil/mediciones`  
**Autenticación:** Requerida (USER)

### Sin Parámetros

### Respuesta Exitosa (200):
```json
{
  "success": true,
  "message": "Historial obtenido",
  "data": [
    {
      "id": 3,
      "peso": 74.2,
      "altura": 175,
      "imc": 24.23,
      "fechaMedicion": "2025-11-19",
      "unidadPeso": "KG"
    },
    {
      "id": 2,
      "peso": 75.0,
      "altura": 175,
      "imc": 24.49,
      "fechaMedicion": "2025-11-10",
      "unidadPeso": "KG"
    }
  ]
}
```

---

## 👤 11. Actualizar Medición

**PUT** `/perfil/mediciones/{id}`  
**Autenticación:** Requerida (USER)

### Request Body:
```json
{
  "peso": 74.0,
  "altura": 175,
  "fechaMedicion": "2025-11-19",
  "unidadPeso": "KG"
}
```

### Respuesta Exitosa (200):
```json
{
  "success": true,
  "message": "Medición actualizada exitosamente",
  "data": {
    "id": 1,
    "peso": 74.0,
    "altura": 175,
    "imc": 24.16,
    "fechaMedicion": "2025-11-19",
    "unidadPeso": "KG"
  }
}
```

---

## 👤 12. Eliminar Medición

**DELETE** `/perfil/mediciones/{id}`  
**Autenticación:** Requerida (USER)

### Respuesta Exitosa (200):
```json
{
  "success": true,
  "message": "Medición eliminada exitosamente",
  "data": null
}
```

---

# Módulo 3: Catálogo

## 👤 13. Ver Catálogo de Planes

**GET** `/planes/catalogo`  
**Autenticación:** Requerida (USER)

### Query Parameters:
- `sugeridos` (opcional, default: false) - Filtra solo planes sugeridos según objetivo
- `page` (opcional, default: 0) - Número de página
- `size` (opcional, default: 20) - Elementos por página

### Ejemplo:
```
GET /planes/catalogo?sugeridos=true&page=0&size=10
```

### Respuesta Exitosa (200):
```json
{
  "success": true,
  "message": "Catálogo de planes obtenido",
  "data": {
    "content": [
      {
        "id": 1,
        "nombre": "Plan Pérdida Peso - 7 días",
        "descripcion": "Plan diseñado para pérdida de peso saludable",
        "duracionDias": 7,
        "activo": true,
        "objetivos": [
          {
            "objetivo": "PERDER_PESO",
            "calorias": 1800,
            "proteinas": 120,
            "carbohidratos": 180,
            "grasas": 50
          }
        ]
      }
    ],
    "totalElements": 5,
    "totalPages": 1,
    "number": 0,
    "size": 20
  }
}
```

### Notas:
- **RN15**: Sugiere planes según objetivo del perfil
- **RN16**: Filtra automáticamente planes con alérgenos incompatibles

---

## 👤 14. Ver Detalle de Plan

**GET** `/planes/catalogo/{id}`  
**Autenticación:** Requerida (USER)

### Respuesta Exitosa (200):
```json
{
  "success": true,
  "message": "Detalle del plan obtenido",
  "data": {
    "id": 1,
    "nombre": "Plan Pérdida Peso - 7 días",
    "descripcion": "Plan completo",
    "duracionDias": 7,
    "activo": true,
    "objetivos": [...],
    "dias": [
      {
        "id": 1,
        "numeroDia": 1,
        "tipoComida": "DESAYUNO",
        "comida": {
          "id": 1,
          "nombre": "Avena con frutas",
          "tipo": "RECETA",
          "informacionNutricional": {
            "calorias": 350,
            "proteinas": 12,
            "carbohidratos": 55,
            "grasas": 8
          }
        }
      }
    ]
  }
}
```

---

## 👤 15. Ver Catálogo de Rutinas

**GET** `/rutinas/catalogo`  
**Autenticación:** Requerida (USER)

### Query Parameters:
- `sugeridos` (opcional, default: false) - Filtra solo rutinas sugeridas según objetivo
- `page` (opcional, default: 0)
- `size` (opcional, default: 20)

### Ejemplo:
```
GET /rutinas/catalogo?sugeridos=true
```

### Respuesta Exitosa (200):
```json
{
  "success": true,
  "message": "Catálogo de rutinas obtenido",
  "data": {
    "content": [
      {
        "id": 1,
        "nombre": "Rutina Full Body - Principiante",
        "descripcion": "Rutina para todo el cuerpo",
        "nivelDificultad": "PRINCIPIANTE",
        "duracionSemanas": 4,
        "diasPorSemana": 3,
        "activo": true
      }
    ],
    "totalElements": 3,
    "totalPages": 1
  }
}
```

---

## 👤 16. Ver Detalle de Rutina

**GET** `/rutinas/catalogo/{id}`  
**Autenticación:** Requerida (USER)

### Respuesta Exitosa (200):
```json
{
  "success": true,
  "message": "Detalle de rutina obtenido",
  "data": {
    "id": 1,
    "nombre": "Rutina Full Body",
    "descripcion": "Rutina completa",
    "nivelDificultad": "PRINCIPIANTE",
    "duracionSemanas": 4,
    "diasPorSemana": 3,
    "activo": true,
    "ejercicios": [
      {
        "id": 1,
        "ejercicio": {
          "id": 1,
          "nombre": "Sentadillas",
          "descripcion": "Ejercicio de piernas",
          "grupoMuscular": "PIERNAS"
        },
        "series": 3,
        "repeticiones": 12,
        "peso": 20.0,
        "duracionMinutos": 15,
        "orden": 1
      }
    ]
  }
}
```

---

# Módulo 4: Asignación de Metas

## 👤 17. Activar Plan Nutricional

**POST** `/usuario/planes/activar`  
**Autenticación:** Requerida (USER)

### Request Body:
```json
{
  "planId": 1,
  "fechaInicio": "2025-11-05",
  "notas": "Iniciando Plan Pérdida Peso"
}
```

### Respuesta Exitosa (201):
```json
{
  "success": true,
  "message": "Plan activado exitosamente",
  "data": {
    "id": 1,
    "planId": 1,
    "planNombre": "Plan Pérdida Peso - 7 días",
    "estado": "ACTIVO",
    "fechaInicio": "2025-11-05",
    "fechaFin": "2025-11-11",
    "diaActual": 1
  }
}
```

### Errores Posibles:
- **400**: Ya tienes este plan activo (RN17)
- **400**: Plan contiene alérgenos incompatibles (RN32)
- **404**: Plan no encontrado

---

## 👤 18. Pausar Plan

**PATCH** `/usuario/planes/{usuarioPlanId}/pausar`  
**Autenticación:** Requerida (USER)

### Respuesta Exitosa (200):
```json
{
  "success": true,
  "message": "Plan pausado exitosamente",
  "data": {
    "id": 1,
    "planId": 1,
    "planNombre": "Plan Pérdida Peso - 7 días",
    "estado": "PAUSADO",
    "fechaInicio": "2025-11-05",
    "fechaFin": "2025-11-11",
    "diaActual": 3
  }
}
```

---

## 👤 19. Reanudar Plan

**PATCH** `/usuario/planes/{usuarioPlanId}/reanudar`  
**Autenticación:** Requerida (USER)

### Respuesta Exitosa (200):
```json
{
  "success": true,
  "message": "Plan reanudado exitosamente",
  "data": {
    "id": 1,
    "estado": "ACTIVO",
    "diaActual": 3
  }
}
```

---

## 👤 20. Completar Plan

**PATCH** `/usuario/planes/{usuarioPlanId}/completar`  
**Autenticación:** Requerida (USER)

### Respuesta Exitosa (200):
```json
{
  "success": true,
  "message": "Plan completado exitosamente",
  "data": {
    "id": 1,
    "estado": "COMPLETADO",
    "fechaFin": "2025-11-11"
  }
}
```

---

## 👤 21. Cancelar Plan

**PATCH** `/usuario/planes/{usuarioPlanId}/cancelar`  
**Autenticación:** Requerida (USER)

### Respuesta Exitosa (200):
```json
{
  "success": true,
  "message": "Plan cancelado",
  "data": {
    "id": 1,
    "estado": "CANCELADO"
  }
}
```

---

## 👤 22. Obtener Plan Activo

**GET** `/usuario/planes/activo`  
**Autenticación:** Requerida (USER)

### Respuesta Exitosa (200):
```json
{
  "success": true,
  "message": "Plan activo obtenido",
  "data": {
    "id": 1,
    "planId": 1,
    "planNombre": "Plan Pérdida Peso - 7 días",
    "estado": "ACTIVO",
    "fechaInicio": "2025-11-05",
    "fechaFin": "2025-11-11",
    "diaActual": 5
  }
}
```

---

## 👤 23. Listar Todos los Planes del Usuario

**GET** `/usuario/planes`  
**Autenticación:** Requerida (USER)

### Respuesta Exitosa (200):
```json
{
  "success": true,
  "message": "Planes obtenidos",
  "data": [
    {
      "id": 1,
      "planId": 1,
      "planNombre": "Plan Pérdida Peso",
      "estado": "ACTIVO",
      "fechaInicio": "2025-11-05",
      "diaActual": 5
    },
    {
      "id": 2,
      "planId": 2,
      "planNombre": "Plan Mantenimiento",
      "estado": "COMPLETADO",
      "fechaInicio": "2025-10-01",
      "fechaFin": "2025-10-30"
    }
  ]
}
```

---

## 👤 24. Listar Planes Activos

**GET** `/usuario/planes/activos`  
**Autenticación:** Requerida (USER)

### Respuesta Exitosa (200):
```json
{
  "success": true,
  "message": "Planes activos obtenidos",
  "data": [
    {
      "id": 1,
      "planId": 1,
      "planNombre": "Plan Pérdida Peso",
      "estado": "ACTIVO",
      "diaActual": 5
    }
  ]
}
```

---

## 👤 25. Activar Rutina

**POST** `/usuario/rutinas/activar`  
**Autenticación:** Requerida (USER)

### Request Body:
```json
{
  "rutinaId": 1,
  "fechaInicio": "2025-11-05",
  "notas": "Iniciando rutina full body"
}
```

### Respuesta Exitosa (201):
```json
{
  "success": true,
  "message": "Rutina activada exitosamente",
  "data": {
    "id": 1,
    "rutinaId": 1,
    "rutinaNombre": "Rutina Full Body - Principiante",
    "estado": "ACTIVO",
    "fechaInicio": "2025-11-05",
    "semanaActual": 1,
    "diaActual": 1
  }
}
```

### Errores Posibles:
- **400**: Ya tienes esta rutina activa (RN17)
- **400**: Rutina contiene ejercicios contraindicados para tu condición médica (RN33)
- **404**: Rutina no encontrada

---

## 👤 26-30. Gestionar Rutinas

Similar a los planes, las rutinas tienen los siguientes endpoints:

- **PATCH** `/usuario/rutinas/{usuarioRutinaId}/pausar`
- **PATCH** `/usuario/rutinas/{usuarioRutinaId}/reanudar`
- **PATCH** `/usuario/rutinas/{usuarioRutinaId}/completar`
- **PATCH** `/usuario/rutinas/{usuarioRutinaId}/cancelar`
- **GET** `/usuario/rutinas/activa`
- **GET** `/usuario/rutinas`
- **GET** `/usuario/rutinas/activas`

El formato de respuesta es idéntico al de planes.

---

# Módulo 5: Tracking de Actividades

## 👤 31. Registrar Comida

**POST** `/usuario/registros/comidas`  
**Autenticación:** Requerida (USER)

### Request Body:
```json
{
  "comidaId": 1,
  "tipoComida": "DESAYUNO",
  "porciones": 1.5,
  "fecha": "2025-11-19",
  "hora": "08:30:00",
  "notas": "Desayuno completo"
}
```

### Tipos de Comida:
- `DESAYUNO`
- `ALMUERZO`
- `CENA`
- `SNACK`

### Respuesta Exitosa (201):
```json
{
  "success": true,
  "message": "Comida registrada exitosamente",
  "data": {
    "id": 1,
    "comidaId": 1,
    "tipoComida": "DESAYUNO",
    "porciones": 1.5,
    "fecha": "2025-11-19",
    "hora": "08:30:00",
    "notas": "Desayuno completo"
  }
}
```

### Errores Posibles:
- **400**: Plan pausado (no permite registrar)
- **404**: Comida no encontrada

---

## 👤 32. Obtener Registros de Comidas del Día

**GET** `/usuario/registros/comidas/hoy`  
**Autenticación:** Requerida (USER)

### Respuesta Exitosa (200):
```json
{
  "success": true,
  "message": "Registros obtenidos",
  "data": [
    {
      "id": 1,
      "comidaId": 1,
      "tipoComida": "DESAYUNO",
      "porciones": 1,
      "fecha": "2025-11-19",
      "hora": "08:30:00"
    },
    {
      "id": 2,
      "comidaId": 5,
      "tipoComida": "ALMUERZO",
      "porciones": 1,
      "fecha": "2025-11-19",
      "hora": "13:00:00"
    }
  ]
}
```

---

## 👤 33. Actualizar Registro de Comida

**PUT** `/usuario/registros/comidas/{registroId}`  
**Autenticación:** Requerida (USER)

### Request Body:
```json
{
  "comidaId": 1,
  "tipoComida": "DESAYUNO",
  "porciones": 2,
  "fecha": "2025-11-19",
  "hora": "09:00:00",
  "notas": "Actualizado"
}
```

### Respuesta Exitosa (200):
```json
{
  "success": true,
  "message": "Registro actualizado",
  "data": {
    "id": 1,
    "porciones": 2,
    "hora": "09:00:00"
  }
}
```

---

## 👤 34. Registrar Ejercicio

**POST** `/usuario/registros/ejercicios`  
**Autenticación:** Requerida (USER)

### Request Body:
```json
{
  "ejercicioId": 1,
  "series": 3,
  "repeticiones": 12,
  "peso": 20.0,
  "duracionMinutos": 15,
  "fecha": "2025-11-19",
  "hora": "17:00:00",
  "notas": "Buen ritmo"
}
```

### Respuesta Exitosa (201):
```json
{
  "success": true,
  "message": "Ejercicio registrado",
  "data": {
    "id": 1,
    "ejercicioId": 1,
    "series": 3,
    "repeticiones": 12,
    "peso": 20.0,
    "fecha": "2025-11-19",
    "hora": "17:00:00"
  }
}
```

---

## 👤 35. Ver Actividades del Plan del Día

**GET** `/usuario/registros/plan/hoy`  
**Autenticación:** Requerida (USER)

### Respuesta Exitosa (200):
```json
{
  "success": true,
  "message": "Actividades del día obtenidas",
  "data": {
    "fecha": "2025-11-19",
    "numeroDia": 5,
    "comidas": [
      {
        "id": 1,
        "tipoComida": "DESAYUNO",
        "comida": {
          "id": 1,
          "nombre": "Avena con frutas",
          "calorias": 350
        },
        "completada": true,
        "horaRegistro": "08:30:00"
      },
      {
        "id": 2,
        "tipoComida": "ALMUERZO",
        "comida": {
          "id": 5,
          "nombre": "Pollo con verduras",
          "calorias": 450
        },
        "completada": false,
        "horaRegistro": null
      }
    ]
  }
}
```

### Notas:
- **RN20**: Muestra checks ✅ en actividades completadas
- `completada: true` indica que fue registrada

---

## 👤 36. Ver Actividades de una Fecha Específica

**GET** `/usuario/registros/plan/dia?fecha=2025-11-15`  
**Autenticación:** Requerida (USER)

### Query Parameters:
- `fecha` (requerido) - Formato: YYYY-MM-DD

### Respuesta: Igual que endpoint anterior

---

## 👤 37. Ver Ejercicios de la Rutina del Día

**GET** `/usuario/registros/rutina/hoy`  
**Autenticación:** Requerida (USER)

### Respuesta Exitosa (200):
```json
{
  "success": true,
  "message": "Ejercicios del día obtenidos",
  "data": {
    "fecha": "2025-11-19",
    "semana": 1,
    "dia": 1,
    "ejercicios": [
      {
        "id": 1,
        "ejercicio": {
          "id": 1,
          "nombre": "Sentadillas",
          "grupoMuscular": "PIERNAS"
        },
        "series": 3,
        "repeticiones": 12,
        "completado": true,
        "horaRegistro": "17:00:00"
      },
      {
        "id": 2,
        "ejercicio": {
          "id": 3,
          "nombre": "Press de banca",
          "grupoMuscular": "PECHO"
        },
        "series": 3,
        "repeticiones": 10,
        "completado": false,
        "horaRegistro": null
      }
    ]
  }
}
```

---

## 👤 38. Ver Ejercicios de una Fecha Específica

**GET** `/usuario/registros/rutina/dia?fecha=2025-11-15`  
**Autenticación:** Requerida (USER)

### Query Parameters:
- `fecha` (requerido) - Formato: YYYY-MM-DD

### Respuesta: Igual que endpoint anterior

---

## 👤 39. Eliminar Registro de Comida

**DELETE** `/usuario/registros/comidas/{registroId}`  
**Autenticación:** Requerida (USER)

### Respuesta Exitosa (204):
```
No Content
```

---

## 👤 40. Eliminar Registro de Ejercicio

**DELETE** `/usuario/registros/ejercicios/{registroId}`  
**Autenticación:** Requerida (USER)

### Respuesta Exitosa (204):
```
No Content
```

---

## 👤 41. Historial de Comidas

**GET** `/usuario/registros/comidas/historial?fechaInicio=2025-11-01&fechaFin=2025-11-30`  
**Autenticación:** Requerida (USER)

### Query Parameters:
- `fechaInicio` (requerido) - Formato: YYYY-MM-DD
- `fechaFin` (requerido) - Formato: YYYY-MM-DD

### Respuesta Exitosa (200):
```json
{
  "success": true,
  "message": "Historial obtenido",
  "data": [
    {
      "id": 1,
      "comidaId": 1,
      "tipoComida": "DESAYUNO",
      "porciones": 1,
      "fecha": "2025-11-19"
    },
    {
      "id": 2,
      "comidaId": 5,
      "tipoComida": "ALMUERZO",
      "porciones": 1,
      "fecha": "2025-11-19"
    }
  ]
}
```

---

## 👤 42. Historial de Ejercicios

**GET** `/usuario/registros/ejercicios/historial?fechaInicio=2025-11-01&fechaFin=2025-11-30`  
**Autenticación:** Requerida (USER)

### Query Parameters:
- `fechaInicio` (requerido) - Formato: YYYY-MM-DD
- `fechaFin` (requerido) - Formato: YYYY-MM-DD

### Respuesta Exitosa (200):
```json
{
  "success": true,
  "message": "Historial obtenido",
  "data": [
    {
      "id": 1,
      "ejercicioId": 1,
      "series": 3,
      "repeticiones": 12,
      "peso": 20.0,
      "fecha": "2025-11-19"
    }
  ]
}
```

---

## 👤 43. Detalle de Registro de Comida

**GET** `/usuario/registros/comidas/{registroId}`  
**Autenticación:** Requerida (USER)

### Respuesta Exitosa (200):
```json
{
  "success": true,
  "message": "Registro obtenido",
  "data": {
    "id": 1,
    "comidaId": 1,
    "tipoComida": "DESAYUNO",
    "porciones": 1,
    "fecha": "2025-11-19",
    "hora": "08:30:00",
    "notas": "Desayuno completo"
  }
}
```

---

## 👤 44. Detalle de Registro de Ejercicio

**GET** `/usuario/registros/ejercicios/{registroId}`  
**Autenticación:** Requerida (USER)

### Respuesta Exitosa (200):
```json
{
  "success": true,
  "message": "Registro obtenido",
  "data": {
    "id": 1,
    "ejercicioId": 1,
    "series": 3,
    "repeticiones": 12,
    "peso": 20.0,
    "duracionMinutos": 15,
    "fecha": "2025-11-19",
    "hora": "17:00:00",
    "notas": "Buen ritmo"
  }
}
```

---

# 📊 Códigos de Estado HTTP

| Código | Significado | Uso |
|--------|-------------|-----|
| 200 | OK | Operación exitosa (GET, PUT, PATCH) |
| 201 | Created | Recurso creado exitosamente (POST) |
| 204 | No Content | Eliminación exitosa (DELETE) |
| 400 | Bad Request | Datos inválidos, validación fallida |
| 401 | Unauthorized | Token inválido o expirado |
| 403 | Forbidden | Sin permisos (rol incorrecto) |
| 404 | Not Found | Recurso no encontrado |
| 409 | Conflict | Conflicto de negocio (duplicados, etc.) |
| 500 | Internal Server Error | Error del servidor |

---

# 🔒 Reglas de Negocio Clave

## RN15 - Sugerencias por Objetivo
Los catálogos muestran primero planes/rutinas que coinciden con el objetivo del usuario.

## RN16 - Validación de Alérgenos ⚠️
**CRÍTICO**: Los planes con ingredientes a los que el usuario es alérgico se filtran automáticamente del catálogo.

## RN17 - No Duplicados Activos
No se puede activar un plan/rutina si ya tienes ese mismo activo.

## RN19 - Estados de Metas
No se pueden pausar/reanudar planes completados o cancelados.

## RN20 - Checks de Completitud
Las actividades muestran ✅ cuando están completadas.

## RN21 - Plan Pausado
No se pueden registrar actividades si el plan está pausado.

## RN22 - Validación de Mediciones
- Peso: 20-300 kg
- Altura: 50-250 cm
- Fecha no puede ser futura

## RN32 - Validación Cruzada de Alérgenos (Planes) ⚠️
**CRÍTICO**: Al activar un plan, se valida que no contenga ingredientes alérgenos del usuario.

## RN33 - Validación de Contraindicaciones Médicas (Rutinas) ⚠️
**CRÍTICO**: Al activar una rutina, se valida que no contenga ejercicios contraindicados para las condiciones médicas/lesiones del usuario.

---

# 🎯 Flujos Principales

## Flujo 1: Registro e Inicio de Sesión

```
1. POST /auth/registro
   → Guarda token

2. Usa token en todos los endpoints siguientes
```

## Flujo 2: Configuración Inicial del Usuario

```
1. POST /perfil/salud
   → Configura objetivo y etiquetas

2. POST /perfil/mediciones
   → Registra primera medición

3. PATCH /perfil/unidades
   → Configura unidades preferidas
```

## Flujo 3: Activar y Seguir un Plan

```
1. GET /planes/catalogo?sugeridos=true
   → Ver planes sugeridos

2. GET /planes/catalogo/{id}
   → Ver detalle del plan

3. POST /usuario/planes/activar
   → Activar el plan

4. GET /usuario/registros/plan/hoy
   → Ver comidas del día

5. POST /usuario/registros/comidas
   → Registrar comida completada
```

## Flujo 4: Tracking Diario

```
1. GET /usuario/registros/plan/hoy
   → Ver actividades del día

2. POST /usuario/registros/comidas
   → Marcar comida como completada

3. GET /usuario/registros/rutina/hoy
   → Ver ejercicios del día

4. POST /usuario/registros/ejercicios
   → Registrar ejercicio completado
```

---

# 🛠️ Ejemplos de Integración

## Ejemplo JavaScript (Fetch API)

```javascript
// Configuración base
const API_BASE_URL = 'http://localhost:8080/api/v1';
let authToken = '';

// Login
async function login(email, password) {
  const response = await fetch(`${API_BASE_URL}/auth/login`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({ email, password })
  });
  
  const data = await response.json();
  if (data.success) {
    authToken = data.data.token;
    localStorage.setItem('token', authToken);
  }
  return data;
}

// Función helper para peticiones autenticadas
async function fetchWithAuth(endpoint, options = {}) {
  const token = localStorage.getItem('token');
  
  const config = {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`,
      ...options.headers
    }
  };
  
  const response = await fetch(`${API_BASE_URL}${endpoint}`, config);
  return response.json();
}

// Obtener perfil completo
async function getPerfilCompleto() {
  return fetchWithAuth('/perfil/completo');
}

// Ver catálogo de planes
async function getCatalogPlanes(sugeridos = false) {
  return fetchWithAuth(`/planes/catalogo?sugeridos=${sugeridos}`);
}

// Activar plan
async function activarPlan(planId, fechaInicio, notas = '') {
  return fetchWithAuth('/usuario/planes/activar', {
    method: 'POST',
    body: JSON.stringify({ planId, fechaInicio, notas })
  });
}

// Ver actividades del día
async function getActividadesHoy() {
  return fetchWithAuth('/usuario/registros/plan/hoy');
}

// Registrar comida
async function registrarComida(comidaId, tipoComida, porciones) {
  const fecha = new Date().toISOString().split('T')[0];
  const hora = new Date().toTimeString().split(' ')[0];
  
  return fetchWithAuth('/usuario/registros/comidas', {
    method: 'POST',
    body: JSON.stringify({
      comidaId,
      tipoComida,
      porciones,
      fecha,
      hora
    })
  });
}
```

## Ejemplo React Hook

```javascript
import { useState, useEffect } from 'react';

function useAuth() {
  const [token, setToken] = useState(localStorage.getItem('token'));
  const [user, setUser] = useState(null);

  async function login(email, password) {
    const response = await fetch(`${API_BASE_URL}/auth/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, password })
    });
    
    const data = await response.json();
    if (data.success) {
      setToken(data.data.token);
      localStorage.setItem('token', data.data.token);
      setUser(data.data);
    }
    return data;
  }

  function logout() {
    setToken(null);
    setUser(null);
    localStorage.removeItem('token');
  }

  return { token, user, login, logout };
}

function useAPI(endpoint) {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const token = localStorage.getItem('token');

  useEffect(() => {
    async function fetchData() {
      try {
        const response = await fetch(`${API_BASE_URL}${endpoint}`, {
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
          }
        });
        const result = await response.json();
        setData(result.data);
      } catch (err) {
        setError(err);
      } finally {
        setLoading(false);
      }
    }

    if (token) {
      fetchData();
    }
  }, [endpoint, token]);

  return { data, loading, error };
}

// Uso:
function Dashboard() {
  const { data: perfil } = useAPI('/perfil/completo');
  const { data: planActivo } = useAPI('/usuario/planes/activo');
  const { data: actividadesHoy } = useAPI('/usuario/registros/plan/hoy');

  return (
    <div>
      <h1>Bienvenido {perfil?.nombreCompleto}</h1>
      <p>Plan actual: {planActivo?.planNombre}</p>
      {/* ... */}
    </div>
  );
}
```

---

# 📝 Notas Finales

## Paginación
Muchos endpoints soportan paginación con estos parámetros:
- `page`: Número de página (comienza en 0)
- `size`: Elementos por página (default: 20)
- `sort`: Campo y dirección (ej: `nombre,asc`)

## Conversión de Unidades
- El peso se almacena siempre en KG en la base de datos
- La API convierte automáticamente según `unidadesMedida` del usuario
- Si el usuario usa LBS, todas las respuestas muestran peso en LBS

## Fechas y Horas
- **Formato de fechas**: `YYYY-MM-DD` (ISO 8601)
- **Formato de horas**: `HH:mm:ss` (24 horas)
- **Zona horaria**: Las fechas se manejan sin zona horaria (local)

## Validaciones Comunes
- Emails: Formato RFC 5322 + validación DNS
- Contraseñas: Mínimo 12 caracteres, complejidad requerida
- IDs: Números enteros positivos
- Strings: Límites de longitud definidos por campo

## Manejo de Errores
Todas las respuestas de error tienen este formato:
```json
{
  "success": false,
  "message": "Descripción del error",
  "data": null
}
```

---

# 🆘 Soporte

Para dudas o problemas:
- Ver documentación completa en Swagger: `http://localhost:8080/swagger-ui.html`
- Revisar tests unitarios en `src/test/java/**/*Test.java`
- Consultar reglas de negocio en `docs/REGLAS_NEGOCIO.MD`

---

**Versión del Documento:** 1.0  
**Última Actualización:** 19 de Noviembre 2025  
**API Version:** v1
