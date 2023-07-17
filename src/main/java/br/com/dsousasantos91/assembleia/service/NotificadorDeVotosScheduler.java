package br.com.dsousasantos91.assembleia.service;

import br.com.dsousasantos91.assembleia.domain.Sessao;
import br.com.dsousasantos91.assembleia.producer.NotificarVotacao;
import br.com.dsousasantos91.assembleia.repository.SessaoRepository;
import br.com.dsousasantos91.assembleia.service.dto.response.ContagemVotosResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificadorDeVotosScheduler {

    private final NotificarVotacao notificarVotacao;
    private final VotacaoService votacaoService;
    private final SessaoRepository sessaoRepository;

    @Scheduled(fixedRate = 60000)
    public void executeScheduledTask() {
        List<Sessao> sessoesEncerradas = sessaoRepository
                .findByDataHoraFimLessThanAndNotificacaoEncerramentoEnviadaIsFalse(LocalDateTime.now())
                .orElse(Collections.emptyList());
        sessoesEncerradas.forEach(sessao -> {
            ContagemVotosResponse contagemVotos = votacaoService.contabilizar(sessao.getId());
            notificarVotacao.enviarResultadoVotacao(contagemVotos);
            sessao.setNotificacaoEncerramentoEnviada(Boolean.TRUE);
        });
        sessaoRepository.saveAll(sessoesEncerradas);
    }
}
