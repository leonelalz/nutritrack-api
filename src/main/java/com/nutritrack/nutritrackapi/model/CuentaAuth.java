package com.nutritrack.nutritrackapi.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "cuentas_auth")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CuentaAuth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

    @Column(nullable = false, updatable = false)
    private LocalDate created_at;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_rol")
    private Rol rol;

    @OneToOne(mappedBy = "cuenta", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private PerfilUsuario perfilUsuario;

    @PrePersist
    public void prePersist() {
        this.created_at = LocalDate.now();
    }
}