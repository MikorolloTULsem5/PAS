package integrationtests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

import static integrationtests.CleaningClass.clean;
import static integrationtests.CleaningClass.initClients;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserControllerTests {

    @AfterAll
    static void cleanAtTheEnd() {
        clean();
    }

    @BeforeEach
    void cleanAndInitDatabase() {
        clean();
        initClients();
    }

    @Test
    void getAllClientsTest() throws URISyntaxException {
        RequestSpecification request = RestAssured.given();
        Response response = request.get(new URI("http://localhost:8080/CourtRent-1.0-SNAPSHOT/api/users"));
        String responseString = response.asString();

        //First Client
        assertTrue(responseString.contains("\"archive\":false"));
        assertTrue(responseString.contains("\"id\":\""));
        assertTrue(responseString.contains("\"login\":\"siemaszka\""));
        assertTrue(responseString.contains("\"clientTypeName\":\"normal\""));
        assertTrue(responseString.contains("\"firstName\":\"Adam\""));
        assertTrue(responseString.contains("\"lastName\":\"Smith\""));

        //Third Client
        assertTrue(responseString.contains("\"login\":\"michas13\""));
        assertTrue(responseString.contains("\"clientTypeName\":\"coach\""));
        assertTrue(responseString.contains("\"firstName\":\"Michal\""));
        assertTrue(responseString.contains("\"lastName\":\"Pi\""));

        assertEquals(200, response.getStatusCode());
    }

    @Test
    void getAllClientsTestNoCont() throws URISyntaxException {
        clean();
        RequestSpecification request = RestAssured.given();
        Response response = request.get(new URI("http://localhost:8080/CourtRent-1.0-SNAPSHOT/api/users"));
        String responseString = response.asString();

        assertTrue(responseString.isEmpty());
        assertEquals(204, response.getStatusCode());
    }

    @Test
    void createClientTestPos() throws URISyntaxException {
        clean();
        String JSON = """
                {
                  "firstName": "John",
                  "lastName": "Bravo",
                  "login": "johnBravo",
                  "clientTypeName": "normal"
                }
                """;
        RequestSpecification requestPost = RestAssured.given();
        requestPost.contentType("application/json");
        requestPost.body(JSON);

        RequestSpecification requestGet = RestAssured.given();
        String responseString = requestGet.get(new URI("http://localhost:8080/CourtRent-1.0-SNAPSHOT/api/users")).asString();

        assertFalse(responseString.contains("\"login\":\"johnBravo\""));
        assertFalse(responseString.contains("\"clientTypeName\":\"normal\""));
        assertFalse(responseString.contains("\"firstName\":\"John\""));
        assertFalse(responseString.contains("\"lastName\":\"Bravo\""));

        Response responsePost = requestPost.post("http://localhost:8080/CourtRent-1.0-SNAPSHOT/api/users/addClient");

        assertEquals(201, responsePost.getStatusCode());

        responseString = requestGet.get(new URI("http://localhost:8080/CourtRent-1.0-SNAPSHOT/api/users")).asString();

        assertTrue(responseString.contains("\"login\":\"johnBravo\""));
        assertTrue(responseString.contains("\"clientTypeName\":\"normal\""));
        assertTrue(responseString.contains("\"firstName\":\"John\""));
        assertTrue(responseString.contains("\"lastName\":\"Bravo\""));
    }

    @Test
    void createClientTestNegInvalidData() throws URISyntaxException {
        String json = """
                {
                  "firstName": "John",
                  "lastName": "  ",
                  "login": "johnBravo",
                  "clientTypeName": "normal"
                }
                """;
        RequestSpecification requestPost = RestAssured.given();
        requestPost.contentType("application/json");
        requestPost.body(json);

        RequestSpecification requestGet = RestAssured.given();
        String responseString = requestGet.get(new URI("http://localhost:8080/CourtRent-1.0-SNAPSHOT/api/users")).asString();

        assertFalse(responseString.contains("\"login\":\"johnBravo\""));
        assertFalse(responseString.contains("\"lastName\":\"  \""));

        Response responsePost = requestPost.post("http://localhost:8080/CourtRent-1.0-SNAPSHOT/api/users/addClient");

        assertEquals(400, responsePost.getStatusCode());

        responseString = requestGet.get(new URI("http://localhost:8080/CourtRent-1.0-SNAPSHOT/api/users")).asString();

        assertFalse(responseString.contains("\"login\":\"johnBravo\""));
        assertFalse(responseString.contains("\"lastName\":\"  \""));
    }

    @Test
    void createClientTestNegSameLogin() throws URISyntaxException {
        String json = """
                {
                  "firstName": "John",
                  "lastName": "Bravo",
                  "login": "michas13",
                  "clientTypeName": "normal"
                }
                """;
        RequestSpecification requestPost = RestAssured.given();
        requestPost.contentType("application/json");
        requestPost.body(json);

        RequestSpecification requestGet = RestAssured.given();
        String responseString = requestGet.get(new URI("http://localhost:8080/CourtRent-1.0-SNAPSHOT/api/users")).asString();

        assertTrue(responseString.contains("\"login\":\"michas13\""));

        assertFalse(responseString.contains("\"firstName\":\"John\""));
        assertFalse(responseString.contains("\"lastName\":\"Bravo\""));

        Response responsePost = requestPost.post("http://localhost:8080/CourtRent-1.0-SNAPSHOT/api/users/addClient");

        assertEquals(409, responsePost.getStatusCode());

        responseString = requestGet.get(new URI("http://localhost:8080/CourtRent-1.0-SNAPSHOT/api/users")).asString();

        assertFalse(responseString.contains("\"firstName\":\"John\""));
        assertFalse(responseString.contains("\"lastName\":\"Bravo\""));
    }

    @Test
    void getClientByLoginTest() throws URISyntaxException {
        RequestSpecification request = RestAssured.given();
        Response response = request.get(new URI("http://localhost:8080/CourtRent-1.0-SNAPSHOT/api/users/get?login=michas13"));
        String responseString = response.asString();

        System.out.println(responseString);
        assertTrue(responseString.contains("\"login\":\"michas13\",\"clientTypeName\":\"coach\",\"firstName\":\"Michal\",\"lastName\":\"Pi\""));

        assertEquals(200, response.getStatusCode());
    }

    @Test
    void getClientByLoginTestNoCont() throws URISyntaxException {
        RequestSpecification request = RestAssured.given();
        Response response = request.get(new URI("http://localhost:8080/CourtRent-1.0-SNAPSHOT/api/users/get?login=564545415612121121"));
        String responseString = response.asString();

        assertTrue(responseString.isEmpty());
        assertEquals(204, response.getStatusCode());
    }

    @Test
    void getClientByIdTest() throws URISyntaxException {
        RequestSpecification request = RestAssured.given();

        //Retrieve UUID
        String responseLogin = request.get(new URI("http://localhost:8080/CourtRent-1.0-SNAPSHOT/api/users/get?login=michas13")).asString();
        int index = responseLogin.indexOf("\"id\":\"") + 6;
        String clientId = responseLogin.substring(index, index + 36);

        Response responseById = request.get(new URI("http://localhost:8080/CourtRent-1.0-SNAPSHOT/api/users/" + clientId));
        String responseByIdString = responseById.asString();

        assertTrue(responseByIdString.contains("\"login\":\"michas13\",\"clientTypeName\":\"coach\",\"firstName\":\"Michal\",\"lastName\":\"Pi\""));

        assertEquals(200, responseById.getStatusCode());
    }

    @Test
    void getClientByIdTestNoCont() throws URISyntaxException {
        RequestSpecification request = RestAssured.given();
        Response response = request.get(new URI("http://localhost:8080/CourtRent-1.0-SNAPSHOT/api/users/" + UUID.randomUUID()));
        String responseString = response.asString();

        assertTrue(responseString.isEmpty());
        assertEquals(204, response.getStatusCode());
    }
}
