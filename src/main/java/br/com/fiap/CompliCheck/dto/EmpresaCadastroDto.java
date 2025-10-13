package br.com.fiap.CompliCheck.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record EmpresaCadastroDto(

        Long id,

        @NotBlank(message = "O nome da empresa é obrigatório")
        @Size(max = 100, message = "O nome não pode exceder 100 caracteres")
        String nome,

        @NotBlank(message = "O CNPJ é obrigatório")
        @Pattern(regexp = "\\d{14}", message = "O CNPJ deve conter 14 dígitos numéricos")
        String cnpj,

        @NotBlank(message = "O setor é obrigatório")
        @Size(max = 50, message = "O setor não pode exceder 50 caracteres")
        String setor

) {}
