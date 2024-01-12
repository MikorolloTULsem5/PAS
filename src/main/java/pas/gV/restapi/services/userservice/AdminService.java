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

    public Admin registerAdmin(String login, String password) {
        try {
            return (Admin) userRepository.create(new Admin(null, login, password));
        } catch (MyMongoException | UnexpectedTypeException exception) {
            throw new UserException("Nie udalo sie zarejestrowac administratora w bazie! - " + exception.getMessage());
        }
    }

    public Admin getAdminById(UUID adminId) {
        User admin = userRepository.readByUUID(adminId, Admin.class);
        return admin != null ? (Admin) admin : null;
    }

    public List<Admin> getAllAdmins() {
        List<Admin> list = new ArrayList<>();
        for (var user : userRepository.readAll(Admin.class)) {
            if (user instanceof Admin admin) {
                list.add(admin);
            }
        }
        return list;
    }

    public Admin getAdminByLogin(String login) {
        var list = userRepository.read(Filters.eq("login", login), Admin.class);
        return !list.isEmpty() ? (Admin) list.get(0) : null;
    }

    public List<Admin> getAdminByLoginMatching(String login) {
        List<Admin> list = new ArrayList<>();
        for (var user : userRepository.read(Filters.and(Filters.regex("login", ".*%s.*".formatted(login)),
                Filters.eq("_clazz", "admin")), Admin.class)) {
            list.add((Admin) user);
        }
        return list;
    }

    public void modifyAdmin(Admin modifiedAdmin) {
        var list = userRepository.read(Filters.and(
                Filters.eq("login", modifiedAdmin.getLogin()),
                Filters.ne("_id", modifiedAdmin.getId().toString())), Admin.class);
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
        return userRepository.readAll(User.class).size();
    }

}
