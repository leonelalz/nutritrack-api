package com.nutritrack.nutritrackapi.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "cuentas_auth")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data

public class CuentaAuth {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(nullable = false, updatable = false)
    private LocalDate created_at;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_rol")
    private Rol rol;

    @PrePersist
    public void prePersist() {
        this.created_at = LocalDate.now();
    }
}