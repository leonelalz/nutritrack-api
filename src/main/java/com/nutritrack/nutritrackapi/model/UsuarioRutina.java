package com.nutritrack.nutritrackapi.model;

import com.nutritrack.nutritrackapi.model.enums.EstadoAsignacion;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios_rutinas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioRutina {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_perfil_usuario", nullable = false)
    private PerfilUsuario perfilUsuario;

    @ManyToOne
    @JoinColumn(name = "id_rutina", nullable = false)
    private Rutina rutina;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDate fechaFin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private EstadoAsignacion estado = EstadoAsignacion.ACTIVO;

    @Column(name = "semana_actual")
    @Builder.Default
    private Integer semanaActual = 1; // Semana de la rutina en la que está el usuario

    @Column(columnDefinition = "TEXT")
    private String notas;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // Helper methods
    public void avanzarSemana() {
        if (this.semanaActual < this.rutina.getDuracionSemanas()) {
            this.semanaActual++;
        }
    }

    public void completar() {
        this.estado = EstadoAsignacion.COMPLETADO;
        this.fechaFin = LocalDate.now();
    }

    public void cancelar() {
        this.estado = EstadoAsignacion.CANCELADO;
        this.fechaFin = LocalDate.now();
    }

    public boolean estaActivo() {
        return this.estado == EstadoAsignacion.ACTIVO;
    }
}
