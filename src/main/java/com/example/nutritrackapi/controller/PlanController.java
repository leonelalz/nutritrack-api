package com.example.nutritrackapi.controller;

import com.example.nutritrackapi.dto.*;
import com.example.nutritrackapi.service.PlanService;
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
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestión de planes nutricionales.
 * US-11: Crear Meta del Catálogo (Plan)
 * US-12: Gestionar Meta (configurar días)
 * US-13: Ver Catálogo de Metas (Admin)
 * US-14: Eliminar Meta
 * Solo accesible por administradores (ROLE_ADMIN)
 */
@RestController
@RequestMapping("/api/v1/planes")
@RequiredArgsConstructor
@Tag(name = "Módulo 3: Gestor de Catálogo - Planes Nutricionales", 
     description = "🔐 ADMIN: Gestión completa | 👤 USER: Ver catálogo filtrado por perfil (US-11 a US-14) - Jhamil Peña")
@SecurityRequirement(name = "bearerAuth")
public class PlanController {

    private final PlanService planService;

    /**
     * US-11: Crear plan nutricional
     * RN11: Nombre único
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "🔐 ADMIN - US-11: Crear plan nutricional [RN11]", 
               description = """
                   REGLAS DE NEGOCIO IMPLEMENTADAS:
                   - RN11: Planes con nombre único en catálogo (@Column unique=true)
                   
                   UNIT TESTS: 22/22 ✅ en PlanServiceTest.java
                   - testCrearPlan_NombreDuplicado_Falla()
                   - testCrearPlan_NombreUnico_Exito()
                   
                   Ejecutar: ./mvnw test -Dtest=PlanServiceTest
                   """)
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Plan creado exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos inválidos o nombre duplicado (RN11)"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "No autorizado - requiere rol ADMIN")
    })
    public ResponseEntity<ApiResponse<PlanResponse>> crearPlan(
            @Valid @RequestBody PlanRequest request
    ) {
        PlanResponse plan = planService.crearPlan(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(plan, "Plan creado exitosamente"));
    }

    /**
     * US-17: Obtener plan por ID (para ver detalle)
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "🔐 ADMIN - Obtener plan por ID", 
               description = "Obtiene los detalles completos de un plan incluyendo objetivos nutricionales. SOLO ADMINISTRADORES.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Plan encontrado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Plan no encontrado")
    })
    public ResponseEntity<ApiResponse<PlanResponse>> obtenerPlan(
            @Parameter(description = "ID del plan") @PathVariable Long id
    ) {
        PlanResponse plan = planService.obtenerPlanPorId(id);
        return ResponseEntity.ok(ApiResponse.success(plan, "Plan encontrado"));
    }

    /**
     * US-13: Listar todos los planes (incluye inactivos para admin)
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "🔐 ADMIN - Listar todos los planes", 
               description = "Obtiene lista paginada de todos los planes incluyendo inactivos. SOLO ADMINISTRADORES.")
    public ResponseEntity<ApiResponse<Page<PlanResponse>>> listarPlanesAdmin(
            @ParameterObject @PageableDefault(size = 20) Pageable pageable
    ) {
        Page<PlanResponse> planes = planService.listarPlanesAdmin(pageable);
        return ResponseEntity.ok(ApiResponse.success(planes, "Planes listados exitosamente"));
    }

    /**
     * Listar planes activos (para catálogo de clientes)
     * US-16: Ver Catálogo (se usará en módulo 4)
     */
    @GetMapping("/activos")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "🔐 ADMIN - Listar planes activos", 
               description = "Obtiene solo los planes activos disponibles para asignar. RN28: Solo activo=true. SOLO ADMINISTRADORES.")
    public ResponseEntity<ApiResponse<Page<PlanResponse>>> listarPlanesActivos(
            @ParameterObject @PageableDefault(size = 20) Pageable pageable
    ) {
        Page<PlanResponse> planes = planService.listarPlanesActivos(pageable);
        return ResponseEntity.ok(ApiResponse.success(planes, "Planes activos listados"));
    }

    /**
     * US-16: Ver Catálogo de Planes (CLIENTE)
     * RN15: Muestra planes sugeridos según objetivo
     * RN16: 🚨CRÍTICO - Filtra planes con ingredientes alérgenos
     */
    @GetMapping("/catalogo")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "👤 USER - Ver catálogo de planes", 
               description = "US-16: Obtiene planes disponibles filtrados por perfil del usuario autenticado. RN15: Sugiere según objetivo. RN16: 🚨FILTRA ALÉRGENOS. SOLO USUARIOS REGULARES.")
    public ResponseEntity<ApiResponse<Page<PlanResponse>>> verCatalogo(
            Authentication authentication,
            @Parameter(description = "Filtrar solo planes sugeridos según objetivo") @RequestParam(required = false, defaultValue = "false") boolean sugeridos,
            @ParameterObject @PageableDefault(size = 20) Pageable pageable
    ) {
        Long perfilUsuarioId = planService.obtenerPerfilUsuarioId(authentication.getName());
        Page<PlanResponse> planes = planService.verCatalogo(perfilUsuarioId, sugeridos, pageable);
        return ResponseEntity.ok(ApiResponse.success(planes, "Catálogo de planes obtenido"));
    }

    /**
     * US-17: Ver Detalle del Plan (CLIENTE)
     * RN16: 🚨CRÍTICO - Valida que el plan no contenga alérgenos del usuario
     */
    @GetMapping("/catalogo/{id}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Ver detalle de plan (Cliente)", 
               description = "US-17: Obtiene detalle del plan validando alérgenos del usuario. RN16: 🚨SEGURIDAD SALUD")
    public ResponseEntity<ApiResponse<PlanResponse>> verDetallePlan(
            Authentication authentication,
            @Parameter(description = "ID del plan") @PathVariable Long id
    ) {
        Long perfilUsuarioId = planService.obtenerPerfilUsuarioId(authentication.getName());
        PlanResponse plan = planService.verDetallePlan(id, perfilUsuarioId);
        return ResponseEntity.ok(ApiResponse.success(plan, "Detalle del plan obtenido"));
    }

    /**
     * Buscar planes por nombre
     */
    @GetMapping("/buscar")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Buscar planes por nombre", 
               description = "Busca planes que contengan el texto especificado (case-insensitive)")
    public ResponseEntity<ApiResponse<Page<PlanResponse>>> buscarPorNombre(
            @Parameter(description = "Texto a buscar en el nombre") @RequestParam String nombre,
            @ParameterObject @PageableDefault(size = 20) Pageable pageable
    ) {
        Page<PlanResponse> planes = planService.buscarPorNombre(nombre, pageable);
        return ResponseEntity.ok(ApiResponse.success(planes, "Búsqueda completada"));
    }

    /**
     * US-12: Actualizar plan
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualizar plan", 
               description = "Actualiza un plan existente. RN11: Nombre debe ser único.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Plan actualizado exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos inválidos o nombre duplicado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Plan no encontrado")
    })
    public ResponseEntity<ApiResponse<PlanResponse>> actualizarPlan(
            @Parameter(description = "ID del plan") @PathVariable Long id,
            @Valid @RequestBody PlanRequest request
    ) {
        PlanResponse plan = planService.actualizarPlan(id, request);
        return ResponseEntity.ok(ApiResponse.success(plan, "Plan actualizado exitosamente"));
    }

    /**
     * US-14: Eliminar plan (soft delete)
     * RN14: No eliminar si tiene usuarios activos
     * RN28: Soft delete - marca como inactivo
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "🔐 ADMIN - US-14: Eliminar plan [RN14, RN28]", 
               description = """
                   REGLAS DE NEGOCIO IMPLEMENTADAS:
                   - RN14: No permite eliminar plan si tiene usuarios activos
                   - RN28: Soft delete - marca activo=false en lugar de DELETE
                   
                   VALIDACIONES AUTOMÁTICAS:
                   1. Verifica si plan tiene registros en usuario_planes con estado ACTIVO
                   2. Rechaza eliminación si hay usuarios activos
                   3. Si no hay usuarios, marca activo=false
                   
                   UNIT TESTS: 22/22 ✅ en PlanServiceTest.java
                   - testEliminarPlan_ConUsuariosActivos_Falla()
                   - testEliminarPlan_SinUsuarios_SoftDelete()
                   """)
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Plan eliminado exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Plan no encontrado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "No se puede eliminar - tiene usuarios activos (RN14)")
    })
    public ResponseEntity<ApiResponse<Void>> eliminarPlan(
            @Parameter(description = "ID del plan") @PathVariable Long id
    ) {
        planService.eliminarPlan(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Plan eliminado exitosamente"));
    }

    /**
     * Reactivar plan inactivo
     */
    @PatchMapping("/{id}/reactivar")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "🔐 ADMIN: Reactivar plan eliminado", 
               description = """
                   Reactiva un plan previamente marcado como inactivo (soft delete).
                   Permite reutilizar planes eliminados en lugar de crear duplicados.
                   
                   ✅ BENEFICIOS:
                   - Reutiliza configuraciones existentes
                   - Preserva historial y relaciones
                   - Evita duplicación de datos
                   
                   ⚠️ RESTRICCIONES:
                   - Solo funciona con planes inactivos (activo=false)
                   - Si el plan ya está activo → error 400 Bad Request
                   """)
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Plan reactivado exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Plan no encontrado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "El plan ya está activo")
    })
    public ResponseEntity<ApiResponse<PlanResponse>> reactivarPlan(
            @Parameter(description = "ID del plan a reactivar") @PathVariable Long id
    ) {
        PlanResponse plan = planService.reactivarPlan(id);
        return ResponseEntity.ok(ApiResponse.success(plan, "Plan reactivado exitosamente"));
    }

    // ========== GESTIÓN DE DÍAS DEL PLAN (US-12) ==========

    /**
     * US-12: Agregar actividad diaria al plan
     */
    @PostMapping("/{id}/dias")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Agregar día al plan", 
               description = "Programa una comida específica para un día del plan")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Día agregado exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos inválidos o día excede duración"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Plan o comida no encontrado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Ya existe comida para ese día y tipo")
    })
    public ResponseEntity<ApiResponse<PlanDiaResponse>> agregarDiaAPlan(
            @Parameter(description = "ID del plan") @PathVariable Long id,
            @Valid @RequestBody PlanDiaRequest request
    ) {
        PlanDiaResponse dia = planService.agregarDiaAPlan(id, request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(dia, "Actividad agregada al plan"));
    }

    /**
     * US-17: Obtener todas las actividades del plan
     */
    @GetMapping("/{id}/dias")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "🔐 ADMIN/USER - Obtener días del plan", 
               description = "Lista todas las actividades programadas del plan ordenadas por día y tipo. Accesible para administradores y usuarios.")
    public ResponseEntity<ApiResponse<List<PlanDiaResponse>>> obtenerDiasDePlan(
            @Parameter(description = "ID del plan") @PathVariable Long id
    ) {
        List<PlanDiaResponse> dias = planService.obtenerDiasDePlan(id);
        return ResponseEntity.ok(ApiResponse.success(dias, "Días del plan obtenidos"));
    }

    /**
     * US-21: Obtener actividades de un día específico
     */
    @GetMapping("/{id}/dias/{numeroDia}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "🔐 ADMIN/USER - Obtener actividades de un día", 
               description = "Lista las comidas programadas para un día específico del plan. Accesible para administradores y usuarios.")
    public ResponseEntity<ApiResponse<List<PlanDiaResponse>>> obtenerActividadesDia(
            @Parameter(description = "ID del plan") @PathVariable Long id,
            @Parameter(description = "Número de día (1, 2, 3...)") @PathVariable Integer numeroDia
    ) {
        List<PlanDiaResponse> actividades = planService.obtenerActividadesDia(id, numeroDia);
        return ResponseEntity.ok(ApiResponse.success(actividades, "Actividades del día obtenidas"));
    }

    /**
     * Eliminar actividad del plan
     */
    @DeleteMapping("/{planId}/dias/{diaId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar actividad del plan", 
               description = "Elimina una comida programada de un día específico")
    public ResponseEntity<ApiResponse<Void>> eliminarDiaDePlan(
            @Parameter(description = "ID del plan") @PathVariable Long planId,
            @Parameter(description = "ID de la actividad") @PathVariable Long diaId
    ) {
        planService.eliminarDiaDePlan(planId, diaId);
        return ResponseEntity.ok(ApiResponse.success(null, "Actividad eliminada del plan"));
    }
}
