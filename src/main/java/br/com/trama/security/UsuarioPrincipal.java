package br.com.trama.security;

import br.com.trama.model.Usuario;
import br.com.trama.model.enums.PerfilUsuario;
import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public final class UsuarioPrincipal implements UserDetails {
    private final Long idUsuario;
    private final String email;
    private final String senhaHash;
    private final PerfilUsuario perfil;
    private final Long idConfeccao;

    public UsuarioPrincipal(Usuario usuario) {
        this.idUsuario = usuario.getIdUsuario();
        this.email = usuario.getEmail();
        this.senhaHash = usuario.getSenhaHash();
        this.perfil = usuario.getPerfil();
        this.idConfeccao = usuario.getConfeccao() == null ? null : usuario.getConfeccao().getIdConfeccao();
    }

    public Long getIdUsuario() { return idUsuario; }
    public PerfilUsuario getPerfil() { return perfil; }
    public Long getIdConfeccao() { return idConfeccao; }

    @Override public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + perfil.name()));
    }
    @Override public String getPassword() { return senhaHash; }
    @Override public String getUsername() { return email; }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}
