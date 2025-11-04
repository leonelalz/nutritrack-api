package com.example.nutritrackapi.service;

import com.example.nutritrackapi.model.CuentaAuth;
import com.example.nutritrackapi.model.PerfilUsuario;
import com.example.nutritrackapi.model.Role;
import com.example.nutritrackapi.repository.CuentaAuthRepository;
import com.example.nutritrackapi.repository.PerfilUsuarioRepository;
import com.example.nutritrackapi.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class StartupService implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final CuentaAuthRepository cuentaAuthRepository;
    private final PerfilUsuarioRepository perfilUsuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("🚀 Iniciando NutriTrack API...");
        initializeRoles();
        initializeAdminUser();
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

    private void initializeAdminUser() {
        String adminEmail = "admin@nutritrack.com";
        
        if (cuentaAuthRepository.findByEmail(adminEmail).isEmpty()) {
            log.info("👤 Creando usuario administrador inicial...");
            
            // Buscar el rol ADMIN
            Role adminRole = roleRepository.findByTipoRol(Role.TipoRol.ROLE_ADMIN)
                    .orElseThrow(() -> new RuntimeException("Rol ROLE_ADMIN no encontrado"));
            
            // Crear cuenta de autenticación
            CuentaAuth cuentaAuth = CuentaAuth.builder()
                    .email(adminEmail)
                    .password(passwordEncoder.encode("Admin123!"))
                    .active(true)
                    .createdAt(LocalDate.now())
                    .role(adminRole)
                    .build();
            
            cuentaAuth = cuentaAuthRepository.save(cuentaAuth);
            
            // Crear perfil de usuario
            PerfilUsuario perfil = PerfilUsuario.builder()
                    .nombre("Administrador")
                    .apellido("Sistema")
                    .cuenta(cuentaAuth)
                    .unidadesMedida(PerfilUsuario.UnidadesMedida.KG)
                    .fechaInicioApp(LocalDate.now())
                    .build();
            
            perfilUsuarioRepository.save(perfil);
            
            log.info("✅ Usuario administrador creado:");
            log.info("   📧 Email: {}", adminEmail);
            log.info("   🔑 Password: Admin123!");
            log.info("   ⚠️  IMPORTANTE: Cambia esta contraseña en producción");
        } else {
            log.info("ℹ️ Usuario administrador ya existe");
        }
    }
}
