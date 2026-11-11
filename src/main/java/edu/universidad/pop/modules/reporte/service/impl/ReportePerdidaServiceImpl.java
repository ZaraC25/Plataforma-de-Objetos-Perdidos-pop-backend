package edu.universidad.pop.modules.reporte.service.impl;

import edu.universidad.pop.common.enums.EstadoReporte;
import edu.universidad.pop.common.exception.ResourceNotFoundException;
import edu.universidad.pop.modules.reporte.domain.ReportePerdida;
import edu.universidad.pop.modules.reporte.dto.CrearReporteRequest;
import edu.universidad.pop.modules.reporte.dto.ReportePerdidaDTO;
import edu.universidad.pop.modules.reporte.repository.ReportePerdidaRepository;
import edu.universidad.pop.modules.reporte.service.ReportePerdidaService;
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
public class ReportePerdidaServiceImpl implements ReportePerdidaService {

    private final ReportePerdidaRepository reporteRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    @Transactional
    public ReportePerdidaDTO crear(CrearReporteRequest request, String correoUsuario) {
        Usuario usuario = usuarioRepository.findByCorreoInstitucional(correoUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        ReportePerdida reporte = ReportePerdida.builder()
                .usuarioReporta(usuario)
                .descripcion(request.descripcion())
                .categoria(request.categoria())
                .fechaPerdida(request.fechaPerdida())
                .ultimaUbicacion(request.ultimaUbicacion())
                .estado(EstadoReporte.ACTIVO)
                .fechaCreacion(LocalDateTime.now())
                .build();

        return ReportePerdidaDTO.from(reporteRepository.save(reporte));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReportePerdidaDTO> listarMios(String correoUsuario, Pageable pageable) {
        Page<ReportePerdida> page = reporteRepository.findAll(Objects.requireNonNull(pageable));
        List<ReportePerdidaDTO> dtos = page.getContent().stream()
                .filter(r -> r.getUsuarioReporta().getCorreoInstitucional().equals(correoUsuario))
                .map(ReportePerdidaDTO::from)
                .collect(Collectors.toList());
        return new PageImpl<>(dtos, pageable, dtos.size());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReportePerdidaDTO> listarTodos(Pageable pageable) {
        // Forzamos la conversión a DTO dentro de la transacción para evitar LazyInitializationException
        Page<ReportePerdida> page = reporteRepository.findAll(Objects.requireNonNull(pageable));
        List<ReportePerdidaDTO> dtos = page.getContent().stream()
                .map(ReportePerdidaDTO::from)
                .collect(Collectors.toList());
        return new PageImpl<>(dtos, pageable, page.getTotalElements());
    }
}