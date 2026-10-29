package com.project.apilistadetarefas.security.oauth2;

import com.project.apilistadetarefas.entity.User;
import com.project.apilistadetarefas.security.UserDatails.CustomUserDatails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Map;

public class CustomOAuth2User extends CustomUserDatails implements OAuth2User {
    private final Map<String, Object> attributes;

    public CustomOAuth2User(User user, Map<String, Object> attributes) {
        super(user);
        this.attributes = attributes;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {
        return getUsername();
    }
}
