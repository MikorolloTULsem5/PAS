package unittests.repositoryTests;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import pas.gV.model.exceptions.CourtNumberException;
import pas.gV.model.logic.courts.Court;
import pas.gV.model.exceptions.MyMongoException;
import pas.gV.model.data.datahandling.entities.CourtEntity;
import pas.gV.model.data.repositories.CourtMongoRepository;
import pas.gV.model.data.repositories.ReservationMongoRepository;
import pas.gV.model.data.repositories.UserMongoRepository;
import pas.gV.model.logic.reservations.Reservation;
import pas.gV.model.logic.users.Client;
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

    Court court1;
    Court court2;
    Court court3;

    private MongoCollection<CourtEntity> getTestCollection() {
        return courtRepository.getDatabase()
                .getCollection(courtRepository.getCollectionName(), CourtEntity.class);
    }

    @BeforeAll
    @AfterAll
    static void cleanFirstAndLastTimeDB() {
        courtRepository.getDatabase().getCollection("users").deleteMany(Filters.empty());
        courtRepository.getDatabase().getCollection("courts").deleteMany(Filters.empty());
        courtRepository.getDatabase().getCollection("reservations").deleteMany(Filters.empty());
    }

    @BeforeEach
    void initData() {
        cleanFirstAndLastTimeDB();
        court1 = new Court(UUID.randomUUID(), 100, 200, 1);
        court2 = new Court(UUID.randomUUID(), 200, 200, 2);
        court3 = new Court(UUID.randomUUID(), 300, 300, 3);
    }


    @Test
    void testCreatingRepository() {
        CourtMongoRepository courtRepository = new CourtMongoRepository();
        assertNotNull(courtRepository);
    }

    @Test
    void testAddingNewDocumentToDBPositive() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        assertNotNull(courtRepository.create(court1));
        assertEquals(1, getTestCollection().find().into(new ArrayList<>()).size());
        assertNotNull(courtRepository.create(court2));
        assertEquals(2, getTestCollection().find().into(new ArrayList<>()).size());
    }

    @Test
    void testAddingNewDocumentToDBNegative() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        assertNotNull(courtRepository.create(court1));
        assertEquals(1, getTestCollection().find().into(new ArrayList<>()).size());
        assertThrows(CourtNumberException.class, () -> courtRepository.create(court1));
        assertEquals(1, getTestCollection().find().into(new ArrayList<>()).size());
    }

    @Test
    void testFindingDocumentsInDBPositive() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        Court court1 = courtRepository.create(this.court1);
        Court court2 = courtRepository.create(this.court2);
        Court court3 = courtRepository.create(this.court3);
        assertEquals(3, getTestCollection().find().into(new ArrayList<>()).size());

        var courtsList1 = courtRepository.read(Filters.eq("area", 300));
        assertEquals(1, courtsList1.size());
        assertEquals(court3, courtsList1.get(0));

        var clientsList2 = courtRepository.read(Filters.eq("basecost", 200));
        assertEquals(2, clientsList2.size());
        assertEquals(court1, clientsList2.get(0));
        assertEquals(court2, clientsList2.get(1));
    }

    @Test
    void testFindingDocumentsInDBNegative() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        assertNotNull(courtRepository.create(court1));
        assertEquals(1, getTestCollection().find().into(new ArrayList<>()).size());

        var clientsList = courtRepository.read(Filters.eq("area", 999));
        assertEquals(0, clientsList.size());
    }

    @Test
    void testFindingAllDocumentsInDB() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        Court court1 = courtRepository.create(this.court1);
        Court court2 = courtRepository.create(this.court2);
        Court court3 = courtRepository.create(this.court3);
        assertEquals(3, getTestCollection().find().into(new ArrayList<>()).size());

        var clientsList = courtRepository.readAll();
        assertEquals(3, clientsList.size());
        assertEquals(court1, clientsList.get(0));
        assertEquals(court2, clientsList.get(1));
        assertEquals(court3, clientsList.get(2));
    }

    @Test
    void testFindingByUUID() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        Court court1 = courtRepository.create(this.court1);
        assertNotNull(courtRepository.create(this.court2));
        Court court3 = courtRepository.create(this.court3);
        assertEquals(3, getTestCollection().find().into(new ArrayList<>()).size());

        Court couMapper1 = courtRepository.readByUUID(UUID.fromString(court1.getId().toString()));
        assertNotNull(couMapper1);
        assertEquals(court1, couMapper1);

        Court couMapper3 = courtRepository.readByUUID(UUID.fromString(court3.getId().toString()));
        assertNotNull(couMapper3);
        assertEquals(court3, couMapper3);
    }

    @Test
    void testDeletingDocumentsInDBPositive() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        Court court1 = courtRepository.create(this.court1);
        Court court2 = courtRepository.create(this.court2);
        Court court3 = courtRepository.create(this.court3);
        assertEquals(3, getTestCollection().find().into(new ArrayList<>()).size());

        assertTrue(courtRepository.delete(UUID.fromString(court2.getId().toString())));
        assertEquals(2, getTestCollection().find().into(new ArrayList<>()).size());

        //Check the rest
        var courtMappersLists = courtRepository.readAll();
        assertEquals(2, courtMappersLists.size());
        assertEquals(court1, courtMappersLists.get(0));
        assertEquals(court3, courtMappersLists.get(1));
    }

    @Test
    void testDeletingDocumentsInDBNegative() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        assertNotNull(courtRepository.create(this.court1));
        assertNotNull(courtRepository.create(this.court2));
        Court court3 = courtRepository.create(this.court3);
        assertEquals(3, getTestCollection().find().into(new ArrayList<>()).size());

        assertTrue(courtRepository.delete(UUID.fromString(court3.getId().toString())));
        assertEquals(2, getTestCollection().find().into(new ArrayList<>()).size());

        assertFalse(courtRepository.delete(UUID.fromString(court3.getId().toString())));
        assertEquals(2, getTestCollection().find().into(new ArrayList<>()).size());
    }

    @Test
    void testDeletingDocumentsInDBExistingAllocation() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        LocalDateTime testTimeStart = LocalDateTime.of(2023, Month.JUNE, 4, 12, 0);

        Court testCourt1 = courtRepository.create(new Court(UUID.randomUUID(), 1000, 100, 1));
        assertNotNull(courtRepository.create(this.court2));
        assertNotNull(courtRepository.create(this.court3));

        assertEquals(3, getTestCollection().find().into(new ArrayList<>()).size());

        try (ReservationMongoRepository reservationMongoRepository = new ReservationMongoRepository();
             UserMongoRepository userMongoRepository = new UserMongoRepository()) {
            Client testClient1 = (Client) userMongoRepository.create(new Client(UUID.randomUUID(), "John",
                    "Smith", "999999999999", "999999999999", "normal"));
            Reservation testReservation1 = new Reservation(UUID.randomUUID(), testClient1, testCourt1, testTimeStart);
            reservationMongoRepository.create(testReservation1);
            assertThrows(IllegalStateException.class, () -> courtRepository.delete(testCourt1.getId()));
            assertEquals(3, getTestCollection().find().into(new ArrayList<>()).size());

            reservationMongoRepository.delete(testReservation1.getId());
        }
    }

    @Test
    void testUpdatingRecordsInDBPositive() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        Court court1 = courtRepository.create(this.court1);
        Court court2 = courtRepository.create(this.court2);
        assertNotNull(courtRepository.create(this.court3));
        assertEquals(3, getTestCollection().find().into(new ArrayList<>()).size());

        assertEquals(200,
                courtRepository.readByUUID(UUID.fromString(court1.getId().toString())).getBaseCost());
        assertTrue(courtRepository.update(UUID.fromString(court1.getId().toString()),
                "basecost", 350));
        assertEquals(350,
                courtRepository.readByUUID(UUID.fromString(court1.getId().toString())).getBaseCost());

        //Test adding new value to document
        assertFalse(courtRepository.getDatabase().getCollection(courtRepository.getCollectionName(), Document.class)
                .find(Filters.eq("_id", court2.getId().toString()))
                .into(new ArrayList<>()).get(0).containsKey("field"));

        assertTrue(courtRepository.update(UUID.fromString(court2.getId().toString()),
                "field", "newValue"));

        assertTrue(courtRepository.getDatabase().getCollection(courtRepository.getCollectionName(), Document.class)
                .find(Filters.eq("_id", court2.getId().toString()))
                .into(new ArrayList<>()).get(0).containsKey("field"));

        assertEquals("newValue",
                courtRepository.getDatabase().getCollection(courtRepository.getCollectionName(), Document.class)
                        .find(Filters.eq("_id", court2.getId().toString()))
                        .into(new ArrayList<>()).get(0).getString("field"));
    }

    @Test
    void testUpdatingRecordsInDBNegative() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        assertNotNull(courtRepository.create(this.court1));
        assertNotNull(courtRepository.create(this.court2));
        Court court3 = courtRepository.create(this.court3);
        assertEquals(3, getTestCollection().find().into(new ArrayList<>()).size());

        assertThrows(MyMongoException.class,
                () -> courtRepository.update(UUID.fromString(court3.getId().toString()),
                        "_id", UUID.randomUUID().toString()));

        assertFalse(courtRepository.update(UUID.randomUUID(), "area", 435.0));
    }

    @Test
    void testUpdatingWholeRecordsInDBPositive() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        Court court1 = courtRepository.create(this.court1);
        assertNotNull(courtRepository.create(this.court2));
        assertNotNull(courtRepository.create(this.court3));
        assertEquals(3, getTestCollection().find().into(new ArrayList<>()).size());

        court1.setArea(111);
        court1.setBaseCost(123);
        assertTrue(courtRepository.updateByReplace(court1.getId(), court1));
        Court court1Copy = courtRepository.readByUUID(court1.getId());
        assertEquals(court1Copy, court1);
    }

    @Test
    void testUpdatingWholeRecordsInDBNegative() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());

        assertNotNull(courtRepository.create(this.court2));
        assertNotNull(courtRepository.create(this.court3));

        assertEquals(2, getTestCollection().find().into(new ArrayList<>()).size());

        court1.setArea(111);
        court1.setBaseCost(123);
        assertFalse(courtRepository.updateByReplace(court1.getId(), court1));
    }
}
