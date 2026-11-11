package edu.universidad.pop.modules.reporte.dto;

import edu.universidad.pop.common.enums.CategoriaObjeto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CrearReporteRequest(
        @NotBlank String descripcion,
        @NotNull CategoriaObjeto categoria,
        @NotNull LocalDateTime fechaPerdida,
        @NotBlank String ultimaUbicacion
) {}