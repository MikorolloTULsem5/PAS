package pas.gV.restapi.security.services;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Service;

import pas.gV.model.logic.users.Client;

import pas.gV.restapi.security.dto.AuthenticationRequest;
import pas.gV.restapi.security.dto.AuthenticationResponse;
import pas.gV.restapi.security.dto.RegisterRequest;

import pas.gV.restapi.services.userservice.ClientService;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true)
public class AuthenticationService {
    private ClientService clientService;
    private UserDetailsService userDetailsService;
    private PasswordEncoder passwordEncoder;
    private JwtService jwtService;
    private AuthenticationManager authenticationManager;

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
        return new AuthenticationResponse(jwtToken);
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getLogin(),
                        request.getPassword()
                )
        );

        String jwtToken = jwtService.generateToken(userDetailsService.loadUserByUsername(request.getLogin()));
        return new AuthenticationResponse(jwtToken);
    }

    //TODO kasowanie tokenu
}
