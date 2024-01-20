package pas.gV.restapi.data.mappers;

import pas.gV.model.logic.users.Admin;
import pas.gV.restapi.data.dto.AdminDTO;

import java.util.UUID;

public class AdminMapper {

    public static AdminDTO toJsonUser(Admin admin) {
        return new AdminDTO(admin.getId().toString(), admin.getLogin(),
                admin.getPassword(), admin.isArchive());
    }

    public static Admin fromJsonUser(AdminDTO adminDTO) {
        Admin newAdmin = new Admin(UUID.fromString(adminDTO.getId()), adminDTO.getLogin(), adminDTO.getPassword());
        newAdmin.setArchive(adminDTO.isArchive());
        return newAdmin;
    }
}
