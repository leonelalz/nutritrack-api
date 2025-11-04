package com.example.nutritrackapi.service;

import com.example.nutritrackapi.model.Role;
import com.example.nutritrackapi.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class StartupService implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) {
        log.info("🚀 Iniciando NutriTrack API...");
        initializeRoles();
        log.info("✅ Aplicación lista!");
    }

    private void initializeRoles() {
        if (roleRepository.count() == 0) {
            log.info("📝 Creando roles por defecto...");
            
            Role userRole = Role.builder()
                    .tipoRol(Role.TipoRol.ROLE_USER)
                    .build();
            
            Role adminRole = Role.builder()
                    .tipoRol(Role.TipoRol.ROLE_ADMIN)
                    .build();
            
            roleRepository.save(userRole);
            roleRepository.save(adminRole);
            
            log.info("✅ Roles creados: ROLE_USER, ROLE_ADMIN");
        } else {
            log.info("ℹ️ Roles ya existen en la base de datos");
        }
    }
}
