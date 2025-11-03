package com.nutritrack.nutritrackapi.repository;

import com.nutritrack.nutritrackapi.model.Receta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecetaRepository extends JpaRepository<Receta, Receta.RecetaId> {
    
    List<Receta> findByIdComida(Long idComida);
    
    List<Receta> findByIdIngrediente(Long idIngrediente);
    
    void deleteByIdComida(Long idComida);
    
    void deleteByIdComidaAndIdIngrediente(Long idComida, Long idIngrediente);
}
