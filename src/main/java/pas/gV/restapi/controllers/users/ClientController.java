package pas.gV.restapi.controllers.users;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pas.gV.exceptions.UserException;
import pas.gV.exceptions.UserLoginException;
import pas.gV.model.users.Client;
import pas.gV.restapi.services.userservice.ClientService;

import java.util.List;
import java.util.Set;
import java.util.UUID;

///TODO moze zmiana jakarta na javax dla adnotacji?

///TODO sprawdz zasiegi
@RestController
@RequestMapping("/clients")
public class ClientController {
    ////TODO zmiana walidatora????
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    private final ClientService clientService;

    @Autowired
    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @PostMapping("/addClient")
    public ResponseEntity<String> addClient(@RequestBody Client client) {
        Set<ConstraintViolation<Client>> violations = validator.validate(client);
        List<String> errors = violations.stream().map(ConstraintViolation::getMessage).toList();
        if (!violations.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors.toString());
        }

        try {
            clientService.registerClient(client.getFirstName(), client.getLastName(),
                    client.getLogin(), client.getClientTypeName());
        } catch (UserLoginException ule) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ule.getMessage());
        } catch (UserException ue) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ue.getMessage());
        }

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public List<Client> getAllClients(HttpServletResponse response) {
        List<Client> resultList = clientService.getAllClients();
        if (resultList.isEmpty()) {
            resultList = null;
            response.setStatus(HttpStatus.NO_CONTENT.value());
        }
        return resultList;
    }

    @GetMapping("/{id}")
    public Client getClientById(@PathVariable("id") String id, HttpServletResponse response) {
        Client client = clientService.getClientById(UUID.fromString(id));
        if (client == null) {
            response.setStatus(HttpStatus.NO_CONTENT.value());
        }
        return client;
    }

    @GetMapping("/get")
    public Client getClientByLogin(@RequestParam("login") String login, HttpServletResponse response) {
        Client client = clientService.getClientByLogin(login);
        if (client == null) {
            response.setStatus(HttpStatus.NO_CONTENT.value());
        }
        return client;
    }

    @GetMapping("/match")
    public List<Client> getClientByLoginMatching(@RequestParam("login") String login, HttpServletResponse response) {
        List<Client> resultList = clientService.getClientByLoginMatching(login);
        if (resultList.isEmpty()) {
            resultList = null;
            response.setStatus(HttpStatus.NO_CONTENT.value());
        }
        return resultList;
    }

    @PutMapping("/modifyClient/{id}")
    public ResponseEntity<String> modifyClient(@PathVariable("id") String id, @RequestBody Client modifiedClient) {
        Set<ConstraintViolation<Client>> violations = validator.validate(modifiedClient);
        List<String> errors = violations.stream().map(ConstraintViolation::getMessage).toList();
        if (!violations.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors.toString());
        }

        try {
            Client finalModifyClient = new Client(UUID.fromString(id), modifiedClient.getFirstName(),
                    modifiedClient.getLastName(), modifiedClient.getLogin(), modifiedClient.getClientTypeName());
            finalModifyClient.setArchive(modifiedClient.isArchive());
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
        clientService.activateClient(UUID.fromString(id));
        response.setStatus(HttpStatus.NO_CONTENT.value());
    }

    @PostMapping("/deactivate/{id}")
    public void archiveClient(@PathVariable("id") String id, HttpServletResponse response) {
        clientService.deactivateClient(UUID.fromString(id));
        response.setStatus(HttpStatus.NO_CONTENT.value());
    }
}

