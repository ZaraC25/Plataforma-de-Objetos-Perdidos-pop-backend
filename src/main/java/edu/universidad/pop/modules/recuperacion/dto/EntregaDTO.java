package edu.universidad.pop.modules.recuperacion.dto;

import edu.universidad.pop.modules.recuperacion.domain.Entrega;

import java.time.LocalDateTime;

/**
 * RF-10 — DTO de respuesta para una entrega registrada.
 *
 * Expone solo los datos que necesita el cliente (frontend).
 * No incluye referencias directas a entidades JPA para evitar ciclos y
 * problemas de serialización lazy.
 */
public record EntregaDTO(
        Long id,
        Long objetoId,
        String objetoTitulo,
        Long solicitudId,
        Long encargadoResponsableId,
        String encargadoResponsableNombre,
        LocalDateTime fechaEntrega,
        boolean confirmacionRecepcion,
        String firmaRecepcion,
        String observaciones
) {

    /**
     * Convierte una entidad Entrega a su DTO equivalente.
     * Se usa en EntregaServiceImpl para construir la respuesta.
     */
    public static EntregaDTO from(Entrega entrega) {
        if (entrega == null) {
            return null;
        }
        return new EntregaDTO(
                entrega.getId(),
                entrega.getObjeto() != null ? entrega.getObjeto().getId() : null,
                entrega.getObjeto() != null ? entrega.getObjeto().getTitulo() : null,
                entrega.getSolicitud() != null ? entrega.getSolicitud().getId() : null,
                entrega.getEncargadoResponsable() != null
                        ? entrega.getEncargadoResponsable().getId() : null,
                entrega.getEncargadoResponsable() != null
                        ? entrega.getEncargadoResponsable().getNombre() : null,
                entrega.getFechaEntrega(),
                entrega.isConfirmacionRecepcion(),
                entrega.getFirmaRecepcion(),
                entrega.getObservaciones()
        );
    }
}