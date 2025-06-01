package br.com.fiap.Ayra.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Autowired
    private AuthFilter authFilter;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        return http
                    .authorizeHttpRequests(auth -> auth
                    .requestMatchers(HttpMethod.POST, "/login").permitAll()
                    .requestMatchers(HttpMethod.POST, "/users/**").permitAll()
                    // Rotas públicas
                    .requestMatchers(HttpMethod.GET, "/coordinates").permitAll()
                    .requestMatchers(HttpMethod.GET, "/coordinates/**").permitAll()
                    .requestMatchers( "/map-marker").permitAll()
                    .requestMatchers( "/map-marker/**").permitAll()
                    .requestMatchers( "/alert").permitAll()
                    .requestMatchers( "/alert/**").permitAll()
                    .requestMatchers( "/safe-routes").permitAll()
                    .requestMatchers( "/safe-location").permitAll()
                    .requestMatchers( "/safe-tip").permitAll()
                    .requestMatchers( "/safe-routes/**").permitAll()
                    .requestMatchers( "/safe-location/**").permitAll()
                    .requestMatchers( "/safe-tip/**").permitAll()
                    .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                    .anyRequest().authenticated()
                )
                .csrf(csrf -> csrf.disable())
                .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class)
                .httpBasic(Customizer.withDefaults())
                .build();
    }
    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authManager(AuthenticationConfiguration config) throws Exception{
        return config.getAuthenticationManager();
    }
    
}