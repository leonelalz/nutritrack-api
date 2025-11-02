package com.nutritrack.nutritrackapi.unit;

import com.nutritrack.nutritrackapi.dto.request.LoginRequestDTO;
import com.nutritrack.nutritrackapi.dto.request.RegistroRequestDTO;
import com.nutritrack.nutritrackapi.dto.response.AuthResponse;
import com.nutritrack.nutritrackapi.exception.BusinessRuleException;
import com.nutritrack.nutritrackapi.model.*;
import com.nutritrack.nutritrackapi.model.enums.TipoRol;
import com.nutritrack.nutritrackapi.repository.*;
import com.nutritrack.nutritrackapi.security.JwtUtil;
import com.nutritrack.nutritrackapi.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService - Pruebas Unitarias")
class AuthServiceTest {

    @Mock
    private CuentaAuthRepository cuentaAuthRepository;

    @Mock
    private PerfilUsuarioRepository perfilUsuarioRepository;

    @Mock
    private RolRepository rolRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthService authService;

    private Rol rolUsuario;

    @BeforeEach
    void setUp() {
        rolUsuario = new Rol();
        rolUsuario.setId(1L);
        rolUsuario.setTipo(TipoRol.ROLE_USER);
    }

    private CuentaAuth crearCuentaMock(String email, String password) {
        CuentaAuth cuenta = new CuentaAuth();
        cuenta.setId(UUID.randomUUID());
        cuenta.setEmail(email);
        cuenta.setPassword(password);
        cuenta.setRol(rolUsuario);
        cuenta.setActive(true);
        return cuenta;
    }

    private PerfilUsuario crearPerfilMock(CuentaAuth cuenta, String nombre) {
        PerfilUsuario perfil = new PerfilUsuario();
        perfil.setId(UUID.randomUUID());
        perfil.setCuenta(cuenta);
        perfil.setName(nombre);
        return perfil;
    }

    // ==================== TEST: REGISTRO ====================
    @Test
    @DisplayName("Debe registrar cuenta y perfil exitosamente")
    void register_ValidData_Success() {
        // Arrange
        RegistroRequestDTO request = new RegistroRequestDTO("user@example.com", "pass123", "John Doe");

        when(cuentaAuthRepository.existsByEmail(request.email())).thenReturn(false);
        when(rolRepository.findByTipo(TipoRol.ROLE_USER)).thenReturn(Optional.of(rolUsuario));
        when(passwordEncoder.encode(request.password())).thenReturn("encodedPassword");

        CuentaAuth cuentaGuardada = crearCuentaMock("user@example.com", "encodedPassword");
        when(cuentaAuthRepository.save(any(CuentaAuth.class))).thenReturn(cuentaGuardada);

        PerfilUsuario perfilGuardado = crearPerfilMock(cuentaGuardada, "John Doe");
        when(perfilUsuarioRepository.save(any(PerfilUsuario.class))).thenReturn(perfilGuardado);

        when(jwtUtil.generateToken("user@example.com", "John Doe", perfilGuardado.getId()))
                .thenReturn("fake-jwt-token");

        // Act
        AuthResponse response = authService.register(request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.token()).isEqualTo("fake-jwt-token");
        assertThat(response.email()).isEqualTo("user@example.com");
        assertThat(response.name()).isEqualTo("John Doe");

        verify(cuentaAuthRepository).existsByEmail("user@example.com");
        verify(cuentaAuthRepository).save(any(CuentaAuth.class));
        verify(perfilUsuarioRepository).save(any(PerfilUsuario.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si el email ya existe")
    void register_DuplicateEmail_ThrowsException() {
        // Arrange
        RegistroRequestDTO request = new RegistroRequestDTO("user@example.com", "pass123", "John Doe");
        when(cuentaAuthRepository.existsByEmail(request.email())).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Email already registered");

        verify(cuentaAuthRepository, never()).save(any(CuentaAuth.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si el rol ROLE_USER no existe")
    void register_RoleNotFound_ThrowsException() {
        // Arrange
        RegistroRequestDTO request = new RegistroRequestDTO("user@example.com", "pass123", "John Doe");
        when(cuentaAuthRepository.existsByEmail(request.email())).thenReturn(false);
        when(rolRepository.findByTipo(TipoRol.ROLE_USER)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Role not found");
    }

    // ==================== TEST: LOGIN ====================
    @Test
    @DisplayName("Debe hacer login exitosamente")
    void login_ValidCredentials_Success() {
        // Arrange
        LoginRequestDTO request = new LoginRequestDTO("user@example.com", "pass123");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        CuentaAuth cuenta = crearCuentaMock("user@example.com", "encodedPassword");
        when(cuentaAuthRepository.findByEmail("user@example.com")).thenReturn(Optional.of(cuenta));

        PerfilUsuario perfil = crearPerfilMock(cuenta, "John Doe");
        when(perfilUsuarioRepository.findByCuenta_Id(cuenta.getId())).thenReturn(Optional.of(perfil));

        when(jwtUtil.generateToken("user@example.com", "John Doe", perfil.getId())).thenReturn("fake-jwt-token");

        // Act
        AuthResponse response = authService.login(request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.token()).isEqualTo("fake-jwt-token");
        assertThat(response.email()).isEqualTo("user@example.com");
        assertThat(response.name()).isEqualTo("John Doe");

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }
}
