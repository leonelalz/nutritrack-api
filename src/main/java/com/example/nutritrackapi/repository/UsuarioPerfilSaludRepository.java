package com.example.nutritrackapi.repository;

import com.example.nutritrackapi.model.UsuarioPerfilSalud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioPerfilSaludRepository extends JpaRepository<UsuarioPerfilSalud, Long> {

    /**
     * Buscar perfil de salud por ID (que es el mismo que el ID del perfil de usuario)
     * Relación 1-to-1
     */
    default Optional<UsuarioPerfilSalud> findByPerfilUsuarioId(Long perfilUsuarioId) {
        return findById(perfilUsuarioId);
    }

    /**
     * Verificar si existe perfil de salud para un usuario
     */
    default boolean existsByPerfilUsuarioId(Long perfilUsuarioId) {
        return existsById(perfilUsuarioId);
    }

    /**
     * Eliminar perfil de salud por ID del perfil de usuario
     */
    default void deleteByPerfilUsuarioId(Long perfilUsuarioId) {
        deleteById(perfilUsuarioId);
    }
}
