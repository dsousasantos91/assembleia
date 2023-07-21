package br.com.dsousasantos91.assembleia.service;

import br.com.dsousasantos91.assembleia.domain.Associado;
import br.com.dsousasantos91.assembleia.domain.Sessao;
import br.com.dsousasantos91.assembleia.domain.Voto;
import br.com.dsousasantos91.assembleia.domain.enumeration.VotoEnum;
import br.com.dsousasantos91.assembleia.exception.GenericBadRequestException;
import br.com.dsousasantos91.assembleia.exception.GenericNotFoundException;
import br.com.dsousasantos91.assembleia.mapper.AssociadoMapper;
import br.com.dsousasantos91.assembleia.mapper.PautaMapper;
import br.com.dsousasantos91.assembleia.mapper.VotoMapper;
import br.com.dsousasantos91.assembleia.repository.AssociadoRepository;
import br.com.dsousasantos91.assembleia.repository.SessaoRepository;
import br.com.dsousasantos91.assembleia.repository.VotoRepository;
import br.com.dsousasantos91.assembleia.service.dto.request.VotoRequest;
import br.com.dsousasantos91.assembleia.service.dto.response.ContagemVotosResponse;
import br.com.dsousasantos91.assembleia.service.dto.response.VotoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Slf4j
@Service
@RequiredArgsConstructor
public class VotoService {

    private final VotoRepository votoRepository;
    private final SessaoRepository sessaoRepository;
    private final AssociadoRepository associadoRepository;
    private final VotoMapper votoMapper;
    private final AssociadoMapper associadoMapper;
    private final PautaMapper pautaMapper;
    private final ValidarCPFService validarCPFService;

    public VotoResponse votar(VotoRequest request) {
        validarCPFService.validar(request.getAssociado().getCpf());
        log.info("Encontrando sessão para votação");
        Sessao sessao = sessaoRepository.findById(request.getSessaoId())
                .orElseThrow(() -> new GenericNotFoundException(String.format("Sessão com id %d não existe.", request.getSessaoId())));
        log.info("Sessão ID [{}] encontrata.", sessao.getId());
        if (LocalDateTime.now().isAfter(sessao.getDataHoraFim()))
            throw new GenericBadRequestException(String.format("Sessão com id %d está encerrada.", request.getSessaoId()));
        log.info("Buscando associado CPF [{}]", request.getAssociado().getCpf());
        Associado associado = associadoRepository.findByCpf(request.getAssociado().getCpf())
                .orElse(associadoMapper.toEntity(request.getAssociado()));
        log.info("Associado CPF [{}] encontrado com sucesso.", request.getAssociado().getCpf());
        if (sessaoContemAssociado(sessao, associado))
            throw new GenericBadRequestException(String.format("Associado %s não tem permissão para votar na sessão de id %d.",
                    associado.getCpf(), request.getSessaoId()));
        Voto voto = Voto.builder()
                .sessao(sessao)
                .associado(associado)
                .voto(request.getVoto())
                .build();
        Voto votoRegistrado = votoRepository.save(voto);
        log.info("Votação ID [{}] registrada com sucesso para o associado CPF [{}].",
                votoRegistrado.getId(), votoRegistrado.getAssociado().getCpf());
        return votoMapper.toResponse(votoRegistrado);
    }

    public Page<VotoResponse> pesquisar(Pageable pageable) {
        log.info("Pesquisando votação.");
        return this.votoRepository.findAll(pageable).map(votoMapper::toResponse);
    }

    public ContagemVotosResponse contabilizar(Long sessaoId) {
        long votosParaNao;
        long votosParaSim;
        log.info("Realizada contagem dos votos para sessão [{}].", sessaoId);
        Optional<Sessao> sessao = sessaoRepository.findById(sessaoId);
        if (!sessao.isPresent()) return null;
        List<Voto> votacoes = votoRepository.findBySessaoId(sessaoId);
        if (!votacoes.isEmpty()) ; {
            votosParaNao = votacoes.stream().filter(votacao -> VotoEnum.NAO.equals(votacao.getVoto())).count();
            votosParaSim = votacoes.stream().filter(votacao -> VotoEnum.SIM.equals(votacao.getVoto())).count();
        }
        log.info("Contagem do votos da sessão [{}] realizada com sucesso.", sessao.get().getId());
        Map<VotoEnum, Long> votos = new HashMap<>();
        votos.put(VotoEnum.NAO, votosParaNao);
        votos.put(VotoEnum.SIM, votosParaSim);
        return ContagemVotosResponse.builder()
                .sessaoId(sessao.get().getId())
                .pauta(pautaMapper.toResponse(sessao.get().getPauta()))
                .votos(votos)
                .build();
    }

    public VotoResponse alterar(Long sessaoId, String cpf) {
        validarCPFService.validar(cpf);
        log.info("Alterando voto do associado CPF [{}].", cpf);
        Voto voto = votoRepository.findBySessaoIdAndAssociadoCpf(sessaoId, cpf)
                .orElseThrow(() -> new GenericNotFoundException(String.format("Associado com CPF %s não encontrado.", cpf)));
        VotoEnum novoVoto = VotoEnum.SIM.equals(voto.getVoto()) ? VotoEnum.NAO : VotoEnum.SIM;
        voto.setVoto(novoVoto);
        Voto votoAlterado = votoRepository.save(voto);
        log.info("Alteração de voto do associado CPF [{}] realizada com sucesso.", cpf);
        return votoMapper.toResponse(votoAlterado);
    }

    private static boolean sessaoContemAssociado(Sessao sessao, Associado associado) {
        return !sessao.getAssociados().isEmpty() && !sessao.getAssociados().stream().map(Associado::getCpf).collect(toList()).contains(associado.getCpf());
    }
}
