package edu.universidad.pop.modules.recuperacion.dto;

public record ResolverSolicitudRequest(
        String decision, // "APROBAR" o "RECHAZAR"
        String motivo
) {}