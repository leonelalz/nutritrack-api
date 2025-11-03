package com.nutritrack.nutritrackapi.repository;

import com.nutritrack.nutritrackapi.model.UsuarioRutina;
import com.nutritrack.nutritrackapi.model.enums.EstadoAsignacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRutinaRepository extends JpaRepository<UsuarioRutina, Long> {

    // Buscar rutinas activas de un usuario
    List<UsuarioRutina> findByPerfilUsuarioIdAndEstado(Long perfilUsuarioId, EstadoAsignacion estado);

    // Buscar todas las rutinas de un usuario
    List<UsuarioRutina> findByPerfilUsuarioId(Long perfilUsuarioId);

    // Buscar usuarios que tienen asignada una rutina específica
    List<UsuarioRutina> findByRutinaId(Long rutinaId);

    // Verificar si un usuario ya tiene asignada una rutina activa
    boolean existsByPerfilUsuarioIdAndRutinaIdAndEstado(Long perfilUsuarioId, Long rutinaId, EstadoAsignacion estado);

    // Buscar con detalles de la rutina
    @Query("SELECT ur FROM UsuarioRutina ur " +
           "JOIN FETCH ur.rutina " +
           "WHERE ur.id = :id")
    Optional<UsuarioRutina> findByIdWithRutina(@Param("id") Long id);

    // Buscar todas las rutinas activas con detalles
    @Query("SELECT ur FROM UsuarioRutina ur " +
           "JOIN FETCH ur.rutina " +
           "WHERE ur.perfilUsuario.id = :perfilUsuarioId " +
           "AND ur.estado = :estado")
    List<UsuarioRutina> findByPerfilUsuarioIdAndEstadoWithRutina(
            @Param("perfilUsuarioId") Long perfilUsuarioId,
            @Param("estado") EstadoAsignacion estado
    );

    // Verificar si un usuario tiene una rutina activa que se solape con las fechas dadas
    @Query("SELECT CASE WHEN COUNT(ur) > 0 THEN true ELSE false END " +
           "FROM UsuarioRutina ur " +
           "WHERE ur.perfilUsuario.id = :perfilUsuarioId " +
           "AND ur.estado = 'ACTIVO' " +
           "AND ((ur.fechaInicio <= :fechaFin AND ur.fechaFin >= :fechaInicio) " +
           "     OR (ur.fechaInicio <= :fechaFin AND ur.fechaFin IS NULL))")
    boolean existsOverlappingActiveRutina(
            @Param("perfilUsuarioId") Long perfilUsuarioId,
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin
    );
}
