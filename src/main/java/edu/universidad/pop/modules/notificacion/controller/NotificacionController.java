package edu.universidad.pop.modules.notificacion.controller;

import edu.universidad.pop.common.dto.ApiResponse;
import edu.universidad.pop.modules.notificacion.dto.NotificacionDTO;
import edu.universidad.pop.modules.notificacion.service.NotificacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * RF-12 — Notificaciones para el usuario autenticado.
 */
@RestController
@RequestMapping("/api/notificaciones")
@RequiredArgsConstructor
public class NotificacionController {

    private final NotificacionService notificacionService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<NotificacionDTO>>> listar(
            @RequestParam(required = false, defaultValue = "false") boolean leida,
            Pageable pageable,
            Authentication authentication) {
        
        String correo = authentication.getName();
        Page<NotificacionDTO> notificaciones = notificacionService.listarPorUsuario(correo, leida, pageable);
        return ResponseEntity.ok(ApiResponse.ok(notificaciones));
    }

    @PatchMapping("/{id}/leer")
    public ResponseEntity<ApiResponse<Void>> marcarComoLeida(@PathVariable Long id) {
        notificacionService.marcarComoLeida(id);
        return ResponseEntity.ok(ApiResponse.ok("Notificación marcada como leída", null));
    }
}