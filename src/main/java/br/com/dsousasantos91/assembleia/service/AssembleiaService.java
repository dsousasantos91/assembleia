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
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AssembleiaService {
    private final AssembleiaRepository assembleiaRepository;
    private final AssembleiaMapper assembleiaMapper;

    public AssembleiaResponse criar(AssembleiaRequest request) {
        log.info("Criando assembleia {}", request.getTipoAssembleia());
        Assembleia assembleia = this.assembleiaMapper.toEntity(request);
        assembleia.getLocal().setAssembleia(assembleia);
        Assembleia assembleiaRegistrada = this.assembleiaRepository.save(assembleia);
        log.info("Assembleia {} registrada com sucesso. ID: [{}]", assembleiaRegistrada.getTipoAssembleia(), assembleiaRegistrada.getId());
        return this.assembleiaMapper.toResponse(assembleiaRegistrada);
    }

    public Page<AssembleiaResponse> pesquisar(Pageable pageable) {
        log.info("Pesquisar assembleias");
        return this.assembleiaRepository.findAll(pageable).map(assembleiaMapper::toResponse);
    }

    public AssembleiaResponse buscarPorId(Long id) {
        log.info("Buscar assembleia ID [{}]", id);
        Assembleia assembleia = this.assembleiaRepository.findById(id).orElseThrow(GenericNotFoundException::new);
        log.info("Assembleia ID [{}] encontrada.", assembleia.getId());
        return this.assembleiaMapper.toResponse(assembleia);
    }

    public AssembleiaResponse atualizar(Long id, AssembleiaUpdateRequest request) {
        log.info("Atualizando assembleia ID [{}]", id);
        Optional<Assembleia> assembleiaEncontrada = this.assembleiaRepository.findById(id);
        if (assembleiaEncontrada.isEmpty())
            throw new GenericNotFoundException(String.format("Assembleia ID: [%d] não encontrada.", id));
        Assembleia assembleia = this.assembleiaMapper.toEntity(request);
        assembleia.getLocal().setAssembleia(assembleia);
        PropertyUtils.copyNonNullProperties(assembleia, assembleiaEncontrada.get());
        this.assembleiaRepository.save(assembleiaEncontrada.get());
        log.info("Assembleia ID [{}] atualizada com sucesso.", assembleiaEncontrada.get().getId());
        return this.assembleiaMapper.toResponse(assembleiaEncontrada.get());
    }

    public AssembleiaResponse encerrar(Long id) {
        log.info("Encerrando assembleia ID [{}]", id);
        Assembleia assembleia = this.assembleiaRepository.findById(id).orElseThrow(GenericNotFoundException::new);
        assembleia.setDataHoraFimApuracao(LocalDateTime.now());
        Assembleia assembleiaEncerrada = assembleiaRepository.save(assembleia);
        log.info("Assembleia ID [{}] encerrada com sucesso.",assembleiaEncerrada.getId());
        return assembleiaMapper.toResponse(assembleiaEncerrada);
    }

    public void apagar(Long id) {
        log.info("Apagando assembleia ID [{}]", id);
        assembleiaRepository.deleteById(id);
        log.info("Assembleia ID [{}] apagada com sucesso.",id);
    }
}
