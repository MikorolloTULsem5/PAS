package pas.gV.restapi.services;

import com.mongodb.client.model.Filters;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.NoArgsConstructor;
import pas.gV.model.courts.Court;
import pas.gV.model.reservations.Reservation;
import pas.gV.model.users.Client;
import pas.gV.exceptions.MyMongoException;
import pas.gV.exceptions.ReservationException;
import pas.gV.data.repositories.ReservationMongoRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
@NoArgsConstructor
public class ReservationService {
    @Inject
    private ReservationMongoRepository reservationRepository;

    public ReservationService(ReservationMongoRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public Reservation makeReservation(UUID clientId, UUID courtId, LocalDateTime beginTime) {
        try {
            Reservation newReservation = reservationRepository.create(
                    new Reservation(null,
                                    new Client(clientId, "", "", "", ""),
                                    new Court(courtId, 0, 0,0),
                                    beginTime));
            if (newReservation == null) {
                throw new ReservationException("Nie udalo sie utworzyc rezerwacji! - brak odpowiedzi");
            }
            return newReservation;
        } catch (MyMongoException exception) {
            throw new ReservationException("Nie udalo sie utworzyc rezerwacji - " + exception.getMessage());
        }
    }

    public Reservation makeReservation(UUID clientId, UUID courtId) {
        return makeReservation(clientId, courtId, LocalDateTime.now());
    }

    public void returnCourt(UUID courtId, LocalDateTime endTime) {
        try {
            reservationRepository.update(courtId, endTime);
        } catch (MyMongoException exception) {
            throw new ReservationException("Blad transakcji. - " + exception.getMessage());
        }
    }

    public void returnCourt(UUID courtId) {
        returnCourt(courtId, LocalDateTime.now());
    }

    public Reservation getReservationById(UUID uuid) {
        return reservationRepository.readByUUID(uuid);
    }

    public List<Reservation> getAllCurrentReservations() {
        return reservationRepository.read(Filters.eq("endtime", null));
    }

    ///TODO
    public List<Reservation> getAllArchiveReservations() {
        return reservationRepository.read(Filters.not(Filters.eq("endtime", null)));
    }

    public List<Reservation> getAllClientReservations(UUID clientId) {
        return reservationRepository.read(Filters.eq("clientid", clientId.toString()));
    }

    public List<Reservation> getClientCurrentReservations(UUID clientId) {
        return reservationRepository.read(Filters.and(
                Filters.eq("clientid", clientId.toString()),
                Filters.eq("endtime", null)));
    }

    ///TODO
    public List<Reservation> getClientEndedReservations(UUID clientId) {
        return reservationRepository.read(Filters.and(
                Filters.eq("clientid", clientId.toString()),
                Filters.not(Filters.eq("endtime", null))));
    }

    public Reservation getCourtCurrentReservation(UUID courtId) {
        var list = reservationRepository.read(
                Filters.and(Filters.eq("courtid", courtId.toString()),
                            Filters.eq("endtime", null)));
        return !list.isEmpty() ? list.get(0) : null;
    }

    public List<Reservation> getCourtEndedReservation(UUID courtId) {
        return reservationRepository.read(Filters.and(
                Filters.eq("courtid", courtId.toString()),
                Filters.ne("endtime", null)));
    }

    public void deleteReservation(UUID reservationId) {
        try {
            reservationRepository.delete(reservationId);
        } catch (IllegalStateException e) {
            throw new ReservationException("Nie mozna usunac zakonczonej rezerwacji");
        } catch (Exception exception) {
            throw new MyMongoException("Nie udalo sie usunac podanej rezerwacji. - " + exception.getMessage());
        }
    }

    public double checkClientReservationBalance(UUID clientId) {
        double sum = 0;
        List<Reservation> reservationList = getClientEndedReservations(clientId);
        for (Reservation reservation : reservationList) {
            sum += reservation.getReservationCost();
        }
        return sum;
    }
}