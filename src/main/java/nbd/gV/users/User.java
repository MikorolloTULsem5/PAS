package nbd.gV.users;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public abstract class User {

    @Setter(AccessLevel.NONE)
    private final UUID id;
    private String login;
    private boolean archive = false;

    public User(UUID id, String login) {
        this.id = id;
        this.login = login;
    }
}
