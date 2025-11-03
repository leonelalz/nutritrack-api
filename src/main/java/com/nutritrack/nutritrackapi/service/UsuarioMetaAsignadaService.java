package com.nutritrack.nutritrackapi.service;

import com.nutritrack.nutritrackapi.dto.request.UsuarioMetaAsignadaRequestDTO;
import com.nutritrack.nutritrackapi.dto.response.UsuarioMetaAsignadaResponseDTO;
import com.nutritrack.nutritrackapi.exception.MetaActivaExistenteException;
import com.nutritrack.nutritrackapi.exception.MetaNoEncontradaException;
import com.nutritrack.nutritrackapi.model.CatalogoMeta;
import com.nutritrack.nutritrackapi.model.UsuarioMetaAsignada;
import com.nutritrack.nutritrackapi.model.enums.EstadoMeta;
import com.nutritrack.nutritrackapi.repository.CatalogoMetaRepository;
import com.nutritrack.nutritrackapi.repository.UsuarioMetaAsignadaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UsuarioMetaAsignadaService {
    private final UsuarioMetaAsignadaRepository usuarioMetaAsignadaRepository;
    private final CatalogoMetaRepository catalogoMetaRepository;

    // Activar una meta (US-18 / RN17)
    @Transactional
    public UsuarioMetaAsignadaResponseDTO activarMeta(UUID idCliente, UsuarioMetaAsignadaRequestDTO request) {

        if (usuarioMetaAsignadaRepository.existsMetaActivaByCliente(idCliente)) {
            throw new MetaActivaExistenteException(idCliente);
        }

        CatalogoMeta meta = catalogoMetaRepository.findById(request.catalogGoalId())
                .orElseThrow(() -> new MetaNoEncontradaException(request.catalogGoalId()));

        UsuarioMetaAsignada asignacion = new UsuarioMetaAsignada();
        asignacion.setIdCliente(idCliente);
        asignacion.setCatalogoMeta(meta);
        asignacion.setEstado(EstadoMeta.ACTIVO);
        asignacion.setFechaInicioAsignacion(LocalDate.now());

        asignacion = usuarioMetaAsignadaRepository.save(asignacion);

        return new UsuarioMetaAsignadaResponseDTO(
                asignacion.getId(),
                asignacion.getEstado().name(),
                meta.getId(),
                asignacion.getFechaInicioAsignacion(),
                asignacion.getFechaFinAsignacion()
        );
    }
}
