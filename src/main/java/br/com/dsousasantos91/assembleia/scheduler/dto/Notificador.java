package br.com.dsousasantos91.assembleia.scheduler.dto;

import br.com.dsousasantos91.assembleia.domain.Sessao;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

import static java.util.Objects.isNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Notificador {

    private String nome;
    private String cron;
    private Sessao sessao;

    public String getNome() {
        if (isNull(sessao)) return nome;
        return "SESSAO_" + sessao.getId();
    }

    public String getCron() {
        if (isNull(sessao)) return "0 * * * * ?";
        if (LocalDateTime.now().isAfter(sessao.getDataHoraFim()))
            sessao.setDataHoraFim(LocalDateTime.now().plusSeconds(10));
        return String.format("%d %d %d %d %d ?",
            this.sessao.getDataHoraFim().getSecond(),
            this.sessao.getDataHoraFim().getMinute(),
            this.sessao.getDataHoraFim().getHour(),
            this.sessao.getDataHoraFim().getDayOfMonth(),
            this.sessao.getDataHoraFim().getMonthValue()
        );
    }
}
