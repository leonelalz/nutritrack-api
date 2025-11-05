package com.example.nutritrackapi.repository;

import com.example.nutritrackapi.model.Plan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Plan
 * US-11: Crear Meta del Catálogo (Plan)
 * US-13: Ver Catálogo de Metas
 * US-14: Eliminar Meta
 * RN11: Nombres únicos
 * RN14: No eliminar si tiene usuarios activos
 */
@Repository
public interface PlanRepository extends JpaRepository<Plan, Long> {

    /**
     * Busca plan por nombre
     */
    Optional<Plan> findByNombre(String nombre);

    /**
     * RN11: Verifica si nombre ya existe
     */
    boolean existsByNombre(String nombre);

    /**
     * RN11: Verifica si nombre existe excluyendo un ID (para actualización)
     */
    boolean existsByNombreAndIdNot(String nombre, Long id);

    /**
     * Busca planes activos (para catálogo de clientes)
     * RN28: Filtra por activo = true
     */
    Page<Plan> findByActivoTrue(Pageable pageable);

    /**
     * Busca todos los planes incluyendo inactivos (para admin)
     */
    Page<Plan> findAll(Pageable pageable);

    /**
     * Busca planes por nombre que contenga (búsqueda parcial)
     */
    Page<Plan> findByNombreContainingIgnoreCaseAndActivoTrue(String nombre, Pageable pageable);

    /**
     * Busca planes por etiqueta (solo activos)
     */
    @Query("""
        SELECT p FROM Plan p JOIN p.etiquetas e
        WHERE e.id = :etiquetaId AND p.activo = true
    """)
    Page<Plan> findByEtiquetaIdAndActivoTrue(@Param("etiquetaId") Long etiquetaId, Pageable pageable);

    /**
     * RN15: Busca planes por nombre de etiqueta (objetivo)
     */
    @Query("""
        SELECT DISTINCT p FROM Plan p JOIN p.etiquetas e
        WHERE LOWER(e.nombre) = LOWER(:nombreEtiqueta) AND p.activo = true
    """)
    Page<Plan> findByActivoTrueAndEtiquetasNombre(@Param("nombreEtiqueta") String nombreEtiqueta, Pageable pageable);

    /**
     * Busca planes por múltiples etiquetas (recomendaciones personalizadas)
     */
    @Query("""
        SELECT DISTINCT p FROM Plan p JOIN p.etiquetas e
        WHERE e.id IN :etiquetaIds AND p.activo = true
        GROUP BY p.id
        HAVING COUNT(DISTINCT e.id) >= :minEtiquetas
    """)
    Page<Plan> findByEtiquetasIn(
        @Param("etiquetaIds") List<Long> etiquetaIds,
        @Param("minEtiquetas") long minEtiquetas,
        Pageable pageable
    );

    /**
     * RN14: Verifica si un plan tiene usuarios activos
     */
    @Query("""
        SELECT CASE WHEN COUNT(up) > 0 THEN true ELSE false END
        FROM UsuarioPlan up
        WHERE up.plan.id = :planId
        AND up.estado = 'ACTIVO'
    """)
    boolean tieneUsuariosActivos(@Param("planId") Long planId);

    /**
     * Cuenta planes activos
     */
    long countByActivoTrue();

    /**
     * Busca planes por duración aproximada (útil para recomendaciones)
     */
    @Query("""
        SELECT p FROM Plan p
        WHERE p.duracionDias BETWEEN :minDias AND :maxDias
        AND p.activo = true
        """)
    Page<Plan> findByDuracionRange(
        @Param("minDias") Integer minDias,
        @Param("maxDias") Integer maxDias,
        Pageable pageable
    );

    /**
     * RN32: Obtener etiquetas de ingredientes de un plan
     */
    @Query("""
        SELECT DISTINCT ie.id FROM Plan p
        INNER JOIN p.dias pd
        INNER JOIN pd.comida c
        INNER JOIN c.comidaIngredientes ci
        INNER JOIN ci.ingrediente i
        INNER JOIN i.etiquetas ie
        WHERE p.id = :planId
        """)
    List<Long> findEtiquetasIngredientesByPlanId(@Param("planId") Long planId);
}