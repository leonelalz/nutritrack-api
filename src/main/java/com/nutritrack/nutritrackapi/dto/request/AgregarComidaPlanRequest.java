package com.nutritrack.nutritrackapi.dto.request;

import com.nutritrack.nutritrackapi.model.enums.TipoComida;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgregarComidaPlanRequest {

    @NotNull(message = "El número de día es obligatorio")
    @Min(value = 1, message = "El número de día debe ser al menos 1")
    private Integer numeroDia;

    @NotNull(message = "El tipo de comida es obligatorio")
    private TipoComida tipoComida;

    @NotNull(message = "El ID de la comida es obligatorio")
    private Long comidaId;

    @Size(max = 500, message = "Las notas no pueden exceder 500 caracteres")
    private String notas;
}
