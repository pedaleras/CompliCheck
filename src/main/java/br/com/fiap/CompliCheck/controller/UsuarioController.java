package br.com.fiap.CompliCheck.controller;

import br.com.fiap.CompliCheck.dto.UsuarioCadastroDto;
import br.com.fiap.CompliCheck.dto.UsuarioExibicaoDto;
import br.com.fiap.CompliCheck.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService service;

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public Page<UsuarioExibicaoDto> listarTodos(Pageable paginacao) {
        return service.listarTodos(paginacao);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UsuarioExibicaoDto buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioExibicaoDto salvar(@RequestBody UsuarioCadastroDto usuarioCadastroDto) {
        return service.salvar(usuarioCadastroDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable Long id) {
        service.deletar(id);
    }
}

