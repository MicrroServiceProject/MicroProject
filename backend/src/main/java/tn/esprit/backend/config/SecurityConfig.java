package tn.esprit.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        System.out.println("Applying SecurityConfig..."); // Add logging to confirm this method is called
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Enable CORS
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() // Allow all requests without authentication
                )
                .csrf(csrf -> csrf.disable()) // Disable CSRF for testing
                .formLogin(form -> form.disable()) // Disable form login to prevent redirects
                .httpBasic(httpBasic -> httpBasic.disable()) // Disable HTTP Basic auth to prevent redirects
                .logout(logout -> logout.disable()); // Disable logout to prevent redirects
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        System.out.println("Configuring CORS..."); // Add logging to confirm CORS configuration
        CorsConfiguration configuration = new CorsConfiguration();
        // Allow localhost:4200, localhost:55916, and localhost:57291
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200", "http://localhost:55916", "http://localhost:57291"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS")); // Allowed methods
        configuration.setAllowedHeaders(Arrays.asList("*")); // Allow all headers
        configuration.setAllowCredentials(true); // Allow cookies/credentials if necessary
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Apply to all endpoints
        return source;
    }
}