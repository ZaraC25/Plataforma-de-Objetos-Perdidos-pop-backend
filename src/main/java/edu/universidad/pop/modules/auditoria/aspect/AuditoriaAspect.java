package edu.universidad.pop.modules.auditoria.aspect;

import edu.universidad.pop.modules.auditoria.domain.RegistroAuditoria;
import edu.universidad.pop.modules.auditoria.repository.RegistroAuditoriaRepository;
import edu.universidad.pop.modules.usuario.domain.Usuario;
import edu.universidad.pop.modules.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings("null")
public class AuditoriaAspect {

    private final RegistroAuditoriaRepository auditoriaRepository;
    private final UsuarioRepository usuarioRepository;

    @Around("@annotation(auditable)")
    public Object auditar(ProceedingJoinPoint joinPoint, Auditable auditable) throws Throwable {
        Object resultado = null;
        String estado = "EXITO";
        String detalleJson = "{\"mensaje\": \"Operación exitosa\"}";

        try {
            resultado = joinPoint.proceed();
            return resultado;
        } catch (Throwable e) {
            estado = "ERROR";
            // Escapamos las comillas para que sea un JSON válido
            String errorMsg = e.getMessage() != null ? e.getMessage().replace("\"", "'") : "Error desconocido";
            detalleJson = "{\"error\": \"" + errorMsg + "\"}";
            throw e;
        } finally {
            guardarAuditoria(auditable, resultado, estado, detalleJson);
        }
    }

    private void guardarAuditoria(Auditable auditable, Object resultado, String estado, String detalleJson) {
        try {
            String correo = SecurityContextHolder.getContext().getAuthentication() != null ?
                    SecurityContextHolder.getContext().getAuthentication().getName() : null;

            Usuario usuario = null;
            if (correo != null && !correo.equals("anonymousUser")) {
                usuario = usuarioRepository.findByCorreoInstitucional(correo).orElse(null);
            }

            Long idAfectado = null;
            if (resultado != null) {
                try {
                    // Intentamos sacar el ID del objeto retornado usando reflexión
                    var method = resultado.getClass().getMethod("getId");
                    idAfectado = (Long) method.invoke(resultado);
                } catch (Exception ignored) {}
            }

            RegistroAuditoria registro = RegistroAuditoria.builder()
                    .usuarioResponsable(usuario)
                    .accion(auditable.accion())
                    .entidadAfectada(auditable.entidad())
                    .registroAfectadoId(idAfectado)
                    .resultado(estado)
                    .detalle(detalleJson) // Pasamos un JSON válido
                    .fechaHora(LocalDateTime.now())
                    .build();

            auditoriaRepository.save(registro);
        } catch (Exception e) {
            log.error("No se pudo guardar la auditoría", e);
        }
    }
}