package integrationtests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URISyntaxException;

import static integrationtests.CleaningClass.clean;
import static integrationtests.CleaningClass.initCourts;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CourtControllerTests {

    @AfterAll
    static void cleanAtTheEnd() {
        clean();
    }

    @BeforeEach
    void cleanAndInitDatabase() {
        clean();
        initCourts();
    }

    @Test
    void getAllCourtsTest() throws URISyntaxException {
        RequestSpecification request = RestAssured.given();
        Response response = request.get(new URI("http://localhost:8080/CourtRent-1.0-SNAPSHOT/api/courts"));
        String responseString = response.asString();
        String[] splitedRespStr = responseString.split("},");

        assertEquals(3, splitedRespStr.length);

        //First Court
        assertTrue(splitedRespStr[0].contains("\"archive\":false"));
        assertTrue(splitedRespStr[0].contains("\"area\":100"));
        assertTrue(splitedRespStr[0].contains("\"baseCost\":100"));
        assertTrue(splitedRespStr[0].contains("\"courtId\":\""));
        assertTrue(splitedRespStr[0].contains("\"courtNumber\":1"));
        assertTrue(splitedRespStr[0].contains("\"rented\":false"));

        //Third Court
        assertTrue(splitedRespStr[2].contains("\"archive\":false"));
        assertTrue(splitedRespStr[2].contains("\"area\":300"));
        assertTrue(splitedRespStr[2].contains("\"baseCost\":200"));
        assertTrue(splitedRespStr[2].contains("\"courtId\":\""));
        assertTrue(splitedRespStr[2].contains("\"courtNumber\":3"));
        assertTrue(splitedRespStr[2].contains("\"rented\":false"));

        assertEquals(200, response.getStatusCode());
    }

    @Test
    void getAllCourtsTestNoCont() throws URISyntaxException {
        clean();
        RequestSpecification request = RestAssured.given();
        Response response = request.get(new URI("http://localhost:8080/CourtRent-1.0-SNAPSHOT/api/courts"));
        String responseString = response.asString();

        assertTrue(responseString.isEmpty());
        assertEquals(204, response.getStatusCode());
    }

    @Test
    void createCourtTestPos() throws URISyntaxException {
        clean();
        String JSON = """
                {
                  "area": 120.0,
                  "baseCost": 50,
                  "courtNumber": 15
                }
                """;
        RequestSpecification requestPost = RestAssured.given();
        requestPost.contentType("application/json");
        requestPost.body(JSON);

        RequestSpecification requestGet = RestAssured.given();
        String responseString = requestGet.get(new URI("http://localhost:8080/CourtRent-1.0-SNAPSHOT/api/courts")).asString();

        assertTrue(responseString.isEmpty());

        Response responsePost = requestPost.post("http://localhost:8080/CourtRent-1.0-SNAPSHOT/api/courts/addCourt");

        assertEquals(201, responsePost.getStatusCode());

        responseString = requestGet.get(new URI("http://localhost:8080/CourtRent-1.0-SNAPSHOT/api/courts")).asString();

        assertTrue(responseString.contains("\"archive\":false"));
        assertTrue(responseString.contains("\"area\":120"));
        assertTrue(responseString.contains("\"baseCost\":50"));
        assertTrue(responseString.contains("\"courtId\":\""));
        assertTrue(responseString.contains("\"courtNumber\":15"));
        assertTrue(responseString.contains("\"rented\":false"));
    }
//
//    @Test
//    void createClientTestNegInvalidData() throws URISyntaxException {
//        String json = """
//                {
//                  "firstName": "John",
//                  "lastName": "  ",
//                  "login": "johnBravo",
//                  "clientTypeName": "normal"
//                }
//                """;
//        RequestSpecification requestPost = RestAssured.given();
//        requestPost.contentType("application/json");
//        requestPost.body(json);
//
//        RequestSpecification requestGet = RestAssured.given();
//        String responseString = requestGet.get(new URI("http://localhost:8080/CourtRent-1.0-SNAPSHOT/api/users")).asString();
//
//        assertFalse(responseString.contains("\"login\":\"johnBravo\""));
//        assertFalse(responseString.contains("\"lastName\":\"  \""));
//
//        Response responsePost = requestPost.post("http://localhost:8080/CourtRent-1.0-SNAPSHOT/api/users/addClient");
//
//        assertEquals(400, responsePost.getStatusCode());
//
//        responseString = requestGet.get(new URI("http://localhost:8080/CourtRent-1.0-SNAPSHOT/api/users")).asString();
//
//        assertFalse(responseString.contains("\"login\":\"johnBravo\""));
//        assertFalse(responseString.contains("\"lastName\":\"  \""));
//    }
//
//    @Test
//    void createClientTestNegSameLogin() throws URISyntaxException {
//        String json = """
//                {
//                  "firstName": "John",
//                  "lastName": "Bravo",
//                  "login": "michas13",
//                  "clientTypeName": "normal"
//                }
//                """;
//        RequestSpecification requestPost = RestAssured.given();
//        requestPost.contentType("application/json");
//        requestPost.body(json);
//
//        RequestSpecification requestGet = RestAssured.given();
//        String responseString = requestGet.get(new URI("http://localhost:8080/CourtRent-1.0-SNAPSHOT/api/users")).asString();
//
//        assertTrue(responseString.contains("\"login\":\"michas13\""));
//
//        assertFalse(responseString.contains("\"firstName\":\"John\""));
//        assertFalse(responseString.contains("\"lastName\":\"Bravo\""));
//
//        Response responsePost = requestPost.post("http://localhost:8080/CourtRent-1.0-SNAPSHOT/api/users/addClient");
//
//        assertEquals(409, responsePost.getStatusCode());
//
//        responseString = requestGet.get(new URI("http://localhost:8080/CourtRent-1.0-SNAPSHOT/api/users")).asString();
//
//        assertFalse(responseString.contains("\"firstName\":\"John\""));
//        assertFalse(responseString.contains("\"lastName\":\"Bravo\""));
//    }
//
//    @Test
//    void getClientByLoginTest() throws URISyntaxException {
//        RequestSpecification request = RestAssured.given();
//        Response response = request.get(new URI("http://localhost:8080/CourtRent-1.0-SNAPSHOT/api/users/get?login=michas13"));
//        String responseString = response.asString();
//
//        System.out.println(responseString);
//        assertTrue(responseString.contains("\"login\":\"michas13\",\"clientTypeName\":\"coach\",\"firstName\":\"Michal\",\"lastName\":\"Pi\""));
//
//        assertEquals(200, response.getStatusCode());
//    }
//
//    @Test
//    void getClientByLoginTestNoCont() throws URISyntaxException {
//        RequestSpecification request = RestAssured.given();
//        Response response = request.get(new URI("http://localhost:8080/CourtRent-1.0-SNAPSHOT/api/users/get?login=564545415612121121"));
//        String responseString = response.asString();
//
//        assertTrue(responseString.isEmpty());
//        assertEquals(204, response.getStatusCode());
//    }
//
//    @Test
//    void getClientByIdTest() throws URISyntaxException {
//        RequestSpecification request = RestAssured.given();
//
//        //Retrieve UUID
//        String responseLogin = request.get(new URI("http://localhost:8080/CourtRent-1.0-SNAPSHOT/api/users/get?login=michas13")).asString();
//        int index = responseLogin.indexOf("\"id\":\"") + 6;
//        String clientId = responseLogin.substring(index, index + 36);
//
//        Response responseById = request.get(new URI("http://localhost:8080/CourtRent-1.0-SNAPSHOT/api/users/" + clientId));
//        String responseByIdString = responseById.asString();
//
//        assertTrue(responseByIdString.contains("\"login\":\"michas13\",\"clientTypeName\":\"coach\",\"firstName\":\"Michal\",\"lastName\":\"Pi\""));
//
//        assertEquals(200, responseById.getStatusCode());
//    }
//
//    @Test
//    void getClientByIdTestNoCont() throws URISyntaxException {
//        RequestSpecification request = RestAssured.given();
//        Response response = request.get(new URI("http://localhost:8080/CourtRent-1.0-SNAPSHOT/api/users/" + UUID.randomUUID()));
//        String responseString = response.asString();
//
//        assertTrue(responseString.isEmpty());
//        assertEquals(204, response.getStatusCode());
//    }
//
//    @Test
//    void getClientByLoginMatchingPos() throws URISyntaxException {
//        RequestSpecification request = RestAssured.given();
//        Response response = request.get(new URI("http://localhost:8080/CourtRent-1.0-SNAPSHOT/api/users/match?login=login"));
//        String responseString = response.asString();
//
//        String[] splitedRespStr = responseString.split("},");
//
//        assertEquals(2, splitedRespStr.length);
//
//        //First Client
//        assertTrue(splitedRespStr[0].contains(
//                "\"login\":\"loginek\"," +
//                "\"clientTypeName\":\"normal\"," +
//                "\"firstName\":\"Adam\"," +
//                "\"lastName\":\"Smith\""));
//
//        //Second Client
//        assertTrue(splitedRespStr[1].contains(
//                "\"login\":\"loginek13\"," +
//                "\"clientTypeName\":\"athlete\"," +
//                "\"firstName\":\"Eva\"," +
//                "\"lastName\":\"Braun\""));
//
//        assertEquals(200, response.getStatusCode());
//    }
//
//    @Test
//    void getClientByLoginMatchingNoCont() throws URISyntaxException {
//        RequestSpecification request = RestAssured.given();
//        Response response = request.get(new URI("http://localhost:8080/CourtRent-1.0-SNAPSHOT/api/users/match?login=uwuwuuwuwu"));
//        String responseString = response.asString();
//
//        assertTrue(responseString.isEmpty());
//
//        assertEquals(204, response.getStatusCode());
//    }
//
//    @Test
//    void modifyClientTest() throws URISyntaxException {
//        String JSON = """
//                {
//                  "archive": true,
//                  "firstName": "John",
//                  "lastName": "Smith",
//                  "login": "loginek",
//                  "clientTypeName": "coach"
//                }
//                """;
//        RequestSpecification requestPut = RestAssured.given();
//        requestPut.contentType("application/json");
//        requestPut.body(JSON);
//
//        RequestSpecification requestGet = RestAssured.given();
//        String responseString = requestGet.get(new URI("http://localhost:8080/CourtRent-1.0-SNAPSHOT/api/users")).asString();
//
//        //Retrieve UUID
//        String responseLogin = requestGet.get(new URI("http://localhost:8080/CourtRent-1.0-SNAPSHOT/api/users/get?login=loginek")).asString();
//        int index = responseLogin.indexOf("\"id\":\"") + 6;
//        String clientId = responseLogin.substring(index, index + 36);
//
//        assertTrue(responseString.contains(
//                "\"archive\":false," +
//                "\"id\":\"" + clientId + "\"," +
//                "\"login\":\"loginek\"," +
//                "\"clientTypeName\":\"normal\"," +
//                "\"firstName\":\"Adam\"," +
//                "\"lastName\":\"Smith\""));
//        assertFalse(responseString.contains(
//                "\"archive\":true," +
//                "\"id\":\"" + clientId + "\"," +
//                "\"login\":\"loginek\"," +
//                "\"clientTypeName\":\"coach\"," +
//                "\"firstName\":\"John\"," +
//                "\"lastName\":\"Smith\""));
//
//        Response responsePut = requestPut.put("http://localhost:8080/CourtRent-1.0-SNAPSHOT/api/users/modifyClient/" + clientId);
//
//        assertEquals(204, responsePut.getStatusCode());
//
//        responseString = requestGet.get(new URI("http://localhost:8080/CourtRent-1.0-SNAPSHOT/api/users")).asString();
//
//        assertFalse(responseString.contains(
//                "\"archive\":false," +
//                "\"id\":\"" + clientId + "\"," +
//                "\"login\":\"loginek\"," +
//                "\"clientTypeName\":\"normal\"," +
//                "\"firstName\":\"Adam\"," +
//                "\"lastName\":\"Smith\""));
//        assertTrue(responseString.contains(
//                "\"archive\":true," +
//                "\"id\":\"" + clientId + "\"," +
//                "\"login\":\"loginek\"," +
//                "\"clientTypeName\":\"coach\"," +
//                "\"firstName\":\"John\"," +
//                "\"lastName\":\"Smith\""));
//    }
//
//    @Test
//    void modifyClientTestNegInvalidData() throws URISyntaxException {
//        String JSON = """
//                {
//                  "archive": true,
//                  "firstName": "   ",
//                  "lastName": "Smith",
//                  "login": "loginek",
//                  "clientTypeName": "coach"
//                }
//                """;
//        RequestSpecification requestPut = RestAssured.given();
//        requestPut.contentType("application/json");
//        requestPut.body(JSON);
//
//        RequestSpecification requestGet = RestAssured.given();
//        String responseString = requestGet.get(new URI("http://localhost:8080/CourtRent-1.0-SNAPSHOT/api/users")).asString();
//
//        //Retrieve UUID
//        String responseLogin = requestGet.get(new URI("http://localhost:8080/CourtRent-1.0-SNAPSHOT/api/users/get?login=loginek")).asString();
//        int index = responseLogin.indexOf("\"id\":\"") + 6;
//        String clientId = responseLogin.substring(index, index + 36);
//
//        assertTrue(responseString.contains(
//                "\"archive\":false," +
//                        "\"id\":\"" + clientId + "\"," +
//                        "\"login\":\"loginek\"," +
//                        "\"clientTypeName\":\"normal\"," +
//                        "\"firstName\":\"Adam\"," +
//                        "\"lastName\":\"Smith\""));
//        assertFalse(responseString.contains(
//                "\"archive\":true," +
//                        "\"id\":\"" + clientId + "\"," +
//                        "\"login\":\"loginek\"," +
//                        "\"clientTypeName\":\"coach\"," +
//                        "\"firstName\":\"John\"," +
//                        "\"lastName\":\"Smith\""));
//
//        Response responsePut = requestPut.put("http://localhost:8080/CourtRent-1.0-SNAPSHOT/api/users/modifyClient/" + clientId);
//
//        assertEquals(400, responsePut.getStatusCode());
//
//        responseString = requestGet.get(new URI("http://localhost:8080/CourtRent-1.0-SNAPSHOT/api/users")).asString();
//
//        assertTrue(responseString.contains(
//                "\"archive\":false," +
//                "\"id\":\"" + clientId + "\"," +
//                "\"login\":\"loginek\"," +
//                "\"clientTypeName\":\"normal\"," +
//                "\"firstName\":\"Adam\"," +
//                "\"lastName\":\"Smith\""));
//        assertFalse(responseString.contains(
//                "\"archive\":true," +
//                "\"id\":\"" + clientId + "\"," +
//                "\"login\":\"loginek\"," +
//                "\"clientTypeName\":\"coach\"," +
//                "\"firstName\":\"John\"," +
//                "\"lastName\":\"Smith\""));
//    }
//
//    @Test
//    void modifyClientTestNegRepeatLoginOfAnotherClient() throws URISyntaxException {
//        String JSON = """
//                {
//                  "archive": true,
//                  "firstName": "John",
//                  "lastName": "Smith",
//                  "login": "michas13",
//                  "clientTypeName": "coach"
//                }
//                """;
//        RequestSpecification requestPut = RestAssured.given();
//        requestPut.contentType("application/json");
//        requestPut.body(JSON);
//
//        RequestSpecification requestGet = RestAssured.given();
//        String responseString = requestGet.get(new URI("http://localhost:8080/CourtRent-1.0-SNAPSHOT/api/users")).asString();
//
//        //Retrieve UUID
//        String responseLogin = requestGet.get(new URI("http://localhost:8080/CourtRent-1.0-SNAPSHOT/api/users/get?login=loginek")).asString();
//        int index = responseLogin.indexOf("\"id\":\"") + 6;
//        String clientId = responseLogin.substring(index, index + 36);
//
//        assertTrue(responseString.contains("\"login\":\"michas13\""));
//
//        assertTrue(responseString.contains(
//                "\"archive\":false," +
//                "\"id\":\"" + clientId + "\"," +
//                "\"login\":\"loginek\"," +
//                "\"clientTypeName\":\"normal\"," +
//                "\"firstName\":\"Adam\"," +
//                "\"lastName\":\"Smith\""));
//        assertFalse(responseString.contains(
//                "\"archive\":true," +
//                "\"id\":\"" + clientId + "\"," +
//                "\"login\":\"michas13\"," +
//                "\"clientTypeName\":\"coach\"," +
//                "\"firstName\":\"John\"," +
//                "\"lastName\":\"Smith\""));
//
//        Response responsePut = requestPut.put("http://localhost:8080/CourtRent-1.0-SNAPSHOT/api/users/modifyClient/" + clientId);
//
//        assertEquals(409, responsePut.getStatusCode());
//
//        responseString = requestGet.get(new URI("http://localhost:8080/CourtRent-1.0-SNAPSHOT/api/users")).asString();
//
//        assertTrue(responseString.contains("\"login\":\"michas13\""));
//
//        assertTrue(responseString.contains(
//                "\"archive\":false," +
//                "\"id\":\"" + clientId + "\"," +
//                "\"login\":\"loginek\"," +
//                "\"clientTypeName\":\"normal\"," +
//                "\"firstName\":\"Adam\"," +
//                "\"lastName\":\"Smith\""));
//        assertFalse(responseString.contains(
//                "\"archive\":true," +
//                "\"id\":\"" + clientId + "\"," +
//                "\"login\":\"michas13\"," +
//                "\"clientTypeName\":\"coach\"," +
//                "\"firstName\":\"John\"," +
//                "\"lastName\":\"Smith\""));
//    }
//
//    @Test
//    void archiveAndActivateClientTest() throws URISyntaxException {
//        RequestSpecification requestGet = RestAssured.given();
//        String responseString = requestGet.get(new URI("http://localhost:8080/CourtRent-1.0-SNAPSHOT/api/users")).asString();
//
//        //Retrieve UUID
//        String responseLogin = requestGet.get(new URI("http://localhost:8080/CourtRent-1.0-SNAPSHOT/api/users/get?login=loginek")).asString();
//        int index = responseLogin.indexOf("\"id\":\"") + 6;
//        String clientId = responseLogin.substring(index, index + 36);
//
//        /*Archive test*/
//        assertTrue(responseString.contains(
//                "\"archive\":false," +
//                "\"id\":\"" + clientId + "\""));
//        assertFalse(responseString.contains(
//                "\"archive\":true," +
//                "\"id\":\"" + clientId + "\""));
//
//        RequestSpecification requestPost = RestAssured.given();
//        Response responsePost = requestPost.post("http://localhost:8080/CourtRent-1.0-SNAPSHOT/api/users/archive/" + clientId);
//
//        assertEquals(204, responsePost.getStatusCode());
//
//        responseString = requestGet.get(new URI("http://localhost:8080/CourtRent-1.0-SNAPSHOT/api/users")).asString();
//
//        assertFalse(responseString.contains(
//                "\"archive\":false," +
//                "\"id\":\"" + clientId + "\""));
//        assertTrue(responseString.contains(
//                "\"archive\":true," +
//                "\"id\":\"" + clientId + "\""));
//
//        /*Activate test*/
//        RequestSpecification requestPost2 = RestAssured.given();
//        Response responsePost2 = requestPost2.post("http://localhost:8080/CourtRent-1.0-SNAPSHOT/api/users/activate/" + clientId);
//
//        assertEquals(204, responsePost2.getStatusCode());
//
//        responseString = requestGet.get(new URI("http://localhost:8080/CourtRent-1.0-SNAPSHOT/api/users")).asString();
//
//        assertTrue(responseString.contains(
//                "\"archive\":false," +
//                "\"id\":\"" + clientId + "\""));
//        assertFalse(responseString.contains(
//                "\"archive\":true," +
//                "\"id\":\"" + clientId + "\""));
//    }
}
