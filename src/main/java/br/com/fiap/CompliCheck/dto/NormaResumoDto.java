package br.com.fiap.CompliCheck.dto;

import java.time.LocalDate;

public record NormaResumoDto(
        Long id,
        String descricao,
        LocalDate dataLimite
) {
    public NormaResumoDto(Long id, String descricao, LocalDate dataLimite) {
        this.id = id;
        this.descricao = descricao;
        this.dataLimite = dataLimite;
    }
}
