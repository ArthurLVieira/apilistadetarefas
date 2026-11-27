package com.project.apilistadetarefas.service;

import com.project.apilistadetarefas.dto.request.ChangePasswordRequest;
import com.project.apilistadetarefas.dto.request.PasswordResetConfirmRequest;
import com.project.apilistadetarefas.dto.request.PasswordResetRequest;
import com.project.apilistadetarefas.dto.request.PerfilRequest;
import com.project.apilistadetarefas.dto.response.UserResponse;
import com.project.apilistadetarefas.entity.PasswordResetCode;
import com.project.apilistadetarefas.entity.User;
import com.project.apilistadetarefas.exception.exceptions.BusinessException;
import com.project.apilistadetarefas.mapper.UserMapper;
import com.project.apilistadetarefas.repository.PasswordResetCodeRepository;
import com.project.apilistadetarefas.repository.UserRepository;
import com.project.apilistadetarefas.security.utils.CurrentUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserServiceTest {

    private UserRepository userRepository;
    private UserMapper userMapper;
    private CurrentUser currentUser;
    private PasswordEncoder passwordEncoder;
    private PasswordResetCodeRepository resetCodeRepository;
    private PasswordResetEmailService resetEmailService;
    private UserService userService;
    private User user;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        userMapper = mock(UserMapper.class);
        currentUser = mock(CurrentUser.class);
        passwordEncoder = mock(PasswordEncoder.class);
        resetCodeRepository = mock(PasswordResetCodeRepository.class);
        resetEmailService = mock(PasswordResetEmailService.class);
        userService = new UserService(userRepository, userMapper, currentUser,
                passwordEncoder, resetCodeRepository, resetEmailService);
        user = User.builder().id(1L).name("Name").email("user@example.com")
                .password("encoded-old-password").build();
    }

    @Test
    void updatePerfilChangesOnlyTheAuthenticatedUsersName() {
        UserResponse response = UserResponse.builder().id(1L).name("New name")
                .email(user.getEmail()).build();
        when(currentUser.getUser()).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(response);

        UserResponse result = userService.updatePerfil(new PerfilRequest(" New name "));

        assertEquals("New name", user.getName());
        assertEquals(response, result);
        verify(userRepository).save(user);
    }

    @Test
    void changePasswordRejectsAnIncorrectCurrentPassword() {
        when(currentUser.getUser()).thenReturn(user);
        when(passwordEncoder.matches("incorrect", user.getPassword())).thenReturn(false);

        assertThrows(BusinessException.class, () -> userService.changePassword(
                new ChangePasswordRequest("incorrect", "new-password")));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void requestPasswordResetDoesNotRevealUnknownEmails() {
        when(userRepository.findByUsername("missing@example.com")).thenReturn(Optional.empty());

        userService.requestPasswordReset(new PasswordResetRequest("missing@example.com"));

        verify(resetEmailService, never()).sendPasswordResetCode(anyString(), anyString(), eq(10L));
    }

    @Test
    void incorrectResetCodeIncrementsAttempts() {
        PasswordResetCode resetCode = new PasswordResetCode();
        resetCode.setUser(user);
        resetCode.setCodeHash("encoded-code");
        resetCode.setExpiresAt(LocalDateTime.now().plusMinutes(10));
        when(userRepository.findByUsername(user.getEmail())).thenReturn(Optional.of(user));
        when(resetCodeRepository.findByUserIdForUpdate(user.getId())).thenReturn(Optional.of(resetCode));
        when(passwordEncoder.matches("000000", "encoded-code")).thenReturn(false);

        assertThrows(BusinessException.class, () -> userService.confirmPasswordReset(
                new PasswordResetConfirmRequest(user.getEmail(), "000000", "new-password")));

        assertEquals(1, resetCode.getAttempts());
        verify(resetCodeRepository).save(resetCode);
    }

    @Test
    void fifthIncorrectResetCodeInvalidatesTheCode() {
        PasswordResetCode resetCode = new PasswordResetCode();
        resetCode.setUser(user);
        resetCode.setCodeHash("encoded-code");
        resetCode.setExpiresAt(LocalDateTime.now().plusMinutes(10));
        resetCode.setAttempts(4);
        when(userRepository.findByUsername(user.getEmail())).thenReturn(Optional.of(user));
        when(resetCodeRepository.findByUserIdForUpdate(user.getId())).thenReturn(Optional.of(resetCode));
        when(passwordEncoder.matches("000000", "encoded-code")).thenReturn(false);

        assertThrows(BusinessException.class, () -> userService.confirmPasswordReset(
                new PasswordResetConfirmRequest(user.getEmail(), "000000", "new-password")));

        assertEquals(5, resetCode.getAttempts());
        verify(resetCodeRepository).delete(resetCode);
        verify(resetCodeRepository, never()).save(resetCode);
    }

    @Test
    void confirmPasswordResetChangesPasswordAndConsumesCode() {
        PasswordResetCode resetCode = new PasswordResetCode();
        resetCode.setUser(user);
        resetCode.setCodeHash("encoded-code");
        resetCode.setExpiresAt(LocalDateTime.now().plusMinutes(10));
        when(userRepository.findByUsername(user.getEmail())).thenReturn(Optional.of(user));
        when(resetCodeRepository.findByUserIdForUpdate(user.getId())).thenReturn(Optional.of(resetCode));
        when(passwordEncoder.matches("123456", "encoded-code")).thenReturn(true);
        when(passwordEncoder.encode("new-password")).thenReturn("encoded-new-password");

        userService.confirmPasswordReset(new PasswordResetConfirmRequest(
                user.getEmail(), "123456", "new-password"));

        assertEquals("encoded-new-password", user.getPassword());
        verify(userRepository).save(user);
        verify(resetCodeRepository).delete(resetCode);
    }
}