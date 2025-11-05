package com.example.nutritrackapi.controller;

import com.example.nutritrackapi.dto.*;
import com.example.nutritrackapi.service.RutinaService;
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

import java.util.List;

/**
 * Controlador REST para gestión de rutinas de ejercicio.
 * US-11: Crear Meta del Catálogo (Rutina)
 * US-12: Gestionar Meta (configurar ejercicios)
 * US-13: Ver Catálogo de Metas (Admin)
 * US-14: Eliminar Meta
 * US-15: Ensamblar Rutinas
 * Solo accesible por administradores (ROLE_ADMIN)
 */
@RestController
@RequestMapping("/api/admin/rutinas")
@RequiredArgsConstructor
@Tag(name = "Módulo 3: Gestor de Catálogo - Rutinas de Ejercicio", 
     description = "Gestión de rutinas de ejercicio (US-11 a US-15) - Jhamil Peña")
@SecurityRequirement(name = "bearerAuth")
public class RutinaController {

    private final RutinaService rutinaService;

    /**
     * US-11: Crear rutina de ejercicio
     * RN11: Nombre único
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crear rutina de ejercicio", 
               description = "Crea una nueva rutina de ejercicio. RN11: Nombre debe ser único.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Rutina creada exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos inválidos o nombre duplicado (RN11)"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "No autorizado - requiere rol ADMIN")
    })
    public ResponseEntity<ApiResponse<RutinaResponse>> crearRutina(
            @Valid @RequestBody RutinaRequest request
    ) {
        RutinaResponse rutina = rutinaService.crearRutina(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(rutina, "Rutina creada exitosamente"));
    }

    /**
     * US-17: Obtener rutina por ID (para ver detalle)
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Obtener rutina por ID", 
               description = "Obtiene los detalles completos de una rutina")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Rutina encontrada"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Rutina no encontrada")
    })
    public ResponseEntity<ApiResponse<RutinaResponse>> obtenerRutina(
            @Parameter(description = "ID de la rutina") @PathVariable Long id
    ) {
        RutinaResponse rutina = rutinaService.obtenerRutinaPorId(id);
        return ResponseEntity.ok(ApiResponse.success(rutina, "Rutina encontrada"));
    }

    /**
     * US-13: Listar todas las rutinas (incluye inactivas para admin)
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Listar todas las rutinas (Admin)", 
               description = "Obtiene lista paginada de todas las rutinas incluyendo inactivas. Solo ADMIN.")
    public ResponseEntity<ApiResponse<Page<RutinaResponse>>> listarRutinasAdmin(
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        Page<RutinaResponse> rutinas = rutinaService.listarRutinasAdmin(pageable);
        return ResponseEntity.ok(ApiResponse.success(rutinas, "Rutinas listadas exitosamente"));
    }

    /**
     * Listar rutinas activas (para catálogo de clientes)
     * US-16: Ver Catálogo (se usará en módulo 4)
     */
    @GetMapping("/activas")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Listar rutinas activas", 
               description = "Obtiene solo las rutinas activas disponibles para asignar. RN28: Solo activo=true")
    public ResponseEntity<ApiResponse<Page<RutinaResponse>>> listarRutinasActivas(
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        Page<RutinaResponse> rutinas = rutinaService.listarRutinasActivas(pageable);
        return ResponseEntity.ok(ApiResponse.success(rutinas, "Rutinas activas listadas"));
    }

    /**
     * US-16: Ver Catálogo de Rutinas (CLIENTE)
     * RN15: Muestra rutinas sugeridas según objetivo
     * RN16: 🚨CRÍTICO - Filtra rutinas con ejercicios alérgenos
     */
    @GetMapping("/catalogo")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Ver catálogo de rutinas (Cliente)", 
               description = "US-16: Obtiene rutinas disponibles. RN15: Sugiere según objetivo. RN16: 🚨FILTRA ALÉRGENOS.")
    public ResponseEntity<ApiResponse<Page<RutinaResponse>>> verCatalogo(
            @Parameter(description = "ID del perfil usuario") @RequestParam Long perfilUsuarioId,
            @Parameter(description = "Filtrar solo rutinas sugeridas según objetivo") @RequestParam(required = false, defaultValue = "false") boolean sugeridos,
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        Page<RutinaResponse> rutinas = rutinaService.verCatalogo(perfilUsuarioId, sugeridos, pageable);
        return ResponseEntity.ok(ApiResponse.success(rutinas, "Catálogo de rutinas obtenido"));
    }

    /**
     * US-17: Ver Detalle de Rutina (CLIENTE)
     * RN16: 🚨CRÍTICO - Valida que la rutina no contenga alérgenos del usuario
     */
    @GetMapping("/catalogo/{id}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Ver detalle de rutina (Cliente)", 
               description = "US-17: Obtiene detalle de rutina validando alérgenos. RN16: 🚨SEGURIDAD SALUD")
    public ResponseEntity<ApiResponse<RutinaResponse>> verDetalleRutina(
            @Parameter(description = "ID de la rutina") @PathVariable Long id,
            @Parameter(description = "ID del perfil usuario") @RequestParam Long perfilUsuarioId
    ) {
        RutinaResponse rutina = rutinaService.verDetalleRutina(id, perfilUsuarioId);
        return ResponseEntity.ok(ApiResponse.success(rutina, "Detalle de rutina obtenido"));
    }

    /**
     * Buscar rutinas por nombre
     */
    @GetMapping("/buscar")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Buscar rutinas por nombre", 
               description = "Busca rutinas que contengan el texto especificado (case-insensitive)")
    public ResponseEntity<ApiResponse<Page<RutinaResponse>>> buscarPorNombre(
            @Parameter(description = "Texto a buscar en el nombre") @RequestParam String nombre,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        Page<RutinaResponse> rutinas = rutinaService.buscarPorNombre(nombre, pageable);
        return ResponseEntity.ok(ApiResponse.success(rutinas, "Búsqueda completada"));
    }

    /**
     * US-12: Actualizar rutina
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualizar rutina", 
               description = "Actualiza una rutina existente. RN11: Nombre debe ser único.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Rutina actualizada exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos inválidos o nombre duplicado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Rutina no encontrada")
    })
    public ResponseEntity<ApiResponse<RutinaResponse>> actualizarRutina(
            @Parameter(description = "ID de la rutina") @PathVariable Long id,
            @Valid @RequestBody RutinaRequest request
    ) {
        RutinaResponse rutina = rutinaService.actualizarRutina(id, request);
        return ResponseEntity.ok(ApiResponse.success(rutina, "Rutina actualizada exitosamente"));
    }

    /**
     * US-14: Eliminar rutina (soft delete)
     * RN14: No eliminar si tiene usuarios activos
     * RN28: Soft delete - marca como inactivo
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar rutina (soft delete)", 
               description = "Marca la rutina como inactiva. RN14: No permite eliminar si tiene usuarios activos. RN28: Soft delete.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Rutina eliminada exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Rutina no encontrada"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "No se puede eliminar - tiene usuarios activos (RN14)")
    })
    public ResponseEntity<ApiResponse<Void>> eliminarRutina(
            @Parameter(description = "ID de la rutina") @PathVariable Long id
    ) {
        rutinaService.eliminarRutina(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Rutina eliminada exitosamente"));
    }

    // ========== ENSAMBLAR RUTINAS (US-12, US-15) ==========

    /**
     * US-12, US-15: Agregar ejercicio a la rutina
     * RN13: Series y repeticiones positivas
     */
    @PostMapping("/{id}/ejercicios")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Agregar ejercicio a rutina", 
               description = "Agrega un ejercicio con configuración específica (series, reps, peso). RN13: Series y reps positivas.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Ejercicio agregado exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos inválidos o series/reps no positivas (RN13)"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Rutina o ejercicio no encontrado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Ya existe ejercicio en ese orden")
    })
    public ResponseEntity<ApiResponse<RutinaEjercicioResponse>> agregarEjercicioARutina(
            @Parameter(description = "ID de la rutina") @PathVariable Long id,
            @Valid @RequestBody RutinaEjercicioRequest request
    ) {
        RutinaEjercicioResponse ejercicio = rutinaService.agregarEjercicioARutina(id, request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(ejercicio, "Ejercicio agregado a la rutina"));
    }

    /**
     * US-17: Obtener todos los ejercicios de la rutina
     */
    @GetMapping("/{id}/ejercicios")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Obtener ejercicios de rutina", 
               description = "Lista todos los ejercicios programados en la rutina ordenados por orden de ejecución")
    public ResponseEntity<ApiResponse<List<RutinaEjercicioResponse>>> obtenerEjerciciosDeRutina(
            @Parameter(description = "ID de la rutina") @PathVariable Long id
    ) {
        List<RutinaEjercicioResponse> ejercicios = rutinaService.obtenerEjerciciosDeRutina(id);
        return ResponseEntity.ok(ApiResponse.success(ejercicios, "Ejercicios de la rutina obtenidos"));
    }

    /**
     * US-15: Actualizar ejercicio de la rutina
     */
    @PutMapping("/{rutinaId}/ejercicios/{ejercicioId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualizar ejercicio de rutina", 
               description = "Actualiza la configuración de un ejercicio en la rutina (series, reps, peso, etc.)")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Ejercicio actualizado exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos inválidos o ejercicio no pertenece a la rutina"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Ejercicio no encontrado")
    })
    public ResponseEntity<ApiResponse<RutinaEjercicioResponse>> actualizarEjercicioDeRutina(
            @Parameter(description = "ID de la rutina") @PathVariable Long rutinaId,
            @Parameter(description = "ID del ejercicio de rutina") @PathVariable Long ejercicioId,
            @Valid @RequestBody RutinaEjercicioRequest request
    ) {
        RutinaEjercicioResponse ejercicio = rutinaService.actualizarEjercicioDeRutina(rutinaId, ejercicioId, request);
        return ResponseEntity.ok(ApiResponse.success(ejercicio, "Ejercicio actualizado"));
    }

    /**
     * Eliminar ejercicio de la rutina
     */
    @DeleteMapping("/{rutinaId}/ejercicios/{ejercicioId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar ejercicio de rutina", 
               description = "Elimina un ejercicio de la rutina")
    public ResponseEntity<ApiResponse<Void>> eliminarEjercicioDeRutina(
            @Parameter(description = "ID de la rutina") @PathVariable Long rutinaId,
            @Parameter(description = "ID del ejercicio de rutina") @PathVariable Long ejercicioId
    ) {
        rutinaService.eliminarEjercicioDeRutina(rutinaId, ejercicioId);
        return ResponseEntity.ok(ApiResponse.success(null, "Ejercicio eliminado de la rutina"));
    }
}
