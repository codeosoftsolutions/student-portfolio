package com.studenttap.config;

import com.studenttap.security.JwtFilter;
import com.studenttap.security.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
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

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http)
            throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(
                corsConfigurationSource()))
            .sessionManagement(session -> session
                .sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS))
            .userDetailsService(userDetailsService)
            .authorizeHttpRequests(auth -> auth

                // ✅ Allow OPTIONS (CORS preflight)
                .requestMatchers(HttpMethod.OPTIONS, "/**")
                    .permitAll()

                // ✅ Allow /error page
                .requestMatchers("/error").permitAll()

                // ✅ ALL HTML pages - permit all
                // Security is handled by JavaScript
                // (JS checks token, redirects to login)
                .requestMatchers("/*.html").permitAll()
                .requestMatchers("/").permitAll()
                .requestMatchers("/index.html").permitAll()
                .requestMatchers("/login.html").permitAll()
                .requestMatchers("/register.html").permitAll()
                .requestMatchers("/dashboard.html").permitAll()
                
                // Updated
                .requestMatchers("/*").permitAll()

                // ✅ Static assets
                .requestMatchers("/assets/**").permitAll()
                .requestMatchers("/uploads/**").permitAll()
                .requestMatchers("/css/**").permitAll()
                .requestMatchers("/js/**").permitAll()
                .requestMatchers("/images/**").permitAll()
                
                
                .requestMatchers(
                	    "/api/auth/register",
                	    "/api/auth/login",
                	    "/api/auth/register-business",
                	    "/api/auth/login-business",
                	    "/api/auth/forgot-password",
                	    "/api/auth/verify-otp",
                	    "/api/auth/reset-password",
                	    "/api/public/**",
                	    "/api/admin/login"
                	).permitAll()

                // ✅ Public APIs - No token needed
                // These open when NFC card is tapped
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/public/**").permitAll()

                // 🔒 Protected APIs - Token required
                // JS sends token in header automatically
                .requestMatchers("/api/student/**")
                    .authenticated()

                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter,
                UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(Arrays.asList("*"));
        config.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(Arrays.asList("*"));
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source =
            new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}