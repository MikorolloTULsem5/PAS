package pas.gV.model.data.datahandling.mappers;

import com.google.common.hash.Hashing;
import pas.gV.model.data.datahandling.entities.AdminEntity;
import pas.gV.model.logic.users.Admin;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;

public class AdminMapper {

    public static AdminEntity toMongoUser(Admin admin) {
        return new AdminEntity(admin.getId().toString(), admin.getLogin(),
                Hashing.sha256().hashString(Objects.requireNonNullElse(admin.getPassword(), ""),
                        StandardCharsets.UTF_8).toString(),
                admin.isArchive());
    }

    public static Admin fromMongoUser(AdminEntity adminDTO) {
        Admin newAdmin = new Admin(UUID.fromString(adminDTO.getId()), adminDTO.getLogin(), adminDTO.getPassword());
        newAdmin.setArchive(adminDTO.isArchive());
        return newAdmin;
    }
}
