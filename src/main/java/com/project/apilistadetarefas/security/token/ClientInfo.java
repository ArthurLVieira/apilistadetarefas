package com.project.apilistadetarefas.security.token;

import jakarta.servlet.http.HttpServletRequest;

public record ClientInfo(String userAgent, String ipAddress) {

    public static ClientInfo from(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");

        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        } else {
            ip = ip.split(",")[0].trim();
        }

        String ua = request.getHeader("user-agent");
        if (ua != null || ua.length() > 255) ua = ua.substring(0, 255);

        return new ClientInfo(ua, ip);
    }

    public static ClientInfo unknow() {
        return new ClientInfo(null, null);
    }
}
