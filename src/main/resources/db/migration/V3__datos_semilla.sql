-- BCrypt hash for 'Admin123!' -> $2a$12$DMy1iG6c5Gf9/wLg0cQw6O0E6bK4tD.P1QO9D/K/3d9O.O1VjQvE6
INSERT INTO usuario (nombre, correo_institucional, identificador_institucional, contrasena_hash, rol, estado_cuenta) VALUES
('Super Admin', 'admin@universidad.edu', 'ADM-001', '$2a$12$DMy1iG6c5Gf9/wLg0cQw6O0E6bK4tD.P1QO9D/K/3d9O.O1VjQvE6', 'ADMINISTRADOR', 'ACTIVA'),
('Encargado POP', 'encargado@universidad.edu', 'ENC-001', '$2a$12$DMy1iG6c5Gf9/wLg0cQw6O0E6bK4tD.P1QO9D/K/3d9O.O1VjQvE6', 'ENCARGADO_OBJETOS_PERDIDOS', 'ACTIVA'),
('Estudiante Uno', 'estudiante1@universidad.edu', 'EST-001', '$2a$12$DMy1iG6c5Gf9/wLg0cQw6O0E6bK4tD.P1QO9D/K/3d9O.O1VjQvE6', 'ESTUDIANTE', 'ACTIVA'),
('Estudiante Dos', 'estudiante2@universidad.edu', 'EST-002', '$2a$12$DMy1iG6c5Gf9/wLg0cQw6O0E6bK4tD.P1QO9D/K/3d9O.O1VjQvE6', 'ESTUDIANTE', 'ACTIVA');
