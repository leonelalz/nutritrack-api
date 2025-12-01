package com.example.nutritrackapi.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidad que representa la asignación de un plan nutricional a un usuario.
 * Tabla: usuarios_planes
 * Módulo 4: Exploración y Activación (Cliente)
 * 
 * Nota: No hay constraint única porque:
 * - RN17 se valida en código (no duplicar mismo plan ACTIVO)
 * - Un usuario SÍ puede completar un plan y volver a activarlo
 * - Se permite historial de múltiples ejecuciones del mismo plan
 */
@Entity
@Table(name = "usuarios_planes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@lombok.Builder
public class UsuarioPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_perfil_usuario", nullable = false)
    private PerfilUsuario perfilUsuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_plan", nullable = false)
    private Plan plan;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDate fechaFin;

    @Column(name = "dia_actual")
    private Integer diaActual;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoAsignacion estado;

    @Column(name = "notas", columnDefinition = "TEXT")
    private String notas;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * Estados posibles de una asignación de plan/rutina.
     * - ACTIVO: Plan actualmente en uso
     * - PAUSADO: Plan temporalmente pausado
     * - COMPLETADO: Plan completado exitosamente
     * - CANCELADO: Plan cancelado por el usuario
     */
    public enum EstadoAsignacion {
        ACTIVO,
        PAUSADO,
        COMPLETADO,
        CANCELADO
    }

    /**
     * Calcula el día actual basado en la fecha de inicio y la fecha actual.
     * @return Día actual del plan (1 a duracion_dias)
     */
    public Integer calcularDiaActual() {
        if (fechaInicio == null || estado != EstadoAsignacion.ACTIVO) {
            return diaActual;
        }
        LocalDate hoy = LocalDate.now();
        long diasTranscurridos = java.time.temporal.ChronoUnit.DAYS.between(fechaInicio, hoy);
        return (int) (diasTranscurridos + 1);
    }

    /**
     * Verifica si el plan está en un estado final (completado o cancelado).
     * @return true si está en estado final
     */
    public boolean esEstadoFinal() {
        return estado == EstadoAsignacion.COMPLETADO || estado == EstadoAsignacion.CANCELADO;
    }

    /**
     * Verifica si se puede pausar este plan.
     * @return true si está activo
     */
    public boolean puedeSerPausado() {
        return estado == EstadoAsignacion.ACTIVO;
    }

    /**
     * Verifica si se puede reanudar este plan.
     * @return true si está pausado
     */
    public boolean puedeSerReanudado() {
        return estado == EstadoAsignacion.PAUSADO;
    }
}
