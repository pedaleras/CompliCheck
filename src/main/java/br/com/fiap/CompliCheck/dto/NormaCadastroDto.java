package br.com.fiap.CompliCheck.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record NormaCadastroDto(

        Long id,

        @NotBlank(message = "A descrição da norma é obrigatória")
        String descricao,

        @NotBlank(message = "A categoria da norma é obrigatória")
        String categoria,

        @NotNull(message = "A data limite é obrigatória")
        @FutureOrPresent(message = "A data limite deve ser hoje ou no futuro")
        LocalDate dataLimite,

        @NotNull(message = "O ID da empresa é obrigatório")
        Long empresaId

) {}
