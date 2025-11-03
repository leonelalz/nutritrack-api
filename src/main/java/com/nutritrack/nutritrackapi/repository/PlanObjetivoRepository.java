package com.nutritrack.nutritrackapi.repository;

import com.nutritrack.nutritrackapi.model.PlanObjetivo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlanObjetivoRepository extends JpaRepository<PlanObjetivo, Long> {

    Optional<PlanObjetivo> findByPlanId(Long planId);

    boolean existsByPlanId(Long planId);

    void deleteByPlanId(Long planId);
}
