package nbd.gV.restapi.services;

import com.mongodb.client.model.Filters;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.NoArgsConstructor;
import nbd.gV.data.datahandling.dto.ClientDTO;
import nbd.gV.data.datahandling.mappers.ClientMapper;
import nbd.gV.data.datahandling.mappers.CourtMapper;
import nbd.gV.model.reservations.Reservation;
import nbd.gV.model.users.Client;
import nbd.gV.model.courts.Court;
import nbd.gV.exceptions.MyMongoException;
import nbd.gV.exceptions.ReservationException;
import nbd.gV.data.datahandling.dto.ReservationDTO;
import nbd.gV.data.datahandling.mappers.ReservationMapper;
import nbd.gV.data.repositories.UserMongoRepository;
import nbd.gV.data.repositories.CourtMongoRepository;
import nbd.gV.data.repositories.ReservationMongoRepository;
import org.bson.conversions.Bson;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
@NoArgsConstructor
public class ReservationService {
    @Inject
    private ReservationMongoRepository reservationRepository;
    @Inject
    private UserMongoRepository clientsRepository;
    @Inject
    private CourtMongoRepository courtMongoRepository;

    public ReservationService(ReservationMongoRepository reservationRepository) {
        this.reservationRepository = reservationRepository;

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
                throw new ReservationException("Nie udalo sie utworzyc rezerwacji! - brak odpowiedzi");
            }
            court.setRented(true);
            return newReservation;
        } catch (MyMongoException exception) {
            throw new ReservationException("Nie udalo sie utworzyc rezerwacji");
        }
    }

    public Reservation makeReservation(UUID clientId, UUID courtId) {
        return makeReservation(clientId, courtId, LocalDateTime.now());
    }

    public void returnCourt(UUID courtId, LocalDateTime endTime) {
        try {
            Court court = CourtMapper.fromMongoCourt(courtMongoRepository.readByUUID(courtId));
            reservationRepository.update(court, endTime);
        } catch (MyMongoException exception) {
            throw new ReservationException("Blad transakcji.");
        }
    }

    public void returnCourt(UUID courtId) {
        returnCourt(courtId, LocalDateTime.now());
    }

    public Reservation getReservationById(UUID uuid) {
        ReservationDTO reservationMapper = reservationRepository.readByUUID(uuid);
        return ReservationMapper.fromMongoReservation(reservationMapper,
                (ClientDTO) clientsRepository.readByUUID(UUID.fromString(reservationMapper.getClientId()), ClientDTO.class),
                courtMongoRepository.readByUUID(UUID.fromString(reservationMapper.getCourtId())));
    }

    public List<Reservation> getAllCurrentReservations() {
        return getReservationsWithBsonFilter(Filters.eq("endtime", null));
    }

    public List<Reservation> getAllArchiveReservations() {
        return getReservationsWithBsonFilter(Filters.not(Filters.eq("endtime", null)));
    }

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


    private List<Reservation> getReservationsWithBsonFilter(Bson filter) {
        List<Reservation> reservations = new ArrayList<>();
        var clientsRepo = clientsRepository;
        var courtsRepo = courtMongoRepository;
        for (var r : reservationRepository.read(filter)) {
            reservations.add(ReservationMapper.fromMongoReservation(r,
                    (ClientDTO) clientsRepo.readByUUID(UUID.fromString(r.getClientId()), ClientDTO.class),
                    courtsRepo.readByUUID(UUID.fromString(r.getCourtId()))));
        }
        return reservations;
    }
}
