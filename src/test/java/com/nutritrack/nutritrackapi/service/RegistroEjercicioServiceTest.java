package com.nutritrack.nutritrackapi.service;

import com.nutritrack.nutritrackapi.dto.request.RegistrarEjercicioRequest;
import com.nutritrack.nutritrackapi.dto.response.EstadisticasEjercicioResponse;
import com.nutritrack.nutritrackapi.dto.response.RegistroEjercicioResponse;
import com.nutritrack.nutritrackapi.exception.BusinessRuleException;
import com.nutritrack.nutritrackapi.exception.ResourceNotFoundException;
import com.nutritrack.nutritrackapi.model.*;
import com.nutritrack.nutritrackapi.model.enums.EstadoAsignacion;
import com.nutritrack.nutritrackapi.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests del servicio RegistroEjercicioService")
class RegistroEjercicioServiceTest {

    @Mock
    private RegistroEjercicioRepository registroEjercicioRepository;

    @Mock
    private PerfilUsuarioRepository perfilUsuarioRepository;

    @Mock
    private EjercicioRepository ejercicioRepository;

    @Mock
    private UsuarioRutinaRepository usuarioRutinaRepository;

    @InjectMocks
    private RegistroEjercicioService registroEjercicioService;

    private PerfilUsuario perfilUsuarioMock;
    private Ejercicio ejercicioMock;
    private UsuarioRutina usuarioRutinaMock;
    private RegistroEjercicio registroEjercicioMock;
    private RegistrarEjercicioRequest registrarEjercicioRequest;

    @BeforeEach
    void setUp() {
        perfilUsuarioMock = PerfilUsuario.builder()
                .id(1L)
                .nombre("Ana")
                .apellido("Martínez")
                .build();

        // Ejercicio: Sentadillas - 300 kcal en 30 minutos = 10 kcal/min
        ejercicioMock = Ejercicio.builder()
                .id(1L)
                .nombre("Sentadillas")
                .caloriasEstimadas(new BigDecimal("300.00"))
                .duracion(30) // minutos
                .build();

        Rutina rutina = Rutina.builder()
                .id(1L)
                .nombre("Rutina de Fuerza")
                .build();

        usuarioRutinaMock = UsuarioRutina.builder()
                .id(1L)
                .perfilUsuario(perfilUsuarioMock)
                .rutina(rutina)
                .estado(EstadoAsignacion.ACTIVO)
                .build();

        registroEjercicioMock = RegistroEjercicio.builder()
                .id(1L)
                .perfilUsuario(perfilUsuarioMock)
                .ejercicio(ejercicioMock)
                .usuarioRutina(usuarioRutinaMock)
                .fecha(LocalDate.of(2025, 11, 3))
                .hora(LocalTime.of(18, 0))
                .seriesRealizadas(4)
                .repeticionesRealizadas(12)
                .pesoUtilizado(new BigDecimal("80.00"))
                .duracionMinutos(25)
                .caloriasQuemadas(new BigDecimal("250.00"))
                .notas("Buen rendimiento")
                .build();

        registrarEjercicioRequest = RegistrarEjercicioRequest.builder()
                .ejercicioId(1L)
                .usuarioRutinaId(1L)
                .fecha(LocalDate.of(2025, 11, 3))
                .hora(LocalTime.of(18, 0))
                .seriesRealizadas(4)
                .repeticionesRealizadas(12)
                .pesoUtilizado(new BigDecimal("80.00"))
                .duracionMinutos(25)
                .notas("Buen rendimiento")
                .build();
    }

    @Test
    @DisplayName("Debe registrar ejercicio correctamente")
    void debeRegistrarEjercicioCorrectamente() {
        when(perfilUsuarioRepository.findById(1L)).thenReturn(Optional.of(perfilUsuarioMock));
        when(ejercicioRepository.findById(1L)).thenReturn(Optional.of(ejercicioMock));
        when(usuarioRutinaRepository.findById(1L)).thenReturn(Optional.of(usuarioRutinaMock));
        when(registroEjercicioRepository.save(any(RegistroEjercicio.class))).thenReturn(registroEjercicioMock);

        RegistroEjercicioResponse response = registroEjercicioService.registrarEjercicio(1L, registrarEjercicioRequest);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Sentadillas", response.getNombreEjercicio());
        assertEquals(4, response.getSeriesRealizadas());
        verify(registroEjercicioRepository).save(any(RegistroEjercicio.class));
    }

    @Test
    @DisplayName("Debe calcular calorías quemadas correctamente")
    void debeCalcularCaloriasQuemadasCorrectamente() {
        when(perfilUsuarioRepository.findById(1L)).thenReturn(Optional.of(perfilUsuarioMock));
        when(ejercicioRepository.findById(1L)).thenReturn(Optional.of(ejercicioMock));
        when(usuarioRutinaRepository.findById(1L)).thenReturn(Optional.of(usuarioRutinaMock));
        when(registroEjercicioRepository.save(any(RegistroEjercicio.class))).thenAnswer(invocation -> {
            RegistroEjercicio registro = invocation.getArgument(0);
            // 300 kcal / 30 min = 10 kcal/min
            // 10 kcal/min * 25 min = 250 kcal
            assertEquals(new BigDecimal("250.00"), registro.getCaloriasQuemadas());
            return registro;
        });

        registroEjercicioService.registrarEjercicio(1L, registrarEjercicioRequest);

        verify(registroEjercicioRepository).save(any(RegistroEjercicio.class));
    }

    @Test
    @DisplayName("Debe calcular calorías con duración diferente")
    void debeCalcularCaloriasConDuracionDiferente() {
        registrarEjercicioRequest.setDuracionMinutos(45);
        
        when(perfilUsuarioRepository.findById(1L)).thenReturn(Optional.of(perfilUsuarioMock));
        when(ejercicioRepository.findById(1L)).thenReturn(Optional.of(ejercicioMock));
        when(usuarioRutinaRepository.findById(1L)).thenReturn(Optional.of(usuarioRutinaMock));
        when(registroEjercicioRepository.save(any(RegistroEjercicio.class))).thenAnswer(invocation -> {
            RegistroEjercicio registro = invocation.getArgument(0);
            // 10 kcal/min * 45 min = 450 kcal
            assertEquals(new BigDecimal("450.00"), registro.getCaloriasQuemadas());
            return registro;
        });

        registroEjercicioService.registrarEjercicio(1L, registrarEjercicioRequest);

        verify(registroEjercicioRepository).save(any(RegistroEjercicio.class));
    }

    @Test
    @DisplayName("Debe registrar ejercicio sin rutina asignada")
    void debeRegistrarEjercicioSinRutina() {
        registrarEjercicioRequest.setUsuarioRutinaId(null);
        
        when(perfilUsuarioRepository.findById(1L)).thenReturn(Optional.of(perfilUsuarioMock));
        when(ejercicioRepository.findById(1L)).thenReturn(Optional.of(ejercicioMock));
        when(registroEjercicioRepository.save(any(RegistroEjercicio.class))).thenReturn(registroEjercicioMock);

        RegistroEjercicioResponse response = registroEjercicioService.registrarEjercicio(1L, registrarEjercicioRequest);

        assertNotNull(response);
        verify(usuarioRutinaRepository, never()).findById(any());
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el usuario no existe")
    void debeLanzarExcepcionCuandoUsuarioNoExiste() {
        when(perfilUsuarioRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, 
            () -> registroEjercicioService.registrarEjercicio(1L, registrarEjercicioRequest));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el ejercicio no existe")
    void debeLanzarExcepcionCuandoEjercicioNoExiste() {
        when(perfilUsuarioRepository.findById(1L)).thenReturn(Optional.of(perfilUsuarioMock));
        when(ejercicioRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, 
            () -> registroEjercicioService.registrarEjercicio(1L, registrarEjercicioRequest));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando la rutina no pertenece al usuario")
    void debeLanzarExcepcionCuandoRutinaNoPertenece() {
        PerfilUsuario otroUsuario = PerfilUsuario.builder().id(2L).build();
        usuarioRutinaMock.setPerfilUsuario(otroUsuario);
        
        when(perfilUsuarioRepository.findById(1L)).thenReturn(Optional.of(perfilUsuarioMock));
        when(ejercicioRepository.findById(1L)).thenReturn(Optional.of(ejercicioMock));
        when(usuarioRutinaRepository.findById(1L)).thenReturn(Optional.of(usuarioRutinaMock));

        assertThrows(BusinessRuleException.class, 
            () -> registroEjercicioService.registrarEjercicio(1L, registrarEjercicioRequest));
    }

    @Test
    @DisplayName("Debe listar registros por fecha")
    void debeListarRegistrosPorFecha() {
        when(registroEjercicioRepository.findByPerfilUsuarioIdAndFecha(1L, LocalDate.of(2025, 11, 3)))
                .thenReturn(Arrays.asList(registroEjercicioMock));

        List<RegistroEjercicioResponse> registros = registroEjercicioService.listarRegistrosPorFecha(
                1L, LocalDate.of(2025, 11, 3));

        assertNotNull(registros);
        assertEquals(1, registros.size());
        assertEquals("Sentadillas", registros.get(0).getNombreEjercicio());
    }

    @Test
    @DisplayName("Debe listar registros por período")
    void debeListarRegistrosPorPeriodo() {
        when(registroEjercicioRepository.findByPerfilUsuarioIdAndFechaBetween(
                1L, LocalDate.of(2025, 11, 1), LocalDate.of(2025, 11, 7)))
                .thenReturn(Arrays.asList(registroEjercicioMock));

        List<RegistroEjercicioResponse> registros = registroEjercicioService.listarRegistrosPorPeriodo(
                1L, LocalDate.of(2025, 11, 1), LocalDate.of(2025, 11, 7));

        assertNotNull(registros);
        assertEquals(1, registros.size());
    }

    @Test
    @DisplayName("Debe buscar registro por ID correctamente")
    void debeBuscarRegistroPorId() {
        when(registroEjercicioRepository.findById(1L)).thenReturn(Optional.of(registroEjercicioMock));

        RegistroEjercicioResponse response = registroEjercicioService.buscarPorId(1L, 1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el registro no pertenece al usuario")
    void debeLanzarExcepcionCuandoRegistroNoPertenece() {
        when(registroEjercicioRepository.findById(1L)).thenReturn(Optional.of(registroEjercicioMock));

        assertThrows(BusinessRuleException.class, 
            () -> registroEjercicioService.buscarPorId(2L, 1L));
    }

    @Test
    @DisplayName("Debe eliminar registro correctamente")
    void debeEliminarRegistro() {
        when(registroEjercicioRepository.findById(1L)).thenReturn(Optional.of(registroEjercicioMock));

        assertDoesNotThrow(() -> registroEjercicioService.eliminarRegistro(1L, 1L));
        verify(registroEjercicioRepository).delete(registroEjercicioMock);
    }

    @Test
    @DisplayName("Debe obtener estadísticas correctamente")
    void debeObtenerEstadisticas() {
        LocalDate fechaInicio = LocalDate.of(2025, 11, 1);
        LocalDate fechaFin = LocalDate.of(2025, 11, 7);
        
        when(registroEjercicioRepository.sumCaloriasQuemadasByPeriodo(1L, fechaInicio, fechaFin))
                .thenReturn(new BigDecimal("1500.00"));
        when(registroEjercicioRepository.sumDuracionMinutosByPeriodo(1L, fechaInicio, fechaFin))
                .thenReturn(150);
        when(registroEjercicioRepository.getEstadisticasPorEjercicio(1L, fechaInicio, fechaFin))
                .thenReturn(Arrays.asList(
                    new Object[]{"Sentadillas", 5L, new BigDecimal("800.00"), 75L},
                    new Object[]{"Press de Banca", 3L, new BigDecimal("500.00"), 50L},
                    new Object[]{"Cardio", 2L, new BigDecimal("200.00"), 25L}
                ));

        EstadisticasEjercicioResponse response = registroEjercicioService.obtenerEstadisticas(1L, fechaInicio, fechaFin);

        assertNotNull(response);
        assertEquals(new BigDecimal("1500.00"), response.getCaloriasTotalesQuemadas());
        assertEquals(new BigDecimal("214.29"), response.getCaloriasPromedioDiario()); // 1500 / 7
        assertEquals(150, response.getDuracionTotalMinutos());
        assertEquals(10, response.getTotalRegistros());
        assertEquals(3, response.getEjerciciosMasRealizados().size());
        assertEquals("Sentadillas", response.getEjerciciosMasRealizados().get(0).getNombreEjercicio());
    }

    @Test
    @DisplayName("Debe manejar peso nulo en el registro")
    void debeManejarPesoNulo() {
        registrarEjercicioRequest.setPesoUtilizado(null);
        
        when(perfilUsuarioRepository.findById(1L)).thenReturn(Optional.of(perfilUsuarioMock));
        when(ejercicioRepository.findById(1L)).thenReturn(Optional.of(ejercicioMock));
        when(usuarioRutinaRepository.findById(1L)).thenReturn(Optional.of(usuarioRutinaMock));
        when(registroEjercicioRepository.save(any(RegistroEjercicio.class))).thenReturn(registroEjercicioMock);

        RegistroEjercicioResponse response = registroEjercicioService.registrarEjercicio(1L, registrarEjercicioRequest);

        assertNotNull(response);
        verify(registroEjercicioRepository).save(any(RegistroEjercicio.class));
    }
}
