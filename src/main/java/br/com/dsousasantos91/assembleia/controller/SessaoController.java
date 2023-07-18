package br.com.dsousasantos91.assembleia.controller;

import br.com.dsousasantos91.assembleia.event.RecursoCriadoEvent;
import br.com.dsousasantos91.assembleia.service.SessaoService;
import br.com.dsousasantos91.assembleia.service.dto.request.SessaoEmLoteRequest;
import br.com.dsousasantos91.assembleia.service.dto.request.SessaoRequest;
import br.com.dsousasantos91.assembleia.service.dto.response.SessaoResponse;
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
import java.util.List;

@Api(value = "API REST - Entidade Sessao")
@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping("/v1/sessao")
public class SessaoController {

	private final SessaoService sessaoService;
	private final ApplicationEventPublisher publish;

	@ApiOperation(value = "Abertura de sessão.")
	@PostMapping(path = "/abrir", produces = "application/json")
	public ResponseEntity<SessaoResponse> abrir(@Valid @RequestBody SessaoRequest request, HttpServletResponse servletResponse) {
		SessaoResponse response = this.sessaoService.abrir(request);
		publish.publishEvent(new RecursoCriadoEvent(this, servletResponse, response.getId()));
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@ApiOperation(value = "Abertura de sessão em lote.")
	@PostMapping(path = "/abrirEmLote", produces = "application/json")
	public ResponseEntity<List<SessaoResponse>> abrirEmLote(@Valid @RequestBody SessaoEmLoteRequest request, HttpServletResponse servletResponse) {
		List<SessaoResponse> responseList = this.sessaoService.abrirEmLote(request);
		responseList.forEach(response -> publish.publishEvent(new RecursoCriadoEvent(this, servletResponse, response.getId())));
		return ResponseEntity.status(HttpStatus.CREATED).body(responseList);
	}

	@ApiOperation(value = "Listagem de sessões.")
	@GetMapping
	public ResponseEntity<Page<SessaoResponse>> pesquisar(
			@SortDefault.SortDefaults({ @SortDefault(sort = "dataHoraInicio") }) Pageable pageable) {
		Page<SessaoResponse> response = sessaoService.pesquisar(pageable);
		return ResponseEntity.ok(response);
	}

	@ApiOperation(value = "Informações sobre uma sessão específica.")
	@GetMapping(path = "/{id}", produces = "application/json")
	public ResponseEntity<SessaoResponse> buscaoPorId(@PathVariable Long id) {
		SessaoResponse response = sessaoService.buscarPorId(id);
		return ResponseEntity.ok(response);
	}

	@ApiOperation(value = "Prorrogar prazo da sessão.")
	@PutMapping(path = "/prorrogar/{id}", produces = "application/json")
	public ResponseEntity<SessaoResponse> prorrogar(@PathVariable Long id, @Valid @RequestBody SessaoRequest request) {
		SessaoResponse response = this.sessaoService.prorrogar(id, request);
		return ResponseEntity.ok(response);
	}

	@ApiOperation(value = "Encerramento de sessão.")
	@DeleteMapping(path = "/encerrar/{sessaoId}", produces = "application/json")
	public ResponseEntity<SessaoResponse> encerrar(@Valid @PathVariable Long sessaoId, HttpServletResponse servletResponse) {
		SessaoResponse response = this.sessaoService.encerrar(sessaoId);
		publish.publishEvent(new RecursoCriadoEvent(this, servletResponse, response.getId()));
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@ApiOperation(value = "Apaga sessão.")
	@DeleteMapping(path = "/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void apagar(@PathVariable Long id) {
		this.sessaoService.apagar(id);
	}
}
