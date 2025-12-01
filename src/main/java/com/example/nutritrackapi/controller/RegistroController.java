package com.example.nutritrackapi.controller;

import com.example.nutritrackapi.dto.*;
import com.example.nutritrackapi.model.CuentaAuth;
import com.example.nutritrackapi.model.RegistroComida;
import com.example.nutritrackapi.repository.CuentaAuthRepository;
import com.example.nutritrackapi.repository.PerfilUsuarioRepository;
import com.example.nutritrackapi.repository.RegistroComidaRepository;
import com.example.nutritrackapi.service.RegistroService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Controlador para gestión de registros de actividades.
 * Módulo 5: US-21, US-22, US-23
 */
@RestController
@RequestMapping("/api/v1/usuario/registros")
@RequiredArgsConstructor
@Tag(name = "Módulo 5: Tracking de Actividades", description = "👤 USER - Registro y seguimiento de comidas y ejercicios diarios (US-21, US-22, US-23). SOLO USUARIOS REGULARES.")
@SecurityRequirement(name = "bearerAuth")
public class RegistroController {

    private final RegistroService registroService;
    private final CuentaAuthRepository cuentaAuthRepository;
    private final PerfilUsuarioRepository perfilUsuarioRepository;
    private final RegistroComidaRepository registroComidaRepository;
    // ============================================================
    // US-22: Registrar Comidas y Ejercicios
    // ============================================================

    @PostMapping("/comidas")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "👤 USER - US-22: Registrar comida [RN20, RN21]", 
               description = """
                   REGLAS DE NEGOCIO IMPLEMENTADAS:
                   - RN20: Mostrar checks ✅ en actividades diarias
                   - RN21: No permite marcar si plan está pausado
                   
                   VALIDACIONES AUTOMÁTICAS:
                   1. Usuario debe tener plan ACTIVO
                   2. Plan no debe estar en estado PAUSADO
                   3. Se marca con timestamp de registro
                   
                   UNIT TESTS: 1/1 ✅ en UsuarioPlanServiceTest.java
                   - testRegistrarComida_PlanPausado_Falla()
                   """)
    public ResponseEntity<RegistroComidaResponse> registrarComida(
            @Valid @RequestBody RegistroComidaRequest request,
            Authentication authentication) {
        Long perfilUsuarioId = obtenerPerfilUsuarioId(authentication);
        RegistroComidaResponse response = registroService.registrarComida(perfilUsuarioId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/comidas/hoy")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<RegistroComidaResponse>> obtenerRegistrosHoy(
            Authentication authentication) {

        Long perfilUsuarioId = obtenerPerfilUsuarioId(authentication);
        LocalDate hoy = LocalDate.now();

        // Obtener registros reales del día
        List<RegistroComida> registros = registroComidaRepository
                .findByPerfilUsuarioIdAndFecha(perfilUsuarioId, hoy);

        // Mapear a DTO usando el método estático
        List<RegistroComidaResponse> resp = registros.stream()
                .map(RegistroComidaResponse::fromEntity)
                .toList();

        return ResponseEntity.ok(resp);
    }


    @PutMapping("/comidas/{registroId}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "👤 USER - Actualizar registro de comida")
    public ResponseEntity<RegistroComidaResponse> actualizarRegistroComida(
            @PathVariable Long registroId,
            @Valid @RequestBody RegistroComidaRequest request,
            Authentication authentication
    ) {
        Long perfilUsuarioId = obtenerPerfilUsuarioId(authentication);
        RegistroComidaResponse response = registroService.actualizarRegistroComida(
                perfilUsuarioId, registroId, request
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/ejercicios")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "👤 USER - Registrar ejercicio realizado", description = "US-22: Marcar ejercicio como completado. SOLO USUARIOS REGULARES.")
    public ResponseEntity<RegistroEjercicioResponse> registrarEjercicio(
            @Valid @RequestBody RegistroEjercicioRequest request,
            Authentication authentication) {
        Long perfilUsuarioId = obtenerPerfilUsuarioId(authentication);
        RegistroEjercicioResponse response = registroService.registrarEjercicio(perfilUsuarioId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ============================================================
    // US-21: Ver Actividades del Día
    // ============================================================

    @GetMapping("/plan/hoy")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "👤 USER - US-21: Ver actividades del plan [RN20, RN23]", 
               description = """
                   REGLAS DE NEGOCIO IMPLEMENTADAS:
                   - RN20: Muestra checks ✅ en actividades completadas
                   - RN23: Gráfico requiere mínimo 2 registros (para tracking)
                   
                   INFORMACIÓN RETORNADA:
                   1. Comidas programadas para el día actual
                   2. Estado de completitud (check ✅ si registrada)
                   3. Información nutricional (calorías, proteínas, etc.)
                   """)
    public ResponseEntity<ActividadesDiaResponse> obtenerActividadesHoy(Authentication authentication) {
        Long perfilUsuarioId = obtenerPerfilUsuarioId(authentication);
        ActividadesDiaResponse response = registroService.obtenerActividadesDia(perfilUsuarioId, LocalDate.now());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/plan/dia")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "👤 USER - Ver actividades del plan en una fecha", description = "US-21: Obtener comidas de un día específico. SOLO USUARIOS REGULARES.")
    public ResponseEntity<ActividadesDiaResponse> obtenerActividadesDia(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            Authentication authentication) {
        Long perfilUsuarioId = obtenerPerfilUsuarioId(authentication);
        ActividadesDiaResponse response = registroService.obtenerActividadesDia(perfilUsuarioId, fecha);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/rutina/hoy")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "👤 USER - Ver ejercicios de la rutina de hoy", description = "US-21: Obtener ejercicios programados y su estado. SOLO USUARIOS REGULARES.")
    public ResponseEntity<EjerciciosDiaResponse> obtenerEjerciciosHoy(Authentication authentication) {
        Long perfilUsuarioId = obtenerPerfilUsuarioId(authentication);
        EjerciciosDiaResponse response = registroService.obtenerEjerciciosDia(perfilUsuarioId, LocalDate.now());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/rutina/dia")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "👤 USER - Ver ejercicios de la rutina en una fecha", description = "US-21: Obtener ejercicios de un día específico. SOLO USUARIOS REGULARES.")
    public ResponseEntity<EjerciciosDiaResponse> obtenerEjerciciosDia(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            Authentication authentication) {
        Long perfilUsuarioId = obtenerPerfilUsuarioId(authentication);
        EjerciciosDiaResponse response = registroService.obtenerEjerciciosDia(perfilUsuarioId, fecha);
        return ResponseEntity.ok(response);
    }

    // ============================================================
    // US-23: Eliminar Registros
    // ============================================================

    @DeleteMapping("/comidas/{registroId}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "👤 USER - Eliminar registro de comida", description = "US-23: Desmarcar comida completada. SOLO USUARIOS REGULARES.")
    public ResponseEntity<Void> eliminarRegistroComida(
            @PathVariable Long registroId,
            Authentication authentication) {
        Long perfilUsuarioId = obtenerPerfilUsuarioId(authentication);
        registroService.eliminarRegistroComida(perfilUsuarioId, registroId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/ejercicios/{registroId}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "👤 USER - Eliminar registro de ejercicio", description = "US-23: Desmarcar ejercicio completado. SOLO USUARIOS REGULARES.")
    public ResponseEntity<Void> eliminarRegistroEjercicio(
            @PathVariable Long registroId,
            Authentication authentication) {
        Long perfilUsuarioId = obtenerPerfilUsuarioId(authentication);
        registroService.eliminarRegistroEjercicio(perfilUsuarioId, registroId);
        return ResponseEntity.noContent().build();
    }

    // ============================================================
    // Consultas Históricas
    // ============================================================

    @GetMapping("/comidas/historial")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "👤 USER - Obtener historial de comidas", description = "Consultar registros de comidas en un rango de fechas. SOLO USUARIOS REGULARES.")
    public ResponseEntity<List<RegistroComidaResponse>> obtenerHistorialComidas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            Authentication authentication) {
        Long perfilUsuarioId = obtenerPerfilUsuarioId(authentication);
        List<RegistroComidaResponse> historial = registroService.obtenerHistorialComidas(
                perfilUsuarioId, fechaInicio, fechaFin);
        return ResponseEntity.ok(historial);
    }

    @GetMapping("/ejercicios/historial")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "👤 USER - Obtener historial de ejercicios", description = "Consultar registros de ejercicios en un rango de fechas. SOLO USUARIOS REGULARES.")
    public ResponseEntity<List<RegistroEjercicioResponse>> obtenerHistorialEjercicios(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            Authentication authentication) {
        Long perfilUsuarioId = obtenerPerfilUsuarioId(authentication);
        List<RegistroEjercicioResponse> historial = registroService.obtenerHistorialEjercicios(
                perfilUsuarioId, fechaInicio, fechaFin);
        return ResponseEntity.ok(historial);
    }

    @GetMapping("/comidas/{registroId}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "👤 USER - Obtener detalle de registro de comida", description = "Consultar información completa de un registro específico. SOLO USUARIOS REGULARES.")
    public ResponseEntity<RegistroComidaResponse> obtenerRegistroComida(
            @PathVariable Long registroId,
            Authentication authentication) {
        Long perfilUsuarioId = obtenerPerfilUsuarioId(authentication);
        RegistroComidaResponse response = registroService.obtenerRegistroComida(perfilUsuarioId, registroId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/ejercicios/{registroId}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "👤 USER - Obtener detalle de registro de ejercicio", description = "Consultar información completa de un registro específico. SOLO USUARIOS REGULARES.")
    public ResponseEntity<RegistroEjercicioResponse> obtenerRegistroEjercicio(
            @PathVariable Long registroId,
            Authentication authentication) {
        Long perfilUsuarioId = obtenerPerfilUsuarioId(authentication);
        RegistroEjercicioResponse response = registroService.obtenerRegistroEjercicio(perfilUsuarioId, registroId);
        return ResponseEntity.ok(response);
    }

    // ============================================================
    // Progreso y Estadísticas
    // ============================================================

    @GetMapping("/ejercicios/progreso/semanal")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "👤 USER - Obtener progreso semanal de ejercicios", 
               description = """
                   Retorna estadísticas detalladas de la semana:
                   - Ejercicios completados vs programados
                   - Porcentaje de cumplimiento
                   - Calorías quemadas totales
                   - Tiempo total de ejercicio
                   - Desglose por día de la semana
                   
                   Si no se especifica fecha, usa la semana actual.
                   """)
    public ResponseEntity<ProgresoSemanalResponse> obtenerProgresoSemanal(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            Authentication authentication) {
        Long perfilUsuarioId = obtenerPerfilUsuarioId(authentication);
        LocalDate fechaReferencia = fecha != null ? fecha : LocalDate.now();
        ProgresoSemanalResponse response = registroService.obtenerProgresoSemanal(perfilUsuarioId, fechaReferencia);
        return ResponseEntity.ok(response);
    }

    // ============================================================
    // Calendario de Comidas
    // ============================================================

    @GetMapping("/comidas/calendario")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "👤 USER - Vista calendario de comidas", 
               description = """
                   Obtiene un resumen de comidas en formato calendario para un rango de fechas.
                   Ideal para vistas semanales o mensuales.
                   
                   INCLUYE:
                   - Estado de cada día (completo/incompleto)
                   - Comidas programadas vs completadas
                   - Resumen nutricional por día
                   - Porcentaje de cumplimiento global
                   
                   PARÁMETROS:
                   - fechaInicio: Fecha inicio del rango (por defecto: inicio de semana actual)
                   - fechaFin: Fecha fin del rango (por defecto: fin de semana actual)
                   """)
    public ResponseEntity<CalendarioComidaResponse> obtenerCalendarioComidas(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            Authentication authentication) {
        Long perfilUsuarioId = obtenerPerfilUsuarioId(authentication);
        
        // Por defecto: semana actual
        LocalDate inicio = fechaInicio != null ? fechaInicio : LocalDate.now().with(java.time.DayOfWeek.MONDAY);
        LocalDate fin = fechaFin != null ? fechaFin : LocalDate.now().with(java.time.DayOfWeek.SUNDAY);
        
        CalendarioComidaResponse response = registroService.obtenerCalendarioComidas(perfilUsuarioId, inicio, fin);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/comidas/calendario/semana")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "👤 USER - Vista calendario semanal", 
               description = "Atajo para obtener calendario de la semana que contiene la fecha especificada.")
    public ResponseEntity<CalendarioComidaResponse> obtenerCalendarioSemanal(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            Authentication authentication) {
        Long perfilUsuarioId = obtenerPerfilUsuarioId(authentication);
        LocalDate referencia = fecha != null ? fecha : LocalDate.now();
        LocalDate inicio = referencia.with(java.time.DayOfWeek.MONDAY);
        LocalDate fin = referencia.with(java.time.DayOfWeek.SUNDAY);
        
        CalendarioComidaResponse response = registroService.obtenerCalendarioComidas(perfilUsuarioId, inicio, fin);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/comidas/calendario/mes")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "👤 USER - Vista calendario mensual", 
               description = "Atajo para obtener calendario del mes que contiene la fecha especificada.")
    public ResponseEntity<CalendarioComidaResponse> obtenerCalendarioMensual(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            Authentication authentication) {
        Long perfilUsuarioId = obtenerPerfilUsuarioId(authentication);
        LocalDate referencia = fecha != null ? fecha : LocalDate.now();
        LocalDate inicio = referencia.withDayOfMonth(1);
        LocalDate fin = referencia.withDayOfMonth(referencia.lengthOfMonth());
        
        CalendarioComidaResponse response = registroService.obtenerCalendarioComidas(perfilUsuarioId, inicio, fin);
        return ResponseEntity.ok(response);
    }

    // ============================================================
    // Progreso Nutricional
    // ============================================================

    @GetMapping("/comidas/progreso/semanal")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "👤 USER - Progreso nutricional semanal", 
               description = """
                   Obtiene el progreso nutricional semanal completo.
                   
                   INCLUYE:
                   - Objetivos vs consumo de: calorías, proteínas, carbohidratos, grasas
                   - Porcentajes de cumplimiento por nutriente
                   - Promedios diarios
                   - Desglose día a día
                   - Estadísticas de comidas registradas
                   """)
    public ResponseEntity<ProgresoNutricionalResponse> obtenerProgresoNutricional(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            Authentication authentication) {
        Long perfilUsuarioId = obtenerPerfilUsuarioId(authentication);
        LocalDate fechaReferencia = fecha != null ? fecha : LocalDate.now();
        ProgresoNutricionalResponse response = registroService.obtenerProgresoNutricional(perfilUsuarioId, fechaReferencia);
        return ResponseEntity.ok(response);
    }

    // ============================================================
    // Comidas Extra
    // ============================================================

    @PostMapping("/comidas/extra")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "👤 USER - Registrar comida extra (no del plan)", 
               description = """
                   Permite registrar comidas que no estaban programadas en el plan.
                   
                   OPCIONES:
                   1. Usar comida del catálogo: especificar comidaId
                   2. Comida manual: especificar nombreComida y opcionalmente nutrientes
                   
                   Si se especifica nombreComida y no existe en el catálogo, se crea automáticamente.
                   """)
    public ResponseEntity<RegistroComidaResponse> registrarComidaExtra(
            @Valid @RequestBody RegistroComidaExtraRequest request,
            Authentication authentication) {
        Long perfilUsuarioId = obtenerPerfilUsuarioId(authentication);
        RegistroComidaResponse response = registroService.registrarComidaExtra(perfilUsuarioId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ============================================================
    // Progreso del Plan
    // ============================================================

    @GetMapping("/plan/progreso")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "👤 USER - Obtener progreso acumulado del plan", 
               description = """
                   Obtiene estadísticas completas del progreso del plan desde su inicio.
                   
                   INCLUYE:
                   - Día actual del plan (con soporte cíclico)
                   - Días completados vs parciales vs sin registro
                   - Porcentaje de cumplimiento de días
                   - Total de comidas programadas vs registradas
                   - Racha actual y mejor racha
                   - Historial de los últimos 7 días
                   - Estado del día actual (comidas completadas hoy)
                   
                   IDEAL PARA:
                   - Mostrar progreso en tarjeta de "Mis Metas"
                   - Calcular porcentaje de barra de progreso
                   - Mostrar estadísticas de cumplimiento
                   """)
    public ResponseEntity<ProgresoPlanResponse> obtenerProgresoPlan(Authentication authentication) {
        Long perfilUsuarioId = obtenerPerfilUsuarioId(authentication);
        ProgresoPlanResponse response = registroService.obtenerProgresoPlan(perfilUsuarioId);
        return ResponseEntity.ok(response);
    }

    // ============================================================
    // Utilidades
    // ============================================================

    private Long obtenerPerfilUsuarioId(Authentication authentication) {
        String email = authentication.getName();
        CuentaAuth cuenta = cuentaAuthRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return perfilUsuarioRepository.findByCuentaId(cuenta.getId())
                .orElseThrow(() -> new RuntimeException("Perfil no encontrado"))
                .getId();
    }
}
