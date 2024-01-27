package pas.gV.restapi.services;

import com.mongodb.client.model.Filters;

import lombok.NoArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pas.gV.model.data.repositories.ReservationMongoRepository;
import pas.gV.model.logic.courts.Court;
import pas.gV.model.logic.reservations.Reservation;
import pas.gV.model.logic.users.Client;
import pas.gV.model.exceptions.MyMongoException;
import pas.gV.model.exceptions.ReservationException;

import pas.gV.restapi.data.dto.ReservationDTO;
import pas.gV.restapi.data.mappers.ReservationMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@NoArgsConstructor
public class ReservationService {

    private ReservationMongoRepository reservationRepository;

    @Autowired
    public ReservationService(ReservationMongoRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public ReservationDTO makeReservation(UUID clientId, UUID courtId, LocalDateTime beginTime) {
        try {
            Reservation newReservation = reservationRepository.create(
                    new Reservation(null,
                            new Client(clientId, "", "", "", "", ""),
                            new Court(courtId, 0, 0, 0),
                            beginTime));
            if (newReservation == null) {
                throw new ReservationException("Nie udalo sie utworzyc rezerwacji! - brak odpowiedzi");
            }
            return ReservationMapper.toJsonReservation(newReservation);
        } catch (MyMongoException exception) {
            throw new ReservationException("Nie udalo sie utworzyc rezerwacji - " + exception.getMessage());
        }
    }

    public ReservationDTO makeReservation(UUID clientId, UUID courtId) {
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

    public ReservationDTO getReservationById(String uuid) {
        Reservation reservation = reservationRepository.readByUUID(UUID.fromString(uuid));
        return reservation != null ? ReservationMapper.toJsonReservation(reservation) : null;
    }

    public List<ReservationDTO> getAllCurrentReservations() {
        return reservationRepository.read(Filters.eq("endtime", null))
                .stream().map(ReservationMapper::toJsonReservation)
                .toList();
    }

    public List<ReservationDTO> getAllArchiveReservations() {
        return reservationRepository.read(Filters.ne("endtime", null))
                .stream().map(ReservationMapper::toJsonReservation)
                .toList();
    }

    public List<ReservationDTO> getAllClientReservations(UUID clientId) {
        return reservationRepository.read(Filters.eq("clientid", clientId.toString()))
                .stream().map(ReservationMapper::toJsonReservation)
                .toList();
    }

    public List<ReservationDTO> getClientCurrentReservations(UUID clientId) {
        return reservationRepository.read(Filters.and(
                        Filters.eq("clientid", clientId.toString()),
                        Filters.eq("endtime", null)))
                .stream().map(ReservationMapper::toJsonReservation)
                .toList();
    }

    public List<ReservationDTO> getClientEndedReservations(UUID clientId) {
        return reservationRepository.read(Filters.and(
                        Filters.eq("clientid", clientId.toString()),
                        Filters.ne("endtime", null)))
                .stream().map(ReservationMapper::toJsonReservation)
                .toList();
    }

    public ReservationDTO getCourtCurrentReservation(UUID courtId) {
        var list = reservationRepository.read(
                Filters.and(Filters.eq("courtid", courtId.toString()),
                        Filters.eq("endtime", null)));
        return !list.isEmpty() ? ReservationMapper.toJsonReservation(list.get(0)) : null;
    }

    public List<ReservationDTO> getCourtEndedReservation(UUID courtId) {
        return reservationRepository.read(Filters.and(
                        Filters.eq("courtid", courtId.toString()),
                        Filters.ne("endtime", null)))
                .stream().map(ReservationMapper::toJsonReservation)
                .toList();
    }

    public void deleteReservation(UUID uuid) {
        deleteReservation(uuid.toString());
    }

    public void deleteReservation(String reservationId) {
        try {
            reservationRepository.delete(UUID.fromString(reservationId));
        } catch (IllegalStateException e) {
            throw new ReservationException("Nie mozna usunac zakonczonej rezerwacji");
        } catch (Exception exception) {
            throw new MyMongoException("Nie udalo sie usunac podanej rezerwacji. - " + exception.getMessage());
        }
    }

    public double checkClientReservationBalance(UUID clientId) {
        double sum = 0;
        List<ReservationDTO> reservationList = getClientEndedReservations(clientId);
        for (var reservation : reservationList) {
            sum += reservation.getReservationCost();
        }
        return sum;
    }
}
