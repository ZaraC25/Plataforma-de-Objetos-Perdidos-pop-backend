package edu.universidad.pop.modules.recuperacion.service;

import edu.universidad.pop.modules.recuperacion.dto.EntregaDTO;
import edu.universidad.pop.modules.recuperacion.dto.RegistrarEntregaRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * RF-10 — Registro de entregas.
 */
public interface EntregaService {

    EntregaDTO registrar(RegistrarEntregaRequest request, String correoEncargado);

    Page<EntregaDTO> listar(Pageable pageable);
}