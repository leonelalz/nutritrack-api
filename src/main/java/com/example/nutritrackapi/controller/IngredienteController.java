package com.example.nutritrackapi.controller;

import com.example.nutritrackapi.dto.ApiResponse;
import com.example.nutritrackapi.dto.IngredienteRequest;
import com.example.nutritrackapi.dto.IngredienteResponse;
import com.example.nutritrackapi.model.Ingrediente;
import com.example.nutritrackapi.service.IngredienteService;
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
 * Controlador REST para gestión de ingredientes.
 * US-07: Gestionar Ingredientes
 * Solo accesible por administradores (ROLE_ADMIN)
 */
@RestController
@RequestMapping("/api/v1/ingredientes")
@RequiredArgsConstructor
@Tag(name = "Módulo 2: Biblioteca de Contenido - Ingredientes", description = "🔐 ADMIN - Gestión del catálogo de ingredientes (US-07) - Fabián Rojas. SOLO ADMINISTRADORES.")
@SecurityRequirement(name = "bearerAuth")
public class IngredienteController {

    private final IngredienteService ingredienteService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "🔐 ADMIN - US-07: Crear ingrediente [RN07, RN12]", 
               description = """
                   REGLAS DE NEGOCIO IMPLEMENTADAS:
                   - RN07: Ingredientes con nombre único (@Column unique=true)
                   - RN12: Solo permite asignar etiquetas existentes (FK constraint)
                   
                   UNIT TESTS: 9/9 ✅ en IngredienteServiceTest.java
                   - testCrearIngrediente_NombreDuplicado_Falla()
                   - testCrearIngrediente_EtiquetaInexistente_Falla()
                   
                   Ejecutar: ./mvnw test -Dtest=IngredienteServiceTest
                   """)
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Ingrediente creado exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos inválidos o nombre duplicado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "No autorizado - requiere rol ADMIN")
    })
    public ResponseEntity<ApiResponse<IngredienteResponse>> crearIngrediente(
            @Valid @RequestBody IngredienteRequest request
    ) {
        IngredienteResponse ingrediente = ingredienteService.crearIngrediente(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(ingrediente, "Ingrediente creado exitosamente"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "🔐 ADMIN - Obtener ingrediente por ID", description = "Obtiene los detalles de un ingrediente específico. SOLO ADMINISTRADORES.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Ingrediente encontrado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Ingrediente no encontrado")
    })
    public ResponseEntity<ApiResponse<IngredienteResponse>> obtenerIngrediente(
            @Parameter(description = "ID del ingrediente") @PathVariable Long id
    ) {
        IngredienteResponse ingrediente = ingredienteService.obtenerIngredientePorId(id);
        return ResponseEntity.ok(ApiResponse.success(ingrediente, "Ingrediente encontrado"));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "🔐 ADMIN - Listar ingredientes", description = "Obtiene una lista paginada de todos los ingredientes. SOLO ADMINISTRADORES.")
    public ResponseEntity<ApiResponse<Page<IngredienteResponse>>> listarIngredientes(
            @ParameterObject @PageableDefault(size = 20) Pageable pageable
    ) {
        Page<IngredienteResponse> ingredientes = ingredienteService.listarIngredientes(pageable);
        return ResponseEntity.ok(ApiResponse.success(ingredientes, "Ingredientes listados exitosamente"));
    }

    @GetMapping("/buscar")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Buscar ingredientes por nombre", description = "Busca ingredientes que contengan el texto especificado (case-insensitive)")
    public ResponseEntity<ApiResponse<Page<IngredienteResponse>>> buscarPorNombre(
            @Parameter(description = "Texto a buscar en el nombre") @RequestParam String nombre,
            @ParameterObject @PageableDefault(size = 20) Pageable pageable
    ) {
        Page<IngredienteResponse> ingredientes = ingredienteService.buscarPorNombre(nombre, pageable);
        return ResponseEntity.ok(ApiResponse.success(ingredientes, "Búsqueda completada"));
    }

    @GetMapping("/categoria/{categoria}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Filtrar por categoría", description = "Obtiene ingredientes de una categoría específica")
    public ResponseEntity<ApiResponse<Page<IngredienteResponse>>> filtrarPorCategoria(
            @Parameter(description = "Categoría del ingrediente") @PathVariable Ingrediente.CategoriaAlimento categoria,
            @ParameterObject @PageableDefault(size = 20) Pageable pageable
    ) {
        Page<IngredienteResponse> ingredientes = ingredienteService.filtrarPorCategoria(categoria, pageable);
        return ResponseEntity.ok(ApiResponse.success(ingredientes, "Ingredientes filtrados exitosamente"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualizar ingrediente", description = "Actualiza los datos de un ingrediente existente")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Ingrediente actualizado exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos inválidos o nombre duplicado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Ingrediente no encontrado")
    })
    public ResponseEntity<ApiResponse<IngredienteResponse>> actualizarIngrediente(
            @Parameter(description = "ID del ingrediente") @PathVariable Long id,
            @Valid @RequestBody IngredienteRequest request
    ) {
        IngredienteResponse ingrediente = ingredienteService.actualizarIngrediente(id, request);
        return ResponseEntity.ok(ApiResponse.success(ingrediente, "Ingrediente actualizado exitosamente"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "🔐 ADMIN - US-07: Eliminar ingrediente [RN09]", 
               description = """
                   REGLAS DE NEGOCIO IMPLEMENTADAS:
                   - RN09: No permite eliminar ingredientes en uso en recetas
                   
                   VALIDACIONES AUTOMÁTICAS:
                   1. Verifica si ingrediente está en tabla comida_ingredientes
                   2. Rechaza eliminación si hay comidas que lo usan
                   
                   UNIT TESTS: 9/9 ✅ en IngredienteServiceTest.java
                   - testEliminarIngrediente_EnUsoEnComida_Falla()
                   - testEliminarIngrediente_SinUso_Exito()
                   """)
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Ingrediente eliminado exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Ingrediente no encontrado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "No se puede eliminar - ingrediente en uso")
    })
    public ResponseEntity<ApiResponse<Void>> eliminarIngrediente(
            @Parameter(description = "ID del ingrediente") @PathVariable Long id
    ) {
        ingredienteService.eliminarIngrediente(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Ingrediente eliminado exitosamente"));
    }
}
