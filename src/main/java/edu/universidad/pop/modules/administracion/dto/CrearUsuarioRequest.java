package edu.universidad.pop.modules.administracion.dto;

import edu.universidad.pop.common.enums.Rol;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CrearUsuarioRequest(
        @NotBlank String nombre,
        @NotBlank @Email String correoInstitucional,
        String identificadorInstitucional,
        @NotBlank @Size(min = 8) String contrasena,
        @NotNull Rol rol
) {}