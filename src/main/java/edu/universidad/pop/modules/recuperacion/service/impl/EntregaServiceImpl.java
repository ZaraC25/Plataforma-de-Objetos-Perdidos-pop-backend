package edu.universidad.pop.modules.recuperacion.service.impl;

import edu.universidad.pop.common.enums.EstadoObjeto;
import edu.universidad.pop.common.enums.EstadoSolicitud;
import edu.universidad.pop.common.exception.BusinessRuleException;
import edu.universidad.pop.common.exception.ResourceNotFoundException;
import edu.universidad.pop.modules.objeto.domain.Objeto;
import edu.universidad.pop.modules.objeto.repository.ObjetoRepository;
import edu.universidad.pop.modules.recuperacion.domain.Entrega;
import edu.universidad.pop.modules.recuperacion.domain.SolicitudRecuperacion;
import edu.universidad.pop.modules.recuperacion.dto.EntregaDTO;
import edu.universidad.pop.modules.recuperacion.dto.RegistrarEntregaRequest;
import edu.universidad.pop.modules.recuperacion.repository.EntregaRepository;
import edu.universidad.pop.modules.recuperacion.repository.SolicitudRecuperacionRepository;
import edu.universidad.pop.modules.recuperacion.service.EntregaService;
import edu.universidad.pop.modules.usuario.domain.Usuario;
import edu.universidad.pop.modules.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class EntregaServiceImpl implements EntregaService {

    private final EntregaRepository entregaRepository;
    private final SolicitudRecuperacionRepository solicitudRepository;
    private final ObjetoRepository objetoRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    @Transactional
    //@Auditable(accion = "REGISTRAR_ENTREGA", entidad = "Entrega")
    public EntregaDTO registrar(RegistrarEntregaRequest request, String correoEncargado) {
        SolicitudRecuperacion solicitud = solicitudRepository.findById(Objects.requireNonNull(request.solicitudId()))
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud no encontrada"));

        if (solicitud.getEstado() != EstadoSolicitud.APROBADA) {
            throw new BusinessRuleException("Solo se puede registrar la entrega de una solicitud APROBADA.");
        }

        if (!request.confirmacionRecepcion()) {
            throw new BusinessRuleException("No se puede registrar una entrega sin confirmación de recepción.");
        }

        if (entregaRepository.existsByObjetoId(solicitud.getObjeto().getId())) {
            throw new BusinessRuleException("Este objeto ya tiene una entrega registrada.");
        }

        Usuario encargado = usuarioRepository.findByCorreoInstitucional(correoEncargado)
                .orElseThrow(() -> new ResourceNotFoundException("Encargado no encontrado"));

        Objeto objeto = solicitud.getObjeto();
        if (objeto.getEstado() != EstadoObjeto.EN_PROCESO_DE_RECUPERACION) {
            throw new BusinessRuleException("El objeto debe estar EN_PROCESO_DE_RECUPERACION para registrar la entrega.");
        }

        Entrega entrega = new Entrega();
        entrega.setObjeto(objeto);
        entrega.setSolicitud(solicitud);
        entrega.setEncargadoResponsable(encargado);
        entrega.setConfirmacionRecepcion(true);
        entrega.setFirmaRecepcion(request.firmaRecepcion());
        entrega.setObservaciones(request.observaciones());
        entrega.setFechaEntrega(LocalDateTime.now());

        objeto.setEstado(EstadoObjeto.ENTREGADO);

        Entrega guardada = entregaRepository.save(entrega);
        objetoRepository.save(objeto);

        return EntregaDTO.from(guardada);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EntregaDTO> listar(Pageable pageable) {
        return entregaRepository.findAll(Objects.requireNonNull(pageable))
                .map(EntregaDTO::from);
    }
}