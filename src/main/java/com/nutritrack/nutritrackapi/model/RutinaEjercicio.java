package com.nutritrack.nutritrackapi.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "rutina_ejercicios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RutinaEjercicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_rutina", nullable = false)
    private Rutina rutina;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ejercicio", nullable = false)
    private Ejercicio ejercicio;

    @Column(nullable = false)
    private Integer orden; // Orden en la rutina (1, 2, 3...)

    @Column(nullable = false)
    private Integer series; // Número de series

    @Column(nullable = false)
    private Integer repeticiones; // Repeticiones por serie

    @Column(precision = 6, scale = 2)
    private BigDecimal peso; // Peso en kg (opcional, para ejercicios de fuerza)

    @Column(nullable = false)
    private Integer duracionMinutos; // Duración total del ejercicio en minutos

    @Column(length = 500)
    private String notas; // Notas adicionales (descanso, forma, etc)

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
