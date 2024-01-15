package nbd.gv.mvc.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import jakarta.annotation.PostConstruct;
import lombok.Getter;

import nbd.gv.mvc.model.Reservation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Scope(value = "session")
@Component(value = "reservationController")
public class ReservationsController {
    Logger logger = LoggerFactory.getLogger(ReservationsController.class);
    private static final String appUrlReservation = "http://localhost:8080/api/reservations";
    @Getter
    private List<Reservation> listOfReservation = new ArrayList<>();
    @Getter
    private Reservation reservation = new Reservation();
    @PostConstruct
    private void init() {
        readAllReservations();
    }

//    public void addClient() {
//        String JSON = """
//                {
//                  "firstName": "%s",
//                  "lastName": "%s",
//                  "login": "%s",
//                  "password": "%s"
//                }
//                """.formatted(client.getFirstName(), client.getLastName(), client.getLogin(), client.getPassword());
//        RequestSpecification request = RestAssured.given();
//        request.contentType("application/json");
//        request.body(JSON);
//        Response response = request.post(appUrlClient + "/addClient");
//    }

    public void readAllReservations() {
        RequestSpecification request = RestAssured.given();
        Response response = request.get(appUrlReservation);

        //Alternative way
//        listOfReservations = new ArrayList<>(response.jsonPath().getList(".", Reservation.class));

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        TypeReference<List<Reservation>> listType = new TypeReference<>() {};
        //Get current reservations
        try {
            listOfReservation = objectMapper.readValue(response.asString(), listType);
        } catch (Exception jpe) {
            logger.error(jpe.getMessage());
        }

        //Add archive reservations
        response = request.get(appUrlReservation + "/archive");
        try {
            listOfReservation.addAll(objectMapper.readValue(response.asString(), listType));
        } catch (Exception jpe) {
            logger.error(jpe.getMessage());
        }
    }
}
