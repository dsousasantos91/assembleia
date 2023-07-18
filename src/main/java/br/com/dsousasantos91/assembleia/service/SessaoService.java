package br.com.dsousasantos91.assembleia.service;

import br.com.dsousasantos91.assembleia.domain.Assembleia;
import br.com.dsousasantos91.assembleia.domain.Pauta;
import br.com.dsousasantos91.assembleia.domain.Sessao;
import br.com.dsousasantos91.assembleia.exception.GenericBadRequestException;
import br.com.dsousasantos91.assembleia.exception.GenericNotFoundException;
import br.com.dsousasantos91.assembleia.mapper.AssociadoMapper;
import br.com.dsousasantos91.assembleia.mapper.SessaoMapper;
import br.com.dsousasantos91.assembleia.repository.AssembleiaRepository;
import br.com.dsousasantos91.assembleia.repository.PautaRepository;
import br.com.dsousasantos91.assembleia.repository.SessaoRepository;
import br.com.dsousasantos91.assembleia.scheduler.NotificadorScheduler;
import br.com.dsousasantos91.assembleia.service.dto.request.SessaoEmLoteRequest;
import br.com.dsousasantos91.assembleia.service.dto.request.SessaoRequest;
import br.com.dsousasantos91.assembleia.service.dto.request.TempoSessaoRequest;
import br.com.dsousasantos91.assembleia.service.dto.response.SessaoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
public class SessaoService {

    private final SessaoRepository sessaoRepository;
    private final PautaRepository pautaRepository;
    private final AssembleiaRepository assembleiaRepository;
    private final SessaoMapper sessaoMapper;
    private final AssociadoMapper associadoMapper;
    private final NotificadorScheduler notificadorScheduler;

    public SessaoResponse abrir(SessaoRequest request) {
        Pauta pauta = pautaRepository.findById(request.getPautaId())
                .orElseThrow(() -> new GenericNotFoundException(String.format("Pauta com ID: %d não encontrada.", request.getPautaId())));
        Sessao sessao = Sessao.builder()
                .pauta(pauta)
                .dataHoraInicio(LocalDateTime.now())
                .dataHoraFim(calcularDataHoraFim(request.getTempoSessao()))
                .build();
        Sessao sessaoAberta = sessaoRepository.save(sessao);
        notificadorScheduler.agendarNotificacao(sessaoAberta);
        return sessaoMapper.toResponse(sessaoAberta);
    }

    public List<SessaoResponse> abrirEmLote(SessaoEmLoteRequest request) {
        List<Sessao> sessoes;
        Optional<Assembleia> assembleia = this.assembleiaRepository.findById(request.getAssembleiaId());
        if (assembleia.isPresent()) {
            List<Long> idsPautas = assembleia.get().getPautas().stream().map(Pauta::getId).toList();
            request.setIdsPautas(idsPautas);
        }
        if (assembleia.isEmpty() && pautasNullOuVazio(request.getIdsPautas()))
            throw new GenericBadRequestException("assembleiaId não enviado OU idsPautas está vazio.");
        sessoes = montarSessoes(request);
        List<Sessao> sessoesEmLote = sessaoRepository.saveAll(sessoes);
        sessoesEmLote.forEach(notificadorScheduler::agendarNotificacao);
        return sessaoMapper.toResponses(sessoesEmLote);
    }

    private List<Sessao> montarSessoes(SessaoEmLoteRequest request) {
        return request.getIdsPautas().stream().map(pautaId -> {
            Optional<Pauta> pauta = pautaRepository.findById(pautaId);
            Sessao sessao = Sessao.builder()
                    .votacaoLivre(request.getVotacaoLivre())
                    .dataHoraInicio(LocalDateTime.now())
                    .dataHoraFim(calcularDataHoraFim(request.getTempoSessao()))
                    .build();
            pauta.ifPresent(sessao::setPauta);
            if (Boolean.FALSE.equals(sessao.getVotacaoLivre()) && request.getAssociados().isEmpty())
                throw new GenericBadRequestException("Votação fechada. Deve-se enviar os associados participantes da Sessão.");
            Optional.ofNullable(request.getAssociados()).ifPresent(associado -> sessao.setAssociados(associadoMapper.toEntities(associado)));
            return sessao;
        }).toList();
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
                    sessaoEncontrada.setDataHoraFim(calcularDataHoraFim(request.getTempoSessao()));
                    notificadorScheduler.agendarNotificacao(sessaoEncontrada);
                    return this.sessaoMapper.toResponse(sessaoEncontrada);
                })
                .orElseGet(() -> this.abrir(request));
    }

    public SessaoResponse encerrar(Long sessaoId) {
        Sessao sessao = sessaoRepository.findById(sessaoId)
                .orElseThrow(() -> new GenericNotFoundException(String.format("Sessao com ID: %d não encontrada.", sessaoId)));
        sessao.setDataHoraFim(LocalDateTime.now());
        Sessao sessaoEncerrada = sessaoRepository.save(sessao);
        notificadorScheduler.agendarNotificacao(sessaoEncerrada);
        return sessaoMapper.toResponse(sessaoEncerrada);
    }

    public void apagar(Long id) {
        this.sessaoRepository.deleteById(id);
    }

    private static LocalDateTime calcularDataHoraFim(TempoSessaoRequest tempoSessao) {
        return LocalDateTime.now()
                .plusDays(tempoSessao.getDias())
                .plusHours(tempoSessao.getHoras())
                .plusMinutes(tempoSessao.getMinutos());
    }

    private Boolean pautasNullOuVazio(List<Long> idsPautas) {
        return isNull(idsPautas) || idsPautas.isEmpty();
    }
}
