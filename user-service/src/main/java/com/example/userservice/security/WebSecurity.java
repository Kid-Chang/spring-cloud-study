package com.example.userservice.security;

import static org.springframework.security.config.Customizer.withDefaults;

import com.example.userservice.service.UserService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class WebSecurity{

    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private UserService userService;
    private Environment env;

    @Bean
    protected SecurityFilterChain configure(HttpSecurity http) throws Exception {
        // Configure AuthenticationManagerBuilder
        AuthenticationManagerBuilder authenticationManagerBuilder =
            http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder);

        AuthenticationManager authenticationManager = authenticationManagerBuilder.build();

        http.csrf( (csrf) -> csrf.disable());
//        http.csrf(AbstractHttpConfigurer::disable);


        http.authorizeHttpRequests((authz) -> authz
                    .requestMatchers(new AntPathRequestMatcher("/actuator/**")).permitAll()
                    .requestMatchers(new AntPathRequestMatcher("/h2-console/**")).permitAll()
                    .requestMatchers(new AntPathRequestMatcher("/users", "POST")).permitAll()
                    .requestMatchers(new AntPathRequestMatcher("/h2-console/**")).permitAll()
                    .requestMatchers(new AntPathRequestMatcher("/health_check")).permitAll()
//                        .requestMatchers("/**").access(this::hasIpAddress)
                    .requestMatchers("/**").access(
                        new WebExpressionAuthorizationManager("hasIpAddress('127.0.0.1') or hasIpAddress('172.30.1.48')"))
                    .anyRequest().authenticated()
            )
            .authenticationManager(authenticationManager)
            .sessionManagement((session) -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.addFilter(getAuthenticationFilter(authenticationManager));
        http.headers((headers) -> headers.frameOptions((frameOptions) -> frameOptions.sameOrigin()));

        return http.build();
    }

    private AuthenticationFilter getAuthenticationFilter(AuthenticationManager authenticationManager) throws Exception {

        AuthenticationFilter authenticationFilter = new AuthenticationFilter(authenticationManager, userService, env);
        authenticationFilter.setAuthenticationManager(authenticationManager);
        return authenticationFilter;

    }

}
