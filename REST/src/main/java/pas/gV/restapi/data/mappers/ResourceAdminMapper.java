package pas.gV.restapi.data.mappers;

import pas.gV.model.logic.users.ResourceAdmin;
import pas.gV.restapi.data.dto.ResourceAdminDTO;

import java.util.UUID;

public class ResourceAdminMapper {

    public static ResourceAdminDTO toJsonUser(ResourceAdmin resourceAdmin) {
        return new ResourceAdminDTO(resourceAdmin.getId().toString(), resourceAdmin.getLogin(),
                resourceAdmin.getPassword(), resourceAdmin.isArchive());
    }

    public static ResourceAdmin fromJsonUser(ResourceAdminDTO resourceAdminDTO) {
        ResourceAdmin newAdmin = new ResourceAdmin(UUID.fromString(resourceAdminDTO.getId()),
                resourceAdminDTO.getLogin(), resourceAdminDTO.getPassword());
        newAdmin.setArchive(resourceAdminDTO.isArchive());
        return newAdmin;
    }
}
