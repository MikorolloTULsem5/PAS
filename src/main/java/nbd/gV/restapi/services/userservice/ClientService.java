package nbd.gV.restapi.services.userservice;

import com.mongodb.client.model.Filters;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nbd.gV.data.datahandling.dto.UserDTO;
import nbd.gV.exceptions.UserLoginException;
import nbd.gV.model.users.Client;
import nbd.gV.exceptions.UserException;
import nbd.gV.exceptions.MyMongoException;
import nbd.gV.data.datahandling.dto.ClientDTO;
import nbd.gV.data.datahandling.mappers.ClientMapper;
import nbd.gV.data.repositories.UserMongoRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
@NoArgsConstructor
@Slf4j
public class ClientService extends UserService {

    @Inject
    private UserMongoRepository userRepository;

    ///TODO kompatybilnosc testow potem wywalic
    public ClientService(UserMongoRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Client registerClient(String firstName, String lastName, String login, String clientType) {
        Client newClient = new Client(UUID.randomUUID(), firstName, lastName, login, clientType);
        try {
            if (!userRepository.read(Filters.eq("login", login), ClientDTO.class).isEmpty()) {
                throw new UserLoginException("Nie udalo sie zarejestrowac klienta w bazie! - klient o tym loginie " +
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

    public Client getClientById(UUID clientID) {
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

    public Client getClientByLogin(String login) {
        var list = userRepository.read(Filters.eq("login", login), ClientDTO.class);
        return !list.isEmpty() ? ClientMapper.fromMongoUser((ClientDTO) list.get(0)) : null;
    }

    public List<Client> getClientByLoginMatching(String login) {
        List<Client> clientsList = new ArrayList<>();
        for (var el : userRepository.read(Filters.and(Filters.regex("login", ".*%s.*".formatted(login)),
                Filters.eq("_clazz", "client")), ClientDTO.class)) {
            clientsList.add(ClientMapper.fromMongoUser((ClientDTO) el));
        }
        return clientsList;
    }

    public void modifyClient(Client modifiedClient) {
        var list = userRepository.read(Filters.and(
                Filters.eq("login", modifiedClient.getLogin()),
                Filters.ne("_id", modifiedClient.getId().toString())), ClientDTO.class);
        if (!list.isEmpty()) {
            throw new UserLoginException("Nie udalo sie zmodyfikowac podanego klienta - " +
                    "proba zmiany loginu na login wystepujacy juz u innego klienta");
        }

        if (!userRepository.updateByReplace(modifiedClient.getId(), ClientMapper.toMongoUser(modifiedClient))) {
            throw new UserException("Nie udalo sie zmodyfikowac podanego klienta.");
        }
    }

    public void activateClient(UUID clientId) {
        ///TODO logi albo wywalic albo obsluzyc
        if (!userRepository.update(clientId, "archive", false)) {
            log.info("Nie udalo sie aktywowac podanego klienta.");
        }
    }

    public void archiveClient(UUID clientId) {
        userRepository.update(clientId, "archive", true);
    }

    @Override
    public int usersSize() {
        return userRepository.readAll(UserDTO.class).size();
    }

    @PostConstruct
    private void init() {
        userRepository.create(ClientMapper.toMongoUser(new Client(UUID.fromString("80e62401-6517-4392-856c-e22ef5f3d6a2"), "Adam", "Smith", "loginek", "normal")));
        userRepository.create(ClientMapper.toMongoUser(new Client(UUID.fromString("b6f5bcb8-7f01-4470-8238-cc3320326157"), "Eva", "Braun", "loginek13", "athlete")));
        userRepository.create(ClientMapper.toMongoUser(new Client(UUID.fromString("6dc63417-0a21-462c-a97a-e0bf6055a3ea"), "Michal", "Pi", "michas13", "coach")));
        userRepository.create(ClientMapper.toMongoUser(new Client(UUID.fromString("3a722080-9668-42a2-9788-4695a4b9f5a7"), "Krzysztof", "Scala", "scKrzy", "normal")));
        userRepository.create(ClientMapper.toMongoUser(new Client(UUID.fromString("126778af-0e19-46d4-b329-0b6b92548f9a"), "Adam", "Scout", "scAdam", "normal")));
    }
}
