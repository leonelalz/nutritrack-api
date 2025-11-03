package com.nutritrack.nutritrackapi.service;

import com.nutritrack.nutritrackapi.dto.request.CrearEjercicioRequest;
import com.nutritrack.nutritrackapi.dto.response.EjercicioResponse;
import com.nutritrack.nutritrackapi.exception.DuplicateResourceException;
import com.nutritrack.nutritrackapi.exception.ResourceNotFoundException;
import com.nutritrack.nutritrackapi.model.Ejercicio;
import com.nutritrack.nutritrackapi.model.Etiqueta;
import com.nutritrack.nutritrackapi.model.enums.Dificultad;
import com.nutritrack.nutritrackapi.model.enums.MusculoPrincipal;
import com.nutritrack.nutritrackapi.model.enums.TipoEjercicio;
import com.nutritrack.nutritrackapi.repository.EjercicioRepository;
import com.nutritrack.nutritrackapi.repository.EtiquetaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EjercicioService - Unit Tests")
class EjercicioServiceTest {

    @Mock
    private EjercicioRepository ejercicioRepository;

    @Mock
    private EtiquetaRepository etiquetaRepository;

    @InjectMocks
    private EjercicioService ejercicioService;

    private Ejercicio ejercicioMock;
    private Etiqueta etiquetaMock;

    @BeforeEach
    void setUp() {
        ejercicioMock = Ejercicio.builder()
                .id(1L)
                .nombre("Sentadillas")
                .tipoEjercicio(TipoEjercicio.FUERZA)
                .musculoPrincipal(MusculoPrincipal.PIERNAS)
                .duracion(30)
                .dificultad(Dificultad.INTERMEDIO)
                .caloriasEstimadas(new BigDecimal("150.00"))
                .etiquetas(new HashSet<>())
                .build();

        etiquetaMock = Etiqueta.builder()
                .id(1L)
                .nombre("Tren Inferior")
                .build();
    }

    @Test
    @DisplayName("Crear ejercicio - Datos válidos - Éxito")
    void crear_DatosValidos_Exito() {
        // Arrange
        CrearEjercicioRequest request = CrearEjercicioRequest.builder()
                .nombre("Sentadillas")
                .tipoEjercicio(TipoEjercicio.FUERZA)
                .musculoPrincipal(MusculoPrincipal.PIERNAS)
                .duracion(30)
                .dificultad(Dificultad.INTERMEDIO)
                .caloriasEstimadas(new BigDecimal("150.00"))
                .build();

        when(ejercicioRepository.existsByNombreIgnoreCase("Sentadillas")).thenReturn(false);
        when(ejercicioRepository.save(any(Ejercicio.class))).thenReturn(ejercicioMock);

        // Act
        EjercicioResponse response = ejercicioService.crear(request);

        // Assert
        assertNotNull(response);
        assertEquals("Sentadillas", response.getNombre());
        assertEquals(TipoEjercicio.FUERZA, response.getTipoEjercicio());
        assertEquals(MusculoPrincipal.PIERNAS, response.getMusculoPrincipal());
        verify(ejercicioRepository).save(any(Ejercicio.class));
    }

    @Test
    @DisplayName("Crear ejercicio - Nombre duplicado - Lanza excepción")
    void crear_NombreDuplicado_LanzaExcepcion() {
        // Arrange
        CrearEjercicioRequest request = CrearEjercicioRequest.builder()
                .nombre("Sentadillas")
                .tipoEjercicio(TipoEjercicio.FUERZA)
                .build();

        when(ejercicioRepository.existsByNombreIgnoreCase("Sentadillas")).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateResourceException.class, () -> ejercicioService.crear(request));
        verify(ejercicioRepository, never()).save(any(Ejercicio.class));
    }

    @Test
    @DisplayName("Crear ejercicio - Con etiquetas - Asigna correctamente")
    void crear_ConEtiquetas_AsignaCorrectamente() {
        // Arrange
        Set<Long> etiquetasIds = Set.of(1L);
        CrearEjercicioRequest request = CrearEjercicioRequest.builder()
                .nombre("Sentadillas")
                .tipoEjercicio(TipoEjercicio.FUERZA)
                .etiquetasIds(etiquetasIds)
                .build();

        when(ejercicioRepository.existsByNombreIgnoreCase("Sentadillas")).thenReturn(false);
        when(etiquetaRepository.findAllById(etiquetasIds)).thenReturn(Arrays.asList(etiquetaMock));
        when(ejercicioRepository.save(any(Ejercicio.class))).thenReturn(ejercicioMock);

        // Act
        ArgumentCaptor<Ejercicio> captor = ArgumentCaptor.forClass(Ejercicio.class);
        ejercicioService.crear(request);

        // Assert
        verify(ejercicioRepository).save(captor.capture());
        verify(etiquetaRepository).findAllById(etiquetasIds);
    }

    @Test
    @DisplayName("Actualizar ejercicio - Datos válidos - Éxito")
    void actualizar_DatosValidos_Exito() {
        // Arrange
        CrearEjercicioRequest request = CrearEjercicioRequest.builder()
                .nombre("Sentadillas Profundas")
                .tipoEjercicio(TipoEjercicio.FUERZA)
                .musculoPrincipal(MusculoPrincipal.GLUTEOS)
                .duracion(40)
                .dificultad(Dificultad.AVANZADO)
                .caloriasEstimadas(new BigDecimal("180.00"))
                .build();

        when(ejercicioRepository.findById(1L)).thenReturn(Optional.of(ejercicioMock));
        when(ejercicioRepository.existsByNombreIgnoreCase("Sentadillas Profundas")).thenReturn(false);
        when(ejercicioRepository.save(any(Ejercicio.class))).thenReturn(ejercicioMock);

        // Act
        EjercicioResponse response = ejercicioService.actualizar(1L, request);

        // Assert
        assertNotNull(response);
        verify(ejercicioRepository).save(any(Ejercicio.class));
    }

    @Test
    @DisplayName("Actualizar ejercicio - No existe - Lanza excepción")
    void actualizar_NoExiste_LanzaExcepcion() {
        // Arrange
        CrearEjercicioRequest request = CrearEjercicioRequest.builder()
                .nombre("Sentadillas")
                .tipoEjercicio(TipoEjercicio.FUERZA)
                .build();

        when(ejercicioRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> ejercicioService.actualizar(999L, request));
        verify(ejercicioRepository, never()).save(any(Ejercicio.class));
    }

    @Test
    @DisplayName("Eliminar ejercicio - Existe - Éxito")
    void eliminar_Existe_Exito() {
        // Arrange
        when(ejercicioRepository.existsById(1L)).thenReturn(true);

        // Act
        ejercicioService.eliminar(1L);

        // Assert
        verify(ejercicioRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Eliminar ejercicio - No existe - Lanza excepción")
    void eliminar_NoExiste_LanzaExcepcion() {
        // Arrange
        when(ejercicioRepository.existsById(999L)).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> ejercicioService.eliminar(999L));
        verify(ejercicioRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Buscar por ID - Existe - Devuelve ejercicio")
    void buscarPorId_Existe_DevuelveEjercicio() {
        // Arrange
        when(ejercicioRepository.findById(1L)).thenReturn(Optional.of(ejercicioMock));

        // Act
        EjercicioResponse response = ejercicioService.buscarPorId(1L);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Sentadillas", response.getNombre());
    }

    @Test
    @DisplayName("Buscar por ID - No existe - Lanza excepción")
    void buscarPorId_NoExiste_LanzaExcepcion() {
        // Arrange
        when(ejercicioRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> ejercicioService.buscarPorId(999L));
    }

    @Test
    @DisplayName("Buscar por nombre - Encuentra resultados")
    void buscarPorNombre_EncuentraResultados() {
        // Arrange
        List<Ejercicio> ejercicios = Arrays.asList(ejercicioMock);
        when(ejercicioRepository.findByNombreContainingIgnoreCase("Sentad")).thenReturn(ejercicios);

        // Act
        List<EjercicioResponse> responses = ejercicioService.buscarPorNombre("Sentad");

        // Assert
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("Sentadillas", responses.get(0).getNombre());
    }

    @Test
    @DisplayName("Buscar por tipo - Encuentra resultados")
    void buscarPorTipo_EncuentraResultados() {
        // Arrange
        List<Ejercicio> ejercicios = Arrays.asList(ejercicioMock);
        when(ejercicioRepository.findByTipoEjercicio(TipoEjercicio.FUERZA)).thenReturn(ejercicios);

        // Act
        List<EjercicioResponse> responses = ejercicioService.buscarPorTipo(TipoEjercicio.FUERZA);

        // Assert
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(TipoEjercicio.FUERZA, responses.get(0).getTipoEjercicio());
    }

    @Test
    @DisplayName("Buscar por músculo - Encuentra resultados")
    void buscarPorMusculo_EncuentraResultados() {
        // Arrange
        List<Ejercicio> ejercicios = Arrays.asList(ejercicioMock);
        when(ejercicioRepository.findByMusculoPrincipal(MusculoPrincipal.PIERNAS)).thenReturn(ejercicios);

        // Act
        List<EjercicioResponse> responses = ejercicioService.buscarPorMusculo(MusculoPrincipal.PIERNAS);

        // Assert
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(MusculoPrincipal.PIERNAS, responses.get(0).getMusculoPrincipal());
    }

    @Test
    @DisplayName("Buscar por dificultad - Encuentra resultados")
    void buscarPorDificultad_EncuentraResultados() {
        // Arrange
        List<Ejercicio> ejercicios = Arrays.asList(ejercicioMock);
        when(ejercicioRepository.findByDificultad(Dificultad.INTERMEDIO)).thenReturn(ejercicios);

        // Act
        List<EjercicioResponse> responses = ejercicioService.buscarPorDificultad(Dificultad.INTERMEDIO);

        // Assert
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(Dificultad.INTERMEDIO, responses.get(0).getDificultad());
    }

    @Test
    @DisplayName("Listar todos - Devuelve lista")
    void listarTodos_DevuelveLista() {
        // Arrange
        List<Ejercicio> ejercicios = Arrays.asList(ejercicioMock);
        when(ejercicioRepository.findAll()).thenReturn(ejercicios);

        // Act
        List<EjercicioResponse> responses = ejercicioService.listarTodos();

        // Assert
        assertNotNull(responses);
        assertEquals(1, responses.size());
    }

    @Test
    @DisplayName("Agregar etiqueta - Ambos existen - Éxito")
    void agregarEtiqueta_AmbosExisten_Exito() {
        // Arrange
        when(ejercicioRepository.findById(1L)).thenReturn(Optional.of(ejercicioMock));
        when(etiquetaRepository.findById(1L)).thenReturn(Optional.of(etiquetaMock));
        when(ejercicioRepository.save(any(Ejercicio.class))).thenReturn(ejercicioMock);

        // Act
        EjercicioResponse response = ejercicioService.agregarEtiqueta(1L, 1L);

        // Assert
        assertNotNull(response);
        verify(ejercicioRepository).save(any(Ejercicio.class));
    }

    @Test
    @DisplayName("Agregar etiqueta - Ejercicio no existe - Lanza excepción")
    void agregarEtiqueta_EjercicioNoExiste_LanzaExcepcion() {
        // Arrange
        when(ejercicioRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> ejercicioService.agregarEtiqueta(999L, 1L));
        verify(ejercicioRepository, never()).save(any(Ejercicio.class));
    }

    @Test
    @DisplayName("Remover etiqueta - Ejercicio existe - Éxito")
    void removerEtiqueta_EjercicioExiste_Exito() {
        // Arrange
        ejercicioMock.getEtiquetas().add(etiquetaMock);
        when(ejercicioRepository.findById(1L)).thenReturn(Optional.of(ejercicioMock));
        when(ejercicioRepository.save(any(Ejercicio.class))).thenReturn(ejercicioMock);

        // Act
        EjercicioResponse response = ejercicioService.removerEtiqueta(1L, 1L);

        // Assert
        assertNotNull(response);
        verify(ejercicioRepository).save(any(Ejercicio.class));
    }
}
