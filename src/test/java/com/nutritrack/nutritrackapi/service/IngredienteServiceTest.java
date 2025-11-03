package com.nutritrack.nutritrackapi.service;

import com.nutritrack.nutritrackapi.dto.request.CrearIngredienteRequest;
import com.nutritrack.nutritrackapi.dto.response.IngredienteResponse;
import com.nutritrack.nutritrackapi.exception.DuplicateResourceException;
import com.nutritrack.nutritrackapi.exception.ResourceNotFoundException;
import com.nutritrack.nutritrackapi.model.Etiqueta;
import com.nutritrack.nutritrackapi.model.Ingrediente;
import com.nutritrack.nutritrackapi.model.enums.GrupoAlimenticio;
import com.nutritrack.nutritrackapi.repository.EtiquetaRepository;
import com.nutritrack.nutritrackapi.repository.IngredienteRepository;
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
@DisplayName("IngredienteService - Unit Tests")
class IngredienteServiceTest {

    @Mock
    private IngredienteRepository ingredienteRepository;

    @Mock
    private EtiquetaRepository etiquetaRepository;

    @InjectMocks
    private IngredienteService ingredienteService;

    private Ingrediente ingredienteMock;
    private Etiqueta etiquetaMock;

    @BeforeEach
    void setUp() {
        ingredienteMock = Ingrediente.builder()
                .id(1L)
                .nombre("Pollo")
                .grupoAlimenticio(GrupoAlimenticio.PROTEINAS_ANIMALES)
                .energia(new BigDecimal("165.00"))
                .proteinas(new BigDecimal("31.00"))
                .grasas(new BigDecimal("3.60"))
                .carbohidratos(new BigDecimal("0.00"))
                .etiquetas(new HashSet<>())
                .build();

        etiquetaMock = Etiqueta.builder()
                .id(1L)
                .nombre("Alto en Proteína")
                .build();
    }

    @Test
    @DisplayName("Crear ingrediente - Datos válidos - Éxito")
    void crear_DatosValidos_Exito() {
        // Arrange
        CrearIngredienteRequest request = CrearIngredienteRequest.builder()
                .nombre("Pollo")
                .grupoAlimenticio(GrupoAlimenticio.PROTEINAS_ANIMALES)
                .energia(new BigDecimal("165.00"))
                .proteinas(new BigDecimal("31.00"))
                .grasas(new BigDecimal("3.60"))
                .carbohidratos(new BigDecimal("0.00"))
                .build();

        when(ingredienteRepository.existsByNombreIgnoreCase("Pollo")).thenReturn(false);
        when(ingredienteRepository.save(any(Ingrediente.class))).thenReturn(ingredienteMock);

        // Act
        IngredienteResponse response = ingredienteService.crear(request);

        // Assert
        assertNotNull(response);
        assertEquals("Pollo", response.getNombre());
        assertEquals(GrupoAlimenticio.PROTEINAS_ANIMALES, response.getGrupoAlimenticio());
        assertEquals(new BigDecimal("165.00"), response.getEnergia());
        verify(ingredienteRepository).save(any(Ingrediente.class));
    }

    @Test
    @DisplayName("Crear ingrediente - Nombre duplicado - Lanza excepción")
    void crear_NombreDuplicado_LanzaExcepcion() {
        // Arrange
        CrearIngredienteRequest request = CrearIngredienteRequest.builder()
                .nombre("Pollo")
                .grupoAlimenticio(GrupoAlimenticio.PROTEINAS_ANIMALES)
                .energia(new BigDecimal("165.00"))
                .proteinas(new BigDecimal("31.00"))
                .grasas(new BigDecimal("3.60"))
                .carbohidratos(new BigDecimal("0.00"))
                .build();

        when(ingredienteRepository.existsByNombreIgnoreCase("Pollo")).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateResourceException.class, () -> ingredienteService.crear(request));
        verify(ingredienteRepository, never()).save(any(Ingrediente.class));
    }

    @Test
    @DisplayName("Crear ingrediente - Con etiquetas - Asigna correctamente")
    void crear_ConEtiquetas_AsignaCorrectamente() {
        // Arrange
        Set<Long> etiquetasIds = Set.of(1L, 2L);
        CrearIngredienteRequest request = CrearIngredienteRequest.builder()
                .nombre("Pollo")
                .grupoAlimenticio(GrupoAlimenticio.PROTEINAS_ANIMALES)
                .energia(new BigDecimal("165.00"))
                .proteinas(new BigDecimal("31.00"))
                .grasas(new BigDecimal("3.60"))
                .carbohidratos(new BigDecimal("0.00"))
                .etiquetasIds(etiquetasIds)
                .build();

        List<Etiqueta> etiquetas = Arrays.asList(
                Etiqueta.builder().id(1L).nombre("Alto en Proteína").build(),
                Etiqueta.builder().id(2L).nombre("Bajo en Grasa").build()
        );

        when(ingredienteRepository.existsByNombreIgnoreCase("Pollo")).thenReturn(false);
        when(etiquetaRepository.findAllById(etiquetasIds)).thenReturn(etiquetas);
        when(ingredienteRepository.save(any(Ingrediente.class))).thenReturn(ingredienteMock);

        // Act
        ArgumentCaptor<Ingrediente> captor = ArgumentCaptor.forClass(Ingrediente.class);
        ingredienteService.crear(request);

        // Assert
        verify(ingredienteRepository).save(captor.capture());
        verify(etiquetaRepository).findAllById(etiquetasIds);
    }

    @Test
    @DisplayName("Actualizar ingrediente - Datos válidos - Éxito")
    void actualizar_DatosValidos_Exito() {
        // Arrange
        CrearIngredienteRequest request = CrearIngredienteRequest.builder()
                .nombre("Pollo Actualizado")
                .grupoAlimenticio(GrupoAlimenticio.PROTEINAS_ANIMALES)
                .energia(new BigDecimal("170.00"))
                .proteinas(new BigDecimal("32.00"))
                .grasas(new BigDecimal("4.00"))
                .carbohidratos(new BigDecimal("0.00"))
                .build();

        when(ingredienteRepository.findById(1L)).thenReturn(Optional.of(ingredienteMock));
        when(ingredienteRepository.existsByNombreIgnoreCase("Pollo Actualizado")).thenReturn(false);
        when(ingredienteRepository.save(any(Ingrediente.class))).thenReturn(ingredienteMock);

        // Act
        IngredienteResponse response = ingredienteService.actualizar(1L, request);

        // Assert
        assertNotNull(response);
        verify(ingredienteRepository).save(any(Ingrediente.class));
    }

    @Test
    @DisplayName("Actualizar ingrediente - No existe - Lanza excepción")
    void actualizar_NoExiste_LanzaExcepcion() {
        // Arrange
        CrearIngredienteRequest request = CrearIngredienteRequest.builder()
                .nombre("Pollo")
                .grupoAlimenticio(GrupoAlimenticio.PROTEINAS_ANIMALES)
                .energia(new BigDecimal("165.00"))
                .proteinas(new BigDecimal("31.00"))
                .grasas(new BigDecimal("3.60"))
                .carbohidratos(new BigDecimal("0.00"))
                .build();

        when(ingredienteRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> ingredienteService.actualizar(999L, request));
        verify(ingredienteRepository, never()).save(any(Ingrediente.class));
    }

    @Test
    @DisplayName("Actualizar ingrediente - Nombre duplicado diferente ID - Lanza excepción")
    void actualizar_NombreDuplicadoDiferenteId_LanzaExcepcion() {
        // Arrange
        CrearIngredienteRequest request = CrearIngredienteRequest.builder()
                .nombre("Pescado")
                .grupoAlimenticio(GrupoAlimenticio.PROTEINAS_ANIMALES)
                .energia(new BigDecimal("165.00"))
                .proteinas(new BigDecimal("31.00"))
                .grasas(new BigDecimal("3.60"))
                .carbohidratos(new BigDecimal("0.00"))
                .build();

        when(ingredienteRepository.findById(1L)).thenReturn(Optional.of(ingredienteMock));
        when(ingredienteRepository.existsByNombreIgnoreCase("Pescado")).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateResourceException.class, () -> ingredienteService.actualizar(1L, request));
        verify(ingredienteRepository, never()).save(any(Ingrediente.class));
    }

    @Test
    @DisplayName("Eliminar ingrediente - Existe - Éxito")
    void eliminar_Existe_Exito() {
        // Arrange
        when(ingredienteRepository.existsById(1L)).thenReturn(true);

        // Act
        ingredienteService.eliminar(1L);

        // Assert
        verify(ingredienteRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Eliminar ingrediente - No existe - Lanza excepción")
    void eliminar_NoExiste_LanzaExcepcion() {
        // Arrange
        when(ingredienteRepository.existsById(999L)).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> ingredienteService.eliminar(999L));
        verify(ingredienteRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Buscar por ID - Existe - Devuelve ingrediente")
    void buscarPorId_Existe_DevuelveIngrediente() {
        // Arrange
        when(ingredienteRepository.findById(1L)).thenReturn(Optional.of(ingredienteMock));

        // Act
        IngredienteResponse response = ingredienteService.buscarPorId(1L);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Pollo", response.getNombre());
    }

    @Test
    @DisplayName("Buscar por ID - No existe - Lanza excepción")
    void buscarPorId_NoExiste_LanzaExcepcion() {
        // Arrange
        when(ingredienteRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> ingredienteService.buscarPorId(999L));
    }

    @Test
    @DisplayName("Buscar por nombre - Encuentra resultados")
    void buscarPorNombre_EncuentraResultados() {
        // Arrange
        List<Ingrediente> ingredientes = Arrays.asList(ingredienteMock);
        when(ingredienteRepository.findByNombreContainingIgnoreCase("Poll")).thenReturn(ingredientes);

        // Act
        List<IngredienteResponse> responses = ingredienteService.buscarPorNombre("Poll");

        // Assert
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("Pollo", responses.get(0).getNombre());
    }

    @Test
    @DisplayName("Buscar por grupo - Encuentra resultados")
    void buscarPorGrupo_EncuentraResultados() {
        // Arrange
        List<Ingrediente> ingredientes = Arrays.asList(ingredienteMock);
        when(ingredienteRepository.findByGrupoAlimenticio(GrupoAlimenticio.PROTEINAS_ANIMALES))
                .thenReturn(ingredientes);

        // Act
        List<IngredienteResponse> responses = ingredienteService.buscarPorGrupo(GrupoAlimenticio.PROTEINAS_ANIMALES);

        // Assert
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(GrupoAlimenticio.PROTEINAS_ANIMALES, responses.get(0).getGrupoAlimenticio());
    }

    @Test
    @DisplayName("Listar todos - Devuelve lista")
    void listarTodos_DevuelveLista() {
        // Arrange
        List<Ingrediente> ingredientes = Arrays.asList(ingredienteMock);
        when(ingredienteRepository.findAll()).thenReturn(ingredientes);

        // Act
        List<IngredienteResponse> responses = ingredienteService.listarTodos();

        // Assert
        assertNotNull(responses);
        assertEquals(1, responses.size());
    }

    @Test
    @DisplayName("Agregar etiqueta - Ambos existen - Éxito")
    void agregarEtiqueta_AmbosExisten_Exito() {
        // Arrange
        when(ingredienteRepository.findById(1L)).thenReturn(Optional.of(ingredienteMock));
        when(etiquetaRepository.findById(1L)).thenReturn(Optional.of(etiquetaMock));
        when(ingredienteRepository.save(any(Ingrediente.class))).thenReturn(ingredienteMock);

        // Act
        IngredienteResponse response = ingredienteService.agregarEtiqueta(1L, 1L);

        // Assert
        assertNotNull(response);
        verify(ingredienteRepository).save(any(Ingrediente.class));
    }

    @Test
    @DisplayName("Agregar etiqueta - Ingrediente no existe - Lanza excepción")
    void agregarEtiqueta_IngredienteNoExiste_LanzaExcepcion() {
        // Arrange
        when(ingredienteRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> ingredienteService.agregarEtiqueta(999L, 1L));
        verify(ingredienteRepository, never()).save(any(Ingrediente.class));
    }

    @Test
    @DisplayName("Agregar etiqueta - Etiqueta no existe - Lanza excepción")
    void agregarEtiqueta_EtiquetaNoExiste_LanzaExcepcion() {
        // Arrange
        when(ingredienteRepository.findById(1L)).thenReturn(Optional.of(ingredienteMock));
        when(etiquetaRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> ingredienteService.agregarEtiqueta(1L, 999L));
        verify(ingredienteRepository, never()).save(any(Ingrediente.class));
    }

    @Test
    @DisplayName("Remover etiqueta - Ingrediente existe - Éxito")
    void removerEtiqueta_IngredienteExiste_Exito() {
        // Arrange
        ingredienteMock.getEtiquetas().add(etiquetaMock);
        when(ingredienteRepository.findById(1L)).thenReturn(Optional.of(ingredienteMock));
        when(ingredienteRepository.save(any(Ingrediente.class))).thenReturn(ingredienteMock);

        // Act
        IngredienteResponse response = ingredienteService.removerEtiqueta(1L, 1L);

        // Assert
        assertNotNull(response);
        verify(ingredienteRepository).save(any(Ingrediente.class));
    }

    @Test
    @DisplayName("Remover etiqueta - Ingrediente no existe - Lanza excepción")
    void removerEtiqueta_IngredienteNoExiste_LanzaExcepcion() {
        // Arrange
        when(ingredienteRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> ingredienteService.removerEtiqueta(999L, 1L));
        verify(ingredienteRepository, never()).save(any(Ingrediente.class));
    }
}
