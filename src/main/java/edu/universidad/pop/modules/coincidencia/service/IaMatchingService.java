package edu.universidad.pop.modules.coincidencia.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import edu.universidad.pop.common.exception.IaNoDisponibleException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class IaMatchingService {

    @Value("${gemini.api.key:}")
    private String apiKey;

    /** Modelo configurable: gemini-1.5-flash fue retirado por Google y responde 404. */
    @Value("${gemini.api.model:gemini-2.5-flash}")
    private String model;

    private final ObjectMapper mapper = new ObjectMapper();

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://generativelanguage.googleapis.com/v1beta")
            .codecs(c -> c.defaultCodecs().maxInMemorySize(4 * 1024 * 1024))
            .build();

    /**
     * Analiza todos los reportes contra todos los objetos en UNA sola llamada a Gemini
     * (una llamada por reporte agotaba el límite por minuto de la capa gratuita).
     *
     * @return mapa reporteId -> IDs de objetos coincidentes (del más al menos probable)
     * @throws IaNoDisponibleException si Gemini no está configurado o falla
     */
    public Map<Long, List<Long>> analizarCoincidencias(String reportesJson, String objetosJson) {
        if (apiKey == null || apiKey.isBlank() || apiKey.equals("TU_API_KEY_AQUI")) {
            log.warn("GEMINI_API_KEY no está configurada");
            throw new IaNoDisponibleException("La IA no está configurada en el servidor (falta GEMINI_API_KEY).");
        }

        String prompt = "Eres un asistente de una plataforma universitaria de objetos perdidos.\n"
                + "Reportes de pérdida (JSON): " + reportesJson + "\n"
                + "Objetos encontrados (JSON): " + objetosJson + "\n"
                + "Para cada reporte, identifica qué objetos encontrados son probablemente el mismo objeto perdido "
                + "(considera tipo de objeto, descripción, categoría y ubicación). "
                + "Responde ÚNICAMENTE con un arreglo JSON con este formato: "
                + "[{\"reporteId\": 1, \"objetos\": [3, 7]}]. Ordena los objetos del más al menos probable. "
                + "Omite los reportes sin coincidencias. Si no hay ninguna coincidencia responde [].";

        String response;
        try {
            ObjectNode requestBody = mapper.createObjectNode();
            ObjectNode content = requestBody.putArray("contents").addObject();
            content.put("role", "user");
            content.putArray("parts").addObject().put("text", prompt);
            requestBody.putObject("generationConfig")
                    .put("responseMimeType", "application/json")
                    .put("temperature", 0.1);

            response = webClient.post()
                    .uri("/models/{model}:generateContent", model)
                    .header("x-goog-api-key", apiKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBody.toString())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block(Duration.ofSeconds(60));
        } catch (WebClientResponseException e) {
            log.error("Gemini respondió {} con el modelo '{}': {}",
                    e.getStatusCode(), model, e.getResponseBodyAsString());
            throw new IaNoDisponibleException(mensajeParaUsuario(e));
        } catch (Exception e) {
            log.error("Error llamando a Gemini con el modelo '{}'", model, e);
            throw new IaNoDisponibleException("No se pudo contactar el servicio de IA. Intenta de nuevo.");
        }

        return parsearRespuesta(response);
    }

    private Map<Long, List<Long>> parsearRespuesta(String response) {
        try {
            JsonNode candidates = mapper.readTree(response).path("candidates");
            if (!candidates.isArray() || candidates.isEmpty()) {
                log.warn("Gemini no devolvió candidatos: {}", response);
                throw new IaNoDisponibleException("La IA no devolvió resultados (posible bloqueo de contenido).");
            }
            String texto = candidates.get(0).path("content").path("parts").path(0).path("text").asText("[]")
                    .replace("```json", "").replace("```", "").trim();
            int ini = texto.indexOf('[');
            int fin = texto.lastIndexOf(']');
            if (ini < 0 || fin < ini) {
                log.warn("Respuesta de Gemini sin arreglo JSON: {}", texto);
                throw new IaNoDisponibleException("La IA devolvió una respuesta con formato inesperado.");
            }

            Map<Long, List<Long>> resultado = new LinkedHashMap<>();
            for (JsonNode item : mapper.readTree(texto.substring(ini, fin + 1))) {
                if (!item.hasNonNull("reporteId")) continue;
                List<Long> ids = new ArrayList<>();
                for (JsonNode id : item.path("objetos")) {
                    if (id.canConvertToLong()) ids.add(id.asLong());
                }
                if (!ids.isEmpty()) resultado.put(item.get("reporteId").asLong(), ids);
            }
            return resultado;
        } catch (IaNoDisponibleException e) {
            throw e;
        } catch (Exception e) {
            log.error("No se pudo interpretar la respuesta de Gemini: {}", response, e);
            throw new IaNoDisponibleException("La IA devolvió una respuesta con formato inesperado.");
        }
    }

    private String mensajeParaUsuario(WebClientResponseException e) {
        String cuerpo = e.getResponseBodyAsString();
        return switch (e.getStatusCode().value()) {
            case 400 -> cuerpo.contains("API_KEY_INVALID") || cuerpo.contains("API key not valid")
                    ? "La API key de Gemini configurada en el servidor no es válida."
                    : "Gemini rechazó la petición (400). Revisa los logs del servidor.";
            case 401, 403 -> "La API key de Gemini no tiene permiso (¿API deshabilitada o key restringida?).";
            case 404 -> "El modelo de IA '" + model + "' no está disponible. Configura otro en GEMINI_MODEL.";
            case 429 -> "Se alcanzó el límite de uso de Gemini. Espera un minuto e intenta de nuevo.";
            default -> "El servicio de IA no está disponible en este momento (" + e.getStatusCode().value() + ").";
        };
    }
}
