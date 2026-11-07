package com.project.apilistadetarefas.service;

import com.project.apilistadetarefas.entity.Token;
import com.project.apilistadetarefas.entity.User;
import com.project.apilistadetarefas.exception.enums.ErrorCode;
import com.project.apilistadetarefas.exception.exceptions.BusinessException;
import com.project.apilistadetarefas.repository.TokenRepository;
import com.project.apilistadetarefas.security.jwt.JwtProperties;
import com.project.apilistadetarefas.security.token.ClientInfo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;

@Service
@RequiredArgsConstructor
public class TokenService {

    private static final Logger log = LoggerFactory.getLogger(TokenService.class);
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int TOKEN_BYTES = 32;

    private final TokenRepository tokenRepository;
    private final JwtProperties props;

    @Transactional
    public String issueRefreshToken(User user, ClientInfo client) {
        String rawToken = generateOpaqueToken();

        Token entity = Token.builder()
                .tokenHash(hash(rawToken))
                .user(user)
                .expiresAt(Instant.now().plusMillis(props.expiresIn()))
                .userAgent(client.userAgent())
                .ipAddress(client.ipAddress())
                .build();

        tokenRepository.save(entity);

        return rawToken;
    }

    @Transactional
    public User validateAndRotate(String rawToken) {
        String hash = hash(rawToken);

        Token token = tokenRepository.findByTokenHash(hash).orElseThrow(() ->
                new BusinessException(ErrorCode.TKN_INVALID));

        if (token.isRevoked()) {
            log.warn("Reuso de refresh token detectado. userId={} ip={}",
                    token.getUser().getId(), token.getIpAddress());

            tokenRepository.revokeAllByUserId(token.getUser().getId(), Instant.now());

            throw new BusinessException(ErrorCode.TKN_INVALID_SESSION);
        }

        if (token.isExpired()) {
            throw new BusinessException(ErrorCode.TKN_INVALID_SESSION);
        }

        token.setRevoked(true);
        token.setRevokedAt(Instant.now());
        tokenRepository.save(token);

        return token.getUser();
    }

    @Transactional
    public void revoke(String rawToken) {
        tokenRepository.findByTokenHash(hash(rawToken)).ifPresent(t -> {
            t.setRevoked(true);
            t.setRevokedAt(Instant.now());
            tokenRepository.save(t);
        });
    }

    @Transactional
    public void revokeAll(User user) {
        tokenRepository.revokeAllByUserId(user.getId(), Instant.now());
    }

    private String generateOpaqueToken() {
        byte[] bytes = new byte[TOKEN_BYTES];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hash(String raw) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(raw.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 indisponível", e);
        }
    }
}
