//package unittests.repositoryTests;
//
//import com.mongodb.client.MongoCollection;
//import com.mongodb.client.model.Filters;
//
//import nbd.gV.data.datahandling.dto.ClientDTO;
//import nbd.gV.exceptions.MultiReservationException;
//import nbd.gV.model.users.Client;
//import nbd.gV.model.courts.Court;
//import nbd.gV.exceptions.UserException;
//import nbd.gV.exceptions.CourtException;
//import nbd.gV.exceptions.MyMongoException;
//import nbd.gV.exceptions.ReservationException;
//import nbd.gV.data.datahandling.mappers.ClientMapper;
//import nbd.gV.data.datahandling.dto.ReservationDTO;
//import nbd.gV.data.datahandling.mappers.CourtMapper;
//import nbd.gV.data.datahandling.mappers.ReservationMapper;
//import nbd.gV.data.repositories.UserMongoRepository;
//import nbd.gV.data.repositories.CourtMongoRepository;
//import nbd.gV.data.repositories.ReservationMongoRepository;
//import nbd.gV.model.reservations.Reservation;
//import org.junit.jupiter.api.AfterAll;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.time.LocalDateTime;
//import java.time.Month;
//import java.util.ArrayList;
//import java.util.UUID;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//public class ReservationMongoRepositoryTest {
//    static final ReservationMongoRepository reservationRepository = new ReservationMongoRepository();
//    static final CourtMongoRepository courtRepository = new CourtMongoRepository();
//    static final UserMongoRepository clientRepository = new UserMongoRepository();
//    String testClientType;
//
//    Client testClient1;
//    Client testClient2;
//    Client testClient3;
//    Court testCourt1;
//    Court testCourt2;
//    Court testCourt3;
//    Court testCourt4;
//    LocalDateTime testTimeStart;
//    LocalDateTime testTimeEnd;
//
//    private MongoCollection<ReservationDTO> getTestCollection() {
//        return reservationRepository.getDatabase()
//                .getCollection(reservationRepository.getCollectionName(), ReservationDTO.class);
//    }
//
//    @BeforeAll
//    @AfterAll
//    static void cleanDB() {
//        reservationRepository.getDatabase().getCollection(reservationRepository.getCollectionName(),
//                ReservationDTO.class).deleteMany(Filters.empty());
//        clientRepository.readAll(ClientDTO.class).forEach((mapper) -> clientRepository.delete(UUID.fromString(mapper.getId())));
//        courtRepository.readAll().forEach((mapper) -> courtRepository.delete(UUID.fromString(mapper.getId())));
//    }
//
//    @BeforeEach
//    void setUp() {
//        cleanDB();
//        testClientType = "normal";
//
//        testClient1 = new Client(UUID.randomUUID(), "John", "Smith", "12345678901", testClientType);
//        testClient2 = new Client(UUID.randomUUID(), "Eva", "Brown", "12345678902", testClientType);
//        testClient3 = new Client(UUID.randomUUID(), "Adam", "Long", "12345678903", testClientType);
//        clientRepository.create(ClientMapper.toMongoUser(testClient1));
//        clientRepository.create(ClientMapper.toMongoUser(testClient2));
//        clientRepository.create(ClientMapper.toMongoUser(testClient3));
//
//        testCourt1 = new Court(1000, 100, 1);
//        testCourt2 = new Court(1000, 100, 2);
//        testCourt3 = new Court(1000, 100, 3);
//        testCourt4 = new Court(1000, 100, 4);
//
//        courtRepository.create(CourtMapper.toMongoCourt(testCourt1));
//        courtRepository.create(CourtMapper.toMongoCourt(testCourt2));
//        courtRepository.create(CourtMapper.toMongoCourt(testCourt3));
//        courtRepository.create(CourtMapper.toMongoCourt(testCourt4));
//
//        testTimeStart = LocalDateTime.of(2023, Month.JUNE, 4, 12, 0);
//        testTimeEnd = LocalDateTime.of(2023, Month.JUNE, 4, 15, 0);
//    }
//
//    @Test
//    void testCreatingRepository() {
//        ReservationMongoRepository reservationRepository = new ReservationMongoRepository();
//        assertNotNull(reservationRepository);
//    }
//
//    @Test
//    void testAddingNewDocumentToDBPositive() {
//        Reservation reservation = new Reservation(testClient1, testCourt1, testTimeStart);
//        assertNotNull(reservation);
//        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
//        reservationRepository.create(ReservationMapper.toMongoReservation(reservation));
//        assertEquals(1, getTestCollection().find().into(new ArrayList<>()).size());
//
//        Reservation reservation2 = new Reservation(testClient2, testCourt2, testTimeStart);
//        assertNotNull(reservation2);
//        reservationRepository.create(ReservationMapper.toMongoReservation(reservation2));
//        assertEquals(2, getTestCollection().find().into(new ArrayList<>()).size());
//    }
//
//    @Test
//    void testAddingNewDocumentToDBNegative() {
//        Reservation reservation = new Reservation(testClient1, testCourt1, testTimeStart);
//        assertNotNull(reservation);
//        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
//        reservationRepository.create(ReservationMapper.toMongoReservation(reservation));
//        assertEquals(1, getTestCollection().find().into(new ArrayList<>()).size());
//
//        //Reserve reserved court
//        Reservation reservation2 = new Reservation(testClient2, testCourt1, testTimeStart);
//        assertNotNull(reservation2);
//        assertThrows(MultiReservationException.class, () -> reservationRepository.create(
//                ReservationMapper.toMongoReservation(reservation2)));
//
//        //No client in the database
//        assertThrows(ReservationException.class, () -> reservationRepository.create(
//                ReservationMapper.toMongoReservation(new Reservation(new Client(UUID.randomUUID(), "John", "Blade",
//                        "12345678911", "normal"), testCourt3, testTimeStart))));
//
//        //No court in the database
//        assertThrows(ReservationException.class, () -> reservationRepository.create(
//                ReservationMapper.toMongoReservation(new Reservation(testClient3, new Court(1000, 100,
//                        5), testTimeStart))));
//
//        //Archive client
//        clientRepository.update(testClient3.getId(), "archive", true);
//        assertThrows(UserException.class, () -> reservationRepository.create(
//                ReservationMapper.toMongoReservation(new Reservation(testClient3, testCourt3, testTimeStart))));
//
//        //Archive court
//        courtRepository.update(testCourt4.getId(), "archive", true);
//        assertThrows(CourtException.class, () -> reservationRepository.create(
//                ReservationMapper.toMongoReservation(new Reservation(testClient2, testCourt4, testTimeStart))));
//    }
//
//    @Test
//    void testFindingDocumentRecordsInDBPositive() {
//        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
//        ReservationDTO reservationMapper1 = ReservationMapper.toMongoReservation(new Reservation(testClient1,
//                testCourt1, LocalDateTime.of(2000, Month.JUNE, 13, 14, 5)));
//        assertTrue(reservationRepository.create(reservationMapper1));
//        ReservationDTO reservationMapper2 = ReservationMapper.toMongoReservation(new Reservation(testClient2,
//                testCourt2, testTimeStart));
//        assertTrue(reservationRepository.create(reservationMapper2));
//        ReservationDTO reservationMapper3 = ReservationMapper.toMongoReservation(new Reservation(testClient3,
//                testCourt3, testTimeStart));
//        assertTrue(reservationRepository.create(reservationMapper3));
//        assertEquals(3, getTestCollection().find().into(new ArrayList<>()).size());
//
//        var reservationsList1 = reservationRepository.read(Filters.eq("clientid",
//                testClient1.getId().toString()));
//        assertEquals(1, reservationsList1.size());
//        assertEquals(reservationMapper1, reservationsList1.get(0));
//
//        var reservationsList2 = reservationRepository.read(Filters.eq("begintime",
//                testTimeStart));
//        assertEquals(reservationMapper2, reservationsList2.get(0));
//        assertEquals(reservationMapper3, reservationsList2.get(1));
//    }
//
//    @Test
//    void testFindingDocumentRecordsInDBNegative() {
//        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
//        ReservationDTO reservationMapper1 = ReservationMapper.toMongoReservation(new Reservation(testClient1,
//                testCourt1, testTimeStart));
//        assertTrue(reservationRepository.create(reservationMapper1));
//        assertEquals(1, getTestCollection().find().into(new ArrayList<>()).size());
//
//        var reservationsList1 = reservationRepository.read(Filters.eq("clientid",
//                testClient2.getId().toString()));
//        assertEquals(0, reservationsList1.size());
//    }
//
//    @Test
//    void testFindingDocumentByUUIDPositive() {
//        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
//        ReservationDTO reservationMapper2 = ReservationMapper.toMongoReservation(new Reservation(testClient2,
//                testCourt2, testTimeStart));
//        assertTrue(reservationRepository.create(reservationMapper2));
//        ReservationDTO reservationMapper3 = ReservationMapper.toMongoReservation(new Reservation(testClient3,
//                testCourt3, testTimeStart));
//        assertTrue(reservationRepository.create(reservationMapper3));
//        assertEquals(2, getTestCollection().find().into(new ArrayList<>()).size());
//
//        var reservation1 = reservationRepository.readByUUID(
//                UUID.fromString(reservationMapper2.getId()));
//        assertNotNull(reservation1);
//        assertEquals(reservationMapper2, reservation1);
//
//        var reservation2 = reservationRepository.readByUUID(
//                UUID.fromString(reservationMapper3.getId()));
//        assertNotNull(reservation2);
//        assertEquals(reservationMapper3, reservation2);
//    }
//
//    @Test
//    void testFindingByUUIDNegative() {
//        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
//        ReservationDTO reservationMapper1 = ReservationMapper.toMongoReservation(new Reservation(testClient1,
//                testCourt1, testTimeStart));
//        assertTrue(reservationRepository.create(reservationMapper1));
//        assertEquals(1, getTestCollection().find().into(new ArrayList<>()).size());
//
//        assertNull(reservationRepository.readByUUID(UUID.randomUUID()));
//        assertThrows(NullPointerException.class, () -> reservationRepository.readByUUID(null));
//    }
//
//    @Test
//    void testFindingAllDocuments() {
//        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
//        ReservationDTO reservationMapper1 = ReservationMapper.toMongoReservation(new Reservation(testClient1,
//                testCourt1, testTimeStart));
//        assertTrue(reservationRepository.create(reservationMapper1));
//        ReservationDTO reservationMapper2 = ReservationMapper.toMongoReservation(new Reservation(testClient2,
//                testCourt2, testTimeStart));
//        assertTrue(reservationRepository.create(reservationMapper2));
//        ReservationDTO reservationMapper3 = ReservationMapper.toMongoReservation(new Reservation(testClient3,
//                testCourt3, testTimeStart));
//        assertTrue(reservationRepository.create(reservationMapper3));
//        assertEquals(3, getTestCollection().find().into(new ArrayList<>()).size());
//
//        var reservationsList = reservationRepository.readAll();
//        assertEquals(3, reservationsList.size());
//        assertEquals(reservationMapper1, reservationsList.get(0));
//        assertEquals(reservationMapper2, reservationsList.get(1));
//        assertEquals(reservationMapper3, reservationsList.get(2));
//    }
//
//    @Test
//    void testDeletingDocumentsInDB() {
//        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
//        var reservationMapper1 =
//                ReservationMapper.toMongoReservation(new Reservation(testClient1, testCourt1, testTimeStart));
//        reservationRepository.create(reservationMapper1);
//        var reservationMapper2 =
//                ReservationMapper.toMongoReservation(new Reservation(testClient2, testCourt2, testTimeStart));
//        reservationRepository.create(reservationMapper2);
//        assertEquals(2, getTestCollection().find().into(new ArrayList<>()).size());
//
//        reservationRepository.delete(UUID.fromString(reservationMapper2.getId()));
//        assertEquals(1, getTestCollection().find().into(new ArrayList<>()).size());
//
//        reservationRepository.delete(UUID.fromString(reservationMapper1.getId()));
//        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
//
//        assertThrows(NullPointerException.class, () -> reservationRepository.delete(null));
//        assertTrue(reservationRepository.delete(UUID.randomUUID()));
//        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
//    }
//
//    @Test
//    void testClassicUpdatingDocumentsInDBPositive() {
//        Reservation reservation = new Reservation(testClient1, testCourt1, testTimeStart);
//        assertNotNull(reservation);
//        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
//        reservationRepository.create(ReservationMapper.toMongoReservation(reservation));
//        assertEquals(1, getTestCollection().find().into(new ArrayList<>()).size());
//
//        assertEquals(testClient1.getId().toString(),
//                reservationRepository.readByUUID(reservation.getId()).getClientId());
//        assertTrue(reservationRepository.update(reservation.getId(), "clientid",
//                testClient2.getId().toString()));
//        assertEquals(testClient2.getId().toString(),
//                reservationRepository.readByUUID(reservation.getId()).getClientId());
//    }
//
//    @Test
//    void testClassicUpdatingDocumentsInDBNegative() {
//        Reservation reservation = new Reservation(testClient2, testCourt2, testTimeStart);
//        assertNotNull(reservation);
//        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
//        reservationRepository.create(ReservationMapper.toMongoReservation(reservation));
//        assertEquals(1, getTestCollection().find().into(new ArrayList<>()).size());
//
//        assertThrows(MyMongoException.class, () -> reservationRepository.update(reservation.getId(),
//                "_id", UUID.randomUUID().toString()));
//        assertFalse(reservationRepository.update(UUID.randomUUID(), "clientid",
//                testClient2.getId().toString()));
//    }
//
//    @Test
//    void testEndUpdatingDocumentsInDBPositive() {
//        Reservation reservation = new Reservation(testClient1, testCourt1, testTimeStart);
//        assertNotNull(reservation);
//        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
//        reservationRepository.create(ReservationMapper.toMongoReservation(reservation));
//        assertEquals(1, getTestCollection().find().into(new ArrayList<>()).size());
//
//        assertNull(reservationRepository.readByUUID(reservation.getId()).getEndTime());
//        assertEquals(0, reservationRepository.readByUUID(reservation.getId()).getReservationCost());
//        reservationRepository.update(testCourt1.getId(), testTimeEnd);
//        assertEquals(testTimeEnd, reservationRepository.readByUUID(reservation.getId()).getEndTime());
//        assertEquals(300, reservationRepository.readByUUID(reservation.getId()).getReservationCost());
//    }
//
//    @Test
//    void testEndUpdatingDocumentsInDBNegative() {
//        Reservation reservation = new Reservation(testClient1, testCourt1, testTimeStart);
//        assertNotNull(reservation);
//        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
//        reservationRepository.create(ReservationMapper.toMongoReservation(reservation));
//        assertEquals(1, getTestCollection().find().into(new ArrayList<>()).size());
//
//        reservationRepository.update(testCourt1.getId(), testTimeEnd);
//        assertThrows(ReservationException.class, () -> reservationRepository.update(testCourt1.getId(), testTimeEnd));
//    }
//}
