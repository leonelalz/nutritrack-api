package com.nutritrack.nutritrackapi.service;

import com.nutritrack.nutritrackapi.dto.response.CatalogoActividadResponseDTO;
import com.nutritrack.nutritrackapi.exception.MetaNoEncontradaException;
import com.nutritrack.nutritrackapi.model.CatalogoActividad;
import com.nutritrack.nutritrackapi.repository.CatalogoActividadRepository;
import com.nutritrack.nutritrackapi.repository.UsuarioMetaAsignadaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.UUID;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CatalogoActividadService {

    private final CatalogoActividadRepository catalogoActividadRepository;
    private final UsuarioMetaAsignadaRepository usuarioMetaAsignadaRepository;

    // Obtener actividades del plan activo (US-21 / RN20)
    @Transactional
    public List<CatalogoActividadResponseDTO> obtenerActividadesPlan(UUID idCliente) {
        var metaActiva = usuarioMetaAsignadaRepository.findByIdClienteAndEstado(idCliente, com.nutritrack.nutritrackapi.model.enums.EstadoMeta.ACTIVO)
                .orElseThrow(() -> new MetaNoEncontradaException(null));

        List<CatalogoActividad> actividades = catalogoActividadRepository.findByCatalogoMeta_Id(metaActiva.getCatalogoMeta().getId());

        return actividades.stream()
                .map(a -> new CatalogoActividadResponseDTO(a.getId(), a.getNombre(), false))
                .toList();
    }
}
