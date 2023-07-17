package br.com.dsousasantos91.assembleia.service;

import br.com.dsousasantos91.assembleia.domain.Pauta;
import br.com.dsousasantos91.assembleia.exception.GenericNotFoundException;
import br.com.dsousasantos91.assembleia.mapper.PautaMapper;
import br.com.dsousasantos91.assembleia.repository.PautaRepository;
import br.com.dsousasantos91.assembleia.service.dto.request.PautaRequest;
import br.com.dsousasantos91.assembleia.service.dto.response.PautaResponse;
import br.com.dsousasantos91.assembleia.util.PropertyUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PautaService {
    private final PautaRepository pautaRepository;
    private final PautaMapper pautaMapper;

    public Page<PautaResponse> pesquisar(Pageable pageable) {
        return this.pautaRepository.findAll(pageable).map(pautaMapper::toResponse);
    }

    public PautaResponse buscarPorId(Long id) {
        Pauta pauta = this.pautaRepository.findById(id).orElseThrow(GenericNotFoundException::new);
        return this.pautaMapper.toResponse(pauta);
    }

    public PautaResponse atualizar(Long id, PautaRequest request) {
        return this.pautaRepository.findById(id)
                .map(assembleiaEncontrada -> {
                    Pauta pauta = this.pautaMapper.toEntity(request);
                    PropertyUtils.copyNonNullProperties(pauta, assembleiaEncontrada);
                    this.pautaRepository.save(assembleiaEncontrada);
                    return this.pautaMapper.toResponse(assembleiaEncontrada);
                })
                .orElseGet(() -> {
                    Pauta pauta = this.pautaMapper.toEntity(request);
                    Pauta pautaSalvo = this.pautaRepository.save(pauta);
                    return this.pautaMapper.toResponse(pautaSalvo);
                });
    }
}
