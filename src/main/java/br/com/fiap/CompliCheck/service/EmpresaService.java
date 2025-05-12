package br.com.fiap.CompliCheck.service;


import br.com.fiap.CompliCheck.dto.EmpresaCadastroDto;
import br.com.fiap.CompliCheck.dto.EmpresaExibicaoDto;
import br.com.fiap.CompliCheck.dto.NormaExibicaoDto;
import br.com.fiap.CompliCheck.model.Empresa;
import br.com.fiap.CompliCheck.model.Norma;
import br.com.fiap.CompliCheck.repository.EmpresaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmpresaService {

    @Autowired
    private EmpresaRepository repository;

    public List<EmpresaExibicaoDto> listarEmpresas() {
        return repository
                .findAll()
                .stream()
                .map(EmpresaExibicaoDto::new)
                .toList();
    }

    public EmpresaExibicaoDto buscarPorId(Long id) {
        Optional<Empresa> empresaOptional = repository.findById(id);
        if (empresaOptional.isPresent())
            return new EmpresaExibicaoDto(empresaOptional.get());
        else
            throw new EntityNotFoundException("Não foi possível encontrar a Empresa!");
    }

    public EmpresaExibicaoDto salvar(EmpresaCadastroDto empresaCadastroDto) {
        Empresa empresa = new Empresa();
        BeanUtils.copyProperties(empresaCadastroDto, empresa);

        return new EmpresaExibicaoDto(repository.save(empresa));
    }

    public ResponseEntity<Void> deletar(Long id) {
        Optional<Empresa> empresaOptional = repository.findById(id);
        if (empresaOptional.isPresent()) {
            repository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else
            return ResponseEntity.notFound().build();
    }

    public EmpresaExibicaoDto atualizar(EmpresaCadastroDto empresaAtualizada) {
        Optional<Empresa> empresaOptional = repository.findById(empresaAtualizada.id());

        if (empresaOptional.isPresent()) {
            Empresa empresa = new Empresa();
            BeanUtils.copyProperties(empresaAtualizada, empresa);

            return new EmpresaExibicaoDto(repository.save(empresa));
        } else
            throw new RuntimeException("Contato não encontrado");

    }
}

