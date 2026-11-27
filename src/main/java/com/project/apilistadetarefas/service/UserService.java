package com.project.apilistadetarefas.service;

import com.project.apilistadetarefas.dto.request.ChangePasswordRequest;
import com.project.apilistadetarefas.dto.request.PasswordResetConfirmRequest;
import com.project.apilistadetarefas.dto.request.PasswordResetRequest;
import com.project.apilistadetarefas.dto.request.PerfilRequest;
import com.project.apilistadetarefas.dto.response.UserResponse;
import com.project.apilistadetarefas.entity.PasswordResetCode;
import com.project.apilistadetarefas.entity.User;
import com.project.apilistadetarefas.exception.exceptions.BusinessException;
import com.project.apilistadetarefas.exception.enums.ErrorCode;
import com.project.apilistadetarefas.mapper.UserMapper;
import com.project.apilistadetarefas.repository.PasswordResetCodeRepository;
import com.project.apilistadetarefas.repository.UserRepository;
import com.project.apilistadetarefas.security.utils.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final CurrentUser currentUser;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetCodeRepository resetCodeRepository;
    private final PasswordResetEmailService resetEmailService;

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final int MAX_CODE_ATTEMPTS = 5;

    @Value("${app.password-reset.code-expiration-minutes:10}")
    private long codeExpirationMinutes;

    @Transactional(readOnly = true)
    public UserResponse getPerfil() {
        return userMapper.toDto(currentUser.getUser());
    }

    public UserResponse updatePerfil(PerfilRequest request) {
        User user = currentUser.getUser();
        user.setName(request.name().trim());
        return userMapper.toDto(userRepository.save(user));
    }

    public void changePassword(ChangePasswordRequest request) {
        User user = currentUser.getUser();
        if (user.getPassword() == null
                || !passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.USER_INVALID_CURRENT_PASSWORD);
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }

    public void requestPasswordReset(PasswordResetRequest request) {
        Optional<User> userResult = userRepository.findByUsername(request.email().trim());
        if (userResult.isEmpty()) return;

        User user = userResult.get();
        String code = generateCode();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(codeExpirationMinutes);
        PasswordResetCode resetCode = resetCodeRepository.findByUser_Id(user.getId())
                .orElseGet(PasswordResetCode::new);
        resetCode.setUser(user);
        resetCode.setCodeHash(passwordEncoder.encode(code));
        resetCode.setExpiresAt(expiresAt);
        resetCode.setAttempts(0);
        resetCodeRepository.save(resetCode);

        resetEmailService.sendPasswordResetCode(user.getEmail(), code, codeExpirationMinutes);
    }

    @Transactional(noRollbackFor = BusinessException.class)
    public void confirmPasswordReset(PasswordResetConfirmRequest request) {
        User user = userRepository.findByUsername(request.email().trim())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_INVALID_PASSWORD_RESET_CODE));
        PasswordResetCode resetCode = resetCodeRepository.findByUserIdForUpdate(user.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_INVALID_PASSWORD_RESET_CODE));

        if (!resetCode.getExpiresAt().isAfter(LocalDateTime.now())) {
            resetCodeRepository.delete(resetCode);
            throw new BusinessException(ErrorCode.USER_INVALID_PASSWORD_RESET_CODE);
        }

        if (!passwordEncoder.matches(request.code(), resetCode.getCodeHash())) {
            resetCode.setAttempts(resetCode.getAttempts() + 1);
            if (resetCode.getAttempts() >= MAX_CODE_ATTEMPTS) {
                resetCodeRepository.delete(resetCode);
            } else {
                resetCodeRepository.save(resetCode);
            }
            throw new BusinessException(ErrorCode.USER_INVALID_PASSWORD_RESET_CODE);
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
        resetCodeRepository.delete(resetCode);
    }

    private String generateCode() {
        String value = Integer.toString(SECURE_RANDOM.nextInt(1_000_000));
        return "0".repeat(6 - value.length()) + value;
    }
}
