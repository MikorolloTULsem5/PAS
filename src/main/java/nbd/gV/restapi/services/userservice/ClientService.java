package nbd.gV.restapi.services.userservice;

import com.mongodb.client.model.Filters;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.UnexpectedTypeException;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nbd.gV.data.datahandling.dto.UserDTO;
import nbd.gV.exceptions.UserLoginException;
import nbd.gV.model.users.Client;
import nbd.gV.exceptions.UserException;
import nbd.gV.exceptions.MyMongoException;
import nbd.gV.data.datahandling.dto.ClientDTO;
import nbd.gV.data.repositories.UserMongoRepository;
import nbd.gV.model.users.User;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
@NoArgsConstructor
@Slf4j
public class ClientService extends UserService {

    @Inject
    private UserMongoRepository userRepository;
    public ClientService(UserMongoRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Client registerClient(String firstName, String lastName, String login, String clientType) {
        try {
            return (Client) userRepository.create(new Client(null, firstName, lastName, login, clientType));
        } catch (MyMongoException | UnexpectedTypeException exception) {
            throw new UserException("Nie udalo sie zarejestrowac klienta w bazie! - " + exception.getMessage());
        }
    }

    public Client getClientById(UUID clientID) {
        User client = userRepository.readByUUID(clientID, ClientDTO.class);
        return client != null ? (Client) client : null;
    }

    ///TODO nope
    public List<User> getAllClients() {
        return userRepository.readAll(ClientDTO.class);
    }

    public Client getClientByLogin(String login) {
        var list = userRepository.read(Filters.eq("login", login), ClientDTO.class);
        return !list.isEmpty() ? (Client) list.get(0) : null;
    }

    public List<User> getClientByLoginMatching(String login) {
        return userRepository.read(Filters.and(Filters.regex("login", ".*%s.*".formatted(login)),
                Filters.eq("_clazz", "client")), ClientDTO.class);
    }

    public void modifyClient(Client modifiedClient) {
        var list = userRepository.read(Filters.and(
                Filters.eq("login", modifiedClient.getLogin()),
                Filters.ne("_id", modifiedClient.getId().toString())), ClientDTO.class);
        if (!list.isEmpty()) {
            throw new UserLoginException("Nie udalo sie zmodyfikowac podanego klienta - " +
                    "proba zmiany loginu na login wystepujacy juz u innego klienta");
        }

        if (!userRepository.updateByReplace(modifiedClient.getId(), modifiedClient)) {
            throw new UserException("Nie udalo sie zmodyfikowac podanego klienta.");
        }
    }

    public void activateClient(UUID clientId) {
        userRepository.update(clientId, "archive", false);
    }

    public void deactivateClient(UUID clientId) {
        userRepository.update(clientId, "archive", true);
    }

    @Override
    public int usersSize() {
        return userRepository.readAll(UserDTO.class).size();
    }

}
