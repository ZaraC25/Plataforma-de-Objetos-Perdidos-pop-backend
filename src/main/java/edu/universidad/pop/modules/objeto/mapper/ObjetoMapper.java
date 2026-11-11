package edu.universidad.pop.modules.objeto.mapper;

import edu.universidad.pop.modules.objeto.domain.Objeto;
import edu.universidad.pop.modules.objeto.dto.CrearObjetoRequest;
import edu.universidad.pop.modules.objeto.dto.ObjetoDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ObjetoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "estado", ignore = true)
    @Mapping(target = "registradoPor", ignore = true)
    @Mapping(target = "fotografias", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaActualizacion", ignore = true)
    Objeto toEntity(CrearObjetoRequest request);

    default ObjetoDTO toDTO(Objeto objeto) {
        return ObjetoDTO.from(objeto);
    }
}