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

import pas.gV.model.exceptions.UserException;
import pas.gV.model.exceptions.UserLoginException;

import pas.gV.restapi.data.dto.ClientDTO;
import pas.gV.restapi.services.userservice.ClientService;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/clients")
public class ClientController {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    private final ClientService clientService;

    @Autowired
    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @PostMapping("/addClient")
    public ResponseEntity<String> addClient(@RequestBody ClientDTO client) {
        Set<ConstraintViolation<ClientDTO>> violations = validator.validate(client);
        List<String> errors = violations.stream().map(ConstraintViolation::getMessage).toList();
        if (!violations.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors.toString());
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

    @PutMapping("/modifyClient/{id}")
    public ResponseEntity<String> modifyClient(@PathVariable("id") String id, @RequestBody ClientDTO modifiedClient) {
        Set<ConstraintViolation<ClientDTO>> violations = validator.validate(modifiedClient);
        List<String> errors = violations.stream().map(ConstraintViolation::getMessage).toList();
        if (!violations.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors.toString());
        }

        try {
            ClientDTO finalModifyClient = new ClientDTO(id, modifiedClient.getFirstName(),
                    modifiedClient.getLastName(), modifiedClient.getLogin(), modifiedClient.getPassword(), modifiedClient.isArchive(),
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
}

