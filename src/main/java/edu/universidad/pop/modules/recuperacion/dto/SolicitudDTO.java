package edu.universidad.pop.modules.recuperacion.dto;

import edu.universidad.pop.modules.recuperacion.domain.SolicitudRecuperacion;

import java.time.LocalDateTime;

public record SolicitudDTO(
        Long id,
        Long objetoId,
        String objetoTitulo,
        String estado,
        LocalDateTime fechaCreacion
) {
    public static SolicitudDTO from(SolicitudRecuperacion s) {
        if (s == null) return null;
        return new SolicitudDTO(
                s.getId(),
                s.getObjeto() != null ? s.getObjeto().getId() : null,
                s.getObjeto() != null ? s.getObjeto().getTitulo() : "Objeto no disponible",
                s.getEstado() != null ? s.getEstado().name() : null,
                s.getFechaCreacion()
        );
    }
}