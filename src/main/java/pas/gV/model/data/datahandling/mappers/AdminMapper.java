package pas.gV.model.data.datahandling.mappers;

import pas.gV.model.data.datahandling.dto.AdminDTO;
import pas.gV.model.logic.users.Admin;

import java.util.UUID;

public class AdminMapper {

    public static AdminDTO toMongoUser(Admin admin) {
        return new AdminDTO(admin.getId().toString(), admin.getLogin(), admin.getPassword(), admin.isArchive());
    }

    public static Admin fromMongoUser(AdminDTO adminDTO) {
        Admin newAdmin = new Admin(UUID.fromString(adminDTO.getId()), adminDTO.getLogin(), adminDTO.getPassword());
        newAdmin.setArchive(adminDTO.isArchive());
        return newAdmin;
    }
}
