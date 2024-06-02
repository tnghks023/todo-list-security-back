package com.suhwan.todolist.config.oauth;

import com.suhwan.todolist.config.jwt.TokenProvider;
import com.suhwan.todolist.domain.RefreshToken;
import com.suhwan.todolist.domain.User;
import com.suhwan.todolist.repository.RefreshTokenRepository;
import com.suhwan.todolist.service.UserService;
import com.suhwan.todolist.util.CookieUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.Duration;

@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    public static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";
    public static final Duration REFRESH_TOKEN_DURATION = Duration.ofDays(14);
    public static final Duration ACCESS_TOKEN_DURATION = Duration.ofDays(1);
//    public static final String REDIRECT_PATH = "http://localhost:5173/todoList";
    public static final String REDIRECT_PATH = "http://ec2-13-125-238-210.ap-northeast-2.compute.amazonaws.com:5000/todoList";



    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final OAuth2AuthorizationRequestBasedOnCookieRepository oAuth2AuthorizationRequestBasedOnCookieRepository;
    private final UserService userService;

    @Override
    public void onAuthenticationSuccess(final HttpServletRequest request, final HttpServletResponse response, final Authentication authentication) throws IOException, ServletException {
        final OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        final User user = userService.findByEmail((String) oAuth2User.getAttributes().get("email"));

        // Generate and save refresh token
        // 리프레시 토큰 생성 -> 저장 -> 쿠키에 저장
        final String refreshToken = tokenProvider.generateToken(user, REFRESH_TOKEN_DURATION);
        saveRefreshToken(user.getId(), refreshToken);
        addRefreshTokenToCookie(request, response, refreshToken);

        // 액세스 토큰 생성 -> 패스에 엑세스 토큰 추가
        // Generate access token and set security context
        final String accessToken = tokenProvider.generateToken(user, ACCESS_TOKEN_DURATION);
        final String targetUrl = getTargetUrl(accessToken);

//        if (tokenProvider.validToken(accessToken)) {
//            final Authentication authentication2 = tokenProvider.getAuthentication(accessToken);
//            SecurityContextHolder.getContext().setAuthentication(authentication2);
//        }

        // Clear authentication attributes and redirect
        // 인증 관련 설정값, 쿠키 제거
        clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    private void saveRefreshToken(final Long userId, final String newRefreshToken) {
        final RefreshToken refreshToken = refreshTokenRepository.findByUserId(userId)
                .map(entity -> entity.update(newRefreshToken))
                .orElse(new RefreshToken(userId, newRefreshToken));

        refreshTokenRepository.save(refreshToken);
    }

    private void addRefreshTokenToCookie(final HttpServletRequest request, final HttpServletResponse response, final String refreshToken) {
        final int cookieMaxAge = (int) REFRESH_TOKEN_DURATION.toSeconds();
        CookieUtil.deleteCookie(request, response, REFRESH_TOKEN_COOKIE_NAME);
        CookieUtil.addCookie(response, REFRESH_TOKEN_COOKIE_NAME, refreshToken, cookieMaxAge);
    }

    private void clearAuthenticationAttributes(final HttpServletRequest request, final HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        oAuth2AuthorizationRequestBasedOnCookieRepository.removeAuthorizationRequestCookies(request, response);
    }
    // 액세스 토큰을 패스에 추가
    private String getTargetUrl(final String token) {
        return UriComponentsBuilder.fromUriString(REDIRECT_PATH)
                .queryParam("token", token)
                .build()
                .toUriString();
    }
}
