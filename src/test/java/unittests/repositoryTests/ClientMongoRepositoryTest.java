package unittests.repositoryTests;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import pas.gV.model.data.datahandling.entities.ClientEntity;
import pas.gV.model.data.repositories.UserMongoRepository;
import pas.gV.model.exceptions.UserLoginException;
import pas.gV.model.logic.users.Client;
import pas.gV.model.exceptions.MyMongoException;
import pas.gV.model.logic.users.User;
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
    static final UserMongoRepository clientRepository = new UserMongoRepository();
    Client client1;
    Client client2;
    Client client3;
    final String testClientType = "normal";

    private MongoCollection<ClientEntity> getTestCollection() {
        return clientRepository.getDatabase()
                .getCollection(clientRepository.getCollectionName(), ClientEntity.class);
    }

    @BeforeAll
    @AfterAll
    static void cleanFirstAndLastTimeDB() {
        clientRepository.getDatabase().getCollection("users").deleteMany(Filters.empty());
    }

    @BeforeEach
    void initData() {
        cleanFirstAndLastTimeDB();
        client1 = new Client(UUID.randomUUID(), "Adam", "Smith", "12345678901", "12345678901", testClientType);

        client2 = new Client(UUID.randomUUID(), "Eva", "Smith", "12345678902", "12345678902", testClientType);

        client3 = new Client(UUID.randomUUID(), "John", "Lenon", "12345678903", "12345678903", testClientType);
    }

    @Test
    void testCreatingRepository() {
        UserMongoRepository clientRepository = new UserMongoRepository();
        assertNotNull(clientRepository);
    }

    @Test
    void testAddingNewDocumentToDBPositive() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        assertNotNull(clientRepository.create(client1));
        assertEquals(1, getTestCollection().find().into(new ArrayList<>()).size());
        assertNotNull(clientRepository.create(client2));
        assertEquals(2, getTestCollection().find().into(new ArrayList<>()).size());
    }

    @Test
    void testAddingNewDocumentToDBNegative() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        assertNotNull(clientRepository.create(client1));
        assertEquals(1, getTestCollection().find().into(new ArrayList<>()).size());
        assertThrows(UserLoginException.class, () -> clientRepository.create(client1));
        assertEquals(1, getTestCollection().find().into(new ArrayList<>()).size());
    }

    @Test
    void testFindingDocumentsInDBPositive() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        Client client1 = (Client) clientRepository.create(this.client1);
        Client client2 = (Client) clientRepository.create(this.client2);
        Client client3 = (Client) clientRepository.create(this.client3);
        assertEquals(3, getTestCollection().find().into(new ArrayList<>()).size());

        var clientsList1 = clientRepository.read(Filters.eq("firstname", "John"), Client.class);
        assertEquals(1, clientsList1.size());
        assertEquals(client3, clientsList1.get(0));

        var clientsList2 = clientRepository.read(Filters.eq("lastname", "Smith"), Client.class);
        assertEquals(2, clientsList2.size());
        assertEquals(client1, clientsList2.get(0));
        assertEquals(client2, clientsList2.get(1));
    }

    @Test
    void testFindingDocumentsInDBNegative() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        assertNotNull(clientRepository.create(client1));
        assertEquals(1, getTestCollection().find().into(new ArrayList<>()).size());

        var clientsList = clientRepository.read(Filters.eq("firstname", "John"), Client.class);
        assertEquals(0, clientsList.size());
    }

    @Test
    void testFindingAllDocumentsInDB() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        Client client1 = (Client) clientRepository.create(this.client1);
        Client client2 = (Client) clientRepository.create(this.client2);
        Client client3 = (Client) clientRepository.create(this.client3);
        assertEquals(3, getTestCollection().find().into(new ArrayList<>()).size());

        var clientsList = clientRepository.readAll(Client.class);
        assertEquals(3, clientsList.size());
        assertEquals(client1, clientsList.get(0));
        assertEquals(client2, clientsList.get(1));
        assertEquals(client3, clientsList.get(2));
    }

    @Test
    void testFindingByUUID() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        Client client1 = (Client) clientRepository.create(this.client1);
        assertNotNull(clientRepository.create(this.client2));
        Client client3 = (Client) clientRepository.create(this.client3);
        assertEquals(3, getTestCollection().find().into(new ArrayList<>()).size());

        User clMapper1 = clientRepository.readByUUID(UUID.fromString(client1.getId().toString()), Client.class);
        assertNotNull(clMapper1);
        assertEquals(client1, clMapper1);

        User clMapper3 = clientRepository.readByUUID(UUID.fromString(client3.getId().toString()), Client.class);
        assertNotNull(clMapper3);
        assertEquals(client3, clMapper3);
    }

    @Test
    void testDeletingDocumentsInDBPositive() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        Client client1 = (Client) clientRepository.create(this.client1);
        Client client2 = (Client) clientRepository.create(this.client2);
        Client client3 = (Client) clientRepository.create(this.client3);
        assertEquals(3, getTestCollection().find().into(new ArrayList<>()).size());

        assertTrue(clientRepository.delete(UUID.fromString(client2.getId().toString())));
        assertEquals(2, getTestCollection().find().into(new ArrayList<>()).size());

        //Check the rest
        var clientMappersList = clientRepository.readAll(Client.class);
        assertEquals(2, clientMappersList.size());
        assertEquals(client1, clientMappersList.get(0));
        assertEquals(client3, clientMappersList.get(1));
    }

    @Test
    void testDeletingDocumentsInDBNegative() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        assertNotNull(clientRepository.create(client1));
        assertNotNull(clientRepository.create(client2));
        Client client = (Client) clientRepository.create(client3);
        assertEquals(3, getTestCollection().find().into(new ArrayList<>()).size());

        assertTrue(clientRepository.delete(UUID.fromString(client.getId().toString())));
        assertEquals(2, getTestCollection().find().into(new ArrayList<>()).size());

        assertFalse(clientRepository.delete(UUID.fromString(client.getId().toString())));
        assertEquals(2, getTestCollection().find().into(new ArrayList<>()).size());
    }

    @Test
    void testUpdatingDocumentsInDBPositive() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        Client client1 = (Client) clientRepository.create(this.client1);
        Client client2 = (Client) clientRepository.create(this.client2);
        assertNotNull(clientRepository.create(this.client3));
        assertEquals(3, getTestCollection().find().into(new ArrayList<>()).size());

        assertEquals("Adam",
                ((Client) clientRepository.readByUUID(UUID.fromString(client1.getId().toString()), Client.class)).getFirstName());
        assertTrue(clientRepository.update(UUID.fromString(client1.getId().toString()),
                "firstname", "Chris"));
        assertEquals("Chris",
                ((Client) clientRepository.readByUUID(UUID.fromString(client1.getId().toString()), Client.class)).getFirstName());

        //Test adding new value to document
        assertFalse(clientRepository.getDatabase().getCollection(clientRepository.getCollectionName(), Document.class)
                .find(Filters.eq("_id", client2.getId().toString()))
                .into(new ArrayList<>()).get(0).containsKey("field"));

        assertTrue(clientRepository.update(UUID.fromString(client2.getId().toString()),
                "field", "newValue"));

        assertTrue(clientRepository.getDatabase().getCollection(clientRepository.getCollectionName(), Document.class)
                .find(Filters.eq("_id", client2.getId().toString()))
                .into(new ArrayList<>()).get(0).containsKey("field"));

        assertEquals("newValue",
                clientRepository.getDatabase().getCollection(clientRepository.getCollectionName(), Document.class)
                        .find(Filters.eq("_id", client2.getId().toString()))
                        .into(new ArrayList<>()).get(0).getString("field"));
    }

    @Test
    void testUpdatingDocumentsInDBNegative() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        assertNotNull(clientRepository.create(client1));
        assertNotNull(clientRepository.create(client2));
        Client client = (Client) clientRepository.create(client3);
        assertEquals(3, getTestCollection().find().into(new ArrayList<>()).size());

        assertThrows(MyMongoException.class,
                () -> clientRepository.update(UUID.fromString(client.getId().toString()),
                        "_id", UUID.randomUUID().toString()));

        assertFalse(clientRepository.update(UUID.randomUUID(), "firstname", "Harry"));
    }
}
