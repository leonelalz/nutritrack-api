package com.example.nutritrackapi.controller;

import com.example.nutritrackapi.dto.ApiResponse;
import com.example.nutritrackapi.dto.EjercicioRequest;
import com.example.nutritrackapi.dto.EjercicioResponse;
import com.example.nutritrackapi.model.Ejercicio;
import com.example.nutritrackapi.service.EjercicioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para gestión de ejercicios.
 * US-08: Gestionar Ejercicios
 * Solo accesible por administradores (ROLE_ADMIN)
 */
@RestController
@RequestMapping("/api/v1/ejercicios")
@RequiredArgsConstructor
@Tag(name = "Módulo 2: Biblioteca de Contenido - Ejercicios", description = "🔐 ADMIN - Gestión del catálogo de ejercicios (US-08) - Fabián Rojas. SOLO ADMINISTRADORES.")
@SecurityRequirement(name = "bearerAuth")
public class EjercicioController {

    private final EjercicioService ejercicioService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "🔐 ADMIN - US-08: Crear ejercicio [RN07, RN12]", 
               description = """
                   REGLAS DE NEGOCIO IMPLEMENTADAS:
                   - RN07: Ejercicios con nombre único (@Column unique=true)
                   - RN12: Solo permite asignar etiquetas existentes (FK constraint)
                   
                   UNIT TESTS: 9/9 ✅ en EjercicioServiceTest.java
                   - testCrearEjercicio_NombreDuplicado_Falla()
                   - testCrearEjercicio_EtiquetaInexistente_Falla()
                   
                   Ejecutar: ./mvnw test -Dtest=EjercicioServiceTest
                   """)
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Ejercicio creado exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos inválidos o nombre duplicado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "No autorizado - requiere rol ADMIN")
    })
    public ResponseEntity<ApiResponse<EjercicioResponse>> crearEjercicio(
            @Valid @RequestBody EjercicioRequest request
    ) {
        EjercicioResponse ejercicio = ejercicioService.crearEjercicio(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(ejercicio, "Ejercicio creado exitosamente"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "🔐 ADMIN - Obtener ejercicio por ID", description = "Obtiene los detalles de un ejercicio específico. SOLO ADMINISTRADORES.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Ejercicio encontrado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Ejercicio no encontrado")
    })
    public ResponseEntity<ApiResponse<EjercicioResponse>> obtenerEjercicio(
            @Parameter(description = "ID del ejercicio") @PathVariable Long id
    ) {
        EjercicioResponse ejercicio = ejercicioService.obtenerEjercicioPorId(id);
        return ResponseEntity.ok(ApiResponse.success(ejercicio, "Ejercicio encontrado"));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Listar todos los ejercicios", description = "Obtiene una lista paginada de todos los ejercicios")
    public ResponseEntity<ApiResponse<Page<EjercicioResponse>>> listarEjercicios(
            @ParameterObject @PageableDefault(size = 20) Pageable pageable
    ) {
        Page<EjercicioResponse> ejercicios = ejercicioService.listarEjercicios(pageable);
        return ResponseEntity.ok(ApiResponse.success(ejercicios, "Ejercicios listados exitosamente"));
    }

    @GetMapping("/buscar")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Buscar ejercicios por nombre", description = "Busca ejercicios que contengan el texto especificado (case-insensitive)")
    public ResponseEntity<ApiResponse<Page<EjercicioResponse>>> buscarPorNombre(
            @Parameter(description = "Texto a buscar en el nombre") @RequestParam String nombre,
            @ParameterObject @PageableDefault(size = 20) Pageable pageable
    ) {
        Page<EjercicioResponse> ejercicios = ejercicioService.buscarPorNombre(nombre, pageable);
        return ResponseEntity.ok(ApiResponse.success(ejercicios, "Búsqueda completada"));
    }

    @GetMapping("/filtrar")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Filtrar ejercicios", description = "Filtra ejercicios por tipo, grupo muscular y/o nivel de dificultad")
    public ResponseEntity<ApiResponse<Page<EjercicioResponse>>> filtrarEjercicios(
            @Parameter(description = "Tipo de ejercicio") @RequestParam(required = false) Ejercicio.TipoEjercicio tipo,
            @Parameter(description = "Grupo muscular") @RequestParam(required = false) Ejercicio.GrupoMuscular grupo,
            @Parameter(description = "Nivel de dificultad") @RequestParam(required = false) Ejercicio.NivelDificultad nivel,
            @ParameterObject @PageableDefault(size = 20) Pageable pageable
    ) {
        Page<EjercicioResponse> ejercicios = ejercicioService.filtrarEjercicios(tipo, grupo, nivel, pageable);
        return ResponseEntity.ok(ApiResponse.success(ejercicios, "Ejercicios filtrados exitosamente"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualizar ejercicio", description = "Actualiza los datos de un ejercicio existente")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Ejercicio actualizado exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos inválidos o nombre duplicado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Ejercicio no encontrado")
    })
    public ResponseEntity<ApiResponse<EjercicioResponse>> actualizarEjercicio(
            @Parameter(description = "ID del ejercicio") @PathVariable Long id,
            @Valid @RequestBody EjercicioRequest request
    ) {
        EjercicioResponse ejercicio = ejercicioService.actualizarEjercicio(id, request);
        return ResponseEntity.ok(ApiResponse.success(ejercicio, "Ejercicio actualizado exitosamente"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "🔐 ADMIN - US-08: Eliminar ejercicio [RN09]", 
               description = """
                   REGLAS DE NEGOCIO IMPLEMENTADAS:
                   - RN09: No permite eliminar ejercicios en uso en rutinas
                   
                   VALIDACIONES AUTOMÁTICAS:
                   1. Verifica si ejercicio está en tabla rutina_ejercicios
                   2. Rechaza eliminación si hay rutinas que lo usan
                   
                   UNIT TESTS: 9/9 ✅ en EjercicioServiceTest.java
                   - testEliminarEjercicio_EnUsoEnRutina_Falla()
                   - testEliminarEjercicio_SinUso_Exito()
                   """)
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Ejercicio eliminado exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Ejercicio no encontrado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "No se puede eliminar - ejercicio en uso")
    })
    public ResponseEntity<ApiResponse<Void>> eliminarEjercicio(
            @Parameter(description = "ID del ejercicio") @PathVariable Long id
    ) {
        ejercicioService.eliminarEjercicio(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Ejercicio eliminado exitosamente"));
    }
}
