package edu.universidad.pop.modules.objeto.dto;

import edu.universidad.pop.common.enums.CategoriaObjeto;
import edu.universidad.pop.common.enums.EstadoObjeto;
import edu.universidad.pop.modules.objeto.domain.Objeto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * RF-03 / RF-05 — DTO de lectura de un objeto.
 */
public record ObjetoDTO(
        Long id,
        String titulo,
        String descripcion,
        CategoriaObjeto categoria,
        LocalDateTime fechaHallazgo,
        String ubicacion,
        EstadoObjeto estado,
        Long registradoPorId,
        String registradoPorNombre,
        List<String> fotos,
        LocalDateTime fechaCreacion,
        LocalDateTime fechaActualizacion
) {

    public static ObjetoDTO from(Objeto objeto) {
        if (objeto == null) {
            return null;
        }
        return new ObjetoDTO(
                objeto.getId(),
                objeto.getTitulo(),
                objeto.getDescripcion(),
                objeto.getCategoria(),
                objeto.getFechaHallazgo(),
                objeto.getUbicacion(),
                objeto.getEstado(),
                objeto.getRegistradoPor() != null ? objeto.getRegistradoPor().getId() : null,
                objeto.getRegistradoPor() != null ? objeto.getRegistradoPor().getNombre() : null,
                null,   // fotos: se llenan aparte si es necesario
                objeto.getFechaCreacion(),
                objeto.getFechaActualizacion()
        );
    }
}