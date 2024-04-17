package pas.gV.model.data.datahandling.mappers;

import pas.gV.model.data.datahandling.entities.ResourceAdminEntity;
import pas.gV.model.logic.users.ResourceAdmin;

import java.util.UUID;

public class ResourceAdminMapper {

    public static ResourceAdminEntity toMongoUser(ResourceAdmin resourceAdmin) {
        return new ResourceAdminEntity(resourceAdmin.getId().toString(), resourceAdmin.getLogin(),
                resourceAdmin.getPassword(),
                resourceAdmin.isArchive());
    }

    public static ResourceAdmin fromMongoUser(ResourceAdminEntity resourceAdminDTO) {
        ResourceAdmin newResourceAdmin = new ResourceAdmin(UUID.fromString(resourceAdminDTO.getId()),
                resourceAdminDTO.getLogin(), resourceAdminDTO.getPassword());
        newResourceAdmin.setArchive(resourceAdminDTO.isArchive());
        return newResourceAdmin;
    }
}
