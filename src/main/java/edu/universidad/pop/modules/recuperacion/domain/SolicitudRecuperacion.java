package edu.universidad.pop.modules.recuperacion.domain;

import edu.universidad.pop.common.enums.EstadoSolicitud;
import edu.universidad.pop.modules.objeto.domain.Objeto;
import edu.universidad.pop.modules.usuario.domain.Usuario;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "solicitud_recuperacion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SolicitudRecuperacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "objeto_id", nullable = false)
    private Objeto objeto;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_solicitante", nullable = false)
    private Usuario usuarioSolicitante;

    @Column(name = "descripcion_privada", nullable = false, columnDefinition = "TEXT")
    private String descripcionPrivada;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, columnDefinition = "estado_solicitud")
    @Builder.Default
    private EstadoSolicitud estado = EstadoSolicitud.PENDIENTE_DE_REVISION;

    @Column(name = "motivo_rechazo", columnDefinition = "TEXT")
    private String motivoRechazo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "encargado_responsable")
    private Usuario encargadoResponsable;

    @Column(name = "fecha_creacion", nullable = false)
    @Builder.Default
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @Column(name = "fecha_resolucion")
    private LocalDateTime fechaResolucion;

    @OneToMany(mappedBy = "solicitud", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<EvidenciaSolicitud> evidencias = new ArrayList<>();
}