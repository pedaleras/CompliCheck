package br.com.fiap.CompliCheck.dto;

import br.com.fiap.CompliCheck.model.Empresa;
import java.util.List;

public record EmpresaExibicaoDto(
        Long id,
        String nome,
        String cnpj,
        String setor,
        List<NormaResumoDto> normas
) {
    public EmpresaExibicaoDto(Empresa e) {
        this(
                e.getId(),
                e.getNome(),
                e.getCnpj(),
                e.getSetor(),
                e.getNormas().stream()
                        .map(n -> new NormaResumoDto(
                                n.getId(),
                                n.getDescricao(),
                                n.getDataLimite()
                        ))
                        .toList()
        );
    }
}
