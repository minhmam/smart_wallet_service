package com.minhpt.smart_wallet_service.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minhpt.smart_wallet_service.common.ApiResponse;
import com.minhpt.smart_wallet_service.constant.Constant;
import com.minhpt.smart_wallet_service.i18n.MessageResolver;
import com.minhpt.smart_wallet_service.model.User;
import com.minhpt.smart_wallet_service.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    private final MessageResolver messageResolver;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith("/auth/")
                || path.equals("/api/v1/payment-transaction/vnpay-return")
                || path.equals("/api/v1/payment-transaction/vnpay-ipn")
                || path.startsWith("/swagger-ui/")
                || path.startsWith("/v3/api-docs/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        String token = resolveToken(header);

        if (token != null) {
            Locale requestLocale = messageResolver.resolveLocale(request);

            if (!jwtService.isValid(token)) {
                writeUnauthorizedResponse(
                        request,
                        response,
                        messageResolver.get(requestLocale, "security.token.invalid_or_expired")
                );
                return;
            }

            if (SecurityContextHolder.getContext().getAuthentication() == null) {

                String userId = jwtService.extractUserId(token);

                User user = userRepository.findByIdAndStatus(Long.valueOf(userId), Constant.NOT_DELETE)
                        .orElse(null);

                if (user == null) {
                    writeUnauthorizedResponse(
                            request,
                            response,
                            messageResolver.get(requestLocale, "security.user.not_found_or_inactive")
                    );
                    return;
                }

                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                user,
                                null,
                                user.getRoleNames().stream()
                                        .map(roleName -> new SimpleGrantedAuthority(Objects.requireNonNullElse(roleName, "ROLE_USER")))
                                        .toList()
                        );

                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        filterChain.doFilter(request, response);
    }

    private void writeUnauthorizedResponse(
            HttpServletRequest request,
            HttpServletResponse response,
            String message
    ) throws IOException {
        ApiResponse<Object> body = ApiResponse.builder()
                .status(HttpServletResponse.SC_UNAUTHORIZED)
                .message(message)
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .build();

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        objectMapper.writeValue(response.getWriter(), body);
    }

    private String resolveToken(String header) {
        if (header == null || header.isBlank()) {
            return null;
        }

        if (header.startsWith("Bearer ")) {
            String bearerToken = header.substring(7).trim();
            return bearerToken.isBlank() ? null : bearerToken;
        }

        String rawToken = header.trim();
        return rawToken.isBlank() ? null : rawToken;
    }
}
