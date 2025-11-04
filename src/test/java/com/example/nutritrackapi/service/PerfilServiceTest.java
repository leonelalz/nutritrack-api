package com.example.nutritrackapi.service;

import com.example.nutritrackapi.dto.*;
import com.example.nutritrackapi.model.*;
import com.example.nutritrackapi.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para PerfilService
 * Cubre User Stories: US-03 (Unidades), US-04 (Perfil Salud), US-24 (Mediciones)
 * Cubre Reglas de Negocio: RN22, RN27
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PerfilService - Pruebas Unitarias")
class PerfilServiceTest {

    @Mock
    private PerfilUsuarioRepository perfilUsuarioRepository;

    @Mock
    private UsuarioPerfilSaludRepository perfilSaludRepository;

    @Mock
    private UsuarioHistorialMedidasRepository historialMedidasRepository;

    @Mock
    private UsuarioEtiquetasSaludRepository etiquetasSaludRepository;

    @Mock
    private EtiquetaRepository etiquetaRepository;

    @Mock
    private CuentaAuthRepository cuentaAuthRepository;

    @InjectMocks
    private PerfilService perfilService;

    private CuentaAuth cuentaAuth;
    private PerfilUsuario perfilUsuario;
    private UsuarioPerfilSalud perfilSalud;

    @BeforeEach
    void setUp() {
        // Setup cuenta auth
        cuentaAuth = new CuentaAuth();
        cuentaAuth.setId(1L);
        cuentaAuth.setEmail("test@example.com");

        // Setup perfil usuario
        perfilUsuario = new PerfilUsuario();
        perfilUsuario.setId(1L);
        perfilUsuario.setNombre("Test");
        perfilUsuario.setApellido("User");
        perfilUsuario.setCuenta(cuentaAuth);
        perfilUsuario.setUnidadesMedida(PerfilUsuario.UnidadesMedida.KG);
        perfilUsuario.setFechaInicioApp(LocalDate.now());

        cuentaAuth.setPerfilUsuario(perfilUsuario);

        // Setup perfil salud
        perfilSalud = UsuarioPerfilSalud.builder()
            .id(1L)
            .perfilUsuario(perfilUsuario)
            .objetivoActual(UsuarioPerfilSalud.ObjetivoSalud.PERDER_PESO)
            .nivelActividadActual(UsuarioPerfilSalud.NivelActividad.MODERADO)
            .fechaActualizacion(LocalDate.now())
            .build();
    }

    // ============================================================================
    // US-03: Actualizar Unidades de Medida
    // ============================================================================

    @Test
    @DisplayName("US-03: Actualizar unidades a LBS exitosamente")
    void testActualizarUnidadesALBS() {
        // Given
        UpdateUnidadesMedidaRequest request = new UpdateUnidadesMedidaRequest();
        request.setUnidadesMedida(PerfilUsuario.UnidadesMedida.LBS);

        when(cuentaAuthRepository.findByEmail("test@example.com"))
            .thenReturn(Optional.of(cuentaAuth));
        when(perfilUsuarioRepository.save(any(PerfilUsuario.class)))
            .thenReturn(perfilUsuario);

        // When
        perfilService.actualizarUnidadesMedida("test@example.com", request);

        // Then
        verify(cuentaAuthRepository).findByEmail("test@example.com");
        verify(perfilUsuarioRepository).save(perfilUsuario);
        assertEquals(PerfilUsuario.UnidadesMedida.LBS, perfilUsuario.getUnidadesMedida());
    }

    @Test
    @DisplayName("US-03: Actualizar unidades a KG exitosamente")
    void testActualizarUnidadesAKG() {
        // Given
        perfilUsuario.setUnidadesMedida(PerfilUsuario.UnidadesMedida.LBS);
        UpdateUnidadesMedidaRequest request = new UpdateUnidadesMedidaRequest();
        request.setUnidadesMedida(PerfilUsuario.UnidadesMedida.KG);

        when(cuentaAuthRepository.findByEmail("test@example.com"))
            .thenReturn(Optional.of(cuentaAuth));
        when(perfilUsuarioRepository.save(any(PerfilUsuario.class)))
            .thenReturn(perfilUsuario);

        // When
        perfilService.actualizarUnidadesMedida("test@example.com", request);

        // Then
        verify(perfilUsuarioRepository).save(perfilUsuario);
        assertEquals(PerfilUsuario.UnidadesMedida.KG, perfilUsuario.getUnidadesMedida());
    }

    // ============================================================================
    // US-04: Actualizar Perfil de Salud
    // ============================================================================

    @Test
    @DisplayName("US-04: Actualizar perfil de salud exitosamente")
    void testActualizarPerfilSaludExitoso() {
        // Given
        PerfilSaludRequest request = PerfilSaludRequest.builder()
            .objetivoActual(UsuarioPerfilSalud.ObjetivoSalud.GANAR_MASA_MUSCULAR)
            .nivelActividadActual(UsuarioPerfilSalud.NivelActividad.ALTO)
            .build();

        when(cuentaAuthRepository.findByEmail("test@example.com"))
            .thenReturn(Optional.of(cuentaAuth));
        when(perfilSaludRepository.findByPerfilUsuarioId(1L))
            .thenReturn(Optional.of(perfilSalud));
        when(perfilSaludRepository.save(any(UsuarioPerfilSalud.class)))
            .thenReturn(perfilSalud);
        when(etiquetasSaludRepository.findEtiquetasByPerfilId(1L))
            .thenReturn(Arrays.asList());

        // When
        PerfilSaludResponse response = perfilService.actualizarPerfilSalud("test@example.com", request);

        // Then
        assertNotNull(response);
        verify(perfilSaludRepository).save(any(UsuarioPerfilSalud.class));
        assertEquals(UsuarioPerfilSalud.ObjetivoSalud.GANAR_MASA_MUSCULAR, perfilSalud.getObjetivoActual());
        assertEquals(UsuarioPerfilSalud.NivelActividad.ALTO, perfilSalud.getNivelActividadActual());
    }

    @Test
    @DisplayName("US-04: Crear perfil de salud si no existe")
    void testCrearPerfilSaludSiNoExiste() {
        // Given
        PerfilSaludRequest request = PerfilSaludRequest.builder()
            .objetivoActual(UsuarioPerfilSalud.ObjetivoSalud.MANTENER_FORMA)
            .nivelActividadActual(UsuarioPerfilSalud.NivelActividad.MODERADO)
            .build();

        when(cuentaAuthRepository.findByEmail("test@example.com"))
            .thenReturn(Optional.of(cuentaAuth));
        when(perfilSaludRepository.findByPerfilUsuarioId(1L))
            .thenReturn(Optional.empty()) // No existe
            .thenReturn(Optional.of(perfilSalud)); // Existe después del save
        when(perfilSaludRepository.save(any(UsuarioPerfilSalud.class)))
            .thenReturn(perfilSalud);
        when(etiquetasSaludRepository.findEtiquetasByPerfilId(1L))
            .thenReturn(Arrays.asList());

        // When
        PerfilSaludResponse response = perfilService.actualizarPerfilSalud("test@example.com", request);

        // Then
        assertNotNull(response);
        verify(perfilSaludRepository).save(any(UsuarioPerfilSalud.class));
    }

    @Test
    @DisplayName("US-04: Obtener perfil de salud existente")
    void testObtenerPerfilSaludExistente() {
        // Given
        when(cuentaAuthRepository.findByEmail("test@example.com"))
            .thenReturn(Optional.of(cuentaAuth));
        when(perfilSaludRepository.findByPerfilUsuarioId(1L))
            .thenReturn(Optional.of(perfilSalud));
        when(etiquetasSaludRepository.findEtiquetasByPerfilId(1L))
            .thenReturn(Arrays.asList());

        // When
        PerfilSaludResponse response = perfilService.obtenerPerfilSalud("test@example.com");

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(UsuarioPerfilSalud.ObjetivoSalud.PERDER_PESO, response.getObjetivoActual());
        assertEquals(UsuarioPerfilSalud.NivelActividad.MODERADO, response.getNivelActividadActual());
    }

    @Test
    @DisplayName("US-04: Obtener perfil de salud retorna null si no existe")
    void testObtenerPerfilSaludNoExiste() {
        // Given
        when(cuentaAuthRepository.findByEmail("test@example.com"))
            .thenReturn(Optional.of(cuentaAuth));
        when(perfilSaludRepository.findByPerfilUsuarioId(1L))
            .thenReturn(Optional.empty());

        // When
        PerfilSaludResponse response = perfilService.obtenerPerfilSalud("test@example.com");

        // Then
        assertNull(response);
    }

    // ============================================================================
    // US-24: Registrar Mediciones Corporales
    // ============================================================================

    @Test
    @DisplayName("US-24: Registrar medición en KG exitosamente (RN27)")
    void testRegistrarMedicionEnKG() {
        // Given
        HistorialMedidasRequest request = HistorialMedidasRequest.builder()
            .peso(new BigDecimal("75.5"))
            .altura(new BigDecimal("175.0"))
            .fechaMedicion(LocalDate.of(2025, 11, 4))
            .unidadPeso(PerfilUsuario.UnidadesMedida.KG)
            .build();

        UsuarioHistorialMedidas medicionGuardada = UsuarioHistorialMedidas.builder()
            .id(1L)
            .perfilUsuario(perfilUsuario)
            .peso(new BigDecimal("75.5"))
            .altura(new BigDecimal("175.0"))
            .imc(new BigDecimal("24.65"))
            .fechaMedicion(LocalDate.of(2025, 11, 4))
            .build();

        when(cuentaAuthRepository.findByEmail("test@example.com"))
            .thenReturn(Optional.of(cuentaAuth));
        when(historialMedidasRepository.existsByPerfilUsuarioIdAndFechaMedicion(1L, LocalDate.of(2025, 11, 4)))
            .thenReturn(false);
        when(historialMedidasRepository.save(any(UsuarioHistorialMedidas.class)))
            .thenReturn(medicionGuardada);

        // When
        HistorialMedidasResponse response = perfilService.registrarMedicion("test@example.com", request);

        // Then
        assertNotNull(response);
        assertEquals(new BigDecimal("75.5"), response.getPeso());
        assertEquals(new BigDecimal("175.0"), response.getAltura());
        assertEquals(new BigDecimal("24.65"), response.getImc());
        assertEquals(PerfilUsuario.UnidadesMedida.KG, response.getUnidadPeso());

        verify(historialMedidasRepository).save(any(UsuarioHistorialMedidas.class));
    }

    @Test
    @DisplayName("RN27: Registrar medición en LBS convierte a KG antes de guardar")
    void testRegistrarMedicionEnLBSConvierteAKG() {
        // Given
        HistorialMedidasRequest request = HistorialMedidasRequest.builder()
            .peso(new BigDecimal("166.4")) // ~75.5 kg
            .altura(new BigDecimal("175.0"))
            .fechaMedicion(LocalDate.of(2025, 11, 4))
            .unidadPeso(PerfilUsuario.UnidadesMedida.LBS)
            .build();

        when(cuentaAuthRepository.findByEmail("test@example.com"))
            .thenReturn(Optional.of(cuentaAuth));
        when(historialMedidasRepository.existsByPerfilUsuarioIdAndFechaMedicion(anyLong(), any(LocalDate.class)))
            .thenReturn(false);
        when(historialMedidasRepository.save(any(UsuarioHistorialMedidas.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        perfilService.registrarMedicion("test@example.com", request);

        // Then
        verify(historialMedidasRepository).save(argThat(medicion -> {
            // Verificar que se guardó en KG (166.4 LBS = ~75.5 KG)
            return medicion.getPeso().compareTo(new BigDecimal("75")) > 0 &&
                   medicion.getPeso().compareTo(new BigDecimal("76")) < 0;
        }));
    }

    @Test
    @DisplayName("RN22: Registrar medición falla si ya existe para la fecha")
    void testRegistrarMedicionFallaFechaDuplicada() {
        // Given
        HistorialMedidasRequest request = HistorialMedidasRequest.builder()
            .peso(new BigDecimal("75.5"))
            .altura(new BigDecimal("175.0"))
            .fechaMedicion(LocalDate.of(2025, 11, 4))
            .build();

        when(cuentaAuthRepository.findByEmail("test@example.com"))
            .thenReturn(Optional.of(cuentaAuth));
        when(historialMedidasRepository.existsByPerfilUsuarioIdAndFechaMedicion(1L, LocalDate.of(2025, 11, 4)))
            .thenReturn(true); // Ya existe

        // When/Then
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> perfilService.registrarMedicion("test@example.com", request)
        );

        assertTrue(exception.getMessage().contains("fecha"));
        verify(historialMedidasRepository, never()).save(any());
    }

    @Test
    @DisplayName("US-24: Obtener historial de mediciones ordenado por fecha desc")
    void testObtenerHistorialMediciones() {
        // Given
        UsuarioHistorialMedidas medicion1 = UsuarioHistorialMedidas.builder()
            .id(1L)
            .perfilUsuario(perfilUsuario)
            .peso(new BigDecimal("75.5"))
            .altura(new BigDecimal("175.0"))
            .imc(new BigDecimal("24.65"))
            .fechaMedicion(LocalDate.of(2025, 11, 4))
            .build();

        UsuarioHistorialMedidas medicion2 = UsuarioHistorialMedidas.builder()
            .id(2L)
            .perfilUsuario(perfilUsuario)
            .peso(new BigDecimal("74.0"))
            .altura(new BigDecimal("175.0"))
            .imc(new BigDecimal("24.16"))
            .fechaMedicion(LocalDate.of(2025, 11, 1))
            .build();

        when(cuentaAuthRepository.findByEmail("test@example.com"))
            .thenReturn(Optional.of(cuentaAuth));
        when(historialMedidasRepository.findByPerfilUsuarioIdOrderByFechaMedicionDesc(1L))
            .thenReturn(Arrays.asList(medicion1, medicion2));

        // When
        List<HistorialMedidasResponse> historial = perfilService.obtenerHistorialMediciones("test@example.com");

        // Then
        assertNotNull(historial);
        assertEquals(2, historial.size());
        assertEquals(LocalDate.of(2025, 11, 4), historial.get(0).getFechaMedicion());
        assertEquals(LocalDate.of(2025, 11, 1), historial.get(1).getFechaMedicion());
        
        verify(historialMedidasRepository).findByPerfilUsuarioIdOrderByFechaMedicionDesc(1L);
    }

    @Test
    @DisplayName("RN27: Historial convierte peso a unidad preferida del usuario (LBS)")
    void testObtenerHistorialConvierteAUnidadPreferida() {
        // Given
        perfilUsuario.setUnidadesMedida(PerfilUsuario.UnidadesMedida.LBS);

        UsuarioHistorialMedidas medicion = UsuarioHistorialMedidas.builder()
            .id(1L)
            .perfilUsuario(perfilUsuario)
            .peso(new BigDecimal("75.0")) // Guardado en KG
            .altura(new BigDecimal("175.0"))
            .imc(new BigDecimal("24.49"))
            .fechaMedicion(LocalDate.now())
            .build();

        when(cuentaAuthRepository.findByEmail("test@example.com"))
            .thenReturn(Optional.of(cuentaAuth));
        when(historialMedidasRepository.findByPerfilUsuarioIdOrderByFechaMedicionDesc(1L))
            .thenReturn(Arrays.asList(medicion));

        // When
        List<HistorialMedidasResponse> historial = perfilService.obtenerHistorialMediciones("test@example.com");

        // Then
        assertNotNull(historial);
        assertEquals(1, historial.size());
        assertEquals(PerfilUsuario.UnidadesMedida.LBS, historial.get(0).getUnidadPeso());
        // 75 KG ≈ 165.35 LBS
        assertTrue(historial.get(0).getPeso().compareTo(new BigDecimal("165")) > 0);
        assertTrue(historial.get(0).getPeso().compareTo(new BigDecimal("166")) < 0);
    }
}
