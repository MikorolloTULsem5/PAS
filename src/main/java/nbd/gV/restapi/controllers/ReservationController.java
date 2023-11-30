package nbd.gV.restapi.controllers;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import nbd.gV.exceptions.CourtException;
import nbd.gV.exceptions.CourtNumberException;
import nbd.gV.exceptions.MultiReservationException;
import nbd.gV.exceptions.MyMongoException;
import nbd.gV.model.reservations.Reservation;
import nbd.gV.restapi.services.ReservationService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Path("/reservations")
@ApplicationScoped
public class ReservationController {

    @Inject
    private ReservationService reservationService;
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/addReservation")
    public Response addReseravtion(@QueryParam("clientId") String clientId, @QueryParam("courtId") String courtId,
                                   @QueryParam("date") String date) {
        try {
            if (date == null) {
                reservationService.makeReservation(UUID.fromString(clientId), UUID.fromString(courtId));
            } else {
                reservationService.makeReservation(UUID.fromString(clientId), UUID.fromString(courtId), LocalDateTime.parse(date));
            }
        } catch (IllegalArgumentException | NullPointerException ex) {
            return Response.status(Response.Status.BAD_REQUEST).entity(ex.getMessage()).build();
        } catch (MultiReservationException cne) {
            return Response.status(Response.Status.CONFLICT).entity(cne.getMessage()).build();
        } catch (Exception ce) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ce.getMessage()).build();
        }

        return Response.status(Response.Status.CREATED).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Reservation> getAllCurrentReservations() {
        List<Reservation> resultList = reservationService.getAllCurrentReservations();
        if (resultList.isEmpty()) {
            resultList = null;
        }
        return resultList;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/archive")
    public List<Reservation> getAllArchiveReservations() {
        List<Reservation> resultList = reservationService.getAllArchiveReservations();
        if (resultList.isEmpty()) {
            resultList = null;
        }
        return resultList;
    }
}
