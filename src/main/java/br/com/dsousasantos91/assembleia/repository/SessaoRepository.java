package br.com.dsousasantos91.assembleia.repository;

import br.com.dsousasantos91.assembleia.domain.Sessao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SessaoRepository extends JpaRepository<Sessao, Long> {

    Optional<List<Sessao>> findByDataHoraFimLessThanAndNotificacaoEncerramentoEnviadaIsFalse(LocalDateTime now);
}
