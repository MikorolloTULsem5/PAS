package pas.gV.model.users;

import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
public class Admin extends User {

    public Admin(UUID id, String login) {
        super(id, login);
    }

    public Admin(String login) {
        super(UUID.randomUUID(), login);
    }
}
