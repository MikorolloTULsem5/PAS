package nbd.gv.mvc.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import jakarta.annotation.PostConstruct;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import lombok.Getter;

import nbd.gv.mvc.model.Client;

import nbd.gv.mvc.services.ClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@ViewScoped
@Component(value = "clientController")
public class ClientController {
    Logger logger = LoggerFactory.getLogger(ClientController.class);
    @Getter
    private List<Client> listOfClients = new ArrayList<>();
    @Getter
    private Client client = new Client();

    private final ClientService clientService;

    @Inject
    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @PostConstruct
    private void init() {
       readAllClients();
    }

    public void addClient() {
        int statusCode = clientService.addClient(client.getFirstName(), client.getLastName(), client.getLogin(),
                client.getPassword());

        String message;
        if (statusCode == 201) {
            message = "Client successfully registered with login: " + client.getLogin();
            logger.info(message);
            MessageView.info(message);
        } else if (statusCode == 409) {
            message = "Client not registered; This login already exists!";
            logger.warn(message);
            MessageView.warn(message);
        } else {
            message = "Cannot to register a client";
            logger.warn(message + "; Returned HTTP code: " + statusCode);
            MessageView.warn(message);
        }
    }

    public void readAllClients() {
        listOfClients = clientService.readAllClients();
    }
}
