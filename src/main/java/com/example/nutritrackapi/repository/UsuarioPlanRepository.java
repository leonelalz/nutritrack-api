package com.example.nutritrackapi.repository;

import com.example.nutritrackapi.model.UsuarioPlan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para gestionar asignaciones de planes a usuarios.
 * Módulo 4: Exploración y Activación
 */
@Repository
public interface UsuarioPlanRepository extends JpaRepository<UsuarioPlan, Long> {

    /**
     * Busca una asignación de plan activa para un usuario y plan específico.
     * Usado en RN17: No duplicar mismo plan activo
     */
    Optional<UsuarioPlan> findByPerfilUsuarioIdAndPlanIdAndEstado(
            Long perfilUsuarioId,
            Long planId,
            UsuarioPlan.EstadoAsignacion estado
    );

    /**
     * Obtiene todos los planes de un usuario.
     */
    List<UsuarioPlan> findByPerfilUsuarioId(Long perfilUsuarioId);

    /**
     * Obtiene los planes de un usuario filtrados por estado.
     */
    List<UsuarioPlan> findByPerfilUsuarioIdAndEstado(
            Long perfilUsuarioId,
            UsuarioPlan.EstadoAsignacion estado
    );

    /**
     * Obtiene el plan activo actual de un usuario (el más reciente si hay varios).
     * Usa LIMIT 1 para evitar NonUniqueResultException cuando hay múltiples planes activos.
     */
    @Query(value = "SELECT * FROM usuarios_planes up " +
           "WHERE up.id_perfil_usuario = :perfilUsuarioId " +
           "AND up.estado = 'ACTIVO' " +
           "ORDER BY up.fecha_inicio DESC " +
           "LIMIT 1", nativeQuery = true)
    Optional<UsuarioPlan> findPlanActivoActual(@Param("perfilUsuarioId") Long perfilUsuarioId);

    /**
     * Obtiene todos los planes activos de un usuario (puede tener múltiples planes diferentes activos).
     */
    @Query("SELECT up FROM UsuarioPlan up " +
           "WHERE up.perfilUsuario.id = :perfilUsuarioId " +
           "AND up.estado = 'ACTIVO' " +
           "ORDER BY up.fechaInicio DESC")
    List<UsuarioPlan> findAllPlanesActivos(@Param("perfilUsuarioId") Long perfilUsuarioId);

    /**
     * Verifica si un usuario tiene un plan específico activo.
     * Útil para RN17.
     */
    boolean existsByPerfilUsuarioIdAndPlanIdAndEstado(
            Long perfilUsuarioId,
            Long planId,
            UsuarioPlan.EstadoAsignacion estado
    );

    /**
     * Obtiene el historial de planes de un usuario paginado.
     */
    Page<UsuarioPlan> findByPerfilUsuarioIdOrderByFechaInicioDesc(
            Long perfilUsuarioId,
            Pageable pageable
    );

    /**
     * Obtiene la asignación más reciente de un plan específico para un usuario.
     * Usado para verificar el estado antes de reactivar.
     */
    Optional<UsuarioPlan> findFirstByPerfilUsuarioIdAndPlanIdOrderByFechaInicioDesc(
            Long perfilUsuarioId,
            Long planId
    );
}
