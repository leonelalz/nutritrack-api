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
@RequestMapping("/api/ingredientes")
@RequiredArgsConstructor
@Tag(name = "Ingredientes", description = "Gestión de ingredientes del sistema")
@SecurityRequirement(name = "bearerAuth")
public class IngredienteController {

    private final IngredienteService ingredienteService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crear nuevo ingrediente", description = "Crea un nuevo ingrediente con información nutricional. Solo ADMIN.")
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
    @Operation(summary = "Obtener ingrediente por ID", description = "Obtiene los detalles de un ingrediente específico")
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
    @Operation(summary = "Listar todos los ingredientes", description = "Obtiene una lista paginada de todos los ingredientes")
    public ResponseEntity<ApiResponse<Page<IngredienteResponse>>> listarIngredientes(
            @PageableDefault(size = 20) Pageable pageable
    ) {
        Page<IngredienteResponse> ingredientes = ingredienteService.listarIngredientes(pageable);
        return ResponseEntity.ok(ApiResponse.success(ingredientes, "Ingredientes listados exitosamente"));
    }

    @GetMapping("/buscar")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Buscar ingredientes por nombre", description = "Busca ingredientes que contengan el texto especificado (case-insensitive)")
    public ResponseEntity<ApiResponse<Page<IngredienteResponse>>> buscarPorNombre(
            @Parameter(description = "Texto a buscar en el nombre") @RequestParam String nombre,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        Page<IngredienteResponse> ingredientes = ingredienteService.buscarPorNombre(nombre, pageable);
        return ResponseEntity.ok(ApiResponse.success(ingredientes, "Búsqueda completada"));
    }

    @GetMapping("/categoria/{categoria}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Filtrar por categoría", description = "Obtiene ingredientes de una categoría específica")
    public ResponseEntity<ApiResponse<Page<IngredienteResponse>>> filtrarPorCategoria(
            @Parameter(description = "Categoría del ingrediente") @PathVariable Ingrediente.CategoriaAlimento categoria,
            @PageableDefault(size = 20) Pageable pageable
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
    @Operation(summary = "Eliminar ingrediente", description = "Elimina un ingrediente. RN09: No permite eliminar si está en uso en recetas.")
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
