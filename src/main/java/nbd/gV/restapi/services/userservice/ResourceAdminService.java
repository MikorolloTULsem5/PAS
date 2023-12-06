package nbd.gV.restapi.services.userservice;

import com.mongodb.client.model.Filters;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.UnexpectedTypeException;
import lombok.NoArgsConstructor;
import nbd.gV.data.datahandling.dto.ClientDTO;
import nbd.gV.data.datahandling.dto.ResourceAdminDTO;
import nbd.gV.data.datahandling.dto.UserDTO;
import nbd.gV.data.repositories.UserMongoRepository;
import nbd.gV.exceptions.MyMongoException;
import nbd.gV.exceptions.UserException;
import nbd.gV.exceptions.UserLoginException;
import nbd.gV.model.users.Client;
import nbd.gV.model.users.ResourceAdmin;
import nbd.gV.model.users.User;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
@NoArgsConstructor
public class ResourceAdminService extends UserService {

    @Inject
    private UserMongoRepository userRepository;

    public ResourceAdminService(UserMongoRepository userRepository) {
        this.userRepository = userRepository;
    }

    public ResourceAdmin registerResourceAdmin(String login) {
        try {
            return (ResourceAdmin) userRepository.create(new ResourceAdmin(null, login));
        } catch (MyMongoException | UnexpectedTypeException exception) {
            throw new UserException("Nie udalo sie zarejestrowac administratora danych w bazie! - " + exception.getMessage());
        }
    }

    public ResourceAdmin getResourceAdminById(UUID resourceAdminId) {
        User resourceAdmin = userRepository.readByUUID(resourceAdminId, ResourceAdmin.class);
        return resourceAdmin != null ? (ResourceAdmin) resourceAdmin : null;
    }

    public List<User> getAllResourceAdmins() {
        return userRepository.readAll(ResourceAdmin.class);
    }

    public ResourceAdmin getResourceAdminByLogin(String login) {
        var list = userRepository.read(Filters.eq("login", login), ResourceAdmin.class);
        return !list.isEmpty() ? (ResourceAdmin) list.get(0) : null;
    }

    public List<ResourceAdmin> getResourceAdminByLoginMatching(String login) {
        List<ResourceAdmin> list = new ArrayList<>();
        for (var user : userRepository.read(Filters.and(Filters.regex("login", ".*%s.*".formatted(login)),
                Filters.eq("_clazz", "resourceadmin")), ResourceAdmin.class)) {
            list.add((ResourceAdmin) user);
        }
        return list;
    }

    public void modifyResourceAdmin(ResourceAdmin modifiedResourceAdmin) {
        var list = userRepository.read(Filters.and(
                Filters.eq("login", modifiedResourceAdmin.getLogin()),
                Filters.ne("_id", modifiedResourceAdmin.getId().toString())), ResourceAdmin.class);
        if (!list.isEmpty()) {
            throw new UserLoginException("Nie udalo sie zmodyfikowac podanego administratora - " +
                    "proba zmiany loginu na login wystepujacy juz u innego administratora");
        }

        if (!userRepository.updateByReplace(modifiedResourceAdmin.getId(), modifiedResourceAdmin)) {
            throw new UserException("Nie udalo sie zmodyfikowac podanego administratora.");
        }
    }

    public void activateResourceAdmin(UUID resourceAdminId) {
        userRepository.update(resourceAdminId, "archive", false);
    }

    public void deactivateResourceAdmin(UUID resourceAdminId) {
        userRepository.update(resourceAdminId, "archive", true);
    }

    @Override
    public int usersSize() {
        return userRepository.readAll(User.class).size();
    }

}