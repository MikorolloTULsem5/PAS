package pas.gV.restapi.services.userservice;

import com.mongodb.client.model.Filters;
import jakarta.validation.UnexpectedTypeException;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pas.gV.model.exceptions.UserLoginException;
import pas.gV.model.logic.users.Client;
import pas.gV.model.exceptions.UserException;
import pas.gV.model.exceptions.MyMongoException;
import pas.gV.model.data.repositories.UserMongoRepository;
import pas.gV.model.logic.users.User;
import pas.gV.restapi.data.dto.ClientDTO;
import pas.gV.restapi.data.mappers.ClientMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@NoArgsConstructor
public class ClientService extends UserService {

    private UserMongoRepository userRepository;
    @Autowired
    public ClientService(UserMongoRepository userRepository) {
        this.userRepository = userRepository;
    }

    public ClientDTO registerClient(String firstName, String lastName, String login, String password, String clientType) {
        try {
            return ClientMapper.toJsonUser(
                    (Client) userRepository.create(new Client(null, firstName, lastName, login, password, clientType)));
        } catch (MyMongoException | UnexpectedTypeException exception) {
            throw new UserException("Nie udalo sie zarejestrowac klienta w bazie! - " + exception.getMessage());
        }
    }

    public ClientDTO getClientById(UUID clientID) {
        return getClientById(clientID.toString());
    }

    public ClientDTO getClientById(String clientID) {
        User client = userRepository.readByUUID(UUID.fromString(clientID), Client.class);
        return client != null ? ClientMapper.toJsonUser((Client) client) : null;
    }

    public List<ClientDTO> getAllClients() {
        List<ClientDTO> list = new ArrayList<>();
        for (var user : userRepository.readAll(Client.class)) {
            if (user instanceof Client client) {
                list.add(ClientMapper.toJsonUser(client));
            }
        }
        return list;
    }

    public ClientDTO getClientByLogin(String login) {
        var list = userRepository.read(Filters.eq("login", login), Client.class);
        return !list.isEmpty() ? ClientMapper.toJsonUser((Client) list.get(0)) : null;
    }

    public List<ClientDTO> getClientByLoginMatching(String login) {
        List<ClientDTO> list = new ArrayList<>();
        for (var user : userRepository.read(Filters.and(Filters.regex("login", ".*%s.*".formatted(login)),
                Filters.eq("_clazz", "client")), Client.class)) {
            list.add(ClientMapper.toJsonUser((Client) user));
        }
        return list;
    }

    public void modifyClient(ClientDTO modifiedClient) {
        var list = userRepository.read(Filters.and(
                Filters.eq("login", modifiedClient.getLogin()),
                Filters.ne("_id", modifiedClient.getId())), Client.class);
        if (!list.isEmpty()) {
            throw new UserLoginException("Nie udalo sie zmodyfikowac podanego klienta - " +
                    "proba zmiany loginu na login wystepujacy juz u innego klienta");
        }

        if (!userRepository.updateByReplace(UUID.fromString(modifiedClient.getId()),
                ClientMapper.fromJsonUser(modifiedClient))) {
            throw new UserException("Nie udalo sie zmodyfikowac podanego klienta.");
        }
    }

    public void activateClient(UUID clientId) {
        activateClient(clientId.toString());
    }

    public void activateClient(String clientId) {
        userRepository.update(UUID.fromString(clientId), "archive", false);
    }

    public void deactivateClient(UUID clientId) {
        deactivateClient(clientId.toString());
    }

    public void deactivateClient(String clientId) {
        userRepository.update(UUID.fromString(clientId), "archive", true);
    }

    @Override
    public int usersSize() {
        return userRepository.readAll(User.class).size();
    }

}
