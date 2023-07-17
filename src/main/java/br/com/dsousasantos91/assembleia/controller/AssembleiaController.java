package br.com.dsousasantos91.assembleia.controller;

import br.com.dsousasantos91.assembleia.event.RecursoCriadoEvent;
import br.com.dsousasantos91.assembleia.service.AssembleiaService;
import br.com.dsousasantos91.assembleia.service.dto.request.AssembleiaRequest;
import br.com.dsousasantos91.assembleia.service.dto.request.AssembleiaUpdateRequest;
import br.com.dsousasantos91.assembleia.service.dto.response.AssembleiaResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Api(value = "API REST - Entidade Assembleia")
@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping("/v1/assembleia")
public class AssembleiaController {

	private final AssembleiaService assembleiaService;
	private final ApplicationEventPublisher publish;

	@ApiOperation(value = "Cadastro de assembleia.")
	@PostMapping(produces = "application/json")
	public ResponseEntity<AssembleiaResponse> criar(@Valid @RequestBody AssembleiaRequest request, HttpServletResponse servletResponse) {
		AssembleiaResponse response = this.assembleiaService.criar(request);
		publish.publishEvent(new RecursoCriadoEvent(this, servletResponse, response.getId()));
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@ApiOperation(value = "Listagem de assembleias.")
	@GetMapping
	public ResponseEntity<Page<AssembleiaResponse>> pesquisar(
			@SortDefault.SortDefaults({ @SortDefault(sort = "dataHoraInicioApuracao") }) Pageable pageable) {
		Page<AssembleiaResponse> response = assembleiaService.pesquisar(pageable);
		return ResponseEntity.ok(response);
	}

	@ApiOperation(value = "Informações sobre uma assembleia.")
	@GetMapping(path = "/{id}", produces = "application/json")
	public ResponseEntity<AssembleiaResponse> buscaoPorId(@PathVariable Long id) {
		AssembleiaResponse response = assembleiaService.buscarPorId(id);
		return ResponseEntity.ok(response);
	}

	@ApiOperation(value = "Atualização de assembleia.")
	@PutMapping(path = "/{id}", produces = "application/json")
	public ResponseEntity<AssembleiaResponse> atualizar(@PathVariable Long id, @Valid @RequestBody AssembleiaUpdateRequest request) {
		AssembleiaResponse response = this.assembleiaService.atualizar(id, request);
		return ResponseEntity.ok(response);
	}

	@ApiOperation(value = "Encerramento de assembleia.")
	@DeleteMapping(path = "/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void remover(@PathVariable Long id) {
		this.assembleiaService.encerrar(id);
	}

}
