package com.nutritrack.nutritrackapi.repository;

import com.nutritrack.nutritrackapi.model.PlanDia;
import com.nutritrack.nutritrackapi.model.enums.TipoComida;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlanDiaRepository extends JpaRepository<PlanDia, Long> {

    List<PlanDia> findByPlanIdOrderByNumeroDiaAscTipoComidaAsc(Long planId);

    List<PlanDia> findByPlanIdAndNumeroDiaOrderByTipoComidaAsc(Long planId, Integer numeroDia);

    boolean existsByPlanIdAndNumeroDiaAndTipoComida(Long planId, Integer numeroDia, TipoComida tipoComida);

    @Query("SELECT pd FROM PlanDia pd JOIN FETCH pd.comida WHERE pd.plan.id = :planId ORDER BY pd.numeroDia ASC, pd.tipoComida ASC")
    List<PlanDia> findByPlanIdWithComida(@Param("planId") Long planId);

    Optional<PlanDia> findByPlanIdAndNumeroDiaAndTipoComida(Long planId, Integer numeroDia, TipoComida tipoComida);

    void deleteByPlanId(Long planId);
}
