package repositoryTests;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import nbd.gV.courts.Court;
import nbd.gV.exceptions.MyMongoException;
import nbd.gV.mappers.CourtMapper;
import nbd.gV.repositories.CourtMongoRepository;
import org.bson.Document;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CourtMongoRepositoryTest {
    static final CourtMongoRepository courtRepository = new CourtMongoRepository();

    CourtMapper courtMapper1;
    CourtMapper courtMapper2;
    CourtMapper courtMapper3;
    Court court1;
    Court court2;
    Court court3;

    private MongoCollection<CourtMapper> getTestCollection() {
        return courtRepository.getDatabase()
                .getCollection(courtRepository.getCollectionName(), CourtMapper.class);
    }

    @BeforeAll
    @AfterAll
    static void cleanFirstAndLastTimeDB() {
        courtRepository.getDatabase()
                .getCollection(courtRepository.getCollectionName(), CourtMapper.class).deleteMany(Filters.empty());
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

        CourtMapper couMapper1 = courtRepository.readByUUID(UUID.fromString(courtMapper1.getCourtId()));
        assertNotNull(couMapper1);
        assertEquals(courtMapper1, couMapper1);

        CourtMapper couMapper3 = courtRepository.readByUUID(UUID.fromString(courtMapper3.getCourtId()));
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
}
