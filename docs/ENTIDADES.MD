# 🗂️ ENTIDADES JPA - GUÍA COMPLETA DE IMPLEMENTACIÓN

> **Documento de Referencia para Reconstrucción**  
> Contiene todas las entidades JPA con anotaciones, relaciones y auditoría.  
> Compatible con Spring Boot 3.5.7 + Hibernate 6.6.33 + PostgreSQL 16.10

---

## 📋 ÍNDICE DE ENTIDADES

### MÓDULO 1: Authentication & Profiles
1. [Rol](#1-rol)
2. [CuentaAuth](#2-cuentaauth)
3. [PerfilUsuario](#3-perfilusuario)
4. [UsuarioPerfilSalud](#4-usuarioperfilsalud)
5. [UsuarioHistorialMedida](#5-usuariohistorialmedida)
6. [UsuarioEtiquetaSalud](#6-usuarioetiquetasalud)

### MÓDULO 2: Content Library
7. [Etiqueta](#7-etiqueta)
8. [Ingrediente](#8-ingrediente)
9. [Ejercicio](#9-ejercicio)
10. [Comida](#10-comida)
11. [Receta](#11-receta)

### MÓDULO 3: Plans & Routines
12. [Plan](#12-plan)
13. [PlanObjetivo](#13-planobjetivo)
14. [PlanDia](#14-plandia)
15. [Rutina](#15-rutina)
16. [RutinaEjercicio](#16-rutinaejercicio)

### MÓDULO 4: Assignments
17. [UsuarioPlan](#17-usuarioplan)
18. [UsuarioRutina](#18-usuariorutina)

### MÓDULO 5: Tracking
19. [RegistroComida](#19-registrocomida)
20. [RegistroEjercicio](#20-registroejercicio)

---

## 📦 MÓDULO 1: AUTHENTICATION & PROFILES

### 1. Rol

```java
package com.nutritrack.nutritrackapi.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entidad que representa los roles del sistema.
 * Solo existen 2 roles: ROLE_USER y ROLE_ADMIN
 */
@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tipo_rol", nullable = false, unique = true)
    @Enumerated(EnumType.STRING)
    private TipoRol tipoRol;

    /**
     * Enum para los tipos de roles
     */
    public enum TipoRol {
        ROLE_USER,
        ROLE_ADMIN
    }

    @Override
    public String toString() {
        return "Rol{id=" + id + ", tipoRol=" + tipoRol + "}";
    }
}
```

---

### 2. CuentaAuth

```java
package com.nutritrack.nutritrackapi.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;

/**
 * Entidad que representa las cuentas de autenticación.
 * Contiene email único, password hasheado, y estado activo/inactivo.
 */
@Entity
@Table(name = "cuentas_auth")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CuentaAuth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false)
    private Boolean active;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_rol")
    private Rol rol;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDate createdAt;

    /**
     * Relación 1-to-1 con PerfilUsuario (lado no propietario)
     */
    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private PerfilUsuario perfilUsuario;

    @Override
    public String toString() {
        return "CuentaAuth{id=" + id + ", email='" + email + "', active=" + active + "}";
    }
}
```

---

### 3. PerfilUsuario

```java
package com.nutritrack.nutritrackapi.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Entidad que representa el perfil de usuario.
 * Contiene información personal y preferencias de unidades de medida.
 */
@Entity
@Table(name = "perfiles_usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PerfilUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 100)
    private String apellido;

    @Enumerated(EnumType.STRING)
    @Column(name = "unidades_medida", length = 10)
    private UnidadesMedida unidadesMedida;

    @Column(name = "fecha_inicio_app", nullable = false)
    private LocalDate fechaInicioApp;

    /**
     * Relación 1-to-1 con CuentaAuth (lado propietario)
     */
    @OneToOne
    @JoinColumn(name = "id_usuario", nullable = false, unique = true)
    private CuentaAuth usuario;

    /**
     * Relación 1-to-1 con UsuarioPerfilSalud
     */
    @OneToOne(mappedBy = "perfil", cascade = CascadeType.ALL, orphanRemoval = true)
    private UsuarioPerfilSalud perfilSalud;

    /**
     * Relación 1-to-many con UsuarioHistorialMedida
     */
    @OneToMany(mappedBy = "perfilUsuario", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<UsuarioHistorialMedida> historialMedidas = new HashSet<>();

    /**
     * Relación many-to-many con Etiqueta (etiquetas de salud)
     */
    @OneToMany(mappedBy = "perfilUsuario", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<UsuarioEtiquetaSalud> etiquetasSalud = new HashSet<>();

    /**
     * Relación 1-to-many con UsuarioPlan
     */
    @OneToMany(mappedBy = "perfilUsuario")
    @Builder.Default
    private Set<UsuarioPlan> planesAsignados = new HashSet<>();

    /**
     * Relación 1-to-many con UsuarioRutina
     */
    @OneToMany(mappedBy = "perfilUsuario")
    @Builder.Default
    private Set<UsuarioRutina> rutinasAsignadas = new HashSet<>();

    /**
     * Relación 1-to-many con RegistroComida
     */
    @OneToMany(mappedBy = "perfilUsuario")
    @Builder.Default
    private Set<RegistroComida> registrosComidas = new HashSet<>();

    /**
     * Relación 1-to-many con RegistroEjercicio
     */
    @OneToMany(mappedBy = "perfilUsuario")
    @Builder.Default
    private Set<RegistroEjercicio> registrosEjercicios = new HashSet<>();

    /**
     * Enum para unidades de medida
     */
    public enum UnidadesMedida {
        KG,
        LBS
    }

    @Override
    public String toString() {
        return "PerfilUsuario{id=" + id + ", nombre='" + nombre + " " + apellido + "'}";
    }
}
```

---

### 4. UsuarioPerfilSalud

```java
package com.nutritrack.nutritrackapi.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * Entidad que representa el perfil de salud del usuario.
 * Contiene objetivos y nivel de actividad.
 * Relación 1-to-1 con PerfilUsuario.
 */
@Entity
@Table(name = "usuario_perfil_salud")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioPerfilSalud {

    @Id
    @Column(name = "id_perfil")
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id_perfil")
    private PerfilUsuario perfil;

    @Enumerated(EnumType.STRING)
    @Column(name = "objetivo_actual", nullable = false)
    private ObjetivoSalud objetivoActual;

    @Enumerated(EnumType.STRING)
    @Column(name = "nivel_actividad_actual", nullable = false)
    private NivelActividad nivelActividadActual;

    @Column(name = "fecha_actualizacion", nullable = false)
    private LocalDate fechaActualizacion;

    /**
     * Enum para objetivos de salud
     */
    public enum ObjetivoSalud {
        PERDER_PESO,
        GANAR_MASA_MUSCULAR,
        MANTENER_FORMA,
        REHABILITACION,
        CONTROLAR_ESTRES
    }

    /**
     * Enum para nivel de actividad
     */
    public enum NivelActividad {
        BAJO,
        MODERADO,
        ALTO
    }

    @Override
    public String toString() {
        return "UsuarioPerfilSalud{objetivo=" + objetivoActual + ", nivelActividad=" + nivelActividadActual + "}";
    }
}
```

---

### 5. UsuarioHistorialMedida

```java
package com.nutritrack.nutritrackapi.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entidad que representa el historial de medidas corporales del usuario.
 * Almacena peso, altura e IMC en diferentes fechas.
 */
@Entity
@Table(name = "usuario_historial_medidas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioHistorialMedida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal peso;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal altura;

    @Column(precision = 5, scale = 2)
    private BigDecimal imc;

    @Column(name = "fecha_medicion", nullable = false)
    private LocalDate fechaMedicion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente", nullable = false)
    private PerfilUsuario perfilUsuario;

    @Override
    public String toString() {
        return "UsuarioHistorialMedida{id=" + id + ", peso=" + peso + ", altura=" + altura + ", imc=" + imc + "}";
    }
}
```

---

### 6. UsuarioEtiquetaSalud

```java
package com.nutritrack.nutritrackapi.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

/**
 * Entidad intermedia para la relación many-to-many entre PerfilUsuario y Etiqueta.
 * Representa las etiquetas de salud (alergias, condiciones médicas) del usuario.
 */
@Entity
@Table(name = "usuario_etiquetas_salud")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioEtiquetaSalud {

    @EmbeddedId
    private UsuarioEtiquetaSaludId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("perfilId")
    @JoinColumn(name = "id_perfil")
    private PerfilUsuario perfilUsuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("etiquetaId")
    @JoinColumn(name = "id_etiqueta")
    private Etiqueta etiqueta;

    /**
     * Clase de clave compuesta
     */
    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class UsuarioEtiquetaSaludId implements Serializable {
        @Column(name = "id_perfil")
        private Long perfilId;

        @Column(name = "id_etiqueta")
        private Long etiquetaId;
    }
}
```

---

## 📚 MÓDULO 2: CONTENT LIBRARY

### 7. Etiqueta

```java
package com.nutritrack.nutritrackapi.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entidad que representa las etiquetas del sistema.
 * Usadas para categorizar ingredientes, ejercicios, planes, rutinas y perfiles de usuario.
 */
@Entity
@Table(name = "etiquetas")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Etiqueta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 255)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_etiqueta", nullable = false, length = 50)
    private TipoEtiqueta tipoEtiqueta;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Relaciones many-to-many con otras entidades
     */
    @ManyToMany(mappedBy = "etiquetas")
    @Builder.Default
    private Set<Ingrediente> ingredientes = new HashSet<>();

    @ManyToMany(mappedBy = "etiquetas")
    @Builder.Default
    private Set<Ejercicio> ejercicios = new HashSet<>();

    @ManyToMany(mappedBy = "etiquetas")
    @Builder.Default
    private Set<Plan> planes = new HashSet<>();

    @ManyToMany(mappedBy = "etiquetas")
    @Builder.Default
    private Set<Rutina> rutinas = new HashSet<>();

    /**
     * Enum para tipos de etiqueta
     */
    public enum TipoEtiqueta {
        ALERGIA,
        CONDICION_MEDICA,
        OBJETIVO,
        DIETA,
        DIFICULTAD,
        GRUPO_MUSCULAR,
        TIPO_EJERCICIO
    }

    @Override
    public String toString() {
        return "Etiqueta{id=" + id + ", nombre='" + nombre + "', tipo=" + tipoEtiqueta + "}";
    }
}
```

---

### 8. Ingrediente

```java
package com.nutritrack.nutritrackapi.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entidad que representa los ingredientes del catálogo.
 * Contiene información nutricional y etiquetas asociadas.
 */
@Entity
@Table(name = "ingredientes")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ingrediente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 255)
    private String nombre;

    @Column(precision = 5, scale = 2)
    private BigDecimal proteinas;

    @Column(precision = 5, scale = 2)
    private BigDecimal carbohidratos;

    @Column(precision = 5, scale = 2)
    private BigDecimal grasas;

    @Column(precision = 5, scale = 2)
    private BigDecimal energia;

    @Enumerated(EnumType.STRING)
    @Column(name = "grupo_alimenticio", length = 50)
    private GrupoAlimenticio grupoAlimenticio;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Relación many-to-many con Etiqueta
     */
    @ManyToMany
    @JoinTable(
        name = "etiquetas_ingredientes",
        joinColumns = @JoinColumn(name = "id_ingrediente"),
        inverseJoinColumns = @JoinColumn(name = "id_etiqueta")
    )
    @Builder.Default
    private Set<Etiqueta> etiquetas = new HashSet<>();

    /**
     * Relación 1-to-many con Receta
     */
    @OneToMany(mappedBy = "ingrediente", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<Receta> recetas = new HashSet<>();

    /**
     * Enum para grupos alimenticios
     */
    public enum GrupoAlimenticio {
        FRUTAS,
        VERDURAS,
        CEREALES,
        LEGUMBRES,
        PROTEINAS_ANIMALES,
        PROTEINAS_VEGETALES,
        LACTEOS,
        GRASAS_SALUDABLES,
        AZUCARES,
        BEBIDAS,
        CONDIMENTOS,
        FRUTOS_SECOS,
        OTRO
    }

    @Override
    public String toString() {
        return "Ingrediente{id=" + id + ", nombre='" + nombre + "', grupo=" + grupoAlimenticio + "}";
    }
}
```

---

### 9. Ejercicio

```java
package com.nutritrack.nutritrackapi.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entidad que representa los ejercicios del catálogo.
 * Contiene información sobre tipo, dificultad, músculo principal y calorías estimadas.
 */
@Entity
@Table(name = "ejercicios")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ejercicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 150)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private Dificultad dificultad;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_ejercicio", length = 50)
    private TipoEjercicio tipoEjercicio;

    @Enumerated(EnumType.STRING)
    @Column(name = "musculo_principal", length = 100)
    private MusculoPrincipal musculoPrincipal;

    @Column(name = "calorias_estimadas", precision = 6, scale = 2)
    private BigDecimal caloriasEstimadas;

    @Column
    private Integer duracion;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Relación many-to-many con Etiqueta
     */
    @ManyToMany
    @JoinTable(
        name = "etiquetas_ejercicios",
        joinColumns = @JoinColumn(name = "id_ejercicio"),
        inverseJoinColumns = @JoinColumn(name = "id_etiqueta")
    )
    @Builder.Default
    private Set<Etiqueta> etiquetas = new HashSet<>();

    /**
     * Relación 1-to-many con RutinaEjercicio
     */
    @OneToMany(mappedBy = "ejercicio")
    @Builder.Default
    private Set<RutinaEjercicio> rutinasEjercicios = new HashSet<>();

    /**
     * Enum para dificultad
     */
    public enum Dificultad {
        PRINCIPIANTE,
        INTERMEDIO,
        AVANZADO,
        EXPERTO
    }

    /**
     * Enum para tipo de ejercicio
     */
    public enum TipoEjercicio {
        CARDIO,
        FUERZA,
        FLEXIBILIDAD,
        EQUILIBRIO,
        HIIT,
        YOGA,
        PILATES,
        FUNCIONAL,
        DEPORTIVO,
        OTRO
    }

    /**
     * Enum para músculo principal
     */
    public enum MusculoPrincipal {
        PECHO,
        ESPALDA,
        HOMBROS,
        BRAZOS,
        BICEPS,
        TRICEPS,
        ABDOMINALES,
        PIERNAS,
        CUADRICEPS,
        GLUTEOS,
        GEMELOS,
        CARDIO,
        CUERPO_COMPLETO,
        CORE,
        OTRO
    }

    @Override
    public String toString() {
        return "Ejercicio{id=" + id + ", nombre='" + nombre + "', tipo=" + tipoEjercicio + "}";
    }
}
```

---

### 10. Comida

```java
package com.nutritrack.nutritrackapi.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entidad que representa las comidas del catálogo.
 * Una comida está compuesta por varios ingredientes (relación con Receta).
 */
@Entity
@Table(name = "comidas")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 255)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_comida", length = 50)
    private TipoComida tipoComida;

    @Column(name = "tiempo_elaboracion")
    private Integer tiempoElaboracion;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Relación 1-to-many con Receta
     */
    @OneToMany(mappedBy = "comida", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<Receta> recetas = new HashSet<>();

    /**
     * Relación 1-to-many con PlanDia
     */
    @OneToMany(mappedBy = "comida")
    @Builder.Default
    private Set<PlanDia> planDias = new HashSet<>();

    /**
     * Enum para tipo de comida
     */
    public enum TipoComida {
        DESAYUNO,
        ALMUERZO,
        CENA,
        SNACK,
        PRE_ENTRENAMIENTO,
        POST_ENTRENAMIENTO,
        COLACION
    }

    @Override
    public String toString() {
        return "Comida{id=" + id + ", nombre='" + nombre + "', tipo=" + tipoComida + "}";
    }
}
```

---

### 11. Receta

```java
package com.nutritrack.nutritrackapi.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Entidad intermedia para la relación many-to-many entre Comida e Ingrediente.
 * Representa los ingredientes de una comida con sus cantidades.
 */
@Entity
@Table(name = "recetas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Receta {

    @EmbeddedId
    private RecetaId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("comidaId")
    @JoinColumn(name = "id_comida")
    private Comida comida;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("ingredienteId")
    @JoinColumn(name = "id_ingrediente")
    private Ingrediente ingrediente;

    @Column(name = "cantidad_ingrediente", nullable = false, precision = 5, scale = 2)
    private BigDecimal cantidadIngrediente;

    /**
     * Clase de clave compuesta
     */
    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class RecetaId implements Serializable {
        @Column(name = "id_comida")
        private Long comidaId;

        @Column(name = "id_ingrediente")
        private Long ingredienteId;
    }
}
```

---

## 📅 MÓDULO 3: PLANS & ROUTINES

### 12. Plan

```java
package com.nutritrack.nutritrackapi.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entidad que representa los planes de alimentación del catálogo.
 * Un plan tiene una duración fija en días y contiene comidas por día.
 */
@Entity
@Table(name = "planes")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Plan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "duracion_dias", nullable = false)
    private Integer duracionDias;

    @Column(nullable = false)
    private Boolean activo;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Relación 1-to-1 con PlanObjetivo
     */
    @OneToOne(mappedBy = "plan", cascade = CascadeType.ALL, orphanRemoval = true)
    private PlanObjetivo objetivos;

    /**
     * Relación 1-to-many con PlanDia
     */
    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<PlanDia> dias = new HashSet<>();

    /**
     * Relación many-to-many con Etiqueta
     */
    @ManyToMany
    @JoinTable(
        name = "plan_etiquetas",
        joinColumns = @JoinColumn(name = "id_plan"),
        inverseJoinColumns = @JoinColumn(name = "id_etiqueta")
    )
    @Builder.Default
    private Set<Etiqueta> etiquetas = new HashSet<>();

    /**
     * Relación 1-to-many con UsuarioPlan
     */
    @OneToMany(mappedBy = "plan")
    @Builder.Default
    private Set<UsuarioPlan> usuariosPlanes = new HashSet<>();

    @Override
    public String toString() {
        return "Plan{id=" + id + ", nombre='" + nombre + "', duracion=" + duracionDias + " días}";
    }
}
```

---

### 13. PlanObjetivo

```java
package com.nutritrack.nutritrackapi.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad que representa los objetivos nutricionales de un plan.
 * Relación 1-to-1 con Plan.
 */
@Entity
@Table(name = "plan_objetivos")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanObjetivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "id_plan", nullable = false, unique = true)
    private Plan plan;

    @Column(name = "calorias_objetivo", precision = 10, scale = 2)
    private BigDecimal caloriasObjetivo;

    @Column(name = "proteinas_objetivo", precision = 10, scale = 2)
    private BigDecimal proteinasObjetivo;

    @Column(name = "carbohidratos_objetivo", precision = 10, scale = 2)
    private BigDecimal carbohidratosObjetivo;

    @Column(name = "grasas_objetivo", precision = 10, scale = 2)
    private BigDecimal grasasObjetivo;

    @Column(length = 500)
    private String descripcion;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Override
    public String toString() {
        return "PlanObjetivo{id=" + id + ", calorias=" + caloriasObjetivo + "}";
    }
}
```

---

### 14. PlanDia

```java
package com.nutritrack.nutritrackapi.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Entidad que representa las comidas asignadas a cada día del plan.
 * Define qué comida se debe consumir en qué día y en qué momento.
 */
@Entity
@Table(name = "plan_dias")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanDia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_plan", nullable = false)
    private Plan plan;

    @Column(name = "numero_dia", nullable = false)
    private Integer numeroDia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_comida", nullable = false)
    private Comida comida;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_comida", nullable = false, length = 20)
    private Comida.TipoComida tipoComida;

    @Column(length = 500)
    private String notas;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Override
    public String toString() {
        return "PlanDia{id=" + id + ", dia=" + numeroDia + ", tipo=" + tipoComida + "}";
    }
}
```

---

### 15. Rutina

```java
package com.nutritrack.nutritrackapi.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entidad que representa las rutinas de ejercicio del catálogo.
 * Una rutina tiene una duración fija en semanas y contiene ejercicios.
 */
@Entity
@Table(name = "rutinas")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rutina {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "duracion_semanas", nullable = false)
    private Integer duracionSemanas;

    @Column(nullable = false)
    private Boolean activo;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Relación 1-to-many con RutinaEjercicio
     */
    @OneToMany(mappedBy = "rutina", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<RutinaEjercicio> ejercicios = new HashSet<>();

    /**
     * Relación many-to-many con Etiqueta
     */
    @ManyToMany
    @JoinTable(
        name = "rutina_etiquetas",
        joinColumns = @JoinColumn(name = "id_rutina"),
        inverseJoinColumns = @JoinColumn(name = "id_etiqueta")
    )
    @Builder.Default
    private Set<Etiqueta> etiquetas = new HashSet<>();

    /**
     * Relación 1-to-many con UsuarioRutina
     */
    @OneToMany(mappedBy = "rutina")
    @Builder.Default
    private Set<UsuarioRutina> usuariosRutinas = new HashSet<>();

    @Override
    public String toString() {
        return "Rutina{id=" + id + ", nombre='" + nombre + "', duracion=" + duracionSemanas + " semanas}";
    }
}
```

---

### 16. RutinaEjercicio

```java
package com.nutritrack.nutritrackapi.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad que representa los ejercicios asignados a una rutina.
 * Incluye configuración de series, repeticiones, peso y orden.
 */
@Entity
@Table(name = "rutina_ejercicios")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RutinaEjercicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_rutina", nullable = false)
    private Rutina rutina;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ejercicio", nullable = false)
    private Ejercicio ejercicio;

    @Column(nullable = false)
    private Integer orden;

    @Column(nullable = false)
    private Integer series;

    @Column(nullable = false)
    private Integer repeticiones;

    @Column(precision = 6, scale = 2)
    private BigDecimal peso;

    @Column(name = "duracion_minutos", nullable = false)
    private Integer duracionMinutos;

    @Column(length = 500)
    private String notas;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Override
    public String toString() {
        return "RutinaEjercicio{id=" + id + ", orden=" + orden + ", series=" + series + "x" + repeticiones + "}";
    }
}
```

---

## 🎯 MÓDULO 4: ASSIGNMENTS

### 17. UsuarioPlan

```java
package com.nutritrack.nutritrackapi.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entidad que representa la asignación de un plan a un usuario.
 * Incluye estado, fechas y seguimiento del día actual.
 */
@Entity
@Table(name = "usuarios_planes")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_perfil_usuario", nullable = false)
    private PerfilUsuario perfilUsuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_plan", nullable = false)
    private Plan plan;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDate fechaFin;

    @Column(name = "dia_actual")
    private Integer diaActual;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoAsignacion estado;

    @Column(columnDefinition = "TEXT")
    private String notas;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * Relación 1-to-many con RegistroComida
     */
    @OneToMany(mappedBy = "usuarioPlan")
    @Builder.Default
    private Set<RegistroComida> registrosComidas = new HashSet<>();

    /**
     * Enum para estado de asignación
     */
    public enum EstadoAsignacion {
        ACTIVO,
        PAUSADO,
        COMPLETADO,
        CANCELADO
    }

    @Override
    public String toString() {
        return "UsuarioPlan{id=" + id + ", estado=" + estado + ", dia=" + diaActual + "}";
    }
}
```

---

### 18. UsuarioRutina

```java
package com.nutritrack.nutritrackapi.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entidad que representa la asignación de una rutina a un usuario.
 * Incluye estado, fechas y seguimiento de la semana actual.
 */
@Entity
@Table(name = "usuarios_rutinas")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioRutina {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_perfil_usuario", nullable = false)
    private PerfilUsuario perfilUsuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_rutina", nullable = false)
    private Rutina rutina;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDate fechaFin;

    @Column(name = "semana_actual")
    private Integer semanaActual;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UsuarioPlan.EstadoAsignacion estado;

    @Column(columnDefinition = "TEXT")
    private String notas;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * Relación 1-to-many con RegistroEjercicio
     */
    @OneToMany(mappedBy = "usuarioRutina")
    @Builder.Default
    private Set<RegistroEjercicio> registrosEjercicios = new HashSet<>();

    @Override
    public String toString() {
        return "UsuarioRutina{id=" + id + ", estado=" + estado + ", semana=" + semanaActual + "}";
    }
}
```

---

## 📊 MÓDULO 5: TRACKING

### 19. RegistroComida

```java
package com.nutritrack.nutritrackapi.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Entidad que representa el registro de comidas consumidas por el usuario.
 * Puede estar vinculado a un plan o ser independiente.
 */
@Entity
@Table(name = "registros_comidas")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistroComida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_perfil_usuario", nullable = false)
    private PerfilUsuario perfilUsuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_comida", nullable = false)
    private Comida comida;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario_plan")
    private UsuarioPlan usuarioPlan;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false)
    private LocalTime hora;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_comida", nullable = false, length = 20)
    private Comida.TipoComida tipoComida;

    @Column(precision = 5, scale = 2)
    private BigDecimal porciones;

    @Column(name = "calorias_consumidas", precision = 8, scale = 2)
    private BigDecimal caloriasConsumidas;

    @Column(columnDefinition = "TEXT")
    private String notas;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Override
    public String toString() {
        return "RegistroComida{id=" + id + ", fecha=" + fecha + ", tipo=" + tipoComida + "}";
    }
}
```

---

### 20. RegistroEjercicio

```java
package com.nutritrack.nutritrackapi.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Entidad que representa el registro de ejercicios realizados por el usuario.
 * Puede estar vinculado a una rutina o ser independiente.
 */
@Entity
@Table(name = "registros_ejercicios")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistroEjercicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_perfil_usuario", nullable = false)
    private PerfilUsuario perfilUsuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ejercicio", nullable = false)
    private Ejercicio ejercicio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario_rutina")
    private UsuarioRutina usuarioRutina;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false)
    private LocalTime hora;

    @Column(name = "duracion_minutos", nullable = false)
    private Integer duracionMinutos;

    @Column(name = "series_realizadas")
    private Integer seriesRealizadas;

    @Column(name = "repeticiones_realizadas")
    private Integer repeticionesRealizadas;

    @Column(name = "peso_utilizado", precision = 6, scale = 2)
    private BigDecimal pesoUtilizado;

    @Column(name = "calorias_quemadas", precision = 8, scale = 2)
    private BigDecimal caloriasQuemadas;

    @Column(columnDefinition = "TEXT")
    private String notas;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Override
    public String toString() {
        return "RegistroEjercicio{id=" + id + ", fecha=" + fecha + ", duracion=" + duracionMinutos + "min}";
    }
}
```

---

## ⚙️ CONFIGURACIÓN NECESARIA

### Habilitar Auditoría JPA

Crea la clase de configuración:

```java
package com.nutritrack.nutritrackapi.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {
}
```

### application.properties

```properties
# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5433/nutritrack_db
spring.datasource.username=postgres
spring.datasource.password=your_password
spring.datasource.driver-class-name=org.postgresql.Driver
```

---

## 📝 NOTAS IMPORTANTES

1. **Lazy Loading**: Todas las relaciones many-to-one y one-to-many usan `FetchType.LAZY` para optimización.

2. **Cascade Types**:
    - `CascadeType.ALL` en relaciones one-to-many donde el padre es propietario total
    - `orphanRemoval = true` para eliminar automáticamente huérfanos

3. **Builder Pattern**: Todas las entidades usan `@Builder` de Lombok para facilitar la creación.

4. **Auditoría**: Entidades con `@CreatedDate` y `@LastModifiedDate` requieren `@EnableJpaAuditing`.

5. **Composite Keys**: `Receta` y `UsuarioEtiquetaSalud` usan `@EmbeddedId` para claves compuestas.

6. **Enums**: Todos los enums usan `@Enumerated(EnumType.STRING)` para legibilidad en la base de datos.

7. **Unique Constraints**: Aplicados en `nombre` de: Etiqueta, Ingrediente, Ejercicio, Comida, Plan, Rutina (RN07).

---

## ✅ VALIDACIÓN

Para validar que las entidades coinciden con la base de datos:

```bash
# Ejecutar con spring.jpa.hibernate.ddl-auto=validate
mvn spring-boot:run
```

Si hay discrepancias, Hibernate lanzará errores detallados.

---

**Documento completado:** 20 entidades JPA listas para implementación.
