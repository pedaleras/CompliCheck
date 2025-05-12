package br.com.fiap.CompliCheck.controller;

import br.com.fiap.CompliCheck.dto.AlertaCadastroDto;
import br.com.fiap.CompliCheck.dto.AlertaExibicaoDto;

import br.com.fiap.CompliCheck.service.AlertaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alertas")
public class AlertaController {

    @Autowired
    private AlertaService service;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<AlertaExibicaoDto> listarAlertas() {
        return service.listar();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public AlertaExibicaoDto buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AlertaExibicaoDto salvar(@RequestBody @Valid AlertaCadastroDto Alerta) {
        return service.salvar(Alerta);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public AlertaExibicaoDto atualizar(@RequestBody @Valid AlertaCadastroDto alertaAtualizado) {
        return service.atualizar(alertaAtualizado);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        return service.deletar(id);
    }
}
