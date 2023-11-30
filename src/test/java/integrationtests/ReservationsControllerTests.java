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
import static integrationtests.CleaningClass.client3;
import static integrationtests.CleaningClass.court3;
import static integrationtests.CleaningClass.initClients;
import static integrationtests.CleaningClass.initCourts;
import static integrationtests.CleaningClass.initReservations;
import static integrationtests.CleaningClass.reservation1;
import static integrationtests.CleaningClass.reservation2;
import static integrationtests.CleaningClass.reservation3;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ReservationsControllerTests {

    static final String appUrlReservation = "http://localhost:8080/CourtRent-1.0-SNAPSHOT/api/reservations";

    @AfterAll
    static void cleanAtTheEnd() {
        clean();
    }

    @BeforeEach
    void cleanAndInitDatabase() {
        clean();
        initClients();
        initCourts();
        initReservations();
    }

    @Test
    void getAllCurrentReservationsTest() throws URISyntaxException {
        RequestSpecification request = RestAssured.given();
        Response response = request.get(new URI(appUrlReservation));
        String responseString = response.asString();
        String[] splitedRespStr = responseString.split("},\\{");

        assertEquals(2, splitedRespStr.length);

        //First Reservation
        assertTrue(splitedRespStr[0].contains("\"beginTime\":\"2023-11-30T14:20:00\""));
        assertTrue(splitedRespStr[0].contains("\"client\":{\""));
        assertTrue(splitedRespStr[0].contains("\"court\":{\""));
        assertTrue(splitedRespStr[0].contains("\"id\":\"" + reservation1.getId() + "\""));
        assertTrue(splitedRespStr[0].contains("\"reservationCost\":0"));
        assertTrue(splitedRespStr[0].contains("\"reservationHours\":0"));

        //Second Reservation
        assertTrue(splitedRespStr[1].contains("\"beginTime\":\"2023-11-30T14:20:00\""));
        assertTrue(splitedRespStr[1].contains("\"client\":{\""));
        assertTrue(splitedRespStr[1].contains("\"court\":{\""));
        assertTrue(splitedRespStr[1].contains("\"id\":\"" + reservation2.getId() + "\""));
        assertTrue(splitedRespStr[1].contains("\"reservationCost\":0"));
        assertTrue(splitedRespStr[1].contains("\"reservationHours\":0"));

        assertEquals(200, response.getStatusCode());
    }

    @Test
    void getAllCurrentReservationsTestNoCont() throws URISyntaxException {
        clean();
        RequestSpecification request = RestAssured.given();
        Response response = request.get(new URI(appUrlReservation));
        String responseString = response.asString();

        assertTrue(responseString.isEmpty());
        assertEquals(204, response.getStatusCode());
    }

    @Test
    void getAllArchiveReservationsTest() throws URISyntaxException {
        RequestSpecification request = RestAssured.given();
        Response response = request.get(new URI(appUrlReservation + "/archive"));
        String responseString = response.asString();
        String[] splitedRespStr = responseString.split("},\\{");

        assertEquals(1, splitedRespStr.length);

        assertTrue(splitedRespStr[0].contains("\"beginTime\":\"2023-11-28T14:20:00\""));
        assertTrue(splitedRespStr[0].contains("\"client\":{\""));
        assertTrue(splitedRespStr[0].contains("\"court\":{\""));
        assertTrue(splitedRespStr[0].contains("\"endTime\":\"2023-11-30T14:20:00\""));
        assertTrue(splitedRespStr[0].contains("\"id\":\"" + reservation3.getId() + "\""));
        assertTrue(splitedRespStr[0].contains("\"reservationCost\":13180"));
        assertTrue(splitedRespStr[0].contains("\"reservationHours\":48"));

        assertEquals(200, response.getStatusCode());
    }

    @Test
    void getAllArchiveReservationsTestNoCont() throws URISyntaxException {
        clean();
        RequestSpecification request = RestAssured.given();
        Response response = request.get(new URI(appUrlReservation + "/archive"));
        String responseString = response.asString();

        assertTrue(responseString.isEmpty());
        assertEquals(204, response.getStatusCode());
    }

    @Test
    void createReservationTestPos() throws URISyntaxException {
        clean();
        initClients();
        initCourts();
        RequestSpecification requestPost = RestAssured.given();

        RequestSpecification requestGet = RestAssured.given();
        String responseString = requestGet.get(new URI(appUrlReservation)).asString();

        assertTrue(responseString.isEmpty());

        Response responsePost = requestPost.post(appUrlReservation +
                "/addReservation?clientId=%s&courtId=%s&date=%s".formatted(client3.getId(), court3.getId(),
                        "2023-11-30T17:03:22"));

        assertEquals(201, responsePost.getStatusCode());

        responseString = requestGet.get(new URI(appUrlReservation)).asString();

        assertTrue(responseString.contains("\"beginTime\":\"2023-11-30T17:03:22\""));
        assertTrue(responseString.contains("\"client\":{\""));
        assertTrue(responseString.contains("\"id\":\"%s\"".formatted(client3.getId())));
        assertTrue(responseString.contains("\"court\":{\""));
        assertTrue(responseString.contains("\"id\":\"%s\"".formatted(court3.getId())));
    }

    @Test
    void createCourtTestNegInvalidData() throws URISyntaxException {
        clean();
        initClients();
        initCourts();
        RequestSpecification requestPost = RestAssured.given();

        RequestSpecification requestGet = RestAssured.given();
        String responseString = requestGet.get(new URI(appUrlReservation)).asString();

        assertTrue(responseString.isEmpty());

        Response responsePost = requestPost.post(appUrlReservation +
                "/addReservation?clientId=%s&courtId=%s&date=%s".formatted("XXX", court3.getId(),
                        "2023-11-30T17:03:22"));

        assertEquals(409, responsePost.getStatusCode());

        responseString = requestGet.get(new URI(appUrlReservation)).asString();

        assertFalse(responseString.contains("\"beginTime\":\"2023-11-30T17:03:22\""));
        assertFalse(responseString.contains("\"client\":{\""));
        assertFalse(responseString.contains("\"id\":\"%s\"".formatted(client3.getId())));
        assertFalse(responseString.contains("\"court\":{\""));
        assertFalse(responseString.contains("\"id\":\"%s\"".formatted(court3.getId())));
    }

//    @Test
//    void createCourtTestNegSameNumber() throws URISyntaxException {
//        String json = """
//                {
//                  "area": 120.0,
//                  "baseCost": 50,
//                  "courtNumber": 2
//                }
//                """;
//        RequestSpecification requestPost = RestAssured.given();
//        requestPost.contentType("application/json");
//        requestPost.body(json);
//
//        RequestSpecification requestGet = RestAssured.given();
//        String responseString = requestGet.get(new URI(appUrlCourt)).asString();
//
//        assertTrue(responseString.contains("\"courtNumber\":2"));
//
//        assertFalse(responseString.contains("\"area\":120.0"));
//        assertFalse(responseString.contains("\"baseCost\":50"));
//
//        Response responsePost = requestPost.post(appUrlCourt + "/addCourt");
//
//        assertEquals(409, responsePost.getStatusCode());
//
//        responseString = requestGet.get(new URI(appUrlCourt)).asString();
//
//        assertTrue(responseString.contains("\"courtNumber\":2"));
//
//        assertFalse(responseString.contains("\"area\":120.0"));
//        assertFalse(responseString.contains("\"baseCost\":50"));
//    }
//
//    @Test
//    void getCourtByCourtNumberTest() throws URISyntaxException {
//        RequestSpecification request = RestAssured.given();
//        Response response = request.get(new URI(appUrlCourt + "/get?number=3"));
//        String responseString = response.asString();
//        String[] splitedRespStr = responseString.split("},");
//
//        assertEquals(1, splitedRespStr.length);
//
//        assertTrue(splitedRespStr[0].contains("\"archive\":false"));
//        assertTrue(splitedRespStr[0].contains("\"area\":300"));
//        assertTrue(splitedRespStr[0].contains("\"baseCost\":200"));
//        assertTrue(splitedRespStr[0].contains("\"courtNumber\":3"));
//        assertTrue(splitedRespStr[0].contains("\"id\":\""));
//        assertTrue(splitedRespStr[0].contains("\"rented\":false"));
//
//        assertEquals(200, response.getStatusCode());
//    }
//
//    @Test
//    void getCourtByCourtNumberTestNoCont() throws URISyntaxException {
//        RequestSpecification request = RestAssured.given();
//        Response response = request.get(new URI(appUrlCourt + "/get?number=42343242"));
//        String responseString = response.asString();
//
//        assertTrue(responseString.isEmpty());
//        assertEquals(204, response.getStatusCode());
//    }
//
//    @Test
//    void getCourtByIdTest() throws URISyntaxException {
//        RequestSpecification request = RestAssured.given();
//
//        //Retrieve UUID
//        String responseNumber = request.get(new URI(appUrlCourt + "/get?number=1")).asString();
//        int index = responseNumber.indexOf("\"id\":\"") + 6;
//        String courtId = responseNumber.substring(index, index + 36);
//
//        Response responseById = request.get(new URI(appUrlCourt + "/" + courtId));
//        String responseByIdString = responseById.asString();
//        String[] splitedRespStr = responseByIdString.split("},");
//
//        assertEquals(1, splitedRespStr.length);
//
//        assertTrue(splitedRespStr[0].contains("\"archive\":false"));
//        assertTrue(splitedRespStr[0].contains("\"area\":100"));
//        assertTrue(splitedRespStr[0].contains("\"baseCost\":100"));
//        assertTrue(splitedRespStr[0].contains("\"courtNumber\":1"));
//        assertTrue(splitedRespStr[0].contains("\"id\":\""));
//        assertTrue(splitedRespStr[0].contains("\"rented\":false"));
//
//        assertEquals(200, responseById.getStatusCode());
//    }
//
//    @Test
//    void getCourtByIdTestNoCont() throws URISyntaxException {
//        RequestSpecification request = RestAssured.given();
//        Response response = request.get(new URI(appUrlCourt + "/" + UUID.randomUUID()));
//        String responseString = response.asString();
//
//        assertTrue(responseString.isEmpty());
//        assertEquals(204, response.getStatusCode());
//    }
//
//    @Test
//    void modifyCourtTest() throws URISyntaxException {
//        String JSON = """
//                {
//                  "area": 150.0,
//                  "baseCost": 75,
//                  "courtNumber": 2
//                }
//                """;
//        RequestSpecification requestPut = RestAssured.given();
//        requestPut.contentType("application/json");
//        requestPut.body(JSON);
//
//        RequestSpecification requestGet = RestAssured.given();
//        String responseString = requestGet.get(new URI(appUrlCourt)).asString();
//
//        //Retrieve UUID
//        String responseNumber = requestGet.get(new URI(appUrlCourt + "/get?number=2")).asString();
//        int index = responseNumber.indexOf("\"id\":\"") + 6;
//        String courtId = responseNumber.substring(index, index + 36);
//
//        assertTrue(responseString.contains(
//                "\"archive\":false," +
//                        "\"area\":100.0," +
//                        "\"baseCost\":200," +
//                        "\"courtNumber\":2," +
//                        "\"id\":\"" + courtId + "\"," +
//                        "\"rented\":false"));
//        assertFalse(responseString.contains(
//                "\"archive\":false," +
//                        "\"area\":150.0," +
//                        "\"baseCost\":75," +
//                        "\"courtNumber\":2," +
//                        "\"id\":\"" + courtId + "\"," +
//                        "\"rented\":false"));
//
//        Response responsePut = requestPut.put(appUrlCourt + "/modifyCourt/" + courtId);
//
//        assertEquals(204, responsePut.getStatusCode());
//
//        responseString = requestGet.get(new URI(appUrlCourt)).asString();
//
//        assertFalse(responseString.contains(
//                "\"archive\":false," +
//                        "\"area\":100.0," +
//                        "\"baseCost\":200," +
//                        "\"courtNumber\":2," +
//                        "\"id\":\"" + courtId + "\"," +
//                        "\"rented\":false"));
//        assertTrue(responseString.contains(
//                "\"archive\":false," +
//                        "\"area\":150.0," +
//                        "\"baseCost\":75," +
//                        "\"courtNumber\":2," +
//                        "\"id\":\"" + courtId + "\"," +
//                        "\"rented\":false"));
//    }
//
//    @Test
//    void modifyCourtTestNegInvalidData() throws URISyntaxException {
//        String JSON = """
//                {
//                  "area": 150.0,
//                  "baseCost": -75,
//                  "courtNumber": 2
//                }
//                """;
//        RequestSpecification requestPut = RestAssured.given();
//        requestPut.contentType("application/json");
//        requestPut.body(JSON);
//
//        RequestSpecification requestGet = RestAssured.given();
//        String responseString = requestGet.get(new URI(appUrlCourt)).asString();
//
//        //Retrieve UUID
//        String responseNumber = requestGet.get(new URI(appUrlCourt + "/get?number=2")).asString();
//        int index = responseNumber.indexOf("\"id\":\"") + 6;
//        String courtId = responseNumber.substring(index, index + 36);
//
//        assertTrue(responseString.contains(
//                "\"archive\":false," +
//                        "\"area\":100.0," +
//                        "\"baseCost\":200," +
//                        "\"courtNumber\":2," +
//                        "\"id\":\"" + courtId + "\"," +
//                        "\"rented\":false"));
//        assertFalse(responseString.contains(
//                "\"archive\":false," +
//                        "\"area\":150.0," +
//                        "\"baseCost\":-75," +
//                        "\"courtNumber\":2," +
//                        "\"id\":\"" + courtId + "\"," +
//                        "\"rented\":false"));
//
//        Response responsePut = requestPut.put(appUrlCourt + "/modifyCourt/" + courtId);
//
//        assertEquals(400, responsePut.getStatusCode());
//
//        responseString = requestGet.get(new URI(appUrlCourt)).asString();
//
//        assertTrue(responseString.contains(
//                "\"archive\":false," +
//                        "\"area\":100.0," +
//                        "\"baseCost\":200," +
//                        "\"courtNumber\":2," +
//                        "\"id\":\"" + courtId + "\"," +
//                        "\"rented\":false"));
//        assertFalse(responseString.contains(
//                "\"archive\":false," +
//                        "\"area\":150.0," +
//                        "\"baseCost\":-75," +
//                        "\"courtNumber\":2," +
//                        "\"id\":\"" + courtId + "\"," +
//                        "\"rented\":false"));
//    }
//
//    @Test
//    void modifyCourtTestNegRepeatNumberOfAnotherCourt() throws URISyntaxException {
//        String JSON = """
//                {
//                  "area": 150.0,
//                  "baseCost": 75,
//                  "courtNumber": 1
//                }
//                """;
//        RequestSpecification requestPut = RestAssured.given();
//        requestPut.contentType("application/json");
//        requestPut.body(JSON);
//
//        RequestSpecification requestGet = RestAssured.given();
//        String responseString = requestGet.get(new URI(appUrlCourt)).asString();
//
//        //Retrieve UUID
//        String responseNumber = requestGet.get(new URI(appUrlCourt + "/get?number=2")).asString();
//        int index = responseNumber.indexOf("\"id\":\"") + 6;
//        String courtId = responseNumber.substring(index, index + 36);
//
//        assertTrue(responseString.contains(
//                "\"archive\":false," +
//                        "\"area\":100.0," +
//                        "\"baseCost\":200," +
//                        "\"courtNumber\":2," +
//                        "\"id\":\"" + courtId + "\"," +
//                        "\"rented\":false"));
//        assertFalse(responseString.contains(
//                "\"archive\":false," +
//                        "\"area\":150.0," +
//                        "\"baseCost\":75," +
//                        "\"courtNumber\":1," +
//                        "\"id\":\"" + courtId + "\"," +
//                        "\"rented\":false"));
//
//        Response responsePut = requestPut.put(appUrlCourt + "/modifyCourt/" + courtId);
//
//        assertEquals(409, responsePut.getStatusCode());
//
//        responseString = requestGet.get(new URI(appUrlCourt)).asString();
//
//        assertTrue(responseString.contains(
//                "\"archive\":false," +
//                        "\"area\":100.0," +
//                        "\"baseCost\":200," +
//                        "\"courtNumber\":2," +
//                        "\"id\":\"" + courtId + "\"," +
//                        "\"rented\":false"));
//        assertFalse(responseString.contains(
//                "\"archive\":false," +
//                        "\"area\":150.0," +
//                        "\"baseCost\":75," +
//                        "\"courtNumber\":1," +
//                        "\"id\":\"" + courtId + "\"," +
//                        "\"rented\":false"));
//    }
}
