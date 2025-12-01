package com.example.nutritrackapi.repository;

import com.example.nutritrackapi.model.Rutina;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Rutina
 * US-11: Crear Meta del Catálogo (Rutina)
 * US-13: Ver Catálogo de Metas
 * US-14: Eliminar Meta
 * RN11: Nombres únicos
 * RN14: No eliminar si tiene usuarios activos
 */
@Repository
public interface RutinaRepository extends JpaRepository<Rutina, Long> {

    /**
     * Busca rutina por nombre
     */
    Optional<Rutina> findByNombre(String nombre);

    /**
     * RN11: Verifica si nombre ya existe
     */
    boolean existsByNombre(String nombre);

    /**
     * RN11: Verifica si nombre existe excluyendo un ID (para actualización)
     */
    boolean existsByNombreAndIdNot(String nombre, Long id);

    /**
     * Busca rutinas activas (para catálogo de clientes)
     * RN28: Filtra por activo = true
     */
    Page<Rutina> findByActivoTrue(Pageable pageable);

    /**
     * Busca todas las rutinas incluyendo inactivas (para admin)
     */
    Page<Rutina> findAll(Pageable pageable);

    /**
     * Busca rutinas por nombre que contenga (búsqueda parcial)
     */
    Page<Rutina> findByNombreContainingIgnoreCaseAndActivoTrue(String nombre, Pageable pageable);

    /**
     * Busca rutinas por etiqueta (solo activas)
     */
    @Query("""
        SELECT r FROM Rutina r JOIN r.etiquetas e
        WHERE e.id = :etiquetaId AND r.activo = true
    """)
    Page<Rutina> findByEtiquetaIdAndActivoTrue(@Param("etiquetaId") Long etiquetaId, Pageable pageable);

    /**
     * RN15: Busca rutinas por nombre de etiqueta (objetivo)
     */
    @Query("""
        SELECT DISTINCT r FROM Rutina r JOIN r.etiquetas e
        WHERE LOWER(e.nombre) = LOWER(:nombreEtiqueta) AND r.activo = true
    """)
    Page<Rutina> findByActivoTrueAndEtiquetasNombre(@Param("nombreEtiqueta") String nombreEtiqueta, Pageable pageable);

    /**
     * Busca rutinas por múltiples etiquetas (recomendaciones personalizadas)
     */
    @Query("""
        SELECT DISTINCT r FROM Rutina r JOIN r.etiquetas e
        WHERE e.id IN :etiquetaIds AND r.activo = true
        GROUP BY r.id
        HAVING COUNT(DISTINCT e.id) >= :minEtiquetas
    """)
    Page<Rutina> findByEtiquetasIn(
        @Param("etiquetaIds") List<Long> etiquetaIds,
        @Param("minEtiquetas") long minEtiquetas,
        Pageable pageable
    );

    /**
     * RN14: Verifica si una rutina tiene usuarios activos
     * TODO Módulo 5: Descomentar cuando se implemente UsuarioRutina
     */
    /*
    @Query("""
        SELECT CASE WHEN COUNT(ur) > 0 THEN true ELSE false END
        FROM UsuarioRutina ur
        WHERE ur.rutina.id = :rutinaId
        AND ur.estado = 'ACTIVO'
    """)
    boolean tieneUsuariosActivos(@Param("rutinaId") Long rutinaId);
    */

    /**
     * Cuenta rutinas activas
     */
    long countByActivoTrue();

    /**
     * Busca rutinas por duración aproximada
     */
    @Query("""
        SELECT r FROM Rutina r
        WHERE r.duracionSemanas BETWEEN :minSemanas AND :maxSemanas
        AND r.activo = true
    """)
    Page<Rutina> findByDuracionRange(
        @Param("minSemanas") Integer minSemanas,
        @Param("maxSemanas") Integer maxSemanas,
        Pageable pageable
    );

    /**
     * RN33: Obtiene todos los IDs de etiquetas de los ejercicios de una rutina
     * Usado para validar contraindicaciones médicas del usuario
     */
    @Query("""
        SELECT DISTINCT e.id FROM Rutina r
        JOIN r.ejercicios re
        JOIN re.ejercicio ej
        JOIN ej.etiquetas e
        WHERE r.id = :rutinaId
    """)
    List<Long> findEtiquetasEjerciciosByRutinaId(@Param("rutinaId") Long rutinaId);

    /**
     * RN33: Obtiene los nombres de etiquetas conflictivas entre usuario y rutina
     * Retorna las etiquetas de condiciones médicas del usuario que coinciden con etiquetas de ejercicios
     */
    @Query("""
        SELECT DISTINCT e.nombre FROM Etiqueta e
        WHERE e.id IN (
            SELECT ej_e.id FROM Rutina r
            JOIN r.ejercicios re
            JOIN re.ejercicio ej
            JOIN ej.etiquetas ej_e
            WHERE r.id = :rutinaId
        )
        AND e.id IN (
            SELECT ues.etiqueta.id FROM UsuarioEtiquetasSalud ues
            WHERE ues.perfilUsuario.id = :perfilUsuarioId
            AND ues.etiqueta.tipoEtiqueta = 'CONDICION_MEDICA'
        )
    """)
    List<String> findContraindicacionesUsuarioRutina(
        @Param("perfilUsuarioId") Long perfilUsuarioId,
        @Param("rutinaId") Long rutinaId
    );
}
