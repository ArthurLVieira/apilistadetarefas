package com.project.apilistadetarefas.service;

import com.project.apilistadetarefas.dto.request.LoginRequest;
import com.project.apilistadetarefas.dto.request.RegisterRequest;
import com.project.apilistadetarefas.dto.response.AuthResponse;
import com.project.apilistadetarefas.dto.response.UserResponse;
import com.project.apilistadetarefas.entity.User;
import com.project.apilistadetarefas.entity.enums.AuthProvider;
import com.project.apilistadetarefas.exception.enums.ErrorCode;
import com.project.apilistadetarefas.exception.exceptions.BusinessException;
import com.project.apilistadetarefas.mapper.UserMapper;
import com.project.apilistadetarefas.repository.UserRepository;
import com.project.apilistadetarefas.security.UserDatails.CustomUserDatails;
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
    private final UserMapper mapper;

    public AuthResponse<UserResponse> register(RegisterRequest request) {
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

    public AuthResponse<UserResponse> login(LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(), request.getPassword())
        );

        CustomUserDatails userDatails = (CustomUserDatails) auth.getPrincipal();
        assert userDatails != null;
        return toResponse(userDatails.getUser());
    }

    private AuthResponse<UserResponse> toResponse(User user) {
        return AuthResponse.<UserResponse>builder()
                .token(jwtService.generateToken(new CustomUserDatails(user)))
                .data(mapper.toDto(user))
                .build();
    }
}
