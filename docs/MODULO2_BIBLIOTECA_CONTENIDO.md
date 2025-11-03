# Módulo 2: Biblioteca de Contenido - Documentación

## Descripción General

El Módulo 2 implementa la **Biblioteca de Contenido** del sistema NutriTrack, permitiendo a los administradores gestionar ingredientes, ejercicios y comidas. Este módulo es exclusivo para usuarios con rol **ADMIN**.

## Características Principales

### 🔐 Seguridad
- Todos los endpoints requieren autenticación JWT
- Solo usuarios con `ROLE_ADMIN` pueden acceder
- Respuesta 403 Forbidden para usuarios sin permisos

### 📊 Funcionalidades

#### 1. Gestión de Ingredientes
- ✅ CRUD completo (Crear, Leer, Actualizar, Eliminar)
- ✅ Información nutricional por 100g (energía, proteínas, grasas, carbohidratos)
- ✅ Clasificación por grupo alimenticio (13 grupos)
- ✅ Búsqueda por nombre y grupo
- ✅ Gestión de etiquetas
- ✅ Validación de nombres duplicados

#### 2. Gestión de Ejercicios
- ✅ CRUD completo
- ✅ Clasificación por tipo (CARDIO, FUERZA, FLEXIBILIDAD, etc.)
- ✅ Clasificación por músculo principal (15 grupos musculares)
- ✅ Niveles de dificultad (PRINCIPIANTE, INTERMEDIO, AVANZADO, EXPERTO)
- ✅ Calorías estimadas y duración en minutos
- ✅ Filtros por tipo, músculo y dificultad
- ✅ Gestión de etiquetas

#### 3. Gestión de Comidas
- ✅ CRUD completo
- ✅ Clasificación por tipo (DESAYUNO, ALMUERZO, CENA, SNACK, etc.)
- ✅ Recetas con ingredientes y cantidades en gramos
- ✅ **Cálculo automático de valores nutricionales totales**
- ✅ Tiempo de elaboración en minutos
- ✅ Gestión individual de ingredientes en recetas

---

## Enumeraciones (Enums)

### TipoEjercicio
```
CARDIO, FUERZA, FLEXIBILIDAD, EQUILIBRIO, HIIT, 
YOGA, PILATES, FUNCIONAL, DEPORTIVO, OTRO
```

### MusculoPrincipal
```
PECHO, ESPALDA, HOMBROS, BRAZOS, BICEPS, TRICEPS, 
ABDOMINALES, PIERNAS, CUADRICEPS, GLUTEOS, GEMELOS, 
CARDIO, CUERPO_COMPLETO, CORE, OTRO
```

### Dificultad
```
PRINCIPIANTE, INTERMEDIO, AVANZADO, EXPERTO
```

### TipoComida
```
DESAYUNO, ALMUERZO, CENA, SNACK, 
PRE_ENTRENAMIENTO, POST_ENTRENAMIENTO, COLACION
```

### GrupoAlimenticio
```
FRUTAS, VERDURAS, CEREALES, LEGUMBRES, 
PROTEINAS_ANIMALES, PROTEINAS_VEGETALES, LACTEOS, 
GRASAS_SALUDABLES, AZUCARES, BEBIDAS, 
CONDIMENTOS, FRUTOS_SECOS, OTRO
```

---

## Endpoints API

### Base URL
```
http://localhost:8080
```

### Autenticación
Todos los endpoints requieren header:
```
Authorization: Bearer <admin-token>
```

---

## 1. Ingredientes (`/admin/ingredientes`)

### POST - Crear Ingrediente
**Endpoint:** `POST /admin/ingredientes`

**Request Body:**
```json
{
  "nombre": "Pechuga de Pollo",
  "grupoAlimenticio": "PROTEINAS_ANIMALES",
  "energia": 165.0,
  "proteinas": 31.0,
  "grasas": 3.6,
  "carbohidratos": 0.0,
  "etiquetasIds": [1, 2]
}
```

**Validaciones:**
- ✅ `nombre`: Obligatorio, máximo 255 caracteres, único
- ✅ `grupoAlimenticio`: Obligatorio, enum válido
- ✅ `energia`, `proteinas`, `grasas`, `carbohidratos`: Obligatorios, >= 0.0, máximo 3 enteros y 2 decimales
- ✅ `etiquetasIds`: Opcional, conjunto de IDs válidos

**Response 201:**
```json
{
  "id": 1,
  "nombre": "Pechuga de Pollo",
  "grupoAlimenticio": "PROTEINAS_ANIMALES",
  "energia": 165.00,
  "proteinas": 31.00,
  "grasas": 3.60,
  "carbohidratos": 0.00,
  "etiquetas": [
    {
      "id": 1,
      "nombre": "Alto en Proteína"
    }
  ],
  "createdAt": "2025-11-02T20:00:00",
  "updatedAt": "2025-11-02T20:00:00"
}
```

**Errores:**
- `400` - Validación fallida
- `403` - Usuario no es ADMIN
- `409` - Ingrediente con mismo nombre ya existe

---

### GET - Listar Todos
**Endpoint:** `GET /admin/ingredientes`

**Response 200:** Array de ingredientes

---

### GET - Buscar por ID
**Endpoint:** `GET /admin/ingredientes/{id}`

**Response 200:** Ingrediente con etiquetas

**Errores:**
- `404` - Ingrediente no encontrado

---

### GET - Buscar por Nombre
**Endpoint:** `GET /admin/ingredientes/buscar?nombre=pollo`

**Query Params:**
- `nombre`: String (búsqueda case-insensitive, contiene)

**Response 200:** Array de ingredientes coincidentes

---

### GET - Buscar por Grupo
**Endpoint:** `GET /admin/ingredientes/grupo/{grupo}`

**Path Params:**
- `grupo`: GrupoAlimenticio enum

**Ejemplo:**
```
GET /admin/ingredientes/grupo/PROTEINAS_ANIMALES
```

**Response 200:** Array de ingredientes del grupo

---

### PUT - Actualizar
**Endpoint:** `PUT /admin/ingredientes/{id}`

**Request Body:** Igual que crear

**Response 200:** Ingrediente actualizado

**Errores:**
- `404` - Ingrediente no encontrado
- `409` - Nombre duplicado con otro ingrediente

---

### POST - Agregar Etiqueta
**Endpoint:** `POST /admin/ingredientes/{ingredienteId}/etiquetas/{etiquetaId}`

**Response 200:** Ingrediente con nueva etiqueta

**Errores:**
- `404` - Ingrediente o etiqueta no encontrada

---

### DELETE - Remover Etiqueta
**Endpoint:** `DELETE /admin/ingredientes/{ingredienteId}/etiquetas/{etiquetaId}`

**Response 200:** Ingrediente actualizado

---

### DELETE - Eliminar
**Endpoint:** `DELETE /admin/ingredientes/{id}`

**Response 204:** Sin contenido

**Errores:**
- `404` - Ingrediente no encontrado

---

## 2. Ejercicios (`/admin/ejercicios`)

### POST - Crear Ejercicio
**Endpoint:** `POST /admin/ejercicios`

**Request Body:**
```json
{
  "nombre": "Sentadillas",
  "tipoEjercicio": "FUERZA",
  "musculoPrincipal": "PIERNAS",
  "duracion": 30,
  "dificultad": "INTERMEDIO",
  "caloriasEstimadas": 150.0,
  "etiquetasIds": [1]
}
```

**Validaciones:**
- ✅ `nombre`: Obligatorio, máximo 150 caracteres, único
- ✅ `tipoEjercicio`: Obligatorio, enum válido
- ✅ `musculoPrincipal`: Opcional, enum válido
- ✅ `duracion`: Opcional, >= 1 minuto
- ✅ `dificultad`: Opcional, enum válido
- ✅ `caloriasEstimadas`: Opcional, >= 0.0, máximo 4 enteros y 2 decimales

**Response 201:**
```json
{
  "id": 1,
  "nombre": "Sentadillas",
  "tipoEjercicio": "FUERZA",
  "musculoPrincipal": "PIERNAS",
  "duracion": 30,
  "dificultad": "INTERMEDIO",
  "caloriasEstimadas": 150.00,
  "etiquetas": [
    {
      "id": 1,
      "nombre": "Tren Inferior"
    }
  ],
  "createdAt": "2025-11-02T20:00:00",
  "updatedAt": "2025-11-02T20:00:00"
}
```

---

### GET - Listar Todos
**Endpoint:** `GET /admin/ejercicios`

---

### GET - Buscar por ID
**Endpoint:** `GET /admin/ejercicios/{id}`

---

### GET - Buscar por Tipo
**Endpoint:** `GET /admin/ejercicios/tipo/{tipo}`

**Ejemplo:**
```
GET /admin/ejercicios/tipo/CARDIO
```

---

### GET - Buscar por Músculo
**Endpoint:** `GET /admin/ejercicios/musculo/{musculo}`

**Ejemplo:**
```
GET /admin/ejercicios/musculo/PIERNAS
```

---

### GET - Buscar por Dificultad
**Endpoint:** `GET /admin/ejercicios/dificultad/{dificultad}`

**Ejemplo:**
```
GET /admin/ejercicios/dificultad/INTERMEDIO
```

---

### PUT - Actualizar
**Endpoint:** `PUT /admin/ejercicios/{id}`

---

### POST/DELETE - Gestión de Etiquetas
Igual que ingredientes.

---

### DELETE - Eliminar
**Endpoint:** `DELETE /admin/ejercicios/{id}`

---

## 3. Comidas (`/admin/comidas`)

### POST - Crear Comida
**Endpoint:** `POST /admin/comidas`

**Request Body:**
```json
{
  "nombre": "Pollo con Arroz y Brócoli",
  "tipoComida": "ALMUERZO",
  "tiempoElaboracion": 30,
  "ingredientes": [
    {
      "idIngrediente": 1,
      "cantidad": 150
    },
    {
      "idIngrediente": 2,
      "cantidad": 100
    },
    {
      "idIngrediente": 3,
      "cantidad": 80
    }
  ]
}
```

**Validaciones:**
- ✅ `nombre`: Obligatorio, máximo 255 caracteres, único
- ✅ `tipoComida`: Obligatorio, enum válido
- ✅ `tiempoElaboracion`: Opcional, >= 1 minuto
- ✅ `ingredientes`: Opcional, lista de ingredientes con cantidades
- ✅ `idIngrediente`: Obligatorio, debe existir
- ✅ `cantidad`: Obligatoria, en gramos

**Response 201:**
```json
{
  "id": 1,
  "nombre": "Pollo con Arroz y Brócoli",
  "tipoComida": "ALMUERZO",
  "tiempoElaboracion": 30,
  "ingredientes": [
    {
      "id": 1,
      "nombre": "Pechuga de Pollo",
      "cantidad": 150.00
    },
    {
      "id": 2,
      "nombre": "Arroz Integral",
      "cantidad": 100.00
    },
    {
      "id": 3,
      "nombre": "Brócoli",
      "cantidad": 80.00
    }
  ],
  "nutricionTotal": {
    "energiaTotal": 385.70,
    "proteinasTotal": 49.74,
    "grasasTotal": 6.72,
    "carbohidratosTotal": 28.60
  },
  "createdAt": "2025-11-02T20:00:00",
  "updatedAt": "2025-11-02T20:00:00"
}
```

**⚠️ Cálculo Nutricional:**

El sistema calcula automáticamente los valores nutricionales totales de la comida basándose en:
1. Los valores nutricionales de cada ingrediente (por 100g)
2. La cantidad en gramos de cada ingrediente en la receta

**Fórmula:**
```
Valor Total = Σ (Valor del Ingrediente × Cantidad / 100)
```

**Ejemplo:**
- Pollo: 165 kcal/100g × 150g / 100 = 247.5 kcal
- Arroz: 111 kcal/100g × 100g / 100 = 111.0 kcal
- Brócoli: 34 kcal/100g × 80g / 100 = 27.2 kcal
- **Total: 385.7 kcal**

---

### GET - Listar Todas
**Endpoint:** `GET /admin/comidas`

**Response 200:** Array de comidas con nutrición total calculada

---

### GET - Buscar por ID
**Endpoint:** `GET /admin/comidas/{id}`

**Response 200:** Comida con ingredientes y nutrición total

---

### GET - Buscar por Tipo
**Endpoint:** `GET /admin/comidas/tipo/{tipo}`

**Ejemplo:**
```
GET /admin/comidas/tipo/ALMUERZO
```

---

### PUT - Actualizar
**Endpoint:** `PUT /admin/comidas/{id}`

**Nota:** Al actualizar, se eliminan todas las recetas anteriores y se crean las nuevas.

---

### POST - Agregar Ingrediente
**Endpoint:** `POST /admin/comidas/{comidaId}/ingredientes/{ingredienteId}?cantidad=150`

**Query Params:**
- `cantidad`: BigDecimal (gramos)

**Response 200:** Comida actualizada con nueva nutrición total

---

### DELETE - Remover Ingrediente
**Endpoint:** `DELETE /admin/comidas/{comidaId}/ingredientes/{ingredienteId}`

**Response 200:** Comida actualizada con nutrición total recalculada

---

### DELETE - Eliminar
**Endpoint:** `DELETE /admin/comidas/{id}`

**Nota:** Elimina la comida y todas sus recetas asociadas.

**Response 204:** Sin contenido

---

## Testing

### Unit Tests (Mockito)

**Total: 54 tests pasados ✅**

- **IngredienteServiceTest**: 18 tests
  - Crear, actualizar, eliminar ingredientes
  - Búsquedas por ID, nombre, grupo
  - Gestión de etiquetas
  - Validaciones de duplicados

- **EjercicioServiceTest**: 17 tests
  - CRUD completo
  - Filtros por tipo, músculo, dificultad
  - Gestión de etiquetas
  - Validaciones

- **ComidaServiceTest**: 19 tests
  - CRUD con recetas
  - Cálculo de nutrición total
  - Gestión de ingredientes
  - Validaciones de existencia

**Ejecutar tests:**
```bash
./mvnw test -Dtest="IngredienteServiceTest,EjercicioServiceTest,ComidaServiceTest"
```

---

## Postman Collection

### Importar Colección
1. Abrir Postman
2. Importar archivo: `postman/Modulo2_BibliotecaContenido.postman_collection.json`
3. Configurar variable `baseUrl`: `http://localhost:8080`

### Flujo de Pruebas

#### 1. Autenticación
```
POST /auth/login
Body: { "email": "admin@fintech.com", "password": "admin123" }
```
El token se guarda automáticamente en `{{adminToken}}`.

#### 2. Crear Ingredientes
- Pechuga de Pollo (PROTEINAS_ANIMALES)
- Arroz Integral (CEREALES)
- Brócoli (VERDURAS)

#### 3. Crear Ejercicios
- Sentadillas (FUERZA - PIERNAS)
- Correr (CARDIO)
- Flexiones (FUERZA - PECHO)

#### 4. Crear Comidas
- Pollo con Arroz y Brócoli (ALMUERZO)
  - Verifica el cálculo automático de nutrición total
- Ensalada de Brócoli (CENA)

#### 5. Pruebas de Búsqueda
- Buscar ingredientes por grupo
- Filtrar ejercicios por tipo/músculo/dificultad
- Listar comidas por tipo

#### 6. Pruebas de Actualización
- Actualizar valores nutricionales
- Modificar recetas de comidas
- Gestionar etiquetas

---

## Swagger UI

### Acceso
```
http://localhost:8080/swagger-ui/index.html
```

### Grupos de Endpoints

**Ingredientes (Admin)**
- 10 endpoints documentados
- Incluye ejemplos de request/response
- Documentación de errores comunes

**Ejercicios (Admin)**
- 10 endpoints documentados
- Filtros por múltiples criterios

**Comidas (Admin)**
- 9 endpoints documentados
- Ejemplos de cálculo nutricional

### Autorización en Swagger
1. Click en botón "Authorize"
2. Ingresar: `Bearer <admin-token>`
3. Click "Authorize"
4. Probar endpoints

---

## Base de Datos

### Tablas Creadas

#### `ingredientes`
```sql
- id (BIGSERIAL PRIMARY KEY)
- nombre (VARCHAR(255) UNIQUE NOT NULL)
- grupo_alimenticio (VARCHAR(50))
- energia (DECIMAL(5,2))
- proteinas (DECIMAL(5,2))
- grasas (DECIMAL(5,2))
- carbohidratos (DECIMAL(5,2))
- created_at (TIMESTAMP)
- updated_at (TIMESTAMP)
```

#### `ejercicios`
```sql
- id (BIGSERIAL PRIMARY KEY)
- nombre (VARCHAR(150) UNIQUE NOT NULL)
- tipo_ejercicio (VARCHAR(50))
- musculo_principal (VARCHAR(50))
- duracion (INTEGER) -- minutos
- dificultad (VARCHAR(20))
- calorias_estimadas (DECIMAL(6,2))
- created_at (TIMESTAMP)
- updated_at (TIMESTAMP)
```

#### `comidas`
```sql
- id (BIGSERIAL PRIMARY KEY)
- nombre (VARCHAR(255) UNIQUE NOT NULL)
- tipo_comida (VARCHAR(50))
- tiempo_elaboracion (INTEGER) -- minutos
- created_at (TIMESTAMP)
- updated_at (TIMESTAMP)
```

#### `recetas` (Join Table)
```sql
- id_comida (BIGINT, FK comidas)
- id_ingrediente (BIGINT, FK ingredientes)
- cantidad_ingrediente (DECIMAL(5,2)) -- gramos
- PRIMARY KEY (id_comida, id_ingrediente)
```

#### `etiquetas_ingredientes` (Join Table)
```sql
- etiqueta_id (BIGINT, FK etiquetas)
- ingrediente_id (BIGINT, FK ingredientes)
- PRIMARY KEY (etiqueta_id, ingrediente_id)
```

#### `etiquetas_ejercicios` (Join Table)
```sql
- etiqueta_id (BIGINT, FK etiquetas)
- ejercicio_id (BIGINT, FK ejercicios)
- PRIMARY KEY (etiqueta_id, ejercicio_id)
```

---

## Reglas de Negocio

### Ingredientes
- ✅ RN-01: Nombres únicos (case-insensitive)
- ✅ RN-02: Valores nutricionales >= 0
- ✅ RN-03: Máximo 3 dígitos enteros, 2 decimales
- ✅ RN-04: Grupo alimenticio obligatorio

### Ejercicios
- ✅ RN-05: Nombres únicos (case-insensitive)
- ✅ RN-06: Tipo de ejercicio obligatorio
- ✅ RN-07: Duración mínima 1 minuto
- ✅ RN-08: Calorías estimadas >= 0

### Comidas
- ✅ RN-09: Nombres únicos (case-insensitive)
- ✅ RN-10: Tipo de comida obligatorio
- ✅ RN-11: Ingredientes deben existir
- ✅ RN-12: Cantidad mínima 0 gramos
- ✅ RN-13: Cálculo automático de nutrición total
- ✅ RN-14: Al eliminar comida, eliminar recetas

---

## Manejo de Errores

### Códigos de Estado

| Código | Descripción | Ejemplo |
|--------|-------------|---------|
| 200 | OK | Consulta exitosa |
| 201 | Created | Recurso creado |
| 204 | No Content | Eliminación exitosa |
| 400 | Bad Request | Validación fallida |
| 403 | Forbidden | Usuario no es ADMIN |
| 404 | Not Found | Recurso no encontrado |
| 409 | Conflict | Nombre duplicado |

### Formato de Error
```json
{
  "timestamp": "2025-11-02T20:00:00",
  "status": 409,
  "error": "Conflict",
  "message": "Ya existe un ingrediente con el nombre: Pollo",
  "path": "/admin/ingredientes"
}
```

---

## Próximos Pasos

### Módulo 3: Planes Nutricionales
- Crear planes personalizados
- Asignar comidas a días/horarios
- Calcular totales nutricionales del plan

### Módulo 4: Rutinas de Ejercicio
- Crear rutinas personalizadas
- Asignar ejercicios con series/repeticiones
- Calcular calorías totales de la rutina

### Módulo 5: Seguimiento y Reportes
- Historial de comidas consumidas
- Historial de ejercicios realizados
- Gráficos de progreso
- Reportes nutricionales

---

## Recursos Adicionales

### Archivos de Configuración
- `pom.xml`: Dependencias Maven
- `application.properties`: Configuración de BD y Swagger
- `SecurityConfig.java`: Configuración de seguridad

### Colecciones Postman
- `postman/Modulo2_BibliotecaContenido.postman_collection.json`

### Tests
- `src/test/java/com/nutritrack/nutritrackapi/service/*ServiceTest.java`

---

## Contacto y Soporte

Para dudas o sugerencias sobre el Módulo 2:
- GitHub Issues: [nutritrack-api/issues](https://github.com/leonelalz/nutritrack-api/issues)
- Documentación Swagger: `http://localhost:8080/swagger-ui/index.html`

---

**Módulo 2 - Biblioteca de Contenido v1.0**  
*Última actualización: 2 de noviembre de 2025*
