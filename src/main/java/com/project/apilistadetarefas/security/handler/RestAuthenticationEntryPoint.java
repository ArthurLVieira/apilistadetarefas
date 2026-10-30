package com.project.apilistadetarefas.security.handler;

import com.project.apilistadetarefas.dto.response.ApiErroResponse;
import com.project.apilistadetarefas.dto.response.ApiResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull AuthenticationException authException
    ) throws IOException, ServletException {
        String message = "Autenticação necessária para acessar este recurso";

        if (authException instanceof  org.springframework.security.authentication.InsufficientAuthenticationException) {
            message = "Token ausente ou inválodo";
        }

        List<ApiErroResponse> error = List.of(
                ApiErroResponse.builder()
                .message(message)
                .build()
        );

        ApiResponse<ApiErroResponse> body = ApiResponse.<ApiErroResponse>builder()
                .success(false)
                .errors(error)
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        objectMapper.writeValue(response.getWriter(), body);
    }
}
