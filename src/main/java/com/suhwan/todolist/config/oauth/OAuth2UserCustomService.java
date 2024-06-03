package com.suhwan.todolist.config.oauth;

import com.suhwan.todolist.domain.User;
import com.suhwan.todolist.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@RequiredArgsConstructor
@Service
public class OAuth2UserCustomService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        // 요청을 바탕으로 유저 정보를 담은 객체 반환
        OAuth2User user = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration()
                .getRegistrationId(); // OAuth 서비스 이름(ex. google, naver)

        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName(); // OAuth 로그인 시 키(pk)가 되는 값

        UserProfile userProfile = OAuthAttributes.extract(registrationId, user.getAttributes());
        userProfile.setProvider(registrationId);
        saveOrUpdate(userProfile);
        return user;
    }

    //유저가 있으면 업데이트, 없으면 유저 생성
    private User saveOrUpdate(UserProfile userProfile) {
        String email = userProfile.getEmail();
        String name = userProfile.getNickname();
        String provider = userProfile.getProvider();
        User user = userRepository.findByEmailAndProvider(email,provider )
                .map(entity -> entity.update(name, email))
                .orElse(User.builder()
                        .email(email)
                        .nickname(name)
                        .provider(provider)
                        .build());
        return userRepository.save(user);
    }
}
