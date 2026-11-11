package edu.universidad.pop.modules.objeto.service.impl;

import edu.universidad.pop.common.enums.EstadoObjeto;
import edu.universidad.pop.common.exception.BusinessRuleException;
import edu.universidad.pop.common.exception.ResourceNotFoundException;
import edu.universidad.pop.modules.auditoria.aspect.Auditable;
import edu.universidad.pop.modules.objeto.domain.Objeto;
import edu.universidad.pop.modules.objeto.dto.CrearObjetoRequest;
import edu.universidad.pop.modules.objeto.dto.ObjetoDTO;
import edu.universidad.pop.modules.objeto.mapper.ObjetoMapper;
import edu.universidad.pop.modules.objeto.repository.ObjetoRepository;
import edu.universidad.pop.modules.objeto.service.ObjetoService;
import edu.universidad.pop.modules.usuario.domain.Usuario;
import edu.universidad.pop.modules.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * RF-03 — Registro de objetos encontrados.
 * RF-13 — Administración de inventario.
 * Regla de negocio #9 — Las operaciones críticas mantienen la consistencia de estados.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@SuppressWarnings("null") // <-- Agregado para quitar el warning de nulos
public class ObjetoServiceImpl implements ObjetoService {

    private final ObjetoRepository objetoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ObjetoMapper objetoMapper;

    @Override
    @Transactional
    // @Auditable(accion = "CREAR_OBJETO", entidad = "Objeto") <-- COMENTA ESTA LÍNEA
    public ObjetoDTO registrar(CrearObjetoRequest request, String correoRegistrador) {
        Usuario registrador = usuarioRepository.findByCorreoInstitucional(correoRegistrador)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Objeto objeto = objetoMapper.toEntity(request);
        objeto.setRegistradoPor(registrador);
        objeto.setEstado(EstadoObjeto.DISPONIBLE_PARA_RECLAMACION);

        return objetoMapper.toDTO(objetoRepository.save(objeto));
    }

    @Override
    public ObjetoDTO obtener(Long id) {
        return objetoRepository.findById(Objects.requireNonNull(id))
                .map(objetoMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Objeto no encontrado: " + id));
    }

    @Override
    public Page<ObjetoDTO> listar(Pageable pageable) {
        Page<Objeto> objetos = objetoRepository.findAll(Objects.requireNonNull(pageable));
        
        // Convertimos a DTO manualmente para evitar el error LazyInitializationException
        List<ObjetoDTO> dtos = objetos.getContent().stream()
                .map(objetoMapper::toDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(dtos, pageable, objetos.getTotalElements());
    }

    @Override
    @Transactional
    @Auditable(accion = "CAMBIAR_ESTADO_OBJETO", entidad = "Objeto")
    public ObjetoDTO cambiarEstado(Long id, EstadoObjeto nuevoEstado) {
        Objeto objeto = objetoRepository.findById(Objects.requireNonNull(id))
                .orElseThrow(() -> new ResourceNotFoundException("Objeto no encontrado: " + id));

        validarTransicion(objeto.getEstado(), nuevoEstado);
        objeto.setEstado(nuevoEstado);

        return objetoMapper.toDTO(objetoRepository.save(objeto));
    }

    /**
     * Regla #7 y #9: un objeto solo puede pasar a ENTREGADO
     * después de registrar una entrega efectiva.
     */
    private void validarTransicion(EstadoObjeto actual, EstadoObjeto nuevo) {
        if (nuevo == EstadoObjeto.ENTREGADO && actual != EstadoObjeto.EN_PROCESO_DE_RECUPERACION) {
            throw new BusinessRuleException(
                    "Un objeto solo puede marcarse como ENTREGADO desde EN_PROCESO_DE_RECUPERACION.");
        }
        if (actual == EstadoObjeto.ENTREGADO) {
            throw new BusinessRuleException("Un objeto ya entregado no puede cambiar de estado.");
        }
    }
}