package pas.gV.restapi.data.mappers;

import pas.gV.model.logic.users.Admin;
import pas.gV.restapi.data.dto.AdminDTO;

import java.util.UUID;

public class AdminMapper {

    public static AdminDTO toJsonUser(Admin admin) {
        if (admin == null) {
            return null;
        }

        return new AdminDTO(admin.getId().toString(), admin.getLogin(),
                admin.getPassword(), admin.isArchive());
    }

    public static Admin fromJsonUser(AdminDTO adminDTO) {
        if (adminDTO == null) {
            return null;
        }

        Admin newAdmin = new Admin(adminDTO.getId() != null ? UUID.fromString(adminDTO.getId()) : null,
                adminDTO.getLogin(),
                adminDTO.getPassword());
        newAdmin.setArchive(adminDTO.isArchive());
        return newAdmin;
    }
}
