package com.project.apilistadetarefas.controller;

import com.project.apilistadetarefas.controller.generics.Generic;
import com.project.apilistadetarefas.dto.request.LoginRequest;
import com.project.apilistadetarefas.dto.request.RefreshTokenRequest;
import com.project.apilistadetarefas.dto.request.RegisterRequest;
import com.project.apilistadetarefas.dto.response.ApiResponse;
import com.project.apilistadetarefas.dto.response.AuthResponse;
import com.project.apilistadetarefas.dto.response.UserResponse;
import com.project.apilistadetarefas.security.token.ClientInfo;
import com.project.apilistadetarefas.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Cadastro, autenticação e renovação de tokens.")
public class AuthController implements Generic {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Registrar usuário")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request,
            HttpServletRequest httpRequest
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(
                authService.register(request, ClientInfo.from(httpRequest))
        );
    }

    @PostMapping("/login")
    @Operation(summary = "Autenticar usuário")
    public ResponseEntity<AuthResponse> login(
            @RequestBody @Valid LoginRequest request,
            HttpServletRequest httpRequest
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(
                authService.login(request, ClientInfo.from(httpRequest))
        );
    }

    @PostMapping("/refresh")
    @Operation(summary = "Renovar tokens de acesso")
    public ResponseEntity<AuthResponse> refresh(
            @RequestBody @Valid RefreshTokenRequest request,
            HttpServletRequest httpRequest
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(
                authService.refresh(request, ClientInfo.from(httpRequest))
        );
    }
}
