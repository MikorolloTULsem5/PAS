package unittests.managersTests;

import nbd.gV.data.datahandling.dto.ClientDTO;
import nbd.gV.exceptions.UserLoginException;
import nbd.gV.model.users.Client;
import nbd.gV.restapi.services.userservice.ClientService;
import nbd.gV.data.repositories.UserMongoRepository;
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
    static final ClientService cm = new ClientService(clientRepository);
    final String testClientType = "normal";

    @BeforeAll
    @AfterAll
    static void cleanDatabaseFirstAndLastTime() {
        clientRepository.readAll(ClientDTO.class).forEach((mapper) -> clientRepository.delete(UUID.fromString(mapper.getId().toString())));
    }

    @BeforeEach
    void cleanDatabase() {
        cleanDatabaseFirstAndLastTime();
    }

    @Test
    void testCreatingClientManager() {
        ClientService clientManager = new ClientService();
        assertNotNull(clientManager);
    }

    @Test
    void testRegisteringNewClient() {
        assertEquals(0, cm.getAllClients().size());

        Client newClient =
                cm.registerClient("Adam", "Smith", "12345678901", testClientType);
        assertNotNull(newClient);
        assertEquals(1, cm.getAllClients().size());
        assertEquals(newClient, cm.getClientById(newClient.getId()));

        cm.registerClient("Adam", "Long", "12345678902", testClientType);
        cm.registerClient("Eva", "Brown", "12345678903", testClientType);
        cm.registerClient("Adam", "Brown", "12345678904", testClientType);
        assertEquals(4, cm.getAllClients().size());

        assertThrows(UserLoginException.class,
                () -> cm.registerClient("Eva", "Brown", "12345678901", testClientType));
        assertEquals(4, cm.getAllClients().size());
    }

    @Test
    void testGettingClient() {
        Client testClient1 =
                cm.registerClient("Adam", "Smith", "12345678901", testClientType);
        assertNotNull(testClient1);
        assertEquals(1, cm.getAllClients().size());

        Client testClient2 =
                cm.registerClient("Eva", "Brown", "12345678902", testClientType);
        assertNotNull(testClient2);
        assertEquals(2, cm.getAllClients().size());

        assertEquals(testClient1, cm.getClientById(testClient1.getId()));
        assertEquals(testClient2, cm.getClientById(testClient2.getId()));
        assertNull(cm.getClientById(UUID.randomUUID()));
    }

    @Test
    void testUnregisteringClient() {
        Client testClient1 =
                cm.registerClient("Adam", "Smith", "12345678901", testClientType);
        assertNotNull(testClient1);
        assertEquals(1, cm.getAllClients().size());
        Client testClient2 =
                cm.registerClient("Eva", "Brown", "12345678902", testClientType);
        assertNotNull(testClient2);
        assertEquals(2, cm.getAllClients().size());

        assertEquals(2, cm.getAllClients().size());
        assertEquals(testClient1, cm.getClientById(testClient1.getId()));
        assertFalse(testClient1.isArchive());

        cm.deactivateClient(testClient1.getId());

        assertEquals(2, cm.getAllClients().size());
        Client dbClient = cm.getClientById(testClient1.getId());
        assertNotNull(dbClient);
        assertTrue(dbClient.isArchive());

        // Testujemy archiwizajce klienta ktory nie nalezy do repozytorium
        Client testClient3 = new Client(UUID.randomUUID(), "John", "Lenon", "12345678903", testClientType);
        assertNotNull(testClient3);
        assertFalse(testClient3.isArchive());
    }

    @Test
    public void testFindClientByLogin() {
        Client testClient1 =
                cm.registerClient("Adam", "Smith", "12345678901", testClientType);
        assertNotNull(testClient1);
        assertEquals(1, cm.getAllClients().size());
        Client testClient2 =
                cm.registerClient("Eva", "Brown", "12345678902", testClientType);
        assertNotNull(testClient2);
        assertEquals(2, cm.getAllClients().size());

        Client newClient = cm.getClientByLogin("12345678901");
        assertNotNull(newClient);
        assertEquals(testClient1, newClient);
        Client newClient2 = cm.getClientByLogin("12345678999");
        assertNull(newClient2);
    }

    @Test
    public void testFindClientByLoginFitting() {
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

        var list = cm.getClientByLoginMatching("adxam");
        assertEquals(2, list.size());
        assertEquals(testClient1.getId(), list.get(0).getId());
        assertEquals(testClient3.getId(), list.get(1).getId());
    }

    @Test
    public void testModifyClient() {
        Client testClient1 =
                cm.registerClient("Adam", "Smith", "adxam@smith", testClientType);
        assertNotNull(testClient1);
        assertEquals(1, cm.getAllClients().size());
        Client testClient2 =
                cm.registerClient("Eva", "Brown", "eva123bro", testClientType);
        assertNotNull(testClient2);
        assertEquals(2, cm.getAllClients().size());

        assertEquals("Eva", ((Client) cm.getAllClients().get(1)).getFirstName());
        assertEquals("Brown", ((Client) cm.getAllClients().get(1)).getLastName());
        assertEquals("eva123bro", cm.getAllClients().get(1).getLogin());

        testClient2.setFirstName("Vanessa");
        testClient2.setLastName("Lock");

        cm.modifyClient(testClient2);

        assertEquals("Vanessa", ((Client) cm.getAllClients().get(1)).getFirstName());
        assertEquals("Lock", ((Client) cm.getAllClients().get(1)).getLastName());
        assertEquals("eva123bro", cm.getAllClients().get(1).getLogin());
    }
}
