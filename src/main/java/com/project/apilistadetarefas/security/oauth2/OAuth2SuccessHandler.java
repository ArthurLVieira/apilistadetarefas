package com.project.apilistadetarefas.security.oauth2;

import com.project.apilistadetarefas.security.UserDatails.CustomUserDatails;
import com.project.apilistadetarefas.security.jwt.JwtService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtService jwtService;

    @Value("${app.oauth2.redirect-uri}")
    private String frontendRedirectUri;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {
        CustomUserDatails userDatails = (CustomUserDatails) authentication.getPrincipal();

        String jwt = jwtService.generateToken(userDatails);

        String redirect = UriComponentsBuilder.fromUriString(frontendRedirectUri)
                .queryParam("token", jwt).build().toUriString();

        response.sendRedirect(redirect);
    }
}
