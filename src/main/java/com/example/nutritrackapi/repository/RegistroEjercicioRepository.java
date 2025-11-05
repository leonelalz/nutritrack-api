package com.example.nutritrackapi.repository;

import com.example.nutritrackapi.model.RegistroEjercicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repositorio para gestión de registros de ejercicios.
 * Módulo 5: US-21, US-22, US-23
 */
@Repository
public interface RegistroEjercicioRepository extends JpaRepository<RegistroEjercicio, Long> {

    /**
     * US-21: Obtener registros de ejercicios del día para un usuario
     */
    @Query("SELECT re FROM RegistroEjercicio re " +
           "WHERE re.perfilUsuario.id = :perfilUsuarioId " +
           "AND re.fecha = :fecha " +
           "ORDER BY re.hora ASC")
    List<RegistroEjercicio> findByPerfilUsuarioIdAndFecha(
            @Param("perfilUsuarioId") Long perfilUsuarioId,
            @Param("fecha") LocalDate fecha);

    /**
     * US-21: Obtener registros de ejercicios de una rutina específica en una fecha
     */
    @Query("SELECT re FROM RegistroEjercicio re " +
           "WHERE re.usuarioRutina.id = :usuarioRutinaId " +
           "AND re.fecha = :fecha " +
           "ORDER BY re.hora ASC")
    List<RegistroEjercicio> findByUsuarioRutinaIdAndFecha(
            @Param("usuarioRutinaId") Long usuarioRutinaId,
            @Param("fecha") LocalDate fecha);

    /**
     * Obtener todos los registros de un usuario en un rango de fechas
     */
    @Query("SELECT re FROM RegistroEjercicio re " +
           "WHERE re.perfilUsuario.id = :perfilUsuarioId " +
           "AND re.fecha BETWEEN :fechaInicio AND :fechaFin " +
           "ORDER BY re.fecha DESC, re.hora DESC")
    List<RegistroEjercicio> findByPerfilUsuarioIdAndFechaBetween(
            @Param("perfilUsuarioId") Long perfilUsuarioId,
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin);

    /**
     * Verificar si un usuario ya registró un ejercicio específico en un día
     */
    boolean existsByPerfilUsuarioIdAndEjercicioIdAndFecha(
            Long perfilUsuarioId,
            Long ejercicioId,
            LocalDate fecha);

    /**
     * US-23: Obtener registros de ejercicios de un usuario
     */
    List<RegistroEjercicio> findByPerfilUsuarioId(Long perfilUsuarioId);

    /**
     * Eliminar todos los registros de una rutina
     */
    void deleteByUsuarioRutinaId(Long usuarioRutinaId);
}
