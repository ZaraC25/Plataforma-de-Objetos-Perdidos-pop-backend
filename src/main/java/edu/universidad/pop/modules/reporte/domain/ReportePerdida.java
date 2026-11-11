package edu.universidad.pop.modules.reporte.domain;

import edu.universidad.pop.common.enums.CategoriaObjeto;
import edu.universidad.pop.common.enums.EstadoReporte;
import edu.universidad.pop.modules.usuario.domain.Usuario;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "reporte_perdida")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportePerdida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_reporta", nullable = false)
    private Usuario usuarioReporta;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "categoria_objeto")
    private CategoriaObjeto categoria;

    @Column(name = "fecha_perdida", nullable = false)
    private LocalDateTime fechaPerdida;

    @Column(name = "ultima_ubicacion", nullable = false, length = 255)
    private String ultimaUbicacion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "estado_reporte")
    @Builder.Default
    private EstadoReporte estado = EstadoReporte.ACTIVO;

    @Column(name = "fecha_creacion", nullable = false)
    @Builder.Default
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
}