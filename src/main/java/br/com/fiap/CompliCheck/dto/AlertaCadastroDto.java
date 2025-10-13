package br.com.fiap.CompliCheck.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record AlertaCadastroDto(

        Long id,

        @NotBlank(message = "O status do alerta é obrigatório")
        String status,

        @NotNull(message = "A data de verificação é obrigatória")
        LocalDate dataVerificacao,

        @NotNull(message = "O ID da norma é obrigatório")
        Long normaId

) {}

