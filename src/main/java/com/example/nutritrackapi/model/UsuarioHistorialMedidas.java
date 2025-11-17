package com.example.nutritrackapi.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entidad UsuarioHistorialMedidas - Historial de mediciones corporales
 * Relación Many-to-1 con PerfilUsuario
 * Permite tracking de progreso físico en el tiempo
 */
@Entity
@Table(name = "usuario_historial_medidas",
    uniqueConstraints = @UniqueConstraint(
        columnNames = {"id_cliente", "fecha_medicion"},
        name = "uk_historial_cliente_fecha"
    )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioHistorialMedidas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente", nullable = false)
    private PerfilUsuario perfilUsuario;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal peso; // Siempre en KG en BD

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal altura; // Siempre en CM en BD

    @Column(precision = 5, scale = 2)
    private BigDecimal imc; // Índice de Masa Corporal

    @Column(name = "fecha_medicion", nullable = false)
    private LocalDate fechaMedicion;

    /**
     * Calcula y establece el IMC basado en peso y altura
     * Fórmula: IMC = peso (kg) / (altura (m))^2
     */
    @PrePersist
    @PreUpdate
    protected void calcularIMC() {
        if (peso != null && altura != null && altura.compareTo(BigDecimal.ZERO) > 0) {
            // Convertir altura de CM a M
            BigDecimal alturaMetros = altura.divide(BigDecimal.valueOf(100), 4, BigDecimal.ROUND_HALF_UP);
            // Calcular IMC
            this.imc = peso.divide(
                alturaMetros.multiply(alturaMetros), 
                2, 
                BigDecimal.ROUND_HALF_UP
            );
        }
    }
}
