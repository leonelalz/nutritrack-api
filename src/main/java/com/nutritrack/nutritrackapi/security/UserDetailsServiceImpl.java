package com.nutritrack.nutritrackapi.security;


import com.nutritrack.nutritrackapi.model.CuentaAuth;
import com.nutritrack.nutritrackapi.repository.CuentaAuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final CuentaAuthRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        CuentaAuth user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        Collection<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority(user.getRol().getTipo().name())
        );
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                user.isActive(),
                true, true, true,
                authorities
        );
    }

    public Long getPerfilUsuarioIdFromAuthentication(Authentication authentication) {
        String email = authentication.getName();
        CuentaAuth user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
        return user.getPerfilUsuario().getId();
    }
}
