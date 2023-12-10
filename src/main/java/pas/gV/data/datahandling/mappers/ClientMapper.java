package pas.gV.data.datahandling.mappers;

import pas.gV.model.users.Client;
import pas.gV.data.datahandling.dto.ClientDTO;

import java.util.UUID;

public class ClientMapper {

    public static ClientDTO toMongoUser(Client client) {
        return new ClientDTO(client.getId().toString(), client.getFirstName(),
                client.getLastName(), client.getLogin(), client.isArchive(),
                client.getClientTypeName());
    }

    public static Client fromMongoUser(ClientDTO clientDTO) {
        Client clientModel = new Client(UUID.fromString(clientDTO.getId()), clientDTO.getFirstName(),
                clientDTO.getLastName(), clientDTO.getLogin(), clientDTO.getClientType());
        clientModel.setArchive(clientDTO.isArchive());
        return clientModel;
    }
}
