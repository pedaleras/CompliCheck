package br.com.fiap.CompliCheck.dto;

public record EmpresaResumoDto(
        Long id,
        String nome
) {
    public EmpresaResumoDto(Long id, String nome) {
        this.id = id;
        this.nome = nome;
    }
}
