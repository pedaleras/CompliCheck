package br.com.fiap.CompliCheck.exception;

import jakarta.persistence.EntityNotFoundException;

public class AlertaNaoEncontradoException extends EntityNotFoundException {
    public AlertaNaoEncontradoException() {
        super("Alerta não encontrado na base de dados!");
    }
}
