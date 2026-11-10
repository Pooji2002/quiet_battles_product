
package com.quietbattles.product_service.security;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class InternalServiceAuthenticationFilter extends OncePerRequestFilter {

    private final String internalServiceKey;

    public InternalServiceAuthenticationFilter(
            @Value("${app.internal.service-key}") String internalServiceKey) {
        this.internalServiceKey = internalServiceKey;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();

        return !("POST".equalsIgnoreCase(request.getMethod())
                && (path.matches("/api/products/[^/]+/reserve-stock")
                || path.matches("/api/products/[^/]+/release-stock")));
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String providedKey = request.getHeader("X-INTERNAL-SERVICE-KEY");

        if (providedKey != null
                && MessageDigest.isEqual(
                        internalServiceKey.getBytes(StandardCharsets.UTF_8),
                        providedKey.getBytes(StandardCharsets.UTF_8))) {

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            "order-service",
                            null,
                            List.of(new SimpleGrantedAuthority("ROLE_SERVICE"))
                    );

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
}
