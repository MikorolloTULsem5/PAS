package pas.gV.model.users;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@NoArgsConstructor
@JsonPropertyOrder({"archive", "id", "login"})
public abstract class User {

    private UUID id;
    @Setter
    @NotBlank
    private String login;
//    @NotBlank
    private String password;

    @Setter
    private boolean archive = false;

    public User(UUID id, String login, String password) {
        this.id = id;
        this.login = login;
        this.password = password;
    }
}
