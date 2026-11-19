package com.project.apilistadetarefas.security.token;

import com.project.apilistadetarefas.repository.TokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.logging.Logger;

@Component
@RequiredArgsConstructor
public class TokenCleanupJob {

    private static final Logger log = Logger.getLogger(TokenCleanupJob.class.getName());
    private final TokenRepository tokenRepository;

    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void cleanup() {
        int removed = tokenRepository.deleteExpired(Instant.now());
        log.info("Limpeza de tokens: {} expirados removidos");
    }
}
