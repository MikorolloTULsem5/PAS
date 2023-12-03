package nbd.gV.restapi.services.userservice;

import com.mongodb.client.model.Filters;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.UnexpectedTypeException;
import lombok.NoArgsConstructor;
import nbd.gV.data.datahandling.dto.AdminDTO;
import nbd.gV.data.datahandling.dto.ResourceAdminDTO;
import nbd.gV.data.datahandling.dto.UserDTO;
import nbd.gV.data.datahandling.mappers.ResourceAdminMapper;
import nbd.gV.data.repositories.UserMongoRepository;
import nbd.gV.exceptions.MyMongoException;
import nbd.gV.exceptions.UserException;
import nbd.gV.exceptions.UserLoginException;
import nbd.gV.model.users.Admin;
import nbd.gV.model.users.ResourceAdmin;

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
            return (ResourceAdmin) userRepository.createNew(new ResourceAdminDTO(null, login, false));
        } catch (MyMongoException | UnexpectedTypeException exception) {
            throw new UserException("Nie udalo sie zarejestrowac administratora danych w bazie! - " + exception.getMessage());
        }
    }

    public ResourceAdmin getResourceAdminById(UUID resourceAdminId) {
        UserDTO resourceAdminDTO = userRepository.readByUUID(resourceAdminId, ResourceAdminDTO.class);
        return resourceAdminDTO != null ? ResourceAdminMapper.fromMongoUser((ResourceAdminDTO) resourceAdminDTO) : null;
    }

    public List<ResourceAdmin> getAllResourceAdmins() {
        List<ResourceAdmin> resourceAdminList = new ArrayList<>();
        for (var el : userRepository.readAll(ResourceAdminDTO.class)) {
            resourceAdminList.add(ResourceAdminMapper.fromMongoUser((ResourceAdminDTO) el));
        }
        return resourceAdminList;
    }

    public ResourceAdmin getResourceAdminByLogin(String login) {
        var list = userRepository.read(Filters.eq("login", login), ResourceAdminDTO.class);
        return !list.isEmpty() ? ResourceAdminMapper.fromMongoUser((ResourceAdminDTO) list.get(0)) : null;
    }

    public List<ResourceAdmin> getResourceAdminByLoginMatching(String login) {
        List<ResourceAdmin> resourceAdminList = new ArrayList<>();
        for (var el : userRepository.read(Filters.and(Filters.regex("login", ".*%s.*".formatted(login)),
                Filters.eq("_clazz", "resourceadmin")), ResourceAdminDTO.class)) {
            resourceAdminList.add(ResourceAdminMapper.fromMongoUser((ResourceAdminDTO) el));
        }
        return resourceAdminList;
    }

    public void modifyResourceAdmin(ResourceAdmin modifiedResourceAdmin) {
        var list = userRepository.read(Filters.and(
                Filters.eq("login", modifiedResourceAdmin.getLogin()),
                Filters.ne("_id", modifiedResourceAdmin.getId().toString())), ResourceAdminDTO.class);
        if (!list.isEmpty()) {
            throw new UserLoginException("Nie udalo sie zmodyfikowac podanego administratora - " +
                    "proba zmiany loginu na login wystepujacy juz u innego administratora");
        }

        if (!userRepository.updateByReplace(modifiedResourceAdmin.getId(), ResourceAdminMapper.toMongoUser(modifiedResourceAdmin))) {
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
        return userRepository.readAll(UserDTO.class).size();
    }

}
