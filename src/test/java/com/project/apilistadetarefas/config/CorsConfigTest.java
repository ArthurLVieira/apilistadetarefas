package com.project.apilistadetarefas.config;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CorsConfigTest {

    @Test
    void allowsProductionOriginAndApiPreflightHeaders() {
        CorsConfigurationSource source = new CorsConfig().corsConfigurationSource();
        HttpServletRequest request = new MockHttpServletRequest("OPTIONS", "/actuator/health");
        CorsConfiguration configuration = source.getCorsConfiguration(request);

        assertEquals("https://listadetarefas-sooty.vercel.app",
                configuration.checkOrigin("https://listadetarefas-sooty.vercel.app"));
        assertTrue(configuration.checkHttpMethod(HttpMethod.OPTIONS).contains(HttpMethod.OPTIONS));
        assertEquals(List.of("Authorization", "Content-Type"),
                configuration.checkHeaders(List.of("Authorization", "Content-Type")));
    }
}