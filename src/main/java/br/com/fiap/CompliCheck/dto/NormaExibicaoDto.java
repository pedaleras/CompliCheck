package br.com.fiap.CompliCheck.dto;

import br.com.fiap.CompliCheck.model.Norma;
import java.time.LocalDate;

public record NormaExibicaoDto(
        Long id,
        String descricao,
        String categoria,
        LocalDate dataLimite,
        EmpresaResumoDto empresa
) {
    public NormaExibicaoDto(Norma n) {
        this(
                n.getId(),
                n.getDescricao(),
                n.getCategoria(),
                n.getDataLimite(),
                new EmpresaResumoDto(
                        n.getEmpresa().getId(),
                        n.getEmpresa().getNome()
                )
        );
    }
}
