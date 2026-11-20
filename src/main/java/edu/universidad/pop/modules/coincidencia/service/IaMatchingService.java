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
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class IaMatchingService {

    @Value("${gemini.api.key:}")
    private String apiKey;

    /**
     * Modelo preferido (configurable con GEMINI_MODEL). Google retira modelos con el tiempo;
     * si este responde 404 se elige automáticamente otro disponible para la API key.
     */
    @Value("${gemini.api.model:gemini-flash-latest}")
    private String model;

    /** Modelo descubierto automáticamente tras un 404 (se reutiliza en las siguientes peticiones). */
    private volatile String modeloActivo;

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

        ObjectNode requestBody = mapper.createObjectNode();
        ObjectNode content = requestBody.putArray("contents").addObject();
        content.put("role", "user");
        content.putArray("parts").addObject().put("text", prompt);
        requestBody.putObject("generationConfig")
                .put("responseMimeType", "application/json")
                .put("temperature", 0.1);

        String body = requestBody.toString();
        Set<String> probados = new LinkedHashSet<>();
        List<String> pendientes = new ArrayList<>();
        pendientes.add(modeloActivo != null ? modeloActivo : model);
        boolean listaConsultada = false;
        WebClientResponseException ultimoError = null;

        // Se prueban hasta MAX_MODELOS modelos; en cada uno se reintenta si Google está saturado (429/5xx)
        while (!pendientes.isEmpty() && probados.size() < MAX_MODELOS) {
            String modelo = pendientes.remove(0);
            if (!probados.add(modelo)) continue;
            try {
                String response = generarConReintentos(modelo, body);
                if (!modelo.equals(modeloActivo)) {
                    modeloActivo = modelo; // se recuerda para las siguientes peticiones
                    log.info("Usando el modelo de Gemini '{}'", modelo);
                }
                return parsearRespuesta(response);
            } catch (WebClientResponseException e) {
                ultimoError = e;
                int status = e.getStatusCode().value();
                log.warn("Gemini respondió {} con el modelo '{}': {}", status, modelo, e.getResponseBodyAsString());
                if (status != 404 && !esTransitorio(status)) {
                    throw new IaNoDisponibleException(mensajeParaUsuario(e, modelo)); // key inválida, etc.
                }
                if (!listaConsultada) { // modelo retirado o saturado: probamos otros modelos de la key
                    listaConsultada = true;
                    for (String alterno : listarModelos()) {
                        if (!probados.contains(alterno) && !pendientes.contains(alterno)) pendientes.add(alterno);
                    }
                }
            } catch (IaNoDisponibleException e) {
                throw e;
            } catch (Exception e) {
                log.error("Error llamando a Gemini con el modelo '{}'", modelo, e);
            }
        }

        log.error("Ningún modelo de Gemini respondió. Probados: {}", probados);
        throw new IaNoDisponibleException(ultimoError != null
                ? mensajeParaUsuario(ultimoError, String.join(", ", probados))
                : "No se pudo contactar el servicio de IA. Intenta de nuevo.");
    }

    private static final int MAX_MODELOS = 3;
    private static final long[] ESPERAS_MS = {2_000, 5_000};

    private static boolean esTransitorio(int status) {
        return status == 429 || status == 500 || status == 502 || status == 503 || status == 504;
    }

    /** Llama a Gemini y, si está saturado (429/5xx), espera y reintenta antes de rendirse. */
    private String generarConReintentos(String modelo, String body) {
        for (int intento = 0; ; intento++) {
            try {
                return generar(modelo, body);
            } catch (WebClientResponseException e) {
                if (!esTransitorio(e.getStatusCode().value()) || intento >= ESPERAS_MS.length) throw e;
                log.info("Gemini saturado ({}) con '{}', reintento {} en {} ms",
                        e.getStatusCode().value(), modelo, intento + 1, ESPERAS_MS[intento]);
                try {
                    Thread.sleep(ESPERAS_MS[intento]);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw e;
                }
            }
        }
    }

    private String generar(String modelo, String body) {
        return webClient.post()
                .uri("/models/{model}:generateContent", modelo)
                .header("x-goog-api-key", apiKey)
                .header("Content-Type", "application/json")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class)
                .block(Duration.ofSeconds(30));
    }

    /**
     * Lista los modelos "flash" de texto que la API key puede usar, ordenados del mejor al peor:
     * prefiere estables sobre preview/experimental, no-lite y la versión más alta.
     */
    private List<String> listarModelos() {
        List<String> candidatos = new ArrayList<>();
        try {
            String lista = webClient.get()
                    .uri(u -> u.path("/models").queryParam("pageSize", 1000).build())
                    .header("x-goog-api-key", apiKey)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block(Duration.ofSeconds(20));
            for (JsonNode m : mapper.readTree(lista).path("models")) {
                boolean generaTexto = false;
                for (JsonNode metodo : m.path("supportedGenerationMethods")) {
                    if ("generateContent".equals(metodo.asText())) generaTexto = true;
                }
                String nombre = m.path("name").asText("").replaceFirst("^models/", "");
                if (generaTexto && nombre.startsWith("gemini") && nombre.contains("flash")
                        && !nombre.matches(".*(image|tts|audio|live|embedding|vision|thinking).*")) {
                    candidatos.add(nombre);
                }
            }
        } catch (Exception e) {
            log.error("No se pudo obtener la lista de modelos de Gemini", e);
            return List.of();
        }
        candidatos.sort(Comparator.comparingInt(IaMatchingService::puntaje).reversed());
        log.info("Modelos flash disponibles para esta API key: {}", candidatos);
        return candidatos;
    }

    private static int puntaje(String nombre) {
        int p = 0;
        if (nombre.equals("gemini-flash-latest")) p += 100_000;
        if (!nombre.matches(".*(preview|exp).*")) p += 10_000;
        if (!nombre.contains("lite")) p += 5_000;
        Matcher v = Pattern.compile("gemini-(\\d+)(?:\\.(\\d+))?").matcher(nombre);
        if (v.find()) {
            p += Integer.parseInt(v.group(1)) * 100 + (v.group(2) != null ? Integer.parseInt(v.group(2)) * 10 : 0);
        }
        if (!nombre.matches(".*-\\d{3,}$")) p += 1; // prefiere el alias sin sufijo de fecha
        return p;
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

    private String mensajeParaUsuario(WebClientResponseException e, String model) {
        String cuerpo = e.getResponseBodyAsString();
        return switch (e.getStatusCode().value()) {
            case 400 -> cuerpo.contains("API_KEY_INVALID") || cuerpo.contains("API key not valid")
                    ? "La API key de Gemini configurada en el servidor no es válida."
                    : "Gemini rechazó la petición (400). Revisa los logs del servidor.";
            case 401, 403 -> "La API key de Gemini no tiene permiso (¿API deshabilitada o key restringida?).";
            case 404 -> "Ningún modelo de Gemini disponible (" + model + "). Configura otro en GEMINI_MODEL.";
            case 429 -> "Se alcanzó el límite gratuito de Gemini. Espera un minuto e intenta de nuevo.";
            case 500, 502, 503, 504 -> "Gemini está saturado en este momento. Intenta de nuevo en unos minutos.";
            default -> "El servicio de IA no está disponible en este momento (" + e.getStatusCode().value() + ").";
        };
    }
}
