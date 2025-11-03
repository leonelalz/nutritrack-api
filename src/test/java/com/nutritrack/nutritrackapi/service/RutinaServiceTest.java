package com.nutritrack.nutritrackapi.service;

import com.nutritrack.nutritrackapi.dto.request.ActualizarRutinaRequest;
import com.nutritrack.nutritrackapi.dto.request.AgregarEjercicioRutinaRequest;
import com.nutritrack.nutritrackapi.dto.request.CrearRutinaRequest;
import com.nutritrack.nutritrackapi.dto.response.RutinaDetalleResponse;
import com.nutritrack.nutritrackapi.dto.response.RutinaResponse;
import com.nutritrack.nutritrackapi.exception.BusinessRuleException;
import com.nutritrack.nutritrackapi.exception.DuplicateResourceException;
import com.nutritrack.nutritrackapi.exception.ResourceNotFoundException;
import com.nutritrack.nutritrackapi.model.*;
import com.nutritrack.nutritrackapi.model.enums.TipoEjercicio;
import com.nutritrack.nutritrackapi.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RutinaService - Pruebas Unitarias")
class RutinaServiceTest {

    @Mock
    private RutinaRepository rutinaRepository;

    @Mock
    private RutinaEjercicioRepository rutinaEjercicioRepository;

    @Mock
    private EjercicioRepository ejercicioRepository;

    @Mock
    private EtiquetaRepository etiquetaRepository;

    @InjectMocks
    private RutinaService rutinaService;

    private Rutina rutinaMock;
    private Ejercicio ejercicioMock;
    private RutinaEjercicio rutinaEjercicioMock;
    private Etiqueta etiquetaMock;

    @BeforeEach
    void setUp() {
        // Rutina mock
        rutinaMock = Rutina.builder()
                .id(1L)
                .nombre("Rutina Full Body 12 Semanas")
                .descripcion("Rutina completa de cuerpo completo")
                .duracionSemanas(12)
                .activo(true)
                .ejercicios(new ArrayList<>())
                .etiquetas(new ArrayList<>())
                .build();

        // Ejercicio mock - Sentadillas
        ejercicioMock = Ejercicio.builder()
                .id(1L)
                .nombre("Sentadillas")
                .tipoEjercicio(TipoEjercicio.FUERZA)
                .duracion(45) // 45 minutos estimados
                .caloriasEstimadas(new BigDecimal("150.00"))
                .build();

        // RutinaEjercicio mock
        rutinaEjercicioMock = RutinaEjercicio.builder()
                .id(1L)
                .rutina(rutinaMock)
                .ejercicio(ejercicioMock)
                .orden(1)
                .series(3)
                .repeticiones(12)
                .peso(new BigDecimal("70.00"))
                .duracionMinutos(30)
                .notas("Mantener la espalda recta")
                .build();

        // Etiqueta mock
        etiquetaMock = Etiqueta.builder()
                .id(1L)
                .nombre("Fuerza")
                .descripcion("Ejercicios de fuerza")
                .build();
    }

    // ==================== TESTS DE CREAR ====================

    @Test
    @DisplayName("Crear rutina válida sin etiquetas")
    void crearRutinaValidaSinEtiquetas() {
        // Arrange
        CrearRutinaRequest request = new CrearRutinaRequest();
        request.setNombre("Nueva Rutina");
        request.setDescripcion("Descripción de rutina");
        request.setDuracionSemanas(8);

        when(rutinaRepository.existsByNombreIgnoreCase(request.getNombre())).thenReturn(false);
        when(rutinaRepository.save(any(Rutina.class))).thenReturn(rutinaMock);

        // Act
        RutinaResponse response = rutinaService.crear(request);

        // Assert
        assertNotNull(response);
        assertEquals(rutinaMock.getId(), response.getId());
        verify(rutinaRepository).existsByNombreIgnoreCase(request.getNombre());
        verify(rutinaRepository).save(any(Rutina.class));
    }

    @Test
    @DisplayName("Crear rutina con nombre duplicado debe lanzar excepción")
    void crearRutinaConNombreDuplicado() {
        // Arrange
        CrearRutinaRequest request = new CrearRutinaRequest();
        request.setNombre("Rutina Existente");

        when(rutinaRepository.existsByNombreIgnoreCase(request.getNombre())).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateResourceException.class, () -> rutinaService.crear(request));
        verify(rutinaRepository).existsByNombreIgnoreCase(request.getNombre());
        verify(rutinaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Crear rutina con etiquetas")
    void crearRutinaConEtiquetas() {
        // Arrange
        CrearRutinaRequest request = new CrearRutinaRequest();
        request.setNombre("Nueva Rutina");
        request.setDescripcion("Descripción");
        request.setDuracionSemanas(12);
        request.setEtiquetasIds(Arrays.asList(1L));

        when(rutinaRepository.existsByNombreIgnoreCase(request.getNombre())).thenReturn(false);
        when(etiquetaRepository.findAllById(Arrays.asList(1L))).thenReturn(Arrays.asList(etiquetaMock));
        when(rutinaRepository.save(any(Rutina.class))).thenReturn(rutinaMock);
        when(rutinaEjercicioRepository.findByRutinaIdOrderByOrdenAsc(anyLong())).thenReturn(new ArrayList<>());

        // Act
        RutinaResponse response = rutinaService.crear(request);

        // Assert
        assertNotNull(response);
        verify(etiquetaRepository).findAllById(Arrays.asList(1L));
        verify(rutinaRepository).save(any(Rutina.class));
    }

    // ==================== TESTS DE ACTUALIZAR ====================

    @Test
    @DisplayName("Actualizar nombre de rutina")
    void actualizarNombreRutina() {
        // Arrange
        ActualizarRutinaRequest request = new ActualizarRutinaRequest();
        request.setNombre("Nuevo Nombre");

        when(rutinaRepository.findById(1L)).thenReturn(Optional.of(rutinaMock));
        when(rutinaRepository.existsByNombreIgnoreCaseAndIdNot("Nuevo Nombre", 1L)).thenReturn(false);
        when(rutinaRepository.save(any(Rutina.class))).thenReturn(rutinaMock);

        // Act
        RutinaResponse response = rutinaService.actualizar(1L, request);

        // Assert
        assertNotNull(response);
        verify(rutinaRepository).findById(1L);
        verify(rutinaRepository).existsByNombreIgnoreCaseAndIdNot("Nuevo Nombre", 1L);
        verify(rutinaRepository).save(rutinaMock);
    }

    @Test
    @DisplayName("Actualizar rutina con nombre duplicado debe lanzar excepción")
    void actualizarRutinaConNombreDuplicado() {
        // Arrange
        ActualizarRutinaRequest request = new ActualizarRutinaRequest();
        request.setNombre("Nombre Existente");

        when(rutinaRepository.findById(1L)).thenReturn(Optional.of(rutinaMock));
        when(rutinaRepository.existsByNombreIgnoreCaseAndIdNot("Nombre Existente", 1L)).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateResourceException.class, () -> rutinaService.actualizar(1L, request));
        verify(rutinaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Actualizar duración de semanas")
    void actualizarDuracionSemanas() {
        // Arrange
        ActualizarRutinaRequest request = new ActualizarRutinaRequest();
        request.setDuracionSemanas(16);

        when(rutinaRepository.findById(1L)).thenReturn(Optional.of(rutinaMock));
        when(rutinaRepository.save(any(Rutina.class))).thenReturn(rutinaMock);

        // Act
        RutinaResponse response = rutinaService.actualizar(1L, request);

        // Assert
        assertNotNull(response);
        verify(rutinaRepository).save(rutinaMock);
    }

    @Test
    @DisplayName("Actualizar rutina inexistente debe lanzar excepción")
    void actualizarRutinaInexistente() {
        // Arrange
        ActualizarRutinaRequest request = new ActualizarRutinaRequest();
        request.setNombre("Nuevo Nombre");

        when(rutinaRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> rutinaService.actualizar(999L, request));
        verify(rutinaRepository, never()).save(any());
    }

    // ==================== TESTS DE ELIMINAR ====================

    @Test
    @DisplayName("Eliminar rutina existente")
    void eliminarRutinaExistente() {
        // Arrange
        when(rutinaRepository.existsById(1L)).thenReturn(true);
        doNothing().when(rutinaRepository).deleteById(1L);

        // Act
        assertDoesNotThrow(() -> rutinaService.eliminar(1L));

        // Assert
        verify(rutinaRepository).existsById(1L);
        verify(rutinaRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Eliminar rutina inexistente debe lanzar excepción")
    void eliminarRutinaInexistente() {
        // Arrange
        when(rutinaRepository.existsById(999L)).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> rutinaService.eliminar(999L));
        verify(rutinaRepository, never()).deleteById(anyLong());
    }

    // ==================== TESTS DE AGREGAR EJERCICIO ====================

    @Test
    @DisplayName("Agregar ejercicio válido a rutina")
    void agregarEjercicioValidoARutina() {
        // Arrange
        AgregarEjercicioRutinaRequest request = new AgregarEjercicioRutinaRequest();
        request.setEjercicioId(1L);
        request.setSeries(3);
        request.setRepeticiones(12);
        request.setPeso(new BigDecimal("70.00"));
        request.setDuracionMinutos(30);
        request.setNotas("Mantener postura");

        when(rutinaRepository.findById(1L)).thenReturn(Optional.of(rutinaMock));
        when(ejercicioRepository.findById(1L)).thenReturn(Optional.of(ejercicioMock));
        when(rutinaEjercicioRepository.existsByRutinaIdAndEjercicioId(1L, 1L)).thenReturn(false);
        when(rutinaEjercicioRepository.findMaxOrdenByRutinaId(1L)).thenReturn(0);
        when(rutinaEjercicioRepository.save(any(RutinaEjercicio.class))).thenReturn(rutinaEjercicioMock);
        when(rutinaRepository.findByIdWithEjercicios(1L)).thenReturn(Optional.of(rutinaMock));

        // Act
        RutinaDetalleResponse response = rutinaService.agregarEjercicio(1L, request);

        // Assert
        assertNotNull(response);
        verify(rutinaRepository).findById(1L);
        verify(ejercicioRepository).findById(1L);
        verify(rutinaEjercicioRepository).existsByRutinaIdAndEjercicioId(1L, 1L);
        verify(rutinaEjercicioRepository).findMaxOrdenByRutinaId(1L);
        verify(rutinaEjercicioRepository).save(any(RutinaEjercicio.class));
    }

    @Test
    @DisplayName("Agregar ejercicio duplicado debe lanzar excepción")
    void agregarEjercicioDuplicado() {
        // Arrange
        AgregarEjercicioRutinaRequest request = new AgregarEjercicioRutinaRequest();
        request.setEjercicioId(1L);
        request.setSeries(3);
        request.setRepeticiones(12);
        request.setDuracionMinutos(30);

        when(rutinaRepository.findById(1L)).thenReturn(Optional.of(rutinaMock));
        when(ejercicioRepository.findById(1L)).thenReturn(Optional.of(ejercicioMock));
        when(rutinaEjercicioRepository.existsByRutinaIdAndEjercicioId(1L, 1L)).thenReturn(true);

        // Act & Assert
        assertThrows(BusinessRuleException.class, () -> rutinaService.agregarEjercicio(1L, request));
        verify(rutinaEjercicioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Agregar ejercicio a rutina inexistente debe lanzar excepción")
    void agregarEjercicioARutinaInexistente() {
        // Arrange
        AgregarEjercicioRutinaRequest request = new AgregarEjercicioRutinaRequest();
        request.setEjercicioId(1L);

        when(rutinaRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> rutinaService.agregarEjercicio(999L, request));
        verify(rutinaEjercicioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Verificar orden auto-increment al agregar ejercicio")
    void verificarOrdenAutoIncrement() {
        // Arrange
        AgregarEjercicioRutinaRequest request = new AgregarEjercicioRutinaRequest();
        request.setEjercicioId(1L);
        request.setSeries(4);
        request.setRepeticiones(10);
        request.setDuracionMinutos(25);

        when(rutinaRepository.findById(1L)).thenReturn(Optional.of(rutinaMock));
        when(ejercicioRepository.findById(1L)).thenReturn(Optional.of(ejercicioMock));
        when(rutinaEjercicioRepository.existsByRutinaIdAndEjercicioId(1L, 1L)).thenReturn(false);
        when(rutinaEjercicioRepository.findMaxOrdenByRutinaId(1L)).thenReturn(2); // Ya hay 2 ejercicios
        when(rutinaRepository.findByIdWithEjercicios(1L)).thenReturn(Optional.of(rutinaMock));
        when(rutinaEjercicioRepository.save(any(RutinaEjercicio.class))).thenAnswer(invocation -> {
            RutinaEjercicio re = invocation.getArgument(0);
            assertEquals(3, re.getOrden(), "El orden debe ser 3 (max + 1)");
            return re;
        });

        // Act
        rutinaService.agregarEjercicio(1L, request);

        // Assert
        verify(rutinaEjercicioRepository).findMaxOrdenByRutinaId(1L);
    }

    // ==================== TESTS DE REMOVER EJERCICIO ====================

    @Test
    @DisplayName("Remover ejercicio de rutina")
    void removerEjercicioDeRutina() {
        // Arrange
        RutinaEjercicio re1 = RutinaEjercicio.builder().id(1L).orden(1).rutina(rutinaMock).build();
        RutinaEjercicio re2 = RutinaEjercicio.builder().id(2L).orden(2).rutina(rutinaMock).build();
        RutinaEjercicio re3 = RutinaEjercicio.builder().id(3L).orden(3).rutina(rutinaMock).build();

        when(rutinaRepository.existsById(1L)).thenReturn(true);
        when(rutinaEjercicioRepository.findByRutinaIdAndEjercicioId(1L, 2L)).thenReturn(Optional.of(re2));
        when(rutinaEjercicioRepository.findByRutinaIdOrderByOrdenAsc(1L))
                .thenReturn(Arrays.asList(re1, re3)); // Después de eliminar re2
        when(rutinaRepository.findByIdWithEjercicios(1L)).thenReturn(Optional.of(rutinaMock));
        doNothing().when(rutinaEjercicioRepository).delete(re2);
        when(rutinaEjercicioRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        RutinaDetalleResponse response = rutinaService.removerEjercicio(1L, 2L);

        // Assert
        assertNotNull(response);
        verify(rutinaEjercicioRepository).delete(re2);
        verify(rutinaEjercicioRepository).saveAll(anyList());
        assertEquals(1, re1.getOrden());
        assertEquals(2, re3.getOrden()); // Re-ordenado de 3 a 2
    }

    // ==================== TESTS DE ETIQUETAS ====================

    @Test
    @DisplayName("Agregar etiqueta a rutina")
    void agregarEtiquetaARutina() {
        // Arrange
        when(rutinaRepository.findByIdWithEtiquetas(1L)).thenReturn(Optional.of(rutinaMock));
        when(etiquetaRepository.findById(1L)).thenReturn(Optional.of(etiquetaMock));
        when(rutinaRepository.save(any(Rutina.class))).thenReturn(rutinaMock);

        // Act
        assertDoesNotThrow(() -> rutinaService.agregarEtiqueta(1L, 1L));

        // Assert
        verify(rutinaRepository).findByIdWithEtiquetas(1L);
        verify(etiquetaRepository).findById(1L);
        verify(rutinaRepository).save(rutinaMock);
    }

    @Test
    @DisplayName("Agregar etiqueta duplicada debe lanzar excepción")
    void agregarEtiquetaDuplicada() {
        // Arrange
        rutinaMock.getEtiquetas().add(etiquetaMock);

        when(rutinaRepository.findByIdWithEtiquetas(1L)).thenReturn(Optional.of(rutinaMock));
        when(etiquetaRepository.findById(1L)).thenReturn(Optional.of(etiquetaMock));

        // Act & Assert
        assertThrows(BusinessRuleException.class, () -> rutinaService.agregarEtiqueta(1L, 1L));
        verify(rutinaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Remover etiqueta de rutina")
    void removerEtiquetaDeRutina() {
        // Arrange
        rutinaMock.getEtiquetas().add(etiquetaMock);

        when(rutinaRepository.findByIdWithEtiquetas(1L)).thenReturn(Optional.of(rutinaMock));
        when(etiquetaRepository.findById(1L)).thenReturn(Optional.of(etiquetaMock));
        when(rutinaRepository.save(any(Rutina.class))).thenReturn(rutinaMock);

        // Act
        assertDoesNotThrow(() -> rutinaService.removerEtiqueta(1L, 1L));

        // Assert
        verify(rutinaRepository).findByIdWithEtiquetas(1L);
        verify(rutinaRepository).save(rutinaMock);
    }

    // ==================== TESTS DE CÁLCULO DE CALORÍAS ====================

    @Test
    @DisplayName("Verificar cálculo de calorías estimadas")
    void verificarCalculoDeCalorias() {
        // Arrange
        rutinaMock.getEjercicios().add(rutinaEjercicioMock);

        when(rutinaRepository.findById(1L)).thenReturn(Optional.of(rutinaMock));
        when(rutinaEjercicioRepository.findByRutinaIdOrderByOrdenAsc(1L))
                .thenReturn(Arrays.asList(rutinaEjercicioMock));

        // Expected: (150 calorías / 45 minutos) * 30 minutos = 99.90 calorías (con redondeo)
        BigDecimal expectedCalorias = new BigDecimal("99.90");

        // Act
        RutinaResponse response = rutinaService.buscarPorId(1L);

        // Assert
        assertNotNull(response);
        assertEquals(expectedCalorias, response.getCaloriasEstimadasTotal());
        assertEquals(30, response.getDuracionTotalMinutos());
        assertEquals(1, response.getTotalEjercicios());
    }

    // ==================== TESTS DE BÚSQUEDA ====================

    @Test
    @DisplayName("Buscar detalle de rutina existente")
    void buscarDetalleDeRutinaExistente() {
        // Arrange
        when(rutinaRepository.findByIdWithEjercicios(1L)).thenReturn(Optional.of(rutinaMock));

        // Act
        RutinaDetalleResponse response = rutinaService.buscarDetallePorId(1L);

        // Assert
        assertNotNull(response);
        assertEquals(rutinaMock.getId(), response.getId());
        verify(rutinaRepository).findByIdWithEjercicios(1L);
    }

    @Test
    @DisplayName("Buscar detalle de rutina inexistente debe lanzar excepción")
    void buscarDetalleDeRutinaInexistente() {
        // Arrange
        when(rutinaRepository.findByIdWithEjercicios(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> rutinaService.buscarDetallePorId(999L));
    }

    @Test
    @DisplayName("Listar todas las rutinas")
    void listarTodasLasRutinas() {
        // Arrange
        when(rutinaRepository.findAll()).thenReturn(Arrays.asList(rutinaMock));

        // Act
        List<RutinaResponse> rutinas = rutinaService.listarTodos();

        // Assert
        assertNotNull(rutinas);
        assertFalse(rutinas.isEmpty());
        assertEquals(1, rutinas.size());
        verify(rutinaRepository).findAll();
    }

    @Test
    @DisplayName("Buscar rutinas por nombre")
    void buscarRutinasPorNombre() {
        // Arrange
        when(rutinaRepository.findByNombreContainingIgnoreCase("Full Body"))
                .thenReturn(Arrays.asList(rutinaMock));

        // Act
        List<RutinaResponse> rutinas = rutinaService.buscarPorNombre("Full Body");

        // Assert
        assertNotNull(rutinas);
        assertEquals(1, rutinas.size());
        verify(rutinaRepository).findByNombreContainingIgnoreCase("Full Body");
    }
}
