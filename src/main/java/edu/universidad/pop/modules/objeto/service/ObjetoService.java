package edu.universidad.pop.modules.objeto.service;

import edu.universidad.pop.common.enums.EstadoObjeto;
import edu.universidad.pop.modules.objeto.dto.CrearObjetoRequest;
import edu.universidad.pop.modules.objeto.dto.ObjetoDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * RF-03 / RF-13 — Servicio de gestión de objetos.
 */
public interface ObjetoService {

    ObjetoDTO registrar(CrearObjetoRequest request, String correoRegistrador);

    ObjetoDTO obtener(Long id);

    Page<ObjetoDTO> listar(Pageable pageable);

    ObjetoDTO cambiarEstado(Long id, EstadoObjeto nuevoEstado);
}