package nbd.gV.data.mappers;

import nbd.gV.data.dto.AdminDTO;
import nbd.gV.data.dto.ResourceAdminDTO;
import nbd.gV.users.Admin;
import nbd.gV.users.ResourceAdmin;

import java.util.UUID;

public class ResourceAdminMapper {

    public static ResourceAdminDTO toMongoUser(ResourceAdmin resourceAdmin) {
        return new ResourceAdminDTO(resourceAdmin.getId().toString(), resourceAdmin.getLogin());
    }

    public static ResourceAdmin fromMongoUser(ResourceAdminDTO resourceAdminDTO) {
        return new ResourceAdmin(UUID.fromString(resourceAdminDTO.getId()), resourceAdminDTO.getLogin());
    }
}
