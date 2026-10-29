package com.project.apilistadetarefas.security.oauth2;

import com.project.apilistadetarefas.entity.User;
import com.project.apilistadetarefas.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.security.AuthProvider;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) {
        OAuth2User oAuth2User = super.loadUser(request);
        String email = oAuth2User.getAttributes().get("email").toString();
        String name = oAuth2User.getAttributes().get("name").toString();
        String sub = oAuth2User.getAttributes().get("sub").toString();

        User user = userRepository.findByUsername(email)
                .orElseGet(() -> {
                   User newUser = User.builder()
                           .email(email)
                           .name(name)
                           .provider(AuthProvider.GOOGLE)
                           .providerId(sub)
                           .build();
                });

        return new CustomOAuth2User(user, oAuth2User.getAttributes());
    }
}
