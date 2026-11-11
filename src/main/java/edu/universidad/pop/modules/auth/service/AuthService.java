package edu.universidad.pop.modules.auth.service;

import edu.universidad.pop.modules.auth.dto.LoginRequest;
import edu.universidad.pop.modules.auth.dto.LoginResponse;
import edu.universidad.pop.modules.auth.dto.RefreshRequest;
import edu.universidad.pop.modules.auth.dto.RegistroRequest;
import edu.universidad.pop.modules.usuario.dto.UsuarioDTO;

/**
 * RF-01 — Autenticación: login, registro, refresh de token.
 */
public interface AuthService {

    LoginResponse login(LoginRequest request);

    UsuarioDTO registro(RegistroRequest request);

    LoginResponse refresh(RefreshRequest request);
}