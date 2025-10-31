package com.nutritrack.nutritrackapi.model;

import com.nutritrack.nutritrackapi.model.enums.TipoRol;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Rol {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private TipoRol tipo;

    public Rol(TipoRol tipo) {
        this.tipo = tipo;
    }
}