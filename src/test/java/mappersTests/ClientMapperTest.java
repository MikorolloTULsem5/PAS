package mappersTests;

import nbd.gV.mappers.ClientMapper;
import nbd.gV.clients.clienttype.Athlete;
import nbd.gV.clients.Client;
import nbd.gV.clients.clienttype.ClientType;
import nbd.gV.clients.clienttype.Coach;
import nbd.gV.clients.clienttype.Normal;
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
    String testPersonalID = "12345678";
    ClientType testTypeAthlete = new Athlete();
    ClientType testTypeCoach = new Coach();
    ClientType testTypeNormal = new Normal();


    @Test
    void testCreatingMapper() {
        ClientMapper clientMapper1 = new ClientMapper(uuid.toString(), testFirstName, testLastName, testPersonalID,
                false, testTypeNormal.getClientTypeName());
        assertNotNull(clientMapper1);

        assertEquals(uuid, UUID.fromString(clientMapper1.getClientID()));
        assertEquals(testFirstName, clientMapper1.getFirstName());
        assertEquals(testLastName, clientMapper1.getLastName());
        assertEquals(testPersonalID, clientMapper1.getPersonalId());
        assertFalse(clientMapper1.isArchive());
        assertEquals(testTypeNormal.getClientTypeName(), clientMapper1.getClientType());

        //Test other clientTypes
        ClientMapper clientMapper2 = new ClientMapper(UUID.randomUUID().toString(),
                testFirstName, testLastName, testPersonalID, false, testTypeAthlete.getClientTypeName());
        assertNotNull(clientMapper2);
        assertEquals(testTypeAthlete.getClientTypeName(), clientMapper2.getClientType());

        ClientMapper clientMapper3 = new ClientMapper(UUID.randomUUID().toString(), testFirstName, testLastName,
                testPersonalID, false, testTypeCoach.getClientTypeName());
        assertNotNull(clientMapper3);
        assertEquals(testTypeCoach.getClientTypeName(), clientMapper3.getClientType());
    }

    @Test
    void testToMongoClientMethod() {
        Client client = new Client(testFirstName, testLastName, testPersonalID, testTypeNormal);
        assertNotNull(client);

        ClientMapper clientMapper = ClientMapper.toMongoClient(client);
        assertNotNull(clientMapper);

        assertEquals(client.getClientId(), UUID.fromString(clientMapper.getClientID()));
        assertEquals(client.getFirstName(), clientMapper.getFirstName());
        assertEquals(client.getLastName(), clientMapper.getLastName());
        assertEquals(client.getPersonalId(), clientMapper.getPersonalId());
        assertFalse(clientMapper.isArchive());
        assertEquals(client.getClientType().getClientTypeName(), clientMapper.getClientType());
    }

    @Test
    void testFromMongoClientMethod() {
        ClientMapper clientMapper1 = new ClientMapper(uuid.toString(), testFirstName, testLastName, testPersonalID,
                true, testTypeNormal.getClientTypeName());
        assertNotNull(clientMapper1);

        Client client1 = ClientMapper.fromMongoClient(clientMapper1);
        assertNotNull(client1);

        assertEquals(UUID.fromString(clientMapper1.getClientID()), client1.getClientId());
        assertEquals(clientMapper1.getFirstName(), client1.getFirstName());
        assertEquals(clientMapper1.getLastName(), client1.getLastName());
        assertEquals(clientMapper1.getPersonalId(), client1.getPersonalId());
        assertTrue(client1.isArchive());
        assertEquals(clientMapper1.getClientType(), client1.getClientType().getClientTypeName());
        assertTrue(client1.getClientType() instanceof Normal);

        //Test other clientTypes
        ClientMapper clientMapper2 = new ClientMapper(UUID.randomUUID().toString(),
                testFirstName, testLastName, testPersonalID, false, testTypeAthlete.getClientTypeName());
        assertNotNull(clientMapper2);

        Client client2 = ClientMapper.fromMongoClient(clientMapper2);
        assertNotNull(client2);
        assertEquals(clientMapper2.getClientType(), client2.getClientType().getClientTypeName());
        assertTrue(client2.getClientType() instanceof Athlete);

        ClientMapper clientMapper3 = new ClientMapper(UUID.randomUUID().toString(), testFirstName, testLastName,
                testPersonalID, false, testTypeCoach.getClientTypeName());
        assertNotNull(clientMapper3);

        Client client3 = ClientMapper.fromMongoClient(clientMapper3);
        assertNotNull(client3);
        assertEquals(clientMapper3.getClientType(), client3.getClientType().getClientTypeName());
        assertTrue(client3.getClientType() instanceof Coach);
    }
}
