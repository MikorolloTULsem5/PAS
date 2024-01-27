package pas.gV.restapi.data.mappers;

import pas.gV.model.logic.users.Client;

import pas.gV.restapi.data.dto.ClientDTO;

import java.util.UUID;

public class ClientMapper {

    public static ClientDTO toJsonUser(Client client) {
        return new ClientDTO(client.getId().toString(),
                client.getFirstName(),
                client.getLastName(),
                client.getLogin(),
                client.getPassword(),
                client.isArchive(),
                client.getClientTypeName());
    }

    public static Client fromJsonUser(ClientDTO clientDTO) {
        Client clientModel = new Client(clientDTO.getId() != null ? UUID.fromString(clientDTO.getId()) : null,
                clientDTO.getFirstName(),
                clientDTO.getLastName(),
                clientDTO.getLogin(),
                clientDTO.getPassword(),
                clientDTO.getClientType());
        clientModel.setArchive(clientDTO.isArchive());
        return clientModel;
    }
}
