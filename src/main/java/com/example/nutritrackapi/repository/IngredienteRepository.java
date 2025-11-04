package com.example.nutritrackapi.repository;

import com.example.nutritrackapi.model.Ingrediente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Ingrediente
 * US-07: Gestionar Ingredientes
 * RN07: Nombres únicos
 * RN09: No eliminar si está en uso en recetas
 */
@Repository
public interface IngredienteRepository extends JpaRepository<Ingrediente, Long> {

    /**
     * Busca ingrediente por nombre (unique constraint)
     */
    Optional<Ingrediente> findByNombre(String nombre);

    /**
     * Busca ingredientes por categoría
     */
    Page<Ingrediente> findByCategoriaAlimento(Ingrediente.CategoriaAlimento categoria, Pageable pageable);

    /**
     * Busca ingredientes por nombre que contenga (búsqueda parcial)
     */
    Page<Ingrediente> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);

    /**
     * Verifica si un ingrediente está en uso en recetas (RN09)
     */
    @Query("""
        SELECT CASE WHEN COUNT(ci) > 0 THEN true ELSE false END
        FROM ComidaIngrediente ci
        WHERE ci.ingrediente.id = :ingredienteId
    """)
    boolean estaEnUsoEnRecetas(@Param("ingredienteId") Long ingredienteId);

    /**
     * Verifica si nombre ya existe
     */
    boolean existsByNombre(String nombre);

    /**
     * Verifica si nombre existe excluyendo un ID (para actualización)
     */
    boolean existsByNombreAndIdNot(String nombre, Long id);

    /**
     * Busca ingredientes por etiqueta
     */
    @Query("""
        SELECT i FROM Ingrediente i JOIN i.etiquetas e
        WHERE e.id = :etiquetaId
    """)
    Page<Ingrediente> findByEtiquetaId(@Param("etiquetaId") Long etiquetaId, Pageable pageable);

    /**
     * Cuenta ingredientes por categoría
     */
    @Query("SELECT COUNT(i) FROM Ingrediente i WHERE i.categoriaAlimento = :categoria")
    long countByCategoria(@Param("categoria") Ingrediente.CategoriaAlimento categoria);
}
