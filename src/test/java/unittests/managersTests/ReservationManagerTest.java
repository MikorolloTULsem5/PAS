package unittests.managersTests;

import com.mongodb.client.model.Filters;
import nbd.gV.data.datahandling.dto.ClientDTO;
import nbd.gV.model.users.Client;
import nbd.gV.model.courts.Court;
import nbd.gV.exceptions.UserException;
import nbd.gV.exceptions.CourtException;
import nbd.gV.exceptions.MainException;
import nbd.gV.exceptions.ReservationException;
import nbd.gV.data.datahandling.mappers.ClientMapper;
import nbd.gV.data.datahandling.dto.ReservationDTO;
import nbd.gV.data.datahandling.mappers.CourtMapper;
import nbd.gV.data.repositories.UserMongoRepository;
import nbd.gV.data.repositories.CourtMongoRepository;
import nbd.gV.data.repositories.ReservationMongoRepository;
import nbd.gV.model.reservations.Reservation;
import nbd.gV.restapi.services.ReservationService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ReservationManagerTest {

    static final ReservationMongoRepository reservationRepository = new ReservationMongoRepository();
    static final UserMongoRepository clientRepository = new UserMongoRepository();
    static final CourtMongoRepository courtRepository = new CourtMongoRepository();
    static final ReservationService rm = new ReservationService(reservationRepository);

    String testClientType;

    Client testClient1;
    Client testClient2;
    Client testClient3;
    Court testCourt1;
    Court testCourt2;
    Court testCourt3;
    Court testCourt4;
    LocalDateTime testTimeStart;
    LocalDateTime testTimeEnd;

    @BeforeAll
    @AfterAll
    static void cleanDB() {
        reservationRepository.getDatabase().getCollection(reservationRepository.getCollectionName(),
                ReservationDTO.class).deleteMany(Filters.empty());
        clientRepository.readAll(ClientDTO.class).forEach((mapper) -> clientRepository.delete(UUID.fromString(mapper.getId())));
        courtRepository.readAll().forEach((mapper) -> courtRepository.delete(UUID.fromString(mapper.getId())));
    }

    @BeforeEach
    void setUp() {
        cleanDB();
        testClientType = "normal";

        testClient1 = new Client(UUID.randomUUID(), "John", "Smith", "12345678901", testClientType);
        testClient2 = new Client(UUID.randomUUID(), "Eva", "Brown", "12345678902", testClientType);
        testClient3 = new Client(UUID.randomUUID(), "Adam", "Long", "12345678903", testClientType);
        clientRepository.create(ClientMapper.toMongoUser(testClient1));
        clientRepository.create(ClientMapper.toMongoUser(testClient2));
        clientRepository.create(ClientMapper.toMongoUser(testClient3));

        testCourt1 = new Court(1000, 100, 1);
        testCourt2 = new Court(1000, 100, 2);
        testCourt3 = new Court(1000, 100, 3);
        testCourt4 = new Court(1000, 100, 4);

        courtRepository.create(CourtMapper.toMongoCourt(testCourt1));
        courtRepository.create(CourtMapper.toMongoCourt(testCourt2));
        courtRepository.create(CourtMapper.toMongoCourt(testCourt3));
        courtRepository.create(CourtMapper.toMongoCourt(testCourt4));

        testTimeStart = LocalDateTime.of(2023, Month.JUNE, 4, 12, 0);
        testTimeEnd = LocalDateTime.of(2023, Month.JUNE, 4, 15, 0);
    }

    @Test
    void testCreatingReservationManager() {
        ReservationService rm = new ReservationService();
        assertNotNull(rm);
    }

    @Test
    void testMakingReservation() {
        assertEquals(rm.getAllCurrentReservations().size(), 0);
        assertFalse(testCourt1.isRented());

        Reservation newReservation = rm.makeReservation(testClient1.getId(), testCourt1.getId(), testTimeStart);

        assertEquals(rm.getAllCurrentReservations().size(), 1);
        assertEquals(newReservation, rm.getReservationByID(newReservation.getId()));
        assertTrue(newReservation.getCourt().isRented());


        assertFalse(testCourt2.isRented());
        Reservation newReservation2 = rm.makeReservation(testClient1.getId(), testCourt2.getId());

        assertEquals(rm.getAllCurrentReservations().size(), 2);
        assertEquals(newReservation2, rm.getReservationByID(newReservation2.getId()));
        assertTrue(newReservation2.getCourt().isRented());

        assertThrows(ReservationException.class, () -> rm.makeReservation(testClient1.getId(), testCourt1.getId(), testTimeStart));
        assertEquals(rm.getAllCurrentReservations().size(), 2);

        clientRepository.update(testClient2.getId(), "archive", true);
        assertFalse(testCourt3.isRented());
        assertThrows(UserException.class, () -> rm.makeReservation(testClient2.getId(), testCourt3.getId(), testTimeStart));
        assertEquals(rm.getAllCurrentReservations().size(), 2);

        courtRepository.update(testCourt4.getId(), "archive", true);
        assertThrows(CourtException.class, () -> rm.makeReservation(testClient1.getId(), testCourt4.getId(), testTimeStart));
        assertEquals(rm.getAllCurrentReservations().size(), 2);
    }

    @Test
    void testCreatingReservationManagerWithNullDate() {
        assertEquals(0, rm.getAllCurrentReservations().size());
        Reservation newReservation = rm.makeReservation(testClient1.getId(), testCourt1.getId());
        var reservations = rm.getAllCurrentReservations();
        assertEquals(1, reservations.size());
        assertEquals(newReservation, reservations.get(0));
    }

    @Test
    void testEndReservation() {
        rm.makeReservation(testClient1.getId(), testCourt1.getId(), testTimeStart);
        rm.makeReservation(testClient2.getId(), testCourt2.getId(), testTimeStart);

        assertEquals(0, rm.getAllArchiveReservations().size());
        assertEquals(2, rm.getAllCurrentReservations().size());
        System.out.println(testCourt1.isRented());
        rm.returnCourt(testCourt1.getId(), testTimeEnd);

        assertEquals(1, rm.getAllArchiveReservations().size());
        assertEquals(1, rm.getAllCurrentReservations().size());
        rm.returnCourt(testCourt2.getId());

        assertEquals(2, rm.getAllArchiveReservations().size());
        assertEquals(0, rm.getAllCurrentReservations().size());

        assertThrows(MainException.class, () -> rm.returnCourt(null));
        assertThrows(ReservationException.class, () -> rm.returnCourt(testCourt3.getId()));
    }


    @Test
    void testCheckingClientBalance() {
        var testSuperTimeEnd = LocalDateTime.of(2023, Month.JUNE, 5, 12, 0);
        var testSuperTimeEnd2 = LocalDateTime.of(2023, Month.JUNE, 6, 12, 0);

        rm.makeReservation(testClient1.getId(), testCourt1.getId(), testTimeStart);
        rm.makeReservation(testClient1.getId(), testCourt2.getId(), testTimeStart);
        rm.makeReservation(testClient1.getId(), testCourt3.getId(), testTimeStart);

        assertEquals(0, rm.checkClientReservationBalance(testClient1.getId()));
        rm.returnCourt(testCourt1.getId(), testTimeEnd);
        assertEquals(300, rm.checkClientReservationBalance(testClient1.getId()));

        rm.returnCourt(testCourt2.getId(), testSuperTimeEnd);
        assertEquals(3750, rm.checkClientReservationBalance(testClient1.getId()));

        rm.returnCourt(testCourt3.getId(), testSuperTimeEnd2);
        assertEquals(10800, rm.checkClientReservationBalance(testClient1.getId()));
    }
}
