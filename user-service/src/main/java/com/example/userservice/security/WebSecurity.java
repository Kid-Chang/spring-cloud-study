package com.example.userservice.security;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurity{

    @Bean
    protected SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http.csrf( (csrf) -> csrf.disable());
        http
            .authorizeRequests(authorizeRequests ->
                authorizeRequests
                    .requestMatchers("/users/**").permitAll()
            )
            .formLogin(withDefaults());
        http.headers((headers) -> headers.frameOptions((frameOptions) -> frameOptions.sameOrigin()));
        return http.build();
    }
}
