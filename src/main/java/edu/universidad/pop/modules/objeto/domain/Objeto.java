package edu.universidad.pop.modules.objeto.domain;

import edu.universidad.pop.common.enums.CategoriaObjeto;
import edu.universidad.pop.common.enums.EstadoObjeto;
import edu.universidad.pop.modules.usuario.domain.Usuario;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode; // <-- IMPORTANTE
import org.hibernate.type.SqlTypes;             // <-- IMPORTANTE

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "objeto")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Objeto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "titulo", nullable = false, length = 200)
    private String titulo;

    @Column(name = "descripcion", nullable = false, columnDefinition = "TEXT")
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM) // <-- ESTO ES CLAVE
    @Column(name = "categoria", nullable = false, columnDefinition = "categoria_objeto_enum")
    private CategoriaObjeto categoria;

    @Column(name = "fecha_hallazgo", nullable = false)
    private LocalDateTime fechaHallazgo;

    @Column(name = "ubicacion", nullable = false, length = 255)
    private String ubicacion;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM) // <-- ESTO ES CLAVE
    @Column(name = "estado", nullable = false, columnDefinition = "estado_objeto_enum")
    @Builder.Default
    private EstadoObjeto estado = EstadoObjeto.DISPONIBLE_PARA_RECLAMACION;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "registrado_por", nullable = false)
    private Usuario registradoPor;

    @OneToMany(mappedBy = "objeto", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Fotografia> fotografias = new ArrayList<>();

    @Column(name = "fecha_creacion", nullable = false)
    @Builder.Default
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
}