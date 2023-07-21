package br.com.dsousasantos91.assembleia.repository;

import br.com.dsousasantos91.assembleia.domain.Sessao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SessaoRepository extends JpaRepository<Sessao, Long> {
    List<Sessao> findByPautaIdInAndDataHoraFimIsAfter(List<Long> pautasIds, LocalDateTime now);
}
