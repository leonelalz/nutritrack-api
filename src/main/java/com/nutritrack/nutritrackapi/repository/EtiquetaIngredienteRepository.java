package com.nutritrack.nutritrackapi.repository;

import com.nutritrack.nutritrackapi.model.EtiquetaIngrediente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface EtiquetaIngredienteRepository extends JpaRepository<EtiquetaIngrediente, Long> {

    List<EtiquetaIngrediente> findByIdIngrediente(Long idIngrediente);

    boolean existsByIdEtiqueta(Long idEtiqueta);

    Optional<EtiquetaIngrediente> findByIdIngredienteAndIdEtiqueta(Long idIngrediente, Long idEtiqueta);

    void deleteByIdIngredienteAndIdEtiqueta(Long idIngrediente, Long idEtiqueta);
}