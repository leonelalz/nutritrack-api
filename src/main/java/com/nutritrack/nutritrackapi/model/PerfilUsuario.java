package com.nutritrack.nutritrackapi.model;

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

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalDate fecha_inicio_app;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cuenta", nullable = false, unique = true)
    private CuentaAuth cuenta;

    @PrePersist
    protected void onCreate() {
        this.fecha_inicio_app = LocalDate.now();
    }
}
