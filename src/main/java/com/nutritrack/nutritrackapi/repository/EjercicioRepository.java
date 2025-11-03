package com.nutritrack.nutritrackapi.repository;

import com.nutritrack.nutritrackapi.model.Ejercicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EjercicioRepository extends JpaRepository<Ejercicio, Long> {

    boolean existsByNombre(String nombre);
}