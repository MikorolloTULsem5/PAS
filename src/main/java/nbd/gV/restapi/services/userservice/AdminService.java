package nbd.gV.restapi.services.userservice;

import com.mongodb.client.model.Filters;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.UnexpectedTypeException;
import lombok.NoArgsConstructor;
import nbd.gV.data.datahandling.dto.AdminDTO;
import nbd.gV.data.datahandling.dto.UserDTO;
import nbd.gV.data.datahandling.mappers.AdminMapper;
import nbd.gV.data.repositories.UserMongoRepository;
import nbd.gV.exceptions.MyMongoException;
import nbd.gV.exceptions.UserException;
import nbd.gV.exceptions.UserLoginException;
import nbd.gV.model.users.Admin;
import nbd.gV.model.users.User;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
@NoArgsConstructor
public class AdminService extends UserService {

    @Inject
    private UserMongoRepository userRepository;

    public AdminService(UserMongoRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Admin registerAdmin(String login) {
        try {
            return (Admin) userRepository.create(new Admin(null, login));
        } catch (MyMongoException | UnexpectedTypeException exception) {
            throw new UserException("Nie udalo sie zarejestrowac administratora w bazie! - " + exception.getMessage());
        }
    }

    public Admin getAdminById(UUID adminId) {
        User admin = userRepository.readByUUID(adminId, AdminDTO.class);
        return admin != null ? (Admin) admin : null;
    }

    public List<User> getAllAdmins() {
        return userRepository.readAll(AdminDTO.class);
    }

    public Admin getAdminByLogin(String login) {
        var list = userRepository.read(Filters.eq("login", login), AdminDTO.class);
        return !list.isEmpty() ? (Admin) list.get(0) : null;
    }

    public List<User> getAdminByLoginMatching(String login) {
        return userRepository.read(Filters.and(Filters.regex("login", ".*%s.*".formatted(login)),
                Filters.eq("_clazz", "admin")), AdminDTO.class);
    }

    public void modifyAdmin(Admin modifiedAdmin) {
        var list = userRepository.read(Filters.and(
                Filters.eq("login", modifiedAdmin.getLogin()),
                Filters.ne("_id", modifiedAdmin.getId().toString())), AdminDTO.class);
        if (!list.isEmpty()) {
            throw new UserLoginException("Nie udalo sie zmodyfikowac podanego administratora - " +
                    "proba zmiany loginu na login wystepujacy juz u innego administratora");
        }

        if (!userRepository.updateByReplace(modifiedAdmin.getId(), modifiedAdmin)) {
            throw new UserException("Nie udalo sie zmodyfikowac podanego administratora.");
        }
    }

    public void activateAdmin(UUID adminId) {
        userRepository.update(adminId, "archive", false);
    }

    public void deactivateAdmin(UUID adminId) {
        userRepository.update(adminId, "archive", true);
    }

    @Override
    public int usersSize() {
        return userRepository.readAll(UserDTO.class).size();
    }

}
