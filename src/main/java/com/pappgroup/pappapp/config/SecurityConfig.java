package com.pappgroup.pappapp.config;

import com.pappgroup.pappapp.security.JwtAuthenticationEntryPoint;
import com.pappgroup.pappapp.security.JwtAuthenticationFilter;
import com.pappgroup.pappapp.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://127.0.0.1:3000"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .authorizeHttpRequests(auth -> auth
                        // Auth endpoint'leri - herkese açık
                        .requestMatchers("/api/auth/register").permitAll()
                        .requestMatchers("/api/auth/login").permitAll()
                        .requestMatchers("/api/auth/verify-email").permitAll()
                        .requestMatchers("/api/auth/resend-verification-code").permitAll()
                        .requestMatchers("/api/auth/refresh").permitAll()
                        .requestMatchers("/api/auth/logout").permitAll()

                        // Şifre sıfırlama endpoint'leri
                        .requestMatchers("/api/auth/forgot-password").permitAll()
                        .requestMatchers("/api/auth/reset-password").permitAll()
                        .requestMatchers("/api/auth/validate-reset-token").permitAll()

                        // OAuth2 endpoint'leri
                        .requestMatchers("/oauth2/**").permitAll()
                        .requestMatchers("/login/oauth2/**").permitAll()

                        // Property Public endpoint'ler - herkese açık
                        .requestMatchers("/api/properties/public/**").permitAll()

                        // Diğer Public endpoint'ler
                        .requestMatchers("/api/listings/public/**").permitAll()
                        .requestMatchers("/api/categories/public/**").permitAll()
                        .requestMatchers("/api/locations/public/**").permitAll()

                        // Test endpoint'leri (sadece development için)
                        .requestMatchers("/api/test/**").permitAll()

                        // Property Admin endpoint'leri
                        .requestMatchers("/api/properties/admin/**").hasRole("ADMIN")

                        // Property User endpoint'leri
                        .requestMatchers("/api/properties/user/**").hasAnyRole("USER", "ADMIN")

                        // Admin endpoint'leri
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // User endpoint'leri
                        .requestMatchers("/api/user/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/api/listings/user/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/api/listings/user/create").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/api/messages/**").hasAnyRole("USER", "ADMIN")

                        // Diğer tüm istekler kimlik doğrulaması gerektirir
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .redirectionEndpoint(redirect -> redirect
                                .baseUri("/login/oauth2/code/*")
                        )
                        .defaultSuccessUrl("/oauth2/callback/google", true)
                        .failureUrl("/oauth2/callback/google?error=true")
                )
                .exceptionHandling(ex -> ex.authenticationEntryPoint(jwtAuthenticationEntryPoint))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}