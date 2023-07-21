package br.com.dsousasantos91.assembleia.service;

import br.com.dsousasantos91.assembleia.domain.Assembleia;
import br.com.dsousasantos91.assembleia.domain.Pauta;
import br.com.dsousasantos91.assembleia.domain.Sessao;
import br.com.dsousasantos91.assembleia.exception.GenericNotFoundException;
import br.com.dsousasantos91.assembleia.mapper.AssembleiaMapper;
import br.com.dsousasantos91.assembleia.repository.AssembleiaRepository;
import br.com.dsousasantos91.assembleia.repository.SessaoRepository;
import br.com.dsousasantos91.assembleia.scheduler.NotificadorScheduler;
import br.com.dsousasantos91.assembleia.service.dto.request.AssembleiaRequest;
import br.com.dsousasantos91.assembleia.service.dto.request.AssembleiaUpdateRequest;
import br.com.dsousasantos91.assembleia.service.dto.response.AssembleiaResponse;
import br.com.dsousasantos91.assembleia.util.PropertyUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Slf4j
@Service
@RequiredArgsConstructor
public class AssembleiaService {
    private final AssembleiaRepository assembleiaRepository;
    private final SessaoRepository sessaoRepository;
    private final AssembleiaMapper assembleiaMapper;
    private final NotificadorScheduler notificadorScheduler;

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
        Assembleia assembleia = this.assembleiaRepository.findById(id)
                .orElseThrow(() -> new GenericNotFoundException(String.format("Assembleia ID: [%d] não encontrada.", id)));
        log.info("Assembleia ID [{}] encontrada.", assembleia.getId());
        return this.assembleiaMapper.toResponse(assembleia);
    }

    @Transactional
    public AssembleiaResponse atualizar(Long id, AssembleiaUpdateRequest request) {
        log.info("Atualizando assembleia ID [{}]", id);
        Assembleia assembleiaEncontrada = this.assembleiaRepository.findById(id)
                .orElseThrow(() -> new GenericNotFoundException(String.format("Assembleia ID: [%d] não encontrada.", id)));
        Assembleia assembleia = this.assembleiaMapper.toEntity(request);
        PropertyUtils.copyNonNullProperties(assembleia, assembleiaEncontrada, "local");
        PropertyUtils.copyNonNullProperties(assembleia.getLocal(), assembleiaEncontrada.getLocal());
        Assembleia assembleiaAtualizada = this.assembleiaRepository.save(assembleiaEncontrada);
        log.info("Assembleia ID [{}] atualizada com sucesso.", assembleiaEncontrada.getId());
        return this.assembleiaMapper.toResponse(assembleiaAtualizada);
    }

    public AssembleiaResponse encerrar(Long id) {
        log.info("Encerrando assembleia ID [{}]", id);
        Assembleia assembleia = this.assembleiaRepository.findById(id)
                .orElseThrow(() -> new GenericNotFoundException(String.format("Assembleia ID: [%d] não encontrada.", id)));
        List<Long> pautasIds = assembleia.getPautas().stream().map(Pauta::getId).collect(toList());
        if (!pautasIds.isEmpty()) {
            log.info("Encerrando sessões abertas para a assembleia ID [{}]", id);
            this.encerrarSessao(pautasIds);
            log.info("Sessões da assembleia ID [{}] encerrada com sucesso.", id);
        }
        assembleia.setDataHoraFimApuracao(LocalDateTime.now());
        assembleia.setPautas(pautasIds.stream().map(pautaId -> Pauta.builder().id(pautaId).build()).collect(toList()));
        Assembleia assembleiaEncerrada = assembleiaRepository.save(assembleia);
        log.info("Assembleia ID [{}] encerrada com sucesso.",assembleiaEncerrada.getId());
        return assembleiaMapper.toResponse(assembleiaEncerrada);
    }

    public void apagar(Long id) {
        log.info("Apagando assembleia ID [{}]", id);
        assembleiaRepository.deleteById(id);
        log.info("Assembleia ID [{}] apagada com sucesso.",id);
    }

    public void encerrarSessao(List<Long> pautasIds) {
        List<Sessao> sessoes = sessaoRepository.findByPautaIdInAndDataHoraFimIsAfter(pautasIds, LocalDateTime.now());
        if (sessoes.isEmpty()) return;
        sessoes.forEach(sessao -> {
            sessao.setDataHoraFim(LocalDateTime.now());
            notificadorScheduler.agendarNotificacao(sessao);
        });
        sessaoRepository.saveAll(sessoes);

    }
}
