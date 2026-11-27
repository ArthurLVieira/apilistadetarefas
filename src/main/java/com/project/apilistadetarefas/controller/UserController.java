package com.project.apilistadetarefas.controller;

import com.project.apilistadetarefas.dto.request.ChangePasswordRequest;
import com.project.apilistadetarefas.dto.request.PasswordResetConfirmRequest;
import com.project.apilistadetarefas.dto.request.PasswordResetRequest;
import com.project.apilistadetarefas.dto.request.PerfilRequest;
import com.project.apilistadetarefas.dto.response.UserResponse;
import com.project.apilistadetarefas.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Usuários", description = "Perfil e recuperação de senha dos usuários.")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    @Operation(summary = "Consultar perfil do usuário autenticado")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<UserResponse> getPerfil() {
        return ResponseEntity.ok(userService.getPerfil());
    }

    @PutMapping("/me")
    @Operation(summary = "Atualizar perfil do usuário autenticado")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<UserResponse> updatePerfil(@Valid @RequestBody PerfilRequest request) {
        return ResponseEntity.ok(userService.updatePerfil(request));
    }

    @PutMapping("/me/password")
    @Operation(summary = "Alterar senha do usuário autenticado")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/password-reset/request")
    @Operation(summary = "Solicitar recuperação de senha")
    public ResponseEntity<Void> requestPasswordReset(@Valid @RequestBody PasswordResetRequest request) {
        userService.requestPasswordReset(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/password-reset/confirm")
    @Operation(summary = "Confirmar recuperação de senha")
    public ResponseEntity<Void> confirmPasswordReset(@Valid @RequestBody PasswordResetConfirmRequest request) {
        userService.confirmPasswordReset(request);
        return ResponseEntity.noContent().build();
    }
}
