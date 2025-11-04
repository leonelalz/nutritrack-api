package com.example.nutritrackapi.repository;

import com.example.nutritrackapi.model.CuentaAuth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CuentaAuthRepository extends JpaRepository<CuentaAuth, Long> {
    
    Optional<CuentaAuth> findByEmail(String email);
    
    boolean existsByEmail(String email);
}
