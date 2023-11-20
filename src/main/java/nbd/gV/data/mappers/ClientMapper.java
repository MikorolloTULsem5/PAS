package nbd.gV.data.mappers;

import nbd.gV.users.Client;
import nbd.gV.users.clienttype.Athlete;
import nbd.gV.users.clienttype.ClientType;
import nbd.gV.users.clienttype.Coach;
import nbd.gV.users.clienttype.Normal;
import nbd.gV.data.dto.ClientDTO;

import java.util.UUID;

public class ClientMapper {

    public static ClientDTO toMongoClient(Client client) {
        return new ClientDTO(client.getId().toString(), client.getFirstName(),
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
