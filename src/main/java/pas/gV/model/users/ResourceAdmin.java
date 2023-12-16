package pas.gV.model.users;

import org.codehaus.jackson.annotate.JsonPropertyOrder;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@JsonPropertyOrder({"archive", "id", "login"})
public class ResourceAdmin extends User {

    public ResourceAdmin(UUID id, String login, String password) {
        super(id, login, password);
    }
}
