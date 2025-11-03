package com.nutritrack.nutritrackapi.repository;

import com.nutritrack.nutritrackapi.model.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Long> {

    boolean existsByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Long id);

    List<Plan> findByActivoTrue();

    List<Plan> findByNombreContainingIgnoreCase(String nombre);

    @Query("SELECT p FROM Plan p LEFT JOIN FETCH p.etiquetas WHERE p.id = :id")
    Optional<Plan> findByIdWithEtiquetas(@Param("id") Long id);

    @Query("SELECT p FROM Plan p LEFT JOIN FETCH p.planDias pd LEFT JOIN FETCH pd.comida WHERE p.id = :id")
    Optional<Plan> findByIdWithDias(@Param("id") Long id);

    @Query("SELECT DISTINCT p FROM Plan p LEFT JOIN p.etiquetas e WHERE e.id = :etiquetaId AND p.activo = true")
    List<Plan> findByEtiquetaId(@Param("etiquetaId") Long etiquetaId);
}
