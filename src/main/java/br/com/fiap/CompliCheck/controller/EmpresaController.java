package br.com.fiap.CompliCheck.controller;


import br.com.fiap.CompliCheck.dto.EmpresaCadastroDto;
import br.com.fiap.CompliCheck.dto.EmpresaExibicaoDto;
import br.com.fiap.CompliCheck.model.Empresa;
import br.com.fiap.CompliCheck.service.EmpresaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/empresas")
public class EmpresaController {

    @Autowired
    private EmpresaService service;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EmpresaExibicaoDto> listarEmpresas() {
        return service.listarEmpresas();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EmpresaExibicaoDto buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EmpresaExibicaoDto salvar(@RequestBody @Valid EmpresaCadastroDto empresa) {
        return service.salvar(empresa);
    }

    @PutMapping
    public EmpresaExibicaoDto atualizar(@RequestBody @Valid EmpresaCadastroDto empresaAtualizada) {
        return service.atualizar(empresaAtualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        return service.deletar(id);
    }
}
