package br.com.dsousasantos91.assembleia.service;

import br.com.dsousasantos91.assembleia.domain.Assembleia;
import br.com.dsousasantos91.assembleia.exception.GenericNotFoundException;
import br.com.dsousasantos91.assembleia.mapper.AssembleiaMapper;
import br.com.dsousasantos91.assembleia.repository.AssembleiaRepository;
import br.com.dsousasantos91.assembleia.service.dto.request.AssembleiaRequest;
import br.com.dsousasantos91.assembleia.service.dto.request.AssembleiaUpdateRequest;
import br.com.dsousasantos91.assembleia.service.dto.response.AssembleiaResponse;
import br.com.dsousasantos91.assembleia.util.PropertyUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AssembleiaService {
    private final AssembleiaRepository assembleiaRepository;
    private final AssembleiaMapper assembleiaMapper;

    public AssembleiaResponse criar(AssembleiaRequest request) {
        Assembleia assembleia = this.assembleiaMapper.toEntity(request);
        assembleia.getLocal().setAssembleia(assembleia);
        Assembleia assembleiaRegistrada = this.assembleiaRepository.save(assembleia);
        return this.assembleiaMapper.toResponse(assembleiaRegistrada);
    }

    public Page<AssembleiaResponse> pesquisar(Pageable pageable) {
        return this.assembleiaRepository.findAll(pageable).map(assembleiaMapper::toResponse);
    }

    public AssembleiaResponse buscarPorId(Long id) {
        Assembleia assembleia = this.assembleiaRepository.findById(id).orElseThrow(GenericNotFoundException::new);
        return this.assembleiaMapper.toResponse(assembleia);
    }

    public AssembleiaResponse atualizar(Long id, AssembleiaUpdateRequest request) {
        return this.assembleiaRepository.findById(id)
                .map(assembleiaEncontrada -> {
                    Assembleia assembleia = this.assembleiaMapper.toEntity(request);
                    assembleia.getLocal().setAssembleia(assembleia);
                    PropertyUtils.copyNonNullProperties(assembleia, assembleiaEncontrada);
                    this.assembleiaRepository.save(assembleiaEncontrada);
                    return this.assembleiaMapper.toResponse(assembleiaEncontrada);
                })
                .orElseGet(() -> {
                    Assembleia assembleia = this.assembleiaMapper.toEntity(request);
                    assembleia.getLocal().setAssembleia(assembleia);
                    Assembleia assembleiaSalvo = this.assembleiaRepository.save(assembleia);
                    return this.assembleiaMapper.toResponse(assembleiaSalvo);
                });
    }

    public AssembleiaResponse encerrar(Long id) {
        Assembleia assembleia = this.assembleiaRepository.findById(id).orElseThrow(GenericNotFoundException::new);
        assembleia.setDataHoraFimApuracao(LocalDateTime.now());
        Assembleia assembleiaEncerrada = assembleiaRepository.save(assembleia);
        return assembleiaMapper.toResponse(assembleiaEncerrada);
    }

    public void apagar(Long id) {
        assembleiaRepository.deleteById(id);
    }
}
