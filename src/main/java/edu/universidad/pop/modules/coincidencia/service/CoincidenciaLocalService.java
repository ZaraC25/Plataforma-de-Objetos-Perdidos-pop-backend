package edu.universidad.pop.modules.coincidencia.service;

import edu.universidad.pop.modules.objeto.domain.Objeto;
import edu.universidad.pop.modules.reporte.domain.ReportePerdida;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Respaldo sin IA: cuando Gemini no responde, compara reportes y objetos por palabras en común,
 * categoría y ubicación. Menos preciso que la IA, pero siempre está disponible.
 */
@Service
public class CoincidenciaLocalService {

    private static final double UMBRAL = 0.35;
    private static final int MAX_POR_REPORTE = 3;

    private static final Set<String> VACIAS = Set.of(
            "el", "la", "los", "las", "un", "una", "unos", "unas", "de", "del", "al", "a", "en", "y", "o", "u",
            "con", "sin", "por", "para", "que", "se", "me", "mi", "mis", "su", "sus", "es", "son", "lo", "le",
            "les", "muy", "mas", "pero", "como", "tiene", "tienen", "tenia", "estaba", "estaban", "fue", "esta",
            "este", "estos", "estas", "eso", "esa", "ese", "hay", "ya", "no", "si", "perdi", "perdido", "perdida",
            "olvide", "deje", "quedo", "encontrado", "encontrada", "color", "marca", "cerca", "dentro", "adentro",
            "parte", "lado", "piso", "despues", "antes", "durante", "mientras", "fui", "objeto", "universidad");

    /** @return mapa reporteId -> IDs de objetos parecidos (del más al menos parecido) */
    public Map<Long, List<Long>> analizar(List<ReportePerdida> reportes, List<Objeto> objetos) {
        Map<Long, List<Long>> resultado = new LinkedHashMap<>();
        for (ReportePerdida r : reportes) {
            Set<String> palabrasReporte = palabras(r.getDescripcion());
            Set<String> lugarReporte = palabras(r.getUltimaUbicacion());

            List<Map.Entry<Long, Double>> candidatos = new ArrayList<>();
            for (Objeto o : objetos) {
                // Lo principal es describir el mismo objeto: sin palabras en común no hay coincidencia
                double texto = similitud(palabrasReporte, palabras(o.getTitulo() + " " + o.getDescripcion()));
                if (texto == 0) continue;
                double puntaje = texto;
                if (r.getCategoria() == o.getCategoria()) puntaje += 0.1;
                if (similitud(lugarReporte, palabras(o.getUbicacion())) > 0) puntaje += 0.05;
                if (puntaje >= UMBRAL) candidatos.add(Map.entry(o.getId(), puntaje));
            }
            candidatos.sort(Map.Entry.<Long, Double>comparingByValue(Comparator.reverseOrder()));
            // Solo los que están cerca del mejor candidato (evita sugerir objetos poco parecidos)
            double mejor = candidatos.isEmpty() ? 0 : candidatos.get(0).getValue();
            List<Long> ids = candidatos.stream()
                    .filter(c -> c.getValue() >= mejor * 0.75)
                    .limit(MAX_POR_REPORTE).map(Map.Entry::getKey).toList();
            if (!ids.isEmpty()) resultado.put(r.getId(), ids);
        }
        return resultado;
    }

    /** Proporción de palabras del conjunto más pequeño que aparecen en el otro (prefijo de 5 letras). */
    private static double similitud(Set<String> a, Set<String> b) {
        if (a.isEmpty() || b.isEmpty()) return 0;
        Set<String> comunes = new HashSet<>(a);
        comunes.retainAll(b);
        return (double) comunes.size() / Math.min(a.size(), b.size());
    }

    private static Set<String> palabras(String texto) {
        Set<String> out = new HashSet<>();
        if (texto == null) return out;
        String limpio = Normalizer.normalize(texto.toLowerCase(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replaceAll("[^a-z0-9 ]", " ");
        for (String p : limpio.split("\\s+")) {
            if (p.length() < 3 || VACIAS.contains(p)) continue;
            // raíz simple: "llaves"/"llavero" -> "llave", "audifonos"/"audifono" -> "audif"
            out.add(p.length() > 5 ? p.substring(0, 5) : p);
        }
        return out;
    }
}
