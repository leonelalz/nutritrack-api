package com.example.nutritrackapi.controller;

import com.example.nutritrackapi.dto.ApiResponse;
import com.example.nutritrackapi.dto.TipoComidaRequest;
import com.example.nutritrackapi.dto.TipoComidaResponse;
import com.example.nutritrackapi.service.TipoComidaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestión de tipos de comida.
 * Permite agregar, modificar y eliminar tipos de comida dinámicamente.
 * Solo accesible por administradores (ROLE_ADMIN) para modificaciones.
 */
@RestController
@RequestMapping("/api/v1/tipos-comida")
@RequiredArgsConstructor
@Tag(name = "Módulo 2: Biblioteca de Contenido - Tipos de Comida", 
     description = "Gestión dinámica de tipos de comida (DESAYUNO, ALMUERZO, CENA, SNACK, MERIENDA, etc.)")
@SecurityRequirement(name = "bearerAuth")
public class TipoComidaController {

    private final TipoComidaService tipoComidaService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "🔐 ADMIN - Crear tipo de comida", 
               description = """
                   Crea un nuevo tipo de comida en el sistema.
                   
                   VALIDACIONES:
                   - Nombre único (case-insensitive)
                   - Nombre obligatorio (2-50 caracteres)
                   
                   EJEMPLO:
                   - nombre: "MERIENDA"
                   - descripcion: "Comida ligera entre comidas principales"
                   - ordenVisualizacion: 4
                   """)
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Tipo de comida creado exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos inválidos o nombre duplicado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "No autorizado - requiere rol ADMIN")
    })
    public ResponseEntity<ApiResponse<TipoComidaResponse>> crearTipoComida(
            @Valid @RequestBody TipoComidaRequest request
    ) {
        TipoComidaResponse tipoComida = tipoComidaService.crearTipoComida(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(tipoComida, "Tipo de comida creado exitosamente"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Obtener tipo de comida por ID", description = "Obtiene los detalles de un tipo de comida específico")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Tipo de comida encontrado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Tipo de comida no encontrado")
    })
    public ResponseEntity<ApiResponse<TipoComidaResponse>> obtenerTipoComida(
            @Parameter(description = "ID del tipo de comida") @PathVariable Long id
    ) {
        TipoComidaResponse tipoComida = tipoComidaService.obtenerTipoComidaPorId(id);
        return ResponseEntity.ok(ApiResponse.success(tipoComida, "Tipo de comida encontrado"));
    }

    @GetMapping("/nombre/{nombre}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Obtener tipo de comida por nombre", description = "Busca un tipo de comida por su nombre exacto")
    public ResponseEntity<ApiResponse<TipoComidaResponse>> obtenerTipoComidaPorNombre(
            @Parameter(description = "Nombre del tipo de comida", example = "DESAYUNO") @PathVariable String nombre
    ) {
        TipoComidaResponse tipoComida = tipoComidaService.obtenerTipoComidaPorNombre(nombre);
        return ResponseEntity.ok(ApiResponse.success(tipoComida, "Tipo de comida encontrado"));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Listar todos los tipos de comida activos", 
               description = "Obtiene una lista de todos los tipos de comida activos, ordenados por visualización")
    public ResponseEntity<ApiResponse<List<TipoComidaResponse>>> listarTiposComidaActivos() {
        List<TipoComidaResponse> tiposComida = tipoComidaService.listarTiposComidaActivos();
        return ResponseEntity.ok(ApiResponse.success(tiposComida, "Tipos de comida listados exitosamente"));
    }

    @GetMapping("/todos")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "🔐 ADMIN - Listar todos los tipos de comida", 
               description = "Obtiene una lista de TODOS los tipos de comida, incluyendo inactivos")
    public ResponseEntity<ApiResponse<List<TipoComidaResponse>>> listarTodosTiposComida() {
        List<TipoComidaResponse> tiposComida = tipoComidaService.listarTodosTiposComida();
        return ResponseEntity.ok(ApiResponse.success(tiposComida, "Todos los tipos de comida listados"));
    }

    @GetMapping("/paginado")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "🔐 ADMIN - Listar tipos de comida paginados", 
               description = "Obtiene una lista paginada de todos los tipos de comida")
    public ResponseEntity<ApiResponse<Page<TipoComidaResponse>>> listarTiposComidaPaginados(
            @ParameterObject @PageableDefault(size = 20) Pageable pageable
    ) {
        Page<TipoComidaResponse> tiposComida = tipoComidaService.listarTiposComida(pageable);
        return ResponseEntity.ok(ApiResponse.success(tiposComida, "Tipos de comida listados exitosamente"));
    }

    @GetMapping("/buscar")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "🔐 ADMIN - Buscar tipos de comida por nombre", 
               description = "Busca tipos de comida que contengan el texto especificado")
    public ResponseEntity<ApiResponse<List<TipoComidaResponse>>> buscarPorNombre(
            @Parameter(description = "Texto a buscar en el nombre") @RequestParam String nombre
    ) {
        List<TipoComidaResponse> tiposComida = tipoComidaService.buscarPorNombre(nombre);
        return ResponseEntity.ok(ApiResponse.success(tiposComida, "Búsqueda completada"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "🔐 ADMIN - Actualizar tipo de comida", 
               description = "Actualiza los datos de un tipo de comida existente")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Tipo de comida actualizado exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos inválidos o nombre duplicado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Tipo de comida no encontrado")
    })
    public ResponseEntity<ApiResponse<TipoComidaResponse>> actualizarTipoComida(
            @Parameter(description = "ID del tipo de comida") @PathVariable Long id,
            @Valid @RequestBody TipoComidaRequest request
    ) {
        TipoComidaResponse tipoComida = tipoComidaService.actualizarTipoComida(id, request);
        return ResponseEntity.ok(ApiResponse.success(tipoComida, "Tipo de comida actualizado exitosamente"));
    }

    @PatchMapping("/{id}/activar")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "🔐 ADMIN - Activar tipo de comida", description = "Activa un tipo de comida desactivado")
    public ResponseEntity<ApiResponse<TipoComidaResponse>> activarTipoComida(
            @Parameter(description = "ID del tipo de comida") @PathVariable Long id
    ) {
        TipoComidaResponse tipoComida = tipoComidaService.cambiarEstado(id, true);
        return ResponseEntity.ok(ApiResponse.success(tipoComida, "Tipo de comida activado exitosamente"));
    }

    @PatchMapping("/{id}/desactivar")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "🔐 ADMIN - Desactivar tipo de comida (soft delete)", 
               description = "Desactiva un tipo de comida. Las comidas/planes existentes mantienen la referencia.")
    public ResponseEntity<ApiResponse<TipoComidaResponse>> desactivarTipoComida(
            @Parameter(description = "ID del tipo de comida") @PathVariable Long id
    ) {
        TipoComidaResponse tipoComida = tipoComidaService.cambiarEstado(id, false);
        return ResponseEntity.ok(ApiResponse.success(tipoComida, "Tipo de comida desactivado exitosamente"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "🔐 ADMIN - Eliminar tipo de comida permanentemente", 
               description = """
                   ⚠️ ADVERTENCIA: Elimina permanentemente un tipo de comida.
                   
                   RECOMENDACIÓN: Usar desactivar en lugar de eliminar para mantener 
                   la integridad de comidas/planes existentes.
                   """)
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Tipo de comida eliminado exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Tipo de comida no encontrado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "No se puede eliminar - tipo de comida en uso")
    })
    public ResponseEntity<ApiResponse<Void>> eliminarTipoComida(
            @Parameter(description = "ID del tipo de comida") @PathVariable Long id
    ) {
        tipoComidaService.eliminarTipoComida(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Tipo de comida eliminado exitosamente"));
    }
}
