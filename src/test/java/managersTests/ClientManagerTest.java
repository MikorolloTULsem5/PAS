package managersTests;

import nbd.gV.data.dto.ClientDTO;
import nbd.gV.users.Client;
import nbd.gV.managers.usermanager.ClientManager;
import nbd.gV.users.clienttype.ClientType;
import nbd.gV.users.clienttype.Normal;
import nbd.gV.exceptions.UserException;
import nbd.gV.exceptions.MainException;
import nbd.gV.repositories.UserMongoRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ClientManagerTest {

    static final UserMongoRepository clientRepository = new UserMongoRepository();
    final ClientType testClientType = new Normal();

    @BeforeAll
    @AfterAll
    static void cleanDatabaseFirstAndLastTime() {
        clientRepository.readAll(ClientDTO.class).forEach((mapper) -> clientRepository.delete(UUID.fromString(mapper.getId())));
    }

    @BeforeEach
    void cleanDatabase() {
        cleanDatabaseFirstAndLastTime();
    }

    @Test
    void testCreatingClientManager() {
        ClientManager clientManager = new ClientManager();
        assertNotNull(clientManager);
        assertEquals(0, clientManager.getAllClients().size());
    }

    @Test
    void testRegisteringNewCourt() {
        ClientManager cm = new ClientManager();
        assertNotNull(cm);
        assertEquals(0, cm.getAllClients().size());

        Client newClient =
                cm.registerClient("Adam", "Smith", "12345678901", testClientType);
        assertNotNull(newClient);
        assertEquals(1, cm.getAllClients().size());
        assertEquals(newClient, cm.getClient(newClient.getId()));

        cm.registerClient("Adam", "Long", "12345678902", testClientType);
        cm.registerClient("Eva", "Brown", "12345678903", testClientType);
        cm.registerClient("Adam", "Brown", "12345678904", testClientType);
        assertEquals(4, cm.getAllClients().size());

        assertThrows(UserException.class,
                () -> cm.registerClient("Eva", "Brown", "12345678901", testClientType));
        assertEquals(4, cm.getAllClients().size());
    }

    @Test
    void testGettingClient() {
        ClientManager cm = new ClientManager();
        assertNotNull(cm);

        Client testClient1 =
                cm.registerClient("Adam", "Smith", "12345678901", testClientType);
        assertNotNull(testClient1);
        assertEquals(1, cm.getAllClients().size());

        Client testClient2 =
                cm.registerClient("Eva", "Brown", "12345678902", testClientType);
        assertNotNull(testClient2);
        assertEquals(2, cm.getAllClients().size());

        assertEquals(testClient1, cm.getClient(testClient1.getId()));
        assertEquals(testClient2, cm.getClient(testClient2.getId()));
        assertNull(cm.getClient(UUID.randomUUID()));
    }

    @Test
    void testUnregisteringClient() {
        ClientManager cm = new ClientManager();
        assertNotNull(cm);

        Client testClient1 =
                cm.registerClient("Adam", "Smith", "12345678901", testClientType);
        assertNotNull(testClient1);
        assertEquals(1, cm.getAllClients().size());
        Client testClient2 =
                cm.registerClient("Eva", "Brown", "12345678902", testClientType);
        assertNotNull(testClient2);
        assertEquals(2, cm.getAllClients().size());

        assertEquals(2, cm.getAllClients().size());
        assertEquals(testClient1, cm.getClient(testClient1.getId()));
        assertFalse(testClient1.isArchive());

        cm.archiveClient(testClient1);

        assertEquals(2, cm.getAllClients().size());
        Client dbClient = cm.getClient(testClient1.getId());
        assertNotNull(dbClient);
        assertTrue(dbClient.isArchive());

        // Testujemy wyrejestrowanie boiska ktore nie nalezy do repozytorium
        Client testClient3 = new Client("John", "Lenon", "12345678903", testClientType);
        assertNotNull(testClient3);
        assertFalse(testClient3.isArchive());

        assertThrows(UserException.class, () -> cm.archiveClient(testClient3));
        assertFalse(testClient3.isArchive());
        assertEquals(2, cm.getAllClients().size());

        // Testujemy wyrejestrowanie null'a
        assertThrows(MainException.class, () -> cm.archiveClient(null));
        assertEquals(2, cm.getAllClients().size());
    }

    @Test
    public void testFindClientByLogin() {
        ClientManager cm = new ClientManager();
        assertNotNull(cm);

        Client testClient1 =
                cm.registerClient("Adam", "Smith", "12345678901", testClientType);
        assertNotNull(testClient1);
        assertEquals(1, cm.getAllClients().size());
        Client testClient2 =
                cm.registerClient("Eva", "Brown", "12345678902", testClientType);
        assertNotNull(testClient2);
        assertEquals(2, cm.getAllClients().size());

        Client newClient = cm.findClientByLogin("12345678901");
        assertNotNull(newClient);
        assertEquals(testClient1, newClient);
        Client newClient2 = cm.findClientByLogin("12345678999");
        assertNull(newClient2);
    }

    @Test
    public void testFindClientByLoginFitting() {
        ClientManager cm = new ClientManager();
        assertNotNull(cm);

        Client testClient1 =
                cm.registerClient("Adam", "Smith", "adxam@smith", testClientType);
        assertNotNull(testClient1);
        assertEquals(1, cm.getAllClients().size());
        Client testClient2 =
                cm.registerClient("Eva", "Brown", "eva123bro", testClientType);
        assertNotNull(testClient2);
        assertEquals(2, cm.getAllClients().size());
        Client testClient3 =
                cm.registerClient("Adam", "Tell", "telladxam13", testClientType);
        assertNotNull(testClient3);
        assertEquals(3, cm.getAllClients().size());

        var list = cm.findClientByLoginFitting("adxam");
        assertEquals(2, list.size());
        assertEquals(testClient1.getId(), list.get(0).getId());
        assertEquals(testClient3.getId(), list.get(1).getId());
    }

    @Test
    public void testModifyClient() {
        ClientManager cm = new ClientManager();
        assertNotNull(cm);

        Client testClient1 =
                cm.registerClient("Adam", "Smith", "adxam@smith", testClientType);
        assertNotNull(testClient1);
        assertEquals(1, cm.getAllClients().size());
        Client testClient2 =
                cm.registerClient("Eva", "Brown", "eva123bro", testClientType);
        assertNotNull(testClient2);
        assertEquals(2, cm.getAllClients().size());

        assertEquals("Eva", cm.getAllClients().get(1).getFirstName());
        assertEquals("Brown", cm.getAllClients().get(1).getLastName());
        assertEquals("eva123bro", cm.getAllClients().get(1).getLogin());

        testClient2.setFirstName("Vanessa");
        testClient2.setLastName("Lock");

        cm.modifyClient(testClient2);

        assertEquals("Vanessa", cm.getAllClients().get(1).getFirstName());
        assertEquals("Lock", cm.getAllClients().get(1).getLastName());
        assertEquals("eva123bro", cm.getAllClients().get(1).getLogin());
    }
}
