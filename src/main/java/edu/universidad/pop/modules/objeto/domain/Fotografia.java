package edu.universidad.pop.modules.objeto.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * RF-03 — Fotografía opcional asociada a un objeto encontrado.
 * RNF-04 — Validar tipo y tamaño de las imágenes.
 */
@Entity
@Table(name = "fotografia")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Fotografia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Relación inversa: cada fotografía pertenece a un objeto.
     * "objeto" debe coincidir con el nombre del campo en la entidad Objeto.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "objeto_id", nullable = false)
    private Objeto objeto;

    @Column(name = "ruta_archivo", nullable = false, length = 500)
    private String rutaArchivo;

    @Column(name = "nombre_original", length = 255)
    private String nombreOriginal;

    @Column(name = "mime_type", length = 100)
    private String mimeType;

    @Column(name = "tamano_bytes")
    private Long tamanoBytes;

    @Column(name = "fecha_creacion", nullable = false)
    @Builder.Default
    private LocalDateTime fechaCreacion = LocalDateTime.now();
}