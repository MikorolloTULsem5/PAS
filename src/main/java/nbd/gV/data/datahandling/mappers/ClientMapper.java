package nbd.gV.data.datahandling.mappers;

import nbd.gV.model.users.Client;
import nbd.gV.model.users.clienttype.Athlete;
import nbd.gV.model.users.clienttype.ClientType;
import nbd.gV.model.users.clienttype.Coach;
import nbd.gV.model.users.clienttype.Normal;
import nbd.gV.data.datahandling.dto.ClientDTO;

import java.util.UUID;

public class ClientMapper {

    public static ClientDTO toMongoUser(Client client) {
        return new ClientDTO(client.getId().toString(), client.getFirstName(),
                client.getLastName(), client.getLogin(), client.isArchive(),
                client.getClientType().getClientTypeName());
    }

    public static Client fromMongoUser(ClientDTO clientDTO) {
        ClientType type = switch (clientDTO.getClientType()) {
            case "Normal" -> new Normal();
            case "Athlete" -> new Athlete();
            case "Coach" -> new Coach();
            default -> null;
        };

        Client clientModel = new Client(UUID.fromString(clientDTO.getId()), clientDTO.getFirstName(),
                clientDTO.getLastName(), clientDTO.getLogin(), type);
        clientModel.setArchive(clientDTO.isArchive());
        return clientModel;
    }
}
