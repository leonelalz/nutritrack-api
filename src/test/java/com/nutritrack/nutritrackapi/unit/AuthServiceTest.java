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
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
        return CuentaAuth.builder()
                .id(1L)
                .email(email)
                .password(password)
                .rol(rolUsuario)
                .active(true)
                .build();
    }

    private PerfilUsuario crearPerfilMock(CuentaAuth cuenta, String nombre) {
        PerfilUsuario perfil = new PerfilUsuario();
        perfil.setId(1L);
        perfil.setCuenta(cuenta);
        perfil.setNombre(nombre);
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

    @Test
    @DisplayName("Login debe lanzar excepción si cuenta no existe después de autenticar")
    void login_CuentaNoExiste_ThrowsException() {
        // Arrange
        LoginRequestDTO request = new LoginRequestDTO("user@example.com", "pass123");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(cuentaAuthRepository.findByEmail("user@example.com")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(org.springframework.security.core.userdetails.UsernameNotFoundException.class)
                .hasMessageContaining("User not found");

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(cuentaAuthRepository).findByEmail("user@example.com");
    }

    @Test
    @DisplayName("Login debe lanzar excepción si perfil no existe")
    void login_PerfilNoExiste_ThrowsException() {
        // Arrange
        LoginRequestDTO request = new LoginRequestDTO("user@example.com", "pass123");
        CuentaAuth cuenta = crearCuentaMock("user@example.com", "encodedPassword");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(cuentaAuthRepository.findByEmail("user@example.com")).thenReturn(Optional.of(cuenta));
        when(perfilUsuarioRepository.findByCuenta_Id(cuenta.getId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Customer not found");

        verify(perfilUsuarioRepository).findByCuenta_Id(cuenta.getId());
    }

    // ==================== TEST: CASOS EDGE ====================

    @Test
    @DisplayName("Debe manejar emails con diferentes formatos válidos")
    void register_EmailFormatsValidos_Success() {
        // Arrange
        String[] emailsValidos = {
            "user@domain.com",
            "user.name@domain.com",
            "user+tag@domain.co.uk",
            "user123@sub.domain.com"
        };

        for (String email : emailsValidos) {
            RegistroRequestDTO request = new RegistroRequestDTO(email, "pass123", "Test User");

            when(cuentaAuthRepository.existsByEmail(email)).thenReturn(false);
            when(rolRepository.findByTipo(TipoRol.ROLE_USER)).thenReturn(Optional.of(rolUsuario));
            when(passwordEncoder.encode(any())).thenReturn("encodedPassword");

            CuentaAuth cuenta = crearCuentaMock(email, "encodedPassword");
            when(cuentaAuthRepository.save(any(CuentaAuth.class))).thenReturn(cuenta);

            PerfilUsuario perfil = crearPerfilMock(cuenta, "Test User");
            when(perfilUsuarioRepository.save(any(PerfilUsuario.class))).thenReturn(perfil);

            when(jwtUtil.generateToken(eq(email), any(), any())).thenReturn("fake-jwt-token");

            // Act
            AuthResponse response = authService.register(request);

            // Assert
            assertThat(response.email()).isEqualTo(email);
        }

        verify(cuentaAuthRepository, times(emailsValidos.length)).save(any(CuentaAuth.class));
    }

    @Test
    @DisplayName("Debe manejar nombres con caracteres especiales")
    void register_NombresEspeciales_Success() {
        // Arrange
        String[] nombresEspeciales = {
            "José García",
            "María José Pérez",
            "O'Connor",
            "Jean-Pierre Dubois"
        };

        for (String nombre : nombresEspeciales) {
            RegistroRequestDTO request = new RegistroRequestDTO("user@test.com", "pass123", nombre);

            when(cuentaAuthRepository.existsByEmail(any())).thenReturn(false);
            when(rolRepository.findByTipo(TipoRol.ROLE_USER)).thenReturn(Optional.of(rolUsuario));
            when(passwordEncoder.encode(any())).thenReturn("encodedPassword");

            CuentaAuth cuenta = crearCuentaMock("user@test.com", "encodedPassword");
            when(cuentaAuthRepository.save(any(CuentaAuth.class))).thenReturn(cuenta);

            PerfilUsuario perfil = crearPerfilMock(cuenta, nombre);
            when(perfilUsuarioRepository.save(any(PerfilUsuario.class))).thenReturn(perfil);

            when(jwtUtil.generateToken(any(), eq(nombre), any())).thenReturn("fake-jwt-token");

            // Act
            AuthResponse response = authService.register(request);

            // Assert
            assertThat(response.name()).isEqualTo(nombre);
        }
    }

    @Test
    @DisplayName("Debe crear cuenta con rol USER por defecto")
    void register_VerificaRolUsuario_Success() {
        // Arrange
        RegistroRequestDTO request = new RegistroRequestDTO("user@example.com", "pass123", "Test User");

        when(cuentaAuthRepository.existsByEmail(request.email())).thenReturn(false);
        when(rolRepository.findByTipo(TipoRol.ROLE_USER)).thenReturn(Optional.of(rolUsuario));
        when(passwordEncoder.encode(request.password())).thenReturn("encodedPassword");

        ArgumentCaptor<CuentaAuth> cuentaCaptor = ArgumentCaptor.forClass(CuentaAuth.class);
        CuentaAuth cuentaGuardada = crearCuentaMock("user@example.com", "encodedPassword");
        when(cuentaAuthRepository.save(cuentaCaptor.capture())).thenReturn(cuentaGuardada);

        PerfilUsuario perfil = crearPerfilMock(cuentaGuardada, "Test User");
        when(perfilUsuarioRepository.save(any(PerfilUsuario.class))).thenReturn(perfil);
        when(jwtUtil.generateToken(any(), any(), any())).thenReturn("fake-jwt-token");

        // Act
        authService.register(request);

        // Assert
        CuentaAuth cuentaCreada = cuentaCaptor.getValue();
        assertThat(cuentaCreada.getRol().getTipo()).isEqualTo(TipoRol.ROLE_USER);
        assertThat(cuentaCreada.isActive()).isTrue();

        verify(rolRepository).findByTipo(TipoRol.ROLE_USER);
    }

    @Test
    @DisplayName("Debe encriptar password antes de guardar")
    void register_EncriptaPassword_Success() {
        // Arrange
        String rawPassword = "MySecurePass123!";
        String encodedPassword = "encodedSecurePassword";
        RegistroRequestDTO request = new RegistroRequestDTO("user@example.com", rawPassword, "Test User");

        when(cuentaAuthRepository.existsByEmail(request.email())).thenReturn(false);
        when(rolRepository.findByTipo(TipoRol.ROLE_USER)).thenReturn(Optional.of(rolUsuario));
        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);

        ArgumentCaptor<CuentaAuth> cuentaCaptor = ArgumentCaptor.forClass(CuentaAuth.class);
        CuentaAuth cuentaGuardada = crearCuentaMock("user@example.com", encodedPassword);
        when(cuentaAuthRepository.save(cuentaCaptor.capture())).thenReturn(cuentaGuardada);

        PerfilUsuario perfil = crearPerfilMock(cuentaGuardada, "Test User");
        when(perfilUsuarioRepository.save(any(PerfilUsuario.class))).thenReturn(perfil);
        when(jwtUtil.generateToken(any(), any(), any())).thenReturn("fake-jwt-token");

        // Act
        authService.register(request);

        // Assert
        CuentaAuth cuentaCreada = cuentaCaptor.getValue();
        assertThat(cuentaCreada.getPassword()).isEqualTo(encodedPassword);
        assertThat(cuentaCreada.getPassword()).isNotEqualTo(rawPassword);

        verify(passwordEncoder).encode(rawPassword);
    }

    @Test
    @DisplayName("Debe crear perfil vinculado a la cuenta")
    void register_CreaPerfilVinculado_Success() {
        // Arrange
        RegistroRequestDTO request = new RegistroRequestDTO("user@example.com", "pass123", "John Doe");

        when(cuentaAuthRepository.existsByEmail(request.email())).thenReturn(false);
        when(rolRepository.findByTipo(TipoRol.ROLE_USER)).thenReturn(Optional.of(rolUsuario));
        when(passwordEncoder.encode(request.password())).thenReturn("encodedPassword");

        CuentaAuth cuentaGuardada = crearCuentaMock("user@example.com", "encodedPassword");
        when(cuentaAuthRepository.save(any(CuentaAuth.class))).thenReturn(cuentaGuardada);

        ArgumentCaptor<PerfilUsuario> perfilCaptor = ArgumentCaptor.forClass(PerfilUsuario.class);
        PerfilUsuario perfilGuardado = crearPerfilMock(cuentaGuardada, "John Doe");
        when(perfilUsuarioRepository.save(perfilCaptor.capture())).thenReturn(perfilGuardado);
        when(jwtUtil.generateToken(any(), any(), any())).thenReturn("fake-jwt-token");

        // Act
        authService.register(request);

        // Assert
        PerfilUsuario perfilCreado = perfilCaptor.getValue();
        assertThat(perfilCreado.getNombre()).isEqualTo("John Doe");
        assertThat(perfilCreado.getCuenta()).isEqualTo(cuentaGuardada);

        verify(perfilUsuarioRepository).save(any(PerfilUsuario.class));
    }
}
