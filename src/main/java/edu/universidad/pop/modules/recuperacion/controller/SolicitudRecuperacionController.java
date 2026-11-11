package edu.universidad.pop.modules.recuperacion.controller;

import edu.universidad.pop.common.dto.ApiResponse;
import edu.universidad.pop.modules.recuperacion.dto.ResolverSolicitudRequest;
import edu.universidad.pop.modules.recuperacion.dto.SolicitudDTO;
import edu.universidad.pop.modules.recuperacion.service.SolicitudRecuperacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/solicitudes")
@RequiredArgsConstructor
public class SolicitudRecuperacionController {

    private final SolicitudRecuperacionService solicitudService;

    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<ApiResponse<SolicitudDTO>> crear(
            @RequestParam("objetoId") Long objetoId,
            @RequestParam("descripcionPrivada") String descripcionPrivada,
            @RequestParam(value = "evidencia", required = false) MultipartFile evidencia,
            Authentication authentication) {
        String correo = authentication.getName();
        SolicitudDTO nueva = solicitudService.crear(objetoId, descripcionPrivada, correo);
        return ResponseEntity.ok(ApiResponse.ok("Solicitud creada exitosamente", nueva));
    }

    @GetMapping("/mias")
    public ResponseEntity<ApiResponse<Page<SolicitudDTO>>> listarMias(
            Pageable pageable, Authentication authentication) {
        String correo = authentication.getName();
        Page<SolicitudDTO> solicitudes = solicitudService.listarMias(correo, pageable);
        return ResponseEntity.ok(ApiResponse.ok(solicitudes));
    }

    @GetMapping("/pendientes")
    @PreAuthorize("hasAnyRole('ENCARGADO_OBJETOS_PERDIDOS', 'ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<Page<SolicitudDTO>>> listarPendientes(Pageable pageable) {
        Page<SolicitudDTO> solicitudes = solicitudService.listarPendientes(pageable);
        return ResponseEntity.ok(ApiResponse.ok(solicitudes));
    }

    @GetMapping("/aprobadas")
    @PreAuthorize("hasAnyRole('ENCARGADO_OBJETOS_PERDIDOS', 'ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<Page<SolicitudDTO>>> listarAprobadas(Pageable pageable) {
        Page<SolicitudDTO> solicitudes = solicitudService.listarAprobadas(pageable);
        return ResponseEntity.ok(ApiResponse.ok(solicitudes));
    }

    @PatchMapping("/{id}/resolver")
    @PreAuthorize("hasAnyRole('ENCARGADO_OBJETOS_PERDIDOS', 'ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<SolicitudDTO>> resolver(
            @PathVariable Long id,
            @RequestBody ResolverSolicitudRequest request,
            Authentication authentication) {
        String correoEncargado = authentication.getName();
        SolicitudDTO resuelta = solicitudService.resolver(id, request, correoEncargado);
        return ResponseEntity.ok(ApiResponse.ok("Solicitud procesada", resuelta));
    }
}