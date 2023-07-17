package br.com.dsousasantos91.assembleia.mapper;

import br.com.dsousasantos91.assembleia.domain.Sessao;
import br.com.dsousasantos91.assembleia.service.dto.request.AssembleiaRequest;
import br.com.dsousasantos91.assembleia.service.dto.response.SessaoResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface SessaoMapper {
    SessaoResponse toResponse(Sessao sessao);
    Sessao toEntity(AssembleiaRequest request);
}
