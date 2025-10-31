package com.nutritrack.nutritrackapi.controller;

import com.nutritrack.nutritrackapi.dto.request.AsignarEtiquetasRequest;
import com.nutritrack.nutritrackapi.dto.request.EtiquetaRequest;
import com.nutritrack.nutritrackapi.dto.response.ApiResponse;
import com.nutritrack.nutritrackapi.dto.response.EtiquetaResponse;
import com.nutritrack.nutritrackapi.service.EtiquetaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/etiquetas")
@RequiredArgsConstructor
public class EtiquetaController {

    private final EtiquetaService etiquetaService;

    //CRUD de Etiquetas

    @PostMapping
    public ResponseEntity<ApiResponse<EtiquetaResponse>> crearEtiqueta(
            @Valid @RequestBody EtiquetaRequest request) {
        EtiquetaResponse created = etiquetaService.crearEtiqueta(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Etiqueta creada exitosamente", created));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<EtiquetaResponse>>> obtenerTodasEtiquetas() {
        List<EtiquetaResponse> etiquetas = etiquetaService.obtenerTodasEtiquetas();
        return ResponseEntity.ok(
                ApiResponse.success("Etiquetas obtenidas exitosamente", etiquetas)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EtiquetaResponse>> obtenerEtiquetaPorId(
            @PathVariable Long id) {
        EtiquetaResponse etiqueta = etiquetaService.obtenerEtiquetaPorId(id);
        return ResponseEntity.ok(
                ApiResponse.success("Etiqueta obtenida exitosamente", etiqueta)
        );
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<EtiquetaResponse>> actualizarEtiqueta(
            @PathVariable Long id,
            @Valid @RequestBody EtiquetaRequest request) {
        EtiquetaResponse updated = etiquetaService.actualizarEtiqueta(id, request);
        return ResponseEntity.ok(
                ApiResponse.success("Etiqueta actualizada exitosamente", updated)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminarEtiqueta(@PathVariable Long id) {
        etiquetaService.eliminarEtiqueta(id);
        return ResponseEntity.ok(
                ApiResponse.success("Etiqueta eliminada exitosamente", null)
        );
    }

    //Asignación a Planes

    @PostMapping("/planes/{idPlan}/etiquetas")
    public ResponseEntity<ApiResponse<Void>> asignarEtiquetasAPlan(
            @PathVariable Long idPlan,
            @Valid @RequestBody AsignarEtiquetasRequest request) {
        etiquetaService.asignarEtiquetasAPlan(idPlan, request.etiquetasIds());
        return ResponseEntity.ok(
                ApiResponse.success("Etiquetas asignadas al plan exitosamente", null)
        );
    }

    @DeleteMapping("/planes/{idPlan}/etiquetas/{idEtiqueta}")
    public ResponseEntity<ApiResponse<Void>> quitarEtiquetaDePlan(
            @PathVariable Long idPlan,
            @PathVariable Long idEtiqueta) {
        etiquetaService.quitarEtiquetaDePlan(idPlan, idEtiqueta);
        return ResponseEntity.ok(
                ApiResponse.success("Etiqueta removida del plan exitosamente", null)
        );
    }

    //Asignación a Ejercicios

    @PostMapping("/ejercicios/{idEjercicio}/etiquetas")
    public ResponseEntity<ApiResponse<Void>> asignarEtiquetasAEjercicio(
            @PathVariable Long idEjercicio,
            @Valid @RequestBody AsignarEtiquetasRequest request) {
        etiquetaService.asignarEtiquetasAEjercicio(idEjercicio, request.etiquetasIds());
        return ResponseEntity.ok(
                ApiResponse.success("Etiquetas asignadas al ejercicio exitosamente", null)
        );
    }

    @DeleteMapping("/ejercicios/{idEjercicio}/etiquetas/{idEtiqueta}")
    public ResponseEntity<ApiResponse<Void>> quitarEtiquetaDeEjercicio(
            @PathVariable Long idEjercicio,
            @PathVariable Long idEtiqueta) {
        etiquetaService.quitarEtiquetaDeEjercicio(idEjercicio, idEtiqueta);
        return ResponseEntity.ok(
                ApiResponse.success("Etiqueta removida del ejercicio exitosamente", null)
        );
    }

    //Asignación a Ingredientes==

    @PostMapping("/ingredientes/{idIngrediente}/etiquetas")
    public ResponseEntity<ApiResponse<Void>> asignarEtiquetasAIngrediente(
            @PathVariable Long idIngrediente,
            @Valid @RequestBody AsignarEtiquetasRequest request) {
        etiquetaService.asignarEtiquetasAIngrediente(idIngrediente, request.etiquetasIds());
        return ResponseEntity.ok(
                ApiResponse.success("Etiquetas asignadas al ingrediente exitosamente", null)
        );
    }

    @DeleteMapping("/ingredientes/{idIngrediente}/etiquetas/{idEtiqueta}")
    public ResponseEntity<ApiResponse<Void>> quitarEtiquetaDeIngrediente(
            @PathVariable Long idIngrediente,
            @PathVariable Long idEtiqueta) {
        etiquetaService.quitarEtiquetaDeIngrediente(idIngrediente, idEtiqueta);
        return ResponseEntity.ok(
                ApiResponse.success("Etiqueta removida del ingrediente exitosamente", null)
        );
    }

    //Asignación a Metas

    @PostMapping("/metas/{idMeta}/etiquetas")
    public ResponseEntity<ApiResponse<Void>> asignarEtiquetasAMeta(
            @PathVariable Long idMeta,
            @Valid @RequestBody AsignarEtiquetasRequest request) {
        etiquetaService.asignarEtiquetasAMeta(idMeta, request.etiquetasIds());
        return ResponseEntity.ok(
                ApiResponse.success("Etiquetas asignadas a la meta exitosamente", null)
        );
    }

    @DeleteMapping("/metas/{idMeta}/etiquetas/{idEtiqueta}")
    public ResponseEntity<ApiResponse<Void>> quitarEtiquetaDeMeta(
            @PathVariable Long idMeta,
            @PathVariable Long idEtiqueta) {
        etiquetaService.quitarEtiquetaDeMeta(idMeta, idEtiqueta);
        return ResponseEntity.ok(
                ApiResponse.success("Etiqueta removida de la meta exitosamente", null)
        );
    }
}