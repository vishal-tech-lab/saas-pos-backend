package com.example.Backend.Configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.Backend.Security.JwtAuthenticationFilter;
import com.example.Backend.Security.JwtAuthEntryPoint;
import com.example.Backend.multitenancy.filter.TenantFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final Logger logger =
            LoggerFactory.getLogger(SecurityConfig.class);

    @Autowired
    private TenantFilter tenantFilter;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private JwtAuthEntryPoint jwtAuthEntryPoint;

    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config
    ) throws Exception {

        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http
    ) throws Exception {

        http

            // ✅ ENABLE CORS
            .cors(cors -> {})

            // ✅ DISABLE CSRF
            .csrf(csrf -> csrf.disable())

            // ✅ STATELESS JWT
            .sessionManagement(sm ->
                    sm.sessionCreationPolicy(
                            SessionCreationPolicy.STATELESS
                    )
            )

            // ✅ JWT ERROR HANDLER
            .exceptionHandling(ex ->
                    ex.authenticationEntryPoint(
                            jwtAuthEntryPoint
                    )
            )

            // ✅ ROUTE SECURITY
            .authorizeHttpRequests(auth -> auth

                // PUBLIC AUTH ROUTES
                .requestMatchers(
    "/auth/login",
    "/auth/signup",
    "/auth/refresh",
    "/tenant/create",
    "/tenant/onboard",
    "/tenant/login",
 "/tenant/by-subdomain/**", 
    "/ws",
    "/ws/**",

    "/customer-display/**"
).permitAll()

                // PUBLIC CUSTOMER ORDERING ROUTES
                .requestMatchers(
                        "/customer-menu/**",
                        "/customer-order/create",
                        "/customer-order/status/**"
                ).permitAll()

                // TABLE MANAGEMENT - ADMIN ONLY
                .requestMatchers(
                        "/table/**"
                ).hasAnyRole("ADMIN","MANAGER")

                // KITCHEN PROTECTED ROUTES
                .requestMatchers(
                        "/kitchen/**"
                ).hasAnyRole("ADMIN","KITCHEN","MANAGER")

                // ADMIN ONLY
                .requestMatchers(
                        "/products/**",
                        "/branches/**",
                        "/users/**",
                        "/stocktransfer/**"
                ).hasAnyRole("ADMIN","CASHIER","MANAGER", "KITCHEN")

                // ADMIN + CASHIER
                .requestMatchers(
                        "/salesitem/**"
                ).hasAnyRole(
                        "ADMIN",
                        "CASHIER","MANAGER", "KITCHEN"
                )
 .requestMatchers(
                        "/dashboard/**"
                ).hasAnyRole(
                        "ADMIN",
                        "CASHIER","MANAGER", "KITCHEN"
                )
                // ADMIN + KITCHEN
                .requestMatchers(
                        "/kitchenproduction/**"
                ).hasAnyRole(
                        "ADMIN",
                        "KITCHEN","CASHIER","MANAGER"
                )

                // PAYMENT - any authenticated user with a valid JWT
                .requestMatchers(
                        "payment/**"
                ).hasAnyRole(
                        "ADMIN",
                        "CASHIER","MANAGER"
                )
                
                // EVERYTHING ELSE
                .anyRequest().authenticated()
            )

            // DISABLE DEFAULT LOGIN
            .formLogin(form -> form.disable())

            // DISABLE BASIC AUTH
            .httpBasic(h -> h.disable())

            // ✅ TENANT FILTER FIRST
            .addFilterBefore(
        tenantFilter,
        UsernamePasswordAuthenticationFilter.class
)

.addFilterAfter(
        jwtAuthenticationFilter,
        TenantFilter.class
);

        logger.info("Security filter chain configured");
        return http.build();
    }
}
