-- Corrige los hashes BCrypt de los usuarios semilla
-- La contraseña para todos es: Admin123!
UPDATE usuario SET contrasena_hash = '$2a$12$ZPaO8xAxj0f78CTeFAf6I.zSXya7Yw08wuuToxcLPQ2GOCnmohQc2'
WHERE correo_institucional IN (
    'admin@universidad.edu',
    'encargado@universidad.edu',
    'estudiante1@universidad.edu',
    'estudiante2@universidad.edu'
);
