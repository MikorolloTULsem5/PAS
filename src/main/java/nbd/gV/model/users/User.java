package nbd.gV.model.users;

import jakarta.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@NoArgsConstructor
public abstract class User {

    private UUID id;
    //TODO sprawdzic przy modyfikacji
    @Setter
    @NotBlank
    private String login;
    @Setter
    private boolean archive = false;

    public User(UUID id, String login) {
        this.id = id;
        this.login = login;
    }
}
