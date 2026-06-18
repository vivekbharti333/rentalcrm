package com.datfusrental.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
            .cors()
            .and()
            .csrf().disable()
            .authorizeRequests()

                // ✅ MUST allow preflight requests
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // ✅ PUBLIC URLs
                .antMatchers(
                    "/",
                    "/doLogin",
                    "/generateToken",
                    "/registerLead",
                    "/health",
                    "/public/**",
                    "/resources/**",
                    "/WEB-INF/views/**"
                ).permitAll()

                // 🔒 PROTECTED APIs
                .antMatchers("/api/**").authenticated()

                .anyRequest().permitAll()

            .and()
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // ✅ JWT FILTER
        http.addFilterBefore(
            jwtAuthenticationFilter,
            UsernamePasswordAuthenticationFilter.class
        );
    }

    // ✅ CORS CONFIGURATION (PRODUCTION SAFE)
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config = new CorsConfiguration();

        // REQUIRED when Authorization header / cookies are used
        config.setAllowCredentials(true);

        // ✅ DO NOT USE TRAILING SLASH
        config.setAllowedOriginPatterns(List.of(
            "http://localhost:*",
            "https://myrranrentals.work",
            "https://myraanrentals.com",
            "https://nautiamigo.com",
            "https://www.nautiamigo.com",
            "https://www.bookings.nautiamigo.com",
            "https://*.nautiamigo.com",
            "https://www.bookings.nautiamigo.com",
            "https://www.romeyourway.com",
            "https://romeyourway.com",
            "https://www.romeyourway.in",
            "https://romeyourway.in" 
        ));

        config.setAllowedMethods(List.of(
            "GET", "POST", "PUT", "DELETE", "OPTIONS"
        ));

        config.setAllowedHeaders(List.of(
            "Authorization",
            "Content-Type",
            "X-Requested-With",
            "Accept"
        ));

        UrlBasedCorsConfigurationSource source =
            new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
