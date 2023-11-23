package repositoryTests;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import nbd.gV.courts.Court;
import nbd.gV.data.mappers.ClientMapper;
import nbd.gV.data.mappers.ReservationMapper;
import nbd.gV.exceptions.MyMongoException;
import nbd.gV.data.dto.CourtDTO;
import nbd.gV.data.mappers.CourtMapper;
import nbd.gV.repositories.CourtMongoRepository;
import nbd.gV.repositories.ReservationMongoRepository;
import nbd.gV.repositories.UserMongoRepository;
import nbd.gV.reservations.Reservation;
import nbd.gV.users.Client;
import nbd.gV.users.clienttype.Normal;
import org.bson.Document;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CourtMongoRepositoryTest {
    static final CourtMongoRepository courtRepository = new CourtMongoRepository();

    CourtDTO courtMapper1;
    CourtDTO courtMapper2;
    CourtDTO courtMapper3;
    Court court1;
    Court court2;
    Court court3;

    private MongoCollection<CourtDTO> getTestCollection() {
        return courtRepository.getDatabase()
                .getCollection(courtRepository.getCollectionName(), CourtDTO.class);
    }

    @BeforeAll
    @AfterAll
    static void cleanFirstAndLastTimeDB() {
        courtRepository.getDatabase()
                .getCollection(courtRepository.getCollectionName(), CourtDTO.class).deleteMany(Filters.empty());
    }

    @BeforeEach
    void initData() {
        cleanFirstAndLastTimeDB();
        court1 = new Court(100, 200, 1);
        courtMapper1 = CourtMapper.toMongoCourt(court1);

        court2 = new Court(200, 200, 2);
        courtMapper2 = CourtMapper.toMongoCourt(court2);

        court3 = new Court(300, 300, 3);
        courtMapper3 = CourtMapper.toMongoCourt(court3);
    }


    @Test
    void testCreatingRepository() {
        CourtMongoRepository courtRepository = new CourtMongoRepository();
        assertNotNull(courtRepository);
    }

    @Test
    void testAddingNewDocumentToDBPositive() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        assertTrue(courtRepository.create(courtMapper1));
        assertEquals(1, getTestCollection().find().into(new ArrayList<>()).size());
        assertTrue(courtRepository.create(courtMapper2));
        assertEquals(2, getTestCollection().find().into(new ArrayList<>()).size());
    }

    @Test
    void testAddingNewDocumentToDBNegative() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        assertTrue(courtRepository.create(courtMapper1));
        assertEquals(1, getTestCollection().find().into(new ArrayList<>()).size());
        assertThrows(MyMongoException.class, () -> courtRepository.create(courtMapper1));
        assertEquals(1, getTestCollection().find().into(new ArrayList<>()).size());
    }

    @Test
    void testFindingDocumentsInDBPositive() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        assertTrue(courtRepository.create(courtMapper1));
        assertTrue(courtRepository.create(courtMapper2));
        assertTrue(courtRepository.create(courtMapper3));
        assertEquals(3, getTestCollection().find().into(new ArrayList<>()).size());

        var courtsList1 = courtRepository.read(Filters.eq("area", 300));
        assertEquals(1, courtsList1.size());
        assertEquals(courtMapper3, courtsList1.get(0));

        var clientsList2 = courtRepository.read(Filters.eq("basecost", 200));
        assertEquals(2, clientsList2.size());
        assertEquals(courtMapper1, clientsList2.get(0));
        assertEquals(courtMapper2, clientsList2.get(1));
    }

    @Test
    void testFindingDocumentsInDBNegative() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        assertTrue(courtRepository.create(courtMapper1));
        assertEquals(1, getTestCollection().find().into(new ArrayList<>()).size());

        var clientsList = courtRepository.read(Filters.eq("area", 999));
        assertEquals(0, clientsList.size());
    }

    @Test
    void testFindingAllDocumentsInDB() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        assertTrue(courtRepository.create(courtMapper1));
        assertTrue(courtRepository.create(courtMapper2));
        assertTrue(courtRepository.create(courtMapper3));
        assertEquals(3, getTestCollection().find().into(new ArrayList<>()).size());

        var clientsList = courtRepository.readAll();
        assertEquals(3, clientsList.size());
        assertEquals(courtMapper1, clientsList.get(0));
        assertEquals(courtMapper2, clientsList.get(1));
        assertEquals(courtMapper3, clientsList.get(2));
    }

    @Test
    void testFindingByUUID() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        assertTrue(courtRepository.create(courtMapper1));
        assertTrue(courtRepository.create(courtMapper2));
        assertTrue(courtRepository.create(courtMapper3));
        assertEquals(3, getTestCollection().find().into(new ArrayList<>()).size());

        CourtDTO couMapper1 = courtRepository.readByUUID(UUID.fromString(courtMapper1.getCourtId()));
        assertNotNull(couMapper1);
        assertEquals(courtMapper1, couMapper1);

        CourtDTO couMapper3 = courtRepository.readByUUID(UUID.fromString(courtMapper3.getCourtId()));
        assertNotNull(couMapper3);
        assertEquals(courtMapper3, couMapper3);
    }

    @Test
    void testDeletingDocumentsInDBPositive() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        assertTrue(courtRepository.create(courtMapper1));
        assertTrue(courtRepository.create(courtMapper2));
        assertTrue(courtRepository.create(courtMapper3));
        assertEquals(3, getTestCollection().find().into(new ArrayList<>()).size());

        assertTrue(courtRepository.delete(UUID.fromString(courtMapper2.getCourtId())));
        assertEquals(2, getTestCollection().find().into(new ArrayList<>()).size());

        //Check the rest
        var courtMappersLists = courtRepository.readAll();
        assertEquals(2, courtMappersLists.size());
        assertEquals(courtMapper1, courtMappersLists.get(0));
        assertEquals(courtMapper3, courtMappersLists.get(1));
    }

    @Test
    void testDeletingDocumentsInDBNegative() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        assertTrue(courtRepository.create(courtMapper1));
        assertTrue(courtRepository.create(courtMapper2));
        assertTrue(courtRepository.create(courtMapper3));
        assertEquals(3, getTestCollection().find().into(new ArrayList<>()).size());

        assertTrue(courtRepository.delete(UUID.fromString(courtMapper3.getCourtId())));
        assertEquals(2, getTestCollection().find().into(new ArrayList<>()).size());

        assertFalse(courtRepository.delete(UUID.fromString(courtMapper3.getCourtId())));
        assertEquals(2, getTestCollection().find().into(new ArrayList<>()).size());
    }

    @Test
    void testDeletingDocumentsInDBExistingAllocation() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        Client testClient1 = new Client("John", "Smith", "12345678901", new Normal());
        Court testCourt1 = new Court(1000, 100, 1);
        LocalDateTime testTimeStart = LocalDateTime.of(2023, Month.JUNE, 4, 12, 0);
        Reservation testReservation1 = new Reservation(testClient1, testCourt1, testTimeStart);

        assertTrue(courtRepository.create(CourtMapper.toMongoCourt(testCourt1)));
        assertTrue(courtRepository.create(courtMapper2));
        assertTrue(courtRepository.create(courtMapper3));
        assertEquals(3, getTestCollection().find().into(new ArrayList<>()).size());

        try (ReservationMongoRepository reservationMongoRepository = new ReservationMongoRepository();
             UserMongoRepository userMongoRepository = new UserMongoRepository()){
            userMongoRepository.create(ClientMapper.toMongoUser(testClient1));
            reservationMongoRepository.create(ReservationMapper.toMongoReservation(testReservation1));
            assertFalse(courtRepository.delete(testCourt1.getCourtId()));
            assertEquals(3, getTestCollection().find().into(new ArrayList<>()).size());

            reservationMongoRepository.delete(testReservation1.getId());
            userMongoRepository.delete(testCourt1.getCourtId());
        }

        assertTrue(courtRepository.delete(testCourt1.getCourtId()));
        assertEquals(2, getTestCollection().find().into(new ArrayList<>()).size());
    }

    @Test
    void testUpdatingRecordsInDBPositive() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        assertTrue(courtRepository.create(courtMapper1));
        assertTrue(courtRepository.create(courtMapper2));
        assertTrue(courtRepository.create(courtMapper3));
        assertEquals(3, getTestCollection().find().into(new ArrayList<>()).size());

        assertEquals(200,
                courtRepository.readByUUID(UUID.fromString(courtMapper1.getCourtId())).getBaseCost());
        assertTrue(courtRepository.update(UUID.fromString(courtMapper1.getCourtId()),
                "basecost", 350));
        assertEquals(350,
                courtRepository.readByUUID(UUID.fromString(courtMapper1.getCourtId())).getBaseCost());

        //Test adding new value to document
        assertFalse(courtRepository.getDatabase().getCollection(courtRepository.getCollectionName(), Document.class)
                .find(Filters.eq("_id", courtMapper2.getCourtId().toString()))
                .into(new ArrayList<>()).get(0).containsKey("field"));

        assertTrue(courtRepository.update(UUID.fromString(courtMapper2.getCourtId()),
                "field", "newValue"));

        assertTrue(courtRepository.getDatabase().getCollection(courtRepository.getCollectionName(), Document.class)
                .find(Filters.eq("_id", courtMapper2.getCourtId().toString()))
                .into(new ArrayList<>()).get(0).containsKey("field"));

        assertEquals("newValue",
                courtRepository.getDatabase().getCollection(courtRepository.getCollectionName(), Document.class)
                        .find(Filters.eq("_id", courtMapper2.getCourtId().toString()))
                        .into(new ArrayList<>()).get(0).getString("field"));
    }
    @Test
    void testUpdatingRecordsInDBNegative() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        assertTrue(courtRepository.create(courtMapper1));
        assertTrue(courtRepository.create(courtMapper2));
        assertTrue(courtRepository.create(courtMapper3));
        assertEquals(3, getTestCollection().find().into(new ArrayList<>()).size());

        assertThrows(MyMongoException.class,
                () -> courtRepository.update(UUID.fromString(courtMapper3.getCourtId()),
                        "_id", UUID.randomUUID().toString()));

        assertFalse(courtRepository.update(UUID.randomUUID(), "area", 435.0));
    }
    @Test
    void testUpdatingWholeRecordsInDBPositive() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        assertTrue(courtRepository.create(courtMapper1));
        assertTrue(courtRepository.create(courtMapper2));
        assertTrue(courtRepository.create(courtMapper3));
        assertEquals(3, getTestCollection().find().into(new ArrayList<>()).size());

        court1.setArea(111);
        court1.setBaseCost(123);
        assertTrue(courtRepository.update(CourtMapper.toMongoCourt(court1)));
        Court court1Copy = CourtMapper.fromMongoCourt(courtRepository.readByUUID(court1.getCourtId()));
        assertEquals(court1Copy, court1);
    }

    @Test
    void testUpdatingWholeRecordsInDBNegative() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        assertTrue(courtRepository.create(courtMapper2));
        assertTrue(courtRepository.create(courtMapper3));
        assertEquals(2, getTestCollection().find().into(new ArrayList<>()).size());

        court1.setArea(111);
        court1.setBaseCost(123);
        assertFalse(courtRepository.update(CourtMapper.toMongoCourt(court1)));
    }
}
