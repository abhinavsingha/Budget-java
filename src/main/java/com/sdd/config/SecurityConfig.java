package com.sdd.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.ArrayList;


@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http.cors().and()  // Enable CORS
                .csrf().disable()  // Disable CSRF if not needed
                 .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll()); // Allow all requests
        return http.build();
    }
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        try {
            ArrayList<String> origin=new ArrayList<>();
            origin.add("https://icg.net.in");
            //origin.add("*");
            configuration.setAllowedOrigins(origin); // Allow only this origin

            ArrayList<String> method=new ArrayList<>();
            method.add("GET");
            method.add("POST");
            method.add("PUT");
            method.add("DELETE");
            method.add("OPTIONS");
            configuration.setAllowedMethods(method);

            ArrayList<String> headers=new ArrayList<>();
            headers.add("*");
            configuration.setAllowedHeaders(headers);

            configuration.setAllowCredentials(true);
            UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
            source.registerCorsConfiguration("/**", configuration);
            return source;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
