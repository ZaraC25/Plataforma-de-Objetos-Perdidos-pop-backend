package edu.universidad.pop.modules.notificacion.dto;

import edu.universidad.pop.modules.notificacion.domain.Notificacion;
import java.time.LocalDateTime;

public record NotificacionDTO(
        Long id,
        String tipo,
        String mensaje,
        boolean leida,
        LocalDateTime fechaCreacion
) {
    public static NotificacionDTO from(Notificacion n) {
        return new NotificacionDTO(
                n.getId(),
                n.getTipo() != null ? n.getTipo().name() : null,
                n.getMensaje(),
                n.isLeida(),
                n.getFechaCreacion()
        );
    }
}