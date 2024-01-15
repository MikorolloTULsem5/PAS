package nbd.gv.mvc.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import jakarta.annotation.PostConstruct;

import lombok.Getter;

import nbd.gv.mvc.model.Client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Scope(value = "session")
@Component(value = "clientController")
public class ClientController {
    Logger logger = LoggerFactory.getLogger(ClientController.class);
    private static final String appUrlClient = "http://localhost:8080/api/clients";
    @Getter
    private List<Client> listOfClients = new ArrayList<>();
    @Getter
    private Client client = new Client();
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
        System.out.println(JSON);
        Response response = request.post(appUrlClient + "/addClient");
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
