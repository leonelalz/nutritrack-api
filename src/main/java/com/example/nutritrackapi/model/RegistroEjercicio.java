package com.example.nutritrackapi.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Entidad que representa el registro de un ejercicio realizado por el usuario.
 * Módulo 5: US-22 - Marcar Actividad como Completada
 */
@Entity
@Table(name = "registros_ejercicios")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistroEjercicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_perfil_usuario", nullable = false)
    private PerfilUsuario perfilUsuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ejercicio", nullable = false)
    private Ejercicio ejercicio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario_rutina")
    private UsuarioRutina usuarioRutina;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false)
    private LocalTime hora;

    @Column(name = "series_realizadas")
    private Integer seriesRealizadas;

    @Column(name = "repeticiones_realizadas")
    private Integer repeticionesRealizadas;

    @Column(name = "peso_utilizado", precision = 6, scale = 2)
    private BigDecimal pesoUtilizado;

    @Column(name = "duracion_minutos")
    private Integer duracionMinutos;

    @Column(name = "calorias_quemadas", precision = 8, scale = 2)
    private BigDecimal caloriasQuemadas;

    @Column(columnDefinition = "TEXT")
    private String notas;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (fecha == null) {
            fecha = LocalDate.now();
        }
        if (hora == null) {
            hora = LocalTime.now();
        }
    }
}
