package com.project.apilistadetarefas.security.oauth2;

import com.project.apilistadetarefas.security.UserDatails.CustomUserDatails;
import com.project.apilistadetarefas.security.jwt.JwtProperties;
import com.project.apilistadetarefas.security.jwt.JwtService;
import com.project.apilistadetarefas.security.token.ClientInfo;
import com.project.apilistadetarefas.service.TokenService;
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
    private final TokenService tokenService;
    private final JwtProperties props;

    @Value("${app.oauth2.redirect-uri}")
    private String frontendRedirectUri;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {
        CustomUserDatails userDatails = (CustomUserDatails) authentication.getPrincipal();
        assert userDatails != null;

        String accessToken = jwtService.generateToken(userDatails);
        String refreshToken = tokenService.issueRefreshToken(
                userDatails.getUser(), ClientInfo.from(request)
        );

        String redirect = UriComponentsBuilder.fromUriString(frontendRedirectUri)
                .queryParam("accessToken", accessToken)
                .queryParam("refreshToken", refreshToken)
                .queryParam("tokenType", "Bearer")
                .queryParam("expiresIn", props.expiresIn())
                .build().toUriString();

        response.sendRedirect(redirect);
    }
}
