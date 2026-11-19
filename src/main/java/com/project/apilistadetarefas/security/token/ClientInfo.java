package com.project.apilistadetarefas.security.token;

import jakarta.servlet.http.HttpServletRequest;

public record ClientInfo(String userAgent, String ipAddress) {

    private static final int MAX_USER_AGENT = 255;
    private static final int MAX_IP = 45;

    public static ClientInfo from(HttpServletRequest request) {
        return new ClientInfo(
                truncate(request.getHeader("User-Agent"), MAX_USER_AGENT),
                resolveIp(request)
        );
    }

    public static ClientInfo unknown() {
        return new ClientInfo(null, null);
    }

    private static String truncate(String value, int max) {
        if (value == null) return null;
        return value.length() <= max ? value : value.substring(0, max);
    }

    private static String resolveIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            String first = forwarded.split(",")[0].trim();
            return truncate(first, MAX_IP);
        }
        return truncate(request.getRemoteAddr(), MAX_IP);
    }
}
