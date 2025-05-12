package br.com.fiap.CompliCheck.service;

import br.com.fiap.CompliCheck.dto.AlertaCadastroDto;
import br.com.fiap.CompliCheck.dto.AlertaExibicaoDto;
import br.com.fiap.CompliCheck.dto.NormaExibicaoDto;
import br.com.fiap.CompliCheck.model.Alerta;
import br.com.fiap.CompliCheck.model.Norma;
import br.com.fiap.CompliCheck.repository.AlertaRepository;
import br.com.fiap.CompliCheck.repository.NormaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AlertaService {

    @Autowired
    private AlertaRepository repository;
    @Autowired
    private NormaRepository normaRepository;

    public AlertaExibicaoDto salvar(AlertaCadastroDto dto) {
        Norma norma = normaRepository.findById(dto.normaId())
                .orElseThrow(() -> new EntityNotFoundException("Norma não encontrada"));

        Alerta alerta = new Alerta();
        alerta.setStatus(dto.status());
        alerta.setDataVerificacao(dto.dataVerificacao());
        alerta.setNorma(norma);

        return new AlertaExibicaoDto(repository.save(alerta));
    }

    public List<AlertaExibicaoDto> listar() {
        return repository.findAll()
                .stream()
                .map(AlertaExibicaoDto::new)
                .toList();
    }

    public AlertaExibicaoDto buscarPorId(Long id) {
        Optional<Alerta> alertaOptional = repository.findById(id);
        if (alertaOptional.isPresent())
            return new AlertaExibicaoDto(alertaOptional.get());
        else
            throw new EntityNotFoundException("Não foi possível encontrar o Alerta!");
    }

    public AlertaExibicaoDto atualizar(AlertaCadastroDto dto) {
        Alerta existente = repository.findById(dto.id())
                .orElseThrow(() -> new EntityNotFoundException("Alerta não encontrado"));

        existente.setStatus(dto.status());
        existente.setDataVerificacao(dto.dataVerificacao());

        return new AlertaExibicaoDto(repository.save(existente));
    }

    public ResponseEntity<Void> deletar(Long id) {
        Optional<Alerta> alertaOptional = repository.findById(id);
        if (alertaOptional.isPresent()) {
            repository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else
            return ResponseEntity.notFound().build();
    }
}


