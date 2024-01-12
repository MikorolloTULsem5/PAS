package pas.gV.data.datahandling.mappers;

import pas.gV.data.datahandling.dto.ResourceAdminDTO;
import pas.gV.model.users.ResourceAdmin;

import java.util.UUID;

public class ResourceAdminMapper {

    public static ResourceAdminDTO toMongoUser(ResourceAdmin resourceAdmin) {
        return new ResourceAdminDTO(resourceAdmin.getId().toString(), resourceAdmin.getLogin(),
                resourceAdmin.getPassword(), resourceAdmin.isArchive());
    }

    public static ResourceAdmin fromMongoUser(ResourceAdminDTO resourceAdminDTO) {
        ResourceAdmin newResourceAdmin = new ResourceAdmin(UUID.fromString(resourceAdminDTO.getId()),
                resourceAdminDTO.getLogin(), resourceAdminDTO.getPassword());
        newResourceAdmin.setArchive(resourceAdminDTO.isArchive());
        return newResourceAdmin;
    }
}
