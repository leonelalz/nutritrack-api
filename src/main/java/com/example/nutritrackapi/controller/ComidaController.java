package com.example.nutritrackapi.controller;

import com.example.nutritrackapi.dto.AgregarIngredienteRequest;
import com.example.nutritrackapi.dto.ApiResponse;
import com.example.nutritrackapi.dto.ComidaRequest;
import com.example.nutritrackapi.dto.ComidaResponse;
import com.example.nutritrackapi.model.Comida;
import com.example.nutritrackapi.service.ComidaService;
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
 * Controlador REST para gestión de comidas y recetas.
 * US-09: Gestionar Comidas
 * US-10: Gestionar Recetas
 * Solo accesible por administradores (ROLE_ADMIN)
 */
@RestController
@RequestMapping("/api/comidas")
@RequiredArgsConstructor
@Tag(name = "Comidas", description = "Gestión de comidas y recetas del sistema")
@SecurityRequirement(name = "bearerAuth")
public class ComidaController {

    private final ComidaService comidaService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crear nueva comida", description = "Crea una nueva comida en el catálogo. Solo ADMIN.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Comida creada exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos inválidos o nombre duplicado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "No autorizado - requiere rol ADMIN")
    })
    public ResponseEntity<ApiResponse<ComidaResponse>> crearComida(
            @Valid @RequestBody ComidaRequest request
    ) {
        ComidaResponse comida = comidaService.crearComida(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(comida, "Comida creada exitosamente"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Obtener comida por ID", description = "Obtiene los detalles de una comida con su información nutricional calculada")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Comida encontrada"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Comida no encontrada")
    })
    public ResponseEntity<ApiResponse<ComidaResponse>> obtenerComida(
            @Parameter(description = "ID de la comida") @PathVariable Long id
    ) {
        ComidaResponse comida = comidaService.obtenerComidaPorId(id);
        return ResponseEntity.ok(ApiResponse.success(comida, "Comida encontrada"));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Listar todas las comidas", description = "Obtiene una lista paginada de todas las comidas")
    public ResponseEntity<ApiResponse<Page<ComidaResponse>>> listarComidas(
            @PageableDefault(size = 20) Pageable pageable
    ) {
        Page<ComidaResponse> comidas = comidaService.listarComidas(pageable);
        return ResponseEntity.ok(ApiResponse.success(comidas, "Comidas listadas exitosamente"));
    }

    @GetMapping("/buscar")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Buscar comidas por nombre", description = "Busca comidas que contengan el texto especificado (case-insensitive)")
    public ResponseEntity<ApiResponse<Page<ComidaResponse>>> buscarPorNombre(
            @Parameter(description = "Texto a buscar en el nombre") @RequestParam String nombre,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        Page<ComidaResponse> comidas = comidaService.buscarPorNombre(nombre, pageable);
        return ResponseEntity.ok(ApiResponse.success(comidas, "Búsqueda completada"));
    }

    @GetMapping("/tipo/{tipo}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Filtrar por tipo", description = "Obtiene comidas de un tipo específico (DESAYUNO, ALMUERZO, etc.)")
    public ResponseEntity<ApiResponse<Page<ComidaResponse>>> filtrarPorTipo(
            @Parameter(description = "Tipo de comida") @PathVariable Comida.TipoComida tipo,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        Page<ComidaResponse> comidas = comidaService.filtrarPorTipo(tipo, pageable);
        return ResponseEntity.ok(ApiResponse.success(comidas, "Comidas filtradas exitosamente"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualizar comida", description = "Actualiza los datos de una comida existente")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Comida actualizada exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos inválidos o nombre duplicado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Comida no encontrada")
    })
    public ResponseEntity<ApiResponse<ComidaResponse>> actualizarComida(
            @Parameter(description = "ID de la comida") @PathVariable Long id,
            @Valid @RequestBody ComidaRequest request
    ) {
        ComidaResponse comida = comidaService.actualizarComida(id, request);
        return ResponseEntity.ok(ApiResponse.success(comida, "Comida actualizada exitosamente"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar comida", description = "Elimina una comida del catálogo")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Comida eliminada exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Comida no encontrada")
    })
    public ResponseEntity<ApiResponse<Void>> eliminarComida(
            @Parameter(description = "ID de la comida") @PathVariable Long id
    ) {
        comidaService.eliminarComida(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Comida eliminada exitosamente"));
    }

    // ========== Gestión de Ingredientes de la Receta (US-10) ==========

    @PostMapping("/{comidaId}/ingredientes")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Agregar ingrediente a receta", description = "Agrega un ingrediente con su cantidad a una comida. RN10: Cantidad debe ser positiva.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Ingrediente agregado exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Ingrediente ya existe o cantidad inválida"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Comida o ingrediente no encontrado")
    })
    public ResponseEntity<ApiResponse<ComidaResponse>> agregarIngrediente(
            @Parameter(description = "ID de la comida") @PathVariable Long comidaId,
            @Valid @RequestBody AgregarIngredienteRequest request
    ) {
        ComidaResponse comida = comidaService.agregarIngrediente(comidaId, request);
        return ResponseEntity.ok(ApiResponse.success(comida, "Ingrediente agregado exitosamente"));
    }

    @PutMapping("/{comidaId}/ingredientes/{ingredienteId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualizar cantidad de ingrediente", description = "Modifica la cantidad de un ingrediente en la receta. RN10: Cantidad debe ser positiva.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cantidad actualizada exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Ingrediente no encontrado en esta comida")
    })
    public ResponseEntity<ApiResponse<ComidaResponse>> actualizarIngrediente(
            @Parameter(description = "ID de la comida") @PathVariable Long comidaId,
            @Parameter(description = "ID del ingrediente") @PathVariable Long ingredienteId,
            @Valid @RequestBody AgregarIngredienteRequest request
    ) {
        ComidaResponse comida = comidaService.actualizarIngrediente(comidaId, ingredienteId, request);
        return ResponseEntity.ok(ApiResponse.success(comida, "Ingrediente actualizado exitosamente"));
    }

    @DeleteMapping("/{comidaId}/ingredientes/{ingredienteId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar ingrediente de receta", description = "Quita un ingrediente de la comida")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Ingrediente eliminado exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Ingrediente no encontrado en esta comida")
    })
    public ResponseEntity<ApiResponse<ComidaResponse>> eliminarIngrediente(
            @Parameter(description = "ID de la comida") @PathVariable Long comidaId,
            @Parameter(description = "ID del ingrediente") @PathVariable Long ingredienteId
    ) {
        ComidaResponse comida = comidaService.eliminarIngrediente(comidaId, ingredienteId);
        return ResponseEntity.ok(ApiResponse.success(comida, "Ingrediente eliminado exitosamente"));
    }
}
