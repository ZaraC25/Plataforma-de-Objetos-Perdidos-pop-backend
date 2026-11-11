package edu.universidad.pop.modules.estadistica.controller;

import edu.universidad.pop.common.dto.ApiResponse;
import edu.universidad.pop.common.enums.EstadoObjeto;
import edu.universidad.pop.common.enums.EstadoSolicitud;
import edu.universidad.pop.modules.objeto.repository.ObjetoRepository;
import edu.universidad.pop.modules.recuperacion.repository.SolicitudRecuperacionRepository;
import edu.universidad.pop.modules.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/estadisticas")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMINISTRADOR')")
public class EstadisticaController {

    private final UsuarioRepository usuarioRepository;
    private final ObjetoRepository objetoRepository;
    private final SolicitudRecuperacionRepository solicitudRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Long>>> obtenerEstadisticas() {
        Map<String, Long> stats = new HashMap<>();
        
        stats.put("totalUsuarios", usuarioRepository.count());
        stats.put("totalObjetos", objetoRepository.count());
        
        // Usamos los nuevos métodos nativos y pasamos el Enum como String (.name())
        stats.put("objetosEntregados", objetoRepository.countByEstadoNative(EstadoObjeto.ENTREGADO.name()));
        stats.put("solicitudesPendientes", solicitudRepository.countByEstadoNative(EstadoSolicitud.PENDIENTE_DE_REVISION.name()));

        return ResponseEntity.ok(ApiResponse.ok(stats));
    }
}