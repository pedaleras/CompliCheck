package br.com.fiap.CompliCheck.exception;

import jakarta.persistence.EntityNotFoundException;

public class UsuarioNaoEncontradoException extends EntityNotFoundException {
    public UsuarioNaoEncontradoException() {
        super("Usuário não encontrado na base de dados!");
    }
}
