package com.nutritrack.nutritrackapi.repository;

import com.nutritrack.nutritrackapi.model.Comida;
import com.nutritrack.nutritrackapi.model.enums.TipoComida;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ComidaRepository extends JpaRepository<Comida, Long> {
    
    Optional<Comida> findByNombreIgnoreCase(String nombre);
    
    List<Comida> findByTipoComida(TipoComida tipoComida);
    
    List<Comida> findByNombreContainingIgnoreCase(String nombre);
    
    boolean existsByNombreIgnoreCase(String nombre);
}
