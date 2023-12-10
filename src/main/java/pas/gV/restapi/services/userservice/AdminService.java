package pas.gV.restapi.services.userservice;

import com.mongodb.client.model.Filters;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.UnexpectedTypeException;
import lombok.NoArgsConstructor;
import pas.gV.data.repositories.UserMongoRepository;
import pas.gV.exceptions.MyMongoException;
import pas.gV.exceptions.UserException;
import pas.gV.exceptions.UserLoginException;
import pas.gV.model.users.Admin;
import pas.gV.model.users.User;

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
        User admin = userRepository.readByUUID(adminId, Admin.class);
        return admin != null ? (Admin) admin : null;
    }

    public List<Admin> getAllAdmins() {
        List<Admin> list = new ArrayList<>();
        for (var user : userRepository.readAll(Admin.class)) {
            list.add((Admin) user);
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
