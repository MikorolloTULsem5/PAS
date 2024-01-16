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

import java.time.LocalDateTime;
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
    @Getter
    private int statusCode = 0;

    @PostConstruct
    private void init() {
        readAllReservations();
    }

    public void test() {
        logger.warn(reservation.getCourt() == null ? null : reservation.getCourt().getId());
    }

    public void addReservation() {
        RequestSpecification request = RestAssured.given();

        statusCode = 0;
        Response response = request.post(appUrlReservation +
                "/addReservation?clientId=%s&courtId=%s&date=%s".formatted("80e62401-6517-4392-856c-e22ef5f3d6a2",
                        reservation.getCourt().getId(), LocalDateTime.now().toString()));
        statusCode = response.statusCode();

        if (response.statusCode() == 500) {
            logger.error("Error occurred: " + response.asString());
        } else if (response.statusCode() == 201) {
            logger.info("Court (%s) reserved".formatted(reservation.getCourt().getId()));
        } else {
            logger.warn("Cannot to reserved a court; Returned HTTP code: " + response.statusCode());
        }
    }

    public void endReservation() {
        RequestSpecification requestReturn = RestAssured.given();
        Response responseReturn = requestReturn.post(appUrlReservation +
                "/returnCourt?courtId=%s&date=%s".formatted(reservation.getCourt().getId(), LocalDateTime.now().toString()));

        if (responseReturn.statusCode() == 500) {
            logger.error("Error occurred: " + responseReturn.asString());
        } else if (responseReturn.statusCode() == 204) {
            logger.info("Court (%s) returned".formatted(reservation.getCourt().getId()));
        } else {
            logger.warn("Cannot to return a court; Returned HTTP code: " + responseReturn.statusCode());
        }
    }

    public void readAllReservations() {
        RequestSpecification request = RestAssured.given();
        Response response = request.get(appUrlReservation);

        //Alternative way
//        listOfReservations = new ArrayList<>(response.jsonPath().getList(".", Reservation.class));

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        TypeReference<List<Reservation>> listType = new TypeReference<>() {
        };
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
