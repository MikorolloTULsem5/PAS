package pas.gV.model.data.datahandling.mappers;

import com.google.common.hash.Hashing;
import pas.gV.model.data.datahandling.entities.ResourceAdminEntity;
import pas.gV.model.logic.users.ResourceAdmin;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;

public class ResourceAdminMapper {

    public static ResourceAdminEntity toMongoUser(ResourceAdmin resourceAdmin) {
        return new ResourceAdminEntity(resourceAdmin.getId().toString(), resourceAdmin.getLogin(),
                Hashing.sha256().hashString(Objects.requireNonNullElse(resourceAdmin.getPassword(), ""),
                        StandardCharsets.UTF_8).toString(),
                resourceAdmin.isArchive());
    }

    public static ResourceAdmin fromMongoUser(ResourceAdminEntity resourceAdminDTO) {
        ResourceAdmin newResourceAdmin = new ResourceAdmin(UUID.fromString(resourceAdminDTO.getId()),
                resourceAdminDTO.getLogin(), resourceAdminDTO.getPassword());
        newResourceAdmin.setArchive(resourceAdminDTO.isArchive());
        return newResourceAdmin;
    }
}
