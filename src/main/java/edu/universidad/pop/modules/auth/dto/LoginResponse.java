package edu.universidad.pop.modules.auth.dto;

import edu.universidad.pop.modules.usuario.dto.UsuarioDTO;

public record LoginResponse(
        String token,
        String refreshToken,
        UsuarioDTO usuario
) {}