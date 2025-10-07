package com.example.wataerp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // OpenAPI/Swagger を全許可
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        // Actuatorのhealth/infoも許可（任意）
                        .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                        // それ以外は許可（学習中は全部許可でOK。将来は認可を付ける）
                        .anyRequest().permitAll());
        return http.build();
    }
}