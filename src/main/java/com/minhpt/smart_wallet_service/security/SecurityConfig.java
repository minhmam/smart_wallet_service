package com.minhpt.smart_wallet_service.security;

import com.minhpt.smart_wallet_service.constant.Constant;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtFilter jwtFilter;
    private final JwtAuthenticationEntryPoint authenticationEntryPoint;
    private final JwtAccessDeniedHandler accessDeniedHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/login", "/auth/register", "/auth/verify").permitAll()
                        .requestMatchers(
                                "/api/v1/payment-transaction/vnpay-ipn",
                                "/api/v1/payment-transaction/vnpay-return"
                        ).permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers(
                                "/api/v1/doashboard/count",
                                "/api/v1/doashboard/user-growth",
                                "/api/v1/doashboard/premium-revenue"
                        ).hasAuthority(Constant.ROLE_ADMIN)
                        .requestMatchers("/api/v1/admin/**").hasAuthority(Constant.ROLE_ADMIN)
                        .requestMatchers(HttpMethod.POST, "/api/v1/subscription-plan", "/api/v1/subscription-plan/search").hasAuthority(Constant.ROLE_ADMIN)
                        .requestMatchers(HttpMethod.PUT, "/api/v1/subscription-plan/**").hasAuthority(Constant.ROLE_ADMIN)
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/subscription-plan/**").hasAuthority(Constant.ROLE_ADMIN)
                        .anyRequest().hasAnyAuthority(Constant.ROLE_USER, Constant.ROLE_ADMIN)
                )

                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)

                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                );

        return http.build();
    }
}
