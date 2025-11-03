package com.nutritrack.nutritrackapi.repository;

import com.nutritrack.nutritrackapi.model.RegistroEjercicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface RegistroEjercicioRepository extends JpaRepository<RegistroEjercicio, Long> {

    // Buscar registros de un usuario en una fecha específica
    List<RegistroEjercicio> findByPerfilUsuarioIdAndFecha(Long perfilUsuarioId, LocalDate fecha);

    // Buscar registros de un usuario en un rango de fechas
    List<RegistroEjercicio> findByPerfilUsuarioIdAndFechaBetween(
            Long perfilUsuarioId,
            LocalDate fechaInicio,
            LocalDate fechaFin
    );

    // Buscar registros de una rutina específica
    List<RegistroEjercicio> findByUsuarioRutinaId(Long usuarioRutinaId);

    // Buscar registros de una rutina en un rango de fechas
    List<RegistroEjercicio> findByUsuarioRutinaIdAndFechaBetween(
            Long usuarioRutinaId,
            LocalDate fechaInicio,
            LocalDate fechaFin
    );

    // Buscar registros de un ejercicio específico
    List<RegistroEjercicio> findByPerfilUsuarioIdAndEjercicioId(Long perfilUsuarioId, Long ejercicioId);

    // Contar registros de una rutina
    long countByUsuarioRutinaId(Long usuarioRutinaId);

    // Calcular calorías totales quemadas en un rango de fechas
    @Query("SELECT COALESCE(SUM(re.caloriasQuemadas), 0) " +
           "FROM RegistroEjercicio re " +
           "WHERE re.perfilUsuario.id = :perfilUsuarioId " +
           "AND re.fecha BETWEEN :fechaInicio AND :fechaFin")
    BigDecimal sumCaloriasQuemadasByPeriodo(
            @Param("perfilUsuarioId") Long perfilUsuarioId,
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin
    );

    // Calcular tiempo total de ejercicio en un período
    @Query("SELECT COALESCE(SUM(re.duracionMinutos), 0) " +
           "FROM RegistroEjercicio re " +
           "WHERE re.perfilUsuario.id = :perfilUsuarioId " +
           "AND re.fecha BETWEEN :fechaInicio AND :fechaFin")
    Integer sumDuracionMinutosByPeriodo(
            @Param("perfilUsuarioId") Long perfilUsuarioId,
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin
    );

    // Obtener estadísticas por ejercicio
    @Query("SELECT re.ejercicio.nombre, COUNT(re), SUM(re.caloriasQuemadas), SUM(re.duracionMinutos) " +
           "FROM RegistroEjercicio re " +
           "WHERE re.perfilUsuario.id = :perfilUsuarioId " +
           "AND re.fecha BETWEEN :fechaInicio AND :fechaFin " +
           "GROUP BY re.ejercicio.nombre " +
           "ORDER BY COUNT(re) DESC")
    List<Object[]> getEstadisticasPorEjercicio(
            @Param("perfilUsuarioId") Long perfilUsuarioId,
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin
    );
}
