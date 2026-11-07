package com.project.apilistadetarefas.controller;

import com.project.apilistadetarefas.controller.generics.Generic;
import com.project.apilistadetarefas.dto.request.LoginRequest;
import com.project.apilistadetarefas.dto.request.RefreshTokenRequest;
import com.project.apilistadetarefas.dto.request.RegisterRequest;
import com.project.apilistadetarefas.dto.response.ApiResponse;
import com.project.apilistadetarefas.dto.response.AuthResponse;
import com.project.apilistadetarefas.dto.response.UserResponse;
import com.project.apilistadetarefas.service.AuthService;
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
public class AuthController implements Generic {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        var response = authService.register(request);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RequestBody @Valid LoginRequest request
    ) {
        var response = authService.login(request);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(
            @RequestBody @Valid RefreshTokenRequest request
    ) {
        var response = authService.refresh(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
