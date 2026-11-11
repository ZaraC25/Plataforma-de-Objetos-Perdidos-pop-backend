package edu.universidad.pop.modules.auth.controller;

import edu.universidad.pop.common.dto.ApiResponse;
import edu.universidad.pop.modules.auth.dto.LoginRequest;
import edu.universidad.pop.modules.auth.dto.LoginResponse;
import edu.universidad.pop.modules.auth.dto.RefreshRequest;
import edu.universidad.pop.modules.auth.dto.RegistroRequest;
import edu.universidad.pop.modules.auth.service.AuthService;
import edu.universidad.pop.modules.usuario.dto.UsuarioDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * RF-01 — Autenticación.
 * Endpoints públicos: /api/auth/login, /api/auth/registro, /api/auth/refresh.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PostMapping("/registro")
    public ResponseEntity<ApiResponse<UsuarioDTO>> registro(
            @Valid @RequestBody RegistroRequest request) {
        UsuarioDTO usuario = authService.registro(request);
        return ResponseEntity.ok(ApiResponse.ok("Usuario registrado", usuario));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<LoginResponse>> refresh(
            @Valid @RequestBody RefreshRequest request) {
        LoginResponse response = authService.refresh(request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}