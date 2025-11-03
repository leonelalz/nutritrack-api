package com.nutritrack.nutritrackapi.unit;

import com.nutritrack.nutritrackapi.model.CuentaAuth;
import com.nutritrack.nutritrackapi.model.Rol;
import com.nutritrack.nutritrackapi.model.enums.TipoRol;
import com.nutritrack.nutritrackapi.repository.CuentaAuthRepository;
import com.nutritrack.nutritrackapi.service.CuentaAuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CuentaAuthService - Pruebas Unitarias")
class CuentaAuthServiceTest {

    @Mock
    private CuentaAuthRepository cuentaRepo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private CuentaAuthService cuentaService;

    private Long cuentaId;
    private CuentaAuth cuentaMock;
    private Rol rolUsuario;
    private Rol rolAdmin;

    @BeforeEach
    void setUp() {
        cuentaId = 1L;

        // Mock Roles
        rolUsuario = new Rol();
        rolUsuario.setId(1L);
        rolUsuario.setTipo(TipoRol.ROLE_USER);

        rolAdmin = new Rol();
        rolAdmin.setId(2L);
        rolAdmin.setTipo(TipoRol.ROLE_ADMIN);

        // Mock CuentaAuth
        cuentaMock = new CuentaAuth();
        cuentaMock.setId(cuentaId);
        cuentaMock.setEmail("test@nutritrack.com");
        cuentaMock.setPassword("encodedPassword123");
        cuentaMock.setRol(rolUsuario);
        cuentaMock.setActive(true);
    }

    // ==================== Cambiar Password ====================

    @Test
    @DisplayName("Debe cambiar password exitosamente")
    void cambiarPassword_PasswordValido_Success() {
        // Arrange
        String nuevaPassword = "NewSecurePass123!";
        String passwordEncoded = "encodedNewPassword";

        when(cuentaRepo.findById(cuentaId)).thenReturn(Optional.of(cuentaMock));
        when(passwordEncoder.encode(nuevaPassword)).thenReturn(passwordEncoded);
        when(cuentaRepo.save(any(CuentaAuth.class))).thenReturn(cuentaMock);

        // Act
        cuentaService.cambiarPassword(cuentaId, nuevaPassword);

        // Assert
        ArgumentCaptor<CuentaAuth> cuentaCaptor = ArgumentCaptor.forClass(CuentaAuth.class);
        verify(cuentaRepo).save(cuentaCaptor.capture());
        
        CuentaAuth cuentaGuardada = cuentaCaptor.getValue();
        assertThat(cuentaGuardada.getPassword()).isEqualTo(passwordEncoded);

        verify(cuentaRepo).findById(cuentaId);
        verify(passwordEncoder).encode(nuevaPassword);
    }

    @Test
    @DisplayName("Debe lanzar excepción si cuenta no existe al cambiar password")
    void cambiarPassword_CuentaNoExiste_ThrowsException() {
        // Arrange
        Long idInexistente = 999L;
        when(cuentaRepo.findById(idInexistente)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> cuentaService.cambiarPassword(idInexistente, "newPass"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Cuenta no encontrada");

        verify(cuentaRepo).findById(idInexistente);
        verify(passwordEncoder, never()).encode(any());
        verify(cuentaRepo, never()).save(any());
    }

    // ==================== Desactivar Cuenta ====================

    @Test
    @DisplayName("Debe desactivar cuenta exitosamente")
    void desactivarCuenta_CuentaActiva_Success() {
        // Arrange
        when(cuentaRepo.findById(cuentaId)).thenReturn(Optional.of(cuentaMock));
        when(cuentaRepo.save(any(CuentaAuth.class))).thenReturn(cuentaMock);

        // Act
        cuentaService.desactivarCuenta(cuentaId);

        // Assert
        ArgumentCaptor<CuentaAuth> cuentaCaptor = ArgumentCaptor.forClass(CuentaAuth.class);
        verify(cuentaRepo).save(cuentaCaptor.capture());
        
        CuentaAuth cuentaGuardada = cuentaCaptor.getValue();
        assertThat(cuentaGuardada.isActive()).isFalse();

        verify(cuentaRepo).findById(cuentaId);
    }

    @Test
    @DisplayName("Debe lanzar excepción si cuenta no existe al desactivar")
    void desactivarCuenta_CuentaNoExiste_ThrowsException() {
        // Arrange
        Long idInexistente = 999L;
        when(cuentaRepo.findById(idInexistente)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> cuentaService.desactivarCuenta(idInexistente))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Cuenta no encontrada");

        verify(cuentaRepo).findById(idInexistente);
        verify(cuentaRepo, never()).save(any());
    }

    // ==================== Reactivar Cuenta ====================

    @Test
    @DisplayName("Debe reactivar cuenta exitosamente")
    void reactivarCuenta_CuentaInactiva_Success() {
        // Arrange
        cuentaMock.setActive(false); // Cuenta desactivada
        when(cuentaRepo.findById(cuentaId)).thenReturn(Optional.of(cuentaMock));
        when(cuentaRepo.save(any(CuentaAuth.class))).thenReturn(cuentaMock);

        // Act
        cuentaService.reactivarCuenta(cuentaId);

        // Assert
        ArgumentCaptor<CuentaAuth> cuentaCaptor = ArgumentCaptor.forClass(CuentaAuth.class);
        verify(cuentaRepo).save(cuentaCaptor.capture());
        
        CuentaAuth cuentaGuardada = cuentaCaptor.getValue();
        assertThat(cuentaGuardada.isActive()).isTrue();

        verify(cuentaRepo).findById(cuentaId);
    }

    @Test
    @DisplayName("Debe lanzar excepción si cuenta no existe al reactivar")
    void reactivarCuenta_CuentaNoExiste_ThrowsException() {
        // Arrange
        Long idInexistente = 999L;
        when(cuentaRepo.findById(idInexistente)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> cuentaService.reactivarCuenta(idInexistente))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Cuenta no encontrada");

        verify(cuentaRepo).findById(idInexistente);
        verify(cuentaRepo, never()).save(any());
    }

    // ==================== Cambiar Rol ====================

    @Test
    @DisplayName("Debe cambiar rol de usuario a admin exitosamente")
    void cambiarRol_DeUsuarioAAdmin_Success() {
        // Arrange
        when(cuentaRepo.findById(cuentaId)).thenReturn(Optional.of(cuentaMock));
        when(cuentaRepo.save(any(CuentaAuth.class))).thenReturn(cuentaMock);

        // Act
        cuentaService.cambiarRol(cuentaId, rolAdmin);

        // Assert
        ArgumentCaptor<CuentaAuth> cuentaCaptor = ArgumentCaptor.forClass(CuentaAuth.class);
        verify(cuentaRepo).save(cuentaCaptor.capture());
        
        CuentaAuth cuentaGuardada = cuentaCaptor.getValue();
        assertThat(cuentaGuardada.getRol().getTipo()).isEqualTo(TipoRol.ROLE_ADMIN);

        verify(cuentaRepo).findById(cuentaId);
    }

    @Test
    @DisplayName("Debe lanzar excepción si cuenta no existe al cambiar rol")
    void cambiarRol_CuentaNoExiste_ThrowsException() {
        // Arrange
        Long idInexistente = 999L;
        when(cuentaRepo.findById(idInexistente)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> cuentaService.cambiarRol(idInexistente, rolAdmin))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Cuenta no encontrada");

        verify(cuentaRepo).findById(idInexistente);
        verify(cuentaRepo, never()).save(any());
    }

    // ==================== Buscar por Email ====================

    @Test
    @DisplayName("Debe buscar cuenta por email exitosamente")
    void buscarPorEmail_EmailExistente_Success() {
        // Arrange
        String email = "test@nutritrack.com";
        when(cuentaRepo.findByEmail(email)).thenReturn(Optional.of(cuentaMock));

        // Act
        Optional<CuentaAuth> resultado = cuentaService.buscarPorEmail(email);

        // Assert
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getEmail()).isEqualTo(email);
        assertThat(resultado.get().getId()).isEqualTo(cuentaId);

        verify(cuentaRepo).findByEmail(email);
    }

    @Test
    @DisplayName("Debe retornar Optional vacío si email no existe")
    void buscarPorEmail_EmailNoExiste_ReturnsEmpty() {
        // Arrange
        String emailInexistente = "noexiste@test.com";
        when(cuentaRepo.findByEmail(emailInexistente)).thenReturn(Optional.empty());

        // Act
        Optional<CuentaAuth> resultado = cuentaService.buscarPorEmail(emailInexistente);

        // Assert
        assertThat(resultado).isEmpty();
        verify(cuentaRepo).findByEmail(emailInexistente);
    }

    // ==================== Casos Edge ====================

    @Test
    @DisplayName("Debe manejar múltiples operaciones en la misma cuenta")
    void multipleOperations_MismaCuenta_Success() {
        // Arrange
        when(cuentaRepo.findById(cuentaId)).thenReturn(Optional.of(cuentaMock));
        when(cuentaRepo.save(any(CuentaAuth.class))).thenReturn(cuentaMock);
        when(passwordEncoder.encode(any())).thenReturn("encodedNewPassword");

        // Act - Cambiar password
        cuentaService.cambiarPassword(cuentaId, "newPass1");
        
        // Act - Desactivar
        cuentaService.desactivarCuenta(cuentaId);
        
        // Act - Cambiar rol
        cuentaService.cambiarRol(cuentaId, rolAdmin);

        // Assert
        verify(cuentaRepo, times(3)).findById(cuentaId);
        verify(cuentaRepo, times(3)).save(any(CuentaAuth.class));
    }

    @Test
    @DisplayName("Debe permitir reactivar cuenta ya activa sin error")
    void reactivarCuenta_YaActiva_NoError() {
        // Arrange
        cuentaMock.setActive(true); // Ya está activa
        when(cuentaRepo.findById(cuentaId)).thenReturn(Optional.of(cuentaMock));
        when(cuentaRepo.save(any(CuentaAuth.class))).thenReturn(cuentaMock);

        // Act
        cuentaService.reactivarCuenta(cuentaId);

        // Assert
        ArgumentCaptor<CuentaAuth> cuentaCaptor = ArgumentCaptor.forClass(CuentaAuth.class);
        verify(cuentaRepo).save(cuentaCaptor.capture());
        assertThat(cuentaCaptor.getValue().isActive()).isTrue();
    }

    @Test
    @DisplayName("Debe permitir desactivar cuenta ya inactiva sin error")
    void desactivarCuenta_YaInactiva_NoError() {
        // Arrange
        cuentaMock.setActive(false); // Ya está inactiva
        when(cuentaRepo.findById(cuentaId)).thenReturn(Optional.of(cuentaMock));
        when(cuentaRepo.save(any(CuentaAuth.class))).thenReturn(cuentaMock);

        // Act
        cuentaService.desactivarCuenta(cuentaId);

        // Assert
        ArgumentCaptor<CuentaAuth> cuentaCaptor = ArgumentCaptor.forClass(CuentaAuth.class);
        verify(cuentaRepo).save(cuentaCaptor.capture());
        assertThat(cuentaCaptor.getValue().isActive()).isFalse();
    }
}
