package pas.gV.model.logic.users;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.NoArgsConstructor;

import java.util.UUID;
@NoArgsConstructor
@JsonPropertyOrder({"archive", "id", "login"})
public class ResourceAdmin extends User {

    public ResourceAdmin(UUID id, String login, String password) {
        super(id, login, password);
        this.role = Role.RESOURCE_ADMIN;
    }
}
