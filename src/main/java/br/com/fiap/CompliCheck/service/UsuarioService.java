package br.com.fiap.CompliCheck.service;


import br.com.fiap.CompliCheck.dto.UsuarioCadastroDto;
import br.com.fiap.CompliCheck.dto.UsuarioExibicaoDto;
import br.com.fiap.CompliCheck.model.Usuario;
import br.com.fiap.CompliCheck.repository.UsuarioRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository repository;

    public Page<UsuarioExibicaoDto> listarTodos(Pageable paginacao) {
        return repository
                .findAll(paginacao)
                .map(UsuarioExibicaoDto::new);
    }

    public UsuarioExibicaoDto buscarPorId(Long id) {
        Optional<Usuario> usuarioOptional = repository.findById(id);

        if (usuarioOptional.isPresent())
            return new UsuarioExibicaoDto(usuarioOptional.get());
        else
            throw new RuntimeException("Usuário não encontrado");
    }

    public UsuarioExibicaoDto salvar(UsuarioCadastroDto usuarioCadastroDto) {

        String senhaCriptografada = new BCryptPasswordEncoder().encode(usuarioCadastroDto.senha());

        Usuario usuario = new Usuario();
        BeanUtils.copyProperties(usuarioCadastroDto, usuario);

        usuario.setSenha(senhaCriptografada);

        return new UsuarioExibicaoDto(repository.save(usuario));
    }

    public void deletar(Long id) {
        Optional<Usuario> usuarioOptional = repository.findById(id);

        if (usuarioOptional.isPresent())
            repository.delete(usuarioOptional.get());
        else
            throw new RuntimeException("Usuário não encontrado");
    }
}


