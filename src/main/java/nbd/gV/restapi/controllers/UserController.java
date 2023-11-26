package nbd.gV.restapi.controllers;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import nbd.gV.model.users.Client;
import nbd.gV.model.users.clienttype.Athlete;
import nbd.gV.model.users.clienttype.Coach;
import nbd.gV.model.users.clienttype.Normal;
import nbd.gV.restapi.services.userservice.ClientService;

import java.util.List;

@Path("/users")
@ApplicationScoped
public class UserController {
    @Inject
    private ClientService clientService;

    @POST
    @Path("/addClient/{typeName}")
    public void addClient(@PathParam("typeName") String type, Client client) {
        Client newClient = clientService.registerClient(client.getFirstName(), client.getLastName(),
                client.getLogin(), switch (type.toLowerCase()) {
                    case "normal" -> new Normal();
                    case "athlete" -> new Athlete();
                    case "coach" -> new Coach();
                    default -> null;
                });
    }

    @GET
    public List<Client> getAllClients() {
        return clientService.getAllClients();
    }
}
