package pas.gV.model.users;

import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
public class ResourceAdmin extends User {

    public ResourceAdmin(UUID id, String login, String password) {
        super(id, login, password);
    }
}
