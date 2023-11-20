package nbd.gV.reservations;

import com.mongodb.client.model.Filters;

import nbd.gV.exceptions.MainException;
import nbd.gV.clients.Client;
import nbd.gV.courts.Court;
import nbd.gV.exceptions.MyMongoException;
import nbd.gV.exceptions.ReservationException;
import nbd.gV.mappers.ReservationMapper;
import nbd.gV.repositories.ClientMongoRepository;
import nbd.gV.repositories.CourtMongoRepository;
import nbd.gV.repositories.ReservationMongoRepository;
import org.bson.conversions.Bson;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ReservationManager {
    private final ReservationMongoRepository reservationRepository;

    public ReservationManager() {
        reservationRepository = new ReservationMongoRepository();
    }

    //Rezerwacji mozna dokonac tylko obiektami ktore juz znajduja sie w bazie danych
    public Reservation makeReservation(Client client, Court court, LocalDateTime beginTime) {
        if (client == null || court == null) {
            throw new MainException("Jeden z podanych parametrow [client/court] prowadzi do nieistniejacego obiektu!");
        }
        try {
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

    public Reservation makeReservation(Client client, Court court) {
        return makeReservation(client, court, LocalDateTime.now());
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
        var clientsRepo = new ClientMongoRepository();
        var courtsRepo = new CourtMongoRepository();
        for (var r : reservationRepository.read(filter)) {
            reservations.add(ReservationMapper.fromMongoReservation(r,
                    clientsRepo.readByUUID(UUID.fromString(r.getClientId())),
                    courtsRepo.readByUUID(UUID.fromString(r.getCourtId()))));
        }
        return reservations;
    }

    public List<Reservation> getAllClientReservations(Client client) {
        if (client == null) {
            throw new MainException("Nie istniejacy klient nie moze posiadac rezerwacji!");
        }
        return getReservationsWithBsonFilter(Filters.eq("clientid", client.getClientId().toString()));
    }

    public List<Reservation> getClientEndedReservations(Client client) {
        if (client == null) {
            throw new MainException("Nie istniejacy klient nie moze posiadac rezerwacji!");
        }
        return getReservationsWithBsonFilter(Filters.and(
                Filters.eq("clientid", client.getClientId().toString()),
                Filters.not(Filters.eq("endtime", null))));
    }

    public Reservation getCourtReservation(Court court) {
        if (court == null) {
            throw new MainException("Nie istniejace boisko nie moze posiadac rezerwacji!");
        }
        var list = getReservationsWithBsonFilter(
                Filters.eq("courtid", court.getCourtId().toString()));
        return !list.isEmpty() ? list.get(0) : null;
    }

    public double checkClientReservationBalance(Client client) {
        if (client == null) {
            throw new MainException("Nie mozna obliczyc salda dla nieistniejacego klienta!");
        }
        double sum = 0;
        List<Reservation> reservationList = getClientEndedReservations(client);
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
        var clientsRepo = new ClientMongoRepository();
        var courtsRepo = new CourtMongoRepository();
        ReservationMapper reservationMapper = reservationRepository.readByUUID(uuid);
        return ReservationMapper.fromMongoReservation(reservationMapper,
                clientsRepo.readByUUID(UUID.fromString(reservationMapper.getClientId())),
                courtsRepo.readByUUID(UUID.fromString(reservationMapper.getCourtId())));
    }
}
