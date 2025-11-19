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
 * Entidad Rutina - Rutinas de ejercicio con duración fija
 * Una rutina es un PROGRAMA DE EJERCICIO COMPLETO
 * 
 * US-11: Crear Meta del Catálogo (Rutina)
 * US-12: Gestionar Meta (configurar ejercicios)
 * US-13: Ver Catálogo de Metas (Admin)
 * US-14: Eliminar Meta
 * US-15: Ensamblar Rutinas
 * RN11: Nombres únicos
 * RN14: No eliminar si tiene usuarios activos
 * RN28: Soft delete (marcar como inactivo)
 */
@Entity
@Table(name = "rutinas")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rutina {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * RN11: Nombre único de la rutina
     */
    @Column(nullable = false, unique = true, length = 255)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    /**
     * Duración de la rutina en semanas (ej: 8 semanas, 12 semanas)
     */
    @Column(name = "duracion_semanas", nullable = false)
    private Integer duracionSemanas;

    /**
     * Patrón de semanas base que se repite cíclicamente
     * Ej: patronSemanas=2 significa que las primeras 2 semanas se repiten hasta completar duracionSemanas
     * Si duracionSemanas=12 y patronSemanas=2, el patrón se repite 6 veces
     */
    @Column(name = "patron_semanas", nullable = false)
    @Builder.Default
    private Integer patronSemanas = 1;

    /**
     * Nivel de dificultad de la rutina completa
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "nivel_dificultad", length = 50)
    private Ejercicio.NivelDificultad nivelDificultad;

    /**
     * RN28: Soft delete - si está activo o no
     * false = rutina eliminada lógicamente
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = true;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Relación 1-to-many con RutinaEjercicio
     * Lista de ejercicios que componen la rutina
     */
    @OneToMany(mappedBy = "rutina", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<RutinaEjercicio> ejercicios = new HashSet<>();

    /**
     * Relación many-to-many con Etiqueta
     * Para búsqueda y filtrado
     */
    @ManyToMany
    @JoinTable(
        name = "rutina_etiquetas",
        joinColumns = @JoinColumn(name = "id_rutina"),
        inverseJoinColumns = @JoinColumn(name = "id_etiqueta")
    )
    @Builder.Default
    private Set<Etiqueta> etiquetas = new HashSet<>();

    /**
     * Helper method para agregar etiqueta
     */
    public void agregarEtiqueta(Etiqueta etiqueta) {
        this.etiquetas.add(etiqueta);
    }

    /**
     * Helper method para remover etiqueta
     */
    public void removerEtiqueta(Etiqueta etiqueta) {
        this.etiquetas.remove(etiqueta);
    }

    /**
     * Helper method para agregar ejercicio
     */
    public void agregarEjercicio(RutinaEjercicio ejercicio) {
        this.ejercicios.add(ejercicio);
        ejercicio.setRutina(this);
    }

    /**
     * Helper method para remover ejercicio
     */
    public void removerEjercicio(RutinaEjercicio ejercicio) {
        this.ejercicios.remove(ejercicio);
        ejercicio.setRutina(null);
    }

    @Override
    public String toString() {
        return "Rutina{id=" + id + ", nombre='" + nombre + "', duracionSemanas=" + duracionSemanas + ", activo=" + activo + "}";
    }
}
