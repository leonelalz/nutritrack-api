package com.nutritrack.nutritrackapi.service;

import com.nutritrack.nutritrackapi.dto.response.UsuarioActividadProgresoResponseDTO;
import com.nutritrack.nutritrackapi.exception.ActividadNoEncontradaException;
import com.nutritrack.nutritrackapi.exception.ActividadYaCompletadaException;
import com.nutritrack.nutritrackapi.exception.MetaNoActivaException;
import com.nutritrack.nutritrackapi.exception.MetaNoEncontradaException;
import com.nutritrack.nutritrackapi.model.CatalogoActividad;
import com.nutritrack.nutritrackapi.model.UsuarioActividadProgreso;
import com.nutritrack.nutritrackapi.model.UsuarioMetaAsignada;
import com.nutritrack.nutritrackapi.model.enums.EstadoMeta;
import com.nutritrack.nutritrackapi.repository.CatalogoActividadRepository;
import com.nutritrack.nutritrackapi.repository.UsuarioActividadProgresoRepository;
import com.nutritrack.nutritrackapi.repository.UsuarioMetaAsignadaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class UsuarioActividadProgresoService {

    private final UsuarioActividadProgresoRepository usuarioActividadProgresoRepository;
    private final UsuarioMetaAsignadaRepository usuarioMetaAsignadaRepository;
    private final CatalogoActividadRepository catalogoActividadRepository;

    // Marcar actividad como completada (US-22 / RN21)
    @Transactional
    public UsuarioActividadProgresoResponseDTO completarActividad(Long idMetaAsignada, Long idActividad) {

        UsuarioMetaAsignada metaAsignada = usuarioMetaAsignadaRepository.findById(idMetaAsignada)
                .orElseThrow(() -> new MetaNoEncontradaException(idMetaAsignada));

        if (metaAsignada.getEstado() != EstadoMeta.ACTIVO) {
            throw new MetaNoActivaException(idMetaAsignada);
        }

        CatalogoActividad actividad = catalogoActividadRepository.findById(idActividad)
                .orElseThrow(() -> new ActividadNoEncontradaException(idActividad));

        boolean yaCompletada = usuarioActividadProgresoRepository.findActividadesCompletadas(idMetaAsignada)
                .stream()
                .anyMatch(a -> a.getCatalogoActividad().getId().equals(idActividad));

        if (yaCompletada) {
            throw new ActividadYaCompletadaException(idActividad);
        }

        UsuarioActividadProgreso progreso = new UsuarioActividadProgreso();
        progreso.setMetaAsignada(metaAsignada);
        progreso.setCatalogoActividad(actividad);
        progreso.setActividadAcabada(true);
        progreso.setFechaCompletado(LocalDate.now());

        usuarioActividadProgresoRepository.save(progreso);

        return new UsuarioActividadProgresoResponseDTO(
                "Actividad '" + actividad.getNombre() + "' completada correctamente."
        );
    }
}
