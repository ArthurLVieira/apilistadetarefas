package com.project.apilistadetarefas.service;

import com.project.apilistadetarefas.dto.request.LoginRequest;
import com.project.apilistadetarefas.dto.request.RefreshTokenRequest;
import com.project.apilistadetarefas.dto.request.RegisterRequest;
import com.project.apilistadetarefas.dto.response.AuthResponse;
import com.project.apilistadetarefas.entity.User;
import com.project.apilistadetarefas.entity.enums.AuthProvider;
import com.project.apilistadetarefas.exception.enums.ErrorCode;
import com.project.apilistadetarefas.exception.exceptions.BusinessException;
import com.project.apilistadetarefas.repository.UserRepository;
import com.project.apilistadetarefas.security.UserDatails.CustomUserDatails;
import com.project.apilistadetarefas.security.jwt.JwtProperties;
import com.project.apilistadetarefas.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository repository;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final JwtProperties props;
    private final UserRepository userRepository;

    public AuthResponse register(RegisterRequest request) {
        var user = repository.findByUsername(request.getEmail());

        if (user.isPresent()) {
            throw new BusinessException(ErrorCode.USER_ALREADY_EXISTS);
        }

        User newUser = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(encoder.encode(request.getPassword()))
                .provider(AuthProvider.LOCAL)
                .build();

        newUser = repository.save(newUser);

        return toResponse(newUser);
    }

    public AuthResponse login(LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(), request.getPassword())
        );

        CustomUserDatails userDatails = (CustomUserDatails) auth.getPrincipal();
        assert userDatails != null;
        return toResponse(userDatails.getUser());
    }

    public AuthResponse refresh(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        if (!jwtService.isTokenValid(refreshToken) || !jwtService.isRefreshToken(refreshToken)) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        String username = jwtService.extractUsername(refreshToken);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        return toResponse(user);
    }

    private AuthResponse toResponse(User user) {
        return AuthResponse.builder()
                .accessToken(jwtService.generateToken(new CustomUserDatails(user)))
                .refreshToken(jwtService.generateRefreshToken(new CustomUserDatails(user)))
                .expiresIn(props.expiresIn())
                .tokenType("Bearer")
                .build();
    }
}
