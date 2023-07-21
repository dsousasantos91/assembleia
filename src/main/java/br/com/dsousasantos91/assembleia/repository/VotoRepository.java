package br.com.dsousasantos91.assembleia.repository;

import br.com.dsousasantos91.assembleia.domain.Voto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VotoRepository extends JpaRepository<Voto, Long> {
    List<Voto> findBySessaoId(Long sessaoId);
    Optional<Voto> findBySessaoIdAndAssociadoCpf(Long sessaoId, String cpf);
}
