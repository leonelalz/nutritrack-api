package com.nutritrack.nutritrackapi.unit;

import com.nutritrack.nutritrackapi.dto.request.ActualizarPerfilRequest;
import com.nutritrack.nutritrackapi.dto.response.PerfilUsuarioResponse;
import com.nutritrack.nutritrackapi.exception.ResourceNotFoundException;
import com.nutritrack.nutritrackapi.model.*;
import com.nutritrack.nutritrackapi.model.enums.NivelActividad;
import com.nutritrack.nutritrackapi.model.enums.ObjetivoGeneral;
import com.nutritrack.nutritrackapi.model.enums.TipoEtiqueta;
import com.nutritrack.nutritrackapi.model.enums.UnidadesMedida;
import com.nutritrack.nutritrackapi.repository.*;
import com.nutritrack.nutritrackapi.service.PerfilUsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PerfilUsuarioService - Pruebas Unitarias")
class PerfilUsuarioServiceTest {

    @Mock
    private PerfilUsuarioRepository perfilRepo;

    @Mock
    private CuentaAuthRepository cuentaRepo;

    @Mock
    private UsuarioPerfilSaludRepository perfilSaludRepo;

    @Mock
    private EtiquetaRepository etiquetaRepo;

    @InjectMocks
    private PerfilUsuarioService perfilService;

    private UUID perfilId;
    private UUID cuentaId;
    private CuentaAuth cuentaMock;
    private PerfilUsuario perfilMock;
    private UsuarioPerfilSalud perfilSaludMock;

    @BeforeEach
    void setUp() {
        perfilId = UUID.randomUUID();
        cuentaId = UUID.randomUUID();

        // Mock CuentaAuth
        cuentaMock = CuentaAuth.builder()
                .id(cuentaId)
                .email("test@nutritrack.com")
                .password("encodedPassword")
                .active(true)
                .build();

        // Mock PerfilUsuario
        perfilMock = new PerfilUsuario();
        perfilMock.setId(perfilId);
        perfilMock.setCuenta(cuentaMock);
        perfilMock.setNombre("Juan Pérez");
        perfilMock.setUnidadesMedida(UnidadesMedida.KG);
        perfilMock.setFechaInicioApp(LocalDate.now());

        // Mock UsuarioPerfilSalud
        perfilSaludMock = UsuarioPerfilSalud.builder()
                .idPerfil(perfilId)
                .objetivoActual(ObjetivoGeneral.PERDER_PESO)
                .nivelActividadActual(NivelActividad.MODERADO)
                .etiquetasSalud(new HashSet<>())
                .fechaActualizacion(LocalDate.now())
                .build();
    }

    // ==================== US-04: Obtener Perfil Completo ====================

    @Test
    @DisplayName("US-04: Debe obtener perfil completo exitosamente")
    void obtenerPerfilCompleto_PerfilExistente_Success() {
        // Arrange
        when(perfilRepo.findById(perfilId)).thenReturn(Optional.of(perfilMock));
        when(perfilSaludRepo.findById(perfilId)).thenReturn(Optional.of(perfilSaludMock));

        // Act
        PerfilUsuarioResponse response = perfilService.obtenerPerfilCompleto(perfilId);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getProfileId()).isEqualTo(perfilId);
        assertThat(response.getNombre()).isEqualTo("Juan Pérez");
        assertThat(response.getUnidadesMedida()).isEqualTo(UnidadesMedida.KG);
        assertThat(response.getPerfilSalud()).isNotNull();
        assertThat(response.getPerfilSalud().getObjetivoActual()).isEqualTo(ObjetivoGeneral.PERDER_PESO);
        assertThat(response.getPerfilSalud().getNivelActividadActual()).isEqualTo(NivelActividad.MODERADO);

        verify(perfilRepo).findById(perfilId);
        verify(perfilSaludRepo).findById(perfilId);
    }

    @Test
    @DisplayName("US-04: Debe obtener perfil sin datos de salud")
    void obtenerPerfilCompleto_SinPerfilSalud_Success() {
        // Arrange
        when(perfilRepo.findById(perfilId)).thenReturn(Optional.of(perfilMock));
        when(perfilSaludRepo.findById(perfilId)).thenReturn(Optional.empty());

        // Act
        PerfilUsuarioResponse response = perfilService.obtenerPerfilCompleto(perfilId);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getProfileId()).isEqualTo(perfilId);
        assertThat(response.getNombre()).isEqualTo("Juan Pérez");
        assertThat(response.getPerfilSalud()).isNull();

        verify(perfilRepo).findById(perfilId);
        verify(perfilSaludRepo).findById(perfilId);
    }

    @Test
    @DisplayName("US-04: Debe lanzar excepción si perfil no existe")
    void obtenerPerfilCompleto_PerfilNoExiste_ThrowsException() {
        // Arrange
        UUID idInexistente = UUID.randomUUID();
        when(perfilRepo.findById(idInexistente)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> perfilService.obtenerPerfilCompleto(idInexistente))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Perfil no encontrado");

        verify(perfilRepo).findById(idInexistente);
        verify(perfilSaludRepo, never()).findById(any());
    }

    // ==================== Obtener Perfil ID por Email ====================

    @Test
    @DisplayName("Debe obtener ID de perfil por email")
    void obtenerPerfilIdPorEmail_EmailValido_Success() {
        // Arrange
        String email = "test@nutritrack.com";
        when(cuentaRepo.findByEmail(email)).thenReturn(Optional.of(cuentaMock));
        when(perfilRepo.findByCuenta_Id(cuentaId)).thenReturn(Optional.of(perfilMock));

        // Act
        UUID resultado = perfilService.obtenerPerfilIdPorEmail(email);

        // Assert
        assertThat(resultado).isEqualTo(perfilId);
        verify(cuentaRepo).findByEmail(email);
        verify(perfilRepo).findByCuenta_Id(cuentaId);
    }

    @Test
    @DisplayName("Debe lanzar excepción si email no existe")
    void obtenerPerfilIdPorEmail_EmailInexistente_ThrowsException() {
        // Arrange
        String emailInexistente = "noexiste@test.com";
        when(cuentaRepo.findByEmail(emailInexistente)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> perfilService.obtenerPerfilIdPorEmail(emailInexistente))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Cuenta no encontrada");

        verify(cuentaRepo).findByEmail(emailInexistente);
        verify(perfilRepo, never()).findByCuenta_Id(any());
    }

    @Test
    @DisplayName("Debe lanzar excepción si cuenta no tiene perfil")
    void obtenerPerfilIdPorEmail_SinPerfil_ThrowsException() {
        // Arrange
        String email = "test@nutritrack.com";
        when(cuentaRepo.findByEmail(email)).thenReturn(Optional.of(cuentaMock));
        when(perfilRepo.findByCuenta_Id(cuentaId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> perfilService.obtenerPerfilIdPorEmail(email))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Perfil no encontrado para la cuenta");

        verify(cuentaRepo).findByEmail(email);
        verify(perfilRepo).findByCuenta_Id(cuentaId);
    }

    // ==================== US-03: Actualizar Perfil ====================

    @Test
    @DisplayName("US-03: Debe actualizar solo el nombre del perfil")
    void actualizarPerfil_SoloNombre_Success() {
        // Arrange
        ActualizarPerfilRequest request = ActualizarPerfilRequest.builder()
                .nombre("Carlos Rodríguez")
                .build();

        when(perfilRepo.findById(perfilId)).thenReturn(Optional.of(perfilMock));
        when(perfilRepo.save(any(PerfilUsuario.class))).thenReturn(perfilMock);
        when(perfilSaludRepo.findById(perfilId)).thenReturn(Optional.of(perfilSaludMock));

        // Act
        PerfilUsuarioResponse response = perfilService.actualizarPerfil(perfilId, request);

        // Assert
        assertThat(response).isNotNull();
        verify(perfilRepo, atLeast(1)).findById(perfilId);
        verify(perfilRepo, atLeast(1)).save(any(PerfilUsuario.class));
        verify(perfilSaludRepo, atLeast(1)).findById(perfilId);
    }

    @Test
    @DisplayName("US-03: Debe actualizar perfil con datos de salud")
    void actualizarPerfil_ConPerfilSalud_Success() {
        // Arrange
        Etiqueta etiqueta1 = Etiqueta.builder().id(1L).nombre("Vegetariano").tipoEtiqueta(TipoEtiqueta.DIETA).build();
        Etiqueta etiqueta2 = Etiqueta.builder().id(2L).nombre("Sin gluten").tipoEtiqueta(TipoEtiqueta.DIETA).build();
        Set<Long> etiquetasIds = new HashSet<>(Arrays.asList(1L, 2L));

        ActualizarPerfilRequest request = ActualizarPerfilRequest.builder()
                .nombre("María González")
                .unidadesMedida(UnidadesMedida.LBS)
                .objetivoActual(ObjetivoGeneral.GANAR_MASA_MUSCULAR)
                .nivelActividadActual(NivelActividad.ALTO)
                .etiquetasSaludIds(etiquetasIds)
                .build();

        when(perfilRepo.findById(perfilId)).thenReturn(Optional.of(perfilMock));
        when(perfilRepo.save(any(PerfilUsuario.class))).thenReturn(perfilMock);
        when(perfilSaludRepo.findById(perfilId)).thenReturn(Optional.of(perfilSaludMock));
        when(etiquetaRepo.findAllById(etiquetasIds)).thenReturn(Arrays.asList(etiqueta1, etiqueta2));
        when(perfilSaludRepo.save(any(UsuarioPerfilSalud.class))).thenReturn(perfilSaludMock);

        // Act
        PerfilUsuarioResponse response = perfilService.actualizarPerfil(perfilId, request);

        // Assert
        assertThat(response).isNotNull();
        verify(perfilRepo, atLeast(1)).save(any(PerfilUsuario.class));
        verify(perfilSaludRepo, atLeast(1)).save(any(UsuarioPerfilSalud.class));
        verify(etiquetaRepo).findAllById(etiquetasIds);
    }

    @Test
    @DisplayName("US-03: Debe crear perfil de salud si no existe")
    void actualizarPerfil_CrearPerfilSalud_Success() {
        // Arrange
        ActualizarPerfilRequest request = ActualizarPerfilRequest.builder()
                .objetivoActual(ObjetivoGeneral.MANTENER_FORMA)
                .nivelActividadActual(NivelActividad.BAJO)
                .build();

        when(perfilRepo.findById(perfilId)).thenReturn(Optional.of(perfilMock));
        when(perfilRepo.save(any(PerfilUsuario.class))).thenReturn(perfilMock);
        when(perfilSaludRepo.findById(perfilId)).thenReturn(Optional.empty()); // No existe
        when(perfilSaludRepo.save(any(UsuarioPerfilSalud.class))).thenReturn(perfilSaludMock);

        // Act
        PerfilUsuarioResponse response = perfilService.actualizarPerfil(perfilId, request);

        // Assert
        assertThat(response).isNotNull();
        verify(perfilSaludRepo, atLeast(1)).findById(perfilId);
        verify(perfilSaludRepo, atLeast(1)).save(any(UsuarioPerfilSalud.class));
    }

    @Test
    @DisplayName("US-03: Debe lanzar excepción si perfil no existe al actualizar")
    void actualizarPerfil_PerfilNoExiste_ThrowsException() {
        // Arrange
        UUID idInexistente = UUID.randomUUID();
        ActualizarPerfilRequest request = ActualizarPerfilRequest.builder()
                .nombre("Test")
                .build();

        when(perfilRepo.findById(idInexistente)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> perfilService.actualizarPerfil(idInexistente, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Perfil no encontrado");

        verify(perfilRepo).findById(idInexistente);
        verify(perfilRepo, never()).save(any());
    }

    // ==================== US-05: Eliminar Cuenta ====================

    @Test
    @DisplayName("US-05: Debe desactivar cuenta exitosamente (soft delete)")
    void eliminarCuenta_PerfilExistente_Success() {
        // Arrange
        when(perfilRepo.findById(perfilId)).thenReturn(Optional.of(perfilMock));
        when(cuentaRepo.save(any(CuentaAuth.class))).thenReturn(cuentaMock);

        // Act
        perfilService.eliminarCuenta(perfilId);

        // Assert
        assertThat(cuentaMock.isActive()).isFalse();
        verify(perfilRepo).findById(perfilId);
        verify(cuentaRepo).save(cuentaMock);
    }

    @Test
    @DisplayName("US-05: Debe lanzar excepción si perfil no existe al eliminar")
    void eliminarCuenta_PerfilNoExiste_ThrowsException() {
        // Arrange
        UUID idInexistente = UUID.randomUUID();
        when(perfilRepo.findById(idInexistente)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> perfilService.eliminarCuenta(idInexistente))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Perfil no encontrado");

        verify(perfilRepo).findById(idInexistente);
        verify(cuentaRepo, never()).save(any());
    }

    @Test
    @DisplayName("US-05: Debe manejar perfil sin cuenta asociada")
    void eliminarCuenta_SinCuenta_NoError() {
        // Arrange
        perfilMock.setCuenta(null); // Sin cuenta asociada
        when(perfilRepo.findById(perfilId)).thenReturn(Optional.of(perfilMock));

        // Act
        perfilService.eliminarCuenta(perfilId);

        // Assert
        verify(perfilRepo).findById(perfilId);
        verify(cuentaRepo, never()).save(any());
    }

    // ==================== Métodos Legacy ====================

    @Test
    @DisplayName("Legacy: Debe actualizar nombre de perfil")
    void actualizarNombre_Success() {
        // Arrange
        String nuevoNombre = "Pedro Martínez";
        when(perfilRepo.findById(perfilId)).thenReturn(Optional.of(perfilMock));
        when(perfilRepo.save(any(PerfilUsuario.class))).thenReturn(perfilMock);

        // Act
        PerfilUsuario resultado = perfilService.actualizarNombre(perfilId, nuevoNombre);

        // Assert
        assertThat(resultado).isNotNull();
        verify(perfilRepo).findById(perfilId);
        verify(perfilRepo).save(any(PerfilUsuario.class));
    }

    @Test
    @DisplayName("Legacy: Debe obtener perfil por cuenta ID")
    void obtenerPorCuenta_Success() {
        // Arrange
        when(cuentaRepo.findById(cuentaId)).thenReturn(Optional.of(cuentaMock));
        when(perfilRepo.findByCuenta_Id(cuentaId)).thenReturn(Optional.of(perfilMock));

        // Act
        PerfilUsuario resultado = perfilService.obtenerPorCuenta(cuentaId);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(perfilId);
        verify(cuentaRepo).findById(cuentaId);
        verify(perfilRepo).findByCuenta_Id(cuentaId);
    }

    @Test
    @DisplayName("Legacy: Debe listar todos los perfiles")
    void listarPerfiles_Success() {
        // Arrange
        List<PerfilUsuario> perfiles = Arrays.asList(perfilMock, new PerfilUsuario());
        when(perfilRepo.findAll()).thenReturn(perfiles);

        // Act
        List<PerfilUsuario> resultado = perfilService.listarPerfiles();

        // Assert
        assertThat(resultado).hasSize(2);
        verify(perfilRepo).findAll();
    }

    @Test
    @DisplayName("Legacy: Debe eliminar perfil permanentemente")
    void eliminarPerfil_Success() {
        // Arrange
        when(perfilRepo.existsById(perfilId)).thenReturn(true);
        doNothing().when(perfilRepo).deleteById(perfilId);

        // Act
        perfilService.eliminarPerfil(perfilId);

        // Assert
        verify(perfilRepo).existsById(perfilId);
        verify(perfilRepo).deleteById(perfilId);
    }

    @Test
    @DisplayName("Legacy: Debe lanzar excepción al eliminar perfil inexistente")
    void eliminarPerfil_NoExiste_ThrowsException() {
        // Arrange
        UUID idInexistente = UUID.randomUUID();
        when(perfilRepo.existsById(idInexistente)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> perfilService.eliminarPerfil(idInexistente))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Perfil no encontrado");

        verify(perfilRepo).existsById(idInexistente);
        verify(perfilRepo, never()).deleteById(any());
    }
}
