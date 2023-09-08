package com.codeday.productivity.Config;

import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import static org.springframework.security.config.Customizer.withDefaults;

/**
 * Security configuration class for the application.
 * <p>
 * This class provides beans that configure security aspects such as password encoding.
 * </p>
 */
@Configuration
public class SecurityConfig {
    private static final Logger logger = LogManager.getLogger(SecurityConfig.class);
    /**
     * Provides a BCrypt password encoder bean.
     * <p>
     * The bean will be used to encode and validate passwords in the application.
     * </p>
     *
     * @return A {@link BCryptPasswordEncoder} instance.
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .formLogin(withDefaults())
                .oauth2Login(withDefaults())
                .authorizeHttpRequests(c -> c.anyRequest().authenticated())
                .build();
    }

    @Bean
    ApplicationListener<AuthenticationSuccessEvent> successLogger() {
        return event -> logger.info("success: {}", event.getAuthentication());
    }
}