package com.nutritrack.nutritrackapi.repository;

import com.nutritrack.nutritrackapi.model.EtiquetaEjercicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface EtiquetaEjercicioRepository extends JpaRepository<EtiquetaEjercicio, Long> {

    List<EtiquetaEjercicio> findByIdEjercicio(Long idEjercicio);

    boolean existsByIdEtiqueta(Long idEtiqueta);

    Optional<EtiquetaEjercicio> findByIdEjercicioAndIdEtiqueta(Long idEjercicio, Long idEtiqueta);

    void deleteByIdEjercicioAndIdEtiqueta(Long idEjercicio, Long idEtiqueta);
}