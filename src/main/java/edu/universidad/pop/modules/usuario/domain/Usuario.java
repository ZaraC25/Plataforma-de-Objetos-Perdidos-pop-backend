package edu.universidad.pop.modules.usuario.domain;

import edu.universidad.pop.common.enums.EstadoCuenta;
import edu.universidad.pop.common.enums.Rol;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "usuario",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_usuario_correo", columnNames = "correo_institucional"),
                @UniqueConstraint(name = "uk_usuario_identificador", columnNames = "identificador_institucional")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", nullable = false, length = 150)
    private String nombre;

    @Column(name = "correo_institucional", nullable = false, length = 150, unique = true)
    private String correoInstitucional;

    @Column(name = "identificador_institucional", length = 50, unique = true)
    private String identificadorInstitucional;

    @Column(name = "contrasena_hash", nullable = false, length = 255)
    private String contrasenaHash;

    // SE AGREGA @JdbcTypeCode PARA POSTGRESQL
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "rol", nullable = false, columnDefinition = "rol_usuario")
    @Builder.Default
    private Rol rol = Rol.ESTUDIANTE;

    // SE AGREGA @JdbcTypeCode PARA POSTGRESQL
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "estado_cuenta", nullable = false, columnDefinition = "estado_cuenta")
    @Builder.Default
    private EstadoCuenta estadoCuenta = EstadoCuenta.PENDIENTE_VERIFICACION;

    @Column(name = "intentos_fallidos", nullable = false)
    @Builder.Default
    private Integer intentosFallidos = 0;

    @Column(name = "bloqueado_hasta")
    private LocalDateTime bloqueadoHasta;

    @Column(name = "fecha_creacion", nullable = false)
    @Builder.Default
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @PreUpdate
    public void preUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }
}