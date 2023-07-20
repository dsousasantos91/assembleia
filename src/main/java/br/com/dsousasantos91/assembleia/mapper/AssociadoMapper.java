package br.com.dsousasantos91.assembleia.mapper;

import br.com.dsousasantos91.assembleia.domain.Associado;
import br.com.dsousasantos91.assembleia.service.dto.request.AssociadoRequest;
import br.com.dsousasantos91.assembleia.service.dto.response.AssociadoResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface AssociadoMapper {
    AssociadoResponse toResponse(Associado associado);
    Associado toEntity(AssociadoRequest request);
    List<Associado> toEntities(List<AssociadoRequest> request);
}
