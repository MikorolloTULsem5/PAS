package nbd.gV.data.mappers;

import nbd.gV.clients.Client;
import nbd.gV.clients.clienttype.Athlete;
import nbd.gV.clients.clienttype.ClientType;
import nbd.gV.clients.clienttype.Coach;
import nbd.gV.clients.clienttype.Normal;
import nbd.gV.data.dto.ClientDTO;

import java.util.UUID;

public class ClientMapper {

    public static ClientDTO toMongoClient(Client client) {
        return new ClientDTO(client.getClientId().toString(), client.getFirstName(),
                client.getLastName(), client.getPersonalId(), client.isArchive(),
                client.getClientType().getClientTypeName());
    }

    public static Client fromMongoClient(ClientDTO clientDTO) {
        ClientType type = switch (clientDTO.getClientType()) {
            case "Normal" -> new Normal();
            case "Athlete" -> new Athlete();
            case "Coach" -> new Coach();
            default -> null;
        };

        Client clientModel = new Client(UUID.fromString(clientDTO.getClientID()), clientDTO.getFirstName(),
                clientDTO.getLastName(), clientDTO.getPersonalId(), type);
        clientModel.setArchive(clientDTO.isArchive());
        return clientModel;
    }
}
