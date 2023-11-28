package integrationtests;

import com.mongodb.client.model.Filters;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import jakarta.inject.Inject;
import nbd.gV.data.datahandling.dto.ClientDTO;
import nbd.gV.data.datahandling.dto.ReservationDTO;
import nbd.gV.data.repositories.CourtMongoRepository;
import nbd.gV.data.repositories.ReservationMongoRepository;
import nbd.gV.data.repositories.UserMongoRepository;
import nbd.gV.restapi.services.userservice.ClientService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserControllerTests {
    @Inject
    ClientService clientServiceTest;
    @Inject
    ReservationMongoRepository reservationRepository;
    @Inject
    UserMongoRepository clientRepository;
    @Inject
    CourtMongoRepository courtRepository;

    void init() {
        clientServiceTest.registerClient("Adam", "Smith", "siemaszka", "normal");
        clientServiceTest.registerClient("Eva", "Braun", "siemaszka13", "athlete");
        clientServiceTest.registerClient("Michal", "Pi", "michas13", "coach");
    }

    @BeforeAll
    @AfterAll
    void clean() {
        reservationRepository.getDatabase().getCollection(reservationRepository.getCollectionName(),
                ReservationDTO.class).deleteMany(Filters.empty());
        clientRepository.readAll(ClientDTO.class).forEach((mapper) -> clientRepository.delete(UUID.fromString(mapper.getId())));
        courtRepository.readAll().forEach((mapper) -> courtRepository.delete(UUID.fromString(mapper.getCourtId())));
    }

    @BeforeEach
    void cleanDatabase() {
        clean();
        init();
    }

    @Test
    void getAllClientsTest() throws URISyntaxException {
        RequestSpecification request = RestAssured.given();
//        request.relaxedHTTPSValidation();
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
        assertTrue(responseString.contains("\"archive\":false"));
        assertTrue(responseString.contains("\"id\":\""));
        assertTrue(responseString.contains("\"login\":\"michas13\""));
        assertTrue(responseString.contains("\"clientTypeName\":\"coach\""));
        assertTrue(responseString.contains("\"firstName\":\"Michal\""));
        assertTrue(responseString.contains("\"lastName\":\"Pi\""));

        assertEquals(200, response.getStatusCode());
    }

    @Test
    void getAllClientsTestNoCont() throws URISyntaxException {
        RequestSpecification request = RestAssured.given();
        Response response = request.get(new URI("http://localhost:8080/CourtRent-1.0-SNAPSHOT/api/users"));
        String responseString = response.asString();

        assertTrue(responseString.isEmpty());
        assertEquals(204, response.getStatusCode());
    }

    @Test
    void createClientTestPos() {

    }
}
