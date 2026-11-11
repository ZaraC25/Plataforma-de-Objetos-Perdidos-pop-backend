package edu.universidad.pop.modules.notificacion.service;

import edu.universidad.pop.modules.notificacion.dto.NotificacionDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificacionService {
    Page<NotificacionDTO> listarPorUsuario(String correo, boolean leida, Pageable pageable);
    void marcarComoLeida(Long id);
}