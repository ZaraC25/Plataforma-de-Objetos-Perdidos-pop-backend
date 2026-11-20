package edu.universidad.pop.modules.coincidencia.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;

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
            .codecs(c -> c.defaultCodecs().maxInMemorySize(2 * 1024 * 1024))
            .build();

    public String analizarCoincidencias(String reportePerdida, String objetosEncontrados) {
        if (apiKey == null || apiKey.isBlank() || apiKey.equals("TU_API_KEY_AQUI")) {
            log.warn("GEMINI_API_KEY no está configurada; no se analizan coincidencias");
            return "[]";
        }

        String prompt = "Eres un asistente de una plataforma universitaria. Un estudiante perdió un objeto descrito así: \""
                + reportePerdida + "\".\n"
                + "Tenemos estos objetos encontrados (en formato JSON): " + objetosEncontrados + ".\n"
                + "Analiza cuáles objetos son probables coincidencias. Devuelve ÚNICAMENTE un arreglo JSON con los IDs "
                + "de los objetos que consideres coincidencias, ordenados del más probable al menos probable. "
                + "Si ninguno coincide devuelve []. Ejemplo: [3, 1]";

        try {
            ObjectNode requestBody = mapper.createObjectNode();
            ArrayNode contents = requestBody.putArray("contents");
            ObjectNode content = contents.addObject();
            content.put("role", "user");
            content.putArray("parts").addObject().put("text", prompt);
            // Forzar salida JSON pura
            requestBody.putObject("generationConfig")
                    .put("responseMimeType", "application/json")
                    .put("temperature", 0.1);

            String response = webClient.post()
                    .uri("/models/{model}:generateContent", model)
                    .header("x-goog-api-key", apiKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBody.toString())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block(Duration.ofSeconds(30));

            JsonNode root = mapper.readTree(response);
            JsonNode candidates = root.path("candidates");
            if (!candidates.isArray() || candidates.isEmpty()) {
                log.warn("Gemini no devolvió candidatos: {}", response);
                return "[]";
            }

            String texto = candidates.get(0).path("content").path("parts").path(0).path("text").asText("[]");
            texto = texto.replace("```json", "").replace("```", "").trim();

            // Extraer solo el arreglo por si el modelo agrega texto alrededor
            int ini = texto.indexOf('[');
            int fin = texto.lastIndexOf(']');
            if (ini < 0 || fin < ini) {
                log.warn("Respuesta de Gemini sin arreglo JSON: {}", texto);
                return "[]";
            }
            return texto.substring(ini, fin + 1);

        } catch (WebClientResponseException e) {
            log.error("Gemini respondió {} con el modelo '{}': {}",
                    e.getStatusCode(), model, e.getResponseBodyAsString());
            return "[]";
        } catch (Exception e) {
            log.error("Error llamando a Gemini con el modelo '{}'", model, e);
            return "[]";
        }
    }
}
