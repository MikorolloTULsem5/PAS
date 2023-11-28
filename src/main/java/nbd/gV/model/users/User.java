package nbd.gV.model.users;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.NotEmpty;

import java.util.UUID;

@Getter
@NoArgsConstructor
public abstract class User {

    private UUID id;
    @Setter
    @NotEmpty
    private String login;
    @Setter
    private boolean archive = false;

    public User(UUID id, String login) {
        this.id = id;
        this.login = login;
    }
}
