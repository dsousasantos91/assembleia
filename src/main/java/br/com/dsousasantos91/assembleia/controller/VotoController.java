package br.com.dsousasantos91.assembleia.controller;

import br.com.dsousasantos91.assembleia.event.RecursoCriadoEvent;
import br.com.dsousasantos91.assembleia.service.VotoService;
import br.com.dsousasantos91.assembleia.service.dto.request.VotoRequest;
import br.com.dsousasantos91.assembleia.service.dto.response.ContagemVotosResponse;
import br.com.dsousasantos91.assembleia.service.dto.response.VotoResponse;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Api(value = "API REST - Entidade Votacao")
@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping(value = "/api/v1/voto", produces = { "application/json;charset=UTF-8" })
public class VotoController {

	private final VotoService votoService;
	private final ApplicationEventPublisher publish;

	@ApiOperation(value = "Votação de Pauta.")
	@PostMapping(path = "/votar")
	public ResponseEntity<VotoResponse> votar(@Valid @RequestBody VotoRequest request, HttpServletResponse servletResponse) {
		VotoResponse response = this.votoService.votar(request);
		publish.publishEvent(new RecursoCriadoEvent(this, servletResponse, response.getId()));
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@ApiOperation(value = "Listagem de pautas.")
	@GetMapping
	public ResponseEntity<Page<VotoResponse>> pesquisar(
			@SortDefault.SortDefaults({ @SortDefault(sort = "id") }) Pageable pageable) {
		Page<VotoResponse> response = votoService.pesquisar(pageable);
		return ResponseEntity.ok(response);
	}

	@ApiOperation(value = "Contabiliza Votos.")
	@GetMapping(path = "/contabilizar/sessao/{sessaoId}")
	public ResponseEntity<ContagemVotosResponse> contabilizar(@PathVariable Long sessaoId) {
		ContagemVotosResponse response = votoService.contabilizar(sessaoId);
		return ResponseEntity.ok(response);
	}

	@ApiOperation(value = "Altera voto associado.")
	@PutMapping(path = "/alterar/sessao/{sessaoId}/associado/{cpf}")
	public ResponseEntity<VotoResponse> alterar(@PathVariable Long sessaoId, @PathVariable String cpf) {
		VotoResponse response = this.votoService.alterar(sessaoId, cpf);
		return ResponseEntity.ok(response);
	}
}
