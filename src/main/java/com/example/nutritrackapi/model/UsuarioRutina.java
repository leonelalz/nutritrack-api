package com.example.nutritrackapi.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidad que representa la asignación de una rutina de ejercicio a un usuario.
 * Tabla: usuarios_rutinas
 * Módulo 4: Exploración y Activación (Cliente)
 */
@Entity
@Table(name = "usuarios_rutinas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioRutina {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_perfil_usuario", nullable = false)
    private PerfilUsuario perfilUsuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_rutina", nullable = false)
    private Rutina rutina;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDate fechaFin;

    @Column(name = "semana_actual")
    private Integer semanaActual;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private UsuarioPlan.EstadoAsignacion estado;

    @Column(name = "notas", columnDefinition = "TEXT")
    private String notas;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * Calcula la semana actual basada en la fecha de inicio y la fecha actual.
     * @return Semana actual de la rutina (1 a duracion_semanas)
     */
    public Integer calcularSemanaActual() {
        if (fechaInicio == null || estado != UsuarioPlan.EstadoAsignacion.ACTIVO) {
            return semanaActual;
        }
        LocalDate hoy = LocalDate.now();
        long diasTranscurridos = java.time.temporal.ChronoUnit.DAYS.between(fechaInicio, hoy);
        return (int) (diasTranscurridos / 7) + 1;
    }

    /**
     * Verifica si la rutina está en un estado final (completado o cancelado).
     * @return true si está en estado final
     */
    public boolean esEstadoFinal() {
        return estado == UsuarioPlan.EstadoAsignacion.COMPLETADO || 
               estado == UsuarioPlan.EstadoAsignacion.CANCELADO;
    }

    /**
     * Verifica si se puede pausar esta rutina.
     * @return true si está activa
     */
    public boolean puedeSerPausada() {
        return estado == UsuarioPlan.EstadoAsignacion.ACTIVO;
    }

    /**
     * Verifica si se puede reanudar esta rutina.
     * @return true si está pausada
     */
    public boolean puedeSerReanudada() {
        return estado == UsuarioPlan.EstadoAsignacion.PAUSADO;
    }
}
