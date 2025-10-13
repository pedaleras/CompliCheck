package br.com.fiap.CompliCheck.dto;


import br.com.fiap.CompliCheck.model.Usuario;
import br.com.fiap.CompliCheck.model.UsuarioRole;

public record UsuarioExibicaoDto(
        Long usuarioId,
        String nome,
        String email,
        UsuarioRole role
) {
    public UsuarioExibicaoDto(Usuario usuario) {
        this(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getRole()
        );
    }
}

