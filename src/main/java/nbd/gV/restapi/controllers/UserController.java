package nbd.gV.restapi.controllers;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import nbd.gV.model.users.Client;
import nbd.gV.restapi.services.userservice.ClientService;

import java.util.List;

@Path("/users")
@ApplicationScoped
public class UserController {
    @Inject
    private ClientService clientService;

    @GET
//    @Produces(MediaType.APPLICATION_JSON)
    public List<Client> getAllClients() {
        return clientService.getAllClients();
    }
}
