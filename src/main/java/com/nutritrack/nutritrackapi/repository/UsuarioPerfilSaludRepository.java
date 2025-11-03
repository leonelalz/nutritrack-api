package com.nutritrack.nutritrackapi.repository;

import com.nutritrack.nutritrackapi.model.UsuarioPerfilSalud;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioPerfilSaludRepository extends JpaRepository<UsuarioPerfilSalud, Long> {
    UsuarioPerfilSalud findByIdPerfil(Long idPerfil);
}
