package edu.universidad.pop.modules.coincidencia.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.universidad.pop.common.dto.ApiResponse;
import edu.universidad.pop.modules.coincidencia.dto.CoincidenciaDTO;
import edu.universidad.pop.modules.coincidencia.service.IaMatchingService;
import edu.universidad.pop.modules.objeto.domain.Objeto;
import edu.universidad.pop.modules.objeto.repository.ObjetoRepository;
import edu.universidad.pop.modules.reporte.domain.ReportePerdida;
import edu.universidad.pop.modules.reporte.repository.ReportePerdidaRepository;
import edu.universidad.pop.modules.usuario.domain.Usuario;
import edu.universidad.pop.modules.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
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
public class CoincidenciaController {

    private final IaMatchingService iaService;
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

        List<ReportePerdida> reportes = reporteRepository.findAll().stream()
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

        // JSON de objetos para la IA
        StringBuilder jsonObjetos = new StringBuilder("[");
        for (Objeto o : objetos) {
            jsonObjetos.append(String.format(
                    "{\"id\":%d,\"titulo\":\"%s\",\"descripcion\":\"%s\",\"categoria\":\"%s\",\"ubicacion\":\"%s\"},",
                    o.getId(),
                    escape(o.getTitulo()),
                    escape(o.getDescripcion()),
                    o.getCategoria().name(),
                    escape(o.getUbicacion())
            ));
        }
        // Elimina la última coma y cierra el array
        String objetosJson = jsonObjetos.substring(0, jsonObjetos.length() - 1) + "]";

        List<CoincidenciaDTO> resultado = new ArrayList<>();

        for (ReportePerdida reporte : reportes) {
            String descripcionReporte = reporte.getDescripcion()
                    + " Categoría: " + reporte.getCategoria().name()
                    + " Última ubicación: " + reporte.getUltimaUbicacion();

            String respuestaIA = iaService.analizarCoincidencias(descripcionReporte, objetosJson);

            try {
                List<Long> idsCoincidentes = objectMapper.readValue(
                        respuestaIA, new TypeReference<List<Long>>() {});

                for (Long idObjeto : idsCoincidentes) {
                    Objeto obj = mapaObjetos.get(idObjeto);
                    if (obj != null) {
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
                }
            } catch (Exception e) {
                // Si la IA no devuelve JSON válido, ignoramos este reporte
            }
        }

        return resultado;
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("\"", "'").replace("\n", " ").replace("\r", "");
    }
}