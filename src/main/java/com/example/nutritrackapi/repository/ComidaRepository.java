package com.example.nutritrackapi.repository;

import com.example.nutritrackapi.model.Comida;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Comida
 * US-09: Gestionar Comidas
 * US-10: Gestionar Recetas
 * 
 * MIGRACIÓN: TipoComida ahora es una entidad (TipoComidaEntity)
 */
@Repository
public interface ComidaRepository extends JpaRepository<Comida, Long> {

    /**
     * Busca comida por nombre
     */
    Optional<Comida> findByNombre(String nombre);

    /**
     * Busca comidas por ID de tipo de comida
     */
    Page<Comida> findByTipoComidaId(Long tipoComidaId, Pageable pageable);

    /**
     * Busca comidas por nombre de tipo de comida
     */
    @Query("SELECT c FROM Comida c WHERE UPPER(c.tipoComida.nombre) = UPPER(:nombre)")
    Page<Comida> findByTipoComidaNombre(@Param("nombre") String nombre, Pageable pageable);

    /**
     * Busca comidas por nombre que contenga (búsqueda parcial)
     */
    Page<Comida> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);

    /**
     * Verifica si nombre ya existe
     */
    boolean existsByNombre(String nombre);

    /**
     * Verifica si nombre existe excluyendo un ID (para actualización)
     */
    boolean existsByNombreAndIdNot(String nombre, Long id);

    /**
     * Busca comidas por etiqueta
     */
    @Query("""
        SELECT c FROM Comida c JOIN c.etiquetas e
        WHERE e.id = :etiquetaId
    """)
    Page<Comida> findByEtiquetaId(@Param("etiquetaId") Long etiquetaId, Pageable pageable);

    /**
     * Busca comidas que contengan un ingrediente específico
     */
    @Query("""
        SELECT DISTINCT c FROM Comida c JOIN c.comidaIngredientes ci
        WHERE ci.ingrediente.id = :ingredienteId
    """)
    List<Comida> findByIngredienteId(@Param("ingredienteId") Long ingredienteId);

    /**
     * Cuenta comidas por ID de tipo
     */
    long countByTipoComidaId(Long tipoComidaId);

    /**
     * Busca comidas sin ingredientes (para validación)
     */
    @Query("""
        SELECT c FROM Comida c
        WHERE c.comidaIngredientes IS EMPTY
    """)
    List<Comida> findComidasSinIngredientes();

    /**
     * Verifica si una comida tiene ingredientes
     */
    @Query("""
        SELECT CASE WHEN COUNT(ci) > 0 THEN true ELSE false END
        FROM ComidaIngrediente ci
        WHERE ci.comida.id = :comidaId
    """)
    boolean tieneIngredientes(@Param("comidaId") Long comidaId);
}
