package com.pvncodes.MD.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Stateless JWT security.
 * - Profile endpoints need a valid token (USER or NGO).
 * - Account listing / lookup / deletion is ADMIN-only (no public access to other people's data).
 * - Donation / medicine / application endpoints stay open for now: the current frontend calls them
 *   without a token. Tighten them once the frontend attaches the Bearer header on every request.
 */
@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthFilter jwtAuthFilter) throws Exception {
        http
            .csrf(csrf -> csrf.disable())                 // stateless API, no cookies/sessions
            .cors(Customizer.withDefaults())              // uses the CORS rules in WebConfig
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(e -> e.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/users/register", "/api/users/login",
                                 "/api/ngos/register", "/api/ngos/login").permitAll()
                .requestMatchers("/api/users/profile").hasRole("USER")
                .requestMatchers("/api/ngos/profile").hasRole("NGO")
                .requestMatchers("/api/users", "/api/users/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/ngos/*").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/ngos/*").hasRole("ADMIN")
                .anyRequest().permitAll())
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
