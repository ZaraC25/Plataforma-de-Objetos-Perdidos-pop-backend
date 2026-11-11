package edu.universidad.pop.modules.administracion.controller;

import edu.universidad.pop.common.dto.ApiResponse;
import edu.universidad.pop.common.enums.EstadoCuenta;
import edu.universidad.pop.common.exception.BusinessRuleException;
import edu.universidad.pop.modules.administracion.dto.CrearUsuarioRequest;
import edu.universidad.pop.modules.usuario.domain.Usuario;
import edu.universidad.pop.modules.usuario.dto.UsuarioDTO;
import edu.universidad.pop.modules.usuario.repository.UsuarioRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/admin/usuarios")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMINISTRADOR')")
@SuppressWarnings("null")
public class AdminController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<UsuarioDTO>>> listar(Pageable pageable) {
        Page<UsuarioDTO> usuarios = usuarioRepository.findAll(Objects.requireNonNull(pageable))
                .map(UsuarioDTO::from);
        return ResponseEntity.ok(ApiResponse.ok(usuarios));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UsuarioDTO>> crear(@Valid @RequestBody CrearUsuarioRequest request) {
        if (usuarioRepository.findByCorreoInstitucional(request.correoInstitucional()).isPresent()) {
            throw new BusinessRuleException("El correo ya está registrado");
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(request.nombre());
        usuario.setCorreoInstitucional(request.correoInstitucional());
        
        String idInst = request.identificadorInstitucional();
        usuario.setIdentificadorInstitucional((idInst == null || idInst.trim().isEmpty()) ? null : idInst.trim());
        
        usuario.setContrasenaHash(passwordEncoder.encode(request.contrasena()));
        usuario.setRol(request.rol());
        usuario.setEstadoCuenta(EstadoCuenta.ACTIVA);

        Usuario guardado = usuarioRepository.save(usuario);
        return ResponseEntity.ok(ApiResponse.ok("Usuario creado", UsuarioDTO.from(guardado)));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<ApiResponse<Void>> cambiarEstado(@PathVariable Long id, @RequestBody Map<String, String> body) {
        Usuario usuario = usuarioRepository.findById(Objects.requireNonNull(id)).orElseThrow();
        String nuevoEstado = body.get("estado");
        
        try {
            usuario.setEstadoCuenta(EstadoCuenta.valueOf(nuevoEstado));
            usuarioRepository.save(usuario);
            return ResponseEntity.ok(ApiResponse.ok("Estado actualizado", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Estado inválido"));
        }
    }
}