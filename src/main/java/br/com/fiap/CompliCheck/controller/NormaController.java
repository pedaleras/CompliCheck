package br.com.fiap.CompliCheck.controller;

import br.com.fiap.CompliCheck.dto.NormaCadastroDto;
import br.com.fiap.CompliCheck.dto.NormaExibicaoDto;
import br.com.fiap.CompliCheck.service.NormaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/normas")
public class NormaController {

    @Autowired
    private NormaService service;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<NormaExibicaoDto> listarNormas() {
        return service.listar();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public NormaExibicaoDto buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NormaExibicaoDto salvar(@RequestBody @Valid NormaCadastroDto norma) {
        return service.salvar(norma);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public NormaExibicaoDto atualizar(@RequestBody @Valid NormaCadastroDto normaAtualizada) {
        return service.salvar(normaAtualizada);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        return service.deletar(id);
    }
}
