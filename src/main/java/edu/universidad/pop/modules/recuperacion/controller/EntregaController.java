package edu.universidad.pop.modules.recuperacion.controller;

import edu.universidad.pop.common.dto.ApiResponse;
import edu.universidad.pop.modules.recuperacion.dto.RegistrarEntregaRequest;
import edu.universidad.pop.modules.recuperacion.service.EntregaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/entregas")
@RequiredArgsConstructor
public class EntregaController {

    private final EntregaService entregaService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ENCARGADO_OBJETOS_PERDIDOS', 'ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<Void>> registrar(
            @Valid @RequestBody RegistrarEntregaRequest request,
            Authentication authentication) {
        String correo = authentication.getName();
        entregaService.registrar(request, correo);
        return ResponseEntity.ok(ApiResponse.ok("Entrega registrada exitosamente", null));
    }
}