package br.com.dsousasantos91.assembleia.repository;

import br.com.dsousasantos91.assembleia.domain.Sessao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SessaoRepository extends JpaRepository<Sessao, Long> {

    @Query("select s from Sessao s where s.pauta.id in :pautasIds and s.dataHoraFim > :now")
    List<Sessao> findSessoesAbertasPorPautaIdIn(List<Long> pautasIds, LocalDateTime now);

    List<Sessao> findByPautaIdIn(List<Long> pautasIds);
}
