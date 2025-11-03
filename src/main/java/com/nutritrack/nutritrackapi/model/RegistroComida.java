package com.nutritrack.nutritrackapi.model;

import com.nutritrack.nutritrackapi.model.enums.TipoComida;
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
@Table(name = "registros_comidas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistroComida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_perfil_usuario", nullable = false)
    private PerfilUsuario perfilUsuario;

    @ManyToOne
    @JoinColumn(name = "id_comida", nullable = false)
    private Comida comida;

    @ManyToOne
    @JoinColumn(name = "id_usuario_plan")
    private UsuarioPlan usuarioPlan; // Opcional: si está siguiendo un plan

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false)
    private LocalTime hora;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_comida", nullable = false, length = 20)
    private TipoComida tipoComida;

    @Column(name = "porciones", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal porciones = BigDecimal.ONE; // Factor multiplicador (0.5, 1, 1.5, etc.)

    @Column(columnDefinition = "TEXT")
    private String notas;

    @Column(name = "calorias_consumidas", precision = 8, scale = 2)
    private BigDecimal caloriasConsumidas; // Calculado al guardar

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
