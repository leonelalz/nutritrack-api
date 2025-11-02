package com.nutritrack.nutritrackapi.repository;

import com.nutritrack.nutritrackapi.model.UsuarioPerfilSalud;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface UsuarioPerfilSaludRepository extends JpaRepository<UsuarioPerfilSalud, UUID> {
    UsuarioPerfilSalud findByIdPerfil(UUID idPerfil);
}
