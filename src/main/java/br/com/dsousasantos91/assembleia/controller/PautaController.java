package br.com.dsousasantos91.assembleia.controller;

import br.com.dsousasantos91.assembleia.service.PautaService;
import br.com.dsousasantos91.assembleia.service.dto.request.PautaRequest;
import br.com.dsousasantos91.assembleia.service.dto.response.PautaResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Api(value = "API REST - Entidade Pauta")
@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping(value = "/api/v1/pauta", produces = { "application/json;charset=UTF-8" })
public class PautaController {

	private final PautaService pautaService;

	@ApiOperation(value = "Listagem de pautas.")
	@GetMapping
	public ResponseEntity<Page<PautaResponse>> pesquisar(
			@SortDefault.SortDefaults({ @SortDefault(sort = "id") }) Pageable pageable) {
		Page<PautaResponse> response = pautaService.pesquisar(pageable);
		return ResponseEntity.ok(response);
	}

	@ApiOperation(value = "Informações sobre uma pauta.")
	@GetMapping(path = "/{id}")
	public ResponseEntity<PautaResponse> buscaoPorId(@PathVariable Long id) {
		PautaResponse response = pautaService.buscarPorId(id);
		return ResponseEntity.ok(response);
	}

	@ApiOperation(value = "Atualização de pauta.")
	@PutMapping(path = "/{id}")
	public ResponseEntity<PautaResponse> atualizar(@PathVariable Long id, @Valid @RequestBody PautaRequest request) {
		PautaResponse response = this.pautaService.atualizar(id, request);
		return ResponseEntity.ok(response);
	}

	@ApiOperation(value = "Apaga pauta.")
	@DeleteMapping(path = "/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void apagar(@PathVariable Long id) {
		this.pautaService.apagar(id);
	}
}
