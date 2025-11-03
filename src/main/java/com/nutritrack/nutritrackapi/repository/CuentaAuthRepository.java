package com.nutritrack.nutritrackapi.repository;

import com.nutritrack.nutritrackapi.model.CuentaAuth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CuentaAuthRepository extends JpaRepository<CuentaAuth, Long> {

    // Buscar cuenta por email
    @Query("SELECT c FROM CuentaAuth c WHERE c.email = :email")
    Optional<CuentaAuth> findByEmail(@Param("email") String email);

    // Verificar si existe una cuenta con ese email
    @Query("SELECT COUNT(c) > 0 FROM CuentaAuth c WHERE c.email = :email")
    boolean existsByEmail(@Param("email") String email);
}