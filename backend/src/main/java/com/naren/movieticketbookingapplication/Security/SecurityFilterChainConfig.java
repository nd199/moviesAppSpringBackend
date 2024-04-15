package com.naren.movieticketbookingapplication.Security;

import com.naren.movieticketbookingapplication.jwt.JwtAuthFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
@EnableWebSecurity
@Configuration
public class SecurityFilterChainConfig {

    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthFilter authFilter;
    private final AuthenticationEntryPoint authenticationEntryPoint;

    public SecurityFilterChainConfig(AuthenticationProvider authenticationProvider, JwtAuthFilter authFilter,
                                     AuthenticationEntryPoint authenticationEntryPoint) {
        this.authenticationProvider = authenticationProvider;
        this.authFilter = authFilter;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    @Bean
    public SecurityFilterChain customSecurityFilterChain(HttpSecurity httpSecurity) throws Exception {
        log.info("Configuring custom Security Filter Chain...");
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth ->
                        auth
                                .requestMatchers(HttpMethod.POST, "/api/v1/roles").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/v1/roles").permitAll()
                                .requestMatchers(HttpMethod.DELETE, "/api/v1/roles/{id}").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/v1/customers", "/api/v1/movies", "api/v1/admins").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/v1/movies", "api/v1/movies/{id}").permitAll()
                                .requestMatchers(HttpMethod.PUT, "/api/v1/movies/{id}").permitAll()
                                .requestMatchers(HttpMethod.DELETE, "/api/v1/movies/{id}").permitAll()
                                .anyRequest().authenticated()
                )
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(
                        handler -> handler.authenticationEntryPoint(authenticationEntryPoint)
                );
        log.info("Custom Security Filter Chain configured successfully.");
        return httpSecurity.build();
    }
}
