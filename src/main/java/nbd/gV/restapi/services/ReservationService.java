package nbd.gV.restapi.services;

import com.mongodb.client.model.Filters;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
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
import java.time.Month;
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
    private CourtMongoRepository courtRepository;

    public ReservationService(ReservationMongoRepository reservationRepository) {
        this.reservationRepository = reservationRepository;

        ///TODO do wywalenia
        clientsRepository = new UserMongoRepository();
        courtRepository = new CourtMongoRepository();
    }

    public Reservation makeReservation(UUID clientId, UUID courtId, LocalDateTime beginTime) {
        try {
            Client client = ClientMapper.fromMongoUser((ClientDTO) clientsRepository.readByUUID(clientId, ClientDTO.class));
            Court court = CourtMapper.fromMongoCourt(courtRepository.readByUUID(courtId));

            Reservation newReservation = new Reservation(UUID.randomUUID(), client, court, beginTime);
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
            Court court = CourtMapper.fromMongoCourt(courtRepository.readByUUID(courtId));
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
                courtRepository.readByUUID(UUID.fromString(reservationMapper.getCourtId())));
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
        var courtsRepo = courtRepository;
        for (var r : reservationRepository.read(filter)) {
            reservations.add(ReservationMapper.fromMongoReservation(r,
                    (ClientDTO) clientsRepo.readByUUID(UUID.fromString(r.getClientId()), ClientDTO.class),
                    courtsRepo.readByUUID(UUID.fromString(r.getCourtId()))));
        }
        return reservations;
    }

    @PostConstruct
    private void init() {
        LocalDateTime dataStart = LocalDateTime.of(2023, Month.NOVEMBER, 30, 14, 20);

        makeReservation(UUID.fromString("80e62401-6517-4392-856c-e22ef5f3d6a2"), UUID.fromString("634d9130-0015-42bb-a70a-543dee846760"), dataStart);
        makeReservation(UUID.fromString("b6f5bcb8-7f01-4470-8238-cc3320326157"), UUID.fromString("fe6a35bb-7535-4c23-a259-a14ac0ccedba"), dataStart);
        makeReservation(UUID.fromString("6dc63417-0a21-462c-a97a-e0bf6055a3ea"), UUID.fromString("30ac2027-dcc8-4af7-920f-831b51023bc9"), LocalDateTime.of(2023, Month.NOVEMBER, 28, 14, 20));

        returnCourt(UUID.fromString("30ac2027-dcc8-4af7-920f-831b51023bc9"), dataStart);
       }

    @PreDestroy
    private void destroy() {
        reservationRepository.getDatabase().getCollection(reservationRepository.getCollectionName(),
                ReservationDTO.class).deleteMany(Filters.empty());
        clientsRepository.readAll(ClientDTO.class).forEach((mapper) -> clientsRepository.delete(UUID.fromString(mapper.getId())));
        courtRepository.readAll().forEach((mapper) -> courtRepository.delete(UUID.fromString(mapper.getId())));
    }
}
