package edu.universidad.pop.modules.reporte.controller;

import edu.universidad.pop.common.dto.ApiResponse;
import edu.universidad.pop.modules.reporte.dto.CrearReporteRequest;
import edu.universidad.pop.modules.reporte.dto.ReportePerdidaDTO;
import edu.universidad.pop.modules.reporte.service.ReportePerdidaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
public class ReportePerdidaController {

    private final ReportePerdidaService reporteService;

    @PostMapping
    public ResponseEntity<ApiResponse<ReportePerdidaDTO>> crear(
            @Valid @RequestBody CrearReporteRequest request,
            Authentication authentication) {
        String correo = authentication.getName();
        ReportePerdidaDTO nuevo = reporteService.crear(request, correo);
        return ResponseEntity.ok(ApiResponse.ok("Reporte creado", nuevo));
    }

    @GetMapping("/mios")
    public ResponseEntity<ApiResponse<Page<ReportePerdidaDTO>>> listarMios(
            Pageable pageable, Authentication authentication) {
        String correo = authentication.getName();
        Page<ReportePerdidaDTO> reportes = reporteService.listarMios(correo, pageable);
        return ResponseEntity.ok(ApiResponse.ok(reportes));
    }

    // --- NUEVO ENDPOINT PARA ADMIN/ENCARGADO ---
    @GetMapping("/todos")
    @PreAuthorize("hasAnyRole('ENCARGADO_OBJETOS_PERDIDOS', 'ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<Page<ReportePerdidaDTO>>> listarTodos(Pageable pageable) {
        Page<ReportePerdidaDTO> reportes = reporteService.listarTodos(pageable);
        return ResponseEntity.ok(ApiResponse.ok(reportes));
    }
}