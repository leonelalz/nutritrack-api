package com.example.nutritrackapi.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "roles")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tipo_rol", nullable = false, unique = true)
    @Enumerated(EnumType.STRING)
    private TipoRol tipoRol;

    public enum TipoRol {
        ROLE_USER,
        ROLE_ADMIN
    }
}
