package edu.universidad.pop.modules.recuperacion.service;

import edu.universidad.pop.modules.recuperacion.dto.ResolverSolicitudRequest;
import edu.universidad.pop.modules.recuperacion.dto.SolicitudDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SolicitudRecuperacionService {
    SolicitudDTO crear(Long objetoId, String descripcionPrivada, String correoUsuario);
    Page<SolicitudDTO> listarMias(String correoUsuario, Pageable pageable);
    Page<SolicitudDTO> listarPendientes(Pageable pageable);
    Page<SolicitudDTO> listarAprobadas(Pageable pageable);
    SolicitudDTO resolver(Long id, ResolverSolicitudRequest request, String correoEncargado);
}