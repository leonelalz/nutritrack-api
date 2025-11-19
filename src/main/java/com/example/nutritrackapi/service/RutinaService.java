package com.example.nutritrackapi.service;

import com.example.nutritrackapi.dto.*;
import com.example.nutritrackapi.model.*;
import com.example.nutritrackapi.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Servicio para gestión de rutinas de ejercicio.
 * Implementa US-11: Crear Meta del Catálogo (Rutina)
 * Implementa US-12: Gestionar Meta (configurar ejercicios)
 * Implementa US-13: Ver Catálogo de Metas (Admin)
 * Implementa US-14: Eliminar Meta
 * Implementa US-15: Ensamblar Rutinas
 * RN11: Nombres únicos
 * RN13: Series y repeticiones positivas
 * RN14: No eliminar si tiene usuarios activos
 * RN28: Soft delete
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RutinaService {

    private final RutinaRepository rutinaRepository;
    private final RutinaEjercicioRepository rutinaEjercicioRepository;
    private final EtiquetaRepository etiquetaRepository;
    private final EjercicioRepository ejercicioRepository;
    private final PerfilUsuarioRepository perfilUsuarioRepository;
    private final CuentaAuthRepository cuentaAuthRepository;

    /**
     * US-11: Crea una nueva rutina de ejercicio.
     * RN11: Valida que el nombre sea único.
     */
    @Transactional
    public RutinaResponse crearRutina(RutinaRequest request) {
        // RN11: Validar nombre único
        if (rutinaRepository.existsByNombre(request.getNombre())) {
            throw new IllegalArgumentException(
                "Ya existe una rutina con el nombre: " + request.getNombre()
            );
        }

        // Validar que patronSemanas no exceda duracionSemanas
        if (request.getPatronSemanas() > request.getDuracionSemanas()) {
            throw new IllegalArgumentException(
                "El patrón de semanas (" + request.getPatronSemanas() + 
                ") no puede ser mayor que la duración total (" + request.getDuracionSemanas() + " semanas)"
            );
        }

        // Crear rutina
        Rutina rutina = Rutina.builder()
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .duracionSemanas(request.getDuracionSemanas())
                .patronSemanas(request.getPatronSemanas())
                .nivelDificultad(request.getNivelDificultad())
                .activo(true)
                .build();

        // Asociar etiquetas
        if (request.getEtiquetaIds() != null && !request.getEtiquetaIds().isEmpty()) {
            Set<Etiqueta> etiquetas = new HashSet<>(
                etiquetaRepository.findAllById(request.getEtiquetaIds())
            );
            if (etiquetas.size() != request.getEtiquetaIds().size()) {
                throw new EntityNotFoundException("Una o más etiquetas no fueron encontradas");
            }
            rutina.setEtiquetas(etiquetas);
        }

        Rutina guardada = rutinaRepository.save(rutina);
        return RutinaResponse.fromEntity(guardada);
    }

    /**
     * US-17: Obtiene una rutina por su ID.
     */
    public RutinaResponse obtenerRutinaPorId(Long id) {
        Rutina rutina = rutinaRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(
                "Rutina no encontrada con ID: " + id
            ));
        return RutinaResponse.fromEntity(rutina);
    }

    /**
     * US-13: Lista todas las rutinas (admin - incluye inactivas).
     */
    public Page<RutinaResponse> listarRutinasAdmin(Pageable pageable) {
        return rutinaRepository.findAll(pageable)
            .map(RutinaResponse::fromEntity);
    }

    /**
     * US-16: Lista rutinas activas (catálogo para clientes).
     * RN28: Solo rutinas activas.
     */
    public Page<RutinaResponse> listarRutinasActivas(Pageable pageable) {
        return rutinaRepository.findByActivoTrue(pageable)
            .map(RutinaResponse::fromEntity);
    }

    /**
     * Busca rutinas por nombre (parcial, case-insensitive).
     */
    public Page<RutinaResponse> buscarPorNombre(String nombre, Pageable pageable) {
        return rutinaRepository.findByNombreContainingIgnoreCaseAndActivoTrue(nombre, pageable)
            .map(RutinaResponse::fromEntity);
    }

    /**
     * US-12: Actualiza una rutina existente.
     * RN11: Valida nombre único (excluyendo la rutina actual).
     */
    @Transactional
    public RutinaResponse actualizarRutina(Long id, RutinaRequest request) {
        Rutina rutina = rutinaRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(
                "Rutina no encontrada con ID: " + id
            ));

        // RN11: Validar nombre único (excluyendo la rutina actual)
        if (rutinaRepository.existsByNombreAndIdNot(request.getNombre(), id)) {
            throw new IllegalArgumentException(
                "Ya existe otra rutina con el nombre: " + request.getNombre()
            );
        }

        // Validar que patronSemanas no exceda duracionSemanas
        if (request.getPatronSemanas() > request.getDuracionSemanas()) {
            throw new IllegalArgumentException(
                "El patrón de semanas (" + request.getPatronSemanas() + 
                ") no puede ser mayor que la duración total (" + request.getDuracionSemanas() + " semanas)"
            );
        }

        rutina.setNombre(request.getNombre());
        rutina.setDescripcion(request.getDescripcion());
        rutina.setDuracionSemanas(request.getDuracionSemanas());
        rutina.setPatronSemanas(request.getPatronSemanas());
        rutina.setNivelDificultad(request.getNivelDificultad());

        // Actualizar etiquetas
        if (request.getEtiquetaIds() != null) {
            Set<Etiqueta> etiquetas = new HashSet<>(
                etiquetaRepository.findAllById(request.getEtiquetaIds())
            );
            if (etiquetas.size() != request.getEtiquetaIds().size()) {
                throw new EntityNotFoundException("Una o más etiquetas no fueron encontradas");
            }
            rutina.setEtiquetas(etiquetas);
        }

        return RutinaResponse.fromEntity(rutinaRepository.save(rutina));
    }

    /**
     * US-14: Elimina una rutina (soft delete).
     * RN14: No permite eliminar si tiene usuarios activos.
     * RN28: Marca como inactivo en lugar de eliminar físicamente.
     */
    @Transactional
    public void eliminarRutina(Long id) {
        Rutina rutina = rutinaRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(
                "Rutina no encontrada con ID: " + id
            ));

        // RN14: Verificar que no tenga usuarios activos
        // TODO: Descomentar cuando se implemente UsuarioRutina
        /*
        if (rutinaRepository.tieneUsuariosActivos(id)) {
            throw new IllegalStateException(
                "No se puede eliminar la rutina porque tiene usuarios activos asignados. " +
                "Espere a que los usuarios completen o cancelen la rutina."
            );
        }
        */

        // RN28: Soft delete - marcar como inactivo
        rutina.setActivo(false);
        rutinaRepository.save(rutina);
    }

    /**
     * Reactiva una rutina marcada como inactiva.
     * Permite reutilizar rutinas previamente eliminadas (soft delete).
     */
    @Transactional
    public RutinaResponse reactivarRutina(Long id) {
        Rutina rutina = rutinaRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(
                "Rutina no encontrada con ID: " + id
            ));

        if (rutina.getActivo()) {
            throw new IllegalStateException(
                "La rutina ya está activa"
            );
        }

        rutina.setActivo(true);
        Rutina actualizada = rutinaRepository.save(rutina);
        return RutinaResponse.fromEntity(actualizada);
    }

    /**
     * US-12, US-15: Agrega un ejercicio a la rutina.
     * RN13: Valida que series y repeticiones sean positivas (ya validado en DTO).
     */
    @Transactional
    public RutinaEjercicioResponse agregarEjercicioARutina(Long rutinaId, RutinaEjercicioRequest request) {
        Rutina rutina = rutinaRepository.findById(rutinaId)
            .orElseThrow(() -> new EntityNotFoundException(
                "Rutina no encontrada con ID: " + rutinaId
            ));

        // Validar que semanaBase no exceda patronSemanas
        if (request.getSemanaBase() > rutina.getPatronSemanas()) {
            throw new IllegalArgumentException(
                "La semana base (" + request.getSemanaBase() + 
                ") no puede ser mayor que el patrón de semanas de la rutina (" + 
                rutina.getPatronSemanas() + ")"
            );
        }

        // Verificar que el ejercicio existe
        Ejercicio ejercicio = ejercicioRepository.findById(request.getEjercicioId())
            .orElseThrow(() -> new EntityNotFoundException(
                "Ejercicio no encontrado con ID: " + request.getEjercicioId()
            ));

        // Verificar si ya existe un ejercicio en ese orden para ese día y semana
        if (rutinaEjercicioRepository.existsByRutinaIdAndOrden(rutinaId, request.getOrden())) {
            throw new IllegalStateException(
                "Ya existe un ejercicio en el orden " + request.getOrden() +
                " para la semana " + request.getSemanaBase() + ", día " + request.getDiaSemana() +
                ". Por favor, elija otro orden o modifique el ejercicio existente."
            );
        }

        RutinaEjercicio rutinaEjercicio = RutinaEjercicio.builder()
                .rutina(rutina)
                .ejercicio(ejercicio)
                .semanaBase(request.getSemanaBase())
                .diaSemana(request.getDiaSemana())
                .orden(request.getOrden())
                .series(request.getSeries())
                .repeticiones(request.getRepeticiones())
                .peso(request.getPeso())
                .duracionMinutos(request.getDuracionMinutos())
                .descansoSegundos(request.getDescansoSegundos())
                .notas(request.getNotas())
                .build();

        RutinaEjercicio guardado = rutinaEjercicioRepository.save(rutinaEjercicio);
        return RutinaEjercicioResponse.fromEntity(guardado);
    }

    /**
     * US-17: Obtiene todos los ejercicios de una rutina.
     */
    public List<RutinaEjercicioResponse> obtenerEjerciciosDeRutina(Long rutinaId) {
        if (!rutinaRepository.existsById(rutinaId)) {
            throw new EntityNotFoundException("Rutina no encontrada con ID: " + rutinaId);
        }

        return rutinaEjercicioRepository.findByRutinaIdOrderBySemanaBaseAscDiaSemanaAscOrdenAsc(rutinaId)
            .stream()
            .map(RutinaEjercicioResponse::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * US-15: Actualiza un ejercicio de la rutina.
     * RN13: Valida series y repeticiones positivas.
     */
    @Transactional
    public RutinaEjercicioResponse actualizarEjercicioDeRutina(
            Long rutinaId,
            Long ejercicioId,
            RutinaEjercicioRequest request) {
        
        RutinaEjercicio rutinaEjercicio = rutinaEjercicioRepository.findById(ejercicioId)
            .orElseThrow(() -> new EntityNotFoundException(
                "Ejercicio de rutina no encontrado con ID: " + ejercicioId
            ));

        if (!rutinaEjercicio.getRutina().getId().equals(rutinaId)) {
            throw new IllegalArgumentException(
                "El ejercicio no pertenece a la rutina especificada"
            );
        }

        // Validar que semanaBase no exceda patronSemanas
        if (request.getSemanaBase() > rutinaEjercicio.getRutina().getPatronSemanas()) {
            throw new IllegalArgumentException(
                "La semana base (" + request.getSemanaBase() + 
                ") no puede ser mayor que el patrón de semanas de la rutina (" + 
                rutinaEjercicio.getRutina().getPatronSemanas() + ")"
            );
        }

        // Si se cambia el orden, verificar que no exista otro ejercicio en ese orden
        if (!rutinaEjercicio.getOrden().equals(request.getOrden())) {
            if (rutinaEjercicioRepository.existsByRutinaIdAndOrden(rutinaId, request.getOrden())) {
                throw new IllegalStateException(
                    "Ya existe un ejercicio en el orden " + request.getOrden()
                );
            }
        }

        // Si se cambia el ejercicio, verificar que existe
        if (request.getEjercicioId() != null && 
            !rutinaEjercicio.getEjercicio().getId().equals(request.getEjercicioId())) {
            Ejercicio nuevoEjercicio = ejercicioRepository.findById(request.getEjercicioId())
                .orElseThrow(() -> new EntityNotFoundException(
                    "Ejercicio no encontrado con ID: " + request.getEjercicioId()
                ));
            rutinaEjercicio.setEjercicio(nuevoEjercicio);
        }

        rutinaEjercicio.setSemanaBase(request.getSemanaBase());
        rutinaEjercicio.setDiaSemana(request.getDiaSemana());
        rutinaEjercicio.setOrden(request.getOrden());
        rutinaEjercicio.setSeries(request.getSeries());
        rutinaEjercicio.setRepeticiones(request.getRepeticiones());
        rutinaEjercicio.setPeso(request.getPeso());
        rutinaEjercicio.setDuracionMinutos(request.getDuracionMinutos());
        rutinaEjercicio.setDescansoSegundos(request.getDescansoSegundos());
        rutinaEjercicio.setNotas(request.getNotas());

        return RutinaEjercicioResponse.fromEntity(rutinaEjercicioRepository.save(rutinaEjercicio));
    }

    /**
     * Elimina un ejercicio de la rutina.
     */
    @Transactional
    public void eliminarEjercicioDeRutina(Long rutinaId, Long ejercicioId) {
        RutinaEjercicio rutinaEjercicio = rutinaEjercicioRepository.findById(ejercicioId)
            .orElseThrow(() -> new EntityNotFoundException(
                "Ejercicio de rutina no encontrado con ID: " + ejercicioId
            ));

        if (!rutinaEjercicio.getRutina().getId().equals(rutinaId)) {
            throw new IllegalArgumentException(
                "El ejercicio no pertenece a la rutina especificada"
            );
        }

        rutinaEjercicioRepository.delete(rutinaEjercicio);
    }

    /**
     * RN16: Verifica si una rutina contiene ejercicios con contraindicaciones para el usuario
     */
    private boolean contieneContraindicaciones(Rutina rutina, java.util.Set<Etiqueta> condicionesUsuario) {
        if (condicionesUsuario == null || condicionesUsuario.isEmpty()) {
            return false;
        }

        // Obtener condiciones médicas del usuario
        java.util.Set<String> nombresCondiciones = condicionesUsuario.stream()
                .filter(e -> e.getTipoEtiqueta() == Etiqueta.TipoEtiqueta.CONDICION_MEDICA)
                .map(Etiqueta::getNombre)
                .collect(java.util.stream.Collectors.toSet());

        if (nombresCondiciones.isEmpty()) {
            return false;
        }

        // Verificar si algún ejercicio de la rutina tiene contraindicaciones
        return rutina.getEtiquetas().stream()
                .anyMatch(etiqueta -> nombresCondiciones.contains(etiqueta.getNombre()));
    }

    /**
     * US-16: Ver Catálogo de Rutinas (CLIENTE)
     * RN15: Sugiere rutinas según objetivo del perfil de salud
     * RN16: Filtra rutinas con contraindicaciones para el usuario
     */
    public Page<RutinaResponse> verCatalogo(Long perfilUsuarioId, boolean sugeridos, Pageable pageable) {
        PerfilUsuario perfil = perfilUsuarioRepository.findById(perfilUsuarioId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Perfil de usuario no encontrado con ID: " + perfilUsuarioId));

        Page<Rutina> rutinas;
        
        if (sugeridos && perfil.getPerfilSalud() != null && perfil.getPerfilSalud().getObjetivoActual() != null) {
            // RN15: Filtrar por objetivo actual del perfil de salud
            String objetivoActual = perfil.getPerfilSalud().getObjetivoActual().name();
            rutinas = rutinaRepository.findByActivoTrueAndEtiquetasNombre(objetivoActual, pageable);
        } else {
            // Sin filtro de objetivo
            rutinas = rutinaRepository.findByActivoTrue(pageable);
        }

        // RN16: Filtrar rutinas que contengan contraindicaciones para el usuario
        java.util.Set<Etiqueta> condicionesUsuario = perfil.getEtiquetasSalud();
        if (condicionesUsuario != null && !condicionesUsuario.isEmpty()) {
            java.util.List<Rutina> rutinasFiltradas = rutinas.getContent().stream()
                    .filter(rutina -> !contieneContraindicaciones(rutina, condicionesUsuario))
                    .collect(java.util.stream.Collectors.toList());
            return new org.springframework.data.domain.PageImpl<>(rutinasFiltradas, pageable, rutinasFiltradas.size())
                    .map(RutinaResponse::fromEntity);
        }

        return rutinas.map(RutinaResponse::fromEntity);
    }

    /**
     * US-17: Ver Detalle de Rutina (CLIENTE)
     * RN16: Advierte si la rutina contiene contraindicaciones para el usuario
     */
    public RutinaResponse verDetalleRutina(Long rutinaId, Long perfilUsuarioId) {
        Rutina rutina = rutinaRepository.findById(rutinaId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Rutina no encontrada con ID: " + rutinaId));

        if (!rutina.getActivo()) {
            throw new IllegalStateException("La rutina no está disponible");
        }

        // Verificar que el perfil existe
        PerfilUsuario perfil = perfilUsuarioRepository.findById(perfilUsuarioId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Perfil de usuario no encontrado con ID: " + perfilUsuarioId));

        // RN16: Validar si la rutina contiene contraindicaciones
        if (contieneContraindicaciones(rutina, perfil.getEtiquetasSalud())) {
            throw new com.example.nutritrackapi.exception.BusinessException(
                    "ADVERTENCIA: Esta rutina contiene ejercicios que pueden estar contraindicados para tus condiciones de salud. Consulta con un profesional antes de activarla.");
        }

        return RutinaResponse.fromEntity(rutina);
    }

    /**
     * Obtiene el ID del perfil del usuario autenticado
     */
    public Long obtenerPerfilUsuarioId(String email) {
        CuentaAuth cuenta = cuentaAuthRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return perfilUsuarioRepository.findByCuentaId(cuenta.getId())
                .orElseThrow(() -> new RuntimeException("Perfil no encontrado"))
                .getId();
    }

}
