package edu.universidad.pop.modules.reporte.dto;

import edu.universidad.pop.common.enums.CategoriaObjeto;
import edu.universidad.pop.common.enums.EstadoReporte;
import edu.universidad.pop.modules.reporte.domain.ReportePerdida;

import java.time.LocalDateTime;

public record ReportePerdidaDTO(
        Long id,
        String descripcion,
        CategoriaObjeto categoria,
        LocalDateTime fechaPerdida,
        String ultimaUbicacion,
        EstadoReporte estado,
        LocalDateTime fechaCreacion
) {
    public static ReportePerdidaDTO from(ReportePerdida r) {
        if (r == null) return null;
        return new ReportePerdidaDTO(
                r.getId(),
                r.getDescripcion(),
                r.getCategoria(),
                r.getFechaPerdida(),
                r.getUltimaUbicacion(),
                r.getEstado(),
                r.getFechaCreacion()
        );
    }
}