package pas.gV.restapi.controllers.users;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pas.gV.model.exceptions.UserException;
import pas.gV.model.exceptions.UserLoginException;

import pas.gV.restapi.data.dto.ClientDTO;
import pas.gV.restapi.data.dto.UserDTO.BasicUserValidation;
import pas.gV.restapi.data.dto.UserDTO.PasswordValidation;
import pas.gV.restapi.security.dto.ChangePasswordDTORequest;
import pas.gV.restapi.security.services.JwsService;
import pas.gV.restapi.services.userservice.ClientService;

import java.util.List;

@RestController
@RequestMapping("/clients")
public class ClientController {
    private final ClientService clientService;
    private final JwsService jwsService;

    @Autowired
    public ClientController(ClientService clientService, JwsService jwsService) {
        this.clientService = clientService;
        this.jwsService = jwsService;
    }

    @PostMapping("/addClient")
    public ResponseEntity<String> addClient(@Validated({BasicUserValidation.class, PasswordValidation.class}) @RequestBody ClientDTO client,
                                            Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(errors.getAllErrors()
                            .stream().map(ObjectError::getDefaultMessage)
                            .toList()
                            .toString()
                    );
        }

        try {
            clientService.registerClient(client.getFirstName(), client.getLastName(),
                    client.getLogin(), client.getPassword(), client.getClientType());
        } catch (UserLoginException ule) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ule.getMessage());
        } catch (UserException ue) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ue.getMessage());
        }

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public List<ClientDTO> getAllClients(HttpServletResponse response) {
        List<ClientDTO> resultList = clientService.getAllClients();
        if (resultList.isEmpty()) {
            resultList = null;
            response.setStatus(HttpStatus.NO_CONTENT.value());
        }
        return resultList;
    }

    @GetMapping("/{id}")
    public ClientDTO getClientById(@PathVariable("id") String id, HttpServletResponse response) {
        ClientDTO client = clientService.getClientById(id);
        if (client == null) {
            response.setStatus(HttpStatus.NO_CONTENT.value());
        }
        return client;
    }

    @GetMapping("/get")
    public ClientDTO getClientByLogin(@RequestParam("login") String login, HttpServletResponse response) {
        ClientDTO client = clientService.getClientByLogin(login);
        if (client == null) {
            response.setStatus(HttpStatus.NO_CONTENT.value());
        }
        return client;
    }

    @GetMapping("/match")
    public List<ClientDTO> getClientByLoginMatching(@RequestParam("login") String login, HttpServletResponse response) {
        List<ClientDTO> resultList = clientService.getClientByLoginMatching(login);
        if (resultList.isEmpty()) {
            resultList = null;
            response.setStatus(HttpStatus.NO_CONTENT.value());
        }
        return resultList;
    }

    @PutMapping("/modifyClient")
    public ResponseEntity<String> modifyClient(HttpServletRequest httpServletRequest,
                                               @Validated(BasicUserValidation.class) @RequestBody ClientDTO modifiedClient,
                                               Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(errors.getAllErrors()
                            .stream().map(ObjectError::getDefaultMessage)
                            .toList()
                            .toString()
                    );
        }

        try {
            ClientDTO finalModifyClient = new ClientDTO(modifiedClient.getId(), modifiedClient.getFirstName(),
                    modifiedClient.getLastName(), modifiedClient.getLogin(), null, modifiedClient.isArchive(),
                    modifiedClient.getClientType());

            clientService.modifyClient(finalModifyClient);

        } catch (UserLoginException ule) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ule.getMessage());
        } catch (UserException ue) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ue.getMessage());
        }

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/activate/{id}")
    public void activateClient(@PathVariable("id") String id, HttpServletResponse response) {
        clientService.activateClient(id);
        response.setStatus(HttpStatus.NO_CONTENT.value());
    }

    @PostMapping("/deactivate/{id}")
    public void archiveClient(@PathVariable("id") String id, HttpServletResponse response) {
        clientService.deactivateClient(id);
        response.setStatus(HttpStatus.NO_CONTENT.value());
    }

    @PatchMapping("/changePassword/{id}")
    public ResponseEntity<String> changeClientPassword(@PathVariable("id") String id,
                                                       @Validated(PasswordValidation.class) @RequestBody ChangePasswordDTORequest body,
                                                       Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(errors.getAllErrors()
                            .stream().map(ObjectError::getDefaultMessage)
                            .toList()
                            .toString()
                    );
        }

        try {
            clientService.changeClientPassword(id, body);
        } catch (IllegalStateException ise) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ise.getMessage());
        }

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /*---------------------------------------FOR CLIENT-------------------------------------------------------------*/
    @GetMapping("/get/me")
    public ClientDTO getClientByLogin(HttpServletResponse response) {
        ClientDTO client = clientService.getClientByLogin(SecurityContextHolder.getContext().getAuthentication().getName());
        if (client == null) {
            response.setStatus(HttpStatus.NO_CONTENT.value());
            return null;
        }
        String etag = jwsService.generateSignatureForClient(client);
        response.setHeader(HttpHeaders.ETAG, etag);
        return client;
    }

    @PutMapping("/modifyClient/me")
    public ResponseEntity<String> modifyClient(@RequestHeader(value = HttpHeaders.IF_MATCH) String ifMatch,
                                               @Validated(BasicUserValidation.class) @RequestBody ClientDTO modifiedClient,
                                               Errors errors) {

        if (ifMatch == null || ifMatch.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Brak naglowka IF-MATCH!");
        }

        if (!SecurityContextHolder.getContext().getAuthentication().getName().equals(modifiedClient.getLogin())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Nie mozna edytowac danych innego uzytkownika!");
        }

        if (errors.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(errors.getAllErrors()
                            .stream().map(ObjectError::getDefaultMessage)
                            .toList()
                            .toString()
                    );
        }

        try {
            ClientDTO finalModifyClient = new ClientDTO(modifiedClient.getId(), modifiedClient.getFirstName(),
                    modifiedClient.getLastName(), modifiedClient.getLogin(), null, modifiedClient.isArchive(),
                    modifiedClient.getClientType());
            if (jwsService.verifyClientSignature(ifMatch, finalModifyClient)) {
                clientService.modifyClient(finalModifyClient);
            } else {
                throw new UserException("Proba zmiany niedozwolonego pola!");
            }

        } catch (UserLoginException ule) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ule.getMessage());
        } catch (UserException ue) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ue.getMessage());
        }

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PatchMapping("/changePassword/me")
    public ResponseEntity<String> changeClientPassword(@Validated(PasswordValidation.class) @RequestBody ChangePasswordDTORequest body,
                                                       Errors errors) {
        ClientDTO client = clientService.getClientByLogin(SecurityContextHolder.getContext().getAuthentication().getName());
        if (errors.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(errors.getAllErrors()
                            .stream().map(ObjectError::getDefaultMessage)
                            .toList()
                            .toString()
                    );
        }

        try {
            clientService.changeClientPassword(client.getId(), body);
        } catch (IllegalStateException ise) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ise.getMessage());
        }

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}

