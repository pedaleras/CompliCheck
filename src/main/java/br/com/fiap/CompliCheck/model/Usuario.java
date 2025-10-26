package br.com.fiap.CompliCheck.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "TBL_CC_USUARIO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id") // Adicionado 'of = "id"' para melhor prática em @EqualsAndHashCode com JPA
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    @Column(unique = true)
    private String email;

    private String senha;

    @ManyToOne
    @JoinColumn(name = "empresa_id")
    private Empresa empresa; // Certifique-se de que a classe Empresa existe e está configurada corretamente

    @Enumerated(EnumType.STRING)
    private UsuarioRole role; // Certifique-se de que o Enum UsuarioRole existe e está configurado corretamente

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (this.role == UsuarioRole.ADMIN) { // Use chaves {} para blocos if/else mesmo que de uma linha para melhor legibilidade
            return List.of(
                    new SimpleGrantedAuthority("ROLE_ADMIN"),
                    new SimpleGrantedAuthority("ROLE_USER")
            );
        } else {
            return List.of(
                    new SimpleGrantedAuthority("ROLE_USER")
            );
        }
    }

    @Override
    public String getPassword() {
        return this.senha;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    // --- Métodos adicionados para completar a implementação de UserDetails ---

    @Override
    public boolean isAccountNonExpired() {
        // Implemente sua lógica de expiração de conta aqui.
        // Por padrão, retorna true, indicando que a conta nunca expira.
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // Implemente sua lógica de bloqueio de conta aqui.
        // Por padrão, retorna true, indicando que a conta nunca está bloqueada.
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // Implemente sua lógica de expiração de credenciais (senha) aqui.
        // Por padrão, retorna true, indicando que as credenciais nunca expiram.
        return true;
    }

    @Override
    public boolean isEnabled() {
        // Implemente sua lógica de habilitação/desabilitação de conta aqui.
        // Por padrão, retorna true, indicando que a conta está sempre habilitada.
        return true;
    }
}