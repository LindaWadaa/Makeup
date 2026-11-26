package com.makeup.security;

import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;




@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Bean //bean utilisé pour configurer la sécurité de l'application pour gérer l'authentification et l'autorisation des utilisateurs
    SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authenticationManager,
            JwtService jwtService, JwtAuthorizationFilter jwtAuthorizationFilter) throws Exception {
        JwtAuthenticationFilter authenticationFilter = new JwtAuthenticationFilter(authenticationManager, jwtService);
        http.csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/login", "/register/**", "/verifyEmail/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/login", "/register", "/api/auth/register",
                                "/api/auth/verifyEmail", "/api/auth/resendCode").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/health").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/allmakeups").hasAnyAuthority("ADMIN", "USER")
                        .requestMatchers(HttpMethod.GET, "/api/getmakeup/**").hasAnyAuthority("ADMIN", "USER")
                        .requestMatchers(HttpMethod.GET, "/api/getmakeupbyname/**").hasAnyAuthority("ADMIN", "USER")
                        .requestMatchers(HttpMethod.GET, "/api/getmakeupbymarque/**").hasAnyAuthority("ADMIN", "USER")
                        .requestMatchers("/api/image/**").permitAll()
                        //.requestMatchers(HttpMethod.POST, "/api/addmakeup").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/updatemakeup").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/deletemakeup/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/marque/all").hasAnyAuthority("ADMIN", "USER")
                        .requestMatchers(HttpMethod.GET, "/api/marque/get/**").hasAnyAuthority("ADMIN", "USER")
                        .requestMatchers(HttpMethod.POST, "/api/marque/add").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/marque/update").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/marque/delete/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/makeup/**").hasAnyAuthority("ADMIN", "USER")
                        .requestMatchers(HttpMethod.POST, "/makeup/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/makeup/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/makeup/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/makeup/**").hasAuthority("ADMIN")
                        .anyRequest().authenticated())
                .exceptionHandling(exceptions -> exceptions.authenticationEntryPoint(
                        (request, response, exception) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED)))
                .addFilter(authenticationFilter)
                .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(
                "http://localhost:4200",
                "http://127.0.0.1:4200",
                "http://localhost:3000",
                "http://127.0.0.1:3000"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept"));
        configuration.setExposedHeaders(List.of("Authorization"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    FilterRegistrationBean<JwtAuthorizationFilter> disableContainerRegistration(
            JwtAuthorizationFilter jwtAuthorizationFilter) {
        FilterRegistrationBean<JwtAuthorizationFilter> registration = new FilterRegistrationBean<>(
                jwtAuthorizationFilter);
        registration.setEnabled(false);
        return registration;
    }
}
