package com.example.nutritrackapi.repository;

import com.example.nutritrackapi.model.Etiqueta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Etiqueta
 * US-06: Gestionar Etiquetas
 * RN06: Nombres únicos
 * RN08: No eliminar si está en uso
 */
@Repository
public interface EtiquetaRepository extends JpaRepository<Etiqueta, Long> {

    /**
     * Buscar etiqueta por nombre (nombre es único)
     */
    Optional<Etiqueta> findByNombre(String nombre);

    /**
     * Verificar si existe una etiqueta con el nombre dado
     */
    boolean existsByNombre(String nombre);

    /**
     * Verifica si nombre existe excluyendo un ID (para actualización)
     */
    boolean existsByNombreAndIdNot(String nombre, Long id);

    /**
     * Buscar etiquetas por tipo
     */
    List<Etiqueta> findByTipoEtiqueta(Etiqueta.TipoEtiqueta tipoEtiqueta);

    /**
     * Busca etiquetas por nombre que contenga (búsqueda parcial)
     */
    Page<Etiqueta> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);

    /**
     * Verificar si una etiqueta está en uso en alguna tabla relacionada
     * Usado para validar RN08: No eliminar etiquetas en uso
     */
    @Query("""
        SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END
        FROM Etiqueta e
        WHERE e.id = :etiquetaId
        AND EXISTS (
            SELECT 1 FROM UsuarioEtiquetasSalud ues WHERE ues.etiqueta.id = :etiquetaId
        )
        """)
    boolean isEtiquetaEnUso(Long etiquetaId);

    /**
     * Verifica si está en uso en ingredientes (RN08)
     */
    @Query("""
        SELECT CASE WHEN COUNT(ie) > 0 THEN true ELSE false END
        FROM Ingrediente i JOIN i.etiquetas ie
        WHERE ie.id = :etiquetaId
    """)
    boolean estaEnUsoEnIngredientes(@Param("etiquetaId") Long etiquetaId);

    /**
     * Verifica si está en uso en ejercicios (RN08)
     */
    @Query("""
        SELECT CASE WHEN COUNT(ee) > 0 THEN true ELSE false END
        FROM Ejercicio e JOIN e.etiquetas ee
        WHERE ee.id = :etiquetaId
    """)
    boolean estaEnUsoEnEjercicios(@Param("etiquetaId") Long etiquetaId);

    /**
     * Verifica si está en uso en comidas (RN08)
     */
    @Query("""
        SELECT CASE WHEN COUNT(ce) > 0 THEN true ELSE false END
        FROM Comida c JOIN c.etiquetas ce
        WHERE ce.id = :etiquetaId
    """)
    boolean estaEnUsoEnComidas(@Param("etiquetaId") Long etiquetaId);
}
