package pas.gV.restapi.services.userservice;

import com.mongodb.client.model.Filters;
import jakarta.validation.UnexpectedTypeException;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pas.gV.model.data.repositories.UserMongoRepository;
import pas.gV.model.exceptions.MyMongoException;
import pas.gV.model.exceptions.UserException;
import pas.gV.model.exceptions.UserLoginException;
import pas.gV.model.logic.users.Admin;
import pas.gV.model.logic.users.User;
import pas.gV.restapi.data.dto.AdminDTO;
import pas.gV.restapi.data.mappers.AdminMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@NoArgsConstructor
public class AdminService extends UserService {

    private UserMongoRepository userRepository;

    @Autowired
    public AdminService(UserMongoRepository userRepository) {
        this.userRepository = userRepository;
    }

    public AdminDTO registerAdmin(String login, String password) {
        try {
            return AdminMapper.toJsonUser((Admin) userRepository.create(new Admin(null, login, password)));
        } catch (MyMongoException | UnexpectedTypeException exception) {
            throw new UserException("Nie udalo sie zarejestrowac administratora w bazie! - " + exception.getMessage());
        }
    }

    public AdminDTO getAdminById(UUID adminId) {
        return getAdminById(adminId.toString());
    }

    public AdminDTO getAdminById(String adminId) {
        User admin = userRepository.readByUUID(UUID.fromString(adminId), Admin.class);
        return admin != null ? AdminMapper.toJsonUser((Admin) admin) : null;
    }

    public List<AdminDTO> getAllAdmins() {
        List<AdminDTO> list = new ArrayList<>();
        for (var user : userRepository.readAll(Admin.class)) {
            if (user instanceof Admin admin) {
                list.add(AdminMapper.toJsonUser(admin));
            }
        }
        return list;
    }

    public AdminDTO getAdminByLogin(String login) {
        var list = userRepository.read(Filters.eq("login", login), Admin.class);
        return !list.isEmpty() ? AdminMapper.toJsonUser((Admin) list.get(0)) : null;
    }

    public List<AdminDTO> getAdminByLoginMatching(String login) {
        List<AdminDTO> list = new ArrayList<>();
        for (var user : userRepository.read(Filters.and(Filters.regex("login", ".*%s.*".formatted(login)),
                Filters.eq("_clazz", "admin")), Admin.class)) {
            list.add(AdminMapper.toJsonUser((Admin) user));
        }
        return list;
    }

    public void modifyAdmin(AdminDTO modifiedAdmin) {
        var list = userRepository.read(Filters.and(
                Filters.eq("login", modifiedAdmin.getLogin()),
                Filters.ne("_id", modifiedAdmin.getId())), Admin.class);
        if (!list.isEmpty()) {
            throw new UserLoginException("Nie udalo sie zmodyfikowac podanego administratora - " +
                    "proba zmiany loginu na login wystepujacy juz u innego administratora");
        }

        if (!userRepository.updateByReplace(UUID.fromString(modifiedAdmin.getId()),
                AdminMapper.fromJsonUser(modifiedAdmin))) {
            throw new UserException("Nie udalo sie zmodyfikowac podanego administratora.");
        }
    }

    public void activateAdmin(UUID adminId) {
        activateAdmin(adminId.toString());
    }

    public void activateAdmin(String adminId) {
        userRepository.update(UUID.fromString(adminId), "archive", false);
    }

    public void deactivateAdmin(UUID adminId) {
        deactivateAdmin(adminId.toString());
    }

    public void deactivateAdmin(String adminId) {
        userRepository.update(UUID.fromString(adminId), "archive", true);
    }

    @Override
    public int usersSize() {
        return userRepository.readAll(User.class).size();
    }

}
