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
import pas.gV.restapi.security.dto.TokenResponse;
import pas.gV.restapi.security.dto.ClientRegisterDTORequest;

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

    public TokenResponse register(ClientRegisterDTORequest requestDto) {
        Client clientReg = new Client(null,
                requestDto.getFirstName(),
                requestDto.getLastName(),
                requestDto.getLogin(),
                passwordEncoder.encode(requestDto.getPassword()),
                "normal");
        clientService.registerClient(clientReg.getFirstName(), clientReg.getLastName(), clientReg.getLogin(),
                clientReg.getPassword(), clientReg.getClientTypeName());

        String jwtToken = jwtService.generateToken(clientReg);
        return new TokenResponse(jwtToken);
    }

    public TokenResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getLogin(),
                        request.getPassword()
                )
        );

        String jwtToken = jwtService.generateToken(userDetailsService.loadUserByUsername(request.getLogin()));
        return new TokenResponse(jwtToken);
    }

    //TODO kasowanie tokenu
}
