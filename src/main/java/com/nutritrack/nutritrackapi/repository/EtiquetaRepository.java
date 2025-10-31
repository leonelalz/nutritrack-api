package com.nutritrack.nutritrackapi.repository;

import com.nutritrack.nutritrackapi.model.Etiqueta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface EtiquetaRepository extends JpaRepository<Etiqueta, Long> {

    boolean existsByNombre(String nombre);

    Optional<Etiqueta> findByNombre(String nombre);
}
