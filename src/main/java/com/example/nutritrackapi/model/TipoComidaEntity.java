package com.example.nutritrackapi.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Entidad TipoComida - Tabla maestra de tipos de comida
 * Permite gestionar dinámicamente los tipos de comida del sistema:
 * - DESAYUNO, ALMUERZO, CENA, SNACK, MERIENDA, etc.
 * 
 * Ventajas:
 * - El admin puede agregar/modificar/eliminar tipos
 * - No hay constraints fijas en la base de datos
 * - Flexible para diferentes culturas alimentarias
 */
@Entity
@Table(name = "tipos_comida")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TipoComidaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre del tipo de comida (único)
     * Ej: DESAYUNO, ALMUERZO, CENA, SNACK, MERIENDA
     */
    @Column(nullable = false, unique = true, length = 50)
    private String nombre;

    /**
     * Descripción opcional del tipo de comida
     */
    @Column(length = 255)
    private String descripcion;

    /**
     * Orden de visualización (para ordenar en UI)
     * Ej: DESAYUNO=1, ALMUERZO=2, CENA=3, SNACK=4
     */
    @Column(name = "orden_visualizacion")
    @Builder.Default
    private Integer ordenVisualizacion = 0;

    /**
     * Si está activo o no (soft delete)
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

    @Override
    public String toString() {
        return "TipoComidaEntity{id=" + id + ", nombre='" + nombre + "'}";
    }
}
