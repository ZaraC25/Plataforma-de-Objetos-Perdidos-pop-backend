package edu.universidad.pop.modules.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank @Email String correoInstitucional,
        @NotBlank String contrasena
) {}