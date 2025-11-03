package com.nutritrack.nutritrackapi.repository;

import com.nutritrack.nutritrackapi.model.Ingrediente;
import com.nutritrack.nutritrackapi.model.enums.GrupoAlimenticio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IngredienteRepository extends JpaRepository<Ingrediente, Long> {
    
    Optional<Ingrediente> findByNombreIgnoreCase(String nombre);
    
    List<Ingrediente> findByGrupoAlimenticio(GrupoAlimenticio grupoAlimenticio);
    
    List<Ingrediente> findByNombreContainingIgnoreCase(String nombre);
    
    boolean existsByNombreIgnoreCase(String nombre);
}
