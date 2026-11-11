CREATE OR REPLACE FUNCTION audit_objeto_estado() RETURNS TRIGGER AS $$
BEGIN
    IF NEW.estado IS DISTINCT FROM OLD.estado THEN
        INSERT INTO registro_auditoria (accion, entidad_afectada, registro_afectado_id, resultado, detalle)
        VALUES ('CAMBIO_ESTADO', 'Objeto', NEW.id, 'EXITO', json_build_object('old', OLD.estado, 'new', NEW.estado)::jsonb);
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_audit_objeto_estado
AFTER UPDATE OF estado ON objeto
FOR EACH ROW EXECUTE FUNCTION audit_objeto_estado();
