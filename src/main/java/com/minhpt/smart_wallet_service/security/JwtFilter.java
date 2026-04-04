package com.minhpt.smart_wallet_service.security;

import com.minhpt.smart_wallet_service.constant.Constant;
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
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

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

            if (!jwtService.isValid(token)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("""
                        {
                          "status": 401,
                          "message": "Invalid or expired token"
                        }
                        """);
                return;
            }

            if (SecurityContextHolder.getContext().getAuthentication() == null) {

                String userId = jwtService.extractUserId(token);

                User user = userRepository.findByIdAndStatus(Long.valueOf(userId), Constant.NOT_DELETE)
                        .orElse(null);

                if (user == null) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");
                    response.getWriter().write("""
                            {
                              "status": 401,
                              "message": "User not found or inactive"
                            }
                            """);
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
