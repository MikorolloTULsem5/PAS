package pas.gV.restapi.security.config;

import lombok.RequiredArgsConstructor;

import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import pas.gV.model.logic.users.User;
import pas.gV.restapi.data.mappers.AdminMapper;
import pas.gV.restapi.data.mappers.ClientMapper;
import pas.gV.restapi.data.mappers.ResourceAdminMapper;
import pas.gV.restapi.services.userservice.AdminService;
import pas.gV.restapi.services.userservice.ClientService;
import pas.gV.restapi.services.userservice.ResourceAdminService;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true)
public class AuthenticationConfig {

    private ClientService clientService;
    private AdminService adminService;
    private ResourceAdminService resourceAdminService;
    private PasswordEncoder passwordEncoder;

    @Bean
    public UserDetailsService userDetailsService() {
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
                User user = null;

                user = ClientMapper.fromJsonUser(clientService.getClientByLogin(login));
                if (user == null) {
                    user = AdminMapper.fromJsonUser(adminService.getAdminByLogin(login));
                }
                if (user == null) {
                    user = ResourceAdminMapper.fromJsonUser(resourceAdminService.getResourceAdminByLogin(login));
                }
                if (user == null) {
                    throw new UsernameNotFoundException("Brak uzytkownika o loginie \"%s\"!".formatted(login));
                }

                return user;
            }
        };
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
