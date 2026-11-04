package com.example.likelion14th_springboot.global.config;


import com.example.likelion14th_springboot.jwt.JwtTokenProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String email = oAuth2User.getAttribute("email");

        // 아래 메서드 이름은 현재 JwtTokenProvider 구현에 맞춰야 함
        String accessToken = jwtTokenProvider.generateAccessToken(email);

        String redirectUrl = UriComponentsBuilder
                .fromUriString("http://localhost:8080/oauth2/success") //프론트와 협업이라면 프론트 주소를 넣어줘야함.
                .queryParam("accessToken", accessToken)
                .build()
                .encode()
                .toUriString();

        response.sendRedirect(redirectUrl);
    }
}