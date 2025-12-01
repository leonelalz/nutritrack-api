package com.example.nutritrackapi.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entidad que representa las comidas del catálogo.
 * Una comida está compuesta por varios ingredientes (relación con ComidaIngrediente).
 * 
 * US-09: Gestionar Comidas
 * US-10: Gestionar Recetas (agregar ingredientes a comida)
 * 
 * MIGRACIÓN: tipoComida ahora es una relación @ManyToOne a TipoComidaEntity
 * para permitir tipos de comida dinámicos.
 */
@Entity
@Table(name = "comidas")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String nombre;

    /**
     * Tipo de comida - Relación con tabla maestra tipos_comida
     * Permite gestionar tipos de comida dinámicamente
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tipo_comida", nullable = false)
    private TipoComidaEntity tipoComida;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "tiempo_preparacion_minutos")
    private Integer tiempoPreparacionMinutos;

    @Column(name = "porciones")
    private Integer porciones;

    @Column(columnDefinition = "TEXT")
    private String instrucciones;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Relación 1-to-many con ComidaIngrediente (Receta)
     * Mínimo 1 ingrediente requerido (validación en servicio)
     */
    @OneToMany(mappedBy = "comida", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<ComidaIngrediente> comidaIngredientes = new HashSet<>();

    /**
     * Relación many-to-many con Etiqueta
     */
    @ManyToMany
    @JoinTable(
        name = "comida_etiquetas",
        joinColumns = @JoinColumn(name = "id_comida"),
        inverseJoinColumns = @JoinColumn(name = "id_etiqueta")
    )
    @Builder.Default
    private Set<Etiqueta> etiquetas = new HashSet<>();

    @Override
    public String toString() {
        return "Comida{id=" + id + ", nombre='" + nombre + "', tipoComida=" + (tipoComida != null ? tipoComida.getNombre() : "null") + "}";
    }
}
