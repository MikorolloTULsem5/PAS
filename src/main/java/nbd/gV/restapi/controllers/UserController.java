package nbd.gV.restapi.controllers;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import nbd.gV.model.users.Client;
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
//    @Produces(MediaType.APPLICATION_JSON)
//    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/addClient")
    public void addClient(Client client) {
        Client newClient = clientService.registerClient(client.getFirstName(), client.getLastName(),
                client.getLogin(), new Coach());
    }

    @GET
//    @Produces(MediaType.APPLICATION_JSON)
    public List<Client> getAllClients() {
        return clientService.getAllClients();
    }
}
