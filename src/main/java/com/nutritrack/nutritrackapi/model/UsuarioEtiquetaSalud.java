package com.nutritrack.nutritrackapi.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "usuario_etiquetas_salud")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioEtiquetaSalud {

    @EmbeddedId
    private UsuarioEtiquetaSaludId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idPerfil")
    @JoinColumn(name = "id_perfil")
    private PerfilUsuario perfil;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idEtiqueta")
    @JoinColumn(name = "id_etiqueta")
    private Etiqueta etiqueta;

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class UsuarioEtiquetaSaludId implements java.io.Serializable {
        
        @Column(name = "id_perfil")
        private UUID idPerfil;

        @Column(name = "id_etiqueta")
        private Long idEtiqueta;
    }
}
