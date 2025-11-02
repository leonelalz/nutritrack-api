package com.nutritrack.nutritrackapi.config;

import com.nutritrack.nutritrackapi.model.PerfilUsuario;
import com.nutritrack.nutritrackapi.model.Rol;
import com.nutritrack.nutritrackapi.model.CuentaAuth;
import com.nutritrack.nutritrackapi.model.enums.TipoRol;
import com.nutritrack.nutritrackapi.repository.PerfilUsuarioRepository;
import com.nutritrack.nutritrackapi.repository.RolRepository;
import com.nutritrack.nutritrackapi.repository.CuentaAuthRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final RolRepository roleRepository;
    private final CuentaAuthRepository userRepository;
    private final PerfilUsuarioRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        log.info("Initializing database with default data...");

        // Crear roles
        Rol userRole = createRoleIfNotExists(TipoRol.ROLE_USER);
        Rol adminRole = createRoleIfNotExists(TipoRol.ROLE_ADMIN);

        // Crear CuentaAuth admin
        createAdminUserIfNotExists(adminRole);

        log.info("Database initialization completed.");
    }

    private Rol createRoleIfNotExists(TipoRol roleType) {
        return roleRepository.findByTipo(roleType)
                .orElseGet(() -> {
                    Rol role = new Rol(roleType);
                    roleRepository.save(role);
                    log.info("Created role: {}", roleType);
                    return role;
                });
    }

    private void createAdminUserIfNotExists(Rol adminRole) {
        String adminEmail = "admin@fintech.com";

        if (userRepository.existsByEmail(adminEmail)) {
            log.info("Admin user already exists: {}", adminEmail);
            return;
        }

        // Create CuentaAuth first
        CuentaAuth adminUser = new CuentaAuth();
        adminUser.setEmail(adminEmail);
        adminUser.setPassword(passwordEncoder.encode("admin123"));
        adminUser.setRol(adminRole);
        adminUser.setActive(true);
        CuentaAuth savedAdmin = userRepository.save(adminUser);

        // Create PerfilUsuario linked to the CuentaAuth
        PerfilUsuario adminCustomer = new PerfilUsuario();
        adminCustomer.setCuenta(savedAdmin);
        adminCustomer.setNombre("System Administrator");
        customerRepository.save(adminCustomer);

        log.info("========================================");
        log.info("DEFAULT ADMIN USER CREATED:");
        log.info("Email: {}", adminEmail);
        log.info("Password: admin123");
        log.info("⚠️  CHANGE THIS PASSWORD IN PRODUCTION!");
        log.info("========================================");
    }
}