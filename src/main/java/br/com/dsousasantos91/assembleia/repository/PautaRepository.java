package br.com.dsousasantos91.assembleia.repository;

import br.com.dsousasantos91.assembleia.domain.Pauta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PautaRepository extends JpaRepository<Pauta, Long> {
    List<Pauta> findByAssembleiaId(Long assembleiaId);
}
