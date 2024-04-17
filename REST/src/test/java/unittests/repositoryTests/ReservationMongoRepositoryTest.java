package unittests.repositoryTests;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

import pas.gV.model.exceptions.MultiReservationException;
import pas.gV.model.logic.users.Client;
import pas.gV.model.logic.courts.Court;
import pas.gV.model.exceptions.UserException;
import pas.gV.model.exceptions.CourtException;
import pas.gV.model.exceptions.MyMongoException;
import pas.gV.model.exceptions.ReservationException;
import pas.gV.model.data.datahandling.entities.ReservationEntity;
import pas.gV.model.data.repositories.UserMongoRepository;
import pas.gV.model.data.repositories.CourtMongoRepository;
import pas.gV.model.data.repositories.ReservationMongoRepository;
import pas.gV.model.logic.reservations.Reservation;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class ReservationMongoRepositoryTest {
    static final ReservationMongoRepository reservationRepository = new ReservationMongoRepository();
    static final CourtMongoRepository courtRepository = new CourtMongoRepository();
    static final UserMongoRepository clientRepository = new UserMongoRepository();
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

    private MongoCollection<ReservationEntity> getTestCollection() {
        return reservationRepository.getDatabase()
                .getCollection(reservationRepository.getCollectionName(), ReservationEntity.class);
    }

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

        testClient1 = (Client) clientRepository.create(new Client(UUID.randomUUID(), "John", "Smith", "12345678901", "12345678901", testClientType));
        testClient2 = (Client) clientRepository.create(new Client(UUID.randomUUID(), "Eva", "Brown", "12345678902", "12345678902", testClientType));
        testClient3 = (Client) clientRepository.create(new Client(UUID.randomUUID(), "Adam", "Long", "12345678903", "12345678903", testClientType));

        testCourt1 = courtRepository.create(new Court(UUID.randomUUID(), 1000, 100, 1));
        testCourt2 = courtRepository.create(new Court(UUID.randomUUID(), 1000, 100, 2));
        testCourt3 = courtRepository.create(new Court(UUID.randomUUID(), 1000, 100, 3));
        testCourt4 = courtRepository.create(new Court(UUID.randomUUID(), 1000, 100, 4));

        testTimeStart = LocalDateTime.of(2023, Month.JUNE, 4, 12, 0);
        testTimeEnd = LocalDateTime.of(2023, Month.JUNE, 4, 15, 0);
    }

    @Test
    void testCreatingRepository() {
        ReservationMongoRepository reservationRepository = new ReservationMongoRepository();
        assertNotNull(reservationRepository);
    }

    @Test
    void testAddingNewDocumentToDBPositive() {
        Reservation reservation = new Reservation(UUID.randomUUID(), testClient1, testCourt1, testTimeStart);
        assertNotNull(reservation);
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        reservationRepository.create(reservation);
        assertEquals(1, getTestCollection().find().into(new ArrayList<>()).size());

        Reservation reservation2 = new Reservation(UUID.randomUUID(), testClient2, testCourt2, testTimeStart);
        assertNotNull(reservation2);
        reservationRepository.create(reservation2);
        assertEquals(2, getTestCollection().find().into(new ArrayList<>()).size());
    }

    @Test
    void testAddingNewDocumentToDBNegative() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        reservationRepository.create(new Reservation(UUID.randomUUID(), testClient1, testCourt1, testTimeStart));
        assertEquals(1, getTestCollection().find().into(new ArrayList<>()).size());

        //Reserve reserved court
        Reservation reservation2 = new Reservation(UUID.randomUUID(), testClient2, testCourt1, testTimeStart);
        assertNotNull(reservation2);
        assertThrows(MultiReservationException.class, () -> reservationRepository.create(reservation2));

        //No client in the database
        assertThrows(ReservationException.class, () -> reservationRepository.create(
                new Reservation(UUID.randomUUID(), new Client(UUID.randomUUID(), "John", "Blade",
                        "12345678911", "12345678911", "normal"), testCourt3, testTimeStart)));

        //No court in the database
        assertThrows(ReservationException.class, () -> reservationRepository.create(
                new Reservation(UUID.randomUUID(), testClient3,
                        new Court(UUID.randomUUID(), 1000, 100, 5), testTimeStart)));

        //Archive client
        clientRepository.update(testClient3.getId(), "archive", true);
        assertThrows(UserException.class, () -> reservationRepository.create(new Reservation(UUID.randomUUID(),
                testClient3, testCourt3, testTimeStart)));

        //Archive court
        courtRepository.update(testCourt4.getId(), "archive", true);
        assertThrows(CourtException.class, () -> reservationRepository.create(new Reservation(UUID.randomUUID(),
                testClient2, testCourt4, testTimeStart)));
    }

    @Test
    void testFindingDocumentRecordsInDBPositive() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        Reservation reservationMapper1 = reservationRepository.create(new Reservation(UUID.randomUUID(), testClient1,
                testCourt1, LocalDateTime.of(2000, Month.JUNE, 13, 14, 5)));
        Reservation reservationMapper2 = reservationRepository.create(new Reservation(UUID.randomUUID(), testClient2,
                testCourt2, testTimeStart));
        Reservation reservationMapper3 = reservationRepository.create(new Reservation(UUID.randomUUID(), testClient3,
                testCourt3, testTimeStart));
        assertEquals(3, getTestCollection().find().into(new ArrayList<>()).size());

        var reservationsList1 = reservationRepository.read(Filters.eq("clientid",
                testClient1.getId().toString()));
        assertEquals(1, reservationsList1.size());
        assertEquals(reservationMapper1, reservationsList1.get(0));

        var reservationsList2 = reservationRepository.read(Filters.eq("begintime",
                testTimeStart));
        assertEquals(reservationMapper2, reservationsList2.get(0));
        assertEquals(reservationMapper3, reservationsList2.get(1));
    }

    @Test
    void testFindingDocumentRecordsInDBNegative() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        Reservation reservationMapper1 = new Reservation(UUID.randomUUID(), testClient1, testCourt1, testTimeStart);
        assertNotNull(reservationRepository.create(reservationMapper1));
        assertEquals(1, getTestCollection().find().into(new ArrayList<>()).size());

        var reservationsList1 = reservationRepository.read(Filters.eq("clientid",
                testClient2.getId().toString()));
        assertEquals(0, reservationsList1.size());
    }

    @Test
    void testFindingDocumentByUUIDPositive() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        Reservation reservationMapper2 = reservationRepository.create(new Reservation(UUID.randomUUID(), testClient2,
                testCourt2, testTimeStart));
        Reservation reservationMapper3 = reservationRepository.create(new Reservation(UUID.randomUUID(), testClient3, testCourt3, testTimeStart));
        assertEquals(2, getTestCollection().find().into(new ArrayList<>()).size());

        var reservation1 = reservationRepository.readByUUID(
                UUID.fromString(reservationMapper2.getId().toString()));
        assertNotNull(reservation1);
        assertEquals(reservationMapper2, reservation1);

        var reservation2 = reservationRepository.readByUUID(
                UUID.fromString(reservationMapper3.getId().toString()));
        assertNotNull(reservation2);
        assertEquals(reservationMapper3, reservation2);
    }

    @Test
    void testFindingByUUIDNegative() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        Reservation reservationMapper1 = new Reservation(UUID.randomUUID(), testClient1, testCourt1, testTimeStart);
        assertNotNull(reservationRepository.create(reservationMapper1));
        assertEquals(1, getTestCollection().find().into(new ArrayList<>()).size());

        assertNull(reservationRepository.readByUUID(UUID.randomUUID()));
    }

    @Test
    void testFindingAllDocuments() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        Reservation reservationMapper1 = reservationRepository.create(new Reservation(UUID.randomUUID(), testClient1, testCourt1, testTimeStart));
        Reservation reservationMapper2 = reservationRepository.create(new Reservation(UUID.randomUUID(), testClient2, testCourt2, testTimeStart));
        Reservation reservationMapper3 = reservationRepository.create(new Reservation(UUID.randomUUID(), testClient3, testCourt3, testTimeStart));
        assertEquals(3, getTestCollection().find().into(new ArrayList<>()).size());

        var reservationsList = reservationRepository.readAll();
        assertEquals(3, reservationsList.size());
        assertEquals(reservationMapper1, reservationsList.get(0));
        assertEquals(reservationMapper2, reservationsList.get(1));
        assertEquals(reservationMapper3, reservationsList.get(2));
    }

    @Test
    void testDeletingDocumentsInDB() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        var reservationMapper1 = reservationRepository.create(new Reservation(UUID.randomUUID(), testClient1, testCourt1, testTimeStart));
        var reservationMapper2 = reservationRepository.create(new Reservation(UUID.randomUUID(), testClient2, testCourt2, testTimeStart));
        assertEquals(2, getTestCollection().find().into(new ArrayList<>()).size());

        reservationRepository.delete(UUID.fromString(reservationMapper2.getId().toString()));
        assertEquals(1, getTestCollection().find().into(new ArrayList<>()).size());

        reservationRepository.delete(UUID.fromString(reservationMapper1.getId().toString()));
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());

        assertThrows(NullPointerException.class, () -> reservationRepository.delete(null));
        assertTrue(reservationRepository.delete(UUID.randomUUID()));
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
    }

    @Test
    void testClassicUpdatingDocumentsInDBPositive() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        Reservation reservation = reservationRepository.create(new Reservation(UUID.randomUUID(), testClient1, testCourt1, testTimeStart));
        assertEquals(1, getTestCollection().find().into(new ArrayList<>()).size());

        assertEquals(testClient1.getId().toString(),
                reservationRepository.readByUUID(reservation.getId()).getClient().getId().toString());
        assertTrue(reservationRepository.update(reservation.getId(), "clientid",
                testClient2.getId().toString()));
        assertEquals(testClient2.getId().toString(),
                reservationRepository.readByUUID(reservation.getId()).getClient().getId().toString());
    }

    @Test
    void testClassicUpdatingDocumentsInDBNegative() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        Reservation reservation = reservationRepository.create(new Reservation(UUID.randomUUID(), testClient2, testCourt2, testTimeStart));
        assertEquals(1, getTestCollection().find().into(new ArrayList<>()).size());

        assertThrows(MyMongoException.class, () -> reservationRepository.update(reservation.getId(),
                "_id", UUID.randomUUID().toString()));
        assertFalse(reservationRepository.update(UUID.randomUUID(), "clientid",
                testClient2.getId().toString()));
    }

    @Test
    void testEndUpdatingDocumentsInDBPositive() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        Reservation reservation = reservationRepository.create(new Reservation(UUID.randomUUID(), testClient1, testCourt1, testTimeStart));
        assertEquals(1, getTestCollection().find().into(new ArrayList<>()).size());

        assertNull(reservationRepository.readByUUID(reservation.getId()).getEndTime());
        assertEquals(0, reservationRepository.readByUUID(reservation.getId()).getReservationCost());
        reservationRepository.update(testCourt1.getId(), testTimeEnd);
        assertEquals(testTimeEnd, reservationRepository.readByUUID(reservation.getId()).getEndTime());
        assertEquals(300, reservationRepository.readByUUID(reservation.getId()).getReservationCost());
    }

    @Test
    void testEndUpdatingDocumentsInDBNegative() {
        Reservation reservation = new Reservation(UUID.randomUUID(), testClient1, testCourt1, testTimeStart);
        assertNotNull(reservation);
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        reservationRepository.create(reservation);
        assertEquals(1, getTestCollection().find().into(new ArrayList<>()).size());

        reservationRepository.update(testCourt1.getId(), testTimeEnd);
        assertThrows(ReservationException.class, () -> reservationRepository.update(testCourt1.getId(), testTimeEnd));
    }
}
