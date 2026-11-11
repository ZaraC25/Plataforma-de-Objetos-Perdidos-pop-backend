package edu.universidad.pop.modules.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegistroRequest(
        @NotBlank @Size(max = 150) String nombre,
        @NotBlank @Email String correoInstitucional,
        @Size(max = 50) String identificadorInstitucional,
        @NotBlank @Size(min = 8, max = 100) String contrasena
) {}