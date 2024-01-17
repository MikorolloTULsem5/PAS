package nbd.gv.mvc.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import jakarta.annotation.PostConstruct;

import jakarta.faces.view.ViewScoped;
import lombok.Getter;

import nbd.gv.mvc.model.Court;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@ViewScoped
@Component(value = "courtController")
public class CourtController {
    Logger logger = LoggerFactory.getLogger(CourtController.class);
    private static final String appUrlCourt = "http://localhost:8080/api/courts";
    @Getter
    private List<Court> listOfCourts = new ArrayList<>();
    @Getter
    private int statusCode = 0;

    @PostConstruct
    private void init() {
        readAllCourts();
    }

    public void readAllCourts() {
        RequestSpecification request = RestAssured.given();
        Response response = request.get(appUrlCourt);

        //Alternative way
//        listOfCourts = new ArrayList<>(response.jsonPath().getList(".", Court.class));

        ObjectMapper objectMapper = new ObjectMapper();
        TypeReference<List<Court>> listType = new TypeReference<>() {};
        try {
            listOfCourts = objectMapper.readValue(response.asString(), listType);
        } catch (Exception jpe) {
            logger.error(jpe.getMessage());
        }
    }
}
