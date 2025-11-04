package com.example.nutritrackapi.repository;

import com.example.nutritrackapi.model.Ejercicio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Ejercicio
 * US-08: Gestionar Ejercicios
 * RN07: Nombres únicos
 * RN09: No eliminar si está en uso en rutinas
 */
@Repository
public interface EjercicioRepository extends JpaRepository<Ejercicio, Long> {

    /**
     * Busca ejercicio por nombre (unique constraint)
     */
    Optional<Ejercicio> findByNombre(String nombre);

    /**
     * Busca ejercicios por tipo
     */
    Page<Ejercicio> findByTipoEjercicio(Ejercicio.TipoEjercicio tipo, Pageable pageable);

    /**
     * Busca ejercicios por grupo muscular
     */
    Page<Ejercicio> findByGrupoMuscular(Ejercicio.GrupoMuscular grupo, Pageable pageable);

    /**
     * Busca ejercicios por nivel de dificultad
     */
    Page<Ejercicio> findByNivelDificultad(Ejercicio.NivelDificultad nivel, Pageable pageable);

    /**
     * Busca ejercicios por nombre que contenga (búsqueda parcial)
     */
    Page<Ejercicio> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);

    /**
     * Verifica si un ejercicio está en uso en rutinas (RN09)
     * Cuando se implemente RutinaEjercicio, descomentar esta query
     */
    /*
    @Query("""
        SELECT CASE WHEN COUNT(re) > 0 THEN true ELSE false END
        FROM RutinaEjercicio re
        WHERE re.ejercicio.id = :ejercicioId
    """)
    boolean estaEnUsoEnRutinas(@Param("ejercicioId") Long ejercicioId);
    */

    /**
     * Verifica si nombre ya existe
     */
    boolean existsByNombre(String nombre);

    /**
     * Verifica si nombre existe excluyendo un ID (para actualización)
     */
    boolean existsByNombreAndIdNot(String nombre, Long id);

    /**
     * Busca ejercicios por etiqueta
     */
    @Query("""
        SELECT e FROM Ejercicio e JOIN e.etiquetas et
        WHERE et.id = :etiquetaId
    """)
    Page<Ejercicio> findByEtiquetaId(@Param("etiquetaId") Long etiquetaId, Pageable pageable);

    /**
     * Busca ejercicios por combinación de filtros
     */
    @Query("""
        SELECT e FROM Ejercicio e
        WHERE (:tipo IS NULL OR e.tipoEjercicio = :tipo)
        AND (:grupo IS NULL OR e.grupoMuscular = :grupo)
        AND (:nivel IS NULL OR e.nivelDificultad = :nivel)
    """)
    Page<Ejercicio> findByFiltros(
        @Param("tipo") Ejercicio.TipoEjercicio tipo,
        @Param("grupo") Ejercicio.GrupoMuscular grupo,
        @Param("nivel") Ejercicio.NivelDificultad nivel,
        Pageable pageable
    );

    /**
     * Cuenta ejercicios por tipo
     */
    long countByTipoEjercicio(Ejercicio.TipoEjercicio tipo);

    /**
     * Cuenta ejercicios por grupo muscular
     */
    long countByGrupoMuscular(Ejercicio.GrupoMuscular grupo);
}
