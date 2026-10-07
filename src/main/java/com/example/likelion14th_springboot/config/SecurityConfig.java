package com.example.likelion14th_springboot.config;

import com.example.likelion14th_springboot.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService; // UserDetailsService DI. 의존성 주입

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // 설정 추가
        http
                .cors((SecurityConfig::corsAllow)) // CORS 설정
                .csrf(AbstractHttpConfigurer::disable) // 비활성화
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/join", "/login").permitAll() // 회원가입, 로그인은 모두 허용
                        .requestMatchers("/**").authenticated()) // 나머지는 인증된 사용자만 허용
                .formLogin(Customizer.withDefaults())
                .logout(Customizer.withDefaults())
                .userDetailsService(customUserDetailsService)
        ;

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private static void corsAllow(CorsConfigurer<HttpSecurity> corsConfigurer) {
        corsConfigurer.configurationSource(request -> {
            CorsConfiguration configuration = new CorsConfiguration();

            configuration.setAllowedMethods(Collections.singletonList("*")); // 모든 메서드 허용
            configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000")); // 프론트에서 오는 요청 허용
            configuration.setAllowedHeaders(Collections.singletonList("*")); // 모든 헤더 허용
            configuration.setAllowCredentials(true);
            configuration.setMaxAge(3600L); // 1시간(3600초) 동안 오는 요청이 처리됨

            return configuration;
        });
    }
}
