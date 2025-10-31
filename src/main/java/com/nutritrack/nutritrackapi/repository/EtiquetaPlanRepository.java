package com.nutritrack.nutritrackapi.repository;

import com.nutritrack.nutritrackapi.model.EtiquetaPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface EtiquetaPlanRepository extends JpaRepository<EtiquetaPlan, Long> {

    List<EtiquetaPlan> findByIdCatalogoPlan(Long idCatalogoPlan);

    boolean existsByIdEtiqueta(Long idEtiqueta);

    Optional<EtiquetaPlan> findByIdCatalogoPlanAndIdEtiqueta(Long idCatalogoPlan, Long idEtiqueta);

    void deleteByIdCatalogoPlanAndIdEtiqueta(Long idCatalogoPlan, Long idEtiqueta);
}