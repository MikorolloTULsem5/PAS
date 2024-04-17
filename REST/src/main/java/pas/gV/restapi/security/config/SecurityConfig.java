package pas.gV.restapi.security.config;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.web.cors.CorsConfiguration;
import pas.gV.restapi.security.filters.JwtAuthenticationFilter;

import java.util.List;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
@FieldDefaults(makeFinal = true)
public class SecurityConfig {


    private JwtAuthenticationFilter jwtAuthFilter;
    private AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(req -> {
                            req
                                    .requestMatchers("/auth/**").permitAll()
                                    .requestMatchers(
                                            "/clients/get/me",
                                            "/clients/changePassword/me",
                                            "/clients/modifyClient/me",
                                            "/reservations/addReservation/me",
                                            "/reservations/clientReservation/me",
                                            "/reservations/returnCourt/me").hasAuthority("ROLE_CLIENT")
                                    .requestMatchers(
                                            "/courts",
                                            "/courts/get").hasAnyAuthority("ROLE_CLIENT", "ROLE_RESOURCE_ADMIN")
                                    .requestMatchers(
                                            "/resAdmins/get/me",
                                            "/resAdmins/changePassword/me",
                                            "/resAdmins/modifyResAdmin/me").hasAuthority("ROLE_RESOURCE_ADMIN")
                                    .requestMatchers("/clients").hasAnyAuthority("ROLE_ADMIN", "ROLE_RESOURCE_ADMIN")
                                    .requestMatchers("/clients/**", "/admins/**", "/resAdmins/**").hasAuthority("ROLE_ADMIN")
                                    .requestMatchers("/courts/**", "/reservations/**").hasAuthority("ROLE_RESOURCE_ADMIN")
                                    .anyRequest().denyAll();
//                                    .anyRequest().authenticated();
                        }
                )
                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
                .authenticationProvider(authenticationProvider)
                .cors(httpSecurityCorsConfigurer ->
                        httpSecurityCorsConfigurer.configurationSource(request -> {
                                    CorsConfiguration configuration = new CorsConfiguration().applyPermitDefaultValues();
                                    configuration.setAllowedMethods(List.of("GET", "HEAD", "POST", "PUT", "DELETE", "PATCH"));
                                    configuration.addExposedHeader("ETag");
                                    return configuration;
                                }

                        )
                )

        ;

        return http.build();
    }
}
