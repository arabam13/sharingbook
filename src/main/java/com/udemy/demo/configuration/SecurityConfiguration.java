package com.udemy.demo.configuration;

import com.udemy.demo.jwt.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@EnableWebSecurity
@Configuration
public class SecurityConfiguration {
    private static final Long MAX_AGE = 3600L;
    @Autowired
    JwtFilter jwtFilter;

    @Bean
    SecurityFilterChain web(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults()) // by default uses a Bean by the name of corsConfigurationSource
                .csrf().disable()
                .exceptionHandling()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers(HttpMethod.POST, "/users", "/authenticate").permitAll()
                        .requestMatchers(HttpMethod.GET, "/isConnected").permitAll()
                        .requestMatchers(HttpMethod.GET, "/").permitAll()
                        .requestMatchers(HttpMethod.GET,"/index.html").permitAll()
                        // .requestMatchers(HttpMethod.GET,"/favicon.ico").permitAll()
                        .requestMatchers(HttpMethod.GET, "/assets/**").permitAll()
                        // .requestMatchers("/v3/api-docs/**").permitAll()
                        // .requestMatchers("/api-docs.yaml").permitAll()
                        // .requestMatchers("/swagger-resources/**").permitAll()
                        // .requestMatchers("/swagger-ui/**").permitAll()
                        // .requestMatchers("/swagger-ui.html").permitAll()
                        // .requestMatchers("/webjars/**").permitAll()
                        // .requestMatchers("/v3/api-docs/swagger-config").permitAll()
                        .anyRequest().authenticated()

                );
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        // config.addAllowedOrigin("http://127.0.0.1:3000");
        config.addAllowedOrigin("https://araba-sharingbook.azurewebsites.net");
        config.setAllowedHeaders(Arrays.asList(
                HttpHeaders.AUTHORIZATION,
                HttpHeaders.CONTENT_TYPE,
                HttpHeaders.ACCEPT));
        config.setAllowedMethods(Arrays.asList(
                HttpMethod.GET.name(),
                HttpMethod.POST.name(),
                HttpMethod.PUT.name(),
                HttpMethod.DELETE.name()));
        config.setMaxAge(MAX_AGE);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}