-- ============================================================
-- V6: Datos demo realistas para la Plataforma de Objetos Perdidos
-- Contraseña de todos los usuarios: Admin123!
-- ============================================================

-- ============================================================
-- USUARIOS adicionales (10 estudiantes, 2 encargados más)
-- ============================================================
INSERT INTO usuario (nombre, correo_institucional, identificador_institucional, contrasena_hash, rol, estado_cuenta) VALUES
('Carlos Mendoza Rivera',    'carlos.mendoza@universidad.edu',   'EST-003', '$2a$12$ZPaO8xAxj0f78CTeFAf6I.zSXya7Yw08wuuToxcLPQ2GOCnmohQc2', 'ESTUDIANTE', 'ACTIVA'),
('María Fernanda López',     'maria.lopez@universidad.edu',      'EST-004', '$2a$12$ZPaO8xAxj0f78CTeFAf6I.zSXya7Yw08wuuToxcLPQ2GOCnmohQc2', 'ESTUDIANTE', 'ACTIVA'),
('Andrés Felipe Torres',     'andres.torres@universidad.edu',    'EST-005', '$2a$12$ZPaO8xAxj0f78CTeFAf6I.zSXya7Yw08wuuToxcLPQ2GOCnmohQc2', 'ESTUDIANTE', 'ACTIVA'),
('Valentina Gómez Ruiz',     'valentina.gomez@universidad.edu',  'EST-006', '$2a$12$ZPaO8xAxj0f78CTeFAf6I.zSXya7Yw08wuuToxcLPQ2GOCnmohQc2', 'ESTUDIANTE', 'ACTIVA'),
('Santiago Herrera Díaz',    'santiago.herrera@universidad.edu', 'EST-007', '$2a$12$ZPaO8xAxj0f78CTeFAf6I.zSXya7Yw08wuuToxcLPQ2GOCnmohQc2', 'ESTUDIANTE', 'ACTIVA'),
('Laura Camila Ramos',       'laura.ramos@universidad.edu',      'EST-008', '$2a$12$ZPaO8xAxj0f78CTeFAf6I.zSXya7Yw08wuuToxcLPQ2GOCnmohQc2', 'ESTUDIANTE', 'ACTIVA'),
('Diego Alejandro Mora',     'diego.mora@universidad.edu',       'EST-009', '$2a$12$ZPaO8xAxj0f78CTeFAf6I.zSXya7Yw08wuuToxcLPQ2GOCnmohQc2', 'ESTUDIANTE', 'ACTIVA'),
('Isabella Vargas Cruz',     'isabella.vargas@universidad.edu',  'EST-010', '$2a$12$ZPaO8xAxj0f78CTeFAf6I.zSXya7Yw08wuuToxcLPQ2GOCnmohQc2', 'ESTUDIANTE', 'ACTIVA'),
('Sebastián Ortiz Pinto',    'sebastian.ortiz@universidad.edu',  'EST-011', '$2a$12$ZPaO8xAxj0f78CTeFAf6I.zSXya7Yw08wuuToxcLPQ2GOCnmohQc2', 'ESTUDIANTE', 'ACTIVA'),
('Daniela Castillo Mejía',   'daniela.castillo@universidad.edu', 'EST-012', '$2a$12$ZPaO8xAxj0f78CTeFAf6I.zSXya7Yw08wuuToxcLPQ2GOCnmohQc2', 'ESTUDIANTE', 'ACTIVA'),
('Encargado Dos',            'encargado2@universidad.edu',       'ENC-002', '$2a$12$ZPaO8xAxj0f78CTeFAf6I.zSXya7Yw08wuuToxcLPQ2GOCnmohQc2', 'ENCARGADO_OBJETOS_PERDIDOS', 'ACTIVA'),
('Admin Secundario',         'admin2@universidad.edu',           'ADM-002', '$2a$12$ZPaO8xAxj0f78CTeFAf6I.zSXya7Yw08wuuToxcLPQ2GOCnmohQc2', 'ADMINISTRADOR', 'ACTIVA');

-- ============================================================
-- OBJETOS ENCONTRADOS (20 objetos variados)
-- ============================================================
INSERT INTO objeto (titulo, descripcion, categoria, fecha_hallazgo, ubicacion, estado, registrado_por) VALUES
('Laptop HP plateada',
 'Laptop HP color plateado, 15 pulgadas. Tiene sticker de Python en la tapa. Encontrada en sala de cómputo.',
 'ELECTRONICA', NOW() - INTERVAL '30 days', 'Sala de Cómputo 201 - Bloque A', 'ENTREGADO', 2),

('Tarjeta de identidad',
 'Cédula de ciudadanía a nombre de un estudiante. Encontrada en el pasillo del segundo piso.',
 'DOCUMENTOS', NOW() - INTERVAL '25 days', 'Pasillo 2do piso - Bloque B', 'ENTREGADO', 2),

('Audífonos Sony negros',
 'Audífonos inalámbricos Sony WH-1000XM4 color negro. Funda gris incluida.',
 'ELECTRONICA', NOW() - INTERVAL '20 days', 'Biblioteca Central - Sala silenciosa', 'DISPONIBLE_PARA_RECLAMACION', 2),

('Mochila azul Totto',
 'Mochila azul marino marca Totto con libros de cálculo y termodinámica adentro.',
 'CARTERAS_BOLSOS', NOW() - INTERVAL '18 days', 'Cafetería Central', 'EN_PROCESO_DE_RECUPERACION', 2),

('Llaves con llavero de oso',
 'Manojo de 3 llaves con llavero de oso panda de peluche pequeño.',
 'LLAVES', NOW() - INTERVAL '15 days', 'Baños 1er piso - Bloque C', 'DISPONIBLE_PARA_RECLAMACION', 11),

('Calculadora Casio fx-991',
 'Calculadora científica Casio fx-991 con estuche negro. Tiene el nombre "Valentina" escrito con marcador.',
 'ELECTRONICA', NOW() - INTERVAL '14 days', 'Salón 305 - Bloque D', 'DISPONIBLE_PARA_RECLAMACION', 11),

('Chaqueta negra universitaria',
 'Chaqueta negra talla M con logo de la universidad en el pecho izquierdo.',
 'ROPA', NOW() - INTERVAL '12 days', 'Auditorio Principal', 'DISPONIBLE_PARA_RECLAMACION', 2),

('Carné universitario',
 'Carné universitario encontrado en la cancha deportiva. Foto de estudiante varón.',
 'DOCUMENTOS', NOW() - INTERVAL '10 days', 'Cancha Deportiva', 'ENTREGADO', 11),

('Tablet Samsung Galaxy',
 'Tablet Samsung Galaxy Tab A8 con funda azul. Tiene contraseña pero pantalla muestra stickers.',
 'ELECTRONICA', NOW() - INTERVAL '9 days', 'Sala de Estudio 3er piso', 'EN_PROCESO_DE_RECUPERACION', 2),

('Billetera café con efectivo',
 'Billetera de cuero café con $45.000 en efectivo, tarjetas de débito y carné estudiantil.',
 'CARTERAS_BOLSOS', NOW() - INTERVAL '8 days', 'Entrada principal - torniquetes', 'ENTREGADO', 2),

('Libro Cálculo Stewart 7ma ed.',
 'Libro Cálculo de una variable, Stewart 7ma edición. Tiene anotaciones y el nombre "Carlos" en la portada.',
 'LIBROS_APUNTES', NOW() - INTERVAL '7 days', 'Sala de Lectura - Biblioteca', 'DISPONIBLE_PARA_RECLAMACION', 11),

('Mouse inalámbrico Logitech',
 'Mouse inalámbrico Logitech M185 color gris. Sin receptor USB.',
 'ELECTRONICA', NOW() - INTERVAL '6 days', 'Sala de Cómputo 103 - Bloque A', 'DISPONIBLE_PARA_RECLAMACION', 2),

('Gafas de sol Ray-Ban',
 'Gafas de sol Ray-Ban Wayfarers color negro con funda original.',
 'ACCESORIOS', NOW() - INTERVAL '5 days', 'Cafetería Central - Mesa exterior', 'DISPONIBLE_PARA_RECLAMACION', 11),

('Cuaderno cuadriculado con apuntes',
 'Cuaderno cuadriculado 100 hojas con apuntes de Estructuras de Datos. Nombre ilegible en portada.',
 'LIBROS_APUNTES', NOW() - INTERVAL '5 days', 'Salón 210 - Bloque B', 'DISPONIBLE_PARA_RECLAMACION', 2),

('Termo Stanley plateado',
 'Termo Stanley 1 litro color plateado. Tiene sticker de mariposa en el lado.',
 'ACCESORIOS', NOW() - INTERVAL '4 days', 'Gym universitario - Vestieres', 'DISPONIBLE_PARA_RECLAMACION', 11),

('USB Kingston 32GB',
 'Memoria USB Kingston DataTraveler 32GB color negro. Tiene información académica.',
 'ELECTRONICA', NOW() - INTERVAL '3 days', 'Sala de Cómputo 201 - Bloque A', 'DISPONIBLE_PARA_RECLAMACION', 2),

('Sudadera gris talla L',
 'Sudadera gris talla L sin logo, bolsillo canguro. Encontrada doblada en silla.',
 'ROPA', NOW() - INTERVAL '3 days', 'Salón 401 - Bloque E', 'DISPONIBLE_PARA_RECLAMACION', 11),

('Pasaporte colombiano',
 'Pasaporte colombiano vigente. Se mantiene en custodia segura.',
 'DOCUMENTOS', NOW() - INTERVAL '2 days', 'Recepción Bloque Administrativo', 'EN_PROCESO_DE_RECUPERACION', 2),

('Cargador MacBook USB-C',
 'Cargador Apple USB-C 61W con cable de 2 metros. Sin caja.',
 'ELECTRONICA', NOW() - INTERVAL '1 day', 'Sala de Estudio 2do piso', 'DISPONIBLE_PARA_RECLAMACION', 11),

('Reloj Casio digital negro',
 'Reloj Casio digital negro modelo W-800H. Correa original.',
 'ACCESORIOS', NOW() - INTERVAL '1 day', 'Entrada Bloque C - torniquetes', 'DISPONIBLE_PARA_RECLAMACION', 2);

-- ============================================================
-- REPORTES DE PÉRDIDA (15 reportes)
-- ============================================================
INSERT INTO reporte_perdida (usuario_reporta, descripcion, categoria, fecha_perdida, ultima_ubicacion, estado) VALUES
(3,  'Perdí mi laptop HP plateada en la sala de cómputo. Tiene sticker de Python. La dejé mientras fui al baño.',
     'ELECTRONICA', NOW() - INTERVAL '30 days', 'Sala de Cómputo 201', 'RESUELTO'),

(4,  'Se me perdió mi cédula en el segundo piso. La saqué para hacer un trámite y no la volví a ver.',
     'DOCUMENTOS', NOW() - INTERVAL '25 days', 'Bloque B - 2do piso', 'RESUELTO'),

(5,  'Perdí mis audífonos Sony negros en la biblioteca. Los dejé en la sala silenciosa.',
     'ELECTRONICA', NOW() - INTERVAL '20 days', 'Biblioteca Central', 'POSIBLE_COINCIDENCIA'),

(6,  'Mi mochila azul Totto desapareció en la cafetería. Tenía mis libros de cálculo adentro.',
     'CARTERAS_BOLSOS', NOW() - INTERVAL '18 days', 'Cafetería Central', 'POSIBLE_COINCIDENCIA'),

(7,  'Perdí mi manojo de llaves con llavero de oso panda. Son 3 llaves, una es del apartamento.',
     'LLAVES', NOW() - INTERVAL '15 days', 'Bloque C - 1er piso', 'ACTIVO'),

(8,  'Se me perdió la calculadora Casio fx-991 con mi nombre escrito. Era del salón 305.',
     'ELECTRONICA', NOW() - INTERVAL '14 days', 'Bloque D - Salón 305', 'ACTIVO'),

(9,  'Perdí mi chaqueta negra universitaria en el auditorio después de la conferencia.',
     'ROPA', NOW() - INTERVAL '12 days', 'Auditorio Principal', 'ACTIVO'),

(10, 'Mi tablet Samsung con funda azul se perdió en la sala de estudio. Tiene fotos importantes.',
     'ELECTRONICA', NOW() - INTERVAL '9 days', 'Sala de Estudio 3er piso', 'POSIBLE_COINCIDENCIA'),

(3,  'Perdí mi billetera café con efectivo y tarjetas cerca de los torniquetes.',
     'CARTERAS_BOLSOS', NOW() - INTERVAL '8 days', 'Entrada principal', 'RESUELTO'),

(4,  'Se me cayó el libro de Cálculo Stewart en la biblioteca. Tiene mis apuntes del semestre.',
     'LIBROS_APUNTES', NOW() - INTERVAL '7 days', 'Biblioteca Central', 'ACTIVO'),

(5,  'Perdí mis gafas Ray-Ban en la cafetería después del almuerzo.',
     'ACCESORIOS', NOW() - INTERVAL '5 days', 'Cafetería Central', 'ACTIVO'),

(6,  'Mi pasaporte se cayó del bolsillo. Lo tenía para un trámite de la universidad.',
     'DOCUMENTOS', NOW() - INTERVAL '2 days', 'Bloque Administrativo', 'POSIBLE_COINCIDENCIA'),

(7,  'Perdí mi cargador de MacBook USB-C blanco en la sala de estudio.',
     'ELECTRONICA', NOW() - INTERVAL '1 day', 'Sala de Estudio 2do piso', 'ACTIVO'),

(8,  'Se me perdió mi termo Stanley plateado en el gimnasio.',
     'ACCESORIOS', NOW() - INTERVAL '4 days', 'Gym universitario', 'ACTIVO'),

(9,  'Perdí una USB Kingston con mi proyecto de grado. Es negra de 32GB.',
     'ELECTRONICA', NOW() - INTERVAL '3 days', 'Sala de Cómputo 201', 'ACTIVO');

-- ============================================================
-- SOLICITUDES DE RECUPERACIÓN
-- ============================================================
-- Solicitud resuelta (objeto 1 - laptop)
INSERT INTO solicitud_recuperacion (objeto_id, usuario_solicitante, descripcion_privada, estado, encargado_responsable, fecha_creacion, fecha_resolucion) VALUES
(1, 3, 'Soy el dueño de la laptop. Tiene sticker de Python en la tapa y el fondo de pantalla es un logo de Django. El número de serie está en la parte inferior.', 'APROBADA', 2, NOW() - INTERVAL '28 days', NOW() - INTERVAL '26 days'),
(2, 4, 'Es mi cédula. El número termina en 847. Puedo mostrar otra identificación para confirmar.', 'APROBADA', 2, NOW() - INTERVAL '23 days', NOW() - INTERVAL '22 days'),
(8, 10, 'Es mi carné. Soy yo en la foto. Puedo ir con otra identificación a reclamarlo.', 'APROBADA', 11, NOW() - INTERVAL '8 days', NOW() - INTERVAL '7 days'),
(10, 3, 'Es mi billetera. El efectivo era $45.000, tengo tarjeta Bancolombia y carné de la universidad.', 'APROBADA', 2, NOW() - INTERVAL '7 days', NOW() - INTERVAL '6 days'),
(4, 6, 'Es mi mochila azul Totto. Adentro están mis libros de cálculo y termodinámica con mi nombre escrito.', 'EN_REVISION', 11, NOW() - INTERVAL '16 days', NULL),
(9, 10, 'La tablet es mía. El fondo de pantalla es una foto de mi perro labrador. Puedo describir las apps instaladas.', 'EN_REVISION', 2, NOW() - INTERVAL '7 days', NULL),
(18, 6, 'El pasaporte es mío. Puedo dar el número completo y fecha de expedición.', 'PENDIENTE_DE_REVISION', 11, NOW() - INTERVAL '1 day', NULL),
(3, 5, 'Son mis audífonos Sony. Los compré hace 6 meses, tengo la factura en mi correo.', 'PENDIENTE_DE_REVISION', 2, NOW() - INTERVAL '18 days', NULL);

-- ============================================================
-- ENTREGAS (objetos ya devueltos)
-- ============================================================
INSERT INTO entrega (objeto_id, solicitud_id, encargado_responsable, fecha_entrega, confirmacion_recepcion, observaciones) VALUES
(1,  1, 2, NOW() - INTERVAL '26 days', TRUE,  'Estudiante presentó comprobante de compra. Entrega verificada sin inconvenientes.'),
(2,  2, 2, NOW() - INTERVAL '22 days', TRUE,  'Se verificó identidad con otro documento. Cédula entregada satisfactoriamente.'),
(8,  3, 11, NOW() - INTERVAL '7 days',  TRUE,  'Estudiante se identificó con pasaporte. Carné entregado.'),
(10, 4, 2, NOW() - INTERVAL '6 days',  TRUE,  'Billetera entregada con todo el contenido intacto. Estudiante firmó recibo.');

-- ============================================================
-- NOTIFICACIONES
-- ============================================================
INSERT INTO notificacion (usuario_destinatario, tipo, mensaje, leida) VALUES
-- Notificaciones de coincidencias
(5,  'COINCIDENCIA_DETECTADA',       'Se encontró un objeto que podría coincidir con tu reporte: "Audífonos Sony negros". Revisa los detalles.', FALSE),
(6,  'COINCIDENCIA_DETECTADA',       'Se encontró un objeto que podría coincidir con tu reporte: "Mochila azul Totto". Revisa los detalles.', FALSE),
(10, 'COINCIDENCIA_DETECTADA',       'Se encontró un objeto que podría coincidir con tu reporte: "Tablet Samsung Galaxy". Revisa los detalles.', TRUE),
(6,  'COINCIDENCIA_DETECTADA',       'Se encontró un objeto que podría coincidir con tu reporte: "Pasaporte colombiano". Revisa los detalles.', FALSE),
-- Notificaciones de solicitudes creadas
(2,  'SOLICITUD_CREADA',             'Nueva solicitud de recuperación recibida para el objeto "Mochila azul Totto". Requiere revisión.', TRUE),
(11, 'SOLICITUD_CREADA',             'Nueva solicitud de recuperación recibida para el objeto "Tablet Samsung Galaxy". Requiere revisión.', TRUE),
(11, 'SOLICITUD_CREADA',             'Nueva solicitud de recuperación recibida para el objeto "Pasaporte colombiano". Requiere revisión.', FALSE),
(2,  'SOLICITUD_CREADA',             'Nueva solicitud de recuperación recibida para el objeto "Audífonos Sony". Requiere revisión.', FALSE),
-- Notificaciones de aprobación
(3,  'SOLICITUD_APROBADA',           'Tu solicitud para recuperar "Laptop HP plateada" fue APROBADA. Acércate a la oficina de objetos perdidos con tu identificación.', TRUE),
(4,  'SOLICITUD_APROBADA',           'Tu solicitud para recuperar "Tarjeta de identidad" fue APROBADA. Puedes recogerla en recepción.', TRUE),
(10, 'SOLICITUD_APROBADA',           'Tu solicitud para recuperar "Carné universitario" fue APROBADA. Acércate con identificación.', TRUE),
(3,  'SOLICITUD_APROBADA',           'Tu solicitud para recuperar "Billetera café" fue APROBADA. Todo el contenido está intacto.', TRUE),
-- Notificaciones de entrega
(3,  'ENTREGA_REGISTRADA',           'Se registró la entrega de tu objeto "Laptop HP plateada". ¡Esperamos que todo esté en orden!', TRUE),
(4,  'ENTREGA_REGISTRADA',           'Se registró la entrega de tu objeto "Tarjeta de identidad". ¡Que no se te vuelva a perder!', TRUE),
(10, 'ENTREGA_REGISTRADA',           'Se registró la entrega de tu objeto "Carné universitario".', TRUE),
(3,  'ENTREGA_REGISTRADA',           'Se registró la entrega de tu objeto "Billetera café con efectivo".', TRUE),
-- Notificaciones sin leer para dar sensación de actividad
(7,  'COINCIDENCIA_DETECTADA',       'Posible coincidencia encontrada para tu reporte de llaves con llavero de oso. Revisa el objeto #5.', FALSE),
(9,  'COINCIDENCIA_DETECTADA',       'Posible coincidencia para tu reporte de chaqueta negra universitaria. Revisa el objeto #7.', FALSE),
(8,  'COINCIDENCIA_DETECTADA',       'Posible coincidencia para tu reporte de calculadora Casio. Revisa el objeto #6.', FALSE);
