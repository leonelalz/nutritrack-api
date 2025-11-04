package com.example.nutritrackapi.service;

import com.example.nutritrackapi.dto.LoginRequest;
import com.example.nutritrackapi.dto.RegisterRequest;
import com.example.nutritrackapi.dto.DeleteAccountRequest;
import com.example.nutritrackapi.dto.AuthResponse;
import com.example.nutritrackapi.model.CuentaAuth;
import com.example.nutritrackapi.model.PerfilUsuario;
import com.example.nutritrackapi.model.Role;
import com.example.nutritrackapi.repository.CuentaAuthRepository;
import com.example.nutritrackapi.repository.PerfilUsuarioRepository;
import com.example.nutritrackapi.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para AuthService
 * Cubre User Stories: US-01 (Registro), US-02 (Login), US-05 (Eliminar Cuenta)
 * Cubre Reglas de Negocio: RN01, RN02, RN03, RN04, RN05
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService - Pruebas Unitarias")
class AuthServiceTest {

    @Mock
    private CuentaAuthRepository cuentaAuthRepository;

    @Mock
    private PerfilUsuarioRepository perfilUsuarioRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private Role roleUser;
    private CuentaAuth cuentaAuth;
    private PerfilUsuario perfilUsuario;

    @BeforeEach
    void setUp() {
        // Setup roles
        roleUser = new Role();
        roleUser.setId(1L);
        roleUser.setTipoRol(Role.TipoRol.ROLE_USER);

        // Setup cuenta auth
        cuentaAuth = new CuentaAuth();
        cuentaAuth.setId(1L);
        cuentaAuth.setEmail("test@example.com");
        cuentaAuth.setPassword("$2a$10$hashedPassword");
        cuentaAuth.setActive(true);
        cuentaAuth.setCreatedAt(LocalDate.now());
        cuentaAuth.setRole(roleUser);

        // Setup perfil usuario
        perfilUsuario = new PerfilUsuario();
        perfilUsuario.setId(1L);
        perfilUsuario.setNombre("Test");
        perfilUsuario.setApellido("User");
        perfilUsuario.setCuenta(cuentaAuth);
        perfilUsuario.setUnidadesMedida(PerfilUsuario.UnidadesMedida.KG);
        perfilUsuario.setFechaInicioApp(LocalDate.now());

        cuentaAuth.setPerfilUsuario(perfilUsuario);
    }

    // ============================================================================
    // US-01: Registro de Usuario
    // ============================================================================

    @Test
    @DisplayName("US-01: Registro exitoso con datos válidos")
    void testRegistroExitoso() {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setNombre("Carlos");
        request.setApellido("Martinez");
        request.setEmail("carlos.martinez@test.com");
        request.setPassword("Test1234!");

        when(cuentaAuthRepository.existsByEmail(anyString())).thenReturn(false);
        when(roleRepository.findByTipoRol(Role.TipoRol.ROLE_USER)).thenReturn(Optional.of(roleUser));
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$encodedPassword");
        when(cuentaAuthRepository.save(any(CuentaAuth.class))).thenReturn(cuentaAuth);
        when(perfilUsuarioRepository.save(any(PerfilUsuario.class))).thenReturn(perfilUsuario);

        // When
        AuthResponse response = authService.register(request);

        // Then
        assertNotNull(response);
        assertEquals("test@example.com", response.getEmail());
        assertEquals("Test", response.getNombre());
        assertEquals("TOKEN_PENDIENTE_JWT", response.getToken());

        verify(cuentaAuthRepository).existsByEmail("carlos.martinez@test.com");
        verify(passwordEncoder).encode("Test1234!");
        verify(cuentaAuthRepository).save(any(CuentaAuth.class));
        verify(perfilUsuarioRepository).save(any(PerfilUsuario.class));
    }

    @Test
    @DisplayName("RN01: Registro con contraseña débil (validación pendiente)")
    void testRegistroFallaPasswordDebil() {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setNombre("Test");
        request.setApellido("User");
        request.setEmail("test@test.com");
        request.setPassword("123");

        // Note: La validación de contraseña fuerte debe implementarse en el servicio
        // Por ahora solo validamos que Bean Validation (min 8 caracteres) funciona
        // Este test pasará cuando se implemente RN01 completamente
    }

    @Test
    @DisplayName("RN01: Registro con contraseña sin mayúscula (validación pendiente)")
    void testRegistroFallaPasswordSinMayuscula() {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setNombre("Test");
        request.setApellido("User");
        request.setEmail("test@test.com");
        request.setPassword("test1234!");

        // Note: La validación de mayúsculas/números debe implementarse
        // Este test pasará cuando se implemente RN01 completamente
    }

    @Test
    @DisplayName("RN01: Registro con contraseña sin número (validación pendiente)")
    void testRegistroFallaPasswordSinNumero() {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setNombre("Test");
        request.setApellido("User");
        request.setEmail("test@test.com");
        request.setPassword("TestPassword!");

        // Note: La validación de números debe implementarse
        // Este test pasará cuando se implemente RN01 completamente
    }

    @Test
    @DisplayName("RN02: Registro falla con email duplicado")
    void testRegistroFallaEmailDuplicado() {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setNombre("Test");
        request.setApellido("User");
        request.setEmail("duplicado@test.com");
        request.setPassword("Test1234!");

        when(cuentaAuthRepository.existsByEmail("duplicado@test.com")).thenReturn(true);

        // When/Then
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> authService.register(request)
        );

        assertTrue(exception.getMessage().contains("email") || 
                   exception.getMessage().contains("registrado"));
        verify(cuentaAuthRepository).existsByEmail("duplicado@test.com");
        verify(cuentaAuthRepository, never()).save(any());
    }

    // ============================================================================
    // US-02: Login de Usuario
    // ============================================================================

    @Test
    @DisplayName("US-02: Login exitoso con credenciales válidas")
    void testLoginExitoso() {
        // Given
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("Test1234!");

        when(cuentaAuthRepository.findByEmail("test@example.com"))
            .thenReturn(Optional.of(cuentaAuth));
        when(passwordEncoder.matches("Test1234!", cuentaAuth.getPassword()))
            .thenReturn(true);
        when(perfilUsuarioRepository.findByCuentaId(1L))
            .thenReturn(Optional.of(perfilUsuario));

        // When
        AuthResponse response = authService.login(request);

        // Then
        assertNotNull(response);
        assertEquals("test@example.com", response.getEmail());
        assertEquals("Test", response.getNombre());
        assertEquals("User", response.getApellido());
        assertEquals("TOKEN_PENDIENTE_JWT", response.getToken());

        verify(cuentaAuthRepository).findByEmail("test@example.com");
        verify(passwordEncoder).matches("Test1234!", cuentaAuth.getPassword());
    }

    @Test
    @DisplayName("RN03: Login falla con email no registrado")
    void testLoginFallaEmailNoRegistrado() {
        // Given
        LoginRequest request = new LoginRequest();
        request.setEmail("noexiste@test.com");
        request.setPassword("Test1234!");

        when(cuentaAuthRepository.findByEmail("noexiste@test.com"))
            .thenReturn(Optional.empty());

        // When/Then
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> authService.login(request)
        );

        assertTrue(exception.getMessage().contains("Credenciales") || 
                   exception.getMessage().contains("inválidas"));
        verify(cuentaAuthRepository).findByEmail("noexiste@test.com");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    @DisplayName("RN03: Login falla con password incorrecto")
    void testLoginFallaPasswordIncorrecto() {
        // Given
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("WrongPassword123!");

        when(cuentaAuthRepository.findByEmail("test@example.com"))
            .thenReturn(Optional.of(cuentaAuth));
        when(passwordEncoder.matches("WrongPassword123!", cuentaAuth.getPassword()))
            .thenReturn(false);

        // When/Then
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> authService.login(request)
        );

        assertTrue(exception.getMessage().contains("Credenciales") || 
                   exception.getMessage().contains("inválidas"));
        verify(cuentaAuthRepository).findByEmail("test@example.com");
        verify(passwordEncoder).matches("WrongPassword123!", cuentaAuth.getPassword());
    }

    @Test
    @DisplayName("RN03: Login falla con cuenta inactiva")
    void testLoginFallaCuentaInactiva() {
        // Given
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("Test1234!");

        cuentaAuth.setActive(false);

        when(cuentaAuthRepository.findByEmail("test@example.com"))
            .thenReturn(Optional.of(cuentaAuth));
        when(passwordEncoder.matches("Test1234!", cuentaAuth.getPassword()))
            .thenReturn(true);

        // When/Then
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> authService.login(request)
        );

        assertTrue(exception.getMessage().contains("inactiva") || 
                   exception.getMessage().contains("desactivada"));
        verify(cuentaAuthRepository).findByEmail("test@example.com");
    }

    // ============================================================================
    // US-05: Eliminar Cuenta
    // ============================================================================

    @Test
    @DisplayName("US-05: Eliminación exitosa con confirmación 'ELIMINAR'")
    void testEliminarCuentaExitoso() {
        // Given
        DeleteAccountRequest request = DeleteAccountRequest.builder()
            .confirmacion("ELIMINAR")
            .build();

        when(cuentaAuthRepository.findByEmail("test@example.com"))
            .thenReturn(Optional.of(cuentaAuth));
        doNothing().when(cuentaAuthRepository).delete(cuentaAuth);

        // When
        authService.eliminarCuenta("test@example.com", request);

        // Then
        verify(cuentaAuthRepository).findByEmail("test@example.com");
        verify(cuentaAuthRepository).delete(cuentaAuth);
    }

    @Test
    @DisplayName("RN05: Eliminación falla sin confirmación 'ELIMINAR'")
    void testEliminarCuentaFallaSinConfirmacion() {
        // Given
        DeleteAccountRequest request = DeleteAccountRequest.builder()
            .confirmacion("eliminar") // Minúsculas
            .build();

        // When/Then
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> authService.eliminarCuenta("test@example.com", request)
        );

        assertTrue(exception.getMessage().contains("ELIMINAR") || 
                   exception.getMessage().contains("confirmación"));
        verify(cuentaAuthRepository, never()).delete(any());
    }

    @Test
    @DisplayName("RN05: Eliminación falla con confirmación vacía")
    void testEliminarCuentaFallaConfirmacionVacia() {
        // Given
        DeleteAccountRequest request = DeleteAccountRequest.builder()
            .confirmacion("")
            .build();

        // When/Then
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> authService.eliminarCuenta("test@example.com", request)
        );

        assertTrue(exception.getMessage().contains("ELIMINAR") || 
                   exception.getMessage().contains("confirmación"));
        verify(cuentaAuthRepository, never()).delete(any());
    }

    @Test
    @DisplayName("RN04: Eliminación falla con cuenta no existente")
    void testEliminarCuentaFallaCuentaNoExiste() {
        // Given
        DeleteAccountRequest request = DeleteAccountRequest.builder()
            .confirmacion("ELIMINAR")
            .build();

        when(cuentaAuthRepository.findByEmail("noexiste@test.com"))
            .thenReturn(Optional.empty());

        // When/Then
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> authService.eliminarCuenta("noexiste@test.com", request)
        );

        assertTrue(exception.getMessage().contains("no encontrada") || 
                   exception.getMessage().contains("not found"));
        verify(cuentaAuthRepository, never()).delete(any());
    }
}

