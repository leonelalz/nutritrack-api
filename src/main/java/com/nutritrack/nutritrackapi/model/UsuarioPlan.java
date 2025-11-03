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
@Table(name = "usuarios_planes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_perfil_usuario", nullable = false)
    private PerfilUsuario perfilUsuario;

    @ManyToOne
    @JoinColumn(name = "id_plan", nullable = false)
    private Plan plan;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDate fechaFin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private EstadoAsignacion estado = EstadoAsignacion.ACTIVO;

    @Column(name = "dia_actual")
    @Builder.Default
    private Integer diaActual = 1; // Día del plan en el que está el usuario

    @Column(columnDefinition = "TEXT")
    private String notas;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // Helper methods
    public void avanzarDia() {
        if (this.diaActual < this.plan.getDuracionDias()) {
            this.diaActual++;
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
