-- Datos de demostración para "Coincidencias IA":
-- cada reporte de pérdida ACTIVO tiene un objeto DISPONIBLE muy parecido,
-- para que Gemini encuentre coincidencias claras.
-- Se usa JOIN con usuario: si un correo no existe, esa fila simplemente no se inserta.

INSERT INTO objeto (titulo, descripcion, categoria, fecha_hallazgo, ubicacion, estado, registrado_por)
SELECT v.titulo, v.descripcion, v.categoria::categoria_objeto_enum, NOW() - (v.dias || ' days')::interval,
       v.ubicacion, 'DISPONIBLE_PARA_RECLAMACION', u.id
FROM usuario u
JOIN (VALUES
    ('Audífonos Sony WH-1000XM4 negros',
     'Audífonos de diadema Sony WH-1000XM4 color negro, en estuche gris. Tienen un rayón en el lado izquierdo.',
     'ELECTRONICA', 2, 'Biblioteca central - 2do piso'),
    ('Termo Stanley verde',
     'Termo metálico Stanley color verde oscuro de 1 litro, con calcomanía de la universidad y una abolladura en la base.',
     'OTROS', 1, 'Cafetería central'),
    ('Llavero con 3 llaves y Pikachu',
     'Llavero de Pikachu amarillo con tres llaves: dos plateadas y una dorada pequeña de candado.',
     'LLAVES', 3, 'Parqueadero de bicicletas - Bloque C'),
    ('Billetera café de cuero',
     'Billetera de cuero color café marca Vélez, con carné estudiantil a nombre de Valentina y tarjeta de transporte.',
     'CARTERAS_BOLSOS', 2, 'Auditorio principal'),
    ('Chaqueta impermeable azul talla M',
     'Chaqueta impermeable azul marino marca Columbia, talla M, con capucha y un bolígrafo en el bolsillo interno.',
     'ROPA', 4, 'Coliseo deportivo'),
    ('Calculadora Casio fx-991LA',
     'Calculadora científica Casio fx-991LA CW gris con tapa, tiene las iniciales "A.T." marcadas con marcador en la parte de atrás.',
     'ELECTRONICA', 1, 'Salón 305 - Bloque B')
) AS v(titulo, descripcion, categoria, dias, ubicacion) ON TRUE
WHERE u.correo_institucional = 'encargado@universidad.edu';

INSERT INTO reporte_perdida (usuario_reporta, descripcion, categoria, fecha_perdida, ultima_ubicacion, estado)
SELECT u.id, v.descripcion, v.categoria::categoria_objeto_enum, NOW() - (v.dias || ' days')::interval,
       v.ubicacion, 'ACTIVO'
FROM (VALUES
    ('estudiante1@universidad.edu',
     'Perdí mis audífonos Sony negros de diadema (modelo WH-1000XM4). Estaban en un estuche gris y tienen un rayón a un lado.',
     'ELECTRONICA', 3, 'Biblioteca, segundo piso'),
    ('carlos.mendoza@universidad.edu',
     'Se me quedó un termo Stanley verde de un litro con el sticker de la universidad. Está un poco abollado abajo.',
     'OTROS', 1, 'Cafetería'),
    ('andres.torres@universidad.edu',
     'Perdí mis llaves de la casa y del candado de la bici, tienen un llavero de Pikachu.',
     'LLAVES', 3, 'Parqueadero de bicicletas'),
    ('valentina.gomez@universidad.edu',
     'Perdí mi billetera de cuero café, marca Vélez. Adentro tenía mi carné de estudiante y la tarjeta del bus.',
     'CARTERAS_BOLSOS', 2, 'Auditorio principal'),
    ('estudiante2@universidad.edu',
     'Olvidé mi chaqueta Columbia azul oscuro impermeable, talla M, después del partido de fútbol sala.',
     'ROPA', 4, 'Coliseo'),
    ('andres.torres@universidad.edu',
     'Dejé mi calculadora Casio fx-991 gris en un salón del bloque B. Le escribí mis iniciales A.T. atrás.',
     'ELECTRONICA', 1, 'Bloque B, salón 305')
) AS v(correo, descripcion, categoria, dias, ubicacion)
JOIN usuario u ON u.correo_institucional = v.correo;
