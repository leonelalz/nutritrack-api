package com.nutritrack.nutritrackapi.repository;

import com.nutritrack.nutritrackapi.model.RegistroComida;
import com.nutritrack.nutritrackapi.model.enums.TipoComida;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface RegistroComidaRepository extends JpaRepository<RegistroComida, Long> {

    // Buscar registros de un usuario en una fecha específica
    List<RegistroComida> findByPerfilUsuarioIdAndFecha(Long perfilUsuarioId, LocalDate fecha);

    // Buscar registros de un usuario en un rango de fechas
    List<RegistroComida> findByPerfilUsuarioIdAndFechaBetween(
            Long perfilUsuarioId,
            LocalDate fechaInicio,
            LocalDate fechaFin
    );

    // Buscar registros de un plan específico
    List<RegistroComida> findByUsuarioPlanId(Long usuarioPlanId);

    // Buscar registros de un plan en un rango de fechas
    List<RegistroComida> findByUsuarioPlanIdAndFechaBetween(
            Long usuarioPlanId,
            LocalDate fechaInicio,
            LocalDate fechaFin
    );

    // Buscar por tipo de comida en una fecha
    List<RegistroComida> findByPerfilUsuarioIdAndFechaAndTipoComida(
            Long perfilUsuarioId,
            LocalDate fecha,
            TipoComida tipoComida
    );

    // Contar registros de un plan
    long countByUsuarioPlanId(Long usuarioPlanId);

    // Calcular calorías totales consumidas en un rango de fechas
    @Query("SELECT COALESCE(SUM(rc.caloriasConsumidas), 0) " +
           "FROM RegistroComida rc " +
           "WHERE rc.perfilUsuario.id = :perfilUsuarioId " +
           "AND rc.fecha BETWEEN :fechaInicio AND :fechaFin")
    BigDecimal sumCaloriasConsumidasByPeriodo(
            @Param("perfilUsuarioId") Long perfilUsuarioId,
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin
    );

    // Obtener estadísticas por tipo de comida
    @Query("SELECT rc.tipoComida, COUNT(rc), SUM(rc.caloriasConsumidas) " +
           "FROM RegistroComida rc " +
           "WHERE rc.perfilUsuario.id = :perfilUsuarioId " +
           "AND rc.fecha BETWEEN :fechaInicio AND :fechaFin " +
           "GROUP BY rc.tipoComida")
    List<Object[]> getEstadisticasPorTipoComida(
            @Param("perfilUsuarioId") Long perfilUsuarioId,
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin
    );
}
