package nbd.gV.restapi.controllers;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import nbd.gV.exceptions.CourtException;
import nbd.gV.exceptions.MultiReservationException;
import nbd.gV.exceptions.MyMongoException;
import nbd.gV.exceptions.ReservationException;
import nbd.gV.model.reservations.Reservation;
import nbd.gV.restapi.services.ReservationService;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.UUID;

@Path("/reservations")
@ApplicationScoped
public class ReservationController {

    @Inject
    private ReservationService reservationService;

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

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/returnCourt")
    public Response returnCourt(@QueryParam("courtId") String courtId, @QueryParam("date") String date) {
        try {
            if (date == null) {
                reservationService.returnCourt(UUID.fromString(courtId));
            } else {
                reservationService.returnCourt(UUID.fromString(courtId), LocalDateTime.parse(date));
            }
        } catch (IllegalArgumentException iae) {
            return Response.status(Response.Status.BAD_REQUEST).entity(iae.getMessage()).build();
        } catch (Exception ce) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ce.getMessage()).build();
        }

        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.TEXT_PLAIN)
    @Path("/{id}")
    public Reservation getReservationById(@PathParam("id") String id) {
        return reservationService.getReservationById(UUID.fromString(id));
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/clientReservation")
    public List<Reservation> getAllClientReservations(@QueryParam("clientId") String clientId) {
        List<Reservation> resultList = reservationService.getAllClientReservations(UUID.fromString(clientId));
        if (resultList.isEmpty()) {
            resultList = null;
        }
        return resultList;
    }

    ///TODO test 3
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/clientReservation/current")
    public List<Reservation> getClientCurrentReservations(@QueryParam("clientId") String clientId) {
        List<Reservation> resultList = reservationService.getClientCurrentReservations(UUID.fromString(clientId));
        if (resultList.isEmpty()) {
            resultList = null;
        }
        return resultList;
    }

    ///TODO test 4
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/clientReservation/ended")
    public List<Reservation> getClientEndedReservations(@QueryParam("clientId") String clientId) {
        List<Reservation> resultList = reservationService.getClientEndedReservations(UUID.fromString(clientId));
        if (resultList.isEmpty()) {
            resultList = null;
        }
        return resultList;
    }

    ///TODO test 5
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/courtReservation/current")
    public Reservation getCourtCurrentReservation(@QueryParam("courtId") String courtId) {
        return reservationService.getCourtCurrentReservation(UUID.fromString(courtId));
    }

    ///TODO test 6
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/courtReservation/ended")
    public List<Reservation> getCourtEndedReservation(@QueryParam("courtId") String courtId) {
        List<Reservation> resultList = reservationService.getCourtEndedReservation(UUID.fromString(courtId));
        if (resultList.isEmpty()) {
            resultList = null;
        }
        return resultList;
    }

    ///TODO test 7
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/clientBalance")
    public double checkClientReservationBalance(@QueryParam("clientId") String clientId) {
        return reservationService.checkClientReservationBalance(UUID.fromString(clientId));
    }

    ///TODO test 8
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/delete/{id}")
    public Response deleteReservation(@PathParam("id") String id) {
        try {
            reservationService.deleteReservation(UUID.fromString(id));
        } catch (ReservationException re) {
            return Response.status(Response.Status.CONFLICT).entity(re.getMessage()).build();
        } catch (MyMongoException mme) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(mme.getMessage()).build();
        }

        return Response.status(Response.Status.NO_CONTENT).build();
    }

}