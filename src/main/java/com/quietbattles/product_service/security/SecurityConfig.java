
package com.quietbattles.product_service.security;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final InternalServiceAuthenticationFilter internalServiceAuthenticationFilter;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter,
            InternalServiceAuthenticationFilter internalServiceAuthenticationFilter) {

        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.internalServiceAuthenticationFilter = internalServiceAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
            .csrf(csrf -> csrf.disable())

            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            .authorizeHttpRequests(auth -> auth

                .requestMatchers("/actuator/health").permitAll()

                // Only trusted internal services can modify stock.
                .requestMatchers(
                    HttpMethod.POST,
                    "/api/products/*/reserve-stock",
                    "/api/products/*/release-stock"
                ).hasRole("SERVICE")

                // Customers and admins can view products.
                .requestMatchers(
                    HttpMethod.GET,
                    "/api/products",
                    "/api/products/**"
                ).hasAnyRole("USER", "ADMIN")

                // Only admins can create products.
                .requestMatchers(
                    HttpMethod.POST,
                    "/api/products"
                ).hasRole("ADMIN")

                // Only admins can update products.
                .requestMatchers(
                    HttpMethod.PUT,
                    "/api/products/**"
                ).hasRole("ADMIN")

                // Only admins can delete products.
                .requestMatchers(
                    HttpMethod.DELETE,
                    "/api/products/**"
                ).hasRole("ADMIN")

                .anyRequest().authenticated()
            )

            .exceptionHandling(exception -> exception
                .authenticationEntryPoint(
                    new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))

            // Internal service filter must run before JWT filter.
            .addFilterBefore(
            	    internalServiceAuthenticationFilter,
            	    UsernamePasswordAuthenticationFilter.class
            	)

            	.addFilterAfter(
            	    jwtAuthenticationFilter,
            	    UsernamePasswordAuthenticationFilter.class
            	);

        return http.build();
    }

    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter>
            jwtFilterRegistration(JwtAuthenticationFilter filter) {

        FilterRegistrationBean<JwtAuthenticationFilter> registration =
                new FilterRegistrationBean<>(filter);

        registration.setEnabled(false);
        return registration;
    }

    @Bean
    public FilterRegistrationBean<InternalServiceAuthenticationFilter>
            internalServiceFilterRegistration(
                    InternalServiceAuthenticationFilter filter) {

        FilterRegistrationBean<InternalServiceAuthenticationFilter> registration =
                new FilterRegistrationBean<>(filter);

        registration.setEnabled(false);
        return registration;
    }
}
