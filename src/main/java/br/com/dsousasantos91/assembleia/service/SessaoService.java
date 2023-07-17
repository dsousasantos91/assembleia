package br.com.dsousasantos91.assembleia.service;

import br.com.dsousasantos91.assembleia.domain.Pauta;
import br.com.dsousasantos91.assembleia.domain.Sessao;
import br.com.dsousasantos91.assembleia.exception.GenericNotFoundException;
import br.com.dsousasantos91.assembleia.mapper.SessaoMapper;
import br.com.dsousasantos91.assembleia.repository.PautaRepository;
import br.com.dsousasantos91.assembleia.repository.SessaoRepository;
import br.com.dsousasantos91.assembleia.service.dto.request.SessaoRequest;
import br.com.dsousasantos91.assembleia.service.dto.response.SessaoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SessaoService {

    private final SessaoRepository sessaoRepository;
    private final PautaRepository pautaRepository;
    private final SessaoMapper sessaoMapper;

    public SessaoResponse abrir(SessaoRequest request) {
        LocalDateTime dataHoraInicio = LocalDateTime.now();
        LocalDateTime dataHoraFim = calcularDataHoraFim(request);
        Pauta pauta = pautaRepository.findById(request.getPautaId())
                .orElseThrow(() -> new GenericNotFoundException(String.format("Pauta com ID: %d não encontrada.", request.getPautaId())));
        Sessao sessao = Sessao.builder()
                .pauta(pauta)
                .dataHoraInicio(dataHoraInicio)
                .dataHoraFim(dataHoraFim)
                .build();
        Sessao sessaoAberta = sessaoRepository.save(sessao);
        return sessaoMapper.toResponse(sessaoAberta);
    }

    public Page<SessaoResponse> pesquisar(Pageable pageable) {
        return this.sessaoRepository.findAll(pageable).map(sessaoMapper::toResponse);
    }

    public SessaoResponse buscarPorId(Long id) {
        Sessao sessao = this.sessaoRepository.findById(id).orElseThrow(GenericNotFoundException::new);
        return this.sessaoMapper.toResponse(sessao);
    }

    public SessaoResponse prorrogar(Long id, SessaoRequest request) {
        return this.sessaoRepository.findById(id)
                .map(sessaoEncontrada -> {
                    sessaoEncontrada.setDataHoraFim(calcularDataHoraFim(request));
                    return this.sessaoMapper.toResponse(sessaoEncontrada);
                })
                .orElseGet(() -> this.abrir(request));
    }

    public SessaoResponse encerrar(Long sessaoId) {
        Sessao sessao = sessaoRepository.findById(sessaoId)
                .orElseThrow(() -> new GenericNotFoundException(String.format("Sessao com ID: %d não encontrada.", sessaoId)));
        sessao.setDataHoraFim(LocalDateTime.now());
        Sessao sessaoEncerrada = sessaoRepository.save(sessao);
        return sessaoMapper.toResponse(sessaoEncerrada);
    }

    private static LocalDateTime calcularDataHoraFim(SessaoRequest request) {
        return LocalDateTime.now()
                .plusDays(request.getTempoSessao().getDias())
                .plusHours(request.getTempoSessao().getHoras())
                .plusMinutes(request.getTempoSessao().getMinutos());
    }
}
