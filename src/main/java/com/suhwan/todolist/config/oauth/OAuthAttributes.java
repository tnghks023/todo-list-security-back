package com.suhwan.todolist.config.oauth;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;

public enum OAuthAttributes {

    GOOGLE("google", (attributes) -> {
        UserProfile userProfile = new UserProfile();
        userProfile.setNickname((String) attributes.get("name"));
        userProfile.setEmail((String) attributes.get("email"));
        return userProfile;
    }),
    NAVER("naver", (attributes) -> {
        UserProfile userProfile = new UserProfile();
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        userProfile.setNickname((String) response.get("name"));
        userProfile.setEmail((String) response.get("email"));
        return userProfile;
    }),;

    private final String registrationId;
    private final Function<Map<String, Object>, UserProfile> of;

    OAuthAttributes(String registrationId, Function<Map<String, Object>, UserProfile> of) {
        this.registrationId = registrationId;
        this.of = of;
    }

    public static UserProfile extract(String registrationId, Map<String, Object> attributes) {
        return Arrays.stream(values())
                .filter(provider -> registrationId.equals(provider.registrationId))
                .findFirst()
                .orElseThrow(IllegalAccessError::new)
                .of.apply(attributes);
    }
}
