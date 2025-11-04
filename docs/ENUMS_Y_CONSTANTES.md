# 🔢 ENUMS Y CONSTANTES - REFERENCIA COMPLETA

> **Documento de Referencia para Reconstrucción**  
> Contiene todos los enums, constantes y valores fijos del sistema.  
> Compatible con Spring Boot 3.5.7 + Java 21

---

## 📋 ÍNDICE DE CONTENIDOS

### ENUMS DEL SISTEMA
1. [TipoRol](#tiporol)
2. [TipoEtiqueta](#tipoetiqueta)
3. [UnidadesMedida](#unidadesmedida)
4. [ObjetivoSalud](#objetivosalud)
5. [NivelActividad](#nivelactividad)
6. [GrupoAlimenticio](#grupoalimenticio)
7. [TipoEjercicio](#tipoejercicio)
8. [Dificultad](#dificultad)
9. [MusculoPrincipal](#musculoprincipal)
10. [TipoComida](#tipocomida)
11. [EstadoAsignacion](#estadoasignacion)

### CONSTANTES
12. [Constantes de Aplicación](#constantes-de-aplicación)
13. [Mensajes de Error](#mensajes-de-error)
14. [Validaciones](#validaciones)

---

## 👥 TIPOROL

```java
package com.nutritrack.nutritrackapi.model;

/**
 * Tipos de roles de usuario en el sistema.
 */
public enum TipoRol {
    /**
     * Usuario estándar - Acceso básico a la aplicación.
     */
    USUARIO("Usuario estándar"),
    
    /**
     * Nutricionista - Puede crear y gestionar planes de alimentación.
     */
    NUTRICIONISTA("Nutricionista profesional"),
    
    /**
     * Entrenador - Puede crear y gestionar rutinas de ejercicio.
     */
    ENTRENADOR("Entrenador profesional"),
    
    /**
     * Administrador - Acceso total al sistema.
     */
    ADMIN("Administrador del sistema");
    
    private final String descripcion;
    
    TipoRol(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
}
```

---

## 🏷️ TIPOETIQUETA

```java
package com.nutritrack.nutritrackapi.model;

/**
 * Tipos de etiquetas para categorizar información de salud y preferencias.
 */
public enum TipoEtiqueta {
    /**
     * Alergias alimentarias (ej: lactosa, gluten, mariscos).
     */
    ALERGIA("Alergia alimentaria"),
    
    /**
     * Condiciones médicas (ej: diabetes, hipertensión, celíaca).
     */
    CONDICION_MEDICA("Condición médica"),
    
    /**
     * Preferencias dietéticas (ej: vegetariano, vegano, kosher).
     */
    PREFERENCIA_DIETETICA("Preferencia dietética"),
    
    /**
     * Objetivos de salud (ej: pérdida de peso, ganancia muscular).
     */
    OBJETIVO("Objetivo de salud"),
    
    /**
     * Restricciones alimentarias generales (ej: bajo en sodio).
     */
    RESTRICCION("Restricción alimentaria"),
    
    /**
     * Otras categorías no especificadas.
     */
    OTRA("Otra categoría");
    
    private final String descripcion;
    
    TipoEtiqueta(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
}
```

---

## 📏 UNIDADESMEDIDA

```java
package com.nutritrack.nutritrackapi.model;

/**
 * Unidades de medida para peso y altura del usuario.
 */
public enum UnidadesMedida {
    /**
     * Sistema métrico: kilogramos y centímetros.
     */
    KG("Kilogramos y centímetros"),
    
    /**
     * Sistema imperial: libras y pulgadas.
     */
    LBS("Libras y pulgadas");
    
    private final String descripcion;
    
    UnidadesMedida(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
}
```

---

## 🎯 OBJETIVOSALUD

```java
package com.nutritrack.nutritrackapi.model;

/**
 * Objetivos de salud del usuario.
 */
public enum ObjetivoSalud {
    /**
     * Reducir peso corporal.
     */
    PERDER_PESO("Perder peso"),
    
    /**
     * Aumentar masa muscular.
     */
    GANAR_MASA_MUSCULAR("Ganar masa muscular"),
    
    /**
     * Mantener peso y condición física actual.
     */
    MANTENER_PESO("Mantener peso"),
    
    /**
     * Mejorar condición física general.
     */
    MEJORAR_CONDICION_FISICA("Mejorar condición física"),
    
    /**
     * Tonificar y definir músculos.
     */
    TONIFICAR("Tonificar"),
    
    /**
     * Aumentar resistencia cardiovascular.
     */
    MEJORAR_RESISTENCIA("Mejorar resistencia"),
    
    /**
     * Aumentar flexibilidad.
     */
    MEJORAR_FLEXIBILIDAD("Mejorar flexibilidad"),
    
    /**
     * Objetivo personalizado.
     */
    PERSONALIZADO("Objetivo personalizado");
    
    private final String descripcion;
    
    ObjetivoSalud(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
}
```

---

## 🏃 NIVELACTIVIDAD

```java
package com.nutritrack.nutritrackapi.model;

/**
 * Nivel de actividad física del usuario.
 */
public enum NivelActividad {
    /**
     * Poco o ningún ejercicio.
     */
    BAJO("Sedentario - Poco o ningún ejercicio"),
    
    /**
     * Ejercicio ligero 1-3 días por semana.
     */
    MODERADO("Moderado - Ejercicio ligero 1-3 días/semana"),
    
    /**
     * Ejercicio moderado 3-5 días por semana.
     */
    ALTO("Alto - Ejercicio moderado 3-5 días/semana"),
    
    /**
     * Ejercicio intenso 6-7 días por semana.
     */
    MUY_ALTO("Muy alto - Ejercicio intenso 6-7 días/semana"),
    
    /**
     * Atleta o entrenamiento muy intenso diario.
     */
    EXTREMO("Extremo - Atleta o entrenamiento muy intenso");
    
    private final String descripcion;
    
    NivelActividad(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
}
```

---

## 🥗 GRUPOALIMENTICIO

```java
package com.nutritrack.nutritrackapi.model;

/**
 * Grupos alimenticios para categorizar ingredientes.
 */
public enum GrupoAlimenticio {
    /**
     * Carnes, pescados, huevos, lácteos.
     */
    PROTEINAS_ANIMALES("Proteínas animales"),
    
    /**
     * Legumbres, tofu, tempeh, seitán.
     */
    PROTEINAS_VEGETALES("Proteínas vegetales"),
    
    /**
     * Arroz, pasta, pan, cereales.
     */
    CEREALES("Cereales y granos"),
    
    /**
     * Verduras frescas y congeladas.
     */
    VERDURAS("Verduras"),
    
    /**
     * Frutas frescas y secas.
     */
    FRUTAS("Frutas"),
    
    /**
     * Leche, yogur, queso.
     */
    LACTEOS("Lácteos"),
    
    /**
     * Aceites, mantequilla, frutos secos.
     */
    GRASAS("Grasas y aceites"),
    
    /**
     * Azúcares, dulces, postres.
     */
    AZUCARES("Azúcares y dulces"),
    
    /**
     * Agua, jugos, bebidas.
     */
    BEBIDAS("Bebidas"),
    
    /**
     * Otros alimentos no categorizados.
     */
    OTROS("Otros");
    
    private final String descripcion;
    
    GrupoAlimenticio(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
}
```

---

## 💪 TIPOEJERCICIO

```java
package com.nutritrack.nutritrackapi.model;

/**
 * Tipos de ejercicio físico.
 */
public enum TipoEjercicio {
    /**
     * Ejercicios aeróbicos (correr, nadar, ciclismo).
     */
    CARDIO("Ejercicio cardiovascular"),
    
    /**
     * Entrenamiento con pesas o resistencia.
     */
    FUERZA("Entrenamiento de fuerza"),
    
    /**
     * Ejercicios de estiramiento y movilidad.
     */
    FLEXIBILIDAD("Flexibilidad y estiramiento"),
    
    /**
     * Ejercicios de equilibrio y coordinación.
     */
    EQUILIBRIO("Equilibrio y coordinación"),
    
    /**
     * Ejercicios pliométricos y explosivos.
     */
    POTENCIA("Potencia y explosividad"),
    
    /**
     * Ejercicios de resistencia muscular.
     */
    RESISTENCIA("Resistencia muscular"),
    
    /**
     * Deportes y actividades recreativas.
     */
    DEPORTE("Deporte"),
    
    /**
     * Combinación de varios tipos.
     */
    MIXTO("Entrenamiento mixto"),
    
    /**
     * Otros tipos de ejercicio.
     */
    OTRO("Otro");
    
    private final String descripcion;
    
    TipoEjercicio(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
}
```

---

## ⚡ DIFICULTAD

```java
package com.nutritrack.nutritrackapi.model;

/**
 * Nivel de dificultad de un ejercicio.
 */
public enum Dificultad {
    /**
     * Para personas sin experiencia o con limitaciones.
     */
    PRINCIPIANTE("Principiante - Sin experiencia"),
    
    /**
     * Para personas con experiencia básica.
     */
    INTERMEDIO("Intermedio - Experiencia básica"),
    
    /**
     * Para personas con experiencia avanzada.
     */
    AVANZADO("Avanzado - Experiencia avanzada"),
    
    /**
     * Para atletas y profesionales.
     */
    EXPERTO("Experto - Nivel profesional");
    
    private final String descripcion;
    
    Dificultad(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
}
```

---

## 🦾 MUSCULOPRINCIPAL

```java
package com.nutritrack.nutritrackapi.model;

/**
 * Músculo principal trabajado en un ejercicio.
 */
public enum MusculoPrincipal {
    /**
     * Músculos del pecho (pectorales).
     */
    PECHO("Pecho"),
    
    /**
     * Músculos de la espalda (dorsales, trapecio).
     */
    ESPALDA("Espalda"),
    
    /**
     * Músculos de los hombros (deltoides).
     */
    HOMBROS("Hombros"),
    
    /**
     * Músculos de los brazos (bíceps, tríceps).
     */
    BRAZOS("Brazos"),
    
    /**
     * Músculos abdominales.
     */
    ABDOMEN("Abdomen"),
    
    /**
     * Músculos de las piernas (cuádriceps, isquiotibiales).
     */
    PIERNAS("Piernas"),
    
    /**
     * Músculos de los glúteos.
     */
    GLUTEOS("Glúteos"),
    
    /**
     * Músculos de las pantorrillas.
     */
    PANTORRILLAS("Pantorrillas"),
    
    /**
     * Ejercicio de cuerpo completo.
     */
    CUERPO_COMPLETO("Cuerpo completo"),
    
    /**
     * Otros músculos no especificados.
     */
    OTRO("Otro");
    
    private final String descripcion;
    
    MusculoPrincipal(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
}
```

---

## 🍽️ TIPOCOMIDA

```java
package com.nutritrack.nutritrackapi.model;

/**
 * Tipo de comida según el momento del día.
 */
public enum TipoComida {
    /**
     * Primera comida del día.
     */
    DESAYUNO("Desayuno"),
    
    /**
     * Comida entre desayuno y almuerzo.
     */
    MEDIA_MANANA("Media mañana"),
    
    /**
     * Comida principal del mediodía.
     */
    ALMUERZO("Almuerzo"),
    
    /**
     * Comida entre almuerzo y cena.
     */
    MERIENDA("Merienda"),
    
    /**
     * Última comida principal del día.
     */
    CENA("Cena"),
    
    /**
     * Comida antes de dormir.
     */
    POST_CENA("Post-cena"),
    
    /**
     * Comida antes del entrenamiento.
     */
    PRE_ENTRENAMIENTO("Pre-entrenamiento"),
    
    /**
     * Comida después del entrenamiento.
     */
    POST_ENTRENAMIENTO("Post-entrenamiento"),
    
    /**
     * Snack o colación.
     */
    SNACK("Snack"),
    
    /**
     * Otros tipos de comida.
     */
    OTRA("Otra");
    
    private final String descripcion;
    
    TipoComida(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
}
```

---

## 📊 ESTADOASIGNACION

```java
package com.nutritrack.nutritrackapi.model;

/**
 * Estado de una asignación de plan o rutina a un usuario.
 */
public enum EstadoAsignacion {
    /**
     * Asignación en curso.
     */
    ACTIVO("Activo"),
    
    /**
     * Asignación pausada temporalmente.
     */
    PAUSADO("Pausado"),
    
    /**
     * Asignación completada exitosamente.
     */
    COMPLETADO("Completado"),
    
    /**
     * Asignación cancelada antes de completarse.
     */
    CANCELADO("Cancelado");
    
    private final String descripcion;
    
    EstadoAsignacion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
}
```

---

## 🔧 CONSTANTES DE APLICACIÓN

### AppConstants.java

```java
package com.nutritrack.nutritrackapi.constants;

/**
 * Constantes generales de la aplicación.
 */
public final class AppConstants {
    
    private AppConstants() {
        throw new UnsupportedOperationException("Esta es una clase de constantes");
    }
    
    // ========== PAGINACIÓN ==========
    
    /**
     * Tamaño de página por defecto.
     */
    public static final int DEFAULT_PAGE_SIZE = 10;
    
    /**
     * Tamaño máximo de página.
     */
    public static final int MAX_PAGE_SIZE = 100;
    
    /**
     * Página inicial (0-indexed).
     */
    public static final int DEFAULT_PAGE_NUMBER = 0;
    
    /**
     * Campo de ordenamiento por defecto.
     */
    public static final String DEFAULT_SORT_BY = "id";
    
    /**
     * Dirección de ordenamiento por defecto.
     */
    public static final String DEFAULT_SORT_DIRECTION = "asc";
    
    // ========== VALIDACIÓN DE MEDIDAS ==========
    
    /**
     * Peso mínimo permitido en kg.
     */
    public static final double MIN_PESO_KG = 20.0;
    
    /**
     * Peso máximo permitido en kg.
     */
    public static final double MAX_PESO_KG = 600.0;
    
    /**
     * Altura mínima permitida en cm.
     */
    public static final double MIN_ALTURA_CM = 50.0;
    
    /**
     * Altura máxima permitida en cm.
     */
    public static final double MAX_ALTURA_CM = 300.0;
    
    /**
     * IMC mínimo saludable.
     */
    public static final double MIN_IMC_SALUDABLE = 18.5;
    
    /**
     * IMC máximo saludable.
     */
    public static final double MAX_IMC_SALUDABLE = 24.9;
    
    // ========== VALIDACIÓN DE MACRONUTRIENTES ==========
    
    /**
     * Calorías mínimas diarias recomendadas.
     */
    public static final int MIN_CALORIAS_DIARIAS = 500;
    
    /**
     * Calorías máximas diarias recomendadas.
     */
    public static final int MAX_CALORIAS_DIARIAS = 5000;
    
    /**
     * Gramos máximos de proteína por 100g de alimento.
     */
    public static final double MAX_PROTEINAS_100G = 100.0;
    
    /**
     * Gramos máximos de carbohidratos por 100g de alimento.
     */
    public static final double MAX_CARBOHIDRATOS_100G = 100.0;
    
    /**
     * Gramos máximos de grasas por 100g de alimento.
     */
    public static final double MAX_GRASAS_100G = 100.0;
    
    // ========== VALIDACIÓN DE PLANES Y RUTINAS ==========
    
    /**
     * Duración mínima de un plan en días.
     */
    public static final int MIN_DURACION_PLAN_DIAS = 1;
    
    /**
     * Duración máxima de un plan en días.
     */
    public static final int MAX_DURACION_PLAN_DIAS = 365;
    
    /**
     * Duración mínima de una rutina en semanas.
     */
    public static final int MIN_DURACION_RUTINA_SEMANAS = 1;
    
    /**
     * Duración máxima de una rutina en semanas.
     */
    public static final int MAX_DURACION_RUTINA_SEMANAS = 52;
    
    // ========== VALIDACIÓN DE EJERCICIOS ==========
    
    /**
     * Número mínimo de series por ejercicio.
     */
    public static final int MIN_SERIES = 1;
    
    /**
     * Número máximo de series por ejercicio.
     */
    public static final int MAX_SERIES = 20;
    
    /**
     * Número mínimo de repeticiones.
     */
    public static final int MIN_REPETICIONES = 1;
    
    /**
     * Número máximo de repeticiones.
     */
    public static final int MAX_REPETICIONES = 100;
    
    /**
     * Duración mínima de ejercicio en minutos.
     */
    public static final int MIN_DURACION_EJERCICIO_MIN = 1;
    
    /**
     * Duración máxima de ejercicio en minutos.
     */
    public static final int MAX_DURACION_EJERCICIO_MIN = 300;
    
    /**
     * Peso máximo en kg para ejercicios.
     */
    public static final double MAX_PESO_EJERCICIO_KG = 500.0;
    
    // ========== JWT Y SEGURIDAD ==========
    
    /**
     * Header HTTP para el token JWT.
     */
    public static final String AUTHORIZATION_HEADER = "Authorization";
    
    /**
     * Prefijo del token Bearer.
     */
    public static final String BEARER_PREFIX = "Bearer ";
    
    /**
     * Longitud mínima del token Bearer.
     */
    public static final int MIN_BEARER_TOKEN_LENGTH = 7;
    
    // ========== FORMATOS DE FECHA ==========
    
    /**
     * Formato de fecha por defecto.
     */
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    
    /**
     * Formato de fecha y hora por defecto.
     */
    public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    
    // ========== OTROS ==========
    
    /**
     * Versión de la API.
     */
    public static final String API_VERSION = "v1";
    
    /**
     * Nombre de la aplicación.
     */
    public static final String APP_NAME = "NutriTrack API";
    
    /**
     * Descripción de la aplicación.
     */
    public static final String APP_DESCRIPTION = "API para gestión de nutrición y ejercicio";
}
```

---

## 💬 MENSAJES DE ERROR

### ErrorMessages.java

```java
package com.nutritrack.nutritrackapi.constants;

/**
 * Mensajes de error estándar de la aplicación.
 */
public final class ErrorMessages {
    
    private ErrorMessages() {
        throw new UnsupportedOperationException("Esta es una clase de constantes");
    }
    
    // ========== AUTENTICACIÓN ==========
    
    public static final String INVALID_CREDENTIALS = "Credenciales inválidas";
    public static final String EMAIL_ALREADY_EXISTS = "El email ya está registrado";
    public static final String USER_NOT_FOUND = "Usuario no encontrado";
    public static final String TOKEN_EXPIRED = "Token expirado";
    public static final String INVALID_TOKEN = "Token inválido";
    public static final String UNAUTHORIZED = "No autorizado";
    
    // ========== RECURSOS NO ENCONTRADOS ==========
    
    public static final String PERFIL_NOT_FOUND = "Perfil no encontrado con id: %d";
    public static final String INGREDIENTE_NOT_FOUND = "Ingrediente no encontrado con id: %d";
    public static final String EJERCICIO_NOT_FOUND = "Ejercicio no encontrado con id: %d";
    public static final String COMIDA_NOT_FOUND = "Comida no encontrada con id: %d";
    public static final String PLAN_NOT_FOUND = "Plan no encontrado con id: %d";
    public static final String RUTINA_NOT_FOUND = "Rutina no encontrada con id: %d";
    public static final String ETIQUETA_NOT_FOUND = "Etiqueta no encontrada con id: %d";
    public static final String USUARIO_PLAN_NOT_FOUND = "Asignación de plan no encontrada con id: %d";
    public static final String USUARIO_RUTINA_NOT_FOUND = "Asignación de rutina no encontrada con id: %d";
    
    // ========== RECURSOS YA EXISTEN (RN07) ==========
    
    public static final String INGREDIENTE_ALREADY_EXISTS = "Ya existe un ingrediente con el nombre: %s";
    public static final String EJERCICIO_ALREADY_EXISTS = "Ya existe un ejercicio con el nombre: %s";
    public static final String COMIDA_ALREADY_EXISTS = "Ya existe una comida con el nombre: %s";
    public static final String PLAN_ALREADY_EXISTS = "Ya existe un plan con el nombre: %s";
    public static final String RUTINA_ALREADY_EXISTS = "Ya existe una rutina con el nombre: %s";
    public static final String ETIQUETA_ALREADY_EXISTS = "Ya existe una etiqueta con el nombre: %s";
    
    // ========== VALIDACIONES DE DEPENDENCIAS (RN08, RN09) ==========
    
    public static final String INGREDIENTE_HAS_DEPENDENCIES = "No se puede eliminar el ingrediente porque está siendo usado en %d recetas";
    public static final String EJERCICIO_HAS_DEPENDENCIES = "No se puede eliminar el ejercicio porque está siendo usado en %d rutinas";
    public static final String PLAN_HAS_ACTIVE_ASSIGNMENTS = "No se puede eliminar el plan porque tiene %d asignaciones activas";
    public static final String RUTINA_HAS_ACTIVE_ASSIGNMENTS = "No se puede eliminar la rutina porque tiene %d asignaciones activas";
    
    // ========== VALIDACIONES DE ASIGNACIÓN (RN17, RN18) ==========
    
    public static final String PLAN_ALREADY_ACTIVE = "El usuario ya tiene un plan activo. Debe pausar o cancelar el plan actual primero";
    public static final String RUTINA_ALREADY_ACTIVE = "El usuario ya tiene una rutina activa. Debe pausar o cancelar la rutina actual primero";
    public static final String PLAN_CANNOT_BE_REPLACED = "No se puede asignar el plan porque el usuario ya tiene un plan activo. Establezca 'reemplazar=true' para confirmar el reemplazo";
    public static final String RUTINA_CANNOT_BE_REPLACED = "No se puede asignar la rutina porque el usuario ya tiene una rutina activa. Establezca 'reemplazar=true' para confirmar el reemplazo";
    
    // ========== VALIDACIONES DE RANGOS (RN22) ==========
    
    public static final String PESO_OUT_OF_RANGE = "El peso debe estar entre %.1f y %.1f kg";
    public static final String ALTURA_OUT_OF_RANGE = "La altura debe estar entre %.1f y %.1f cm";
    public static final String CALORIAS_OUT_OF_RANGE = "Las calorías deben estar entre %d y %d";
    public static final String DURACION_PLAN_OUT_OF_RANGE = "La duración del plan debe estar entre %d y %d días";
    public static final String DURACION_RUTINA_OUT_OF_RANGE = "La duración de la rutina debe estar entre %d y %d semanas";
    
    // ========== VALIDACIONES DE ESTADO (RN21) ==========
    
    public static final String INVALID_STATE_TRANSITION = "No se puede cambiar de estado %s a %s";
    public static final String REGISTRO_REQUIRES_ACTIVE_PLAN = "Solo se pueden registrar comidas de un plan activo";
    public static final String REGISTRO_REQUIRES_ACTIVE_RUTINA = "Solo se pueden registrar ejercicios de una rutina activa";
    
    // ========== VALIDACIONES GENERALES ==========
    
    public static final String INVALID_PAGE_NUMBER = "El número de página debe ser mayor o igual a 0";
    public static final String INVALID_PAGE_SIZE = "El tamaño de página debe estar entre 1 y %d";
    public static final String INVALID_SORT_FIELD = "Campo de ordenamiento inválido: %s";
    public static final String INVALID_DATE_RANGE = "La fecha de inicio debe ser anterior a la fecha de fin";
    public static final String INVALID_ENUM_VALUE = "Valor inválido para %s: %s";
    
    // ========== OTROS ERRORES ==========
    
    public static final String INTERNAL_SERVER_ERROR = "Error interno del servidor";
    public static final String DATABASE_ERROR = "Error de base de datos";
    public static final String VALIDATION_ERROR = "Error de validación";
}
```

---

## ✅ VALIDACIONES

### ValidationConstants.java

```java
package com.nutritrack.nutritrackapi.constants;

/**
 * Constantes para validaciones de Bean Validation.
 */
public final class ValidationConstants {
    
    private ValidationConstants() {
        throw new UnsupportedOperationException("Esta es una clase de constantes");
    }
    
    // ========== MENSAJES DE VALIDACIÓN ==========
    
    // Email
    public static final String EMAIL_REQUIRED = "El email es obligatorio";
    public static final String EMAIL_INVALID = "Debe ser un email válido";
    
    // Contraseña
    public static final String PASSWORD_REQUIRED = "La contraseña es obligatoria";
    public static final String PASSWORD_MIN_SIZE = "La contraseña debe tener al menos 8 caracteres";
    public static final String PASSWORD_PATTERN = "La contraseña debe contener al menos una mayúscula, una minúscula y un número";
    
    // Nombre
    public static final String NOMBRE_REQUIRED = "El nombre es obligatorio";
    public static final String NOMBRE_SIZE = "El nombre debe tener entre 2 y 100 caracteres";
    
    // Apellido
    public static final String APELLIDO_REQUIRED = "El apellido es obligatorio";
    public static final String APELLIDO_SIZE = "El apellido debe tener entre 2 y 100 caracteres";
    
    // Peso
    public static final String PESO_REQUIRED = "El peso es obligatorio";
    public static final String PESO_MIN = "El peso debe ser mayor a 20 kg";
    public static final String PESO_MAX = "El peso debe ser menor a 600 kg";
    
    // Altura
    public static final String ALTURA_REQUIRED = "La altura es obligatoria";
    public static final String ALTURA_MIN = "La altura debe ser mayor a 50 cm";
    public static final String ALTURA_MAX = "La altura debe ser menor a 300 cm";
    
    // Calorías
    public static final String CALORIAS_MIN = "Las calorías deben ser al menos 500";
    public static final String CALORIAS_MAX = "Las calorías no pueden exceder 5000";
    
    // Macronutrientes
    public static final String PROTEINAS_MIN = "Las proteínas no pueden ser negativas";
    public static final String PROTEINAS_MAX = "Las proteínas no pueden exceder 100g";
    public static final String CARBOHIDRATOS_MIN = "Los carbohidratos no pueden ser negativos";
    public static final String CARBOHIDRATOS_MAX = "Los carbohidratos no pueden exceder 100g";
    public static final String GRASAS_MIN = "Las grasas no pueden ser negativas";
    public static final String GRASAS_MAX = "Las grasas no pueden exceder 100g";
    
    // Duración
    public static final String DURACION_MIN = "La duración debe ser al menos 1";
    public static final String DURACION_MAX_DIAS = "La duración no puede exceder 365 días";
    public static final String DURACION_MAX_SEMANAS = "La duración no puede exceder 52 semanas";
    
    // Series y repeticiones
    public static final String SERIES_MIN = "Debe haber al menos 1 serie";
    public static final String SERIES_MAX = "No puede exceder 20 series";
    public static final String REPETICIONES_MIN = "Debe haber al menos 1 repetición";
    public static final String REPETICIONES_MAX = "No puede exceder 100 repeticiones";
    
    // ========== EXPRESIONES REGULARES ==========
    
    /**
     * Regex para validar email.
     */
    public static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    
    /**
     * Regex para contraseña fuerte.
     * Mínimo 8 caracteres, al menos una mayúscula, una minúscula y un número.
     */
    public static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$";
    
    /**
     * Regex para validar solo letras y espacios.
     */
    public static final String LETTERS_ONLY_REGEX = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$";
}
```

---

## 🔄 CONVERTIDORES DE ENUMS

### EnumConverter.java

```java
package com.nutritrack.nutritrackapi.util;

import com.nutritrack.nutritrackapi.constants.ErrorMessages;
import com.nutritrack.nutritrackapi.exception.InvalidEnumException;

/**
 * Utilidad para convertir strings a enums de forma segura.
 */
public final class EnumConverter {
    
    private EnumConverter() {
        throw new UnsupportedOperationException("Esta es una clase de utilidad");
    }
    
    /**
     * Convierte un string a enum, lanzando excepción si es inválido.
     * @param enumClass Clase del enum
     * @param value Valor string
     * @return Valor del enum
     * @throws InvalidEnumException si el valor no es válido
     */
    public static <E extends Enum<E>> E fromString(Class<E> enumClass, String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new InvalidEnumException(
                String.format(ErrorMessages.INVALID_ENUM_VALUE, enumClass.getSimpleName(), "null")
            );
        }
        
        try {
            return Enum.valueOf(enumClass, value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidEnumException(
                String.format(ErrorMessages.INVALID_ENUM_VALUE, enumClass.getSimpleName(), value)
            );
        }
    }
    
    /**
     * Convierte un string a enum de forma segura, retornando null si es inválido.
     * @param enumClass Clase del enum
     * @param value Valor string
     * @return Valor del enum o null
     */
    public static <E extends Enum<E>> E fromStringOrNull(Class<E> enumClass, String value) {
        try {
            return fromString(enumClass, value);
        } catch (InvalidEnumException e) {
            return null;
        }
    }
}
```

### InvalidEnumException.java

```java
package com.nutritrack.nutritrackapi.exception;

/**
 * Excepción lanzada cuando se intenta convertir un valor inválido a enum.
 */
public class InvalidEnumException extends RuntimeException {
    
    public InvalidEnumException(String message) {
        super(message);
    }
}
```

---

**Documento completado:** 11 enums completos + constantes de aplicación + mensajes de error + validaciones + utilidades.
