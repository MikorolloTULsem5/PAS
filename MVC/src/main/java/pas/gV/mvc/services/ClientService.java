package pas.gV.mvc.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import pas.gV.mvc.model.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ClientService {
    Logger logger = LoggerFactory.getLogger(ClientService.class);
    private static final String appUrlClient = "http://localhost:8080/api/clients";

    public int addClient(String firstName, String lastName, String login, String password) {
        String JSON = """
                {
                  "firstName": "%s",
                  "lastName": "%s",
                  "login": "%s",
                  "password": "%s"
                }
                """.formatted(firstName, lastName, login, password);
        RequestSpecification request = RestAssured.given();
        request.contentType("application/json");
        request.body(JSON);

        Response response = request.post(appUrlClient + "/addClient");
        return response.statusCode();
    }

    public List<Client> readAllClients() {
        RequestSpecification request = RestAssured.given();
        Response response = request.get(appUrlClient);

        //Alternative way
//        listOfCourts = new ArrayList<>(response.jsonPath().getList(".", Client.class));

        ObjectMapper objectMapper = new ObjectMapper();
        TypeReference<List<Client>> listType = new TypeReference<>() {};
        try {
            return objectMapper.readValue(response.asString(), listType);
        } catch (Exception jpe) {
            logger.error(jpe.getMessage());
            return new ArrayList<>();
        }
    }
}
