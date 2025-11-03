package com.nutritrack.nutritrackapi.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "registros_ejercicios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistroEjercicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_perfil_usuario", nullable = false)
    private PerfilUsuario perfilUsuario;

    @ManyToOne
    @JoinColumn(name = "id_ejercicio", nullable = false)
    private Ejercicio ejercicio;

    @ManyToOne
    @JoinColumn(name = "id_usuario_rutina")
    private UsuarioRutina usuarioRutina; // Opcional: si está siguiendo una rutina

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

    @Column(name = "duracion_minutos", nullable = false)
    private Integer duracionMinutos;

    @Column(columnDefinition = "TEXT")
    private String notas;

    @Column(name = "calorias_quemadas", precision = 8, scale = 2)
    private BigDecimal caloriasQuemadas; // Calculado al guardar

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // Helper methods
    @PrePersist
    protected void onCreate() {
        if (this.fecha == null) {
            this.fecha = LocalDate.now();
        }
        if (this.hora == null) {
            this.hora = LocalTime.now();
        }
    }
}
