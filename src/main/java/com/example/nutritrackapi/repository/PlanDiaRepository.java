package com.example.nutritrackapi.repository;

import com.example.nutritrackapi.model.PlanDia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para la entidad PlanDia
 * US-12: Gestionar Meta (configurar días)
 * US-17: Ver Detalle de Meta
 * US-21: Ver Actividades de mi Plan
 * 
 * MIGRACIÓN: TipoComida ahora es una entidad (TipoComidaEntity)
 */
@Repository
public interface PlanDiaRepository extends JpaRepository<PlanDia, Long> {

    /**
     * Busca todos los días de un plan ordenados por número de día
     */
    @Query("SELECT pd FROM PlanDia pd WHERE pd.plan.id = :planId ORDER BY pd.numeroDia ASC, pd.tipoComida.ordenVisualizacion ASC")
    List<PlanDia> findByPlanIdOrderByNumeroDiaAscTipoComidaAsc(@Param("planId") Long planId);

    /**
     * Busca actividades de un día específico del plan
     */
    List<PlanDia> findByPlanIdAndNumeroDia(Long planId, Integer numeroDia);

    /**
     * Busca actividades de un plan por ID de tipo de comida
     */
    List<PlanDia> findByPlanIdAndTipoComidaId(Long planId, Long tipoComidaId);

    /**
     * Busca actividades de un plan por nombre de tipo de comida
     */
    @Query("SELECT pd FROM PlanDia pd WHERE pd.plan.id = :planId AND UPPER(pd.tipoComida.nombre) = UPPER(:tipoComidaNombre)")
    List<PlanDia> findByPlanIdAndTipoComidaNombre(@Param("planId") Long planId, @Param("tipoComidaNombre") String tipoComidaNombre);

    /**
     * Cuenta cuántos días tiene programados un plan
     */
    @Query("""
        SELECT COUNT(DISTINCT pd.numeroDia)
        FROM PlanDia pd
        WHERE pd.plan.id = :planId
    """)
    long countDiasProgramados(@Param("planId") Long planId);

    /**
     * Busca el número máximo de día programado en un plan
     */
    @Query("""
        SELECT MAX(pd.numeroDia)
        FROM PlanDia pd
        WHERE pd.plan.id = :planId
    """)
    Integer findMaxNumeroDia(@Param("planId") Long planId);

    /**
     * Verifica si ya existe una comida programada para un día y tipo específico (por ID)
     */
    boolean existsByPlanIdAndNumeroDiaAndTipoComidaId(
        Long planId,
        Integer numeroDia,
        Long tipoComidaId
    );

    /**
     * Elimina todos los días de un plan
     */
    void deleteByPlanId(Long planId);

    /**
     * Busca planes que usan una comida específica (para RN09)
     */
    @Query("""
        SELECT pd FROM PlanDia pd
        WHERE pd.comida.id = :comidaId
    """)
    List<PlanDia> findByComidaId(@Param("comidaId") Long comidaId);

    /**
     * Cuenta cuántos planes usan una comida específica
     */
    @Query("""
        SELECT COUNT(DISTINCT pd.plan.id)
        FROM PlanDia pd
        WHERE pd.comida.id = :comidaId
    """)
    long countPlanesUsingComida(@Param("comidaId") Long comidaId);
}
