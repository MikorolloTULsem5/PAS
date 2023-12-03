package nbd.gV.data.datahandling.mappers;

import nbd.gV.data.datahandling.dto.ResourceAdminDTO;
import nbd.gV.model.users.ResourceAdmin;

import java.util.UUID;

public class ResourceAdminMapper {

    public static ResourceAdminDTO toMongoUser(ResourceAdmin resourceAdmin) {
        return new ResourceAdminDTO(resourceAdmin.getId().toString(), resourceAdmin.getLogin(), resourceAdmin.isArchive());
    }

    public static ResourceAdmin fromMongoUser(ResourceAdminDTO resourceAdminDTO) {
        return new ResourceAdmin(UUID.fromString(resourceAdminDTO.getId()), resourceAdminDTO.getLogin());
    }
}
