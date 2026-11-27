package com.project.apilistadetarefas.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class PasswordResetEmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${app.mail.from:no-reply@localhost}")
    private String from;

    public void sendPasswordResetCode(String email, String code, long expirationMinutes) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());
            Context context = new Context(Locale.forLanguageTag("pt-BR"));
            context.setVariable("code", code);
            context.setVariable("expirationMinutes", expirationMinutes);

            helper.setFrom(from);
            helper.setTo(email);
            helper.setSubject("Código para redefinir sua senha");
            helper.setText(templateEngine.process("password-reset", context), true);
            mailSender.send(message);
        } catch (MessagingException exception) {
            throw new MailSendException("Não foi possível preparar o e-mail de recuperação.", exception);
        }
    }
}