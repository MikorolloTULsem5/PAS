package nbd.gV.managers;

import com.mongodb.client.model.Filters;
import nbd.gV.data.dto.AdminDTO;
import nbd.gV.data.dto.UserDTO;
import nbd.gV.data.mappers.AdminMapper;
import nbd.gV.data.mappers.ClientMapper;
import nbd.gV.exceptions.UserException;
import nbd.gV.exceptions.MainException;
import nbd.gV.exceptions.MyMongoException;
import nbd.gV.repositories.UserMongoRepository;
import nbd.gV.users.Admin;
import nbd.gV.users.Client;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AdminManager extends UserManager {

    private final UserMongoRepository userRepository;

    public AdminManager() {
        this.userRepository = new UserMongoRepository();
    }


    public Admin registerAdmin(String login) {
        Admin newAdmin = new Admin(login);
        try {
            if (!userRepository.read(Filters.eq("login", login), AdminDTO.class).isEmpty()) {
                throw new UserException("Nie udalo sie zarejestrowac administratora w bazie! - administrator o tym loginie" +
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

    public Admin getAdmin(UUID adminId) {
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

    public Admin findAdminByLogin(String login) {
        var list = userRepository.read(Filters.eq("login", login), AdminDTO.class);
        return !list.isEmpty() ? AdminMapper.fromMongoUser((AdminDTO) list.get(0)) : null;
    }

    public List<Admin> findAdminByLoginFitting(String login) {
        List<Admin> adminsList = new ArrayList<>();
        for (var el : userRepository.read(Filters.regex("login", ".*%s.*".formatted(login)), AdminDTO.class)) {
            adminsList.add(AdminMapper.fromMongoUser((AdminDTO) el));
        }
        return adminsList;
    }

    public void modifyAdmin(Client modifiedAdmin) {
        if (modifiedAdmin == null) {
            throw new MainException("Nie mozna modyfikowac nieistniejacego administratora!");
        }
        try {
            if (!userRepository.updateByReplace(modifiedAdmin.getId(), ClientMapper.toMongoUser(modifiedAdmin))) {
                throw new UserException("Nie udalo sie wyrejestrowac podanego administratora.");
            }
        } catch (Exception exception) {
            throw new UserException("Nie udalo sie aktywowac podanego administratora. - nieznany blad");
        }
    }

    public void activateAdmin(Admin admin) {
        if (admin == null) {
            throw new MainException("Nie mozna wyrejestrowac nieistniejacego administratora!");
        }
        if (!admin.isArchive()) {
            throw new UserException("Ten administratora jest juz aktywny!");
        }
        try {
            admin.setArchive(false);
            if (!userRepository.update(admin.getId(), "archive", false)) {
                admin.setArchive(true);
                throw new UserException("Nie udalo sie wyrejestrowac podanego administratora.");
            }
        } catch (Exception exception) {
            admin.setArchive(true);
            throw new UserException("Nie udalo sie aktywowac podanego administratora. - nieznany blad");
        }
    }

    public void archiveAdmin(Admin admin) {
        if (admin == null) {
            throw new MainException("Nie mozna dezaktywowac nieistniejacego administratora!");
        }
        if (admin.isArchive()) {
            throw new UserException("Ten administratora jest juz zarchiwizowany!");
        }
        try {
            admin.setArchive(true);
            if (!userRepository.update(admin.getId(), "archive", true)) {
                admin.setArchive(false);
                throw new UserException("Nie udalo sie wyrejestrowac podanego administratora.");
            }
        } catch (Exception exception) {
            admin.setArchive(false);
            throw new UserException("Nie udalo sie wyrejestrowac podanego administratora. - nieznany blad");
        }
    }

    @Override
    public int usersSize() {
        return userRepository.readAll(UserDTO.class).size();
    }
}
