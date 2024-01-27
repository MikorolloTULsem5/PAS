package unittests.servicesTests;

import com.mongodb.client.model.Filters;
import pas.gV.model.exceptions.MultiReservationException;
import pas.gV.model.logic.users.Client;
import pas.gV.model.logic.courts.Court;
import pas.gV.model.exceptions.UserException;
import pas.gV.model.exceptions.CourtException;
import pas.gV.model.exceptions.ReservationException;
import pas.gV.model.data.repositories.UserMongoRepository;
import pas.gV.model.data.repositories.CourtMongoRepository;
import pas.gV.model.data.repositories.ReservationMongoRepository;
import pas.gV.model.logic.reservations.Reservation;
import pas.gV.restapi.data.dto.ReservationDTO;
import pas.gV.restapi.services.ReservationService;
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

public class ReservationServiceTest {

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
        reservationRepository.getDatabase().getCollection("users").deleteMany(Filters.empty());
        reservationRepository.getDatabase().getCollection("courts").deleteMany(Filters.empty());
        reservationRepository.getDatabase().getCollection("reservations").deleteMany(Filters.empty());
    }

    @BeforeEach
    void setUp() {
        cleanDB();
        testClientType = "normal";

        testClient1 = (Client) clientRepository.create(new Client(UUID.randomUUID(), "John", "Smith", "12345678901", "123456789", testClientType));
        testClient2 = (Client) clientRepository.create(new Client(UUID.randomUUID(), "Eva", "Brown", "12345678902", "123456789", testClientType));
        testClient3 = (Client) clientRepository.create(new Client(UUID.randomUUID(), "Adam", "Long", "12345678903", "123456789", testClientType));

        testCourt1 = courtRepository.create(new Court(UUID.randomUUID(), 1000, 100, 1));
        testCourt2 = courtRepository.create(new Court(UUID.randomUUID(), 1000, 100, 2));
        testCourt3 = courtRepository.create(new Court(UUID.randomUUID(), 1000, 100, 3));
        testCourt4 = courtRepository.create(new Court(UUID.randomUUID(), 1000, 100, 4));

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

        ReservationDTO newReservation = rm.makeReservation(testClient1.getId(), testCourt1.getId(), testTimeStart);

        assertEquals(rm.getAllCurrentReservations().size(), 1);
        assertEquals(newReservation, rm.getReservationById(newReservation.getId()));
        assertTrue(newReservation.getCourt().isRented());


        assertFalse(testCourt2.isRented());
        ReservationDTO newReservation2 = rm.makeReservation(testClient1.getId(), testCourt2.getId());

        assertEquals(rm.getAllCurrentReservations().size(), 2);
        assertEquals(newReservation2, rm.getReservationById(newReservation2.getId()));
        assertTrue(newReservation2.getCourt().isRented());

        assertThrows(MultiReservationException.class, () -> rm.makeReservation(testClient1.getId(), testCourt1.getId(), testTimeStart));
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
        ReservationDTO newReservation = rm.makeReservation(testClient1.getId(), testCourt1.getId());
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

        rm.returnCourt(testCourt1.getId(), testTimeEnd);

        assertEquals(1, rm.getAllArchiveReservations().size());
        assertEquals(1, rm.getAllCurrentReservations().size());
        rm.returnCourt(testCourt2.getId());

        assertEquals(2, rm.getAllArchiveReservations().size());
        assertEquals(0, rm.getAllCurrentReservations().size());

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
