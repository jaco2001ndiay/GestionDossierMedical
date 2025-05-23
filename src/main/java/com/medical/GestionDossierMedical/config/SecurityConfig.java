package com.medical.GestionDossierMedical.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    /// encodeur a creer pour les mots de pass
    @Bean
    public PasswordEncoder passwordEncoder()
    {
        return new BCryptPasswordEncoder();
    }
    ///  Creeons un AuthManager pour les login manuel
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception
    {
        return config.getAuthenticationManager() ;
    }
    /// Definissons les regles de securite
    @Bean
    public SecurityFilterChain filterchain(HttpSecurity http) throws  Exception
    {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll() // on le rend accessible sans login
                        .anyRequest().authenticated() // et on protege tout le reste
                ).formLogin(AbstractHttpConfigurer::disable) // desactive le formulaire lofin html
                .httpBasic(AbstractHttpConfigurer::disable) ; // desactive l'auth basic
        return http.build();
    }
}

