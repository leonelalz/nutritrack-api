package com.nutritrack.nutritrackapi.repository;

import com.nutritrack.nutritrackapi.model.Ejercicio;
import com.nutritrack.nutritrackapi.model.enums.Dificultad;
import com.nutritrack.nutritrackapi.model.enums.MusculoPrincipal;
import com.nutritrack.nutritrackapi.model.enums.TipoEjercicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EjercicioRepository extends JpaRepository<Ejercicio, Long> {
    
    Optional<Ejercicio> findByNombreIgnoreCase(String nombre);
    
    List<Ejercicio> findByTipoEjercicio(TipoEjercicio tipoEjercicio);
    
    List<Ejercicio> findByMusculoPrincipal(MusculoPrincipal musculoPrincipal);
    
    List<Ejercicio> findByDificultad(Dificultad dificultad);
    
    List<Ejercicio> findByNombreContainingIgnoreCase(String nombre);
    
    boolean existsByNombreIgnoreCase(String nombre);
}
