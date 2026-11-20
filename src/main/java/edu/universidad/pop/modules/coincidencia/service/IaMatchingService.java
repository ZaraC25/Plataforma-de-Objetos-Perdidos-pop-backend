package edu.universidad.pop.modules.coincidencia.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Objects;

@Service
public class IaMatchingService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent")
            .build();

    @SuppressWarnings("null")
    public String analizarCoincidencias(String reportePerdida, String objetosEncontrados) {
        if (apiKey == null || apiKey.equals("TU_API_KEY_AQUI") || apiKey.isEmpty()) {
            return "[]"; // Si no hay API Key, devolvemos lista vacía
        }

        String prompt = "Eres un asistente de una plataforma universitaria. Un estudiante perdió un objeto descrito así: \"" 
                + reportePerdida + "\".\n"
                + "Tenemos estos objetos encontrados (en formato JSON): " + objetosEncontrados + ".\n"
                + "Analiza cuáles objetos son probables coincidencias. Devuelve ÚNICAMENTE un arreglo JSON con los IDs de los objetos que consideres coincidencias, ordenados del más probable al menos probable. Ejemplo: [3, 1]";

        try {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode requestBody = mapper.createObjectNode();
            ArrayNode contents = mapper.createArrayNode();
            ObjectNode content = mapper.createObjectNode();
            ArrayNode parts = mapper.createArrayNode();
            ObjectNode part = mapper.createObjectNode();
            
            part.put("text", prompt);
            parts.add(part);
            content.set("parts", parts);
            contents.add(content);
            requestBody.set("contents", contents);

            String response = webClient.post()
                    .uri(uriBuilder -> uriBuilder.queryParam("key", apiKey).build())
                    .header("Content-Type", "application/json")
                    .bodyValue(Objects.requireNonNull(requestBody.toString()))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JsonNode root = mapper.readTree(response);
            JsonNode candidates = root.path("candidates");
            
            if (candidates.isMissingNode() || candidates.isEmpty()) {
                return "[]";
            }

            String textoRespuesta = candidates.get(0).path("content").path("parts").get(0).path("text").asText();
            
            // Limpiar el texto por si la IA devuelve formato markdown
            textoRespuesta = textoRespuesta.replace("```json", "").replace("```", "").trim();
            return textoRespuesta;

        } catch (Exception e) {
            e.printStackTrace();
            return "[]";
        }
    }
}