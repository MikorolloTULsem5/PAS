package nbd.gV.restapi.controllers;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import nbd.gV.exceptions.CourtException;
import nbd.gV.exceptions.CourtNumberException;
import nbd.gV.model.courts.Court;
import nbd.gV.restapi.services.CourtService;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Path("/courts")
@ApplicationScoped
public class CourtController {

    @Inject
    private CourtService courtService;
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/addCourt")
    public Response addCourt(Court court) {
        Set<ConstraintViolation<Court>> violations = validator.validate(court);
        List<String> errors = violations.stream().map(ConstraintViolation::getMessage).collect(Collectors.toList());
        if (!violations.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity(errors).build();
        }

        try {
            courtService.registerCourt(court.getArea(), court.getBaseCost(), court.getCourtNumber());
        } catch (CourtNumberException cne) {
            return Response.status(Response.Status.CONFLICT).entity(cne.getMessage()).build();
        } catch (CourtException ce) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ce.getMessage()).build();
        }

        return Response.status(Response.Status.CREATED).build();
    }

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
    public Court getCourtByCourtNumber(@QueryParam("number") String number) {
        return courtService.getCourtByCourtNumber(Integer.parseInt(number));
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/modifyCourt/{id}")
    public Response modifyCourt(@PathParam("id") String id, Court modifiedCourt) {
        Set<ConstraintViolation<Court>> violations = validator.validate(modifiedCourt);
        List<String> errors = violations.stream().map(ConstraintViolation::getMessage).collect(Collectors.toList());
        if (!violations.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity(errors).build();
        }

        try {
            Court finalModifyCourt = new Court(UUID.fromString(id), modifiedCourt.getArea(), modifiedCourt.getBaseCost(),
                    modifiedCourt.getCourtNumber());
            finalModifyCourt.setArchive(modifiedCourt.isArchive());
            finalModifyCourt.setRented(modifiedCourt.isRented());
            courtService.modifyCourt(finalModifyCourt);
        } catch (CourtNumberException cne) {
            return Response.status(Response.Status.CONFLICT).entity(cne.getMessage()).build();
        } catch (CourtException ce) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ce.getMessage()).build();
        }

        return Response.status(Response.Status.CREATED).build();
    }
}
