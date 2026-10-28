package com.example.likelion14th_springboot.service;

import com.example.likelion14th_springboot.domain.Member;
import com.example.likelion14th_springboot.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        // 토큰 확인
        String accessToken = userRequest.getAccessToken().getTokenValue();
        System.out.println("Access Token: " + accessToken);
        //

        OAuth2User oAuth2User = super.loadUser(userRequest);

        String email = oAuth2User.getAttribute("email");
        String username = oAuth2User.getAttribute("name");


        Member member = memberRepository.findByEmail(email)
                .orElseGet(() -> Member.builder()
                        .email(email)
                        .name(username)
                        .password("") // OAuth 사용자는 비밀번호가 필요하지 않으므로 빈 문자열로 처리
                        .build());

        // 새로운 사용자는 DB에 저장
        if (member.getId() == null) {
            memberRepository.save(member);
        }

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                oAuth2User.getAttributes(),
                "email");
    }
}