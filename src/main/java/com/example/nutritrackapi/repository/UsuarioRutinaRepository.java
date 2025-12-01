package com.example.nutritrackapi.repository;

import com.example.nutritrackapi.model.UsuarioPlan;
import com.example.nutritrackapi.model.UsuarioRutina;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para gestionar asignaciones de rutinas a usuarios.
 * Módulo 4: Exploración y Activación
 */
@Repository
public interface UsuarioRutinaRepository extends JpaRepository<UsuarioRutina, Long> {

    /**
     * Busca una asignación de rutina activa para un usuario y rutina específica.
     * Usado en RN17: No duplicar misma rutina activa
     */
    Optional<UsuarioRutina> findByPerfilUsuarioIdAndRutinaIdAndEstado(
            Long perfilUsuarioId,
            Long rutinaId,
            UsuarioPlan.EstadoAsignacion estado
    );

    /**
     * Obtiene todas las rutinas de un usuario.
     */
    List<UsuarioRutina> findByPerfilUsuarioId(Long perfilUsuarioId);

    /**
     * Obtiene las rutinas de un usuario filtradas por estado.
     */
    List<UsuarioRutina> findByPerfilUsuarioIdAndEstado(
            Long perfilUsuarioId,
            UsuarioPlan.EstadoAsignacion estado
    );

    /**
     * Obtiene la rutina activa actual de un usuario (si existe).
     */
    @Query("SELECT ur FROM UsuarioRutina ur " +
           "WHERE ur.perfilUsuario.id = :perfilUsuarioId " +
           "AND ur.estado = 'ACTIVO' " +
           "ORDER BY ur.fechaInicio DESC")
    Optional<UsuarioRutina> findRutinaActivaActual(@Param("perfilUsuarioId") Long perfilUsuarioId);

    /**
     * Obtiene todas las rutinas activas de un usuario (puede tener múltiples rutinas diferentes activas).
     */
    @Query("SELECT ur FROM UsuarioRutina ur " +
           "WHERE ur.perfilUsuario.id = :perfilUsuarioId " +
           "AND ur.estado = 'ACTIVO' " +
           "ORDER BY ur.fechaInicio DESC")
    List<UsuarioRutina> findAllRutinasActivas(@Param("perfilUsuarioId") Long perfilUsuarioId);

    /**
     * Verifica si un usuario tiene una rutina específica activa.
     * Útil para RN17.
     */
    boolean existsByPerfilUsuarioIdAndRutinaIdAndEstado(
            Long perfilUsuarioId,
            Long rutinaId,
            UsuarioPlan.EstadoAsignacion estado
    );

    /**
     * Obtiene el historial de rutinas de un usuario paginado.
     */
    Page<UsuarioRutina> findByPerfilUsuarioIdOrderByFechaInicioDesc(
            Long perfilUsuarioId,
            Pageable pageable
    );

    /**
     * Busca la asignación más reciente de una rutina para un usuario,
     * independientemente del estado. Usado para detectar si la rutina
     * está PAUSADA o CANCELADA al intentar activarla.
     */
    @Query("SELECT ur FROM UsuarioRutina ur " +
           "WHERE ur.perfilUsuario.id = :perfilUsuarioId " +
           "AND ur.rutina.id = :rutinaId " +
           "ORDER BY ur.fechaInicio DESC")
    Optional<UsuarioRutina> findAsignacionMasReciente(
            @Param("perfilUsuarioId") Long perfilUsuarioId,
            @Param("rutinaId") Long rutinaId
    );
}
