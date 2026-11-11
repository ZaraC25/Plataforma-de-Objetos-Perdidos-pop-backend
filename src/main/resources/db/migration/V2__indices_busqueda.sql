CREATE EXTENSION IF NOT EXISTS pg_trgm;

CREATE INDEX idx_objeto_titulo_trgm ON objeto USING GIN (titulo gin_trgm_ops);
CREATE INDEX idx_objeto_descripcion_trgm ON objeto USING GIN (descripcion gin_trgm_ops);

CREATE INDEX idx_objeto_categoria ON objeto(categoria);
CREATE INDEX idx_objeto_estado ON objeto(estado);
CREATE INDEX idx_objeto_fecha_hallazgo ON objeto(fecha_hallazgo);

CREATE INDEX idx_reporte_categoria ON reporte_perdida(categoria);
CREATE INDEX idx_reporte_estado ON reporte_perdida(estado);
