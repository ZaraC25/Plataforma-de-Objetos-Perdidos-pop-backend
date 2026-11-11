package edu.universidad.pop.modules.auth.service.impl;

import edu.universidad.pop.common.enums.EstadoCuenta;
import edu.universidad.pop.common.enums.Rol;
import edu.universidad.pop.common.exception.BusinessRuleException;
import edu.universidad.pop.common.exception.ResourceNotFoundException;
import edu.universidad.pop.modules.auth.dto.LoginRequest;
import edu.universidad.pop.modules.auth.dto.LoginResponse;
import edu.universidad.pop.modules.auth.dto.RefreshRequest;
import edu.universidad.pop.modules.auth.dto.RegistroRequest;
import edu.universidad.pop.modules.auth.security.JwtService;
import edu.universidad.pop.modules.auth.security.UsuarioPrincipal;
import edu.universidad.pop.modules.auth.service.AuthService;
import edu.universidad.pop.modules.usuario.domain.Usuario;
import edu.universidad.pop.modules.usuario.dto.UsuarioDTO;
import edu.universidad.pop.modules.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository
                .findByCorreoInstitucional(request.correoInstitucional())
                .orElseThrow(() -> new BusinessRuleException("Credenciales inválidas"));

        if (!passwordEncoder.matches(request.contrasena(), usuario.getContrasenaHash())) {
            throw new BusinessRuleException("Credenciales inválidas");
        }

        if (usuario.getEstadoCuenta() != EstadoCuenta.ACTIVA) {
            throw new BusinessRuleException("La cuenta no está activa");
        }

        UsuarioPrincipal principal = new UsuarioPrincipal(usuario);
        String accessToken = jwtService.generarAccessToken(principal);
        String refreshToken = jwtService.generarRefreshToken(principal);

        return new LoginResponse(accessToken, refreshToken, UsuarioDTO.from(usuario));
    }

    @Override
    @Transactional
    public UsuarioDTO registro(RegistroRequest request) {
        if (usuarioRepository.findByCorreoInstitucional(request.correoInstitucional()).isPresent()) {
            throw new BusinessRuleException("El correo ya está registrado");
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(request.nombre());
        usuario.setCorreoInstitucional(request.correoInstitucional());
        
        // SOLUCIÓN: Si el identificador viene vacío, lo guardamos como null para evitar el error de "unique constraint"
        String idInst = request.identificadorInstitucional();
        usuario.setIdentificadorInstitucional((idInst == null || idInst.trim().isEmpty()) ? null : idInst.trim());
        
        usuario.setContrasenaHash(passwordEncoder.encode(request.contrasena()));
        usuario.setRol(Rol.ESTUDIANTE);
        usuario.setEstadoCuenta(EstadoCuenta.ACTIVA);

        Usuario guardado = usuarioRepository.save(usuario);
        return UsuarioDTO.from(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public LoginResponse refresh(RefreshRequest request) {
        String correo;
        try {
            correo = jwtService.extraerCorreo(request.refreshToken());
        } catch (Exception e) {
            throw new BusinessRuleException("Refresh token inválido o expirado");
        }

        Usuario usuario = usuarioRepository.findByCorreoInstitucional(correo)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        UsuarioPrincipal principal = new UsuarioPrincipal(usuario);
        String accessToken = jwtService.generarAccessToken(principal);
        String refreshToken = jwtService.generarRefreshToken(principal);

        return new LoginResponse(accessToken, refreshToken, UsuarioDTO.from(usuario));
    }
}