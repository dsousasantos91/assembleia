package br.com.dsousasantos91.assembleia.service;

import br.com.dsousasantos91.assembleia.domain.Associado;
import br.com.dsousasantos91.assembleia.domain.Sessao;
import br.com.dsousasantos91.assembleia.domain.Votacao;
import br.com.dsousasantos91.assembleia.domain.enumeration.Voto;
import br.com.dsousasantos91.assembleia.exception.GenericBadRequestException;
import br.com.dsousasantos91.assembleia.exception.GenericNotFoundException;
import br.com.dsousasantos91.assembleia.mapper.AssociadoMapper;
import br.com.dsousasantos91.assembleia.mapper.PautaMapper;
import br.com.dsousasantos91.assembleia.mapper.VotacaoMapper;
import br.com.dsousasantos91.assembleia.repository.AssociadoRepository;
import br.com.dsousasantos91.assembleia.repository.SessaoRepository;
import br.com.dsousasantos91.assembleia.repository.VotacaoRepository;
import br.com.dsousasantos91.assembleia.service.dto.request.VotacaoRequest;
import br.com.dsousasantos91.assembleia.service.dto.response.ContagemVotosResponse;
import br.com.dsousasantos91.assembleia.service.dto.response.VotacaoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class VotacaoService {

    private final VotacaoRepository votacaoRepository;
    private final SessaoRepository sessaoRepository;
    private final AssociadoRepository associadoRepository;
    private final VotacaoMapper votacaoMapper;
    private final AssociadoMapper associadoMapper;
    private final PautaMapper pautaMapper;

    public VotacaoResponse votar(VotacaoRequest request) {
        Sessao sessao = sessaoRepository.findById(request.getSessaoId())
                .orElseThrow(() -> new GenericNotFoundException(String.format("Sessão com id %d não existe.", request.getSessaoId())));

        if (LocalDateTime.now().isAfter(sessao.getDataHoraFim()))
            throw new GenericBadRequestException(String.format("Sessão com id %d está encerrada.", request.getSessaoId()));

        Associado associado = associadoRepository.findByCpf(request.getAssociado().getCpf())
                .orElse(associadoMapper.toEntity(request.getAssociado()));
        Votacao votacao = Votacao.builder()
                .sessao(sessao)
                .associado(associado)
                .voto(request.getVoto())
                .build();
        Votacao votacaoRegistrada = votacaoRepository.save(votacao);
        return votacaoMapper.toResponse(votacaoRegistrada);
    }

    public Page<VotacaoResponse> pesquisar(Pageable pageable) {
        return this.votacaoRepository.findAll(pageable).map(votacaoMapper::toResponse);
    }

    public ContagemVotosResponse contabilizar(Long sessaoId) {
        Sessao sessao = sessaoRepository.findById(sessaoId)
                .orElseThrow(() -> new GenericNotFoundException(String.format("Sessão com id %d não existe.", sessaoId)));
        List<Votacao> votacoes = votacaoRepository.findBySessaoId(sessaoId)
                .orElseThrow(() -> new GenericNotFoundException("Sessão não encontrada."));
        long votosParaNao = votacoes.stream().filter(votacao -> Voto.NAO.equals(votacao.getVoto())).count();
        long votosParaSim = votacoes.stream().filter(votacao -> Voto.SIM.equals(votacao.getVoto())).count();
        return ContagemVotosResponse.builder()
                .pauta(pautaMapper.toResponse(sessao.getPauta()))
                .votos(Map.of(Voto.NAO, votosParaNao, Voto.SIM, votosParaSim))
                .build();
    }
}
