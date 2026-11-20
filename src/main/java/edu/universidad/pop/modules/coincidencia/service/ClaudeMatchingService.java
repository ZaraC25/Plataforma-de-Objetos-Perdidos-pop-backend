package edu.universidad.pop.modules.coincidencia.service;

import com.anthropic.client.AnthropicClient;
import com.anthropic.client.okhttp.AnthropicOkHttpClient;
import com.anthropic.core.JsonValue;
import com.anthropic.errors.AnthropicIoException;
import com.anthropic.errors.AnthropicServiceException;
import com.anthropic.errors.BadRequestException;
import com.anthropic.errors.NotFoundException;
import com.anthropic.errors.PermissionDeniedException;
import com.anthropic.errors.RateLimitException;
import com.anthropic.errors.UnauthorizedException;
import com.anthropic.models.messages.MessageCreateParams;
import com.anthropic.models.messages.StopReason;
import com.anthropic.models.messages.StructuredMessage;
import com.anthropic.models.messages.StructuredMessageCreateParams;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import edu.universidad.pop.common.exception.IaNoDisponibleException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Coincidencias con Claude (Anthropic). Se activa cuando ANTHROPIC_API_KEY está configurada.
 * Usa salida estructurada: Claude responde directamente con el JSON del esquema de {@link ResultadoIa}.
 */
@Slf4j
@Service
public class ClaudeMatchingService {

    /** Una coincidencia: un reporte de pérdida y los objetos encontrados que podrían ser el mismo. */
    public record CoincidenciaIa(
            @JsonPropertyDescription("ID del reporte de pérdida") long reporteId,
            @JsonPropertyDescription("IDs de los objetos encontrados que coinciden, del más al menos probable")
            List<Long> objetos) {}

    public record ResultadoIa(
            @JsonPropertyDescription("Solo los reportes que tienen al menos una coincidencia")
            List<CoincidenciaIa> coincidencias) {}

    private static final String INSTRUCCIONES =
            "Eres el asistente de una plataforma universitaria de objetos perdidos. "
            + "Recibirás reportes de pérdida hechos por estudiantes y objetos encontrados en el campus. "
            + "Para cada reporte, indica qué objetos encontrados son probablemente el mismo objeto perdido, "
            + "comparando tipo de objeto, marca, color, rasgos distintivos, categoría y ubicación. "
            + "Las descripciones pueden usar palabras distintas para lo mismo (ej. 'termo' y 'botella térmica'). "
            + "Incluye solo coincidencias razonables; omite los reportes sin coincidencias.";

    @Value("${anthropic.api.key:}")
    private String apiKey;

    @Value("${anthropic.model:claude-opus-5-5}")
    private String model;

    private volatile AnthropicClient client;

    public boolean estaConfigurado() {
        return apiKey != null && !apiKey.isBlank();
    }

    private AnthropicClient cliente() {
        if (client == null) {
            synchronized (this) {
                if (client == null) {
                    client = AnthropicOkHttpClient.builder()
                            .apiKey(apiKey)
                            .timeout(Duration.ofSeconds(90))
                            .build();
                }
            }
        }
        return client;
    }

    /** @return mapa reporteId -> IDs de objetos coincidentes (del más al menos probable) */
    public Map<Long, List<Long>> analizarCoincidencias(String reportesJson, String objetosJson) {
        StructuredMessageCreateParams<ResultadoIa> params = MessageCreateParams.builder()
                .model(model)
                .maxTokens(16000L)
                .system(INSTRUCCIONES)
                .outputConfig(ResultadoIa.class)
                // Si el modelo declina la petición, la API la reintenta con un modelo alterno
                .putAdditionalHeader("anthropic-beta", "server-side-fallback-2026-07-01")
                .putAdditionalBodyProperty("fallbacks", JsonValue.from("default"))
                .addUserMessage("Reportes de pérdida (JSON):\n" + reportesJson
                        + "\n\nObjetos encontrados (JSON):\n" + objetosJson)
                .build();

        StructuredMessage<ResultadoIa> respuesta;
        try {
            respuesta = cliente().messages().create(params);
        } catch (UnauthorizedException e) {
            log.error("Claude rechazó la API key", e);
            throw new IaNoDisponibleException("La API key de Claude (ANTHROPIC_API_KEY) no es válida.");
        } catch (PermissionDeniedException e) {
            log.error("Claude: permiso denegado", e);
            throw new IaNoDisponibleException("La API key de Claude no tiene permiso para usar el modelo '" + model + "'.");
        } catch (NotFoundException e) {
            log.error("Claude: modelo no encontrado", e);
            throw new IaNoDisponibleException("El modelo de Claude '" + model + "' no existe. Revisa ANTHROPIC_MODEL.");
        } catch (RateLimitException e) {
            log.warn("Claude: límite de uso alcanzado", e);
            throw new IaNoDisponibleException("Se alcanzó el límite de uso de Claude. Espera un minuto e intenta de nuevo.");
        } catch (BadRequestException e) {
            log.error("Claude rechazó la petición: {}", e.getMessage(), e);
            String msg = String.valueOf(e.getMessage()).toLowerCase();
            throw new IaNoDisponibleException(msg.contains("credit") || msg.contains("billing")
                    ? "La cuenta de Claude no tiene saldo. Agrega créditos en console.anthropic.com."
                    : "Claude rechazó la petición (400). Revisa los logs del servidor.");
        } catch (AnthropicServiceException e) {
            log.error("Claude respondió {}", e.statusCode(), e);
            throw new IaNoDisponibleException("Claude no está disponible en este momento (" + e.statusCode()
                    + "). Intenta de nuevo en unos segundos.");
        } catch (AnthropicIoException e) {
            log.error("No se pudo conectar con Claude", e);
            throw new IaNoDisponibleException("No se pudo contactar a Claude. Intenta de nuevo.");
        }

        if (respuesta.stopReason().map(StopReason.REFUSAL::equals).orElse(false)) {
            log.warn("Claude declinó la petición: {}", respuesta.stopDetails());
            throw new IaNoDisponibleException("Claude no pudo analizar estos datos. Intenta de nuevo más tarde.");
        }

        Map<Long, List<Long>> resultado = new LinkedHashMap<>();
        respuesta.content().stream()
                .flatMap(bloque -> bloque.text().stream())
                .forEach(texto -> {
                    ResultadoIa datos = texto.text();
                    if (datos == null || datos.coincidencias() == null) return;
                    for (CoincidenciaIa c : datos.coincidencias()) {
                        if (c.objetos() != null && !c.objetos().isEmpty()) {
                            resultado.put(c.reporteId(), c.objetos());
                        }
                    }
                });
        log.info("Claude ({}) encontró coincidencias para {} reportes", model, resultado.size());
        return resultado;
    }
}
