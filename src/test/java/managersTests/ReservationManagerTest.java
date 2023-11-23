package managersTests;

import com.mongodb.client.model.Filters;
import nbd.gV.data.dto.ClientDTO;
import nbd.gV.users.*;
import nbd.gV.users.clienttype.ClientType;
import nbd.gV.users.clienttype.Normal;
import nbd.gV.courts.Court;
import nbd.gV.exceptions.UserException;
import nbd.gV.exceptions.CourtException;
import nbd.gV.exceptions.MainException;
import nbd.gV.exceptions.ReservationException;
import nbd.gV.data.mappers.ClientMapper;
import nbd.gV.data.dto.ReservationDTO;
import nbd.gV.data.mappers.CourtMapper;
import nbd.gV.repositories.UserMongoRepository;
import nbd.gV.repositories.CourtMongoRepository;
import nbd.gV.repositories.ReservationMongoRepository;
import nbd.gV.reservations.Reservation;
import nbd.gV.managers.ReservationManager;
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
    ClientType testClientType;

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
        courtRepository.readAll().forEach((mapper) -> courtRepository.delete(UUID.fromString(mapper.getCourtId())));
    }

    @BeforeEach
    void setUp() {
        cleanDB();
        testClientType = new Normal();

        testClient1 = new Client("John", "Smith", "12345678901", testClientType);
        testClient2 = new Client("Eva", "Brown", "12345678902", testClientType);
        testClient3 = new Client("Adam", "Long", "12345678903", testClientType);
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
        ReservationManager rm = new ReservationManager();
        assertNotNull(rm);
    }

    @Test
    void testMakingReservation() {
        ReservationManager rm = new ReservationManager();
        assertNotNull(rm);
        assertEquals(rm.getAllCurrentReservations().size(), 0);
        assertFalse(testCourt1.isRented());

        Reservation newReservation = rm.makeReservation(testClient1, testCourt1, testTimeStart);

        assertEquals(rm.getAllCurrentReservations().size(), 1);
        assertEquals(newReservation, rm.getReservationByID(newReservation.getId()));
        assertTrue(testCourt1.isRented());


        assertFalse(testCourt2.isRented());
        Reservation newReservation2 = rm.makeReservation(testClient1, testCourt2);

        assertEquals(rm.getAllCurrentReservations().size(), 2);
        assertEquals(newReservation2, rm.getReservationByID(newReservation2.getId()));
        assertTrue(testCourt2.isRented());

        assertThrows(ReservationException.class, () -> rm.makeReservation(testClient1, testCourt1, testTimeStart));
        assertEquals(rm.getAllCurrentReservations().size(), 2);
        assertTrue(testCourt1.isRented());

        clientRepository.update(testClient2.getId(), "archive", true);
        assertFalse(testCourt3.isRented());
        assertThrows(UserException.class, () -> rm.makeReservation(testClient2, testCourt3, testTimeStart));
        assertEquals(rm.getAllCurrentReservations().size(), 2);
        assertFalse(testCourt3.isRented());

        courtRepository.update(testCourt4.getCourtId(), "archive", true);
        assertFalse(testCourt4.isRented());
        assertThrows(CourtException.class, () -> rm.makeReservation(testClient1, testCourt4, testTimeStart));
        assertEquals(rm.getAllCurrentReservations().size(), 2);
        assertFalse(testCourt4.isRented());

        assertThrows(MainException.class, () -> rm.makeReservation(null, testCourt4, testTimeStart));
        assertFalse(testCourt4.isRented());
        assertEquals(rm.getAllCurrentReservations().size(), 2);

        assertThrows(MainException.class, () -> rm.makeReservation(testClient1, null, testTimeStart));
        assertEquals(rm.getAllCurrentReservations().size(), 2);
    }

    @Test
    void testCreatingReservationManagerWithNullDate() {
        ReservationManager rm = new ReservationManager();
        assertNotNull(rm);

        assertEquals(0, rm.getAllCurrentReservations().size());
        Reservation newReservation = rm.makeReservation(testClient1, testCourt1);
        var reservations = rm.getAllCurrentReservations();
        assertEquals(1, reservations.size());
        assertEquals(newReservation, reservations.get(0));
    }

    @Test
    void testEndReservation() {
        ReservationManager rm = new ReservationManager();
        assertNotNull(rm);

        rm.makeReservation(testClient1,testCourt1,testTimeStart);
        rm.makeReservation(testClient2,testCourt2,testTimeStart);

        assertEquals(0, rm.getAllArchiveReservations().size());
        assertEquals(2, rm.getAllCurrentReservations().size());
        System.out.println(testCourt1.isRented());
        rm.returnCourt(testCourt1, testTimeEnd);

        assertEquals(1, rm.getAllArchiveReservations().size());
        assertEquals(1, rm.getAllCurrentReservations().size());
        rm.returnCourt(testCourt2);

        assertEquals(2, rm.getAllArchiveReservations().size());
        assertEquals(0, rm.getAllCurrentReservations().size());

        assertThrows(MainException.class, () -> rm.returnCourt(null));
        assertThrows(ReservationException.class, () -> rm.returnCourt(testCourt3));
    }


    @Test
    void testCheckingClientBalance() {
        var testSuperTimeEnd = LocalDateTime.of(2023, Month.JUNE, 5, 12, 0);
        var testSuperTimeEnd2 = LocalDateTime.of(2023, Month.JUNE, 6, 12, 0);
        ReservationManager rm = new ReservationManager();
        assertNotNull(rm);

        rm.makeReservation(testClient1,testCourt1,testTimeStart);
        rm.makeReservation(testClient1, testCourt2, testTimeStart);
        rm.makeReservation(testClient1, testCourt3, testTimeStart);

        assertEquals(0, rm.checkClientReservationBalance(testClient1));
        rm.returnCourt(testCourt1, testTimeEnd);
        assertEquals(300, rm.checkClientReservationBalance(testClient1));

        rm.returnCourt(testCourt2, testSuperTimeEnd);
        assertEquals(3750, rm.checkClientReservationBalance(testClient1));

        rm.returnCourt(testCourt3, testSuperTimeEnd2);
        assertEquals(10800, rm.checkClientReservationBalance(testClient1));

        assertThrows(MainException.class, () -> rm.checkClientReservationBalance(null));
    }
}
