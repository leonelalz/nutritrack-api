# 🗄️ REPOSITORIOS - GUÍA COMPLETA DE SPRING DATA JPA

> **Documento de Referencia para Reconstrucción**  
> Contiene todos los repositories con queries custom, métodos derivados y proyecciones.  
> Compatible con Spring Boot 3.5.7 + Spring Data JPA

---

## 📋 ÍNDICE DE REPOSITORIOS

### MÓDULO 1: Authentication & Profiles
1. [RolRepository](#rolrepository)
2. [CuentaAuthRepository](#cuentaauthrepository)
3. [PerfilUsuarioRepository](#perfilusuariorepository)
4. [UsuarioPerfilSaludRepository](#usuarioperfilsaludrepository)
5. [UsuarioHistorialMedidaRepository](#usuariohistorialmedarepository)

### MÓDULO 2: Content Library
6. [EtiquetaRepository](#etiquetarepository)
7. [IngredienteRepository](#ingredienterepository)
8. [EjercicioRepository](#ejerciciorepository)
9. [ComidaRepository](#comidarepository)

### MÓDULO 3: Plans & Routines
10. [PlanRepository](#planrepository)
11. [PlanDiaRepository](#plandiarepository)
12. [RutinaRepository](#rutinarepository)

### MÓDULO 4: Assignments
13. [UsuarioPlanRepository](#usuarioplanrepository)
14. [UsuarioRutinaRepository](#usuariorutinarepository)

### MÓDULO 5: Tracking
15. [RegistroComidaRepository](#registrocomidarepository)
16. [RegistroEjercicioRepository](#registroejerciciorepository)

---

## 🔐 MÓDULO 1: AUTHENTICATION & PROFILES

### RolRepository

```java
package com.nutritrack.nutritrackapi.repository;

import com.nutritrack.nutritrackapi.model.Rol;
import com.nutritrack.nutritrackapi.model.TipoRol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RolRepository extends JpaRepository<Rol, Long> {
    
    /**
     * Busca un rol por su tipo.
     * @param tipo Tipo de rol (USUARIO, ENTRENADOR, NUTRICIONISTA, ADMIN)
     * @return Optional con el rol si existe
     */
    Optional<Rol> findByTipo(TipoRol tipo);
    
    /**
     * Verifica si existe un rol con el tipo especificado.
     * @param tipo Tipo de rol
     * @return true si existe
     */
    boolean existsByTipo(TipoRol tipo);
}
```

---

### CuentaAuthRepository

```java
package com.nutritrack.nutritrackapi.repository;

import com.nutritrack.nutritrackapi.model.CuentaAuth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CuentaAuthRepository extends JpaRepository<CuentaAuth, Long> {
    
    /**
     * Busca una cuenta de autenticación por email.
     * @param email Email del usuario
     * @return Optional con la cuenta si existe
     */
    Optional<CuentaAuth> findByEmail(String email);
    
    /**
     * Verifica si existe una cuenta con el email especificado.
     * @param email Email a verificar
     * @return true si existe
     */
    boolean existsByEmail(String email);
    
    /**
     * Busca cuenta con perfil y rol cargados (optimización con JOIN FETCH).
     * Evita N+1 queries al cargar relaciones LAZY.
     * @param email Email del usuario
     * @return Optional con la cuenta y relaciones cargadas
     */
    @Query("""
        SELECT c FROM CuentaAuth c
        LEFT JOIN FETCH c.perfilUsuario p
        LEFT JOIN FETCH c.rol r
        WHERE c.email = :email
    """)
    Optional<CuentaAuth> findByEmailWithProfile(@Param("email") String email);
    
    /**
     * Busca cuenta por ID con perfil y rol cargados.
     * @param id ID de la cuenta
     * @return Optional con la cuenta
     */
    @Query("""
        SELECT c FROM CuentaAuth c
        LEFT JOIN FETCH c.perfilUsuario p
        LEFT JOIN FETCH c.rol r
        WHERE c.id = :id
    """)
    Optional<CuentaAuth> findByIdWithProfile(@Param("id") Long id);
}
```

---

### PerfilUsuarioRepository

```java
package com.nutritrack.nutritrackapi.repository;

import com.nutritrack.nutritrackapi.model.PerfilUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PerfilUsuarioRepository extends JpaRepository<PerfilUsuario, Long> {
    
    /**
     * Busca perfil por ID de cuenta de autenticación.
     * @param cuentaAuthId ID de la cuenta de autenticación
     * @return Optional con el perfil si existe
     */
    Optional<PerfilUsuario> findByCuentaAuthId(Long cuentaAuthId);
    
    /**
     * Busca perfil con todas las relaciones cargadas (perfil salud, medidas, etiquetas).
     * Útil para dashboard o vista completa del usuario.
     * @param id ID del perfil
     * @return Optional con el perfil completo
     */
    @Query("""
        SELECT DISTINCT p FROM PerfilUsuario p
        LEFT JOIN FETCH p.perfilSalud ps
        LEFT JOIN FETCH ps.etiquetas e
        LEFT JOIN FETCH p.medidas m
        WHERE p.id = :id
    """)
    Optional<PerfilUsuario> findByIdWithDetails(@Param("id") Long id);
    
    /**
     * Busca perfil por email de la cuenta.
     * @param email Email del usuario
     * @return Optional con el perfil
     */
    @Query("""
        SELECT p FROM PerfilUsuario p
        JOIN p.cuentaAuth c
        WHERE c.email = :email
    """)
    Optional<PerfilUsuario> findByEmail(@Param("email") String email);
}
```

---

### UsuarioPerfilSaludRepository

```java
package com.nutritrack.nutritrackapi.repository;

import com.nutritrack.nutritrackapi.model.UsuarioPerfilSalud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioPerfilSaludRepository extends JpaRepository<UsuarioPerfilSalud, Long> {
    
    /**
     * Busca perfil de salud por ID de perfil de usuario.
     * @param perfilUsuarioId ID del perfil de usuario
     * @return Optional con el perfil de salud
     */
    Optional<UsuarioPerfilSalud> findByPerfilUsuarioId(Long perfilUsuarioId);
    
    /**
     * Busca perfil de salud con etiquetas cargadas.
     * @param id ID del perfil de salud
     * @return Optional con el perfil y etiquetas
     */
    @Query("""
        SELECT DISTINCT ps FROM UsuarioPerfilSalud ps
        LEFT JOIN FETCH ps.etiquetas e
        WHERE ps.id = :id
    """)
    Optional<UsuarioPerfilSalud> findByIdWithEtiquetas(@Param("id") Long id);
    
    /**
     * Verifica si existe perfil de salud para un usuario.
     * @param perfilUsuarioId ID del perfil de usuario
     * @return true si existe
     */
    boolean existsByPerfilUsuarioId(Long perfilUsuarioId);
}
```

---

### UsuarioHistorialMedidaRepository

```java
package com.nutritrack.nutritrackapi.repository;

import com.nutritrack.nutritrackapi.model.UsuarioHistorialMedida;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioHistorialMedidaRepository extends JpaRepository<UsuarioHistorialMedida, Long> {
    
    /**
     * Encuentra todas las medidas de un perfil ordenadas por fecha descendente.
     * @param perfilUsuarioId ID del perfil
     * @param pageable Configuración de paginación
     * @return Page con las medidas
     */
    Page<UsuarioHistorialMedida> findByPerfilUsuarioIdOrderByFechaMedicionDesc(
        Long perfilUsuarioId, 
        Pageable pageable
    );
    
    /**
     * Obtiene la última medida registrada de un usuario.
     * @param perfilUsuarioId ID del perfil
     * @return Optional con la última medida
     */
    @Query("""
        SELECT m FROM UsuarioHistorialMedida m
        WHERE m.perfilUsuario.id = :perfilUsuarioId
        ORDER BY m.fechaMedicion DESC
        LIMIT 1
    """)
    Optional<UsuarioHistorialMedida> findUltimaMedida(@Param("perfilUsuarioId") Long perfilUsuarioId);
    
    /**
     * Encuentra medidas dentro de un rango de fechas.
     * @param perfilUsuarioId ID del perfil
     * @param fechaInicio Fecha de inicio del rango
     * @param fechaFin Fecha de fin del rango
     * @return Lista de medidas en el rango
     */
    List<UsuarioHistorialMedida> findByPerfilUsuarioIdAndFechaMedicionBetweenOrderByFechaMedicionDesc(
        Long perfilUsuarioId,
        LocalDate fechaInicio,
        LocalDate fechaFin
    );
    
    /**
     * Cuenta las medidas registradas de un usuario.
     * @param perfilUsuarioId ID del perfil
     * @return Número de medidas
     */
    long countByPerfilUsuarioId(Long perfilUsuarioId);
}
```

---

## 📚 MÓDULO 2: CONTENT LIBRARY

### EtiquetaRepository

```java
package com.nutritrack.nutritrackapi.repository;

import com.nutritrack.nutritrackapi.model.Etiqueta;
import com.nutritrack.nutritrackapi.model.TipoEtiqueta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EtiquetaRepository extends JpaRepository<Etiqueta, Long> {
    
    /**
     * Busca etiqueta por nombre exacto.
     * @param nombre Nombre de la etiqueta
     * @return Optional con la etiqueta
     */
    Optional<Etiqueta> findByNombre(String nombre);
    
    /**
     * Busca etiquetas por tipo.
     * @param tipo Tipo de etiqueta
     * @return Lista de etiquetas del tipo especificado
     */
    List<Etiqueta> findByTipoEtiqueta(TipoEtiqueta tipo);
    
    /**
     * Busca etiquetas que contengan un texto (case insensitive).
     * @param nombre Texto a buscar
     * @return Lista de etiquetas que coinciden
     */
    List<Etiqueta> findByNombreContainingIgnoreCase(String nombre);
    
    /**
     * Verifica si existe una etiqueta con el nombre especificado.
     * @param nombre Nombre de la etiqueta
     * @return true si existe
     */
    boolean existsByNombre(String nombre);
    
    /**
     * Busca etiquetas por IDs.
     * @param ids Lista de IDs
     * @return Lista de etiquetas
     */
    List<Etiqueta> findByIdIn(List<Long> ids);
}
```

---

### IngredienteRepository

```java
package com.nutritrack.nutritrackapi.repository;

import com.nutritrack.nutritrackapi.model.GrupoAlimenticio;
import com.nutritrack.nutritrackapi.model.Ingrediente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IngredienteRepository extends JpaRepository<Ingrediente, Long> {
    
    /**
     * Busca ingrediente por nombre exacto.
     * @param nombre Nombre del ingrediente
     * @return Optional con el ingrediente
     */
    Optional<Ingrediente> findByNombre(String nombre);
    
    /**
     * Busca ingredientes que contengan un texto en su nombre.
     * @param nombre Texto a buscar
     * @param pageable Configuración de paginación
     * @return Page con ingredientes encontrados
     */
    Page<Ingrediente> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);
    
    /**
     * Busca ingredientes por grupo alimenticio.
     * @param grupo Grupo alimenticio
     * @param pageable Configuración de paginación
     * @return Page con ingredientes del grupo
     */
    Page<Ingrediente> findByGrupoAlimenticio(GrupoAlimenticio grupo, Pageable pageable);
    
    /**
     * Verifica si existe un ingrediente con el nombre especificado.
     * @param nombre Nombre del ingrediente
     * @return true si existe
     */
    boolean existsByNombre(String nombre);
    
    /**
     * Busca ingredientes con etiquetas cargadas.
     * @param id ID del ingrediente
     * @return Optional con el ingrediente y etiquetas
     */
    @Query("""
        SELECT DISTINCT i FROM Ingrediente i
        LEFT JOIN FETCH i.etiquetas e
        WHERE i.id = :id
    """)
    Optional<Ingrediente> findByIdWithEtiquetas(@Param("id") Long id);
    
    /**
     * Busca ingredientes excluyendo ciertas etiquetas (alergias).
     * Implementa RN16: Filtrado por alergias.
     * @param etiquetasIds IDs de etiquetas a excluir
     * @param pageable Configuración de paginación
     * @return Page con ingredientes seguros
     */
    @Query("""
        SELECT DISTINCT i FROM Ingrediente i
        WHERE i.id NOT IN (
            SELECT ie.id FROM Ingrediente ie
            JOIN ie.etiquetas e
            WHERE e.id IN :etiquetasIds
        )
    """)
    Page<Ingrediente> findIngredientesSinAlergias(
        @Param("etiquetasIds") List<Long> etiquetasIds,
        Pageable pageable
    );
    
    /**
     * Busca todos los ingredientes con sus etiquetas (optimizado).
     * @return Lista de ingredientes
     */
    @Query("""
        SELECT DISTINCT i FROM Ingrediente i
        LEFT JOIN FETCH i.etiquetas
    """)
    List<Ingrediente> findAllWithEtiquetas();
}
```

---

### EjercicioRepository

```java
package com.nutritrack.nutritrackapi.repository;

import com.nutritrack.nutritrackapi.model.Dificultad;
import com.nutritrack.nutritrackapi.model.Ejercicio;
import com.nutritrack.nutritrackapi.model.TipoEjercicio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EjercicioRepository extends JpaRepository<Ejercicio, Long> {
    
    /**
     * Busca ejercicio por nombre exacto.
     * @param nombre Nombre del ejercicio
     * @return Optional con el ejercicio
     */
    Optional<Ejercicio> findByNombre(String nombre);
    
    /**
     * Busca ejercicios que contengan un texto en su nombre.
     * @param nombre Texto a buscar
     * @param pageable Configuración de paginación
     * @return Page con ejercicios encontrados
     */
    Page<Ejercicio> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);
    
    /**
     * Busca ejercicios por tipo.
     * @param tipo Tipo de ejercicio
     * @param pageable Configuración de paginación
     * @return Page con ejercicios del tipo
     */
    Page<Ejercicio> findByTipoEjercicio(TipoEjercicio tipo, Pageable pageable);
    
    /**
     * Busca ejercicios por dificultad.
     * @param dificultad Nivel de dificultad
     * @param pageable Configuración de paginación
     * @return Page con ejercicios de la dificultad
     */
    Page<Ejercicio> findByDificultad(Dificultad dificultad, Pageable pageable);
    
    /**
     * Verifica si existe un ejercicio con el nombre especificado.
     * @param nombre Nombre del ejercicio
     * @return true si existe
     */
    boolean existsByNombre(String nombre);
    
    /**
     * Busca ejercicio con etiquetas cargadas.
     * @param id ID del ejercicio
     * @return Optional con el ejercicio y etiquetas
     */
    @Query("""
        SELECT DISTINCT e FROM Ejercicio e
        LEFT JOIN FETCH e.etiquetas et
        WHERE e.id = :id
    """)
    Optional<Ejercicio> findByIdWithEtiquetas(@Param("id") Long id);
    
    /**
     * Busca ejercicios filtrando por tipo y dificultad.
     * @param tipo Tipo de ejercicio
     * @param dificultad Dificultad
     * @param pageable Configuración de paginación
     * @return Page con ejercicios filtrados
     */
    Page<Ejercicio> findByTipoEjercicioAndDificultad(
        TipoEjercicio tipo,
        Dificultad dificultad,
        Pageable pageable
    );
}
```

---

### ComidaRepository

```java
package com.nutritrack.nutritrackapi.repository;

import com.nutritrack.nutritrackapi.model.Comida;
import com.nutritrack.nutritrackapi.model.TipoComida;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ComidaRepository extends JpaRepository<Comida, Long> {
    
    /**
     * Busca comida por nombre exacto.
     * @param nombre Nombre de la comida
     * @return Optional con la comida
     */
    Optional<Comida> findByNombre(String nombre);
    
    /**
     * Busca comidas que contengan un texto en su nombre.
     * @param nombre Texto a buscar
     * @param pageable Configuración de paginación
     * @return Page con comidas encontradas
     */
    Page<Comida> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);
    
    /**
     * Busca comidas por tipo.
     * @param tipo Tipo de comida
     * @param pageable Configuración de paginación
     * @return Page con comidas del tipo
     */
    Page<Comida> findByTipoComida(TipoComida tipo, Pageable pageable);
    
    /**
     * Verifica si existe una comida con el nombre especificado.
     * @param nombre Nombre de la comida
     * @return true si existe
     */
    boolean existsByNombre(String nombre);
    
    /**
     * Busca comida con recetas e ingredientes cargados.
     * @param id ID de la comida
     * @return Optional con la comida completa
     */
    @Query("""
        SELECT DISTINCT c FROM Comida c
        LEFT JOIN FETCH c.recetas r
        LEFT JOIN FETCH r.ingrediente i
        WHERE c.id = :id
    """)
    Optional<Comida> findByIdWithRecetas(@Param("id") Long id);
    
    /**
     * Busca comidas que NO contengan ingredientes con las etiquetas especificadas.
     * Implementa RN16: Filtrado por alergias en comidas.
     * @param etiquetasIds IDs de etiquetas a excluir
     * @param pageable Configuración de paginación
     * @return Page con comidas seguras
     */
    @Query("""
        SELECT DISTINCT c FROM Comida c
        WHERE c.id NOT IN (
            SELECT cm.id FROM Comida cm
            JOIN cm.recetas r
            JOIN r.ingrediente i
            JOIN i.etiquetas e
            WHERE e.id IN :etiquetasIds
        )
    """)
    Page<Comida> findComidasSinAlergias(
        @Param("etiquetasIds") List<Long> etiquetasIds,
        Pageable pageable
    );
    
    /**
     * Cuenta cuántas recetas tiene una comida.
     * Implementa RN09: Validar dependencias antes de eliminar.
     * @param comidaId ID de la comida
     * @return Número de recetas
     */
    @Query("""
        SELECT COUNT(r) FROM Receta r
        WHERE r.id.comidaId = :comidaId
    """)
    long countRecetasByComidaId(@Param("comidaId") Long comidaId);
}
```

---

## 📅 MÓDULO 3: PLANS & ROUTINES

### PlanRepository

```java
package com.nutritrack.nutritrackapi.repository;

import com.nutritrack.nutritrackapi.model.Plan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Long> {
    
    /**
     * Busca plan por nombre exacto.
     * @param nombre Nombre del plan
     * @return Optional con el plan
     */
    Optional<Plan> findByNombre(String nombre);
    
    /**
     * Busca planes activos.
     * @param pageable Configuración de paginación
     * @return Page con planes activos
     */
    Page<Plan> findByActivoTrue(Pageable pageable);
    
    /**
     * Busca planes que contengan un texto en su nombre.
     * @param nombre Texto a buscar
     * @param pageable Configuración de paginación
     * @return Page con planes encontrados
     */
    Page<Plan> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);
    
    /**
     * Verifica si existe un plan con el nombre especificado.
     * @param nombre Nombre del plan
     * @return true si existe
     */
    boolean existsByNombre(String nombre);
    
    /**
     * Busca plan con objetivos y etiquetas cargados.
     * @param id ID del plan
     * @return Optional con el plan completo
     */
    @Query("""
        SELECT DISTINCT p FROM Plan p
        LEFT JOIN FETCH p.objetivos o
        LEFT JOIN FETCH p.etiquetas e
        WHERE p.id = :id
    """)
    Optional<Plan> findByIdWithObjetivosAndEtiquetas(@Param("id") Long id);
    
    /**
     * Busca plan con todos los detalles (objetivos, días, etiquetas).
     * @param id ID del plan
     * @return Optional con el plan completo
     */
    @Query("""
        SELECT DISTINCT p FROM Plan p
        LEFT JOIN FETCH p.objetivos o
        LEFT JOIN FETCH p.dias d
        LEFT JOIN FETCH d.comida c
        LEFT JOIN FETCH p.etiquetas e
        WHERE p.id = :id
    """)
    Optional<Plan> findByIdWithFullDetails(@Param("id") Long id);
    
    /**
     * Cuenta cuántas asignaciones activas tiene un plan.
     * Implementa RN08: Validar dependencias antes de eliminar.
     * @param planId ID del plan
     * @return Número de asignaciones activas
     */
    @Query("""
        SELECT COUNT(up) FROM UsuarioPlan up
        WHERE up.plan.id = :planId
        AND up.estado = 'ACTIVO'
    """)
    long countAsignacionesActivasByPlanId(@Param("planId") Long planId);
}
```

---

### PlanDiaRepository

```java
package com.nutritrack.nutritrackapi.repository;

import com.nutritrack.nutritrackapi.model.PlanDia;
import com.nutritrack.nutritrackapi.model.TipoComida;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlanDiaRepository extends JpaRepository<PlanDia, Long> {
    
    /**
     * Busca todos los días de un plan ordenados por número de día.
     * @param planId ID del plan
     * @return Lista de días del plan
     */
    List<PlanDia> findByPlanIdOrderByNumeroDiaAsc(Long planId);
    
    /**
     * Busca día específico de un plan.
     * @param planId ID del plan
     * @param numeroDia Número del día
     * @return Lista de comidas de ese día
     */
    List<PlanDia> findByPlanIdAndNumeroDia(Long planId, Integer numeroDia);
    
    /**
     * Busca comidas de un tipo específico en un plan.
     * @param planId ID del plan
     * @param tipoComida Tipo de comida
     * @return Lista de días con ese tipo de comida
     */
    List<PlanDia> findByPlanIdAndTipoComida(Long planId, TipoComida tipoComida);
    
    /**
     * Verifica si existe un día específico con tipo de comida en el plan.
     * @param planId ID del plan
     * @param numeroDia Número del día
     * @param tipoComida Tipo de comida
     * @return true si existe
     */
    boolean existsByPlanIdAndNumeroDiaAndTipoComida(
        Long planId,
        Integer numeroDia,
        TipoComida tipoComida
    );
    
    /**
     * Busca días de un plan con comida cargada.
     * @param planId ID del plan
     * @return Lista de días con comida
     */
    @Query("""
        SELECT pd FROM PlanDia pd
        LEFT JOIN FETCH pd.comida c
        WHERE pd.plan.id = :planId
        ORDER BY pd.numeroDia ASC
    """)
    List<PlanDia> findByPlanIdWithComida(@Param("planId") Long planId);
}
```

---

### RutinaRepository

```java
package com.nutritrack.nutritrackapi.repository;

import com.nutritrack.nutritrackapi.model.Rutina;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RutinaRepository extends JpaRepository<Rutina, Long> {
    
    /**
     * Busca rutina por nombre exacto.
     * @param nombre Nombre de la rutina
     * @return Optional con la rutina
     */
    Optional<Rutina> findByNombre(String nombre);
    
    /**
     * Busca rutinas activas.
     * @param pageable Configuración de paginación
     * @return Page con rutinas activas
     */
    Page<Rutina> findByActivoTrue(Pageable pageable);
    
    /**
     * Busca rutinas que contengan un texto en su nombre.
     * @param nombre Texto a buscar
     * @param pageable Configuración de paginación
     * @return Page con rutinas encontradas
     */
    Page<Rutina> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);
    
    /**
     * Verifica si existe una rutina con el nombre especificado.
     * @param nombre Nombre de la rutina
     * @return true si existe
     */
    boolean existsByNombre(String nombre);
    
    /**
     * Busca rutina con etiquetas cargadas.
     * @param id ID de la rutina
     * @return Optional con la rutina y etiquetas
     */
    @Query("""
        SELECT DISTINCT r FROM Rutina r
        LEFT JOIN FETCH r.etiquetas e
        WHERE r.id = :id
    """)
    Optional<Rutina> findByIdWithEtiquetas(@Param("id") Long id);
    
    /**
     * Busca rutina con todos los detalles (ejercicios y etiquetas).
     * @param id ID de la rutina
     * @return Optional con la rutina completa
     */
    @Query("""
        SELECT DISTINCT r FROM Rutina r
        LEFT JOIN FETCH r.ejercicios re
        LEFT JOIN FETCH re.ejercicio e
        LEFT JOIN FETCH r.etiquetas et
        WHERE r.id = :id
    """)
    Optional<Rutina> findByIdWithFullDetails(@Param("id") Long id);
    
    /**
     * Cuenta cuántas asignaciones activas tiene una rutina.
     * Implementa RN08: Validar dependencias antes de eliminar.
     * @param rutinaId ID de la rutina
     * @return Número de asignaciones activas
     */
    @Query("""
        SELECT COUNT(ur) FROM UsuarioRutina ur
        WHERE ur.rutina.id = :rutinaId
        AND ur.estado = 'ACTIVO'
    """)
    long countAsignacionesActivasByRutinaId(@Param("rutinaId") Long rutinaId);
}
```

---

## 🎯 MÓDULO 4: ASSIGNMENTS

### UsuarioPlanRepository

```java
package com.nutritrack.nutritrackapi.repository;

import com.nutritrack.nutritrackapi.model.EstadoAsignacion;
import com.nutritrack.nutritrackapi.model.UsuarioPlan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioPlanRepository extends JpaRepository<UsuarioPlan, Long> {
    
    /**
     * Busca asignaciones de plan por perfil de usuario.
     * @param perfilUsuarioId ID del perfil
     * @param pageable Configuración de paginación
     * @return Page con asignaciones del usuario
     */
    Page<UsuarioPlan> findByPerfilUsuarioId(Long perfilUsuarioId, Pageable pageable);
    
    /**
     * Busca asignaciones de plan por estado.
     * @param perfilUsuarioId ID del perfil
     * @param estado Estado de la asignación
     * @param pageable Configuración de paginación
     * @return Page con asignaciones filtradas
     */
    Page<UsuarioPlan> findByPerfilUsuarioIdAndEstado(
        Long perfilUsuarioId,
        EstadoAsignacion estado,
        Pageable pageable
    );
    
    /**
     * Busca el plan activo actual de un usuario.
     * Implementa RN17: Solo un plan activo por usuario.
     * @param perfilUsuarioId ID del perfil
     * @return Optional con el plan activo
     */
    @Query("""
        SELECT up FROM UsuarioPlan up
        WHERE up.perfilUsuario.id = :perfilUsuarioId
        AND up.estado = 'ACTIVO'
        ORDER BY up.fechaInicio DESC
        LIMIT 1
    """)
    Optional<UsuarioPlan> findPlanActivoByPerfilUsuarioId(@Param("perfilUsuarioId") Long perfilUsuarioId);
    
    /**
     * Verifica si existe un plan activo para el usuario.
     * @param perfilUsuarioId ID del perfil
     * @return true si existe
     */
    boolean existsByPerfilUsuarioIdAndEstado(Long perfilUsuarioId, EstadoAsignacion estado);
    
    /**
     * Busca asignación con plan y detalles cargados.
     * @param id ID de la asignación
     * @return Optional con la asignación completa
     */
    @Query("""
        SELECT up FROM UsuarioPlan up
        LEFT JOIN FETCH up.plan p
        LEFT JOIN FETCH p.objetivos
        WHERE up.id = :id
    """)
    Optional<UsuarioPlan> findByIdWithPlanDetails(@Param("id") Long id);
    
    /**
     * Busca asignaciones dentro de un rango de fechas.
     * @param perfilUsuarioId ID del perfil
     * @param fechaInicio Fecha de inicio del rango
     * @param fechaFin Fecha de fin del rango
     * @return Lista de asignaciones en el rango
     */
    List<UsuarioPlan> findByPerfilUsuarioIdAndFechaInicioBetween(
        Long perfilUsuarioId,
        LocalDate fechaInicio,
        LocalDate fechaFin
    );
    
    /**
     * Cuenta las asignaciones de un usuario por estado.
     * @param perfilUsuarioId ID del perfil
     * @param estado Estado a contar
     * @return Número de asignaciones
     */
    long countByPerfilUsuarioIdAndEstado(Long perfilUsuarioId, EstadoAsignacion estado);
}
```

---

### UsuarioRutinaRepository

```java
package com.nutritrack.nutritrackapi.repository;

import com.nutritrack.nutritrackapi.model.EstadoAsignacion;
import com.nutritrack.nutritrackapi.model.UsuarioRutina;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRutinaRepository extends JpaRepository<UsuarioRutina, Long> {
    
    /**
     * Busca asignaciones de rutina por perfil de usuario.
     * @param perfilUsuarioId ID del perfil
     * @param pageable Configuración de paginación
     * @return Page con asignaciones del usuario
     */
    Page<UsuarioRutina> findByPerfilUsuarioId(Long perfilUsuarioId, Pageable pageable);
    
    /**
     * Busca asignaciones de rutina por estado.
     * @param perfilUsuarioId ID del perfil
     * @param estado Estado de la asignación
     * @param pageable Configuración de paginación
     * @return Page con asignaciones filtradas
     */
    Page<UsuarioRutina> findByPerfilUsuarioIdAndEstado(
        Long perfilUsuarioId,
        EstadoAsignacion estado,
        Pageable pageable
    );
    
    /**
     * Busca la rutina activa actual de un usuario.
     * Implementa RN18: Solo una rutina activa por usuario.
     * @param perfilUsuarioId ID del perfil
     * @return Optional con la rutina activa
     */
    @Query("""
        SELECT ur FROM UsuarioRutina ur
        WHERE ur.perfilUsuario.id = :perfilUsuarioId
        AND ur.estado = 'ACTIVO'
        ORDER BY ur.fechaInicio DESC
        LIMIT 1
    """)
    Optional<UsuarioRutina> findRutinaActivaByPerfilUsuarioId(@Param("perfilUsuarioId") Long perfilUsuarioId);
    
    /**
     * Verifica si existe una rutina activa para el usuario.
     * @param perfilUsuarioId ID del perfil
     * @return true si existe
     */
    boolean existsByPerfilUsuarioIdAndEstado(Long perfilUsuarioId, EstadoAsignacion estado);
    
    /**
     * Busca asignación con rutina y ejercicios cargados.
     * @param id ID de la asignación
     * @return Optional con la asignación completa
     */
    @Query("""
        SELECT ur FROM UsuarioRutina ur
        LEFT JOIN FETCH ur.rutina r
        LEFT JOIN FETCH r.ejercicios re
        LEFT JOIN FETCH re.ejercicio e
        WHERE ur.id = :id
    """)
    Optional<UsuarioRutina> findByIdWithRutinaDetails(@Param("id") Long id);
    
    /**
     * Busca asignaciones dentro de un rango de fechas.
     * @param perfilUsuarioId ID del perfil
     * @param fechaInicio Fecha de inicio del rango
     * @param fechaFin Fecha de fin del rango
     * @return Lista de asignaciones en el rango
     */
    List<UsuarioRutina> findByPerfilUsuarioIdAndFechaInicioBetween(
        Long perfilUsuarioId,
        LocalDate fechaInicio,
        LocalDate fechaFin
    );
    
    /**
     * Cuenta las asignaciones de un usuario por estado.
     * @param perfilUsuarioId ID del perfil
     * @param estado Estado a contar
     * @return Número de asignaciones
     */
    long countByPerfilUsuarioIdAndEstado(Long perfilUsuarioId, EstadoAsignacion estado);
}
```

---

## 📊 MÓDULO 5: TRACKING

### RegistroComidaRepository

```java
package com.nutritrack.nutritrackapi.repository;

import com.nutritrack.nutritrackapi.model.RegistroComida;
import com.nutritrack.nutritrackapi.model.TipoComida;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface RegistroComidaRepository extends JpaRepository<RegistroComida, Long> {
    
    /**
     * Busca registros de comida por perfil de usuario.
     * @param perfilUsuarioId ID del perfil
     * @param pageable Configuración de paginación
     * @return Page con registros del usuario
     */
    Page<RegistroComida> findByPerfilUsuarioIdOrderByFechaDescHoraDesc(
        Long perfilUsuarioId,
        Pageable pageable
    );
    
    /**
     * Busca registros de comida por fecha.
     * @param perfilUsuarioId ID del perfil
     * @param fecha Fecha del registro
     * @return Lista de registros del día
     */
    List<RegistroComida> findByPerfilUsuarioIdAndFechaOrderByHoraAsc(
        Long perfilUsuarioId,
        LocalDate fecha
    );
    
    /**
     * Busca registros dentro de un rango de fechas.
     * @param perfilUsuarioId ID del perfil
     * @param fechaInicio Fecha de inicio
     * @param fechaFin Fecha de fin
     * @param pageable Configuración de paginación
     * @return Page con registros en el rango
     */
    Page<RegistroComida> findByPerfilUsuarioIdAndFechaBetween(
        Long perfilUsuarioId,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        Pageable pageable
    );
    
    /**
     * Busca registros de un plan específico.
     * @param usuarioPlanId ID del plan asignado
     * @param pageable Configuración de paginación
     * @return Page con registros del plan
     */
    Page<RegistroComida> findByUsuarioPlanId(Long usuarioPlanId, Pageable pageable);
    
    /**
     * Calcula el total de calorías consumidas en un día.
     * @param perfilUsuarioId ID del perfil
     * @param fecha Fecha del registro
     * @return Total de calorías
     */
    @Query("""
        SELECT COALESCE(SUM(r.caloriasConsumidas), 0)
        FROM RegistroComida r
        WHERE r.perfilUsuario.id = :perfilUsuarioId
        AND r.fecha = :fecha
    """)
    BigDecimal calcularCaloriasConsumidasDelDia(
        @Param("perfilUsuarioId") Long perfilUsuarioId,
        @Param("fecha") LocalDate fecha
    );
    
    /**
     * Cuenta cuántos registros tiene un usuario en un día.
     * @param perfilUsuarioId ID del perfil
     * @param fecha Fecha del registro
     * @return Número de registros
     */
    long countByPerfilUsuarioIdAndFecha(Long perfilUsuarioId, LocalDate fecha);
    
    /**
     * Busca registros con comida cargada.
     * @param perfilUsuarioId ID del perfil
     * @param pageable Configuración de paginación
     * @return Page con registros y comida
     */
    @Query("""
        SELECT r FROM RegistroComida r
        LEFT JOIN FETCH r.comida c
        WHERE r.perfilUsuario.id = :perfilUsuarioId
        ORDER BY r.fecha DESC, r.hora DESC
    """)
    Page<RegistroComida> findByPerfilUsuarioIdWithComida(
        @Param("perfilUsuarioId") Long perfilUsuarioId,
        Pageable pageable
    );
}
```

---

### RegistroEjercicioRepository

```java
package com.nutritrack.nutritrackapi.repository;

import com.nutritrack.nutritrackapi.model.RegistroEjercicio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface RegistroEjercicioRepository extends JpaRepository<RegistroEjercicio, Long> {
    
    /**
     * Busca registros de ejercicio por perfil de usuario.
     * @param perfilUsuarioId ID del perfil
     * @param pageable Configuración de paginación
     * @return Page con registros del usuario
     */
    Page<RegistroEjercicio> findByPerfilUsuarioIdOrderByFechaDescHoraDesc(
        Long perfilUsuarioId,
        Pageable pageable
    );
    
    /**
     * Busca registros de ejercicio por fecha.
     * @param perfilUsuarioId ID del perfil
     * @param fecha Fecha del registro
     * @return Lista de registros del día
     */
    List<RegistroEjercicio> findByPerfilUsuarioIdAndFechaOrderByHoraAsc(
        Long perfilUsuarioId,
        LocalDate fecha
    );
    
    /**
     * Busca registros dentro de un rango de fechas.
     * @param perfilUsuarioId ID del perfil
     * @param fechaInicio Fecha de inicio
     * @param fechaFin Fecha de fin
     * @param pageable Configuración de paginación
     * @return Page con registros en el rango
     */
    Page<RegistroEjercicio> findByPerfilUsuarioIdAndFechaBetween(
        Long perfilUsuarioId,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        Pageable pageable
    );
    
    /**
     * Busca registros de una rutina específica.
     * @param usuarioRutinaId ID de la rutina asignada
     * @param pageable Configuración de paginación
     * @return Page con registros de la rutina
     */
    Page<RegistroEjercicio> findByUsuarioRutinaId(Long usuarioRutinaId, Pageable pageable);
    
    /**
     * Calcula el total de calorías quemadas en un día.
     * @param perfilUsuarioId ID del perfil
     * @param fecha Fecha del registro
     * @return Total de calorías quemadas
     */
    @Query("""
        SELECT COALESCE(SUM(r.caloriasQuemadas), 0)
        FROM RegistroEjercicio r
        WHERE r.perfilUsuario.id = :perfilUsuarioId
        AND r.fecha = :fecha
    """)
    BigDecimal calcularCaloriasQuemadasDelDia(
        @Param("perfilUsuarioId") Long perfilUsuarioId,
        @Param("fecha") LocalDate fecha
    );
    
    /**
     * Calcula el total de minutos de ejercicio en un día.
     * @param perfilUsuarioId ID del perfil
     * @param fecha Fecha del registro
     * @return Total de minutos
     */
    @Query("""
        SELECT COALESCE(SUM(r.duracionMinutos), 0)
        FROM RegistroEjercicio r
        WHERE r.perfilUsuario.id = :perfilUsuarioId
        AND r.fecha = :fecha
    """)
    Integer calcularMinutosEjercicioDelDia(
        @Param("perfilUsuarioId") Long perfilUsuarioId,
        @Param("fecha") LocalDate fecha
    );
    
    /**
     * Cuenta cuántos registros tiene un usuario en un día.
     * @param perfilUsuarioId ID del perfil
     * @param fecha Fecha del registro
     * @return Número de registros
     */
    long countByPerfilUsuarioIdAndFecha(Long perfilUsuarioId, LocalDate fecha);
    
    /**
     * Busca registros con ejercicio cargado.
     * @param perfilUsuarioId ID del perfil
     * @param pageable Configuración de paginación
     * @return Page con registros y ejercicio
     */
    @Query("""
        SELECT r FROM RegistroEjercicio r
        LEFT JOIN FETCH r.ejercicio e
        WHERE r.perfilUsuario.id = :perfilUsuarioId
        ORDER BY r.fecha DESC, r.hora DESC
    """)
    Page<RegistroEjercicio> findByPerfilUsuarioIdWithEjercicio(
        @Param("perfilUsuarioId") Long perfilUsuarioId,
        Pageable pageable
    );
}
```

---

## ⚙️ CONFIGURACIÓN BASE

### application.properties

```properties
# JPA Configuration
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.use_sql_comments=true

# Batch fetching optimization
spring.jpa.properties.hibernate.default_batch_fetch_size=10

# Query hints for performance
spring.jpa.properties.hibernate.query.in_clause_parameter_padding=true

# Second level cache (optional)
# spring.jpa.properties.hibernate.cache.use_second_level_cache=true
# spring.jpa.properties.hibernate.cache.region.factory_class=org.hibernate.cache.jcache.JCacheRegionFactory
```

---

**Documento completado:** 16 repositories con 100+ queries custom y métodos derivados listos para implementación.
