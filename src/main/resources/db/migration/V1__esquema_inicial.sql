CREATE TYPE rol_enum AS ENUM ('ESTUDIANTE', 'ENCARGADO_OBJETOS_PERDIDOS', 'ADMINISTRADOR');
CREATE TYPE estado_cuenta_enum AS ENUM ('ACTIVA', 'INACTIVA', 'SUSPENDIDA', 'PENDIENTE_VERIFICACION');
CREATE TYPE estado_objeto_enum AS ENUM ('DISPONIBLE_PARA_RECLAMACION', 'EN_PROCESO_DE_RECUPERACION', 'ENTREGADO', 'CERRADO');
CREATE TYPE estado_reporte_enum AS ENUM ('ACTIVO', 'POSIBLE_COINCIDENCIA', 'RESUELTO', 'CERRADO');
CREATE TYPE estado_solicitud_enum AS ENUM ('PENDIENTE_DE_REVISION', 'EN_REVISION', 'INFORMACION_ADICIONAL_SOLICITADA', 'APROBADA', 'RECHAZADA', 'CANCELADA');
CREATE TYPE tipo_notificacion_enum AS ENUM ('COINCIDENCIA_DETECTADA', 'SOLICITUD_CREADA', 'SOLICITUD_APROBADA', 'SOLICITUD_RECHAZADA', 'INFORMACION_ADICIONAL_SOLICITADA', 'ENTREGA_REGISTRADA', 'OBJETO_ACTUALIZADO');
CREATE TYPE categoria_objeto_enum AS ENUM ('ELECTRONICA', 'DOCUMENTOS', 'ROPA', 'ACCESORIOS', 'LLAVES', 'CARTERAS_BOLSOS', 'LIBROS_APUNTES', 'DEPORTES', 'OTROS');

CREATE TABLE usuario (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    correo_institucional VARCHAR(100) NOT NULL UNIQUE,
    identificador_institucional VARCHAR(50) UNIQUE,
    contrasena_hash VARCHAR(255) NOT NULL,
    rol rol_enum NOT NULL,
    estado_cuenta estado_cuenta_enum NOT NULL DEFAULT 'ACTIVA',
    intentos_fallidos INT DEFAULT 0,
    bloqueado_hasta TIMESTAMP,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE codigo_invitacion (
    id BIGSERIAL PRIMARY KEY,
    codigo VARCHAR(50) NOT NULL UNIQUE,
    correo_destino VARCHAR(100) NOT NULL,
    rol_asignado rol_enum NOT NULL,
    creado_por BIGINT REFERENCES usuario(id),
    usado BOOLEAN DEFAULT FALSE,
    fecha_expiracion TIMESTAMP,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE objeto (
    id BIGSERIAL PRIMARY KEY,
    titulo VARCHAR(150) NOT NULL,
    descripcion TEXT,
    categoria categoria_objeto_enum NOT NULL,
    fecha_hallazgo TIMESTAMP NOT NULL,
    ubicacion VARCHAR(255) NOT NULL,
    estado estado_objeto_enum NOT NULL DEFAULT 'DISPONIBLE_PARA_RECLAMACION',
    registrado_por BIGINT REFERENCES usuario(id),
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE fotografia (
    id BIGSERIAL PRIMARY KEY,
    objeto_id BIGINT REFERENCES objeto(id),
    ruta_archivo VARCHAR(500) NOT NULL,
    nombre_original VARCHAR(255),
    mime_type VARCHAR(50),
    tamano_bytes BIGINT,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE reporte_perdida (
    id BIGSERIAL PRIMARY KEY,
    usuario_reporta BIGINT REFERENCES usuario(id),
    descripcion TEXT NOT NULL,
    categoria categoria_objeto_enum NOT NULL,
    fecha_perdida TIMESTAMP NOT NULL,
    ultima_ubicacion VARCHAR(255),
    estado estado_reporte_enum NOT NULL DEFAULT 'ACTIVO',
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE solicitud_recuperacion (
    id BIGSERIAL PRIMARY KEY,
    objeto_id BIGINT REFERENCES objeto(id),
    usuario_solicitante BIGINT REFERENCES usuario(id),
    descripcion_privada TEXT,
    estado estado_solicitud_enum NOT NULL DEFAULT 'PENDIENTE_DE_REVISION',
    motivo_rechazo TEXT,
    encargado_responsable BIGINT REFERENCES usuario(id),
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_resolucion TIMESTAMP
);

CREATE TABLE evidencia_solicitud (
    id BIGSERIAL PRIMARY KEY,
    solicitud_id BIGINT REFERENCES solicitud_recuperacion(id),
    ruta_archivo VARCHAR(500) NOT NULL,
    mime_type VARCHAR(50),
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE entrega (
    id BIGSERIAL PRIMARY KEY,
    objeto_id BIGINT UNIQUE REFERENCES objeto(id),
    solicitud_id BIGINT UNIQUE REFERENCES solicitud_recuperacion(id),
    encargado_responsable BIGINT REFERENCES usuario(id),
    fecha_entrega TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    confirmacion_recepcion BOOLEAN DEFAULT FALSE,
    firma_recepcion TEXT,
    observaciones TEXT
);

CREATE TABLE notificacion (
    id BIGSERIAL PRIMARY KEY,
    usuario_destinatario BIGINT REFERENCES usuario(id),
    tipo tipo_notificacion_enum NOT NULL,
    mensaje TEXT NOT NULL,
    leida BOOLEAN DEFAULT FALSE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE registro_auditoria (
    id BIGSERIAL PRIMARY KEY,
    usuario_responsable BIGINT REFERENCES usuario(id),
    accion VARCHAR(100) NOT NULL,
    entidad_afectada VARCHAR(100),
    registro_afectado_id BIGINT,
    resultado VARCHAR(50),
    detalle JSONB,
    fecha_hora TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
