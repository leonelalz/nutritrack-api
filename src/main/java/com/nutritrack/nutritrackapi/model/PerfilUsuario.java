package com.nutritrack.nutritrackapi.model;

import com.nutritrack.nutritrackapi.model.enums.UnidadesMedida;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;


@Entity
@Table(name = "perfiles_usuario")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class PerfilUsuario {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 255)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(name = "unidades_medida", length = 10)
    @Builder.Default
    private UnidadesMedida unidadesMedida = UnidadesMedida.KG;

    @Column(name = "fecha_inicio_app", nullable = false)
    private LocalDate fechaInicioApp;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false, unique = true, referencedColumnName = "id")
    private CuentaAuth cuenta;

    @PrePersist
    protected void onCreate() {
        if (this.fechaInicioApp == null) {
            this.fechaInicioApp = LocalDate.now();
        }
        if (this.unidadesMedida == null) {
            this.unidadesMedida = UnidadesMedida.KG;
        }
    }
}
