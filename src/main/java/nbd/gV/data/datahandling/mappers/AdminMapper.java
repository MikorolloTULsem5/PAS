package nbd.gV.data.datahandling.mappers;

import nbd.gV.data.datahandling.dto.AdminDTO;
import nbd.gV.model.users.Admin;

import java.util.UUID;

public class AdminMapper {

    public static AdminDTO toMongoUser(Admin admin) {
        return new AdminDTO(admin.getId().toString(), admin.getLogin());
    }

    public static Admin fromMongoUser(AdminDTO adminDTO) {
        return new Admin(UUID.fromString(adminDTO.getId()), adminDTO.getLogin());
    }
}
