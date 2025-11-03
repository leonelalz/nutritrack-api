package com.nutritrack.nutritrackapi.repository;

import com.nutritrack.nutritrackapi.model.Rutina;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RutinaRepository extends JpaRepository<Rutina, Long> {

    boolean existsByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Long id);

    List<Rutina> findByActivoTrue();

    List<Rutina> findByNombreContainingIgnoreCase(String nombre);

    @Query("SELECT r FROM Rutina r LEFT JOIN FETCH r.etiquetas WHERE r.id = :id")
    Optional<Rutina> findByIdWithEtiquetas(@Param("id") Long id);

    @Query("SELECT r FROM Rutina r LEFT JOIN FETCH r.ejercicios re LEFT JOIN FETCH re.ejercicio WHERE r.id = :id")
    Optional<Rutina> findByIdWithEjercicios(@Param("id") Long id);

    @Query("SELECT DISTINCT r FROM Rutina r LEFT JOIN r.etiquetas e WHERE e.id = :etiquetaId AND r.activo = true")
    List<Rutina> findByEtiquetaId(@Param("etiquetaId") Long etiquetaId);
}
