package integrationtests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

import static integrationtests.NewCleaningClassForTests.cleanCourts;
import static integrationtests.NewCleaningClassForTests.initClients;
import static integrationtests.NewCleaningClassForTests.initCourts;
import static integrationtests.NewCleaningClassForTests.initReservations;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CourtControllerTests {

    static final String appUrlCourt = "http://localhost:8080/api/courts";

    @BeforeAll
    static void init() throws URISyntaxException {
        RestAssured.given().get(new URI(appUrlCourt));
    }

    @AfterAll
    static void cleanAtTheEnd() {
        cleanCourts();
    }

    @BeforeEach
    void cleanAndInitDatabase() {
        cleanCourts();
        initCourts();
    }

    @Test
    void getAllCourtsTest() throws URISyntaxException {
        RequestSpecification request = RestAssured.given();
        Response response = request.get(new URI(appUrlCourt));
        String responseString = response.asString();
        String[] splitedRespStr = responseString.split("},");

        assertEquals(5, splitedRespStr.length);

        //First Court
        assertTrue(splitedRespStr[0].contains("\"archive\":false"));
        assertTrue(splitedRespStr[0].contains("\"area\":100"));
        assertTrue(splitedRespStr[0].contains("\"baseCost\":100"));
        assertTrue(splitedRespStr[0].contains("\"courtNumber\":1"));
        assertTrue(splitedRespStr[0].contains("\"id\":\""));
        assertTrue(splitedRespStr[0].contains("\"rented\":false"));

        //Third Court
        assertTrue(splitedRespStr[2].contains("\"archive\":false"));
        assertTrue(splitedRespStr[2].contains("\"area\":300"));
        assertTrue(splitedRespStr[2].contains("\"baseCost\":200"));
        assertTrue(splitedRespStr[2].contains("\"courtNumber\":3"));
        assertTrue(splitedRespStr[2].contains("\"id\":\""));
        assertTrue(splitedRespStr[2].contains("\"rented\":false"));

        assertEquals(200, response.getStatusCode());
    }

    @Test
    void getAllCourtsTestNoCont() throws URISyntaxException {
        cleanCourts();
        RequestSpecification request = RestAssured.given();
        Response response = request.get(new URI(appUrlCourt));
        String responseString = response.asString();

        assertTrue(responseString.isEmpty());
        assertEquals(204, response.getStatusCode());
    }

    @Test
    void createCourtTestPos() throws URISyntaxException {
        cleanCourts();
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
        String responseString = requestGet.get(new URI(appUrlCourt)).asString();

        assertTrue(responseString.isEmpty());

        Response responsePost = requestPost.post(appUrlCourt + "/addCourt");

        assertEquals(201, responsePost.getStatusCode());

        responseString = requestGet.get(new URI(appUrlCourt)).asString();

        assertTrue(responseString.contains("\"archive\":false"));
        assertTrue(responseString.contains("\"area\":120"));
        assertTrue(responseString.contains("\"baseCost\":50"));
        assertTrue(responseString.contains("\"courtNumber\":15"));
        assertTrue(responseString.contains("\"id\":\""));
        assertTrue(responseString.contains("\"rented\":false"));
    }

    @Test
    void createCourtTestNegInvalidData() throws URISyntaxException {
        String json = """
                {
                  "area": 120.0,
                  "baseCost": -50,
                  "courtNumber": 15
                }
                """;
        RequestSpecification requestPost = RestAssured.given();
        requestPost.contentType("application/json");
        requestPost.body(json);

        RequestSpecification requestGet = RestAssured.given();
        String responseString = requestGet.get(new URI(appUrlCourt)).asString();

        assertFalse(responseString.contains("\"courtNumber\":15"));
        assertFalse(responseString.contains("\"baseCost\":-50"));

        Response responsePost = requestPost.post(appUrlCourt + "/addCourt");

        assertEquals(400, responsePost.getStatusCode());

        responseString = requestGet.get(new URI(appUrlCourt)).asString();

        assertFalse(responseString.contains("\"courtNumber\":15"));
        assertFalse(responseString.contains("\"baseCost\":-50"));
    }

    @Test
    void createCourtTestNegSameNumber() throws URISyntaxException {
        String json = """
                {
                  "area": 120.0,
                  "baseCost": 50,
                  "courtNumber": 2
                }
                """;
        RequestSpecification requestPost = RestAssured.given();
        requestPost.contentType("application/json");
        requestPost.body(json);

        RequestSpecification requestGet = RestAssured.given();
        String responseString = requestGet.get(new URI(appUrlCourt)).asString();

        assertTrue(responseString.contains("\"courtNumber\":2"));

        assertFalse(responseString.contains("\"area\":120.0"));
        assertFalse(responseString.contains("\"baseCost\":50"));

        Response responsePost = requestPost.post(appUrlCourt + "/addCourt");

        assertEquals(409, responsePost.getStatusCode());

        responseString = requestGet.get(new URI(appUrlCourt)).asString();

        assertTrue(responseString.contains("\"courtNumber\":2"));

        assertFalse(responseString.contains("\"area\":120.0"));
        assertFalse(responseString.contains("\"baseCost\":50"));
    }

    @Test
    void getCourtByCourtNumberTest() throws URISyntaxException {
        RequestSpecification request = RestAssured.given();
        Response response = request.get(new URI(appUrlCourt + "/get?number=3"));
        String responseString = response.asString();
        String[] splitedRespStr = responseString.split("},");

        assertEquals(1, splitedRespStr.length);

        System.out.println(splitedRespStr[0]);
        assertTrue(splitedRespStr[0].contains("\"archive\":false"));
        assertTrue(splitedRespStr[0].contains("\"area\":300"));
        assertTrue(splitedRespStr[0].contains("\"baseCost\":200"));
        assertTrue(splitedRespStr[0].contains("\"courtNumber\":3"));
        assertTrue(splitedRespStr[0].contains("\"id\":\""));
        assertTrue(splitedRespStr[0].contains("\"rented\":false"));

        assertEquals(200, response.getStatusCode());
    }

    @Test
    void getCourtByCourtNumberTestNoCont() throws URISyntaxException {
        RequestSpecification request = RestAssured.given();
        Response response = request.get(new URI(appUrlCourt + "/get?number=42343242"));
        String responseString = response.asString();

        assertTrue(responseString.isEmpty());
        assertEquals(204, response.getStatusCode());
    }

    @Test
    void getCourtByIdTest() throws URISyntaxException {
        RequestSpecification request = RestAssured.given();

        //Retrieve UUID
        String responseNumber = request.get(new URI(appUrlCourt + "/get?number=1")).asString();
        int index = responseNumber.indexOf("\"id\":\"") + 6;
        String courtId = responseNumber.substring(index, index + 36);

        Response responseById = request.get(new URI(appUrlCourt + "/" + courtId));
        String responseByIdString = responseById.asString();
        String[] splitedRespStr = responseByIdString.split("},");

        assertEquals(1, splitedRespStr.length);

        assertTrue(splitedRespStr[0].contains("\"archive\":false"));
        assertTrue(splitedRespStr[0].contains("\"area\":100"));
        assertTrue(splitedRespStr[0].contains("\"baseCost\":100"));
        assertTrue(splitedRespStr[0].contains("\"courtNumber\":1"));
        assertTrue(splitedRespStr[0].contains("\"id\":\""));
        assertTrue(splitedRespStr[0].contains("\"rented\":false"));

        assertEquals(200, responseById.getStatusCode());
    }

    @Test
    void getCourtByIdTestNoCont() throws URISyntaxException {
        RequestSpecification request = RestAssured.given();
        Response response = request.get(new URI(appUrlCourt + "/" + UUID.randomUUID()));
        String responseString = response.asString();

        assertTrue(responseString.isEmpty());
        assertEquals(204, response.getStatusCode());
    }

    @Test
    void modifyCourtTest() throws URISyntaxException {
        String JSON = """
                {
                  "area": 150.0,
                  "baseCost": 75,
                  "courtNumber": 2
                }
                """;
        RequestSpecification requestPut = RestAssured.given();
        requestPut.contentType("application/json");
        requestPut.body(JSON);

        RequestSpecification requestGet = RestAssured.given();
        String responseString = requestGet.get(new URI(appUrlCourt)).asString();

        //Retrieve UUID
        String responseNumber = requestGet.get(new URI(appUrlCourt + "/get?number=2")).asString();
        int index = responseNumber.indexOf("\"id\":\"") + 6;
        String courtId = responseNumber.substring(index, index + 36);

        assertTrue(responseString.contains(
                "\"archive\":false," +
                        "\"area\":100.0," +
                        "\"baseCost\":200," +
                        "\"courtNumber\":2," +
                        "\"id\":\"" + courtId + "\"," +
                        "\"rented\":false"));
        assertFalse(responseString.contains(
                "\"archive\":false," +
                        "\"area\":150.0," +
                        "\"baseCost\":75," +
                        "\"courtNumber\":2," +
                        "\"id\":\"" + courtId + "\"," +
                        "\"rented\":false"));

        Response responsePut = requestPut.put(appUrlCourt + "/modifyCourt/" + courtId);

        assertEquals(204, responsePut.getStatusCode());

        responseString = requestGet.get(new URI(appUrlCourt)).asString();

        assertFalse(responseString.contains(
                "\"archive\":false," +
                        "\"area\":100.0," +
                        "\"baseCost\":200," +
                        "\"courtNumber\":2," +
                        "\"id\":\"" + courtId + "\"," +
                        "\"rented\":false"));
        assertTrue(responseString.contains(
                "\"archive\":false," +
                        "\"area\":150.0," +
                        "\"baseCost\":75," +
                        "\"courtNumber\":2," +
                        "\"id\":\"" + courtId + "\"," +
                        "\"rented\":false"));
    }

    @Test
    void modifyCourtTestNegInvalidData() throws URISyntaxException {
        String JSON = """
                {
                  "area": 150.0,
                  "baseCost": -75,
                  "courtNumber": 2
                }
                """;
        RequestSpecification requestPut = RestAssured.given();
        requestPut.contentType("application/json");
        requestPut.body(JSON);

        RequestSpecification requestGet = RestAssured.given();
        String responseString = requestGet.get(new URI(appUrlCourt)).asString();

        //Retrieve UUID
        String responseNumber = requestGet.get(new URI(appUrlCourt + "/get?number=2")).asString();
        int index = responseNumber.indexOf("\"id\":\"") + 6;
        String courtId = responseNumber.substring(index, index + 36);

        assertTrue(responseString.contains(
                "\"archive\":false," +
                        "\"area\":100.0," +
                        "\"baseCost\":200," +
                        "\"courtNumber\":2," +
                        "\"id\":\"" + courtId + "\"," +
                        "\"rented\":false"));
        assertFalse(responseString.contains(
                "\"archive\":false," +
                        "\"area\":150.0," +
                        "\"baseCost\":-75," +
                        "\"courtNumber\":2," +
                        "\"id\":\"" + courtId + "\"," +
                        "\"rented\":false"));

        Response responsePut = requestPut.put(appUrlCourt + "/modifyCourt/" + courtId);

        assertEquals(400, responsePut.getStatusCode());

        responseString = requestGet.get(new URI(appUrlCourt)).asString();

        assertTrue(responseString.contains(
                "\"archive\":false," +
                        "\"area\":100.0," +
                        "\"baseCost\":200," +
                        "\"courtNumber\":2," +
                        "\"id\":\"" + courtId + "\"," +
                        "\"rented\":false"));
        assertFalse(responseString.contains(
                "\"archive\":false," +
                        "\"area\":150.0," +
                        "\"baseCost\":-75," +
                        "\"courtNumber\":2," +
                        "\"id\":\"" + courtId + "\"," +
                        "\"rented\":false"));
    }

    @Test
    void modifyCourtTestNegRepeatNumberOfAnotherCourt() throws URISyntaxException {
        String JSON = """
                {
                  "area": 150.0,
                  "baseCost": 75,
                  "courtNumber": 1
                }
                """;
        RequestSpecification requestPut = RestAssured.given();
        requestPut.contentType("application/json");
        requestPut.body(JSON);

        RequestSpecification requestGet = RestAssured.given();
        String responseString = requestGet.get(new URI(appUrlCourt)).asString();

        //Retrieve UUID
        String responseNumber = requestGet.get(new URI(appUrlCourt + "/get?number=2")).asString();
        int index = responseNumber.indexOf("\"id\":\"") + 6;
        String courtId = responseNumber.substring(index, index + 36);

        assertTrue(responseString.contains(
                "\"archive\":false," +
                        "\"area\":100.0," +
                        "\"baseCost\":200," +
                        "\"courtNumber\":2," +
                        "\"id\":\"" + courtId + "\"," +
                        "\"rented\":false"));
        assertFalse(responseString.contains(
                "\"archive\":false," +
                        "\"area\":150.0," +
                        "\"baseCost\":75," +
                        "\"courtNumber\":1," +
                        "\"id\":\"" + courtId + "\"," +
                        "\"rented\":false"));

        Response responsePut = requestPut.put(appUrlCourt + "/modifyCourt/" + courtId);

        assertEquals(409, responsePut.getStatusCode());

        responseString = requestGet.get(new URI(appUrlCourt)).asString();

        assertTrue(responseString.contains(
                "\"archive\":false," +
                        "\"area\":100.0," +
                        "\"baseCost\":200," +
                        "\"courtNumber\":2," +
                        "\"id\":\"" + courtId + "\"," +
                        "\"rented\":false"));
        assertFalse(responseString.contains(
                "\"archive\":false," +
                        "\"area\":150.0," +
                        "\"baseCost\":75," +
                        "\"courtNumber\":1," +
                        "\"id\":\"" + courtId + "\"," +
                        "\"rented\":false"));
    }

    @Test
    void archiveAndActivateClientTest() throws URISyntaxException {
        RequestSpecification requestGet = RestAssured.given();
        String responseString = requestGet.get(new URI(appUrlCourt)).asString();

        //Retrieve UUID
        String responseNumber = requestGet.get(new URI(appUrlCourt + "/get?number=3")).asString();
        int index = responseNumber.indexOf("\"id\":\"") + 6;
        String courtId = responseNumber.substring(index, index + 36);

        /*Archive test*/
        assertTrue(responseString.contains(
                "\"archive\":false," +
                        "\"area\":300.0," +
                        "\"baseCost\":200," +
                        "\"courtNumber\":3," +
                        "\"id\":\"" + courtId + "\"," +
                        "\"rented\":false"));
        assertFalse(responseString.contains(
                "\"archive\":true," +
                        "\"area\":300.0," +
                        "\"baseCost\":200," +
                        "\"courtNumber\":3," +
                        "\"id\":\"" + courtId + "\"," +
                        "\"rented\":false"));

        RequestSpecification requestPost = RestAssured.given();
        Response responsePost = requestPost.post(appUrlCourt + "/deactivate/" + courtId);

        assertEquals(204, responsePost.getStatusCode());

        responseString = requestGet.get(new URI(appUrlCourt)).asString();

        assertFalse(responseString.contains(
                "\"archive\":false," +
                        "\"area\":300.0," +
                        "\"baseCost\":200," +
                        "\"courtNumber\":3," +
                        "\"id\":\"" + courtId + "\"," +
                        "\"rented\":false"));
        assertTrue(responseString.contains(
                "\"archive\":true," +
                        "\"area\":300.0," +
                        "\"baseCost\":200," +
                        "\"courtNumber\":3," +
                        "\"id\":\"" + courtId + "\"," +
                        "\"rented\":false"));

        /*Activate test*/
        RequestSpecification requestPost2 = RestAssured.given();
        Response responsePost2 = requestPost2.post(appUrlCourt + "/activate/" + courtId);

        assertEquals(204, responsePost2.getStatusCode());

        responseString = requestGet.get(new URI(appUrlCourt)).asString();

        assertTrue(responseString.contains(
                "\"archive\":false," +
                        "\"area\":300.0," +
                        "\"baseCost\":200," +
                        "\"courtNumber\":3," +
                        "\"id\":\"" + courtId + "\"," +
                        "\"rented\":false"));
        assertFalse(responseString.contains(
                "\"archive\":true," +
                        "\"area\":300.0," +
                        "\"baseCost\":200," +
                        "\"courtNumber\":3," +
                        "\"id\":\"" + courtId + "\"," +
                        "\"rented\":false"));
    }

    @Test
    void deleteCourtTestPos() throws URISyntaxException {
        RequestSpecification requestGet = RestAssured.given();
        String responseString = requestGet.get(new URI(appUrlCourt)).asString();

        //Retrieve UUID
        String responseNumber = requestGet.get(new URI(appUrlCourt + "/get?number=3")).asString();
        int index = responseNumber.indexOf("\"id\":\"") + 6;
        String courtId = responseNumber.substring(index, index + 36);

        assertTrue(responseString.contains(
                "\"archive\":false," +
                        "\"area\":300.0," +
                        "\"baseCost\":200," +
                        "\"courtNumber\":3," +
                        "\"id\":\"" + courtId + "\"," +
                        "\"rented\":false"));

        RequestSpecification requestDelete = RestAssured.given();
        Response responseDelete = requestDelete.delete(appUrlCourt + "/delete/" + courtId);

        assertEquals(204, responseDelete.getStatusCode());

        responseString = requestGet.get(new URI(appUrlCourt)).asString();

        assertFalse(responseString.contains(
                "\"archive\":false," +
                        "\"area\":300.0," +
                        "\"baseCost\":200," +
                        "\"courtNumber\":3," +
                        "\"id\":\"" + courtId + "\"," +
                        "\"rented\":false"));

        Response responseDelete2 = requestDelete.delete(appUrlCourt + "/delete/" + courtId);

        assertEquals(204, responseDelete2.getStatusCode());
    }

    @Test
    void deleteCourtTestNeg() throws URISyntaxException {
        //Additional preparing
        initClients();
        initReservations();

        RequestSpecification requestGet = RestAssured.given();
        String responseString = requestGet.get(new URI(appUrlCourt)).asString();

        //Retrieve UUID
        String responseNumber = requestGet.get(new URI(appUrlCourt + "/get?number=1")).asString();
        int index = responseNumber.indexOf("\"id\":\"") + 6;
        String courtId = responseNumber.substring(index, index + 36);

        assertTrue(responseString.contains(
                "\"archive\":false," +
                        "\"area\":100.0," +
                        "\"baseCost\":100," +
                        "\"courtNumber\":1," +
                        "\"id\":\"" + courtId + "\"," +
                        "\"rented\":true"));

        RequestSpecification requestDelete = RestAssured.given();
        Response responseDelete = requestDelete.delete(appUrlCourt + "/delete/" + courtId);

        assertEquals(409, responseDelete.getStatusCode());

        responseString = requestGet.get(new URI(appUrlCourt)).asString();

        assertTrue(responseString.contains(
                "\"archive\":false," +
                        "\"area\":100.0," +
                        "\"baseCost\":100," +
                        "\"courtNumber\":1," +
                        "\"id\":\"" + courtId + "\"," +
                        "\"rented\":true"));
    }
}
