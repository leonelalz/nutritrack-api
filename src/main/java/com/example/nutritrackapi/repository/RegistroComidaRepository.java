package com.example.nutritrackapi.repository;

import com.example.nutritrackapi.model.RegistroComida;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para gestión de registros de comidas.
 * Módulo 5: US-21, US-22, US-23
 */
@Repository
public interface RegistroComidaRepository extends JpaRepository<RegistroComida, Long> {

    /**
     * US-21: Obtener registros de comidas del día para un usuario
     */
    @Query("SELECT rc FROM RegistroComida rc " +
           "WHERE rc.perfilUsuario.id = :perfilUsuarioId " +
           "AND rc.fecha = :fecha " +
           "ORDER BY rc.hora ASC")
    List<RegistroComida> findByPerfilUsuarioIdAndFecha(
            @Param("perfilUsuarioId") Long perfilUsuarioId,
            @Param("fecha") LocalDate fecha);

    @Query("""
    SELECT r
    FROM RegistroComida r
    WHERE r.id = :registroId
      AND r.perfilUsuario.id = :perfilId
    """)
    Optional<RegistroComida> findByIdAndPerfilId(
            @Param("registroId") Long registroId,
            @Param("perfilId") Long perfilId
    );
    /**
     * US-21: Obtener registros de comidas de un plan específico en una fecha
     */
    @Query("SELECT rc FROM RegistroComida rc " +
           "WHERE rc.usuarioPlan.id = :usuarioPlanId " +
           "AND rc.fecha = :fecha " +
           "ORDER BY rc.hora ASC")
    List<RegistroComida> findByUsuarioPlanIdAndFecha(
            @Param("usuarioPlanId") Long usuarioPlanId,
            @Param("fecha") LocalDate fecha);

    /**
     * Obtener todos los registros de un usuario en un rango de fechas
     */
    @Query("SELECT rc FROM RegistroComida rc " +
           "WHERE rc.perfilUsuario.id = :perfilUsuarioId " +
           "AND rc.fecha BETWEEN :fechaInicio AND :fechaFin " +
           "ORDER BY rc.fecha DESC, rc.hora DESC")
    List<RegistroComida> findByPerfilUsuarioIdAndFechaBetween(
            @Param("perfilUsuarioId") Long perfilUsuarioId,
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin);

    /**
     * Verificar si un usuario ya registró una comida específica en un día
     */
    boolean existsByPerfilUsuarioIdAndComidaIdAndFecha(
            Long perfilUsuarioId,
            Long comidaId,
            LocalDate fecha);

    /**
     * US-23: Obtener registros de comidas de un usuario
     */
    List<RegistroComida> findByPerfilUsuarioId(Long perfilUsuarioId);

    /**
     * Eliminar todos los registros de un plan
     */
    void deleteByUsuarioPlanId(Long usuarioPlanId);
}
