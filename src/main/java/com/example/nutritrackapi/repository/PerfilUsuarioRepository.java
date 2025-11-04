package com.example.nutritrackapi.repository;

import com.example.nutritrackapi.model.PerfilUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PerfilUsuarioRepository extends JpaRepository<PerfilUsuario, Long> {
    
    Optional<PerfilUsuario> findByCuentaId(Long cuentaId);
}
