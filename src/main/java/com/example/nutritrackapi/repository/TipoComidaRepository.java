package com.example.nutritrackapi.repository;

import com.example.nutritrackapi.model.TipoComidaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository para TipoComidaEntity
 * Gestión de tipos de comida dinámicos
 */
@Repository
public interface TipoComidaRepository extends JpaRepository<TipoComidaEntity, Long> {

    /**
     * Buscar tipo de comida por nombre exacto (case-insensitive)
     */
    Optional<TipoComidaEntity> findByNombreIgnoreCase(String nombre);

    /**
     * Verificar si existe un tipo de comida con ese nombre
     */
    boolean existsByNombreIgnoreCase(String nombre);

    /**
     * Obtener todos los tipos de comida activos ordenados por visualización
     */
    @Query("SELECT t FROM TipoComidaEntity t WHERE t.activo = true ORDER BY t.ordenVisualizacion ASC, t.nombre ASC")
    List<TipoComidaEntity> findAllActiveOrdered();

    /**
     * Obtener todos ordenados por visualización
     */
    List<TipoComidaEntity> findAllByOrderByOrdenVisualizacionAscNombreAsc();

    /**
     * Buscar por nombre que contenga texto (para búsquedas)
     */
    List<TipoComidaEntity> findByNombreContainingIgnoreCase(String nombre);
}
