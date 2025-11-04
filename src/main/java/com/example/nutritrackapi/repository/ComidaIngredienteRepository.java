package com.example.nutritrackapi.repository;

import com.example.nutritrackapi.model.ComidaIngrediente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad ComidaIngrediente (Receta)
 * US-10: Gestionar Recetas
 * RN10: Cantidad debe ser positiva
 */
@Repository
public interface ComidaIngredienteRepository extends JpaRepository<ComidaIngrediente, ComidaIngrediente.ComidaIngredienteId> {

    /**
     * Busca todos los ingredientes de una comida
     */
    List<ComidaIngrediente> findByComidaId(Long comidaId);

    /**
     * Busca todas las comidas que usan un ingrediente
     */
    List<ComidaIngrediente> findByIngredienteId(Long ingredienteId);

    /**
     * Busca un ingrediente específico en una comida
     */
    Optional<ComidaIngrediente> findByComidaIdAndIngredienteId(Long comidaId, Long ingredienteId);

    /**
     * Verifica si un ingrediente ya está en la comida
     */
    boolean existsByComidaIdAndIngredienteId(Long comidaId, Long ingredienteId);

    /**
     * Cuenta cuántos ingredientes tiene una comida
     */
    long countByComidaId(Long comidaId);

    /**
     * Elimina todos los ingredientes de una comida
     */
    @Modifying
    @Query("DELETE FROM ComidaIngrediente ci WHERE ci.comida.id = :comidaId")
    void deleteByComidaId(@Param("comidaId") Long comidaId);

    /**
     * Elimina un ingrediente específico de una comida
     */
    @Modifying
    @Query("DELETE FROM ComidaIngrediente ci WHERE ci.comida.id = :comidaId AND ci.ingrediente.id = :ingredienteId")
    void deleteByComidaIdAndIngredienteId(
        @Param("comidaId") Long comidaId,
        @Param("ingredienteId") Long ingredienteId
    );
}
