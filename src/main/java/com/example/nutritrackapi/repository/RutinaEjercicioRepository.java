package com.example.nutritrackapi.repository;

import com.example.nutritrackapi.model.RutinaEjercicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para la entidad RutinaEjercicio
 * US-12: Gestionar Meta (configurar ejercicios)
 * US-15: Ensamblar Rutinas
 * US-17: Ver Detalle de Meta
 */
@Repository
public interface RutinaEjercicioRepository extends JpaRepository<RutinaEjercicio, Long> {

    /**
     * Busca todos los ejercicios de una rutina ordenados por semanaBase, diaSemana y orden
     */
    List<RutinaEjercicio> findByRutinaIdOrderBySemanaBaseAscDiaSemanaAscOrdenAsc(Long rutinaId);

    /**
     * Cuenta cuántos ejercicios tiene una rutina
     */
    long countByRutinaId(Long rutinaId);

    /**
     * Verifica si ya existe un ejercicio en un orden específico de la rutina
     */
    boolean existsByRutinaIdAndOrden(Long rutinaId, Integer orden);

    /**
     * Busca ejercicio específico en una rutina
     */
    @Query("""
        SELECT re FROM RutinaEjercicio re
        WHERE re.rutina.id = :rutinaId
        AND re.ejercicio.id = :ejercicioId
    """)
    List<RutinaEjercicio> findByRutinaIdAndEjercicioId(
        @Param("rutinaId") Long rutinaId,
        @Param("ejercicioId") Long ejercicioId
    );

    /**
     * Elimina todos los ejercicios de una rutina
     */
    void deleteByRutinaId(Long rutinaId);

    /**
     * RN09: Busca rutinas que usan un ejercicio específico (para validar eliminación)
     */
    @Query("""
        SELECT re FROM RutinaEjercicio re
        WHERE re.ejercicio.id = :ejercicioId
    """)
    List<RutinaEjercicio> findByEjercicioId(@Param("ejercicioId") Long ejercicioId);

    /**
     * RN09: Cuenta cuántas rutinas usan un ejercicio específico
     */
    @Query("""
        SELECT COUNT(DISTINCT re.rutina.id)
        FROM RutinaEjercicio re
        WHERE re.ejercicio.id = :ejercicioId
    """)
    long countRutinasUsingEjercicio(@Param("ejercicioId") Long ejercicioId);

    /**
     * Busca el orden máximo en una rutina (para agregar al final)
     */
    @Query("""
        SELECT MAX(re.orden)
        FROM RutinaEjercicio re
        WHERE re.rutina.id = :rutinaId
    """)
    Integer findMaxOrden(@Param("rutinaId") Long rutinaId);
}
