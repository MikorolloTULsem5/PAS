package pas.gV.restapi.controllers;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pas.gV.model.exceptions.MultiReservationException;
import pas.gV.model.exceptions.MyMongoException;
import pas.gV.model.exceptions.ReservationException;

import pas.gV.restapi.data.dto.ReservationDTO;
import pas.gV.restapi.security.services.JwsService;
import pas.gV.restapi.services.ReservationService;
import pas.gV.restapi.services.userservice.ClientService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService reservationService;
    private final ClientService clientService;
    private final JwsService jwsService;

    @Autowired
    public ReservationController(ReservationService reservationService, ClientService clientService, JwsService jwsService) {
        this.reservationService = reservationService;
        this.clientService = clientService;
        this.jwsService = jwsService;
    }

    @PostMapping("/addReservation")
    public ResponseEntity<String> addReservation(@RequestParam("clientId") String clientId, @RequestParam("courtId") String courtId,
                                                 @RequestParam(value = "date", required = false) String date) {
        try {
            if (date == null) {
                reservationService.makeReservation(UUID.fromString(clientId), UUID.fromString(courtId));
            } else {
                reservationService.makeReservation(UUID.fromString(clientId), UUID.fromString(courtId), LocalDateTime.parse(date));
            }
        } catch (IllegalArgumentException | NullPointerException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } catch (MultiReservationException cne) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(cne.getMessage());
        } catch (Exception ce) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ce.getMessage());
        }

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public List<ReservationDTO> getAllCurrentReservations(HttpServletResponse response) {
        List<ReservationDTO> resultList = reservationService.getAllCurrentReservations();
        if (resultList.isEmpty()) {
            resultList = null;
            response.setStatus(HttpStatus.NO_CONTENT.value());
        }
        return resultList;
    }

    @GetMapping("/archive")
    public List<ReservationDTO> getAllArchiveReservations(HttpServletResponse response) {
        List<ReservationDTO> resultList = reservationService.getAllArchiveReservations();
        if (resultList.isEmpty()) {
            resultList = null;
            response.setStatus(HttpStatus.NO_CONTENT.value());
        }
        return resultList;
    }

    @PostMapping("/returnCourt")
    public ResponseEntity<String> returnCourt(@RequestParam("courtId") String courtId, @RequestParam(value = "date", required = false) String date) {
        try {
            if (date == null) {
                reservationService.returnCourt(UUID.fromString(courtId));
            } else {
                reservationService.returnCourt(UUID.fromString(courtId), LocalDateTime.parse(date));
            }
        } catch (IllegalArgumentException iae) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(iae.getMessage());
        } catch (Exception ce) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ce.getMessage());
        }

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/{id}")
    public ReservationDTO getReservationById(@PathVariable("id") String id, HttpServletResponse response) {
        ReservationDTO reservation = reservationService.getReservationById(id);
        if (reservation == null) {
            response.setStatus(HttpStatus.NO_CONTENT.value());
        }
        return reservation;
    }

    @GetMapping("/clientReservation")
    public List<ReservationDTO> getAllClientReservations(@RequestParam("clientId") String clientId, HttpServletResponse response) {
        List<ReservationDTO> resultList = reservationService.getAllClientReservations(UUID.fromString(clientId));
        if (resultList.isEmpty()) {
            resultList = null;
            response.setStatus(HttpStatus.NO_CONTENT.value());
        }
        return resultList;
    }

    @GetMapping("/clientReservation/current")
    public List<ReservationDTO> getClientCurrentReservations(@RequestParam("clientId") String clientId, HttpServletResponse response) {
        List<ReservationDTO> resultList = reservationService.getClientCurrentReservations(UUID.fromString(clientId));
        if (resultList.isEmpty()) {
            resultList = null;
            response.setStatus(HttpStatus.NO_CONTENT.value());
        }
        return resultList;
    }

    @GetMapping("/clientReservation/ended")
    public List<ReservationDTO> getClientEndedReservations(@RequestParam("clientId") String clientId, HttpServletResponse response) {
        List<ReservationDTO> resultList = reservationService.getClientEndedReservations(UUID.fromString(clientId));
        if (resultList.isEmpty()) {
            resultList = null;
            response.setStatus(HttpStatus.NO_CONTENT.value());
        }
        return resultList;
    }

    @GetMapping("/courtReservation/current")
    public ReservationDTO getCourtCurrentReservation(@RequestParam("courtId") String courtId, HttpServletResponse response) {
        ReservationDTO reservation = reservationService.getCourtCurrentReservation(UUID.fromString(courtId));
        if (reservation == null) {
            response.setStatus(HttpStatus.NO_CONTENT.value());
        }
        return reservation;
    }

    @GetMapping("/courtReservation/ended")
    public List<ReservationDTO> getCourtEndedReservation(@RequestParam("courtId") String courtId, HttpServletResponse response) {
        List<ReservationDTO> resultList = reservationService.getCourtEndedReservation(UUID.fromString(courtId));
        if (resultList.isEmpty()) {
            resultList = null;
            response.setStatus(HttpStatus.NO_CONTENT.value());
        }
        return resultList;
    }

    @GetMapping("/clientBalance")
    public double checkClientReservationBalance(@RequestParam("clientId") String clientId) {
        return reservationService.checkClientReservationBalance(UUID.fromString(clientId));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteReservation(@PathVariable("id") String id) {
        try {
            reservationService.deleteReservation(UUID.fromString(id));
        } catch (ReservationException re) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(re.getMessage());
        } catch (MyMongoException mme) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mme.getMessage());
        }

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /*---------------------------------------FOR CLIENT-------------------------------------------------------------*/
    @PostMapping("/addReservation/me")
    public ResponseEntity<String> addReservationByClient(@RequestParam("courtId") String courtId,
                                                 @RequestParam(value = "date", required = false) String date) {
        String clientId = clientService.getClientByLogin(
                SecurityContextHolder.getContext().getAuthentication().getName()).getId();
        return addReservation(clientId, courtId, date);
    }

    @GetMapping("/clientReservation/me")
    public List<ReservationDTO> getAllClientReservationsByClient(HttpServletResponse response) {
        String clientId = clientService.getClientByLogin(
                SecurityContextHolder.getContext().getAuthentication().getName()).getId();
        return getAllClientReservations(clientId, response);
    }

    @PostMapping("/returnCourt/me")
    public ResponseEntity<String> returnCourtByClient(@RequestParam("courtId") String courtId, @RequestParam(value = "date", required = false) String date) {
        String clientId = clientService.getClientByLogin(
                SecurityContextHolder.getContext().getAuthentication().getName()).getId();

        ReservationDTO reservation = reservationService.getCourtCurrentReservation(UUID.fromString(courtId));
        if (reservation == null || !reservation.getClient().getId().equals(clientId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("To boisko nie jest wypozyczone przez aktualnego uzytkownika");
        }

        return returnCourt(courtId, date);
    }
}