package com.nutritrack.nutritrackapi.repository;

import com.nutritrack.nutritrackapi.model.PerfilUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PerfilUsuarioRepository extends JpaRepository<PerfilUsuario, UUID> {
    Optional<PerfilUsuario> findByCuenta_Id(UUID cuentaId);


}