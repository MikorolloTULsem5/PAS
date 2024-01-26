package unittests.basicClassesTests;

import pas.gV.model.logic.users.Client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import java.util.UUID;

public class ClientTest {
    String testFirstName = "John";
    String testLastName = "Smith";
    String testLogin = "12345678";
    String testPassword = "12345678";
    String testTypeAthlete = "athlete";
    String testTypeCoach = "coach";
    String testTypeNormal = "normal";

    @Test
    void testCreatingClient() {
        Client client = new Client(UUID.randomUUID(), testFirstName, testLastName, testLogin, testPassword, testTypeNormal);
        assertNotNull(client);

        assertEquals(testFirstName, client.getFirstName());
        assertEquals(testLastName, client.getLastName());
        assertEquals(testLogin, client.getLogin());
        assertEquals(testTypeNormal, client.getClientTypeName());
        assertFalse(client.isArchive());
    }

    @Test
    void testSetters() {
        Client client = new Client(UUID.randomUUID(), testFirstName, testLastName, testLogin, testPassword, testTypeNormal);
        assertNotNull(client);

        assertEquals(testFirstName, client.getFirstName());
        client.setFirstName("Adam");
        assertEquals("Adam", client.getFirstName());

        assertEquals(testLastName, client.getLastName());
        client.setLastName("Long");
        assertEquals("Long", client.getLastName());

        assertFalse(client.isArchive());
        client.setArchive(true);
        assertTrue(client.isArchive());
        client.setArchive(false);
        assertFalse(client.isArchive());

        assertEquals(testTypeNormal, client.getClientTypeName());
        client.setClientTypeName(testTypeAthlete);
        assertEquals(testTypeAthlete, client.getClientTypeName());
        client.setClientTypeName(testTypeCoach);
        assertEquals(testTypeCoach, client.getClientTypeName());
    }

    @Test
    void testGettingClientMaxHoursAndApplyingDiscount() {
        Client client = new Client(UUID.randomUUID(), testFirstName, testLastName, testLogin, testPassword, testTypeNormal);
        assertNotNull(client);
        Client client1 = new Client(UUID.randomUUID(), testFirstName, testLastName, testLogin, testPassword, testTypeAthlete);
        assertNotNull(client1);
        Client client2 = new Client(UUID.randomUUID(), testFirstName, testLastName, testLogin, testPassword, testTypeCoach);
        assertNotNull(client2);

        assertEquals(0, client.applyDiscount());
        assertEquals(0.1, client1.applyDiscount());
        assertEquals(0.2, client2.applyDiscount());

        assertEquals(3, client.clientMaxHours());
        assertEquals(6, client1.clientMaxHours());
        assertEquals(12, client2.clientMaxHours());
    }
}
