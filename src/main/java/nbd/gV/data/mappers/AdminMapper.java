package nbd.gV.data.mappers;

import nbd.gV.data.dto.AdminDTO;
import nbd.gV.users.Admin;

import java.util.UUID;

public class AdminMapper {

    public static AdminDTO toMongoUser(Admin admin) {
        return new AdminDTO(admin.getId().toString(), admin.getLogin());
    }

    public static Admin fromMongoUser(AdminDTO adminDTO) {
        return new Admin(UUID.fromString(adminDTO.getId()), adminDTO.getLogin());
    }
}
