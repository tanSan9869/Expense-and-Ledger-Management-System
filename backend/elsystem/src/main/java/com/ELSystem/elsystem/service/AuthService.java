package com.ELSystem.elsystem.service;

import com.ELSystem.elsystem.dto.request.LoginRequest;
import com.ELSystem.elsystem.dto.request.UserRequest;
import com.ELSystem.elsystem.dto.response.AuthResponse;
import com.ELSystem.elsystem.exception.DuplicateResourceException;
import com.ELSystem.elsystem.model.User;
import com.ELSystem.elsystem.repository.UserRepository;
import com.ELSystem.elsystem.util.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Transactional
    public AuthResponse register(UserRequest request){
        if(userRepository.existsByEmail(request.getEmail())){
            throw new DuplicateResourceException("User already exists with email: " + request.getEmail());
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();
        user = userRepository.save(user);

        String token = jwtUtil.generateToken(user.getEmail());

        return mapToResponse(user);
    }

    public AuthResponse login(LoginRequest request){
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(),request.getPassword()));
        } catch (AuthenticationException e) {
            log.error("Authentication failed or UNAUTHORIZED: {}", e.getMessage());
        }

        Optional<User> user = userRepository.findByEmail(request.getEmail());
        String token = jwtUtil.generateToken(user.get().getEmail());

        return mapToResponse(user.get());
    }

    public AuthResponse mapToResponse(User user) {
        return AuthResponse.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .token(jwtUtil.generateToken(user.getEmail()))
                .userId(user.getId())
                .build();
    }
}
