package br.com.dsousasantos91.assembleia.repository;

import br.com.dsousasantos91.assembleia.domain.Sessao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SessaoRepository extends JpaRepository<Sessao, Long> {
}
