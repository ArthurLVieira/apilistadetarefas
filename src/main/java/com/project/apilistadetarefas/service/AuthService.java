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
import com.project.apilistadetarefas.security.token.ClientInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
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
    private final TokenService tokenService;

    public AuthResponse register(RegisterRequest request, ClientInfo client) {
        repository.findByUsername(request.getEmail()).ifPresent(user -> {
            throw new BusinessException(ErrorCode.USER_ALREADY_EXISTS);
        });

        User entity = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(encoder.encode(request.getPassword()))
                .provider(AuthProvider.LOCAL)
                .build();

        entity = repository.save(entity);

        return ussueSession(entity, client);
    }

    public AuthResponse login(LoginRequest request, ClientInfo client) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(), request.getPassword())
            );

            CustomUserDatails userDatails = (CustomUserDatails) auth.getPrincipal();
            assert userDatails != null;
            return ussueSession(userDatails.getUser(), client);
        } catch (AuthenticationException e) {
            throw new BusinessException(ErrorCode.CDT_INVALID);
        }
    }

    public AuthResponse refresh(RefreshTokenRequest request, ClientInfo client) {
        User user = tokenService.validateAndRotate(request.getRefreshToken());
        return ussueSession(user, client);
    }

    public void logout(String refreshToken) {
        tokenService.revoke(refreshToken);
    }

    private AuthResponse ussueSession(User user, ClientInfo client) {
        CustomUserDatails userDatails = new CustomUserDatails(user);
        String accessToken = jwtService.generateToken(userDatails);
        String refreshToken = tokenService.issueRefreshToken(userDatails.getUser(), client);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(props.expiresIn())
                .tokenType("Bearer")
                .build();
    }
}
