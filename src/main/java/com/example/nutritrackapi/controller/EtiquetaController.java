package com.example.nutritrackapi.controller;

import com.example.nutritrackapi.dto.ApiResponse;
import com.example.nutritrackapi.dto.EtiquetaRequest;
import com.example.nutritrackapi.dto.EtiquetaResponse;
import com.example.nutritrackapi.service.EtiquetaService;
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
 * Controlador REST para gestión de etiquetas.
 * US-06: Gestionar Etiquetas
 * Solo accesible por administradores (ROLE_ADMIN)
 */
@RestController
@RequestMapping("/api/etiquetas")
@RequiredArgsConstructor
@Tag(name = "Etiquetas", description = "Gestión de etiquetas del sistema")
@SecurityRequirement(name = "bearerAuth")
public class EtiquetaController {

    private final EtiquetaService etiquetaService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crear nueva etiqueta", description = "Crea una nueva etiqueta en el sistema. Solo ADMIN.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Etiqueta creada exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos inválidos o nombre duplicado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "No autorizado - requiere rol ADMIN")
    })
    public ResponseEntity<ApiResponse<EtiquetaResponse>> crearEtiqueta(
            @Valid @RequestBody EtiquetaRequest request
    ) {
        EtiquetaResponse etiqueta = etiquetaService.crearEtiqueta(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(etiqueta, "Etiqueta creada exitosamente"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Obtener etiqueta por ID", description = "Obtiene los detalles de una etiqueta específica")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Etiqueta encontrada"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Etiqueta no encontrada")
    })
    public ResponseEntity<ApiResponse<EtiquetaResponse>> obtenerEtiqueta(
            @Parameter(description = "ID de la etiqueta") @PathVariable Long id
    ) {
        EtiquetaResponse etiqueta = etiquetaService.obtenerEtiquetaPorId(id);
        return ResponseEntity.ok(ApiResponse.success(etiqueta, "Etiqueta encontrada"));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Listar todas las etiquetas", description = "Obtiene una lista paginada de todas las etiquetas")
    public ResponseEntity<ApiResponse<Page<EtiquetaResponse>>> listarEtiquetas(
            @PageableDefault(size = 20) Pageable pageable
    ) {
        Page<EtiquetaResponse> etiquetas = etiquetaService.listarEtiquetas(pageable);
        return ResponseEntity.ok(ApiResponse.success(etiquetas, "Etiquetas listadas exitosamente"));
    }

    @GetMapping("/buscar")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Buscar etiquetas por nombre", description = "Busca etiquetas que contengan el texto especificado (case-insensitive)")
    public ResponseEntity<ApiResponse<Page<EtiquetaResponse>>> buscarPorNombre(
            @Parameter(description = "Texto a buscar en el nombre") @RequestParam String nombre,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        Page<EtiquetaResponse> etiquetas = etiquetaService.buscarPorNombre(nombre, pageable);
        return ResponseEntity.ok(ApiResponse.success(etiquetas, "Búsqueda completada"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualizar etiqueta", description = "Actualiza los datos de una etiqueta existente")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Etiqueta actualizada exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos inválidos o nombre duplicado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Etiqueta no encontrada")
    })
    public ResponseEntity<ApiResponse<EtiquetaResponse>> actualizarEtiqueta(
            @Parameter(description = "ID de la etiqueta") @PathVariable Long id,
            @Valid @RequestBody EtiquetaRequest request
    ) {
        EtiquetaResponse etiqueta = etiquetaService.actualizarEtiqueta(id, request);
        return ResponseEntity.ok(ApiResponse.success(etiqueta, "Etiqueta actualizada exitosamente"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar etiqueta", description = "Elimina una etiqueta. RN08: No permite eliminar si está en uso.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Etiqueta eliminada exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Etiqueta no encontrada"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "No se puede eliminar - etiqueta en uso")
    })
    public ResponseEntity<ApiResponse<Void>> eliminarEtiqueta(
            @Parameter(description = "ID de la etiqueta") @PathVariable Long id
    ) {
        etiquetaService.eliminarEtiqueta(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Etiqueta eliminada exitosamente"));
    }
}
