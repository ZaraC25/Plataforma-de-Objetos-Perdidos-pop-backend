package edu.universidad.pop.modules.coincidencia.controller;

import edu.universidad.pop.common.dto.ApiResponse;
import edu.universidad.pop.modules.coincidencia.service.IaMatchingService;
import edu.universidad.pop.modules.objeto.domain.Objeto;
import edu.universidad.pop.modules.objeto.repository.ObjetoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/coincidencias")
@RequiredArgsConstructor
public class CoincidenciaController {

    private final IaMatchingService iaService;
    private final ObjetoRepository objetoRepository;

    @GetMapping("/buscar")
    @PreAuthorize("hasAnyRole('ESTUDIANTE', 'ENCARGADO_OBJETOS_PERDIDOS', 'ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<String>> buscarCoincidencias(@RequestParam String descripcion) {
        // 1. Traer todos los objetos disponibles
        List<Objeto> objetos = objetoRepository.findAll().stream()
                .filter(o -> o.getEstado().name().equals("DISPONIBLE_PARA_RECLAMACION"))
                .collect(Collectors.toList());

        if (objetos.isEmpty()) {
            return ResponseEntity.ok(ApiResponse.ok("[]"));
        }

        // 2. Crear un resumen en texto o JSON de los objetos para enviarlo a la IA
        StringBuilder jsonObjetos = new StringBuilder("[");
        for (Objeto o : objetos) {
            jsonObjetos.append(String.format("{\"id\": %d, \"titulo\": \"%s\", \"descripcion\": \"%s\"},",
                    o.getId(), o.getTitulo(), o.getDescripcion().replace("\"", "'")));
        }
        jsonObjetos.append("]");

        // 3. Llamar a la IA
        String resultadoIA = iaService.analizarCoincidencias(descripcion, jsonObjetos.toString());
        
        return ResponseEntity.ok(ApiResponse.ok(resultadoIA));
    }
}