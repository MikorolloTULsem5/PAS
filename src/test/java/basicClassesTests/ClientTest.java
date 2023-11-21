package basicClassesTests;

import nbd.gV.users.Client;
import nbd.gV.users.clienttype.Athlete;
import nbd.gV.users.clienttype.ClientType;
import nbd.gV.users.clienttype.Coach;
import nbd.gV.users.clienttype.Normal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import nbd.gV.exceptions.MainException;
import org.junit.jupiter.api.Test;

public class ClientTest {
    String testFirstName = "John";
    String testLastName = "Smith";
    String testLogin = "12345678";
    ClientType testTypeAthlete = new Athlete();
    ClientType testTypeCoach = new Coach();
    ClientType testTypeNormal = new Normal();
    @Test
    void testCreatingClient() {
        Client client = new Client(testFirstName, testLastName, testLogin, testTypeNormal);
        assertNotNull(client);

        assertEquals(testFirstName, client.getFirstName());
        assertEquals(testLastName, client.getLastName());
        assertEquals(testLogin, client.getLogin());
        assertEquals(testTypeNormal, client.getClientType());
        assertFalse(client.isArchive());

        assertThrows(MainException.class, ()
                -> new Client("", testLastName, testLogin, testTypeNormal));
        assertThrows(MainException.class, ()
                -> new Client(testFirstName, "", testLogin, testTypeNormal));
        assertThrows(MainException.class, ()
                -> new Client(testFirstName, testLastName, "", testTypeNormal));
        assertThrows(MainException.class, ()
                -> new Client(testFirstName, testLastName, testLogin, null));
    }

    @Test
    void testSetters() {
        Client client = new Client(testFirstName, testLastName, testLogin, testTypeNormal);
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

        assertEquals(testTypeNormal, client.getClientType());
        client.setClientType(testTypeAthlete);
        assertEquals(testTypeAthlete, client.getClientType());
        client.setClientType(testTypeCoach);
        assertEquals(testTypeCoach, client.getClientType());
    }

    @Test
    void testGettingClientInfo() {
        Client client = new Client(testFirstName, testLastName, testLogin, testTypeNormal);
        assertNotNull(client);

        assertEquals("Klient - John Smith o numerze PESEL 12345678\n", client.getClientInfo());
    }

    @Test
    void testGettingClientMaxHoursAndApplyingDiscount() {
        Client client = new Client(testFirstName, testLastName, testLogin, testTypeNormal);
        assertNotNull(client);
        Client client1 = new Client(testFirstName, testLastName, testLogin, testTypeAthlete);
        assertNotNull(client1);
        Client client2 = new Client(testFirstName, testLastName, testLogin, testTypeCoach);
        assertNotNull(client2);

        assertEquals(0, client.applyDiscount(100));
        assertEquals(10, client1.applyDiscount(100));
        assertEquals(15, client2.applyDiscount(100));

        assertEquals(3, client.getClientMaxHours());
        assertEquals(6, client1.getClientMaxHours());
        assertEquals(12, client2.getClientMaxHours());
    }
}
