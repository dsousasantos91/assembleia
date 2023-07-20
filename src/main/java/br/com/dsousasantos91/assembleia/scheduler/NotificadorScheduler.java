package br.com.dsousasantos91.assembleia.scheduler;

import br.com.dsousasantos91.assembleia.domain.Sessao;
import br.com.dsousasantos91.assembleia.producer.NotificarVotosProducer;
import br.com.dsousasantos91.assembleia.scheduler.dto.Notificador;
import br.com.dsousasantos91.assembleia.service.VotoService;
import br.com.dsousasantos91.assembleia.service.dto.response.ContagemVotosResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ScheduledFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificadorScheduler {

    private final TaskScheduler taskScheduler;
    private final NotificarVotosProducer notificarVotosProducer;
    private final VotoService votoService;
    private final Map<String, ScheduledFuture<?>> schedulerMap = new HashMap<>();

    public void agendarNotificacao(Sessao sessao) {
        Notificador notificador = Notificador.builder().sessao(sessao).build();
        log.info("Agendamento de Notificacao [{}]", notificador.toString());
        cancelarNotificacao(notificador);
        ScheduledFuture<?> scheduleTask = taskScheduler.schedule(
                () -> {
                    ContagemVotosResponse contagemVotos = votoService.contabilizar(notificador.getSessao().getId());
                    notificarVotosProducer.enviarResultadoVotacao(contagemVotos);
                },
                new CronTrigger(notificador.getCron(), TimeZone.getTimeZone(TimeZone.getDefault().toZoneId()))
        );
        log.info("Agendamento de Notificacao: [{}] realizado com sucesso", notificador);
        schedulerMap.put(notificador.getNome(), scheduleTask);
    }

    private void cancelarNotificacao(Notificador notificador) {
        log.info("Cancelamento de Notificacao:"+ notificador.toString());
        if (schedulerMap.containsKey(notificador.getNome()))
            schedulerMap.get(notificador.getNome()).cancel(true);
    }
}
