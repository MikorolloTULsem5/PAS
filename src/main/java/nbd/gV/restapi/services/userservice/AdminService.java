package nbd.gV.restapi.services.userservice;

import com.mongodb.client.model.Filters;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.NoArgsConstructor;
import nbd.gV.data.datahandling.dto.AdminDTO;
import nbd.gV.data.datahandling.dto.UserDTO;
import nbd.gV.data.datahandling.mappers.AdminMapper;
import nbd.gV.data.repositories.UserMongoRepository;
import nbd.gV.exceptions.MyMongoException;
import nbd.gV.exceptions.UserException;
import nbd.gV.exceptions.UserLoginException;
import nbd.gV.model.users.Admin;

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
        Admin newAdmin = new Admin(UUID.randomUUID(), login);
        try {
            if (!userRepository.read(Filters.eq("login", login), AdminDTO.class).isEmpty()) {
                throw new UserLoginException("Nie udalo sie zarejestrowac administratora w bazie! - admin o tym loginie " +
                        "znajduje sie juz w bazie");
            }

            if (!userRepository.create(AdminMapper.toMongoUser(newAdmin))) {
                throw new UserException("Nie udalo sie zarejestrowac administratora w bazie! - brak odpowiedzi");
            }
        } catch (MyMongoException exception) {
            throw new UserException("Nie udalo sie zarejestrowac administratora w bazie!");
        }
        return newAdmin;
    }

    public Admin getAdminById(UUID adminId) {
        UserDTO adminDTO = userRepository.readByUUID(adminId, AdminDTO.class);
        return adminDTO != null ? AdminMapper.fromMongoUser((AdminDTO) adminDTO) : null;
    }

    public List<Admin> getAllAdmins() {
        List<Admin> adminsList = new ArrayList<>();
        for (var el : userRepository.readAll(AdminDTO.class)) {
            adminsList.add(AdminMapper.fromMongoUser((AdminDTO) el));
        }
        return adminsList;
    }

    public Admin getAdminByLogin(String login) {
        var list = userRepository.read(Filters.eq("login", login), AdminDTO.class);
        return !list.isEmpty() ? AdminMapper.fromMongoUser((AdminDTO) list.get(0)) : null;
    }

    public List<Admin> getAdminByLoginMatching(String login) {
        List<Admin> adminsList = new ArrayList<>();
        for (var el : userRepository.read(Filters.and(Filters.regex("login", ".*%s.*".formatted(login)),
                Filters.eq("_clazz", "admin")), AdminDTO.class)) {
            adminsList.add(AdminMapper.fromMongoUser((AdminDTO) el));
        }
        return adminsList;
    }

    public void modifyAdmin(Admin modifiedAdmin) {
        var list = userRepository.read(Filters.and(
                Filters.eq("login", modifiedAdmin.getLogin()),
                Filters.ne("_id", modifiedAdmin.getId().toString())), AdminDTO.class);
        if (!list.isEmpty()) {
            throw new UserLoginException("Nie udalo sie zmodyfikowac podanego administratora - " +
                    "proba zmiany loginu na login wystepujacy juz u innego administratora");
        }

        if (!userRepository.updateByReplace(modifiedAdmin.getId(), AdminMapper.toMongoUser(modifiedAdmin))) {
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
