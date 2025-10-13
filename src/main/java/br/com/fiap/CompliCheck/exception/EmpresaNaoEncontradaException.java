package br.com.fiap.CompliCheck.exception;

import jakarta.persistence.EntityNotFoundException;

public class EmpresaNaoEncontradaException extends EntityNotFoundException {
    public EmpresaNaoEncontradaException() {
        super("Empresa não encontrada na base de dados!");
    }
}
