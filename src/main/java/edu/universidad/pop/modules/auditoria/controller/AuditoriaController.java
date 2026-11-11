package edu.universidad.pop.modules.auditoria.controller;

import edu.universidad.pop.common.dto.ApiResponse;
import edu.universidad.pop.modules.auditoria.domain.RegistroAuditoria;
import edu.universidad.pop.modules.auditoria.repository.RegistroAuditoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/api/admin/auditoria")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMINISTRADOR')")
@SuppressWarnings("null") // <-- Ignora las advertencias de nulos
public class AuditoriaController {

    private final RegistroAuditoriaRepository auditoriaRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<RegistroAuditoria>>> listar(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(auditoriaRepository.findAll(Objects.requireNonNull(pageable))));
    }
}