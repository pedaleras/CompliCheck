package br.com.fiap.CompliCheck.service;

import br.com.fiap.CompliCheck.dto.NormaCadastroDto;
import br.com.fiap.CompliCheck.dto.NormaExibicaoDto;
import br.com.fiap.CompliCheck.model.Empresa;
import br.com.fiap.CompliCheck.model.Norma;
import br.com.fiap.CompliCheck.repository.EmpresaRepository;
import br.com.fiap.CompliCheck.repository.NormaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NormaService {

    @Autowired
    private NormaRepository repository;

    @Autowired
    private EmpresaRepository empresaRepository;

    public List<NormaExibicaoDto> listar() {
        return repository
                .findAll()
                .stream()
                .map(NormaExibicaoDto::new)
                .toList();
    }

    public NormaExibicaoDto buscarPorId(Long id) {
        Optional<Norma> normaOptional = repository.findById(id);
        if (normaOptional.isPresent())
            return new NormaExibicaoDto(normaOptional.get());
        else
            throw new EntityNotFoundException("Não foi possível encontrar a Norma!");
    }

    public NormaExibicaoDto salvar(NormaCadastroDto dto) {
        Empresa empresa = empresaRepository.findById(dto.empresaId())
                .orElseThrow(() -> new EntityNotFoundException("Empresa não encontrada"));

        Norma norma = new Norma();
        BeanUtils.copyProperties(dto, norma);

        norma.setEmpresa(empresa);

        return new NormaExibicaoDto(repository.save(norma));

    }

    public NormaExibicaoDto atualizar(NormaCadastroDto normaCadastroDto) {
        Optional<Norma> normaOptional = repository.findById(normaCadastroDto.id());

        if (normaOptional.isPresent()) {
            Norma norma = new Norma();
            BeanUtils.copyProperties(normaCadastroDto, norma);
            return new NormaExibicaoDto(repository.save(norma));
        } else
            throw new RuntimeException("Norma não encontrada");
    }

    public ResponseEntity<Void> deletar(Long id) {
        Optional<Norma> normaOptional = repository.findById(id);
        if (normaOptional.isPresent()) {
            repository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else
            return ResponseEntity.notFound().build();
    }
}
