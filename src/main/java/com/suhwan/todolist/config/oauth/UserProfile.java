package com.suhwan.todolist.config.oauth;

import com.suhwan.todolist.domain.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserProfile {

    private String email;
    private String provider;
    private String nickname;

    public User toUser() {
        return User.builder()
                .email(email)
                .provider(provider)
                .nickname(nickname)
                .build();
    }

}
