package pas.gV.mvc.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import lombok.Getter;

import pas.gV.mvc.model.Reservation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@ViewScoped
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

    public void addReservation() {
        String message;

        if (reservation.getClient() == null || reservation.getClient().getId() == null) {
            message = "Cannot reserve a court without choosing client";
            logger.warn(message);
            MessageView.warn(message);
            return;
        }
        RequestSpecification request = RestAssured.given();

        statusCode = 0;
        Response response = request.post(appUrlReservation +
                "/addReservation?clientId=%s&courtId=%s&date=%s".formatted(reservation.getClient().getId(),
                        reservation.getCourt().getId(), LocalDateTime.now().toString()));
        statusCode = response.statusCode();

        if (statusCode == 409) {
            message = "Error occurred: " + response.asString();
            logger.error(message);
            MessageView.error(message);
        } else if (statusCode == 201) {
            message = "Court (%s) reserved".formatted(reservation.getCourt().getId());
            logger.info(message);
            MessageView.info(message);
        } else {
            message = "Cannot to reserved a court";
            logger.warn(message + "; Returned HTTP code: " + statusCode);
            MessageView.warn(message);
        }
    }

    public void endReservation() {
        RequestSpecification request = RestAssured.given();
        Response response = request.post(appUrlReservation +
                "/returnCourt?courtId=%s&date=%s".formatted(reservation.getCourt().getId(), LocalDateTime.now().toString()));
        statusCode = response.statusCode();

        String message;
        if (statusCode == 500) {
            message = "Error occurred: " + response.asString();
            logger.error(message);
            MessageView.error(message);
        } else if (statusCode == 204) {
            message = "Court (%s) returned".formatted(reservation.getCourt().getId());
            logger.info(message);
            MessageView.info(message);
        } else {
            logger.warn("Cannot to return a court; Returned HTTP code: " + statusCode);
        }
    }

    public void readAllReservations() {
        if (!listOfReservation.isEmpty()) {
            listOfReservation = new ArrayList<>();
        }

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
