package edu.universidad.pop.modules.auditoria.domain;

import edu.universidad.pop.modules.usuario.domain.Usuario;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(name = "registro_auditoria")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistroAuditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_responsable")
    private Usuario usuarioResponsable;

    @Column(name = "accion", nullable = false, length = 100)
    private String accion;

    @Column(name = "entidad_afectada", nullable = false, length = 100)
    private String entidadAfectada;

    @Column(name = "registro_afectado_id")
    private Long registroAfectadoId;

    @Column(name = "resultado", nullable = false, length = 20)
    private String resultado;

    // LE DECIMOS A HIBERNATE QUE ESTO ES UN JSON EN POSTGRESQL
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "detalle", columnDefinition = "jsonb")
    private String detalle;

    @Column(name = "fecha_hora", nullable = false)
    @Builder.Default
    private LocalDateTime fechaHora = LocalDateTime.now();
}