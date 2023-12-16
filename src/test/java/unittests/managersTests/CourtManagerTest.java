package unittests.managersTests;

import com.mongodb.client.model.Filters;
import pas.gV.exceptions.CourtNumberException;
import pas.gV.model.courts.Court;
import pas.gV.data.datahandling.dto.CourtDTO;
import pas.gV.restapi.services.CourtService;
import pas.gV.exceptions.CourtException;
import pas.gV.data.repositories.CourtMongoRepository;
import pas.gV.data.repositories.ReservationMongoRepository;
import pas.gV.data.repositories.UserMongoRepository;
import pas.gV.model.reservations.Reservation;
import pas.gV.model.users.Client;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CourtManagerTest {

    static final CourtMongoRepository courtRepository = new CourtMongoRepository();
    static final CourtService cm = new CourtService(courtRepository);

    @BeforeAll
    @AfterAll
    static void cleanDatabaseFirstAndLastTime() {
        courtRepository.getDatabase().getCollection("users").deleteMany(Filters.empty());
        courtRepository.getDatabase().getCollection("courts").deleteMany(Filters.empty());
        courtRepository.getDatabase().getCollection("reservations").deleteMany(Filters.empty());
    }

    @BeforeEach
    void cleanDatabase() {
        cleanDatabaseFirstAndLastTime();
    }

    @Test
    void testCreatingCourtManager() {
        CourtService courtManager = new CourtService();
        assertNotNull(courtManager);
    }

    @Test
    void testRegisteringNewCourt() {
        assertNotNull(cm);
        assertEquals(0, cm.getAllCourts().size());

        Court newCourt = cm.registerCourt(200, 200, 5);
        assertNotNull(newCourt);
        assertEquals(1, cm.getAllCourts().size());
        assertEquals(newCourt, cm.getCourtById(newCourt.getId()));
        assertThrows(CourtNumberException.class, () -> cm.registerCourt(300, 300, 5));
        assertEquals(1, cm.getAllCourts().size());

        cm.registerCourt(200, 200, 6);
        cm.registerCourt(200, 200, 7);
        cm.registerCourt(200, 200, 8);
        assertEquals(4, cm.getAllCourts().size());
    }

    @Test
    void testGettingCourt() {
        Court testCourt1 = cm.registerCourt(10, 50, 1);
        assertNotNull(testCourt1);
        assertEquals(1, cm.getAllCourts().size());

        Court testCourt2 = cm.registerCourt(14, 67, 2);
        assertNotNull(testCourt2);
        assertEquals(2, cm.getAllCourts().size());

        assertEquals(testCourt1, cm.getCourtById(testCourt1.getId()));
        assertEquals(testCourt2, cm.getCourtById(testCourt2.getId()));
        assertNull(cm.getCourtById(UUID.randomUUID()));
    }

    @Test
    void testDeactivateCourt() {
        Court testCourt1 = cm.registerCourt(10, 50, 1);
        assertNotNull(testCourt1);
        assertEquals(1, cm.getAllCourts().size());
        Court testCourt2 = cm.registerCourt(14, 67, 2);
        assertNotNull(testCourt2);
        assertEquals(2, cm.getAllCourts().size());

        assertEquals(2, cm.getAllCourts().size());
        assertEquals(testCourt1, cm.getCourtById(testCourt1.getId()));
        assertFalse(testCourt1.isArchive());

        cm.deactivateCourt(testCourt1.getId());

        assertEquals(2, cm.getAllCourts().size());
        Court dbCourt = cm.getCourtById(testCourt1.getId());
        assertNotNull(dbCourt);
        assertTrue(dbCourt.isArchive());
    }

    @Test
    void testDeletingCourtSuccess() {
        var collection = courtRepository.getDatabase().getCollection(courtRepository.getCollectionName(), CourtDTO.class);
        assertEquals(0, collection.find().into(new ArrayList<>()).size());
        Court testCourt1 = courtRepository.create(new Court(null,1000, 100, 1));

        assertEquals(1, collection.find().into(new ArrayList<>()).size());

        cm.deleteCourt(testCourt1.getId());
        assertEquals(0, collection.find().into(new ArrayList<>()).size());
    }

    @Test
    void testDeletingCourtFailure() {
        var collection = courtRepository.getDatabase().getCollection(courtRepository.getCollectionName(), CourtDTO.class);
        assertEquals(0, collection.find().into(new ArrayList<>()).size());
        Court testCourt1 = courtRepository.create(new Court(null,1000, 100, 1));
        LocalDateTime testTimeStart = LocalDateTime.of(2023, Month.JUNE, 4, 12, 0);

        assertEquals(1, collection.find().into(new ArrayList<>()).size());

        try (ReservationMongoRepository reservationMongoRepository = new ReservationMongoRepository();
             UserMongoRepository userMongoRepository = new UserMongoRepository()) {
            Client testClient1 = (Client) userMongoRepository.create(new Client(UUID.randomUUID(), "John", "Smith", "normal", "12345678901", ""));
            Reservation testReservation1 = new Reservation(UUID.randomUUID(), testClient1, testCourt1, testTimeStart);

            reservationMongoRepository.create(testReservation1);
            assertThrows(CourtException.class, () -> cm.deleteCourt(testCourt1.getId()));
            assertEquals(1, collection.find().into(new ArrayList<>()).size());
        }
    }

    @Test
    public void testFindByCourtNumber() {
        Court testCourt1 = cm.registerCourt(10, 50, 1);
        assertNotNull(testCourt1);
        assertEquals(1, cm.getAllCourts().size());
        Court testCourt2 = cm.registerCourt(14, 67, 2);
        assertNotNull(testCourt2);
        assertEquals(2, cm.getAllCourts().size());

        Court newCourt = cm.getCourtByCourtNumber(1);
        assertNotNull(newCourt);
        assertEquals(testCourt1, newCourt);
        Court newCourt2 = cm.getCourtByCourtNumber(4);
        assertNull(newCourt2);
    }
}
