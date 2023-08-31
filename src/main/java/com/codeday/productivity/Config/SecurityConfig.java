package com.codeday.productivity.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Security configuration class for the application.
 * <p>
 * This class provides beans that configure security aspects such as password encoding.
 * </p>
 */
@Configuration
public class SecurityConfig {

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

}