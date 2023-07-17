package br.com.dsousasantos91.assembleia.mapper;

import br.com.dsousasantos91.assembleia.domain.Pauta;
import br.com.dsousasantos91.assembleia.domain.Votacao;
import br.com.dsousasantos91.assembleia.service.dto.request.PautaRequest;
import br.com.dsousasantos91.assembleia.service.dto.request.VotacaoRequest;
import br.com.dsousasantos91.assembleia.service.dto.response.PautaResponse;
import br.com.dsousasantos91.assembleia.service.dto.response.VotacaoResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface PautaMapper {
    PautaResponse toResponse(Pauta pauta);
    Pauta toEntity(PautaRequest request);
}
