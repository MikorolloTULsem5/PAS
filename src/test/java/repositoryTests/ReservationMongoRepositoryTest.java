package repositoryTests;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

import nbd.gV.clients.Client;
import nbd.gV.clients.clienttype.ClientType;
import nbd.gV.clients.clienttype.Normal;
import nbd.gV.courts.Court;
import nbd.gV.exceptions.ClientException;
import nbd.gV.exceptions.CourtException;
import nbd.gV.exceptions.MyMongoException;
import nbd.gV.exceptions.ReservationException;
import nbd.gV.mappers.ClientMapper;
import nbd.gV.mappers.CourtMapper;
import nbd.gV.mappers.ReservationMapper;
import nbd.gV.repositories.ClientMongoRepository;
import nbd.gV.repositories.CourtMongoRepository;
import nbd.gV.repositories.ReservationMongoRepository;
import nbd.gV.reservations.Reservation;
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
    static final ClientMongoRepository clientRepository = new ClientMongoRepository();
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

    private MongoCollection<ReservationMapper> getTestCollection() {
        return reservationRepository.getDatabase()
                .getCollection(reservationRepository.getCollectionName(), ReservationMapper.class);
    }

    @BeforeAll
    @AfterAll
    static void cleanDB() {
        reservationRepository.getDatabase().getCollection(reservationRepository.getCollectionName(),
                ReservationMapper.class).deleteMany(Filters.empty());
        clientRepository.readAll().forEach((mapper) -> clientRepository.delete(UUID.fromString(mapper.getClientID())));
        courtRepository.readAll().forEach((mapper) -> courtRepository.delete(UUID.fromString(mapper.getCourtId())));
    }

    @BeforeEach
    void setUp() {
        cleanDB();
        testClientType = new Normal();

        testClient1 = new Client("John", "Smith", "12345678901", testClientType);
        testClient2 = new Client("Eva", "Brown", "12345678902", testClientType);
        testClient3 = new Client("Adam", "Long", "12345678903", testClientType);
        clientRepository.create(ClientMapper.toMongoClient(testClient1));
        clientRepository.create(ClientMapper.toMongoClient(testClient2));
        clientRepository.create(ClientMapper.toMongoClient(testClient3));

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
    void testCreatingRepository() {
        ReservationMongoRepository reservationRepository = new ReservationMongoRepository();
        assertNotNull(reservationRepository);
    }

    @Test
    void testAddingNewDocumentToDBPositive() {
        Reservation reservation = new Reservation(testClient1, testCourt1, testTimeStart);
        assertNotNull(reservation);
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        reservationRepository.create(ReservationMapper.toMongoReservation(reservation));
        assertEquals(1, getTestCollection().find().into(new ArrayList<>()).size());

        Reservation reservation2 = new Reservation(testClient2, testCourt2, testTimeStart);
        assertNotNull(reservation2);
        reservationRepository.create(ReservationMapper.toMongoReservation(reservation2));
        assertEquals(2, getTestCollection().find().into(new ArrayList<>()).size());
    }

    @Test
    void testAddingNewDocumentToDBNegative() {
        Reservation reservation = new Reservation(testClient1, testCourt1, testTimeStart);
        assertNotNull(reservation);
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        reservationRepository.create(ReservationMapper.toMongoReservation(reservation));
        assertEquals(1, getTestCollection().find().into(new ArrayList<>()).size());

        //Reserve reserved court
        Reservation reservation2 = new Reservation(testClient2, testCourt1, testTimeStart);
        assertNotNull(reservation2);
        assertThrows(ReservationException.class, () -> reservationRepository.create(
                ReservationMapper.toMongoReservation(reservation2)));

        //No client in the database
        assertThrows(ReservationException.class, () -> reservationRepository.create(
                ReservationMapper.toMongoReservation(new Reservation(new Client("John", "Blade",
                        "12345678911", new Normal()), testCourt3, testTimeStart))));

        //No court in the database
        assertThrows(ReservationException.class, () -> reservationRepository.create(
                ReservationMapper.toMongoReservation(new Reservation(testClient3, new Court(1000, 100,
                        5), testTimeStart))));

        //Archive client
        clientRepository.update(testClient3.getClientId(), "archive", true);
        assertThrows(ClientException.class, () -> reservationRepository.create(
                ReservationMapper.toMongoReservation(new Reservation(testClient3, testCourt3, testTimeStart))));

        //Archive court
        courtRepository.update(testCourt4.getCourtId(), "archive", true);
        assertThrows(CourtException.class, () -> reservationRepository.create(
                ReservationMapper.toMongoReservation(new Reservation(testClient2, testCourt4, testTimeStart))));
    }

    @Test
    void testFindingDocumentRecordsInDBPositive() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        ReservationMapper reservationMapper1 = ReservationMapper.toMongoReservation(new Reservation(testClient1,
                testCourt1, LocalDateTime.of(2000, Month.JUNE, 13, 14, 5)));
        assertTrue(reservationRepository.create(reservationMapper1));
        ReservationMapper reservationMapper2 = ReservationMapper.toMongoReservation(new Reservation(testClient2,
                testCourt2, testTimeStart));
        assertTrue(reservationRepository.create(reservationMapper2));
        ReservationMapper reservationMapper3 = ReservationMapper.toMongoReservation(new Reservation(testClient3,
                testCourt3, testTimeStart));
        assertTrue(reservationRepository.create(reservationMapper3));
        assertEquals(3, getTestCollection().find().into(new ArrayList<>()).size());

        var reservationsList1 = reservationRepository.read(Filters.eq("clientid",
                testClient1.getClientId().toString()));
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
        ReservationMapper reservationMapper1 = ReservationMapper.toMongoReservation(new Reservation(testClient1,
                testCourt1, testTimeStart));
        assertTrue(reservationRepository.create(reservationMapper1));
        assertEquals(1, getTestCollection().find().into(new ArrayList<>()).size());

        var reservationsList1 = reservationRepository.read(Filters.eq("clientid",
                testClient2.getClientId().toString()));
        assertEquals(0, reservationsList1.size());
    }

    @Test
    void testFindingDocumentByUUIDPositive() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        ReservationMapper reservationMapper2 = ReservationMapper.toMongoReservation(new Reservation(testClient2,
                testCourt2, testTimeStart));
        assertTrue(reservationRepository.create(reservationMapper2));
        ReservationMapper reservationMapper3 = ReservationMapper.toMongoReservation(new Reservation(testClient3,
                testCourt3, testTimeStart));
        assertTrue(reservationRepository.create(reservationMapper3));
        assertEquals(2, getTestCollection().find().into(new ArrayList<>()).size());

        var reservation1 = reservationRepository.readByUUID(
                UUID.fromString(reservationMapper2.getId()));
        assertNotNull(reservation1);
        assertEquals(reservationMapper2, reservation1);

        var reservation2 = reservationRepository.readByUUID(
                UUID.fromString(reservationMapper3.getId()));
        assertNotNull(reservation2);
        assertEquals(reservationMapper3, reservation2);
    }

    @Test
    void testFindingByUUIDNegative() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        ReservationMapper reservationMapper1 = ReservationMapper.toMongoReservation(new Reservation(testClient1,
                testCourt1, testTimeStart));
        assertTrue(reservationRepository.create(reservationMapper1));
        assertEquals(1, getTestCollection().find().into(new ArrayList<>()).size());

        assertNull(reservationRepository.readByUUID(UUID.randomUUID()));
        assertThrows(NullPointerException.class, () -> reservationRepository.readByUUID(null));
    }

    @Test
    void testFindingAllDocuments() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        ReservationMapper reservationMapper1 = ReservationMapper.toMongoReservation(new Reservation(testClient1,
                testCourt1, testTimeStart));
        assertTrue(reservationRepository.create(reservationMapper1));
        ReservationMapper reservationMapper2 = ReservationMapper.toMongoReservation(new Reservation(testClient2,
                testCourt2, testTimeStart));
        assertTrue(reservationRepository.create(reservationMapper2));
        ReservationMapper reservationMapper3 = ReservationMapper.toMongoReservation(new Reservation(testClient3,
                testCourt3, testTimeStart));
        assertTrue(reservationRepository.create(reservationMapper3));
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
        var reservationMapper1 =
                ReservationMapper.toMongoReservation(new Reservation(testClient1, testCourt1, testTimeStart));
        reservationRepository.create(reservationMapper1);
        var reservationMapper2 =
                ReservationMapper.toMongoReservation(new Reservation(testClient2, testCourt2, testTimeStart));
        reservationRepository.create(reservationMapper2);
        assertEquals(2, getTestCollection().find().into(new ArrayList<>()).size());

        reservationRepository.delete(UUID.fromString(reservationMapper2.getId()));
        assertEquals(1, getTestCollection().find().into(new ArrayList<>()).size());

        reservationRepository.delete(UUID.fromString(reservationMapper1.getId()));
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());

        assertThrows(NullPointerException.class, () -> reservationRepository.delete(null));
        assertFalse(reservationRepository.delete(UUID.randomUUID()));
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
    }

    @Test
    void testClassicUpdatingDocumentsInDBPositive() {
        Reservation reservation = new Reservation(testClient1, testCourt1, testTimeStart);
        assertNotNull(reservation);
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        reservationRepository.create(ReservationMapper.toMongoReservation(reservation));
        assertEquals(1, getTestCollection().find().into(new ArrayList<>()).size());

        assertEquals(testClient1.getClientId().toString(),
                reservationRepository.readByUUID(reservation.getId()).getClientId());
        assertTrue(reservationRepository.update(reservation.getId(), "clientid",
                testClient2.getClientId().toString()));
        assertEquals(testClient2.getClientId().toString(),
                reservationRepository.readByUUID(reservation.getId()).getClientId());
    }

    @Test
    void testClassicUpdatingDocumentsInDBNegative() {
        Reservation reservation = new Reservation(testClient2, testCourt2, testTimeStart);
        assertNotNull(reservation);
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        reservationRepository.create(ReservationMapper.toMongoReservation(reservation));
        assertEquals(1, getTestCollection().find().into(new ArrayList<>()).size());

        assertThrows(MyMongoException.class, () -> reservationRepository.update(reservation.getId(),
                "_id", UUID.randomUUID().toString()));
        assertFalse(reservationRepository.update(UUID.randomUUID(), "clientid",
                testClient2.getClientId().toString()));
    }

    @Test
    void testEndUpdatingDocumentsInDBPositive() {
        Reservation reservation = new Reservation(testClient1, testCourt1, testTimeStart);
        assertNotNull(reservation);
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        reservationRepository.create(ReservationMapper.toMongoReservation(reservation));
        assertEquals(1, getTestCollection().find().into(new ArrayList<>()).size());

        assertNull(reservationRepository.readByUUID(reservation.getId()).getEndTime());
        assertEquals(0, reservationRepository.readByUUID(reservation.getId()).getReservationCost());
        reservationRepository.update(testCourt1, testTimeEnd);
        assertEquals(testTimeEnd, reservationRepository.readByUUID(reservation.getId()).getEndTime());
        assertEquals(300, reservationRepository.readByUUID(reservation.getId()).getReservationCost());
    }

    @Test
    void testEndUpdatingDocumentsInDBNegative() {
        Reservation reservation = new Reservation(testClient1, testCourt1, testTimeStart);
        assertNotNull(reservation);
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        reservationRepository.create(ReservationMapper.toMongoReservation(reservation));
        assertEquals(1, getTestCollection().find().into(new ArrayList<>()).size());

        reservationRepository.update(testCourt1, testTimeEnd);
        assertThrows(ReservationException.class, () -> reservationRepository.update(testCourt1, testTimeEnd));
    }
}
