package pas.gV.restapi.security.auth;

import com.mongodb.client.model.Filters;
import jakarta.validation.UnexpectedTypeException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pas.gV.model.data.repositories.UserMongoRepository;
import pas.gV.model.logic.users.Admin;
import pas.gV.model.logic.users.Client;
import pas.gV.model.logic.users.ResourceAdmin;
import pas.gV.model.logic.users.User;
import pas.gV.restapi.security.config.JwtService;
import pas.gV.restapi.services.userservice.ClientService;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class AuthenticationService {
  private final UserMongoRepository repository;
  private final ClientService clientService;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;

  public AuthenticationResponse register(RegisterRequest request) {
    Client clientReg = new Client(null,
            request.getFirstName(),
            request.getLastName(),
            request.getLogin(),
            passwordEncoder.encode(request.getPassword()),
            "normal");
    clientService.registerClient(clientReg.getFirstName(), clientReg.getLastName(), clientReg.getLogin(),
            clientReg.getPassword(), clientReg.getClientTypeName());

    String jwtToken = jwtService.generateToken(clientReg);
    return AuthenticationResponse.builder()
        .accessToken(jwtToken)
        .build();
  }

  //TODO kasowanie tokenu
  public AuthenticationResponse authenticate(AuthenticationRequest request) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            request.getLogin(),
            request.getPassword()
        )
    );

    User user = getUser(request.getLogin());
    if (user == null) {
      throw new UsernameNotFoundException("User not found");
    }

    String jwtToken = jwtService.generateToken(user);
    return AuthenticationResponse.builder()
        .accessToken(jwtToken)
        .build();
  }

  private User getUser(String login) {
    List<User> list = new ArrayList<>();
    try {
      list = repository.read(Filters.eq("login", login), Client.class);
    } catch (UnexpectedTypeException e) {
      try {
        list = repository.read(Filters.eq("login", login), Admin.class);
      } catch (UnexpectedTypeException e2) {
        list = repository.read(Filters.eq("login", login), ResourceAdmin.class);
      }
    }
    return !list.isEmpty() ? list.get(0) : null;
  }
}
