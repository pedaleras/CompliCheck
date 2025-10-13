package br.com.fiap.CompliCheck.dto;

import br.com.fiap.CompliCheck.model.Alerta;
import java.time.LocalDate;

public record AlertaExibicaoDto(

        Long id,
        String status,
        LocalDate dataVerificacao,
        Long normaId

) {
    public AlertaExibicaoDto(Alerta alerta) {
        this(
                alerta.getId(),
                alerta.getStatus(),
                alerta.getDataVerificacao(),
                alerta.getNorma().getId()
        );
    }
}
