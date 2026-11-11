package edu.universidad.pop.modules.recuperacion.domain;

import edu.universidad.pop.modules.objeto.domain.Objeto;
import edu.universidad.pop.modules.usuario.domain.Usuario;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "entrega")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Entrega {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "objeto_id", nullable = false)
    private Objeto objeto;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "solicitud_id", nullable = false, unique = true)
    private SolicitudRecuperacion solicitud;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "encargado_responsable", nullable = false)
    private Usuario encargadoResponsable;

    @Column(name = "fecha_entrega", nullable = false)
    @Builder.Default // <-- IMPORTANTE: Asegura que no se guarde como nulo
    private LocalDateTime fechaEntrega = LocalDateTime.now();

    @Column(name = "confirmacion_recepcion", nullable = false)
    @Builder.Default // <-- IMPORTANTE
    private boolean confirmacionRecepcion = false;

    @Column(name = "firma_recepcion", length = 255)
    private String firmaRecepcion;

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;
}