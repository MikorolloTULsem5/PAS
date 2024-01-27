package pas.gV.model.logic.users;

import jakarta.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@NoArgsConstructor
public abstract class User {

    private UUID id;
    @Setter
    @NotBlank
    private String login;
    @Setter
    private String password;

    @Setter
    private boolean archive = false;

    public User(UUID id, String login, String password) {
        this.id = id;
        this.login = login;
        this.password = password;
    }
}
