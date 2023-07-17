package br.com.dsousasantos91.assembleia.mapper;

import br.com.dsousasantos91.assembleia.domain.Assembleia;
import br.com.dsousasantos91.assembleia.service.dto.request.AssembleiaRequest;
import br.com.dsousasantos91.assembleia.service.dto.request.AssembleiaUpdateRequest;
import br.com.dsousasantos91.assembleia.service.dto.response.AssembleiaResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface AssembleiaMapper {
    AssembleiaResponse toResponse(Assembleia assembleia);
    Assembleia toEntity(AssembleiaRequest request);
    Assembleia toEntity(AssembleiaUpdateRequest request);
}
