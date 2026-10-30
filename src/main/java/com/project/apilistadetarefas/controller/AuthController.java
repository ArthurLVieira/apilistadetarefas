package com.project.apilistadetarefas.controller;

import com.project.apilistadetarefas.controller.generics.Generic;
import com.project.apilistadetarefas.dto.request.LoginRequest;
import com.project.apilistadetarefas.dto.request.RegisterRequest;
import com.project.apilistadetarefas.dto.response.ApiResponse;
import com.project.apilistadetarefas.dto.response.AuthResponse;
import com.project.apilistadetarefas.dto.response.UserResponse;
import com.project.apilistadetarefas.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController implements Generic {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse<UserResponse>>> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        var authResponse = authService.register(request);

        return ResponseEntity.ok(
                ApiResponse.<AuthResponse<UserResponse>>builder()
                        .success(true)
                        .data(authResponse)
                        .message("Registro realizado com sucesso!")
                        .path("/register")
                        .traceId(getUri(authResponse.getData().getId()))
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse<UserResponse>>> login(
            @RequestBody @Valid LoginRequest request
    ) {
        var authResponse = authService.login(request);

        return ResponseEntity.ok(
                ApiResponse.<AuthResponse<UserResponse>>builder()
                        .success(true)
                        .data(authResponse)
                        .message("Login realizado com sucesso!")
                        .path("/login")
                        .traceId(getUri(authResponse.getData().getId()))
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }



}
