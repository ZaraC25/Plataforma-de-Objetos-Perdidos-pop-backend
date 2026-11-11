package edu.universidad.pop.modules.objeto.dto;

import edu.universidad.pop.common.enums.CategoriaObjeto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * RF-03 — Datos para registrar un objeto encontrado.
 */
public record CrearObjetoRequest(

        @NotBlank(message = "El título es obligatorio")
        @Size(max = 200, message = "El título no puede superar 200 caracteres")
        String titulo,

        @NotBlank(message = "La descripción es obligatoria")
        String descripcion,

        @NotNull(message = "La categoría es obligatoria")
        CategoriaObjeto categoria,

        @NotNull(message = "La fecha de hallazgo es obligatoria")
        LocalDateTime fechaHallazgo,

        @NotBlank(message = "La ubicación es obligatoria")
        @Size(max = 255, message = "La ubicación no puede superar 255 caracteres")
        String ubicacion
) {}