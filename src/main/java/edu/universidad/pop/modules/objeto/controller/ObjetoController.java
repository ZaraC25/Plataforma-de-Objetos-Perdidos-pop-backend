package edu.universidad.pop.modules.objeto.controller;

import edu.universidad.pop.common.dto.ApiResponse;
import edu.universidad.pop.common.enums.EstadoObjeto;
import edu.universidad.pop.modules.objeto.dto.CrearObjetoRequest;
import edu.universidad.pop.modules.objeto.dto.ObjetoDTO;
import edu.universidad.pop.modules.objeto.service.ObjetoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * RF-03 / RF-05 — Controlador de Objetos.
 * Las rutas GET son públicas para que cualquiera pueda consultar el catálogo.
 */
@RestController
@RequestMapping("/api/objetos")
@RequiredArgsConstructor
public class ObjetoController {

    private final ObjetoService objetoService;

    /**
     * Listado público de objetos con paginación.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<ObjetoDTO>>> listar(Pageable pageable) {
        Page<ObjetoDTO> objetos = objetoService.listar(pageable);
        return ResponseEntity.ok(ApiResponse.ok(objetos));
    }

    /**
     * Detalle público de un objeto por su ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ObjetoDTO>> obtener(@PathVariable Long id) {
        ObjetoDTO objeto = objetoService.obtener(id);
        return ResponseEntity.ok(ApiResponse.ok(objeto));
    }

    /**
     * Registrar un objeto encontrado (Solo Encargados o Admin).
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ENCARGADO_OBJETOS_PERDIDOS', 'ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<ObjetoDTO>> registrar(
            @Valid @RequestBody CrearObjetoRequest request,
            Authentication authentication) {
        
        String correo = authentication.getName();
        ObjetoDTO nuevoObjeto = objetoService.registrar(request, correo);
        return ResponseEntity.ok(ApiResponse.ok("Objeto registrado exitosamente", nuevoObjeto));
    }

    /**
     * Cambiar el estado de un objeto (Solo Encargados o Admin).
     */
    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAnyRole('ENCARGADO_OBJETOS_PERDIDOS', 'ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<ObjetoDTO>> cambiarEstado(
            @PathVariable Long id,
            @RequestBody CambiarEstadoRequest request) {
            
        ObjetoDTO actualizado = objetoService.cambiarEstado(id, request.estado());
        return ResponseEntity.ok(ApiResponse.ok("Estado actualizado", actualizado));
    }

    // DTO interno para recibir el nuevo estado en el body
    public record CambiarEstadoRequest(EstadoObjeto estado) {}
}