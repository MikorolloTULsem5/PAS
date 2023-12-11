package pas.gV.restapi.controllers.users;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
//import jakarta.ws.rs.*;
//import jakarta.ws.rs.core.Response;
import jakarta.websocket.server.PathParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/clients")
public class ClientController {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    private final ClientService clientService;

    @Autowired
    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

//    @PostMapping(path = "/addClient", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
//    public Response addClient(Client client) {
//        Set<ConstraintViolation<Client>> violations = validator.validate(client);
//        List<String> errors = violations.stream().map(ConstraintViolation::getMessage).collect(Collectors.toList());
//        if (!violations.isEmpty()) {
//            return Response.status(Response.Status.BAD_REQUEST).entity(errors).build();
//        }
//
//        try {
//            clientService.registerClient(client.getFirstName(), client.getLastName(),
//                    client.getLogin(), client.getClientTypeName());
//        } catch (UserLoginException ule) {
//            return Response.status(Response.Status.CONFLICT).entity(ule.getMessage()).build();
//        } catch (UserException ue) {
//            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ue.getMessage()).build();
//        }
//
//        return Response.status(Response.Status.CREATED).build();
//    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Client> getAllClients(HttpServletResponse response) {
        List<Client> resultList = clientService.getAllClients();
        if (resultList.isEmpty()) {
            resultList = null;
            response.setStatus(HttpStatus.NO_CONTENT.value());
        }
        return resultList;
    }

    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Client getClientById(@PathVariable("id") String id, HttpServletResponse response) {
        Client client = clientService.getClientById(UUID.fromString(id));
        if (client == null) {
            response.setStatus(HttpStatus.NO_CONTENT.value());
        }
        return client;
    }

    @GetMapping(path = "/get", produces = MediaType.APPLICATION_JSON_VALUE)
    public Client getClientByLogin(@RequestParam("login") String login, HttpServletResponse response) {
        Client client = clientService.getClientByLogin(login);
        if (client == null) {
            response.setStatus(HttpStatus.NO_CONTENT.value());
        }
        return client;
    }
//
//    @GET
//    @Produces(MediaType.APPLICATION_JSON)
//    @Consumes(MediaType.TEXT_PLAIN)
//    @Path("/match")
//    public List<Client> getClientByLoginMatching(@QueryParam("login") String login) {
//        List<Client> resultList = clientService.getClientByLoginMatching(login);
//        if (resultList.isEmpty()) {
//            resultList = null;
//        }
//        return resultList;
//    }
//
//    @PUT
//    @Produces(MediaType.APPLICATION_JSON)
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Path("/modifyClient/{id}")
//    public Response modifyClient(@PathParam("id") String id, Client modifiedClient) {
//        Set<ConstraintViolation<Client>> violations = validator.validate(modifiedClient);
//        List<String> errors = violations.stream().map(ConstraintViolation::getMessage).collect(Collectors.toList());
//        if (!violations.isEmpty()) {
//            return Response.status(Response.Status.BAD_REQUEST).entity(errors).build();
//        }
//
//        try {
//            Client finalModifyClient = new Client(UUID.fromString(id), modifiedClient.getFirstName(),
//                    modifiedClient.getLastName(), modifiedClient.getLogin(), modifiedClient.getClientTypeName());
//            finalModifyClient.setArchive(modifiedClient.isArchive());
//            clientService.modifyClient(finalModifyClient);
//        } catch (UserLoginException ule) {
//            return Response.status(Response.Status.CONFLICT).entity(ule.getMessage()).build();
//        } catch (UserException ue) {
//            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ue.getMessage()).build();
//        }
//
//        return Response.status(Response.Status.NO_CONTENT).build();
//    }
//
//    @POST
//    @Produces(MediaType.APPLICATION_JSON)
//    @Path("/activate/{id}")
//    public void activateClient(@PathParam("id") String id) {
//        clientService.activateClient(UUID.fromString(id));
//    }
//
//    @POST
//    @Produces(MediaType.APPLICATION_JSON)
//    @Path("/deactivate/{id}")
//    public void archiveClient(@PathParam("id") String id) {
//        clientService.deactivateClient(UUID.fromString(id));
//    }
}

