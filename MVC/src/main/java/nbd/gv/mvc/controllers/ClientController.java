package nbd.gv.mvc.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import jakarta.annotation.PostConstruct;

import jakarta.faces.view.ViewScoped;
import lombok.Getter;

import lombok.Setter;
import nbd.gv.mvc.model.Client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@ViewScoped
@Component(value = "clientController")
public class ClientController {
    Logger logger = LoggerFactory.getLogger(ClientController.class);
    private static final String appUrlClient = "http://localhost:8080/api/clients";
    @Getter
    private List<Client> listOfClients = new ArrayList<>();
    @Getter
    private Client client = new Client();
    @Getter
    private int statusCode = 0;


    @PostConstruct
    private void init() {
        readAllClients();
    }

    public void addClient() {
        String JSON = """
                {
                  "firstName": "%s",
                  "lastName": "%s",
                  "login": "%s",
                  "password": "%s"
                }
                """.formatted(client.getFirstName(), client.getLastName(), client.getLogin(), client.getPassword());
        RequestSpecification request = RestAssured.given();
        request.contentType("application/json");
        request.body(JSON);

        statusCode = 0;
        Response response = request.post(appUrlClient + "/addClient");
        statusCode = response.statusCode();

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
        RequestSpecification request = RestAssured.given();
        Response response = request.get(appUrlClient);

        //Alternative way
//        listOfCourts = new ArrayList<>(response.jsonPath().getList(".", Client.class));

        ObjectMapper objectMapper = new ObjectMapper();
        TypeReference<List<Client>> listType = new TypeReference<>() {};
        try {
            listOfClients = objectMapper.readValue(response.asString(), listType);
        } catch (Exception jpe) {
            logger.error(jpe.getMessage());
        }
    }
}
