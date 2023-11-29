package nbd.gV.restapi.controllers;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import nbd.gV.model.courts.Court;
import nbd.gV.model.users.Client;
import nbd.gV.restapi.services.CourtService;

import java.util.List;
import java.util.UUID;

@Path("/courts")
@ApplicationScoped
public class CourtController {

    @Inject
    private CourtService courtService;
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Court> getAllCourts() {
        List<Court> resultList = courtService.getAllCourts();
        if (resultList.isEmpty()) {
            resultList = null;
        }
        return resultList;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.TEXT_PLAIN)
    @Path("/{id}")
    public Court getCourtById(@PathParam("id") String id) {
        return courtService.getCourtById(UUID.fromString(id));
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.TEXT_PLAIN)
    @Path("/get")
    public Court getCourtByCourtNumber(@QueryParam("number") int number) {
        return courtService.getCourtByCourtNumber(number);
    }
}
