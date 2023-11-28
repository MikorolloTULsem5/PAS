package nbd.gV.restapi.controllers;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import nbd.gV.model.users.Client;
import nbd.gV.restapi.services.userservice.ClientService;

import java.util.List;
import java.util.UUID;

@Path("/users")
@ApplicationScoped
public class UserController {
    @Inject
    private ClientService clientService;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/addClient/{typeName}")
    public void addClient(@PathParam("typeName") String type, Client client) {
        Client newClient = clientService.registerClient(client.getFirstName(), client.getLastName(),
                client.getLogin(), switch (type.toLowerCase()) {
                    default -> null;
                });
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Client> getAllClients() {
        return clientService.getAllClients();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.TEXT_PLAIN)
    @Path("/{id}")
    public Client getClientById(@PathParam("id") String id) {
        return clientService.getClientById(UUID.fromString(id));
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.TEXT_PLAIN)
    @Path("/get")
    public Client getClientByLogin(@QueryParam("login") String login) {
        return clientService.getClientByLogin(login);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.TEXT_PLAIN)
    @Path("/match")
    public List<Client> getClientByLoginMatching(@QueryParam("login") String login) {
        return clientService.getClientByLoginMatching(login);
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/modifyClient")
    public void modifyClient(Client modifiedClient) {
        clientService.modifyClient(modifiedClient);
    }
}
