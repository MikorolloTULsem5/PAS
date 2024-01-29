package pas.gV.restapi.security.config;

import com.mongodb.client.model.Filters;
import jakarta.validation.UnexpectedTypeException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import pas.gV.model.data.repositories.UserMongoRepository;
import pas.gV.model.logic.users.Admin;
import pas.gV.model.logic.users.Client;
import pas.gV.model.logic.users.ResourceAdmin;
import pas.gV.model.logic.users.User;

import java.util.ArrayList;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

  private final UserMongoRepository userRepository;

//  @Bean
//  public UserDetailsService userDetailsService() {
//    return username -> repository.read(username)
//        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
//  }

  @Bean
  public UserDetailsService userDetailsService() {
    return new UserDetailsService() {
      @Override
      public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<User> list = new ArrayList<>();
        try {
          list = userRepository.read(Filters.eq("login", username), Client.class);
        } catch (UnexpectedTypeException e) {
          try {
            list = userRepository.read(Filters.eq("login", username), Admin.class);
          } catch (UnexpectedTypeException e2) {
            list = userRepository.read(Filters.eq("login", username), ResourceAdmin.class);
          }
        }

        if (list.isEmpty()) {
          throw new UsernameNotFoundException("User not found");
        }

        return list.get(0);
        ///TODO do poprawy userType xd
      }
    };
  }

  @Bean
  public AuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    authProvider.setUserDetailsService(userDetailsService());
    authProvider.setPasswordEncoder(passwordEncoder());
    return authProvider;
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
    return config.getAuthenticationManager();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

}
