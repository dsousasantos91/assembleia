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
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
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
        log.info("Encontrando sessão para votação");
        Sessao sessao = sessaoRepository.findById(request.getSessaoId())
                .orElseThrow(() -> new GenericNotFoundException(String.format("Sessão com id %d não existe.", request.getSessaoId())));
        log.info("sSssão ID [{}] encontrata.", sessao.getId());
        if (LocalDateTime.now().isAfter(sessao.getDataHoraFim()))
            throw new GenericBadRequestException(String.format("Sessão com id %d está encerrada.", request.getSessaoId()));
        log.info("Buscando associado CPF [{}]", request.getAssociado().getCpf());
        Associado associado = associadoRepository.findByCpf(request.getAssociado().getCpf())
                .orElse(associadoMapper.toEntity(request.getAssociado()));
        log.info("Associado CPF [{}] encontrado com sucesso.", request.getAssociado().getCpf());
        if (!sessao.getAssociados().contains(associado))
            throw new GenericBadRequestException(String.format("Associado %s não tem permissão para votar na sessão de id %d.",
                    associado.getCpf(), request.getSessaoId()));
        Votacao votacao = Votacao.builder()
                .sessao(sessao)
                .associado(associado)
                .voto(request.getVoto())
                .build();
        Votacao votacaoRegistrada = votacaoRepository.save(votacao);
        log.info("Votação ID [{}] registrada com sucesso para o associado CPF [{}].",
                votacaoRegistrada.getId(), votacaoRegistrada.getAssociado().getCpf());
        return votacaoMapper.toResponse(votacaoRegistrada);
    }

    public Page<VotacaoResponse> pesquisar(Pageable pageable) {
        log.info("Pesquisando votação.");
        return this.votacaoRepository.findAll(pageable).map(votacaoMapper::toResponse);
    }

    public ContagemVotosResponse contabilizar(Long id) {
        log.info("Realizada contagem dos votos para sessão [{}].", id);
        Sessao sessao = sessaoRepository.findById(id)
                .orElseThrow(() -> new GenericNotFoundException(String.format("Sessão com id %d não existe.", id)));
        List<Votacao> votacoes = votacaoRepository.findBySessaoId(id)
                .orElseThrow(() -> new GenericNotFoundException("Sessão não encontrada."));
        long votosParaNao = votacoes.stream().filter(votacao -> Voto.NAO.equals(votacao.getVoto())).count();
        long votosParaSim = votacoes.stream().filter(votacao -> Voto.SIM.equals(votacao.getVoto())).count();
        log.info("Contagem do votos da sessão [{}] realizada com sucesso.", sessao.getId());
        return ContagemVotosResponse.builder()
                .pauta(pautaMapper.toResponse(sessao.getPauta()))
                .votos(Map.of(Voto.NAO, votosParaNao, Voto.SIM, votosParaSim))
                .build();
    }

    public VotacaoResponse alterarVoto(Long sessaoId, String cpf) {
        log.info("Alterando voto do associado CPF [{}].", cpf);
        Votacao votacao = votacaoRepository.findBySessaoIdAndAssociadoCpf(sessaoId, cpf)
                .orElseThrow(() -> new GenericNotFoundException(String.format("Associado com CPF %s não encontrado.", cpf)));
        Voto novoVoto = Voto.SIM.equals(votacao.getVoto()) ? Voto.SIM : Voto.NAO;
        votacao.setVoto(novoVoto);
        Votacao votacaoAlterada = votacaoRepository.save(votacao);
        log.info("Alteração de voto do associado CPF [{}] realizada com sucesso.", cpf);
        return votacaoMapper.toResponse(votacaoAlterada);
    }
}
