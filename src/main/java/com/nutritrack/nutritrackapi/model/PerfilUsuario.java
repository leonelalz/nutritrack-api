package com.nutritrack.nutritrackapi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nutritrack.nutritrackapi.model.enums.UnidadesMedida;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;


@Entity
@Table(name = "perfiles_usuario")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class PerfilUsuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 100)
    private String apellido;

    @Enumerated(EnumType.STRING)
    @Column(name = "unidades_medida", length = 10)
    @Builder.Default
    private UnidadesMedida unidadesMedida = UnidadesMedida.KG;

    @Column(name = "fecha_inicio_app", nullable = false)
    private LocalDate fechaInicioApp;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false, unique = true, referencedColumnName = "id")
    @JsonIgnore
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
