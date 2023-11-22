package nbd.gV.managers;

import com.mongodb.client.model.Filters;
import nbd.gV.data.dto.UserDTO;
import nbd.gV.users.Client;
import nbd.gV.users.clienttype.ClientType;
import nbd.gV.exceptions.ClientException;
import nbd.gV.exceptions.MainException;
import nbd.gV.exceptions.MyMongoException;
import nbd.gV.data.dto.ClientDTO;
import nbd.gV.data.mappers.ClientMapper;
import nbd.gV.repositories.UserMongoRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ClientManager {

    private final UserMongoRepository clientRepository;

    public ClientManager() {
        this.clientRepository = new UserMongoRepository();
    }

    public Client registerClient(String firstName, String lastName, String login, ClientType clientType) {
        Client newClient = new Client(firstName, lastName, login, clientType);
        try {
            if (!clientRepository.read(Filters.eq("login", login), ClientDTO.class).isEmpty()) {
                throw new ClientException("Nie udalo sie zarejestrowac klienta w bazie! - klient o tym numerze PESEL" +
                        "znajduje sie juz w bazie");
            }

            if (!clientRepository.create(ClientMapper.toMongoUser(newClient))) {
                throw new ClientException("Nie udalo sie zarejestrowac klienta w bazie! - brak odpowiedzi");
            }
        } catch (MyMongoException exception) {
            throw new ClientException("Nie udalo sie zarejestrowac klienta w bazie!");
        }
        return newClient;
    }

    public void unregisterClient(Client client) {
        if (client == null) {
            throw new MainException("Nie mozna wyrejestrowac nieistniejacego klienta!");
        }
        try {
            client.setArchive(true);
            if (!clientRepository.update(client.getId(), "archive", true)) {
                client.setArchive(false);
                throw new ClientException("Nie udalo sie wyrejestrowac podanego klienta.");
            }
        } catch (Exception exception) {
            client.setArchive(false);
            throw new ClientException("Nie udalo sie wyrejestrowac podanego klienta. - nieznany blad");
        }
    }

    public Client getClient(UUID clientID) {
        try {
            UserDTO clientMapper = clientRepository.readByUUID(clientID, ClientDTO.class);
            return clientMapper != null ? ClientMapper.fromMongoUser((ClientDTO) clientMapper) : null;
        } catch (Exception exception) {
            throw new ClientException("Blad transakcji.");
        }
    }

    public List<Client> getAllClients() {
        try {
            List<Client> clientsList = new ArrayList<>();
            for (var el : clientRepository.readAll(ClientDTO.class)) {
                clientsList.add(ClientMapper.fromMongoUser((ClientDTO) el));
            }
            return clientsList;
        } catch (Exception exception) {
            throw new ClientException("Nie udalo sie uzyskac clientow.");
        }
    }

    public Client findClientByLogin(String login) {
        var list = clientRepository.read(Filters.eq("login", login), ClientDTO.class);
        return !list.isEmpty() ? ClientMapper.fromMongoUser((ClientDTO) list.get(0)) : null;
    }
}
