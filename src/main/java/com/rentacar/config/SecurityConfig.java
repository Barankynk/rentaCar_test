package com.rentacar.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security Konfigürasyonu
 * - Public sayfalar: Ana sayfa, araçlar, randevu formu
 * - Korumalı sayfalar: Admin panel (Basic Auth)
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // Public sayfalar
                        .requestMatchers("/", "/vehicles/**", "/appointments/**", "/error", "/css/**", "/js/**",
                                "/images/**", "/favicon.ico")
                        .permitAll()
                        // H2 Console (development)
                        .requestMatchers("/h2-console/**").permitAll()
                        // Admin sayfaları
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        // Diğer tüm istekler authenticated
                        .anyRequest().authenticated())
                // HTTP Basic Auth (Admin için)
                .httpBasic(basic -> {
                })
                // CSRF kapatma (test kolaylığı için)
                .csrf(csrf -> csrf.disable())
                // H2 Console için frame options
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails admin = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin123"))
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(admin);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
