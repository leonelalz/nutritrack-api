package com.nutritrack.nutritrackapi.service;

import com.nutritrack.nutritrackapi.dto.request.CrearComidaRequest;
import com.nutritrack.nutritrackapi.dto.response.ComidaResponse;
import com.nutritrack.nutritrackapi.exception.DuplicateResourceException;
import com.nutritrack.nutritrackapi.exception.ResourceNotFoundException;
import com.nutritrack.nutritrackapi.model.Comida;
import com.nutritrack.nutritrackapi.model.Ingrediente;
import com.nutritrack.nutritrackapi.model.Receta;
import com.nutritrack.nutritrackapi.model.enums.GrupoAlimenticio;
import com.nutritrack.nutritrackapi.model.enums.TipoComida;
import com.nutritrack.nutritrackapi.repository.ComidaRepository;
import com.nutritrack.nutritrackapi.repository.IngredienteRepository;
import com.nutritrack.nutritrackapi.repository.RecetaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ComidaService - Unit Tests")
class ComidaServiceTest {

    @Mock
    private ComidaRepository comidaRepository;

    @Mock
    private IngredienteRepository ingredienteRepository;

    @Mock
    private RecetaRepository recetaRepository;

    @InjectMocks
    private ComidaService comidaService;

    private Comida comidaMock;
    private Ingrediente ingredienteMock;
    private Receta recetaMock;

    @BeforeEach
    void setUp() {
        comidaMock = Comida.builder()
                .id(1L)
                .nombre("Ensalada de Pollo")
                .tipoComida(TipoComida.ALMUERZO)
                .tiempoElaboracion(20)
                .build();

        ingredienteMock = Ingrediente.builder()
                .id(1L)
                .nombre("Pollo")
                .grupoAlimenticio(GrupoAlimenticio.PROTEINAS_ANIMALES)
                .energia(new BigDecimal("165.00"))
                .proteinas(new BigDecimal("31.00"))
                .grasas(new BigDecimal("3.60"))
                .carbohidratos(new BigDecimal("0.00"))
                .build();

        recetaMock = Receta.builder()
                .idComida(1L)
                .idIngrediente(1L)
                .cantidadIngrediente(new BigDecimal("150.00"))
                .comida(comidaMock)
                .ingrediente(ingredienteMock)
                .build();
    }

    @Test
    @DisplayName("Crear comida - Datos válidos sin ingredientes - Éxito")
    void crear_DatosValidosSinIngredientes_Exito() {
        // Arrange
        CrearComidaRequest request = CrearComidaRequest.builder()
                .nombre("Ensalada de Pollo")
                .tipoComida(TipoComida.ALMUERZO)
                .tiempoElaboracion(20)
                .build();

        when(comidaRepository.existsByNombreIgnoreCase("Ensalada de Pollo")).thenReturn(false);
        when(comidaRepository.save(any(Comida.class))).thenReturn(comidaMock);
        when(comidaRepository.findById(1L)).thenReturn(Optional.of(comidaMock));
        when(recetaRepository.findByIdComida(1L)).thenReturn(Collections.emptyList());

        // Act
        ComidaResponse response = comidaService.crear(request);

        // Assert
        assertNotNull(response);
        assertEquals("Ensalada de Pollo", response.getNombre());
        assertEquals(TipoComida.ALMUERZO, response.getTipoComida());
        verify(comidaRepository).save(any(Comida.class));
    }

    @Test
    @DisplayName("Crear comida - Con ingredientes - Crea recetas")
    void crear_ConIngredientes_CreaRecetas() {
        // Arrange
        List<CrearComidaRequest.IngredienteReceta> ingredientes = Arrays.asList(
                new CrearComidaRequest.IngredienteReceta(1L, new BigDecimal("150.00"))
        );

        CrearComidaRequest request = CrearComidaRequest.builder()
                .nombre("Ensalada de Pollo")
                .tipoComida(TipoComida.ALMUERZO)
                .tiempoElaboracion(20)
                .ingredientes(ingredientes)
                .build();

        when(comidaRepository.existsByNombreIgnoreCase("Ensalada de Pollo")).thenReturn(false);
        when(comidaRepository.save(any(Comida.class))).thenReturn(comidaMock);
        when(ingredienteRepository.findById(1L)).thenReturn(Optional.of(ingredienteMock));
        when(recetaRepository.save(any(Receta.class))).thenReturn(recetaMock);
        when(comidaRepository.findById(1L)).thenReturn(Optional.of(comidaMock));
        when(recetaRepository.findByIdComida(1L)).thenReturn(Arrays.asList(recetaMock));

        // Act
        ComidaResponse response = comidaService.crear(request);

        // Assert
        assertNotNull(response);
        verify(recetaRepository, atLeastOnce()).save(any(Receta.class));
    }

    @Test
    @DisplayName("Crear comida - Nombre duplicado - Lanza excepción")
    void crear_NombreDuplicado_LanzaExcepcion() {
        // Arrange
        CrearComidaRequest request = CrearComidaRequest.builder()
                .nombre("Ensalada de Pollo")
                .tipoComida(TipoComida.ALMUERZO)
                .build();

        when(comidaRepository.existsByNombreIgnoreCase("Ensalada de Pollo")).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateResourceException.class, () -> comidaService.crear(request));
        verify(comidaRepository, never()).save(any(Comida.class));
    }

    @Test
    @DisplayName("Crear comida - Ingrediente no existe - Lanza excepción")
    void crear_IngredienteNoExiste_LanzaExcepcion() {
        // Arrange
        List<CrearComidaRequest.IngredienteReceta> ingredientes = Arrays.asList(
                new CrearComidaRequest.IngredienteReceta(999L, new BigDecimal("150.00"))
        );

        CrearComidaRequest request = CrearComidaRequest.builder()
                .nombre("Ensalada de Pollo")
                .tipoComida(TipoComida.ALMUERZO)
                .ingredientes(ingredientes)
                .build();

        when(comidaRepository.existsByNombreIgnoreCase("Ensalada de Pollo")).thenReturn(false);
        when(comidaRepository.save(any(Comida.class))).thenReturn(comidaMock);
        when(ingredienteRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> comidaService.crear(request));
    }

    @Test
    @DisplayName("Actualizar comida - Datos válidos - Éxito")
    void actualizar_DatosValidos_Exito() {
        // Arrange
        CrearComidaRequest request = CrearComidaRequest.builder()
                .nombre("Ensalada Mediterránea")
                .tipoComida(TipoComida.CENA)
                .tiempoElaboracion(25)
                .build();

        when(comidaRepository.findById(1L)).thenReturn(Optional.of(comidaMock));
        when(comidaRepository.existsByNombreIgnoreCase("Ensalada Mediterránea")).thenReturn(false);
        when(comidaRepository.save(any(Comida.class))).thenReturn(comidaMock);
        when(recetaRepository.findByIdComida(1L)).thenReturn(Collections.emptyList());

        // Act
        ComidaResponse response = comidaService.actualizar(1L, request);

        // Assert
        assertNotNull(response);
        verify(comidaRepository).save(any(Comida.class));
        verify(recetaRepository).deleteByIdComida(1L);
    }

    @Test
    @DisplayName("Actualizar comida - No existe - Lanza excepción")
    void actualizar_NoExiste_LanzaExcepcion() {
        // Arrange
        CrearComidaRequest request = CrearComidaRequest.builder()
                .nombre("Ensalada de Pollo")
                .tipoComida(TipoComida.ALMUERZO)
                .build();

        when(comidaRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> comidaService.actualizar(999L, request));
        verify(comidaRepository, never()).save(any(Comida.class));
    }

    @Test
    @DisplayName("Eliminar comida - Existe - Elimina recetas y comida")
    void eliminar_Existe_EliminaRecetasYComida() {
        // Arrange
        when(comidaRepository.existsById(1L)).thenReturn(true);

        // Act
        comidaService.eliminar(1L);

        // Assert
        verify(recetaRepository).deleteByIdComida(1L);
        verify(comidaRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Eliminar comida - No existe - Lanza excepción")
    void eliminar_NoExiste_LanzaExcepcion() {
        // Arrange
        when(comidaRepository.existsById(999L)).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> comidaService.eliminar(999L));
        verify(comidaRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Buscar por ID - Existe - Devuelve comida con nutrición total")
    void buscarPorId_Existe_DevuelveComidaConNutricion() {
        // Arrange
        when(comidaRepository.findById(1L)).thenReturn(Optional.of(comidaMock));
        when(recetaRepository.findByIdComida(1L)).thenReturn(Arrays.asList(recetaMock));

        // Act
        ComidaResponse response = comidaService.buscarPorId(1L);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertNotNull(response.getNutricionTotal());
        assertNotNull(response.getNutricionTotal().getEnergiaTotal());
    }

    @Test
    @DisplayName("Buscar por ID - No existe - Lanza excepción")
    void buscarPorId_NoExiste_LanzaExcepcion() {
        // Arrange
        when(comidaRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> comidaService.buscarPorId(999L));
    }

    @Test
    @DisplayName("Buscar por nombre - Encuentra resultados")
    void buscarPorNombre_EncuentraResultados() {
        // Arrange
        List<Comida> comidas = Arrays.asList(comidaMock);
        when(comidaRepository.findByNombreContainingIgnoreCase("Ensalada")).thenReturn(comidas);
        when(recetaRepository.findByIdComida(1L)).thenReturn(Collections.emptyList());

        // Act
        List<ComidaResponse> responses = comidaService.buscarPorNombre("Ensalada");

        // Assert
        assertNotNull(responses);
        assertEquals(1, responses.size());
    }

    @Test
    @DisplayName("Buscar por tipo - Encuentra resultados")
    void buscarPorTipo_EncuentraResultados() {
        // Arrange
        List<Comida> comidas = Arrays.asList(comidaMock);
        when(comidaRepository.findByTipoComida(TipoComida.ALMUERZO)).thenReturn(comidas);
        when(recetaRepository.findByIdComida(1L)).thenReturn(Collections.emptyList());

        // Act
        List<ComidaResponse> responses = comidaService.buscarPorTipo(TipoComida.ALMUERZO);

        // Assert
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(TipoComida.ALMUERZO, responses.get(0).getTipoComida());
    }

    @Test
    @DisplayName("Listar todos - Devuelve lista")
    void listarTodos_DevuelveLista() {
        // Arrange
        List<Comida> comidas = Arrays.asList(comidaMock);
        when(comidaRepository.findAll()).thenReturn(comidas);
        when(recetaRepository.findByIdComida(1L)).thenReturn(Collections.emptyList());

        // Act
        List<ComidaResponse> responses = comidaService.listarTodos();

        // Assert
        assertNotNull(responses);
        assertEquals(1, responses.size());
    }

    @Test
    @DisplayName("Agregar ingrediente - Ambos existen - Éxito")
    void agregarIngrediente_AmbosExisten_Exito() {
        // Arrange
        when(comidaRepository.findById(1L)).thenReturn(Optional.of(comidaMock));
        when(ingredienteRepository.findById(1L)).thenReturn(Optional.of(ingredienteMock));
        when(recetaRepository.save(any(Receta.class))).thenReturn(recetaMock);
        when(recetaRepository.findByIdComida(1L)).thenReturn(Arrays.asList(recetaMock));

        // Act
        ComidaResponse response = comidaService.agregarIngrediente(1L, 1L, new BigDecimal("150.00"));

        // Assert
        assertNotNull(response);
        verify(recetaRepository).save(any(Receta.class));
    }

    @Test
    @DisplayName("Agregar ingrediente - Comida no existe - Lanza excepción")
    void agregarIngrediente_ComidaNoExiste_LanzaExcepcion() {
        // Arrange
        when(comidaRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, 
                () -> comidaService.agregarIngrediente(999L, 1L, new BigDecimal("150.00")));
        verify(recetaRepository, never()).save(any(Receta.class));
    }

    @Test
    @DisplayName("Agregar ingrediente - Ingrediente no existe - Lanza excepción")
    void agregarIngrediente_IngredienteNoExiste_LanzaExcepcion() {
        // Arrange
        when(comidaRepository.findById(1L)).thenReturn(Optional.of(comidaMock));
        when(ingredienteRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, 
                () -> comidaService.agregarIngrediente(1L, 999L, new BigDecimal("150.00")));
        verify(recetaRepository, never()).save(any(Receta.class));
    }

    @Test
    @DisplayName("Remover ingrediente - Comida existe - Éxito")
    void removerIngrediente_ComidaExiste_Exito() {
        // Arrange
        when(comidaRepository.existsById(1L)).thenReturn(true);
        when(comidaRepository.findById(1L)).thenReturn(Optional.of(comidaMock));
        when(recetaRepository.findByIdComida(1L)).thenReturn(Collections.emptyList());

        // Act
        ComidaResponse response = comidaService.removerIngrediente(1L, 1L);

        // Assert
        assertNotNull(response);
        verify(recetaRepository).deleteByIdComidaAndIdIngrediente(1L, 1L);
    }

    @Test
    @DisplayName("Remover ingrediente - Comida no existe - Lanza excepción")
    void removerIngrediente_ComidaNoExiste_LanzaExcepcion() {
        // Arrange
        when(comidaRepository.existsById(999L)).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> comidaService.removerIngrediente(999L, 1L));
        verify(recetaRepository, never()).deleteByIdComidaAndIdIngrediente(anyLong(), anyLong());
    }

    @Test
    @DisplayName("Calcular nutrición total - Con múltiples ingredientes - Suma correctamente")
    void calcularNutricionTotal_ConMultiplesIngredientes_SumaCorrectamente() {
        // Arrange
        Ingrediente ingrediente2 = Ingrediente.builder()
                .id(2L)
                .nombre("Lechuga")
                .energia(new BigDecimal("15.00"))
                .proteinas(new BigDecimal("1.40"))
                .grasas(new BigDecimal("0.20"))
                .carbohidratos(new BigDecimal("2.90"))
                .build();

        Receta receta2 = Receta.builder()
                .idComida(1L)
                .idIngrediente(2L)
                .cantidadIngrediente(new BigDecimal("100.00"))
                .ingrediente(ingrediente2)
                .build();

        when(comidaRepository.findById(1L)).thenReturn(Optional.of(comidaMock));
        when(recetaRepository.findByIdComida(1L)).thenReturn(Arrays.asList(recetaMock, receta2));

        // Act
        ComidaResponse response = comidaService.buscarPorId(1L);

        // Assert
        assertNotNull(response.getNutricionTotal());
        assertTrue(response.getNutricionTotal().getEnergiaTotal().compareTo(BigDecimal.ZERO) > 0);
        assertTrue(response.getNutricionTotal().getProteinasTotal().compareTo(BigDecimal.ZERO) > 0);
    }
}
