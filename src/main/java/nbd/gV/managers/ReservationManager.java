package nbd.gV.managers;

import com.mongodb.client.model.Filters;

import nbd.gV.data.dto.ClientDTO;
import nbd.gV.data.mappers.ClientMapper;
import nbd.gV.data.mappers.CourtMapper;
import nbd.gV.exceptions.MainException;
import nbd.gV.reservations.Reservation;
import nbd.gV.users.Client;
import nbd.gV.courts.Court;
import nbd.gV.exceptions.MyMongoException;
import nbd.gV.exceptions.ReservationException;
import nbd.gV.data.dto.ReservationDTO;
import nbd.gV.data.mappers.ReservationMapper;
import nbd.gV.repositories.UserMongoRepository;
import nbd.gV.repositories.CourtMongoRepository;
import nbd.gV.repositories.ReservationMongoRepository;
import org.bson.conversions.Bson;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ReservationManager {
    private final ReservationMongoRepository reservationRepository;
    private final UserMongoRepository clientsRepository;
    private final CourtMongoRepository courtMongoRepository;

    public ReservationManager() {
        reservationRepository = new ReservationMongoRepository();

        ///TODO do wywalenia
        clientsRepository = new UserMongoRepository();
        courtMongoRepository = new CourtMongoRepository();
    }

    public Reservation makeReservation(UUID clientId, UUID courtId, LocalDateTime beginTime) {
        try {
            Client client = ClientMapper.fromMongoUser((ClientDTO) clientsRepository.readByUUID(clientId, ClientDTO.class));
            Court court = CourtMapper.fromMongoCourt(courtMongoRepository.readByUUID(courtId));

            Reservation newReservation = new Reservation(client, court, beginTime);
            boolean result = reservationRepository.create(ReservationMapper.toMongoReservation(newReservation));
            if (!result) {
                throw new ReservationException("Nie udalo sie utworzyc transkacji!");
            }
            court.setRented(true);
            return newReservation;
        } catch (MyMongoException exception) {
            throw new ReservationException("Blad transakcji.");
        }
    }

    public Reservation makeReservation(UUID clientId, UUID courtId) {
        return makeReservation(clientId, courtId, LocalDateTime.now());
    }

    public void returnCourt(Court court, LocalDateTime endTime) {
        if (court == null) {
            throw new MainException("Nie mozna zwrocic nieistniejacego boiska!");
        } else {
            try {
                reservationRepository.update(court, endTime);
            } catch (MyMongoException exception) {
                throw new ReservationException("Blad transakcji.");
            }
        }
    }

    public void returnCourt(Court court) {
        returnCourt(court, LocalDateTime.now());
    }

    private List<Reservation> getReservationsWithBsonFilter(Bson filter) {
        List<Reservation> reservations = new ArrayList<>();
        var clientsRepo = new UserMongoRepository();
        var courtsRepo = new CourtMongoRepository();
        for (var r : reservationRepository.read(filter)) {
            reservations.add(ReservationMapper.fromMongoReservation(r,
                    (ClientDTO) clientsRepo.readByUUID(UUID.fromString(r.getClientId()), ClientDTO.class),
                    courtsRepo.readByUUID(UUID.fromString(r.getCourtId()))));
        }
        return reservations;
    }

    ///TODO przetestowac co gdy UUID jest zly
    public List<Reservation> getAllClientReservations(UUID clientId) {
        return getReservationsWithBsonFilter(Filters.eq("clientid", clientId.toString()));
    }

    public List<Reservation> getClientCurrentReservations(UUID clientId) {
        return getReservationsWithBsonFilter(Filters.and(
                Filters.eq("clientid", clientId.toString()),
                Filters.eq("endtime", null)));
    }

    public List<Reservation> getClientEndedReservations(UUID clientId) {
        return getReservationsWithBsonFilter(Filters.and(
                Filters.eq("clientid", clientId.toString()),
                Filters.not(Filters.eq("endtime", null))));
    }

    public Reservation getCourtCurrentReservation(UUID courtId) {
        var list = getReservationsWithBsonFilter(
                Filters.eq("courtid", courtId.toString()));
        return !list.isEmpty() ? list.get(0) : null;
    }

    ///TODO obtestowac

    public List<Reservation> getCourtEndedReservation(UUID courtId) {
        return getReservationsWithBsonFilter(Filters.and(
                Filters.eq("courtid", courtId.toString()),
                Filters.not(Filters.eq("endtime", null))));
    }

    public double checkClientReservationBalance(UUID clientId) {
        double sum = 0;
        List<Reservation> reservationList = getClientEndedReservations(clientId);
        for (Reservation reservation : reservationList) {
            sum += reservation.getReservationCost();
        }
        return sum;
    }

    public List<Reservation> getAllCurrentReservations() {
        return getReservationsWithBsonFilter(Filters.eq("endtime", null));
    }

    public List<Reservation> getAllArchiveReservations() {
        return getReservationsWithBsonFilter(Filters.not(Filters.eq("endtime", null)));
    }

    public Reservation getReservationByID(UUID uuid) {
        ReservationDTO reservationMapper = reservationRepository.readByUUID(uuid);
        return ReservationMapper.fromMongoReservation(reservationMapper,
                (ClientDTO) clientsRepository.readByUUID(UUID.fromString(reservationMapper.getClientId()), ClientDTO.class),
                courtMongoRepository.readByUUID(UUID.fromString(reservationMapper.getCourtId())));
    }
}
