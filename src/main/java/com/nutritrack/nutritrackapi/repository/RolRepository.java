package com.nutritrack.nutritrackapi.repository;

import com.nutritrack.nutritrackapi.model.Rol;
import com.nutritrack.nutritrackapi.model.enums.TipoRol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface RolRepository extends JpaRepository<Rol, Long> {
    Optional<Rol> findByTipo(TipoRol tipo);

}
