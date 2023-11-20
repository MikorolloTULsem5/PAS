package mappersTests;

import nbd.gV.data.dto.ClientDTO;
import nbd.gV.clients.clienttype.Athlete;
import nbd.gV.clients.Client;
import nbd.gV.clients.clienttype.ClientType;
import nbd.gV.clients.clienttype.Coach;
import nbd.gV.clients.clienttype.Normal;
import nbd.gV.data.mappers.ClientMapper;
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
        ClientDTO clientDTO1 = new ClientDTO(uuid.toString(), testFirstName, testLastName, testPersonalID,
                false, testTypeNormal.getClientTypeName());
        assertNotNull(clientDTO1);

        assertEquals(uuid, UUID.fromString(clientDTO1.getClientID()));
        assertEquals(testFirstName, clientDTO1.getFirstName());
        assertEquals(testLastName, clientDTO1.getLastName());
        assertEquals(testPersonalID, clientDTO1.getPersonalId());
        assertFalse(clientDTO1.isArchive());
        assertEquals(testTypeNormal.getClientTypeName(), clientDTO1.getClientType());

        //Test other clientTypes
        ClientDTO clientDTO2 = new ClientDTO(UUID.randomUUID().toString(),
                testFirstName, testLastName, testPersonalID, false, testTypeAthlete.getClientTypeName());
        assertNotNull(clientDTO2);
        assertEquals(testTypeAthlete.getClientTypeName(), clientDTO2.getClientType());

        ClientDTO clientDTO3 = new ClientDTO(UUID.randomUUID().toString(), testFirstName, testLastName,
                testPersonalID, false, testTypeCoach.getClientTypeName());
        assertNotNull(clientDTO3);
        assertEquals(testTypeCoach.getClientTypeName(), clientDTO3.getClientType());
    }

    @Test
    void testToMongoClientMethod() {
        Client client = new Client(testFirstName, testLastName, testPersonalID, testTypeNormal);
        assertNotNull(client);

        ClientDTO clientDTO = ClientMapper.toMongoClient(client);
        assertNotNull(clientDTO);

        assertEquals(client.getClientId(), UUID.fromString(clientDTO.getClientID()));
        assertEquals(client.getFirstName(), clientDTO.getFirstName());
        assertEquals(client.getLastName(), clientDTO.getLastName());
        assertEquals(client.getPersonalId(), clientDTO.getPersonalId());
        assertFalse(clientDTO.isArchive());
        assertEquals(client.getClientType().getClientTypeName(), clientDTO.getClientType());
    }

    @Test
    void testFromMongoClientMethod() {
        ClientDTO clientDTO1 = new ClientDTO(uuid.toString(), testFirstName, testLastName, testPersonalID,
                true, testTypeNormal.getClientTypeName());
        assertNotNull(clientDTO1);

        Client client1 = ClientMapper.fromMongoClient(clientDTO1);
        assertNotNull(client1);

        assertEquals(UUID.fromString(clientDTO1.getClientID()), client1.getClientId());
        assertEquals(clientDTO1.getFirstName(), client1.getFirstName());
        assertEquals(clientDTO1.getLastName(), client1.getLastName());
        assertEquals(clientDTO1.getPersonalId(), client1.getPersonalId());
        assertTrue(client1.isArchive());
        assertEquals(clientDTO1.getClientType(), client1.getClientType().getClientTypeName());
        assertTrue(client1.getClientType() instanceof Normal);

        //Test other clientTypes
        ClientDTO clientDTO2 = new ClientDTO(UUID.randomUUID().toString(),
                testFirstName, testLastName, testPersonalID, false, testTypeAthlete.getClientTypeName());
        assertNotNull(clientDTO2);

        Client client2 = ClientMapper.fromMongoClient(clientDTO2);
        assertNotNull(client2);
        assertEquals(clientDTO2.getClientType(), client2.getClientType().getClientTypeName());
        assertTrue(client2.getClientType() instanceof Athlete);

        ClientDTO clientDTO3 = new ClientDTO(UUID.randomUUID().toString(), testFirstName, testLastName,
                testPersonalID, false, testTypeCoach.getClientTypeName());
        assertNotNull(clientDTO3);

        Client client3 = ClientMapper.fromMongoClient(clientDTO3);
        assertNotNull(client3);
        assertEquals(clientDTO3.getClientType(), client3.getClientType().getClientTypeName());
        assertTrue(client3.getClientType() instanceof Coach);
    }
}
