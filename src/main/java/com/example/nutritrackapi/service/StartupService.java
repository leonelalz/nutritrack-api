package com.example.nutritrackapi.service;

import com.example.nutritrackapi.model.*;
import com.example.nutritrackapi.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class StartupService implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final CuentaAuthRepository cuentaAuthRepository;
    private final PerfilUsuarioRepository perfilUsuarioRepository;
    private final UsuarioPerfilSaludRepository usuarioPerfilSaludRepository;
    private final UsuarioHistorialMedidasRepository usuarioHistorialMedidasRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("🚀 Iniciando NutriTrack API...");
        initializeRoles();
        initializeAdminUser();
        initializeDemoUser();
        initializeDemoData();
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

    private void initializeDemoUser() {
        String demoEmail = "demo@nutritrack.com";
        
        if (cuentaAuthRepository.findByEmail(demoEmail).isEmpty()) {
            log.info("👤 Creando usuario demo para pruebas...");
            
            // Buscar el rol USER
            Role userRole = roleRepository.findByTipoRol(Role.TipoRol.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Rol ROLE_USER no encontrado"));
            
            // Crear cuenta de autenticación
            CuentaAuth cuentaAuth = CuentaAuth.builder()
                    .email(demoEmail)
                    .password(passwordEncoder.encode("Demo123!"))
                    .active(true)
                    .createdAt(LocalDate.now())
                    .role(userRole)
                    .build();
            
            cuentaAuth = cuentaAuthRepository.save(cuentaAuth);
            
            // Crear perfil de usuario
            PerfilUsuario perfil = PerfilUsuario.builder()
                    .nombre("Usuario")
                    .apellido("Demo")
                    .cuenta(cuentaAuth)
                    .unidadesMedida(PerfilUsuario.UnidadesMedida.KG)
                    .fechaInicioApp(LocalDate.now())
                    .build();
            
            perfilUsuarioRepository.save(perfil);
            
            log.info("✅ Usuario demo creado:");
            log.info("   📧 Email: {}", demoEmail);
            log.info("   🔑 Password: Demo123!");
        } else {
            log.info("ℹ️ Usuario demo ya existe");
        }
    }

    private void initializeDemoData() {
        // Verificar si ya existen datos de demostración
        if (usuarioPerfilSaludRepository.count() > 0) {
            log.info("ℹ️ Datos de demostración ya existen");
            return;
        }
        
        log.info("📊 Cargando datos de demostración...");
        
        // Obtener usuarios
        CuentaAuth adminCuenta = cuentaAuthRepository.findByEmail("admin@nutritrack.com")
                .orElseThrow(() -> new RuntimeException("Usuario admin no encontrado"));
        CuentaAuth demoCuenta = cuentaAuthRepository.findByEmail("demo@nutritrack.com")
                .orElseThrow(() -> new RuntimeException("Usuario demo no encontrado"));
        
        PerfilUsuario adminPerfil = perfilUsuarioRepository.findByCuentaId(adminCuenta.getId())
                .orElseThrow(() -> new RuntimeException("Perfil admin no encontrado"));
        PerfilUsuario demoPerfil = perfilUsuarioRepository.findByCuentaId(demoCuenta.getId())
                .orElseThrow(() -> new RuntimeException("Perfil demo no encontrado"));
        
        // Crear perfiles de salud
        createHealthProfile(adminPerfil, UsuarioPerfilSalud.ObjetivoSalud.MANTENER_FORMA, UsuarioPerfilSalud.NivelActividad.ALTO);
        createHealthProfile(demoPerfil, UsuarioPerfilSalud.ObjetivoSalud.PERDER_PESO, UsuarioPerfilSalud.NivelActividad.MODERADO);
        
        // Crear mediciones para admin (peso estable)
        createMeasurements(adminPerfil, new double[][]{
            {70.0, 175}, // 2025-09-01
            {70.2, 175}, // 2025-09-08
            {70.1, 175}, // 2025-09-15
            {70.3, 175}, // 2025-09-22
            {70.2, 175}, // 2025-09-29
            {70.4, 175}, // 2025-10-06
            {70.3, 175}, // 2025-10-13
            {70.5, 175}, // 2025-10-20
            {70.4, 175}, // 2025-10-27
            {70.5, 175}, // 2025-11-03
            {70.5, 175}  // 2025-11-04
        });
        
        // Crear mediciones para demo (pérdida de peso)
        createMeasurements(demoPerfil, new double[][]{
            {78.0, 168}, // 2025-09-01
            {77.5, 168}, // 2025-09-08
            {77.0, 168}, // 2025-09-15
            {76.5, 168}, // 2025-09-22
            {76.0, 168}, // 2025-09-29
            {75.5, 168}, // 2025-10-06
            {75.0, 168}, // 2025-10-13
            {74.5, 168}, // 2025-10-20
            {74.0, 168}, // 2025-10-27
            {73.0, 168}, // 2025-11-03
            {72.5, 168}  // 2025-11-04
        });
        
        log.info("✅ Datos de demostración cargados:");
        log.info("   👔 Admin: 11 mediciones (70.0→70.5 kg, MANTENER_FORMA)");
        log.info("   👤 Demo: 11 mediciones (78.0→72.5 kg, PERDER_PESO, -5.5 kg)");
    }

    private void createHealthProfile(PerfilUsuario perfil, UsuarioPerfilSalud.ObjetivoSalud objetivo, UsuarioPerfilSalud.NivelActividad actividad) {
        UsuarioPerfilSalud perfilSalud = UsuarioPerfilSalud.builder()
                .id(perfil.getId())
                .perfilUsuario(perfil)
                .objetivoActual(objetivo)
                .nivelActividadActual(actividad)
                .fechaActualizacion(LocalDate.now())
                .build();
        usuarioPerfilSaludRepository.save(perfilSalud);
    }

    private void createMeasurements(PerfilUsuario perfil, double[][] data) {
        LocalDate startDate = LocalDate.of(2025, 9, 1);
        
        for (int i = 0; i < data.length; i++) {
            LocalDate fecha = startDate.plusWeeks(i);
            if (i == 9) fecha = LocalDate.of(2025, 11, 3); // Penúltima medición
            if (i == 10) fecha = LocalDate.of(2025, 11, 4); // Última medición
            
            UsuarioHistorialMedidas medida = UsuarioHistorialMedidas.builder()
                    .perfilUsuario(perfil)
                    .fechaMedicion(fecha)
                    .peso(BigDecimal.valueOf(data[i][0]))
                    .altura(BigDecimal.valueOf(data[i][1]))
                    .build();
            usuarioHistorialMedidasRepository.save(medida);
        }
    }
}
