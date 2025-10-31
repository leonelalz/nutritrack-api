package com.nutritrack.nutritrackapi.repository;

import com.nutritrack.nutritrackapi.model.UsuarioHistorialMedida;
import com.nutritrack.nutritrackapi.model.PerfilUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UsuarioHistorialMedidaRepository extends JpaRepository<UsuarioHistorialMedida, Long> {
    // Ejemplo: obtener todas las mediciones de un usuario
    List<UsuarioHistorialMedida> findByClienteOrderByFechaMedicionDesc(PerfilUsuario cliente);
}
