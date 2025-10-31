package com.nutritrack.nutritrackapi.service;

import com.nutritrack.nutritrackapi.model.UsuarioHistorialMedida;
import com.nutritrack.nutritrackapi.model.PerfilUsuario;
import com.nutritrack.nutritrackapi.repository.UsuarioHistorialMedidaRepository;
import com.nutritrack.nutritrackapi.repository.PerfilUsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UsuarioHistorialMedidaService {

    private final UsuarioHistorialMedidaRepository historialRepo;
    private final PerfilUsuarioRepository perfilRepo;

    /**
     * Registrar una nueva medición para un usuario.
     */
    public UsuarioHistorialMedida registrarMedicion(UUID idCliente, BigDecimal peso, BigDecimal altura) {
        PerfilUsuario cliente = perfilRepo.findById(idCliente)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con id: " + idCliente));

        BigDecimal imc = calcularImc(peso, altura);

        UsuarioHistorialMedida medida = UsuarioHistorialMedida.builder()
                .cliente(cliente)
                .peso(peso)
                .altura(altura)
                .imc(imc)
                .fechaMedicion(LocalDate.now())
                .build();

        return historialRepo.save(medida);
    }

    /**
     * Obtener todas las mediciones de un usuario ordenadas por fecha descendente.
     */
    public List<UsuarioHistorialMedida> obtenerHistorialPorCliente(UUID idCliente) {
        PerfilUsuario cliente = perfilRepo.findById(idCliente)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con id: " + idCliente));

        return historialRepo.findByClienteOrderByFechaMedicionDesc(cliente);
    }

    /**
     * Calcular IMC (peso / (altura/100)^2)
     */
    private BigDecimal calcularImc(BigDecimal peso, BigDecimal altura) {
        if (peso == null || altura == null || altura.compareTo(BigDecimal.ZERO) <= 0) {
            return null;
        }
        BigDecimal alturaMetros = altura.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
        return peso.divide(alturaMetros.pow(2), 2, RoundingMode.HALF_UP);
    }
}
