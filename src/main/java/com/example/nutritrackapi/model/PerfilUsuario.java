package com.example.nutritrackapi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Entidad PerfilUsuario - Perfil básico del usuario
 * Relación 1-to-1 con CuentaAuth
 * Contiene datos personales y preferencias
 */
@Entity
@Table(name = "perfiles_usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PerfilUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 100)
    private String apellido;

    @Column(name = "unidades_medida", length = 10)
    @Enumerated(EnumType.STRING)
    private UnidadesMedida unidadesMedida;

    @Column(name = "fecha_inicio_app", nullable = false)
    private LocalDate fechaInicioApp;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false, unique = true)
    @JsonIgnore
    private CuentaAuth cuenta;

    @OneToOne(mappedBy = "perfilUsuario", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private UsuarioPerfilSalud perfilSalud;

    @OneToMany(mappedBy = "perfilUsuario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<UsuarioEtiquetasSalud> etiquetasSalud = new HashSet<>();


    @Transient
    public Set<Etiqueta> getEtiquetas() {
        return etiquetasSalud.stream()
                .map(UsuarioEtiquetasSalud::getEtiqueta)
                .collect(Collectors.toSet());
    }
    
    @PrePersist
    protected void onCreate() {
        if (fechaInicioApp == null) {
            fechaInicioApp = LocalDate.now();
        }
        if (unidadesMedida == null) {
            unidadesMedida = UnidadesMedida.KG;
        }
    }

    /**
     * Unidades de medida para peso
     */
    public enum UnidadesMedida {
        KG,   // Kilogramos
        LBS   // Libras
    }
}
