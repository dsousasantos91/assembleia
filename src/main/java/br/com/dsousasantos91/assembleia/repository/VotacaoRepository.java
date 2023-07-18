package br.com.dsousasantos91.assembleia.repository;

import br.com.dsousasantos91.assembleia.domain.Votacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VotacaoRepository extends JpaRepository<Votacao, Long> {
    Optional<List<Votacao>> findBySessaoId(Long sessaoId);
    Optional<Votacao> findBySessaoIdAndAssociadoCpf(Long sessaoId, String cpf);
}
