package repositoryTests;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import nbd.gV.mappers.ClientMapper;
import nbd.gV.repositories.ClientMongoRepository;
import nbd.gV.clients.Client;
import nbd.gV.clients.clienttype.ClientType;
import nbd.gV.clients.clienttype.Normal;
import nbd.gV.exceptions.MyMongoException;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ClientMongoRepositoryTest {
    static final ClientMongoRepository clientRepository = new ClientMongoRepository();
    ClientMapper clientMapper1;
    ClientMapper clientMapper2;
    ClientMapper clientMapper3;
    Client client1;
    Client client2;
    Client client3;
    final ClientType testClientType = new Normal();

    private MongoCollection<ClientMapper> getTestCollection() {
        return clientRepository.getDatabase()
                .getCollection(clientRepository.getCollectionName(), ClientMapper.class);
    }

    @BeforeAll
    @AfterAll
    static void cleanFirstAndLastTimeDB() {
        clientRepository.getDatabase()
                .getCollection(clientRepository.getCollectionName(), ClientMapper.class).deleteMany(Filters.empty());
    }

    @BeforeEach
    void initData() {
        cleanFirstAndLastTimeDB();
        client1 = new Client("Adam", "Smith", "12345678901", testClientType);
        clientMapper1 = ClientMapper.toMongoClient(client1);

        client2 = new Client("Eva", "Smith", "12345678902", testClientType);
        clientMapper2 = ClientMapper.toMongoClient(client2);

        client3 = new Client("John", "Lenon", "12345678903", testClientType);
        clientMapper3 = ClientMapper.toMongoClient(client3);
    }

    @Test
    void testCreatingRepository() {
        ClientMongoRepository clientRepository = new ClientMongoRepository();
        assertNotNull(clientRepository);
    }

    @Test
    void testAddingNewDocumentToDBPositive() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        assertTrue(clientRepository.create(clientMapper1));
        assertEquals(1, getTestCollection().find().into(new ArrayList<>()).size());
        assertTrue(clientRepository.create(clientMapper2));
        assertEquals(2, getTestCollection().find().into(new ArrayList<>()).size());
    }

    @Test
    void testAddingNewDocumentToDBNegative() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        assertTrue(clientRepository.create(clientMapper1));
        assertEquals(1, getTestCollection().find().into(new ArrayList<>()).size());
        assertThrows(MyMongoException.class, () -> clientRepository.create(clientMapper1));
        assertEquals(1, getTestCollection().find().into(new ArrayList<>()).size());
    }

    @Test
    void testFindingDocumentsInDBPositive() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        assertTrue(clientRepository.create(clientMapper1));
        assertTrue(clientRepository.create(clientMapper2));
        assertTrue(clientRepository.create(clientMapper3));
        assertEquals(3, getTestCollection().find().into(new ArrayList<>()).size());

        var clientsList1 = clientRepository.read(Filters.eq("firstname", "John"));
        assertEquals(1, clientsList1.size());
        assertEquals(clientMapper3, clientsList1.get(0));

        var clientsList2 = clientRepository.read(Filters.eq("lastname", "Smith"));
        assertEquals(2, clientsList2.size());
        assertEquals(clientMapper1, clientsList2.get(0));
        assertEquals(clientMapper2, clientsList2.get(1));
    }

    @Test
    void testFindingDocumentsInDBNegative() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        assertTrue(clientRepository.create(clientMapper1));
        assertEquals(1, getTestCollection().find().into(new ArrayList<>()).size());

        var clientsList = clientRepository.read(Filters.eq("firstname", "John"));
        assertEquals(0, clientsList.size());
    }

    @Test
    void testFindingAllDocumentsInDB() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        assertTrue(clientRepository.create(clientMapper1));
        assertTrue(clientRepository.create(clientMapper2));
        assertTrue(clientRepository.create(clientMapper3));
        assertEquals(3, getTestCollection().find().into(new ArrayList<>()).size());

        var clientsList = clientRepository.readAll();
        assertEquals(3, clientsList.size());
        assertEquals(clientMapper1, clientsList.get(0));
        assertEquals(clientMapper2, clientsList.get(1));
        assertEquals(clientMapper3, clientsList.get(2));
    }

    @Test
    void testFindingByUUID() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        assertTrue(clientRepository.create(clientMapper1));
        assertTrue(clientRepository.create(clientMapper2));
        assertTrue(clientRepository.create(clientMapper3));
        assertEquals(3, getTestCollection().find().into(new ArrayList<>()).size());

        ClientMapper clMapper1 = clientRepository.readByUUID(UUID.fromString(clientMapper1.getClientID()));
        assertNotNull(clMapper1);
        assertEquals(clientMapper1, clMapper1);

        ClientMapper clMapper3 = clientRepository.readByUUID(UUID.fromString(clientMapper3.getClientID()));
        assertNotNull(clMapper3);
        assertEquals(clientMapper3, clMapper3);
    }

    @Test
    void testDeletingDocumentsInDBPositive() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        assertTrue(clientRepository.create(clientMapper1));
        assertTrue(clientRepository.create(clientMapper2));
        assertTrue(clientRepository.create(clientMapper3));
        assertEquals(3, getTestCollection().find().into(new ArrayList<>()).size());

        assertTrue(clientRepository.delete(UUID.fromString(clientMapper2.getClientID())));
        assertEquals(2, getTestCollection().find().into(new ArrayList<>()).size());

        //Check the rest
        var clientMappersList = clientRepository.readAll();
        assertEquals(2, clientMappersList.size());
        assertEquals(clientMapper1, clientMappersList.get(0));
        assertEquals(clientMapper3, clientMappersList.get(1));
    }

    @Test
    void testDeletingDocumentsInDBNegative() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        assertTrue(clientRepository.create(clientMapper1));
        assertTrue(clientRepository.create(clientMapper2));
        assertTrue(clientRepository.create(clientMapper3));
        assertEquals(3, getTestCollection().find().into(new ArrayList<>()).size());

        assertTrue(clientRepository.delete(UUID.fromString(clientMapper3.getClientID())));
        assertEquals(2, getTestCollection().find().into(new ArrayList<>()).size());

        assertFalse(clientRepository.delete(UUID.fromString(clientMapper3.getClientID())));
        assertEquals(2, getTestCollection().find().into(new ArrayList<>()).size());
    }

    @Test
    void testUpdatingDocumentsInDBPositive() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        assertTrue(clientRepository.create(clientMapper1));
        assertTrue(clientRepository.create(clientMapper2));
        assertTrue(clientRepository.create(clientMapper3));
        assertEquals(3, getTestCollection().find().into(new ArrayList<>()).size());

        assertEquals("Adam",
                clientRepository.readByUUID(UUID.fromString(clientMapper1.getClientID())).getFirstName());
        assertTrue(clientRepository.update(UUID.fromString(clientMapper1.getClientID()),
                "firstname", "Chris"));
        assertEquals("Chris",
                clientRepository.readByUUID(UUID.fromString(clientMapper1.getClientID())).getFirstName());

        //Test adding new value to document
        assertFalse(clientRepository.getDatabase().getCollection(clientRepository.getCollectionName(), Document.class)
                .find(Filters.eq("_id", clientMapper2.getClientID().toString()))
                .into(new ArrayList<>()).get(0).containsKey("field"));

        assertTrue(clientRepository.update(UUID.fromString(clientMapper2.getClientID()),
                "field", "newValue"));

        assertTrue(clientRepository.getDatabase().getCollection(clientRepository.getCollectionName(), Document.class)
                .find(Filters.eq("_id", clientMapper2.getClientID().toString()))
                .into(new ArrayList<>()).get(0).containsKey("field"));

        assertEquals("newValue",
                clientRepository.getDatabase().getCollection(clientRepository.getCollectionName(), Document.class)
                .find(Filters.eq("_id", clientMapper2.getClientID().toString()))
                .into(new ArrayList<>()).get(0).getString("field"));
    }
    @Test
    void testUpdatingDocumentsInDBNegative() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        assertTrue(clientRepository.create(clientMapper1));
        assertTrue(clientRepository.create(clientMapper2));
        assertTrue(clientRepository.create(clientMapper3));
        assertEquals(3, getTestCollection().find().into(new ArrayList<>()).size());

        assertThrows(MyMongoException.class,
                () -> clientRepository.update(UUID.fromString(clientMapper3.getClientID()),
                        "_id", UUID.randomUUID().toString()));

        assertFalse(clientRepository.update(UUID.randomUUID(), "firstname", "Harry"));
    }
}
