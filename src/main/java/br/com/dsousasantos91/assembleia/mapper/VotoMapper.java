package br.com.dsousasantos91.assembleia.mapper;

import br.com.dsousasantos91.assembleia.domain.Voto;
import br.com.dsousasantos91.assembleia.service.dto.request.VotoRequest;
import br.com.dsousasantos91.assembleia.service.dto.response.VotoResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface VotoMapper {
    VotoResponse toResponse(Voto voto);
    Voto toEntity(VotoRequest request);
}
