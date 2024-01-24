package pas.gV.restapi.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .cors(httpSecurityCorsConfigurer ->
                httpSecurityCorsConfigurer.configurationSource(request -> {
                    CorsConfiguration configuration = new CorsConfiguration().applyPermitDefaultValues();
                    configuration.setAllowedMethods(List.of("GET", "HEAD", "POST", "PUT", "DELETE", "PATCH"));
                    return configuration;
                }

                )
        );
        return http.build();
    }
}
