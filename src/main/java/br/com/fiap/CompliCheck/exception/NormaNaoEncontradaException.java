package br.com.fiap.CompliCheck.exception;

import jakarta.persistence.EntityNotFoundException;

public class NormaNaoEncontradaException extends EntityNotFoundException {
    public NormaNaoEncontradaException() {
        super("Norma não encontrada na base de dados!");
    }
}
