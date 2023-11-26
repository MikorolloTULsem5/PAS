package nbd.gV.model.users;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
public abstract class User {

    private final UUID id;
    private final String login;
    @Setter
    private boolean archive = false;

    public User(UUID id, String login) {
        this.id = id;
        this.login = login;
    }
}
