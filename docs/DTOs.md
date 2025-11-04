# 📦 DTOs - GUÍA COMPLETA DE REQUEST/RESPONSE

> **Documento de Referencia para Reconstrucción**  
> Contiene todos los DTOs de entrada/salida con validaciones Bean Validation.  
> Compatible con Spring Boot 3.5.7 + Jakarta Validation

---

## 📋 ÍNDICE DE DTOs

### MÓDULO 1: Authentication & Profiles
1. [Auth DTOs](#auth-dtos)
2. [Perfil Usuario DTOs](#perfil-usuario-dtos)
3. [Perfil Salud DTOs](#perfil-salud-dtos)

### MÓDULO 2: Content Library
4. [Etiqueta DTOs](#etiqueta-dtos)
5. [Ingrediente DTOs](#ingrediente-dtos)
6. [Ejercicio DTOs](#ejercicio-dtos)
7. [Comida DTOs](#comida-dtos)

### MÓDULO 3: Plans & Routines
8. [Plan DTOs](#plan-dtos)
9. [Rutina DTOs](#rutina-dtos)

### MÓDULO 4: Assignments
10. [UsuarioPlan DTOs](#usuarioplan-dtos)
11. [UsuarioRutina DTOs](#usuariorutina-dtos)

### MÓDULO 5: Tracking
12. [RegistroComida DTOs](#registrocomida-dtos)
13. [RegistroEjercicio DTOs](#registroejercicio-dtos)

---

## 🔐 MÓDULO 1: AUTHENTICATION & PROFILES

### Auth DTOs

#### LoginRequest
```java
package com.nutritrack.nutritrackapi.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Debe ser un email válido")
    private String email;
    
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;
}
```

#### RegisterRequest
```java
package com.nutritrack.nutritrackapi.dto.auth;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Debe ser un email válido")
    private String email;
    
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, max = 100, message = "La contraseña debe tener entre 8 y 100 caracteres")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$",
        message = "La contraseña debe contener al menos una mayúscula, una minúscula y un número"
    )
    private String password;
    
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String nombre;
    
    @NotBlank(message = "El apellido es obligatorio")
    @Size(min = 2, max = 100, message = "El apellido debe tener entre 2 y 100 caracteres")
    private String apellido;
    
    @NotNull(message = "Las unidades de medida son obligatorias")
    private String unidadesMedida; // "KG" o "LBS"
}
```

#### JwtResponse
```java
package com.nutritrack.nutritrackapi.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String email;
    private String rol;
    private Long perfilId;
    private String nombreCompleto;
}
```

---

### Perfil Usuario DTOs

#### PerfilUsuarioDTO
```java
package com.nutritrack.nutritrackapi.dto.perfil;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PerfilUsuarioDTO {
    private Long id;
    private String nombre;
    private String apellido;
    private String nombreCompleto;
    private String email;
    private String unidadesMedida;
    private LocalDate fechaInicioApp;
    private PerfilSaludDTO perfilSalud;
    private UsuarioHistorialMedidaDTO ultimaMedida;
}
```

#### ActualizarPerfilRequest
```java
package com.nutritrack.nutritrackapi.dto.perfil;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActualizarPerfilRequest {
    
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100)
    private String nombre;
    
    @NotBlank(message = "El apellido es obligatorio")
    @Size(min = 2, max = 100)
    private String apellido;
    
    private String unidadesMedida; // "KG" o "LBS"
}
```

---

### Perfil Salud DTOs

#### PerfilSaludDTO
```java
package com.nutritrack.nutritrackapi.dto.perfil;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PerfilSaludDTO {
    private Long id;
    private String objetivoActual;
    private String nivelActividadActual;
    private LocalDate fechaActualizacion;
    private List<EtiquetaDTO> etiquetasSalud;
}
```

#### ActualizarPerfilSaludRequest
```java
package com.nutritrack.nutritrackapi.dto.perfil;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActualizarPerfilSaludRequest {
    
    @NotBlank(message = "El objetivo es obligatorio")
    private String objetivoActual; // PERDER_PESO, GANAR_MASA_MUSCULAR, etc.
    
    @NotBlank(message = "El nivel de actividad es obligatorio")
    private String nivelActividadActual; // BAJO, MODERADO, ALTO
    
    private List<Long> etiquetasIds; // IDs de etiquetas de salud (alergias, condiciones)
}
```

#### UsuarioHistorialMedidaDTO
```java
package com.nutritrack.nutritrackapi.dto.perfil;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioHistorialMedidaDTO {
    private Long id;
    private BigDecimal peso;
    private BigDecimal altura;
    private BigDecimal imc;
    private LocalDate fechaMedicion;
}
```

#### RegistrarMedidaRequest
```java
package com.nutritrack.nutritrackapi.dto.perfil;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrarMedidaRequest {
    
    @NotNull(message = "El peso es obligatorio")
    @DecimalMin(value = "20.0", message = "El peso debe ser mayor a 20 kg")
    @DecimalMax(value = "600.0", message = "El peso debe ser menor a 600 kg")
    private BigDecimal peso;
    
    @NotNull(message = "La altura es obligatoria")
    @DecimalMin(value = "50.0", message = "La altura debe ser mayor a 50 cm")
    @DecimalMax(value = "300.0", message = "La altura debe ser menor a 300 cm")
    private BigDecimal altura;
}
```

---

## 📚 MÓDULO 2: CONTENT LIBRARY

### Etiqueta DTOs

#### EtiquetaDTO
```java
package com.nutritrack.nutritrackapi.dto.etiqueta;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EtiquetaDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private String tipoEtiqueta;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

#### CrearEtiquetaRequest
```java
package com.nutritrack.nutritrackapi.dto.etiqueta;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrearEtiquetaRequest {
    
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 255, message = "El nombre debe tener entre 2 y 255 caracteres")
    private String nombre;
    
    @Size(max = 1000, message = "La descripción no puede exceder 1000 caracteres")
    private String descripcion;
    
    @NotBlank(message = "El tipo de etiqueta es obligatorio")
    private String tipoEtiqueta; // ALERGIA, CONDICION_MEDICA, OBJETIVO, etc.
}
```

---

### Ingrediente DTOs

#### IngredienteDTO
```java
package com.nutritrack.nutritrackapi.dto.ingrediente;

import com.nutritrack.nutritrackapi.dto.etiqueta.EtiquetaDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IngredienteDTO {
    private Long id;
    private String nombre;
    private BigDecimal proteinas;
    private BigDecimal carbohidratos;
    private BigDecimal grasas;
    private BigDecimal energia;
    private String grupoAlimenticio;
    private List<EtiquetaDTO> etiquetas;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

#### CrearIngredienteRequest
```java
package com.nutritrack.nutritrackapi.dto.ingrediente;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrearIngredienteRequest {
    
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 255)
    private String nombre;
    
    @DecimalMin(value = "0.0", message = "Las proteínas no pueden ser negativas")
    @DecimalMax(value = "100.0", message = "Las proteínas no pueden exceder 100g")
    private BigDecimal proteinas;
    
    @DecimalMin(value = "0.0", message = "Los carbohidratos no pueden ser negativos")
    @DecimalMax(value = "100.0", message = "Los carbohidratos no pueden exceder 100g")
    private BigDecimal carbohidratos;
    
    @DecimalMin(value = "0.0", message = "Las grasas no pueden ser negativas")
    @DecimalMax(value = "100.0", message = "Las grasas no pueden exceder 100g")
    private BigDecimal grasas;
    
    @DecimalMin(value = "0.0", message = "La energía no puede ser negativa")
    @DecimalMax(value = "900.0", message = "La energía no puede exceder 900 kcal")
    private BigDecimal energia;
    
    @NotBlank(message = "El grupo alimenticio es obligatorio")
    private String grupoAlimenticio;
    
    private List<Long> etiquetasIds;
}
```

---

### Ejercicio DTOs

#### EjercicioDTO
```java
package com.nutritrack.nutritrackapi.dto.ejercicio;

import com.nutritrack.nutritrackapi.dto.etiqueta.EtiquetaDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EjercicioDTO {
    private Long id;
    private String nombre;
    private String dificultad;
    private String tipoEjercicio;
    private String musculoPrincipal;
    private BigDecimal caloriasEstimadas;
    private Integer duracion;
    private List<EtiquetaDTO> etiquetas;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

#### CrearEjercicioRequest
```java
package com.nutritrack.nutritrackapi.dto.ejercicio;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrearEjercicioRequest {
    
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 150)
    private String nombre;
    
    @NotBlank(message = "La dificultad es obligatoria")
    private String dificultad; // PRINCIPIANTE, INTERMEDIO, AVANZADO, EXPERTO
    
    @NotBlank(message = "El tipo de ejercicio es obligatorio")
    private String tipoEjercicio; // CARDIO, FUERZA, FLEXIBILIDAD, etc.
    
    private String musculoPrincipal; // PECHO, ESPALDA, PIERNAS, etc.
    
    @DecimalMin(value = "0.0", message = "Las calorías no pueden ser negativas")
    private BigDecimal caloriasEstimadas;
    
    @Min(value = 1, message = "La duración debe ser al menos 1 minuto")
    @Max(value = 300, message = "La duración no puede exceder 300 minutos")
    private Integer duracion;
    
    private List<Long> etiquetasIds;
}
```

---

### Comida DTOs

#### ComidaDTO
```java
package com.nutritrack.nutritrackapi.dto.comida;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComidaDTO {
    private Long id;
    private String nombre;
    private String tipoComida;
    private Integer tiempoElaboracion;
    private List<RecetaDTO> recetas;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

#### RecetaDTO
```java
package com.nutritrack.nutritrackapi.dto.comida;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecetaDTO {
    private Long ingredienteId;
    private String ingredienteNombre;
    private BigDecimal cantidad;
}
```

#### CrearComidaRequest
```java
package com.nutritrack.nutritrackapi.dto.comida;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrearComidaRequest {
    
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 255)
    private String nombre;
    
    @NotBlank(message = "El tipo de comida es obligatorio")
    private String tipoComida; // DESAYUNO, ALMUERZO, CENA, etc.
    
    @Min(value = 1, message = "El tiempo de elaboración debe ser al menos 1 minuto")
    @Max(value = 480, message = "El tiempo de elaboración no puede exceder 480 minutos")
    private Integer tiempoElaboracion;
    
    @NotEmpty(message = "Debe incluir al menos un ingrediente")
    @Valid
    private List<AgregarIngredienteRequest> ingredientes;
}
```

#### AgregarIngredienteRequest
```java
package com.nutritrack.nutritrackapi.dto.comida;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgregarIngredienteRequest {
    
    @NotNull(message = "El ID del ingrediente es obligatorio")
    private Long ingredienteId;
    
    @NotNull(message = "La cantidad es obligatoria")
    @DecimalMin(value = "0.1", message = "La cantidad debe ser mayor a 0")
    @DecimalMax(value = "10000.0", message = "La cantidad no puede exceder 10000")
    private BigDecimal cantidad;
}
```

---

## 📅 MÓDULO 3: PLANS & ROUTINES

### Plan DTOs

#### PlanDTO
```java
package com.nutritrack.nutritrackapi.dto.plan;

import com.nutritrack.nutritrackapi.dto.etiqueta.EtiquetaDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private Integer duracionDias;
    private Boolean activo;
    private PlanObjetivoDTO objetivos;
    private List<EtiquetaDTO> etiquetas;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

#### PlanDetalleDTO
```java
package com.nutritrack.nutritrackapi.dto.plan;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanDetalleDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private Integer duracionDias;
    private Boolean activo;
    private PlanObjetivoDTO objetivos;
    private List<PlanDiaDTO> dias;
    private List<String> etiquetas;
}
```

#### PlanObjetivoDTO
```java
package com.nutritrack.nutritrackapi.dto.plan;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanObjetivoDTO {
    private Long id;
    private BigDecimal caloriasObjetivo;
    private BigDecimal proteinasObjetivo;
    private BigDecimal carbohidratosObjetivo;
    private BigDecimal grasasObjetivo;
    private String descripcion;
}
```

#### PlanDiaDTO
```java
package com.nutritrack.nutritrackapi.dto.plan;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanDiaDTO {
    private Long id;
    private Integer numeroDia;
    private String tipoComida;
    private Long comidaId;
    private String comidaNombre;
    private String notas;
}
```

#### CrearPlanRequest
```java
package com.nutritrack.nutritrackapi.dto.plan;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrearPlanRequest {
    
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 3, max = 100)
    private String nombre;
    
    @Size(max = 2000, message = "La descripción no puede exceder 2000 caracteres")
    private String descripcion;
    
    @NotNull(message = "La duración es obligatoria")
    @Min(value = 1, message = "La duración debe ser al menos 1 día")
    @Max(value = 365, message = "La duración no puede exceder 365 días")
    private Integer duracionDias;
    
    @Valid
    private CrearPlanObjetivoRequest objetivos;
    
    private List<Long> etiquetasIds;
}
```

#### CrearPlanObjetivoRequest
```java
package com.nutritrack.nutritrackapi.dto.plan;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrearPlanObjetivoRequest {
    
    @DecimalMin(value = "500.0", message = "Las calorías deben ser al menos 500")
    private BigDecimal caloriasObjetivo;
    
    @DecimalMin(value = "0.0", message = "Las proteínas no pueden ser negativas")
    private BigDecimal proteinasObjetivo;
    
    @DecimalMin(value = "0.0", message = "Los carbohidratos no pueden ser negativos")
    private BigDecimal carbohidratosObjetivo;
    
    @DecimalMin(value = "0.0", message = "Las grasas no pueden ser negativas")
    private BigDecimal grasasObjetivo;
    
    @Size(max = 500)
    private String descripcion;
}
```

#### AgregarComidaPlanRequest
```java
package com.nutritrack.nutritrackapi.dto.plan;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgregarComidaPlanRequest {
    
    @NotNull(message = "El número de día es obligatorio")
    @Min(value = 1, message = "El día debe ser al menos 1")
    private Integer numeroDia;
    
    @NotBlank(message = "El tipo de comida es obligatorio")
    private String tipoComida;
    
    @NotNull(message = "El ID de la comida es obligatorio")
    private Long comidaId;
    
    @Size(max = 500)
    private String notas;
}
```

---

### Rutina DTOs

#### RutinaDTO
```java
package com.nutritrack.nutritrackapi.dto.rutina;

import com.nutritrack.nutritrackapi.dto.etiqueta.EtiquetaDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RutinaDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private Integer duracionSemanas;
    private Boolean activo;
    private List<EtiquetaDTO> etiquetas;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

#### RutinaDetalleDTO
```java
package com.nutritrack.nutritrackapi.dto.rutina;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RutinaDetalleDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private Integer duracionSemanas;
    private Boolean activo;
    private List<RutinaEjercicioDTO> ejercicios;
    private List<String> etiquetas;
}
```

#### RutinaEjercicioDTO
```java
package com.nutritrack.nutritrackapi.dto.rutina;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RutinaEjercicioDTO {
    private Long id;
    private Long ejercicioId;
    private String ejercicioNombre;
    private Integer orden;
    private Integer series;
    private Integer repeticiones;
    private BigDecimal peso;
    private Integer duracionMinutos;
    private String notas;
}
```

#### CrearRutinaRequest
```java
package com.nutritrack.nutritrackapi.dto.rutina;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrearRutinaRequest {
    
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 3, max = 100)
    private String nombre;
    
    @Size(max = 2000, message = "La descripción no puede exceder 2000 caracteres")
    private String descripcion;
    
    @NotNull(message = "La duración es obligatoria")
    @Min(value = 1, message = "La duración debe ser al menos 1 semana")
    @Max(value = 52, message = "La duración no puede exceder 52 semanas")
    private Integer duracionSemanas;
    
    private List<Long> etiquetasIds;
}
```

#### AgregarEjercicioRutinaRequest
```java
package com.nutritrack.nutritrackapi.dto.rutina;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgregarEjercicioRutinaRequest {
    
    @NotNull(message = "El ID del ejercicio es obligatorio")
    private Long ejercicioId;
    
    @NotNull(message = "El orden es obligatorio")
    @Min(value = 1)
    private Integer orden;
    
    @NotNull(message = "Las series son obligatorias")
    @Min(value = 1, message = "Debe haber al menos 1 serie")
    @Max(value = 20, message = "No puede exceder 20 series")
    private Integer series;
    
    @NotNull(message = "Las repeticiones son obligatorias")
    @Min(value = 1, message = "Debe haber al menos 1 repetición")
    @Max(value = 100, message = "No puede exceder 100 repeticiones")
    private Integer repeticiones;
    
    @DecimalMin(value = "0.0", message = "El peso no puede ser negativo")
    @DecimalMax(value = "500.0", message = "El peso no puede exceder 500 kg")
    private BigDecimal peso;
    
    @NotNull(message = "La duración es obligatoria")
    @Min(value = 1, message = "La duración debe ser al menos 1 minuto")
    @Max(value = 120, message = "La duración no puede exceder 120 minutos")
    private Integer duracionMinutos;
    
    @Size(max = 500)
    private String notas;
}
```

---

## 🎯 MÓDULO 4: ASSIGNMENTS

### UsuarioPlan DTOs

#### UsuarioPlanDTO
```java
package com.nutritrack.nutritrackapi.dto.asignacion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioPlanDTO {
    private Long id;
    private Long perfilUsuarioId;
    private Long planId;
    private String planNombre;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Integer diaActual;
    private String estado;
    private String notas;
    private Double porcentajeCompletado;
}
```

#### AsignarPlanRequest
```java
package com.nutritrack.nutritrackapi.dto.asignacion;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AsignarPlanRequest {
    
    @NotNull(message = "El ID del perfil es obligatorio")
    private Long perfilUsuarioId;
    
    @NotNull(message = "El ID del plan es obligatorio")
    private Long planId;
    
    private Boolean reemplazar; // true para confirmar reemplazo de plan activo
    
    @Size(max = 2000)
    private String notas;
}
```

---

### UsuarioRutina DTOs

#### UsuarioRutinaDTO
```java
package com.nutritrack.nutritrackapi.dto.asignacion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioRutinaDTO {
    private Long id;
    private Long perfilUsuarioId;
    private Long rutinaId;
    private String rutinaNombre;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Integer semanaActual;
    private String estado;
    private String notas;
    private Double porcentajeCompletado;
}
```

#### AsignarRutinaRequest
```java
package com.nutritrack.nutritrackapi.dto.asignacion;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AsignarRutinaRequest {
    
    @NotNull(message = "El ID del perfil es obligatorio")
    private Long perfilUsuarioId;
    
    @NotNull(message = "El ID de la rutina es obligatorio")
    private Long rutinaId;
    
    private Boolean reemplazar;
    
    @Size(max = 2000)
    private String notas;
}
```

---

## 📊 MÓDULO 5: TRACKING

### RegistroComida DTOs

#### RegistroComidaDTO
```java
package com.nutritrack.nutritrackapi.dto.tracking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistroComidaDTO {
    private Long id;
    private Long perfilUsuarioId;
    private Long comidaId;
    private String comidaNombre;
    private Long usuarioPlanId;
    private LocalDate fecha;
    private LocalTime hora;
    private String tipoComida;
    private BigDecimal porciones;
    private BigDecimal caloriasConsumidas;
    private String notas;
}
```

#### RegistroComidaRequest
```java
package com.nutritrack.nutritrackapi.dto.tracking;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistroComidaRequest {
    
    @NotNull(message = "El ID del perfil es obligatorio")
    private Long perfilUsuarioId;
    
    @NotNull(message = "El ID de la comida es obligatorio")
    private Long comidaId;
    
    private Long usuarioPlanId; // Opcional
    
    @NotNull(message = "La fecha es obligatoria")
    private LocalDate fecha;
    
    @NotNull(message = "La hora es obligatoria")
    private LocalTime hora;
    
    @NotBlank(message = "El tipo de comida es obligatorio")
    private String tipoComida;
    
    @DecimalMin(value = "0.1", message = "Las porciones deben ser al menos 0.1")
    @DecimalMax(value = "20.0", message = "Las porciones no pueden exceder 20")
    private BigDecimal porciones;
    
    @DecimalMin(value = "0.0", message = "Las calorías no pueden ser negativas")
    private BigDecimal caloriasConsumidas;
    
    @Size(max = 2000)
    private String notas;
}
```

---

### RegistroEjercicio DTOs

#### RegistroEjercicioDTO
```java
package com.nutritrack.nutritrackapi.dto.tracking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistroEjercicioDTO {
    private Long id;
    private Long perfilUsuarioId;
    private Long ejercicioId;
    private String ejercicioNombre;
    private Long usuarioRutinaId;
    private LocalDate fecha;
    private LocalTime hora;
    private Integer duracionMinutos;
    private Integer seriesRealizadas;
    private Integer repeticionesRealizadas;
    private BigDecimal pesoUtilizado;
    private BigDecimal caloriasQuemadas;
    private String notas;
}
```

#### RegistroEjercicioRequest
```java
package com.nutritrack.nutritrackapi.dto.tracking;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistroEjercicioRequest {
    
    @NotNull(message = "El ID del perfil es obligatorio")
    private Long perfilUsuarioId;
    
    @NotNull(message = "El ID del ejercicio es obligatorio")
    private Long ejercicioId;
    
    private Long usuarioRutinaId; // Opcional
    
    @NotNull(message = "La fecha es obligatoria")
    private LocalDate fecha;
    
    @NotNull(message = "La hora es obligatoria")
    private LocalTime hora;
    
    @NotNull(message = "La duración es obligatoria")
    @Min(value = 1, message = "La duración debe ser al menos 1 minuto")
    @Max(value = 300, message = "La duración no puede exceder 300 minutos")
    private Integer duracionMinutos;
    
    @Min(value = 0, message = "Las series no pueden ser negativas")
    @Max(value = 50, message = "Las series no pueden exceder 50")
    private Integer seriesRealizadas;
    
    @Min(value = 0, message = "Las repeticiones no pueden ser negativas")
    @Max(value = 200, message = "Las repeticiones no pueden exceder 200")
    private Integer repeticionesRealizadas;
    
    @DecimalMin(value = "0.0", message = "El peso no puede ser negativo")
    @DecimalMax(value = "500.0", message = "El peso no puede exceder 500 kg")
    private BigDecimal pesoUtilizado;
    
    @DecimalMin(value = "0.0", message = "Las calorías no pueden ser negativas")
    private BigDecimal caloriasQuemadas;
    
    @Size(max = 2000)
    private String notas;
}
```

---

## 🛠️ MAPPERS (MapStruct)

### Configuración de MapStruct

```xml
<!-- pom.xml -->
<dependency>
    <groupId>org.mapstruct</groupId>
    <artifactId>mapstruct</artifactId>
    <version>1.5.5.Final</version>
</dependency>
```

### Ejemplo de Mapper

```java
package com.nutritrack.nutritrackapi.mapper;

import com.nutritrack.nutritrackapi.dto.ingrediente.IngredienteDTO;
import com.nutritrack.nutritrackapi.dto.ingrediente.CrearIngredienteRequest;
import com.nutritrack.nutritrackapi.model.Ingrediente;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface IngredienteMapper {
    
    IngredienteDTO toDTO(Ingrediente entity);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "etiquetas", ignore = true)
    @Mapping(target = "recetas", ignore = true)
    Ingrediente toEntity(CrearIngredienteRequest request);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "etiquetas", ignore = true)
    @Mapping(target = "recetas", ignore = true)
    void updateEntity(CrearIngredienteRequest request, @MappingTarget Ingrediente entity);
}
```

---

## ✅ VALIDACIÓN GLOBAL

### Exception Handler

```java
package com.nutritrack.nutritrackapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(
        MethodArgumentNotValidException ex
    ) {
        Map<String, String> errors = new HashMap<>();
        
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
}
```

---

**Documento completado:** 40+ DTOs con validaciones Bean Validation listas para implementación.
