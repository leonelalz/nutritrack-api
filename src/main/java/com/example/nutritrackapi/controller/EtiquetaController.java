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
 * Controlador REST para gestión de etiquetas.
 * US-06: Gestionar Etiquetas
 * Solo accesible por administradores (ROLE_ADMIN)
 */
@RestController
@RequestMapping("/api/v1/etiquetas")
@RequiredArgsConstructor
@Tag(name = "Módulo 2: Biblioteca de Contenido - Etiquetas", description = "🔐 ADMIN - Gestión del catálogo de etiquetas (US-06) - Fabián Rojas. SOLO ADMINISTRADORES.")
@SecurityRequirement(name = "bearerAuth")
public class EtiquetaController {

    private final EtiquetaService etiquetaService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "🔐 ADMIN - US-06: Crear etiqueta [RN06]", 
               description = """
                   REGLAS DE NEGOCIO IMPLEMENTADAS:
                   - RN06: Etiquetas con nombre único (@Column unique=true)
                   
                   UNIT TESTS: 12/12 ✅ en EtiquetaServiceTest.java
                   - testCrearEtiqueta_NombreDuplicado_Falla()
                   - testCrearEtiqueta_NombreUnico_Exito()
                   
                   Ejecutar: ./mvnw test -Dtest=EtiquetaServiceTest
                   """)
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
    @Operation(summary = "🔐 ADMIN - Obtener etiqueta por ID", description = "Obtiene los detalles de una etiqueta específica. SOLO ADMINISTRADORES.")
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
            @ParameterObject @PageableDefault(size = 20) Pageable pageable
    ) {
        Page<EtiquetaResponse> etiquetas = etiquetaService.listarEtiquetas(pageable);
        return ResponseEntity.ok(ApiResponse.success(etiquetas, "Etiquetas listadas exitosamente"));
    }

    @GetMapping("/buscar")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Buscar etiquetas por nombre", description = "Busca etiquetas que contengan el texto especificado (case-insensitive)")
    public ResponseEntity<ApiResponse<Page<EtiquetaResponse>>> buscarPorNombre(
            @Parameter(description = "Texto a buscar en el nombre") @RequestParam String nombre,
            @ParameterObject @PageableDefault(size = 20) Pageable pageable
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
    @Operation(summary = "🔐 ADMIN - US-06: Eliminar etiqueta [RN08]", 
               description = """
                   REGLAS DE NEGOCIO IMPLEMENTADAS:
                   - RN08: No permite eliminar etiquetas en uso
                   
                   VALIDACIONES AUTOMÁTICAS:
                   1. Verifica si etiqueta está en ingredientes (ingrediente_etiquetas)
                   2. Verifica si etiqueta está en ejercicios (ejercicio_etiquetas)
                   3. Verifica si etiqueta está en perfiles (usuario_etiquetas_salud)
                   4. Rechaza eliminación si hay referencias
                   
                   UNIT TESTS: 12/12 ✅ en EtiquetaServiceTest.java
                   - testEliminarEtiqueta_EnUsoEnIngrediente_Falla()
                   - testEliminarEtiqueta_EnUsoEnEjercicio_Falla()
                   - testEliminarEtiqueta_EnUsoEnPerfil_Falla()
                   """)
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
