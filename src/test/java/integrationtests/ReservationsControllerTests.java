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

import static integrationtests.NewCleaningClassForTests.cleanAll;
import static integrationtests.NewCleaningClassForTests.cleanReservations;
import static integrationtests.NewCleaningClassForTests.client1;
import static integrationtests.NewCleaningClassForTests.client2;
import static integrationtests.NewCleaningClassForTests.client3;
import static integrationtests.NewCleaningClassForTests.client4;
import static integrationtests.NewCleaningClassForTests.court1;
import static integrationtests.NewCleaningClassForTests.court3;
import static integrationtests.NewCleaningClassForTests.court2;
import static integrationtests.NewCleaningClassForTests.court4;
import static integrationtests.NewCleaningClassForTests.court5;
import static integrationtests.NewCleaningClassForTests.initClients;
import static integrationtests.NewCleaningClassForTests.initCourts;
import static integrationtests.NewCleaningClassForTests.initReservations;
import static integrationtests.NewCleaningClassForTests.reservation1;
import static integrationtests.NewCleaningClassForTests.reservation2;
import static integrationtests.NewCleaningClassForTests.reservation3;
import static integrationtests.NewCleaningClassForTests.reservation6;
import static integrationtests.NewCleaningClassForTests.reservation7;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ReservationsControllerTests {

    static final String appUrlReservation = "http://localhost:8080/CourtRent-1.0-SNAPSHOT/api/reservations";

    @BeforeAll
    static void init() throws URISyntaxException  {
        RestAssured.given().get(new URI(appUrlReservation));
    }

    @AfterAll
    static void cleanAtTheEnd() {
        cleanAll();
    }

    @BeforeEach
    void cleanAndInitDatabase() {
        cleanAll();
        initReservations();
    }

//    @Test
//    void getAllCurrentReservationsTest() throws URISyntaxException {
//        RequestSpecification request = RestAssured.given();
//        Response response = request.get(new URI(appUrlReservation));
//        String responseString = response.asString();
//        String[] splitedRespStr = responseString.split("},\\{");
//
//        assertEquals(4, splitedRespStr.length);
//
//        //First Reservation
//        assertTrue(splitedRespStr[0].contains("\"beginTime\":\"2023-11-30T14:20:00\""));
//        assertTrue(splitedRespStr[0].contains("\"client\":{\""));
//        assertTrue(splitedRespStr[0].contains("\"court\":{\""));
//        assertTrue(splitedRespStr[0].contains("\"id\":\"" + reservation1.getId() + "\""));
//        assertTrue(splitedRespStr[0].contains("\"reservationCost\":0"));
//        assertTrue(splitedRespStr[0].contains("\"reservationHours\":0"));
//
//        //Second Reservation
//        assertTrue(splitedRespStr[1].contains("\"beginTime\":\"2023-11-30T14:20:00\""));
//        assertTrue(splitedRespStr[1].contains("\"client\":{\""));
//        assertTrue(splitedRespStr[1].contains("\"court\":{\""));
//        assertTrue(splitedRespStr[1].contains("\"id\":\"" + reservation2.getId() + "\""));
//        assertTrue(splitedRespStr[1].contains("\"reservationCost\":0"));
//        assertTrue(splitedRespStr[1].contains("\"reservationHours\":0"));
//
//        assertEquals(200, response.getStatusCode());
//    }
//
//    @Test
//    void getAllCurrentReservationsTestNoCont() throws URISyntaxException {
//        cleanReservations();
//        RequestSpecification request = RestAssured.given();
//        Response response = request.get(new URI(appUrlReservation));
//        String responseString = response.asString();
//
//        assertTrue(responseString.isEmpty());
//        assertEquals(204, response.getStatusCode());
//    }
//
//    @Test
//    void getAllArchiveReservationsTest() throws URISyntaxException {
//        RequestSpecification request = RestAssured.given();
//        Response response = request.get(new URI(appUrlReservation + "/archive"));
//        String responseString = response.asString();
//        String[] splitedRespStr = responseString.split("},\\{");
//
//        assertEquals(3, splitedRespStr.length);
//
//        assertTrue(splitedRespStr[0].contains("\"beginTime\":\"2023-11-28T14:20:00\""));
//        assertTrue(splitedRespStr[0].contains("\"client\":{\""));
//        assertTrue(splitedRespStr[0].contains("\"court\":{\""));
//        assertTrue(splitedRespStr[0].contains("\"endTime\":\"2023-11-30T14:20:00\""));
//        assertTrue(splitedRespStr[0].contains("\"id\":\"" + reservation3.getId() + "\""));
//        assertTrue(splitedRespStr[0].contains("\"reservationCost\":13180"));
//        assertTrue(splitedRespStr[0].contains("\"reservationHours\":48"));
//
//        assertEquals(200, response.getStatusCode());
//    }
//
//    @Test
//    void getAllArchiveReservationsTestNoCont() throws URISyntaxException {
//        cleanReservations();
//        RequestSpecification request = RestAssured.given();
//        Response response = request.get(new URI(appUrlReservation + "/archive"));
//        String responseString = response.asString();
//
//        assertTrue(responseString.isEmpty());
//        assertEquals(204, response.getStatusCode());
//    }
//
//    @Test
//    void createReservationTestPos() throws URISyntaxException {
//        cleanReservations();
//        initClients();
//        initCourts();
//        RequestSpecification requestPost = RestAssured.given();
//
//        RequestSpecification requestGet = RestAssured.given();
//        String responseString = requestGet.get(new URI(appUrlReservation)).asString();
//
//        assertTrue(responseString.isEmpty());
//
//        Response responsePost = requestPost.post(appUrlReservation +
//                "/addReservation?clientId=%s&courtId=%s&date=%s".formatted(client3.getId(), court3.getId(),
//                        "2023-11-30T17:03:22"));
//
//        assertEquals(201, responsePost.getStatusCode());
//
//        responseString = requestGet.get(new URI(appUrlReservation)).asString();
//
//        assertTrue(responseString.contains("\"beginTime\":\"2023-11-30T17:03:22\""));
//        assertTrue(responseString.contains("\"client\":{\""));
//        assertTrue(responseString.contains("\"id\":\"%s\"".formatted(client3.getId())));
//        assertTrue(responseString.contains("\"court\":{\""));
//        assertTrue(responseString.contains("\"id\":\"%s\"".formatted(court3.getId())));
//    }
//
//    @Test
//    void createReservationTestNegInvalidData() throws URISyntaxException {
//        cleanReservations();
//        initClients();
//        initCourts();
//        RequestSpecification requestPost = RestAssured.given();
//
//        RequestSpecification requestGet = RestAssured.given();
//        String responseString = requestGet.get(new URI(appUrlReservation)).asString();
//
//        assertTrue(responseString.isEmpty());
//
//        Response responsePost = requestPost.post(appUrlReservation +
//                "/addReservation?clientId=%s&courtId=%s&date=%s".formatted("XXX", court3.getId(),
//                        "2023-11-30T17:03:22"));
//
//        assertEquals(400, responsePost.getStatusCode());
//
//        responseString = requestGet.get(new URI(appUrlReservation)).asString();
//
//        assertFalse(responseString.contains("\"beginTime\":\"2023-11-30T17:03:22\""));
//        assertFalse(responseString.contains("\"client\":{\""));
//        assertFalse(responseString.contains("\"id\":\"%s\"".formatted(client3.getId())));
//        assertFalse(responseString.contains("\"court\":{\""));
//        assertFalse(responseString.contains("\"id\":\"%s\"".formatted(court3.getId())));
//    }
//
//    @Test
//    void createReservationTestNegReservedCourt() throws URISyntaxException {
//        RequestSpecification requestPost = RestAssured.given();
//
//        RequestSpecification requestGet = RestAssured.given();
//        String responseString = requestGet.get(new URI(appUrlReservation)).asString();
//
//        assertFalse(responseString.contains("\"beginTime\":\"2023-12-15T17:03:22\""));
//        assertFalse(responseString.contains("\"id\":\"%s\"".formatted(client4.getId())));
//        assertTrue(responseString.contains("\"id\":\"%s\"".formatted(court2.getId())));
//
//        Response responsePostNeg = requestPost.post(appUrlReservation +
//                "/addReservation?clientId=%s&courtId=%s&date=%s".formatted(client4.getId(), court2.getId(),
//                        "2023-12-15T17:03:22"));
//
//        assertEquals(409, responsePostNeg.getStatusCode());
//
//        responseString = requestGet.get(new URI(appUrlReservation)).asString();
//
//        assertFalse(responseString.contains("\"beginTime\":\"2023-12-15T17:03:22\""));
//        assertFalse(responseString.contains("\"id\":\"%s\"".formatted(client4.getId())));
//        assertTrue(responseString.contains("\"id\":\"%s\"".formatted(court2.getId())));
//    }
//
//    @Test
//    void returnCourtTestPos() throws URISyntaxException {
//        RequestSpecification request = RestAssured.given();
//
//        //Check current reservations
//        Response response = request.get(new URI(appUrlReservation));
//        String responseString = response.asString();
//        String[] splitedRespStr = responseString.split("},\\{");
//
//        assertEquals(4, splitedRespStr.length);
//
//        //First Reservation
//        assertTrue(responseString.contains("\"id\":\"" + reservation1.getId() + "\""));
//        assertFalse(responseString.contains("\"endTime\":\"2023-12-05T17:03:22\""));
//
//        //Check archive reservations
//        Response responseArch = request.get(new URI(appUrlReservation + "/archive"));
//        String responseStringArch = responseArch.asString();
//        String[] splitedRespStrArch = responseStringArch.split("},\\{");
//
//        assertEquals(3, splitedRespStrArch.length);
//
//        //First Reservation
//        assertFalse(responseStringArch.contains("\"id\":\"" + reservation1.getId() + "\""));
//        assertFalse(responseStringArch.contains("\"endTime\":\"2023-12-05T17:03:22\""));
//
//        //Do return
//        RequestSpecification requestPost = RestAssured.given();
//        Response responsePostReturning = requestPost.post(appUrlReservation +
//                "/returnCourt?courtId=%s&date=%s".formatted(court1.getId().toString(), "2023-12-05T17:03:22"));
//
//        System.out.println(responsePostReturning.getBody().asString());
//        assertEquals(204, responsePostReturning.getStatusCode());
//
//        //Check current reservations
//        response = request.get(new URI(appUrlReservation));
//        responseString = response.asString();
//        splitedRespStr = responseString.split("},\\{");
//
//        assertEquals(3, splitedRespStr.length);
//
//        //First Reservation
//        assertFalse(responseString.contains("\"id\":\"" + reservation1.getId() + "\""));
//        assertFalse(responseString.contains("\"endTime\":\"2023-12-05T17:03:22\""));
//
//        //Check archive reservations
//        responseArch = request.get(new URI(appUrlReservation + "/archive"));
//        responseStringArch = responseArch.asString();
//        splitedRespStrArch = responseStringArch.split("},\\{");
//
//        assertEquals(4, splitedRespStrArch.length);
//
//        //First Reservation
//        assertTrue(responseStringArch.contains("\"id\":\"" + reservation1.getId() + "\""));
//        assertTrue(responseStringArch.contains("\"endTime\":\"2023-12-05T17:03:22\""));
//    }
//
//    @Test
//    void returnCourtTestNegInvalidData() throws URISyntaxException {
//        RequestSpecification request = RestAssured.given();
//
//        //Check current reservations
//        Response response = request.get(new URI(appUrlReservation));
//        String responseString = response.asString();
//        String[] splitedRespStr = responseString.split("},\\{");
//
//        assertEquals(4, splitedRespStr.length);
//
//        //First Reservation
//        assertTrue(responseString.contains("\"id\":\"" + reservation1.getId() + "\""));
//        assertFalse(responseString.contains("\"endTime\":\"2023-12-05T17:03:22\""));
//
//        //Check archive reservations
//        Response responseArch = request.get(new URI(appUrlReservation + "/archive"));
//        String responseStringArch = responseArch.asString();
//        String[] splitedRespStrArch = responseStringArch.split("},\\{");
//
//        assertEquals(3, splitedRespStrArch.length);
//
//        //First Reservation
//        assertFalse(responseStringArch.contains("\"id\":\"" + reservation1.getId() + "\""));
//        assertFalse(responseStringArch.contains("\"endTime\":\"2023-12-05T17:03:22\""));
//
//        //Do return
//        RequestSpecification requestPost = RestAssured.given();
//        Response responsePostReturning = requestPost.post(appUrlReservation +
//                "/returnCourt?courtId=%s&date=%s".formatted("XXX", "2023-12-05T17:03:22"));
//
//        assertEquals(400, responsePostReturning.getStatusCode());
//
//        //Check current reservations
//        response = request.get(new URI(appUrlReservation));
//        responseString = response.asString();
//        splitedRespStr = responseString.split("},\\{");
//
//        assertEquals(4, splitedRespStr.length);
//
//        //First Reservation
//        assertTrue(responseString.contains("\"id\":\"" + reservation1.getId() + "\""));
//        assertFalse(responseString.contains("\"endTime\":\"2023-12-05T17:03:22\""));
//
//        //Check archive reservations
//        responseArch = request.get(new URI(appUrlReservation + "/archive"));
//        responseStringArch = responseArch.asString();
//        splitedRespStrArch = responseStringArch.split("},\\{");
//
//        assertEquals(3, splitedRespStrArch.length);
//
//        //First Reservation
//        assertFalse(responseStringArch.contains("\"id\":\"" + reservation1.getId() + "\""));
//        assertFalse(responseStringArch.contains("\"endTime\":\"2023-12-05T17:03:22\""));
//    }
//
//    @Test
//    void returnCourtTestNegBadUUID() throws URISyntaxException {
//        RequestSpecification request = RestAssured.given();
//
//        //Check current reservations
//        Response response = request.get(new URI(appUrlReservation));
//        String responseString = response.asString();
//        String[] splitedRespStr = responseString.split("},\\{");
//
//        assertEquals(4, splitedRespStr.length);
//
//        //First Reservation
//        assertTrue(responseString.contains("\"id\":\"" + reservation1.getId() + "\""));
//        assertFalse(responseString.contains("\"endTime\":\"2023-12-05T17:03:22\""));
//
//        //Check archive reservations
//        Response responseArch = request.get(new URI(appUrlReservation + "/archive"));
//        String responseStringArch = responseArch.asString();
//        String[] splitedRespStrArch = responseStringArch.split("},\\{");
//
//        assertEquals(3, splitedRespStrArch.length);
//
//        //First Reservation
//        assertFalse(responseStringArch.contains("\"id\":\"" + reservation1.getId() + "\""));
//        assertFalse(responseStringArch.contains("\"endTime\":\"2023-12-05T17:03:22\""));
//
//        //Do return
//        RequestSpecification requestPost = RestAssured.given();
//        Response responsePostReturning = requestPost.post(appUrlReservation +
//                "/returnCourt?courtId=%s&date=%s".formatted(UUID.randomUUID().toString(), "2023-12-05T17:03:22"));
//
//        assertEquals(500, responsePostReturning.getStatusCode());
//
//        //Check current reservations
//        response = request.get(new URI(appUrlReservation));
//        responseString = response.asString();
//        splitedRespStr = responseString.split("},\\{");
//
//        assertEquals(4, splitedRespStr.length);
//
//        //First Reservation
//        assertTrue(responseString.contains("\"id\":\"" + reservation1.getId() + "\""));
//        assertFalse(responseString.contains("\"endTime\":\"2023-12-05T17:03:22\""));
//
//        //Check archive reservations
//        responseArch = request.get(new URI(appUrlReservation + "/archive"));
//        responseStringArch = responseArch.asString();
//        splitedRespStrArch = responseStringArch.split("},\\{");
//
//        assertEquals(3, splitedRespStrArch.length);
//
//        //First Reservation
//        assertFalse(responseStringArch.contains("\"id\":\"" + reservation1.getId() + "\""));
//        assertFalse(responseStringArch.contains("\"endTime\":\"2023-12-05T17:03:22\""));
//    }
//
//    @Test
//    void getReservationByIdTest() throws URISyntaxException {
//        RequestSpecification request = RestAssured.given();
//
//        //Retrieve UUID
//        Response responseById = request.get(new URI(appUrlReservation + "/" + reservation2.getId()));
//        String responseByIdString = responseById.asString();
//        String[] splitedRespStr = responseByIdString.split("},\\{");
//
//        assertEquals(1, splitedRespStr.length);
//
//        assertTrue(splitedRespStr[0].contains("\"beginTime\":\"2023-11-30T14:20:00\""));
//        assertTrue(splitedRespStr[0].contains("\"client\":{\""));
//        assertTrue(splitedRespStr[0].contains("\"id\":\"%s\"".formatted(client2.getId())));
//        assertTrue(splitedRespStr[0].contains("\"court\":{\""));
//        assertTrue(splitedRespStr[0].contains("\"id\":\"%s\"".formatted(court2.getId())));
//        assertTrue(splitedRespStr[0].contains("\"id\":\"%s\"".formatted(reservation2.getId())));
//
//        assertEquals(200, responseById.getStatusCode());
//    }
//
//    @Test
//    void getReservationByIdTestNoCont() throws URISyntaxException {
//        RequestSpecification request = RestAssured.given();
//        Response response = request.get(new URI(appUrlReservation + "/" + UUID.randomUUID()));
//        String responseString = response.asString();
//
//        assertTrue(responseString.isEmpty());
//        assertEquals(204, response.getStatusCode());
//    }
//

    @Test
    void getAllClientReservationsTest() throws URISyntaxException {
        RequestSpecification request = RestAssured.given();
        Response response = request.get(new URI(appUrlReservation + "/clientReservation?clientId=" + client3.getId()));
        String responseString = response.asString();
        String[] splitedRespStr = responseString.split("},\\{");

        assertEquals(3, splitedRespStr.length);

        //First Reservation
        assertTrue(splitedRespStr[0].contains("\"beginTime\":\"2023-11-28T14:20:00\""));
        assertTrue(splitedRespStr[0].contains("\"client\":{\""));
        assertTrue(splitedRespStr[0].contains("\"id\":\"%s\"".formatted(client3.getId())));
        assertTrue(splitedRespStr[0].contains("\"court\":{\""));
        assertTrue(splitedRespStr[0].contains("\"id\":\"%s\"".formatted(court3.getId())));
        assertTrue(splitedRespStr[0].contains("\"id\":\"" + reservation3.getId() + "\""));

        //Third Reservation
        assertTrue(splitedRespStr[2].contains("\"beginTime\":\"2023-12-16T10:00:00\""));
        assertTrue(splitedRespStr[2].contains("\"client\":{\""));
        assertTrue(splitedRespStr[2].contains("\"id\":\"%s\"".formatted(client3.getId())));
        assertTrue(splitedRespStr[2].contains("\"court\":{\""));
        assertTrue(splitedRespStr[2].contains("\"id\":\"%s\"".formatted(court5.getId())));
        assertTrue(splitedRespStr[2].contains("\"id\":\"" + reservation7.getId() + "\""));

        assertEquals(200, response.getStatusCode());
    }

    @Test
    void getAllClientReservationsTestNoCont() throws URISyntaxException {
        RequestSpecification request = RestAssured.given();
        Response response = request.get(new URI(appUrlReservation + "/clientReservation?clientId=" + client4.getId()));
        String responseString = response.asString();

        assertTrue(responseString.isEmpty());
        assertEquals(204, response.getStatusCode());
    }

    @Test
    void getClientCurrentReservationsTest() throws URISyntaxException {
        RequestSpecification request = RestAssured.given();
        Response response = request.get(new URI(appUrlReservation + "/clientReservation/current?clientId=" + client1.getId()));
        String responseString = response.asString();
        String[] splitedRespStr = responseString.split("},\\{");

        assertEquals(2, splitedRespStr.length);

        //First Reservation
        assertTrue(splitedRespStr[0].contains("\"beginTime\":\"2023-11-30T14:20:00\""));
        assertTrue(splitedRespStr[0].contains("\"client\":{\""));
        assertTrue(splitedRespStr[0].contains("\"id\":\"%s\"".formatted(client1.getId())));
        assertTrue(splitedRespStr[0].contains("\"court\":{\""));
        assertTrue(splitedRespStr[0].contains("\"id\":\"%s\"".formatted(court1.getId())));
        assertTrue(splitedRespStr[0].contains("\"id\":\"" + reservation1.getId() + "\""));

        //Second Reservation
        assertTrue(splitedRespStr[1].contains("\"beginTime\":\"2023-12-15T10:00:00\""));
        assertTrue(splitedRespStr[1].contains("\"client\":{\""));
        assertTrue(splitedRespStr[1].contains("\"id\":\"%s\"".formatted(client1.getId())));
        assertTrue(splitedRespStr[1].contains("\"court\":{\""));
        assertTrue(splitedRespStr[1].contains("\"id\":\"%s\"".formatted(court3.getId())));
        assertTrue(splitedRespStr[1].contains("\"id\":\"" + reservation6.getId() + "\""));

        assertEquals(200, response.getStatusCode());
    }

    @Test
    void getClientCurrentReservationsTestNoCont() throws URISyntaxException {
        RequestSpecification request = RestAssured.given();
        Response response = request.get(new URI(appUrlReservation + "/clientReservation/current?clientId=" + client4.getId()));
        String responseString = response.asString();

        assertTrue(responseString.isEmpty());
        assertEquals(204, response.getStatusCode());
    }

    @Test
    void getClientEndedReservationsTest() throws URISyntaxException {
        RequestSpecification request = RestAssured.given();
        Response response = request.get(new URI(appUrlReservation + "/clientReservation/ended?clientId=" + client3.getId()));
        String responseString = response.asString();
        String[] splitedRespStr = responseString.split("},\\{");

        assertEquals(2, splitedRespStr.length);

        //First Reservation
        assertTrue(splitedRespStr[0].contains("\"beginTime\":\"2023-11-28T14:20:00\""));
        assertTrue(splitedRespStr[0].contains("\"client\":{\""));
        assertTrue(splitedRespStr[0].contains("\"id\":\"%s\"".formatted(client1.getId())));
        assertTrue(splitedRespStr[0].contains("\"court\":{\""));
        assertTrue(splitedRespStr[0].contains("\"id\":\"%s\"".formatted(court1.getId())));
        assertTrue(splitedRespStr[0].contains("\"id\":\"" + reservation1.getId() + "\""));

        //Second Reservation
        assertTrue(splitedRespStr[1].contains("\"beginTime\":\"2023-12-01T14:20:00\""));
        assertTrue(splitedRespStr[1].contains("\"client\":{\""));
        assertTrue(splitedRespStr[1].contains("\"id\":\"%s\"".formatted(client1.getId())));
        assertTrue(splitedRespStr[1].contains("\"court\":{\""));
        assertTrue(splitedRespStr[1].contains("\"id\":\"%s\"".formatted(court4.getId())));
        assertTrue(splitedRespStr[1].contains("\"id\":\"" + reservation1.getId() + "\""));

        assertEquals(200, response.getStatusCode());
    }

    @Test
    void getClientEndedReservationsTestNoCont() throws URISyntaxException {
        RequestSpecification request = RestAssured.given();
        Response response = request.get(new URI(appUrlReservation + "/clientReservation/ended?clientId=" + client4.getId()));
        String responseString = response.asString();

        assertTrue(responseString.isEmpty());
        assertEquals(204, response.getStatusCode());
    }

    @Test
    void getCourtCurrentReservationTest() throws URISyntaxException {
        RequestSpecification request = RestAssured.given();
        Response response = request.get(new URI(appUrlReservation + "/courtReservation/current?courtId=" + court2.getId()));
        String responseString = response.asString();
        String[] splitedRespStr = responseString.split("},\\{");

        assertEquals(1, splitedRespStr.length);

        assertTrue(splitedRespStr[0].contains("\"beginTime\":\"2023-11-30T14:20:00\""));
        assertTrue(splitedRespStr[0].contains("\"client\":{\""));
        assertTrue(splitedRespStr[0].contains("\"id\":\"%s\"".formatted(client2.getId())));
        assertTrue(splitedRespStr[0].contains("\"court\":{\""));
        assertTrue(splitedRespStr[0].contains("\"id\":\"%s\"".formatted(court2.getId())));
        assertTrue(splitedRespStr[0].contains("\"id\":\"" + reservation2.getId() + "\""));

        assertEquals(200, response.getStatusCode());
    }

    @Test
    void getCourtCurrentReservationTestNoCont() throws URISyntaxException {
        RequestSpecification request = RestAssured.given();
        Response response = request.get(new URI(appUrlReservation + "/courtReservation/current?courtId" + court4.getId()));
        String responseString = response.asString();

        assertTrue(responseString.isEmpty());
        assertEquals(204, response.getStatusCode());
    }

//    @Test
//    void deleteCourtTestNeg() throws URISyntaxException {
//        //Additional preparing
//
//        RequestSpecification requestGet = RestAssured.given();
//        String responseString = requestGet.get(new URI(appUrlReservation)).asString();
//
//        //Retrieve UUID
//        String responseNumber = requestGet.get(new URI(appUrlReservation + "/get?number=1")).asString();
//        int index = responseNumber.indexOf("\"id\":\"") + 6;
//        String courtId = responseNumber.substring(index, index + 36);
//
//        assertTrue(responseString.contains(
//                "\"archive\":false," +
//                        "\"area\":100.0," +
//                        "\"baseCost\":100," +
//                        "\"courtNumber\":1," +
//                        "\"id\":\"" + courtId + "\"," +
//                        "\"rented\":true"));
//
//        RequestSpecification requestDelete = RestAssured.given();
//        Response responseDelete = requestDelete.delete(appUrlCourt + "/delete/" + courtId);
//
//        assertEquals(409, responseDelete.getStatusCode());
//
//        responseString = requestGet.get(new URI(appUrlCourt)).asString();
//
//        assertTrue(responseString.contains(
//                "\"archive\":false," +
//                        "\"area\":100.0," +
//                        "\"baseCost\":100," +
//                        "\"courtNumber\":1," +
//                        "\"id\":\"" + courtId + "\"," +
//                        "\"rented\":true"));
//    }
}
