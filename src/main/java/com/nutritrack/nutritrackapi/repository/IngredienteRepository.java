package com.nutritrack.nutritrackapi.repository;

import com.nutritrack.nutritrackapi.model.Ingrediente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IngredienteRepository extends JpaRepository<Ingrediente, Long> {

    boolean existsByNombre(String nombre);
}