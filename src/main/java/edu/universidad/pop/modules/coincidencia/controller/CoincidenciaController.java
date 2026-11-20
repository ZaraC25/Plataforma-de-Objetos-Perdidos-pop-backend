package edu.universidad.pop.modules.coincidencia.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.universidad.pop.common.dto.ApiResponse;
import edu.universidad.pop.modules.coincidencia.dto.CoincidenciaDTO;
import edu.universidad.pop.modules.coincidencia.service.ClaudeMatchingService;
import edu.universidad.pop.modules.coincidencia.service.IaMatchingService;
import edu.universidad.pop.modules.objeto.domain.Objeto;
import edu.universidad.pop.modules.objeto.repository.ObjetoRepository;
import edu.universidad.pop.modules.reporte.domain.ReportePerdida;
import edu.universidad.pop.modules.reporte.repository.ReportePerdidaRepository;
import edu.universidad.pop.modules.usuario.domain.Usuario;
import edu.universidad.pop.modules.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/coincidencias")
@RequiredArgsConstructor
@Slf4j
public class CoincidenciaController {

    private final IaMatchingService iaService;
    private final ClaudeMatchingService claudeService;
    private final ObjetoRepository objetoRepository;
    private final ReportePerdidaRepository reporteRepository;
    private final UsuarioRepository usuarioRepository;
    private final ObjectMapper objectMapper;

    /**
     * Para ESTUDIANTE: analiza solo sus propios reportes de pérdida
     * contra los objetos disponibles y devuelve coincidencias.
     */
    @GetMapping("/mis-reportes")
    @PreAuthorize("hasRole('ESTUDIANTE')")
    public ResponseEntity<ApiResponse<List<CoincidenciaDTO>>> coincidenciasMisReportes(
            Authentication authentication) {
        log.info("=== /coincidencias/mis-reportes llamado por: {} con roles: {}",
                authentication.getName(), authentication.getAuthorities());

        String correo = authentication.getName();
        Usuario usuario = usuarioRepository.findByCorreoInstitucional(correo)
                .orElseThrow();

        List<ReportePerdida> misReportes = reporteRepository.findByUsuarioReporta_Id(usuario.getId()).stream()
                .filter(r -> r.getEstado().name().equals("ACTIVO") || r.getEstado().name().equals("POSIBLE_COINCIDENCIA"))
                .collect(Collectors.toList());

        List<Objeto> objetosDisponibles = objetoRepository.findAll().stream()
                .filter(o -> o.getEstado().name().equals("DISPONIBLE_PARA_RECLAMACION"))
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.ok(
                analizarCoincidencias(misReportes, objetosDisponibles)
        ));
    }

    /**
     * Para ADMINISTRADOR / ENCARGADO: analiza todos los reportes activos
     * contra todos los objetos disponibles.
     */
    @GetMapping("/todos")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ENCARGADO_OBJETOS_PERDIDOS')")
    public ResponseEntity<ApiResponse<List<CoincidenciaDTO>>> coincidenciasTodos() {
        log.info("=== /coincidencias/todos llamado");

        List<ReportePerdida> reportes = reporteRepository.findAllConUsuario().stream()
                .filter(r -> r.getEstado().name().equals("ACTIVO") || r.getEstado().name().equals("POSIBLE_COINCIDENCIA"))
                .collect(Collectors.toList());

        List<Objeto> objetosDisponibles = objetoRepository.findAll().stream()
                .filter(o -> o.getEstado().name().equals("DISPONIBLE_PARA_RECLAMACION"))
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.ok(
                analizarCoincidencias(reportes, objetosDisponibles)
        ));
    }

    // ---------- helpers ----------

    private List<CoincidenciaDTO> analizarCoincidencias(
            List<ReportePerdida> reportes, List<Objeto> objetos) {

        if (reportes.isEmpty() || objetos.isEmpty()) return List.of();

        // Mapa para lookup rápido por ID
        Map<Long, Objeto> mapaObjetos = objetos.stream()
                .collect(Collectors.toMap(Objeto::getId, o -> o));

        Map<Long, ReportePerdida> mapaReportes = reportes.stream()
                .collect(Collectors.toMap(ReportePerdida::getId, r -> r));

        // JSON para la IA (Jackson se encarga de escapar comillas y saltos de línea)
        List<Map<String, Object>> objetosIa = objetos.stream()
                .map(o -> Map.<String, Object>of(
                        "id", o.getId(),
                        "titulo", nvl(o.getTitulo()),
                        "descripcion", nvl(o.getDescripcion()),
                        "categoria", o.getCategoria().name(),
                        "ubicacion", nvl(o.getUbicacion())))
                .collect(Collectors.toList());
        List<Map<String, Object>> reportesIa = reportes.stream()
                .map(r -> Map.<String, Object>of(
                        "reporteId", r.getId(),
                        "descripcion", nvl(r.getDescripcion()),
                        "categoria", r.getCategoria().name(),
                        "ultimaUbicacion", nvl(r.getUltimaUbicacion())))
                .collect(Collectors.toList());

        String objetosJson;
        String reportesJson;
        try {
            objetosJson = objectMapper.writeValueAsString(objetosIa);
            reportesJson = objectMapper.writeValueAsString(reportesIa);
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo serializar los datos para la IA", e);
        }

        // Una sola llamada a la IA para todos los reportes: Claude si ANTHROPIC_API_KEY está configurada, si no Gemini
        Map<Long, List<Long>> coincidencias = claudeService.estaConfigurado()
                ? claudeService.analizarCoincidencias(reportesJson, objetosJson)
                : iaService.analizarCoincidencias(reportesJson, objetosJson);
        log.info("IA devolvió coincidencias para {} de {} reportes", coincidencias.size(), reportes.size());

        List<CoincidenciaDTO> resultado = new ArrayList<>();
        coincidencias.forEach((reporteId, idsObjetos) -> {
            ReportePerdida reporte = mapaReportes.get(reporteId);
            if (reporte == null) return; // la IA inventó un ID: se ignora
            for (Long idObjeto : idsObjetos) {
                Objeto obj = mapaObjetos.get(idObjeto);
                if (obj == null) continue;
                resultado.add(new CoincidenciaDTO(
                        reporte.getId(),
                        reporte.getDescripcion(),
                        reporte.getCategoria().name(),
                        reporte.getUltimaUbicacion(),
                        reporte.getUsuarioReporta().getNombre(),
                        reporte.getUsuarioReporta().getCorreoInstitucional(),
                        obj.getId(),
                        obj.getTitulo(),
                        obj.getDescripcion(),
                        obj.getCategoria().name(),
                        obj.getUbicacion()
                ));
            }
        });

        return resultado;
    }

    private String nvl(String s) {
        return s == null ? "" : s;
    }

}