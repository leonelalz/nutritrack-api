package com.nutritrack.nutritrackapi.repository;

import com.nutritrack.nutritrackapi.model.UsuarioPlan;
import com.nutritrack.nutritrackapi.model.enums.EstadoAsignacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioPlanRepository extends JpaRepository<UsuarioPlan, Long> {

    // Buscar planes activos de un usuario
    List<UsuarioPlan> findByPerfilUsuarioIdAndEstado(Long perfilUsuarioId, EstadoAsignacion estado);

    // Buscar todos los planes de un usuario
    List<UsuarioPlan> findByPerfilUsuarioId(Long perfilUsuarioId);

    // Buscar usuarios que tienen asignado un plan específico
    List<UsuarioPlan> findByPlanId(Long planId);

    // Verificar si un usuario ya tiene asignado un plan activo
    boolean existsByPerfilUsuarioIdAndPlanIdAndEstado(Long perfilUsuarioId, Long planId, EstadoAsignacion estado);

    // Buscar con detalles del plan
    @Query("SELECT up FROM UsuarioPlan up " +
           "JOIN FETCH up.plan " +
           "WHERE up.id = :id")
    Optional<UsuarioPlan> findByIdWithPlan(@Param("id") Long id);

    // Buscar todos los planes activos con detalles
    @Query("SELECT up FROM UsuarioPlan up " +
           "JOIN FETCH up.plan " +
           "WHERE up.perfilUsuario.id = :perfilUsuarioId " +
           "AND up.estado = :estado")
    List<UsuarioPlan> findByPerfilUsuarioIdAndEstadoWithPlan(
            @Param("perfilUsuarioId") Long perfilUsuarioId,
            @Param("estado") EstadoAsignacion estado
    );

    // Verificar si un usuario tiene un plan activo que se solape con las fechas dadas
    @Query("SELECT CASE WHEN COUNT(up) > 0 THEN true ELSE false END " +
           "FROM UsuarioPlan up " +
           "WHERE up.perfilUsuario.id = :perfilUsuarioId " +
           "AND up.estado = 'ACTIVO' " +
           "AND ((up.fechaInicio <= :fechaFin AND up.fechaFin >= :fechaInicio) " +
           "     OR (up.fechaInicio <= :fechaFin AND up.fechaFin IS NULL))")
    boolean existsOverlappingActivePlan(
            @Param("perfilUsuarioId") Long perfilUsuarioId,
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin
    );
}
