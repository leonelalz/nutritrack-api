package com.nutritrack.nutritrackapi.service;

import com.nutritrack.nutritrackapi.dto.request.RegistrarComidaRequest;
import com.nutritrack.nutritrackapi.dto.response.EstadisticasNutricionResponse;
import com.nutritrack.nutritrackapi.dto.response.RegistroComidaResponse;
import com.nutritrack.nutritrackapi.exception.BusinessRuleException;
import com.nutritrack.nutritrackapi.exception.ResourceNotFoundException;
import com.nutritrack.nutritrackapi.model.*;
import com.nutritrack.nutritrackapi.model.enums.EstadoAsignacion;
import com.nutritrack.nutritrackapi.model.enums.TipoComida;
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
@DisplayName("Tests del servicio RegistroComidaService")
class RegistroComidaServiceTest {

    @Mock
    private RegistroComidaRepository registroComidaRepository;

    @Mock
    private PerfilUsuarioRepository perfilUsuarioRepository;

    @Mock
    private ComidaRepository comidaRepository;

    @Mock
    private UsuarioPlanRepository usuarioPlanRepository;

    @Mock
    private RecetaRepository recetaRepository;

    @InjectMocks
    private RegistroComidaService registroComidaService;

    private PerfilUsuario perfilUsuarioMock;
    private Comida comidaMock;
    private UsuarioPlan usuarioPlanMock;
    private RegistroComida registroComidaMock;
    private RegistrarComidaRequest registrarComidaRequest;
    private Ingrediente ingrediente1;
    private Ingrediente ingrediente2;
    private Receta receta1;
    private Receta receta2;

    @BeforeEach
    void setUp() {
        perfilUsuarioMock = PerfilUsuario.builder()
                .id(1L)
                .nombre("Carlos")
                .apellido("López")
                .build();

        comidaMock = Comida.builder()
                .id(1L)
                .nombre("Ensalada César")
                .build();

        Plan plan = Plan.builder()
                .id(1L)
                .nombre("Plan de Mantenimiento")
                .build();

        usuarioPlanMock = UsuarioPlan.builder()
                .id(1L)
                .perfilUsuario(perfilUsuarioMock)
                .plan(plan)
                .estado(EstadoAsignacion.ACTIVO)
                .build();

        // Ingrediente 1: Pollo (165 kcal/100g)
        ingrediente1 = Ingrediente.builder()
                .id(1L)
                .nombre("Pechuga de pollo")
                .energia(new BigDecimal("165.00"))
                .build();

        // Ingrediente 2: Lechuga (15 kcal/100g)
        ingrediente2 = Ingrediente.builder()
                .id(2L)
                .nombre("Lechuga romana")
                .energia(new BigDecimal("15.00"))
                .build();

        // Receta 1: 150g de pollo = (165 * 150) / 100 = 247.5 kcal
        receta1 = Receta.builder()
                .idComida(1L)
                .idIngrediente(1L)
                .comida(comidaMock)
                .ingrediente(ingrediente1)
                .cantidadIngrediente(new BigDecimal("150.00"))
                .build();

        // Receta 2: 100g de lechuga = (15 * 100) / 100 = 15 kcal
        receta2 = Receta.builder()
                .idComida(1L)
                .idIngrediente(2L)
                .comida(comidaMock)
                .ingrediente(ingrediente2)
                .cantidadIngrediente(new BigDecimal("100.00"))
                .build();

        registroComidaMock = RegistroComida.builder()
                .id(1L)
                .perfilUsuario(perfilUsuarioMock)
                .comida(comidaMock)
                .usuarioPlan(usuarioPlanMock)
                .fecha(LocalDate.of(2025, 11, 3))
                .hora(LocalTime.of(12, 30))
                .tipoComida(TipoComida.ALMUERZO)
                .porciones(new BigDecimal("1.0"))
                .caloriasConsumidas(new BigDecimal("262.50"))
                .notas("Almuerzo saludable")
                .build();

        registrarComidaRequest = RegistrarComidaRequest.builder()
                .comidaId(1L)
                .usuarioPlanId(1L)
                .fecha(LocalDate.of(2025, 11, 3))
                .hora(LocalTime.of(12, 30))
                .tipoComida(TipoComida.ALMUERZO)
                .porciones(new BigDecimal("1.0"))
                .notas("Almuerzo saludable")
                .build();
    }

    @Test
    @DisplayName("Debe registrar comida correctamente")
    void debeRegistrarComidaCorrectamente() {
        when(perfilUsuarioRepository.findById(1L)).thenReturn(Optional.of(perfilUsuarioMock));
        when(comidaRepository.findById(1L)).thenReturn(Optional.of(comidaMock));
        when(usuarioPlanRepository.findById(1L)).thenReturn(Optional.of(usuarioPlanMock));
        when(recetaRepository.findByIdComida(1L)).thenReturn(Arrays.asList(receta1, receta2));
        when(registroComidaRepository.save(any(RegistroComida.class))).thenReturn(registroComidaMock);

        RegistroComidaResponse response = registroComidaService.registrarComida(1L, registrarComidaRequest);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Ensalada César", response.getNombreComida());
        assertEquals(TipoComida.ALMUERZO, response.getTipoComida());
        verify(registroComidaRepository).save(any(RegistroComida.class));
    }

    @Test
    @DisplayName("Debe calcular calorías correctamente para 1.0 porción")
    void debeCalcularCaloriasCorrectamente1Porcion() {
        when(perfilUsuarioRepository.findById(1L)).thenReturn(Optional.of(perfilUsuarioMock));
        when(comidaRepository.findById(1L)).thenReturn(Optional.of(comidaMock));
        when(usuarioPlanRepository.findById(1L)).thenReturn(Optional.of(usuarioPlanMock));
        when(recetaRepository.findByIdComida(1L)).thenReturn(Arrays.asList(receta1, receta2));
        when(registroComidaRepository.save(any(RegistroComida.class))).thenAnswer(invocation -> {
            RegistroComida registro = invocation.getArgument(0);
            // (165 * 150 / 100) + (15 * 100 / 100) = 247.5 + 15 = 262.5 kcal
            assertEquals(new BigDecimal("262.50"), registro.getCaloriasConsumidas());
            return registro;
        });

        registroComidaService.registrarComida(1L, registrarComidaRequest);

        verify(registroComidaRepository).save(any(RegistroComida.class));
    }

    @Test
    @DisplayName("Debe calcular calorías correctamente para 1.5 porciones")
    void debeCalcularCaloriasCorrectamente15Porciones() {
        registrarComidaRequest.setPorciones(new BigDecimal("1.5"));
        
        when(perfilUsuarioRepository.findById(1L)).thenReturn(Optional.of(perfilUsuarioMock));
        when(comidaRepository.findById(1L)).thenReturn(Optional.of(comidaMock));
        when(usuarioPlanRepository.findById(1L)).thenReturn(Optional.of(usuarioPlanMock));
        when(recetaRepository.findByIdComida(1L)).thenReturn(Arrays.asList(receta1, receta2));
        when(registroComidaRepository.save(any(RegistroComida.class))).thenAnswer(invocation -> {
            RegistroComida registro = invocation.getArgument(0);
            // 262.5 * 1.5 = 393.75 kcal
            assertEquals(new BigDecimal("393.75"), registro.getCaloriasConsumidas());
            return registro;
        });

        registroComidaService.registrarComida(1L, registrarComidaRequest);

        verify(registroComidaRepository).save(any(RegistroComida.class));
    }

    @Test
    @DisplayName("Debe registrar comida sin plan asignado")
    void debeRegistrarComidaSinPlan() {
        registrarComidaRequest.setUsuarioPlanId(null);
        
        when(perfilUsuarioRepository.findById(1L)).thenReturn(Optional.of(perfilUsuarioMock));
        when(comidaRepository.findById(1L)).thenReturn(Optional.of(comidaMock));
        when(recetaRepository.findByIdComida(1L)).thenReturn(Arrays.asList(receta1, receta2));
        
        // Create a registro mock without usuarioPlan
        RegistroComida registroSinPlan = RegistroComida.builder()
                .id(1L)
                .perfilUsuario(perfilUsuarioMock)
                .comida(comidaMock)
                .usuarioPlan(null)
                .fecha(LocalDate.of(2025, 11, 3))
                .hora(LocalTime.of(12, 30))
                .tipoComida(TipoComida.ALMUERZO)
                .porciones(new BigDecimal("1.0"))
                .caloriasConsumidas(new BigDecimal("262.50"))
                .notas("Almuerzo saludable")
                .build();
        
        when(registroComidaRepository.save(any(RegistroComida.class))).thenReturn(registroSinPlan);

        RegistroComidaResponse response = registroComidaService.registrarComida(1L, registrarComidaRequest);

        assertNotNull(response);
        assertNull(response.getUsuarioPlanId());
        verify(usuarioPlanRepository, never()).findById(any());
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el usuario no existe")
    void debeLanzarExcepcionCuandoUsuarioNoExiste() {
        when(perfilUsuarioRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, 
            () -> registroComidaService.registrarComida(1L, registrarComidaRequest));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando la comida no existe")
    void debeLanzarExcepcionCuandoComidaNoExiste() {
        when(perfilUsuarioRepository.findById(1L)).thenReturn(Optional.of(perfilUsuarioMock));
        when(comidaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, 
            () -> registroComidaService.registrarComida(1L, registrarComidaRequest));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el plan no pertenece al usuario")
    void debeLanzarExcepcionCuandoPlanNoPertenece() {
        PerfilUsuario otroUsuario = PerfilUsuario.builder().id(2L).build();
        usuarioPlanMock.setPerfilUsuario(otroUsuario);
        
        when(perfilUsuarioRepository.findById(1L)).thenReturn(Optional.of(perfilUsuarioMock));
        when(comidaRepository.findById(1L)).thenReturn(Optional.of(comidaMock));
        when(usuarioPlanRepository.findById(1L)).thenReturn(Optional.of(usuarioPlanMock));

        assertThrows(BusinessRuleException.class, 
            () -> registroComidaService.registrarComida(1L, registrarComidaRequest));
    }

    @Test
    @DisplayName("Debe listar registros por fecha")
    void debeListarRegistrosPorFecha() {
        when(registroComidaRepository.findByPerfilUsuarioIdAndFecha(1L, LocalDate.of(2025, 11, 3)))
                .thenReturn(Arrays.asList(registroComidaMock));

        List<RegistroComidaResponse> registros = registroComidaService.listarRegistrosPorFecha(1L, LocalDate.of(2025, 11, 3));

        assertNotNull(registros);
        assertEquals(1, registros.size());
        assertEquals("Ensalada César", registros.get(0).getNombreComida());
    }

    @Test
    @DisplayName("Debe listar registros por período")
    void debeListarRegistrosPorPeriodo() {
        when(registroComidaRepository.findByPerfilUsuarioIdAndFechaBetween(
                1L, LocalDate.of(2025, 11, 1), LocalDate.of(2025, 11, 7)))
                .thenReturn(Arrays.asList(registroComidaMock));

        List<RegistroComidaResponse> registros = registroComidaService.listarRegistrosPorPeriodo(
                1L, LocalDate.of(2025, 11, 1), LocalDate.of(2025, 11, 7));

        assertNotNull(registros);
        assertEquals(1, registros.size());
    }

    @Test
    @DisplayName("Debe buscar registro por ID correctamente")
    void debeBuscarRegistroPorId() {
        when(registroComidaRepository.findById(1L)).thenReturn(Optional.of(registroComidaMock));

        RegistroComidaResponse response = registroComidaService.buscarPorId(1L, 1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el registro no pertenece al usuario")
    void debeLanzarExcepcionCuandoRegistroNoPertenece() {
        when(registroComidaRepository.findById(1L)).thenReturn(Optional.of(registroComidaMock));

        assertThrows(BusinessRuleException.class, 
            () -> registroComidaService.buscarPorId(2L, 1L));
    }

    @Test
    @DisplayName("Debe eliminar registro correctamente")
    void debeEliminarRegistro() {
        when(registroComidaRepository.findById(1L)).thenReturn(Optional.of(registroComidaMock));

        assertDoesNotThrow(() -> registroComidaService.eliminarRegistro(1L, 1L));
        verify(registroComidaRepository).delete(registroComidaMock);
    }

    @Test
    @DisplayName("Debe obtener estadísticas correctamente")
    void debeObtenerEstadisticas() {
        LocalDate fechaInicio = LocalDate.of(2025, 11, 1);
        LocalDate fechaFin = LocalDate.of(2025, 11, 7);
        
        when(registroComidaRepository.sumCaloriasConsumidasByPeriodo(1L, fechaInicio, fechaFin))
                .thenReturn(new BigDecimal("1800.00"));
        when(registroComidaRepository.getEstadisticasPorTipoComida(1L, fechaInicio, fechaFin))
                .thenReturn(Arrays.asList(
                    new Object[]{TipoComida.DESAYUNO, 3L, new BigDecimal("600.00")},
                    new Object[]{TipoComida.ALMUERZO, 3L, new BigDecimal("900.00")},
                    new Object[]{TipoComida.CENA, 1L, new BigDecimal("300.00")}
                ));

        EstadisticasNutricionResponse response = registroComidaService.obtenerEstadisticas(1L, fechaInicio, fechaFin);

        assertNotNull(response);
        assertEquals(new BigDecimal("1800.00"), response.getCaloriasTotales());
        assertEquals(new BigDecimal("257.14"), response.getCaloriasPromedioDiario()); // 1800 / 7
        assertEquals(7, response.getTotalRegistros());
        assertEquals(3, response.getEstadisticasPorTipo().size());
    }

    @Test
    @DisplayName("Debe retornar 0 calorías cuando la comida no tiene ingredientes")
    void debeRetornar0CaloriasSinIngredientes() {
        when(perfilUsuarioRepository.findById(1L)).thenReturn(Optional.of(perfilUsuarioMock));
        when(comidaRepository.findById(1L)).thenReturn(Optional.of(comidaMock));
        when(recetaRepository.findByIdComida(1L)).thenReturn(Arrays.asList());
        when(registroComidaRepository.save(any(RegistroComida.class))).thenAnswer(invocation -> {
            RegistroComida registro = invocation.getArgument(0);
            assertEquals(0, registro.getCaloriasConsumidas().compareTo(BigDecimal.ZERO));
            return registro;
        });

        registrarComidaRequest.setUsuarioPlanId(null);
        registroComidaService.registrarComida(1L, registrarComidaRequest);

        verify(registroComidaRepository).save(any(RegistroComida.class));
    }
}
