package edu.universidad.pop.modules.notificacion.service.impl;

import edu.universidad.pop.modules.notificacion.dto.NotificacionDTO;
import edu.universidad.pop.modules.notificacion.repository.NotificacionRepository;
import edu.universidad.pop.modules.notificacion.service.NotificacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class NotificacionServiceImpl implements NotificacionService {

    private final NotificacionRepository notificacionRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<NotificacionDTO> listarPorUsuario(String correo, boolean leida, Pageable pageable) {
        return notificacionRepository.findByUsuarioDestinatarioCorreoInstitucionalAndLeida(correo, leida, Objects.requireNonNull(pageable))
                .map(NotificacionDTO::from);
    }

    @Override
    @Transactional
    public void marcarComoLeida(Long id) {
        notificacionRepository.findById(Objects.requireNonNull(id)).ifPresent(n -> {
            n.setLeida(true);
            notificacionRepository.save(n);
        });
    }
}