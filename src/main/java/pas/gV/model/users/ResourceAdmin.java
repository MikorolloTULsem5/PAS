package pas.gV.model.users;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.NoArgsConstructor;

import java.util.UUID;
@NoArgsConstructor
@JsonPropertyOrder({"archive", "id", "login"})
public class ResourceAdmin extends User {

    public ResourceAdmin(UUID id, String login) {
        super(id, login);
    }
}
