package net.partala.task_manager.auth;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class SecurityUser implements UserDetails {

    @Getter
    private final Long id;
    private final String username;
    private final String password;
    private final List<String> roles;
    private final boolean active = true;

    public SecurityUser(Long id, String username, String password, List<String> roles) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.roles = roles;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }

    @Override public boolean isAccountNonLocked() {
        return active;
    }

    @Override public boolean isAccountNonExpired() { return true; }


    @Override public boolean isCredentialsNonExpired() { return true; }
}
