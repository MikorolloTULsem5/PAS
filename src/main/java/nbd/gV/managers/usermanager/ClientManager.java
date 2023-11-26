package nbd.gV.managers.usermanager;

import com.mongodb.client.model.Filters;
import nbd.gV.data.dto.UserDTO;
import nbd.gV.users.Client;
import nbd.gV.users.clienttype.ClientType;
import nbd.gV.exceptions.UserException;
import nbd.gV.exceptions.MainException;
import nbd.gV.exceptions.MyMongoException;
import nbd.gV.data.dto.ClientDTO;
import nbd.gV.data.mappers.ClientMapper;
import nbd.gV.repositories.UserMongoRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ClientManager extends UserManager {

    private final UserMongoRepository userRepository;

    public ClientManager() {
        this.userRepository = new UserMongoRepository();
    }

    public Client registerClient(String firstName, String lastName, String login, ClientType clientType) {
        Client newClient = new Client(UUID.randomUUID(), firstName, lastName, login, clientType);
        try {
            if (!userRepository.read(Filters.eq("login", login), ClientDTO.class).isEmpty()) {
                throw new UserException("Nie udalo sie zarejestrowac klienta w bazie! - klient o tym loginie" +
                        "znajduje sie juz w bazie");
            }

            if (!userRepository.create(ClientMapper.toMongoUser(newClient))) {
                throw new UserException("Nie udalo sie zarejestrowac klienta w bazie! - brak odpowiedzi");
            }
        } catch (MyMongoException exception) {
            throw new UserException("Nie udalo sie zarejestrowac klienta w bazie!");
        }
        return newClient;
    }

    public Client getClient(UUID clientID) {
        UserDTO clientDTO = userRepository.readByUUID(clientID, ClientDTO.class);
        return clientDTO != null ? ClientMapper.fromMongoUser((ClientDTO) clientDTO) : null;
    }

    public List<Client> getAllClients() {
        List<Client> clientsList = new ArrayList<>();
        for (var el : userRepository.readAll(ClientDTO.class)) {
            clientsList.add(ClientMapper.fromMongoUser((ClientDTO) el));
        }
        return clientsList;
    }

    public Client findClientByLogin(String login) {
        var list = userRepository.read(Filters.eq("login", login), ClientDTO.class);
        return !list.isEmpty() ? ClientMapper.fromMongoUser((ClientDTO) list.get(0)) : null;
    }

    public List<Client> findClientByLoginFitting(String login) {
        List<Client> clientsList = new ArrayList<>();
        for (var el : userRepository.read(Filters.and(Filters.regex("login", ".*%s.*".formatted(login)),
                Filters.eq("_clazz", "client")), ClientDTO.class)) {
            clientsList.add(ClientMapper.fromMongoUser((ClientDTO) el));
        }
        return clientsList;
    }

    public void modifyClient(Client modifiedClient) {
        if (modifiedClient == null) {
            throw new MainException("Nie mozna modyfikowac nieistniejacego klienta!");
        }
        try {
            if (!userRepository.updateByReplace(modifiedClient.getId(), ClientMapper.toMongoUser(modifiedClient))) {
                throw new UserException("Nie udalo sie wyrejestrowac podanego klienta.");
            }
        } catch (Exception exception) {
            throw new UserException("Nie udalo sie aktywowac podanego klienta. - nieznany blad");
        }
    }

    public void activateClient(Client client) {
        if (client == null) {
            throw new MainException("Nie mozna wyrejestrowac nieistniejacego klienta!");
        }
        if (!client.isArchive()) {
            throw new UserException("Ten klient jest juz aktywny!");
        }
        try {
            client.setArchive(false);
            if (!userRepository.update(client.getId(), "archive", false)) {
                client.setArchive(true);
                throw new UserException("Nie udalo sie wyrejestrowac podanego klienta.");
            }
        } catch (Exception exception) {
            client.setArchive(true);
            throw new UserException("Nie udalo sie aktywowac podanego klienta. - nieznany blad");
        }
    }

    public void archiveClient(Client client) {
        if (client == null) {
            throw new MainException("Nie mozna dezaktywowac nieistniejacego klienta!");
        }
        if (client.isArchive()) {
            throw new UserException("Ten klient jest juz zarchiwizowany!");
        }
        try {
            client.setArchive(true);
            if (!userRepository.update(client.getId(), "archive", true)) {
                client.setArchive(false);
                throw new UserException("Nie udalo sie wyrejestrowac podanego klienta.");
            }
        } catch (Exception exception) {
            client.setArchive(false);
            throw new UserException("Nie udalo sie wyrejestrowac podanego klienta. - nieznany blad");
        }
    }

    @Override
    public int usersSize() {
        return userRepository.readAll(UserDTO.class).size();
    }
}
