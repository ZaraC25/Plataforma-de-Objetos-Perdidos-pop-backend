package edu.universidad.pop.modules.usuario.dto;

import edu.universidad.pop.common.enums.Rol;
import edu.universidad.pop.modules.usuario.domain.Usuario;

import java.time.LocalDateTime;

public record UsuarioDTO(
        Long id,
        String nombre,
        String correoInstitucional,
        String identificadorInstitucional,
        Rol rol,
        String estadoCuenta,
        LocalDateTime fechaCreacion
) {

    public static UsuarioDTO from(Usuario u) {
        if (u == null) return null;
        return new UsuarioDTO(
                u.getId(),
                u.getNombre(),
                u.getCorreoInstitucional(),
                u.getIdentificadorInstitucional(),
                u.getRol(),
                u.getEstadoCuenta() != null ? u.getEstadoCuenta().name() : null,
                u.getFechaCreacion()
        );
    }
}