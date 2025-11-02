package com.nutritrack.nutritrackapi.service;

import com.nutritrack.nutritrackapi.dto.request.LoginRequestDTO;
import com.nutritrack.nutritrackapi.dto.request.RegistroRequestDTO;
import com.nutritrack.nutritrackapi.dto.response.AuthResponse;
import com.nutritrack.nutritrackapi.exception.BusinessRuleException;
import com.nutritrack.nutritrackapi.model.PerfilUsuario;
import com.nutritrack.nutritrackapi.model.Rol;
import com.nutritrack.nutritrackapi.model.enums.TipoRol;
import com.nutritrack.nutritrackapi.model.CuentaAuth;
import com.nutritrack.nutritrackapi.repository.PerfilUsuarioRepository;
import com.nutritrack.nutritrackapi.repository.RolRepository;
import com.nutritrack.nutritrackapi.repository.CuentaAuthRepository;
import com.nutritrack.nutritrackapi.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class AuthService {

    private final CuentaAuthRepository userRepository;
    private final RolRepository roleRepository;
    private final PerfilUsuarioRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegistroRequestDTO request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessRuleException("Email already registered");
        }

        // Crear User
        CuentaAuth user = new CuentaAuth();
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));

        Rol userRole = roleRepository.findByTipo(TipoRol.ROLE_USER)
                .orElseThrow(() -> new BusinessRuleException("Role not found"));
        user.setRol(userRole);

        CuentaAuth savedUser = userRepository.save(user);

        // Crear Customer asociado
        PerfilUsuario customer = new PerfilUsuario();
        customer.setCuenta(savedUser);
        customer.setNombre(request.nombre());
        PerfilUsuario savedCustomer = customerRepository.save(customer);

        // Generar JWT con email, nombre y customerId
        String token = jwtUtil.generateToken(
                savedUser.getEmail(),
                savedCustomer.getNombre(),
                savedCustomer.getId()
        );

        return new AuthResponse(token, savedUser.getEmail(), savedCustomer.getNombre());
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequestDTO request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        CuentaAuth user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        PerfilUsuario customer = customerRepository.findByCuenta_Id(user.getId())
                .orElseThrow(() -> new RuntimeException("Customer not found for user"));

        // Generar JWT con email, nombre y customerId
        String token = jwtUtil.generateToken(
                user.getEmail(),
                customer.getNombre(),
                customer.getId()
        );

        return new AuthResponse(token, user.getEmail(), customer.getNombre());
    }
}