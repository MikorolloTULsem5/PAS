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

public class ClientManager extends UserManager {

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

    public Client getClient(UUID clientID) {
        UserDTO clientMapper = clientRepository.readByUUID(clientID, ClientDTO.class);
        return clientMapper != null ? ClientMapper.fromMongoUser((ClientDTO) clientMapper) : null;
    }

    public List<Client> getAllClients() {
        List<Client> clientsList = new ArrayList<>();
        for (var el : clientRepository.readAll(ClientDTO.class)) {
            clientsList.add(ClientMapper.fromMongoUser((ClientDTO) el));
        }
        return clientsList;
    }

    public Client findClientByLogin(String login) {
        var list = clientRepository.read(Filters.eq("login", login), ClientDTO.class);
        return !list.isEmpty() ? ClientMapper.fromMongoUser((ClientDTO) list.get(0)) : null;
    }

    public List<Client> findClientByLoginFitting(String login) {
        List<Client> clientsList = new ArrayList<>();
        for (var el : clientRepository.read(Filters.regex("login", ".*%s.*".formatted(login)), ClientDTO.class)) {
            clientsList.add(ClientMapper.fromMongoUser((ClientDTO) el));
        }
        return clientsList;
    }

    ///TODO ewentualnie podmienic tylko zmienione wartosci
    public void modifyClient(Client modifiedClient) {
        if (modifiedClient == null) {
            throw new MainException("Nie mozna modyfikowac nieistniejacego klienta!");
        }

    }

    public void activateClient(Client client) {
        if (client == null) {
            throw new MainException("Nie mozna wyrejestrowac nieistniejacego klienta!");
        }
        if (!client.isArchive()) {
            throw new ClientException("Ten klient jest juz aktywny!");
        }
        try {
            client.setArchive(false);
            if (!clientRepository.update(client.getId(), "archive", false)) {
                client.setArchive(true);
                throw new ClientException("Nie udalo sie wyrejestrowac podanego klienta.");
            }
        } catch (Exception exception) {
            client.setArchive(true);
            throw new ClientException("Nie udalo sie aktywowac podanego klienta. - nieznany blad");
        }
    }

    public void archiveClient(Client client) {
        if (client == null) {
            throw new MainException("Nie mozna dezaktywowac nieistniejacego klienta!");
        }
        if (client.isArchive()) {
            throw new ClientException("Ten klient jest juz zarchiwizowany!");
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

    @Override
    public int usersSize() {
        return clientRepository.readAll(UserDTO.class).size();
    }
}
