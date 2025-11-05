package com.example.nutritrackapi.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Entidad que representa el registro de una comida consumida por el usuario.
 * Módulo 5: US-22 - Marcar Actividad como Completada
 */
@Entity
@Table(name = "registros_comidas")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistroComida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_perfil_usuario", nullable = false)
    private PerfilUsuario perfilUsuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_comida", nullable = false)
    private Comida comida;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario_plan")
    private UsuarioPlan usuarioPlan;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false)
    private LocalTime hora;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_comida", nullable = false, length = 20)
    private TipoComida tipoComida;

    @Column(precision = 5, scale = 2)
    private BigDecimal porciones;

    @Column(name = "calorias_consumidas", precision = 8, scale = 2)
    private BigDecimal caloriasConsumidas;

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

    /**
     * Tipos de comida
     */
    public enum TipoComida {
        DESAYUNO,
        ALMUERZO,
        CENA,
        SNACK,
        PRE_ENTRENAMIENTO,
        POST_ENTRENAMIENTO,
        COLACION
    }
}
