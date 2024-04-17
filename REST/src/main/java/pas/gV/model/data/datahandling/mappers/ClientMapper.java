package pas.gV.model.data.datahandling.mappers;

import pas.gV.model.logic.users.Client;
import pas.gV.model.data.datahandling.entities.ClientEntity;

import java.util.UUID;

public class ClientMapper {

    public static ClientEntity toMongoUser(Client client) {
        return new ClientEntity(client.getId().toString(), client.getFirstName(),
                client.getLastName(), client.getLogin(),
                client.getPassword(),
                client.isArchive(),
                client.getClientTypeName());
    }

    public static Client fromMongoUser(ClientEntity clientDTO) {
        Client clientModel = new Client(UUID.fromString(clientDTO.getId()), clientDTO.getFirstName(),
                clientDTO.getLastName(), clientDTO.getLogin(), clientDTO.getPassword(), clientDTO.getClientType());
        clientModel.setArchive(clientDTO.isArchive());
        return clientModel;
    }
}
