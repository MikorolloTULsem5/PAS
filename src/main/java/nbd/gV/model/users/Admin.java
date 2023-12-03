package nbd.gV.model.users;

import java.util.UUID;

public class Admin extends User {

    public Admin(UUID id, String login) {
        super(id, login);
    }

    public Admin(String login) {
        super(UUID.randomUUID(), login);
    }
}
