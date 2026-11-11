package edu.universidad.pop.modules.reporte.service;

import edu.universidad.pop.modules.reporte.dto.CrearReporteRequest;
import edu.universidad.pop.modules.reporte.dto.ReportePerdidaDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReportePerdidaService {
    ReportePerdidaDTO crear(CrearReporteRequest request, String correoUsuario);
    Page<ReportePerdidaDTO> listarMios(String correoUsuario, Pageable pageable);
    Page<ReportePerdidaDTO> listarTodos(Pageable pageable);
}