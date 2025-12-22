package com.loginAuth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    /**
     * Configures application security rules.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            // Disable CSRF for form-based authentication without Spring Security login
            .csrf(csrf -> csrf.disable())

            // Configure authorization rules
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/login",
                    "/home",
                    "/register",
                    "/forgot-password",
                    "/reset-password",
                    "/css/**",
                    "/js/**",
                    "/oauth2/**"
                ).permitAll()
                .anyRequest().authenticated()
            ).oauth2Login(oauth -> oauth
                 .loginPage("/login")
                 .defaultSuccessUrl("/home", true)
            )
             .logout(logout -> logout
             .logoutSuccessUrl("/login")
            );

        return http.build();
    }
}
