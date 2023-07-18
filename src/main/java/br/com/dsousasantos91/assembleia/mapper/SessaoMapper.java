package br.com.dsousasantos91.assembleia.mapper;

import br.com.dsousasantos91.assembleia.domain.Sessao;
import br.com.dsousasantos91.assembleia.service.dto.request.SessaoRequest;
import br.com.dsousasantos91.assembleia.service.dto.response.SessaoResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface SessaoMapper {
    SessaoResponse toResponse(Sessao sessao);
    List<SessaoResponse> toResponses(List<Sessao> sessoes);
    Sessao toEntity(SessaoRequest request);
}
