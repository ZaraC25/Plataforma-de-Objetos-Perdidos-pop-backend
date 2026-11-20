package edu.universidad.pop.modules.coincidencia.dto;

public record CoincidenciaDTO(
        Long reporteId,
        String reporteDescripcion,
        String reporteCategoria,
        String reporteUltimaUbicacion,
        String reporteUsuarioNombre,
        String reporteUsuarioCorreo,
        Long objetoId,
        String objetoTitulo,
        String objetoDescripcion,
        String objetoCategoria,
        String objetoUbicacion
) {}
