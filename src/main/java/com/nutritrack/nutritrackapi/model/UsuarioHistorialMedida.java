package com.nutritrack.nutritrackapi.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Entity
@Table(name = "usuario_historial_medidas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioHistorialMedida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fecha_medicion", nullable = false)
    private LocalDate fechaMedicion;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal peso;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal altura;

    @Column(precision = 5, scale = 2)
    private BigDecimal imc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente", nullable = false)
    private PerfilUsuario cliente;

    @PrePersist @PreUpdate
    private void calcularImc() {
        if (peso != null && altura != null && altura.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal alturaMetros = altura.divide(BigDecimal.valueOf(100)); // altura en cm
            this.imc = peso.divide(alturaMetros.pow(2), 2, RoundingMode.HALF_UP);
        }
    }
}
