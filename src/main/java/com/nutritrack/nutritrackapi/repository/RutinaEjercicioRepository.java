package com.nutritrack.nutritrackapi.repository;

import com.nutritrack.nutritrackapi.model.RutinaEjercicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RutinaEjercicioRepository extends JpaRepository<RutinaEjercicio, Long> {

    List<RutinaEjercicio> findByRutinaIdOrderByOrdenAsc(Long rutinaId);

    boolean existsByRutinaIdAndEjercicioId(Long rutinaId, Long ejercicioId);

    Optional<RutinaEjercicio> findByRutinaIdAndEjercicioId(Long rutinaId, Long ejercicioId);

    @Query("SELECT re FROM RutinaEjercicio re JOIN FETCH re.ejercicio WHERE re.rutina.id = :rutinaId ORDER BY re.orden ASC")
    List<RutinaEjercicio> findByRutinaIdWithEjercicio(@Param("rutinaId") Long rutinaId);

    void deleteByRutinaId(Long rutinaId);

    @Query("SELECT COALESCE(MAX(re.orden), 0) FROM RutinaEjercicio re WHERE re.rutina.id = :rutinaId")
    Integer findMaxOrdenByRutinaId(@Param("rutinaId") Long rutinaId);
}
