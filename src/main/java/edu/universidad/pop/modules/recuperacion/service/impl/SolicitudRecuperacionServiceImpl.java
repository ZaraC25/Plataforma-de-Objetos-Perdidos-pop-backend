package edu.universidad.pop.modules.recuperacion.service.impl;

import edu.universidad.pop.common.enums.EstadoObjeto;
import edu.universidad.pop.common.enums.EstadoSolicitud;
import edu.universidad.pop.common.exception.BusinessRuleException;
import edu.universidad.pop.common.exception.ResourceNotFoundException;
import edu.universidad.pop.modules.objeto.domain.Objeto;
import edu.universidad.pop.modules.objeto.repository.ObjetoRepository;
import edu.universidad.pop.modules.recuperacion.domain.SolicitudRecuperacion;
import edu.universidad.pop.modules.recuperacion.dto.ResolverSolicitudRequest;
import edu.universidad.pop.modules.recuperacion.dto.SolicitudDTO;
import edu.universidad.pop.modules.recuperacion.repository.SolicitudRecuperacionRepository;
import edu.universidad.pop.modules.recuperacion.service.SolicitudRecuperacionService;
import edu.universidad.pop.modules.usuario.domain.Usuario;
import edu.universidad.pop.modules.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class SolicitudRecuperacionServiceImpl implements SolicitudRecuperacionService {

    private final SolicitudRecuperacionRepository solicitudRepository;
    private final UsuarioRepository usuarioRepository;
    private final ObjetoRepository objetoRepository;

    @Override
    @Transactional
    public SolicitudDTO crear(Long objetoId, String descripcionPrivada, String correoUsuario) {
        Usuario usuario = usuarioRepository.findByCorreoInstitucional(correoUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Objeto objeto = objetoRepository.findById(Objects.requireNonNull(objetoId))
                .orElseThrow(() -> new ResourceNotFoundException("Objeto no encontrado"));

        if (objeto.getEstado() != EstadoObjeto.DISPONIBLE_PARA_RECLAMACION) {
            throw new BusinessRuleException("El objeto ya no está disponible para reclamación.");
        }

        SolicitudRecuperacion solicitud = SolicitudRecuperacion.builder()
                .objeto(objeto)
                .usuarioSolicitante(usuario)
                .descripcionPrivada(descripcionPrivada)
                .estado(EstadoSolicitud.PENDIENTE_DE_REVISION)
                .fechaCreacion(LocalDateTime.now())
                .build();

        return SolicitudDTO.from(solicitudRepository.save(solicitud));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SolicitudDTO> listarMias(String correoUsuario, Pageable pageable) {
        Page<SolicitudRecuperacion> page = solicitudRepository.findAll(Objects.requireNonNull(pageable));
        List<SolicitudDTO> dtos = page.getContent().stream()
                .filter(s -> s.getUsuarioSolicitante().getCorreoInstitucional().equals(correoUsuario))
                .map(SolicitudDTO::from)
                .collect(Collectors.toList());
        return new PageImpl<>(dtos, pageable, dtos.size());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SolicitudDTO> listarPendientes(Pageable pageable) {
        Page<SolicitudRecuperacion> page = solicitudRepository.findAll(Objects.requireNonNull(pageable));
        List<SolicitudDTO> dtos = page.getContent().stream()
                .filter(s -> s.getEstado() == EstadoSolicitud.PENDIENTE_DE_REVISION || s.getEstado() == EstadoSolicitud.EN_REVISION)
                .map(SolicitudDTO::from)
                .collect(Collectors.toList());
        return new PageImpl<>(dtos, pageable, dtos.size());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SolicitudDTO> listarAprobadas(Pageable pageable) {
        Page<SolicitudRecuperacion> page = solicitudRepository.findAll(Objects.requireNonNull(pageable));
        List<SolicitudDTO> dtos = page.getContent().stream()
                // Solo mostramos las aprobadas SI el objeto aún NO ha sido entregado
                .filter(s -> s.getEstado() == EstadoSolicitud.APROBADA 
                          && s.getObjeto().getEstado() != EstadoObjeto.ENTREGADO)
                .map(SolicitudDTO::from)
                .collect(Collectors.toList());
        return new PageImpl<>(dtos, pageable, dtos.size());
    }

    @Override
    @Transactional
    public SolicitudDTO resolver(Long id, ResolverSolicitudRequest request, String correoEncargado) {
        SolicitudRecuperacion solicitud = solicitudRepository.findById(Objects.requireNonNull(id))
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud no encontrada"));

        Usuario encargado = usuarioRepository.findByCorreoInstitucional(correoEncargado)
                .orElseThrow(() -> new ResourceNotFoundException("Encargado no encontrado"));

        if (request.decision().equalsIgnoreCase("APROBAR")) {
            solicitud.setEstado(EstadoSolicitud.APROBADA);
            solicitud.getObjeto().setEstado(EstadoObjeto.EN_PROCESO_DE_RECUPERACION);
        } else if (request.decision().equalsIgnoreCase("RECHAZAR")) {
            solicitud.setEstado(EstadoSolicitud.RECHAZADA);
            solicitud.setMotivoRechazo(request.motivo());
        } else {
            throw new BusinessRuleException("Decisión no válida. Use APROBAR o RECHAZAR.");
        }

        solicitud.setEncargadoResponsable(encargado);
        solicitud.setFechaResolucion(LocalDateTime.now());

        return SolicitudDTO.from(solicitudRepository.save(solicitud));
    }
}