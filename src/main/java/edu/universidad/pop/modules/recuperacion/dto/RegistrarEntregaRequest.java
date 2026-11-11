package edu.universidad.pop.modules.recuperacion.dto;

import jakarta.validation.constraints.NotNull;

public record RegistrarEntregaRequest(
        @NotNull long solicitudId,
        @NotNull boolean confirmacionRecepcion,
        String firmaRecepcion,
        String observaciones
) {}