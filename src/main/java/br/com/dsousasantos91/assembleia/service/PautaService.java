package br.com.dsousasantos91.assembleia.service;

import br.com.dsousasantos91.assembleia.domain.Pauta;
import br.com.dsousasantos91.assembleia.exception.GenericNotFoundException;
import br.com.dsousasantos91.assembleia.mapper.PautaMapper;
import br.com.dsousasantos91.assembleia.repository.PautaRepository;
import br.com.dsousasantos91.assembleia.service.dto.request.PautaRequest;
import br.com.dsousasantos91.assembleia.service.dto.response.PautaResponse;
import br.com.dsousasantos91.assembleia.util.PropertyUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PautaService {
    private final PautaRepository pautaRepository;
    private final PautaMapper pautaMapper;

    public Page<PautaResponse> pesquisar(Pageable pageable) {
        log.info("Pesquisar pautas");
        return this.pautaRepository.findAll(pageable).map(pautaMapper::toResponse);
    }

    public PautaResponse buscarPorId(Long id) {
        log.info("Buscar pauta ID [{}]", id);
        Pauta pauta = this.pautaRepository.findById(id).orElseThrow(GenericNotFoundException::new);
        log.info("Pauta ID [{}] encontrada.", pauta.getId());
        return this.pautaMapper.toResponse(pauta);
    }

    public PautaResponse atualizar(Long id, PautaRequest request) {
        log.info("Atualizando pauta ID [{}]", id);
        Optional<Pauta> pautaEncontrada = this.pautaRepository.findById(id);
        if (pautaEncontrada.isEmpty())
            throw new GenericNotFoundException(String.format("Pauta ID: [%d] não encontrada.", id));
        Pauta pauta = this.pautaMapper.toEntity(request);
        PropertyUtils.copyNonNullProperties(pauta, pautaEncontrada.get());
        this.pautaRepository.save(pautaEncontrada.get());
        log.info("Pauta ID [{}] atualizada com sucesso.", pautaEncontrada.get().getId());
        return this.pautaMapper.toResponse(pautaEncontrada.get());
    }

    public void apagar(Long id) {
        log.info("Apagando pauta ID [{}]", id);
        pautaRepository.deleteById(id);
        log.info("Pauta ID [{}] apagada com sucesso.",id);
    }
}
