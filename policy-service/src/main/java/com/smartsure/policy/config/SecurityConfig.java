package com.smartsure.policy.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import java.io.IOException;
import java.util.Collections;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.csrf(csrf -> csrf.disable());

        http.addFilterBefore(headerAuthenticationFilter(),
                org.springframework.security.web.authentication.
                        preauth.AbstractPreAuthenticatedProcessingFilter.class);

        http.authorizeHttpRequests(auth -> auth.anyRequest().authenticated());

        return http.build();
    }

    @Bean
    public Filter headerAuthenticationFilter() {
        return (request, response, chain) -> {

            HttpServletRequest httpRequest = (HttpServletRequest) request;

            String role = httpRequest.getHeader("X-User-Role");
            String email = httpRequest.getHeader("X-User-Email");
            String userId = httpRequest.getHeader("X-User-Id");

            if (role != null && email != null) {

                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                userId, // ✅ store userId here
                                null,
                                Collections.singletonList(
                                        new SimpleGrantedAuthority("ROLE_" + role)
                                )
                        );

                org.springframework.security.core.context.SecurityContextHolder
                        .getContext()
                        .setAuthentication(auth);
            }

            chain.doFilter(request, response);
        };
    }
}