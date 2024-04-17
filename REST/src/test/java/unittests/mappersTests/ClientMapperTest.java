package unittests.mappersTests;

import pas.gV.model.data.datahandling.entities.ClientEntity;
import pas.gV.model.logic.users.Client;
import pas.gV.model.data.datahandling.mappers.ClientMapper;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ClientMapperTest {
    UUID uuid = UUID.randomUUID();
    String testFirstName = "John";
    String testLastName = "Smith";
    String testLogin = "12345678";
    String testPassword = "12345678";
    String testTypeAthlete = "athlete";
    String testTypeCoach = "coach";
    String testTypeNormal = "normal";


    @Test
    void testCreatingMapper() {
        ClientEntity clientDTO1 = new ClientEntity(uuid.toString(), testFirstName, testLastName, testLogin, testPassword,
                false, testTypeNormal);
        assertNotNull(clientDTO1);

        assertEquals(uuid, UUID.fromString(clientDTO1.getId()));
        assertEquals(testFirstName, clientDTO1.getFirstName());
        assertEquals(testLastName, clientDTO1.getLastName());
        assertEquals(testLogin, clientDTO1.getLogin());
        assertFalse(clientDTO1.isArchive());
        assertEquals(testTypeNormal, clientDTO1.getClientType());

        //Test other clientTypes
        ClientEntity clientDTO2 = new ClientEntity(UUID.randomUUID().toString(),
                testFirstName, testLastName, testLogin, testPassword, false, testTypeAthlete);
        assertNotNull(clientDTO2);
        assertEquals(testTypeAthlete, clientDTO2.getClientType());

        ClientEntity clientDTO3 = new ClientEntity(UUID.randomUUID().toString(), testFirstName, testLastName,
                testLogin, "aaa", false, testTypeCoach);
        assertNotNull(clientDTO3);
        assertEquals(testTypeCoach, clientDTO3.getClientType());
    }

    @Test
    void testToMongoClientMethod() {
        Client client = new Client(UUID.randomUUID(), testFirstName, testLastName, testLogin, testPassword, testTypeNormal);
        assertNotNull(client);

        ClientEntity clientDTO = ClientMapper.toMongoUser(client);
        assertNotNull(clientDTO);

        assertEquals(client.getId(), UUID.fromString(clientDTO.getId()));
        assertEquals(client.getFirstName(), clientDTO.getFirstName());
        assertEquals(client.getLastName(), clientDTO.getLastName());
        assertEquals(client.getLogin(), clientDTO.getLogin());
        assertFalse(clientDTO.isArchive());
        assertEquals(client.getClientTypeName(), clientDTO.getClientType());
    }

    @Test
    void testFromMongoClientMethod() {
        ClientEntity clientDTO1 = new ClientEntity(uuid.toString(), testFirstName, testLastName, testLogin, testPassword,
                true, testTypeNormal);
        assertNotNull(clientDTO1);

        Client client1 = ClientMapper.fromMongoUser(clientDTO1);
        assertNotNull(client1);

        assertEquals(UUID.fromString(clientDTO1.getId()), client1.getId());
        assertEquals(clientDTO1.getFirstName(), client1.getFirstName());
        assertEquals(clientDTO1.getLastName(), client1.getLastName());
        assertEquals(clientDTO1.getLogin(), client1.getLogin());
        assertTrue(client1.isArchive());
        assertEquals(clientDTO1.getClientType(), client1.getClientTypeName());

        //Test other clientTypes
        ClientEntity clientDTO2 = new ClientEntity(UUID.randomUUID().toString(),
                testFirstName, testLastName, testLogin, testPassword, false, testTypeAthlete);
        assertNotNull(clientDTO2);

        Client client2 = ClientMapper.fromMongoUser(clientDTO2);
        assertNotNull(client2);
        assertEquals(clientDTO2.getClientType(), client2.getClientTypeName());

        ClientEntity clientDTO3 = new ClientEntity(UUID.randomUUID().toString(), testFirstName, testLastName,
                testLogin, testPassword, false, testTypeCoach);
        assertNotNull(clientDTO3);

        Client client3 = ClientMapper.fromMongoUser(clientDTO3);
        assertNotNull(client3);
        assertEquals(clientDTO3.getClientType(), client3.getClientTypeName());
    }
}
