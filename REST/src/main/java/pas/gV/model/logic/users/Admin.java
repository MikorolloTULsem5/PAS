package pas.gV.model.logic.users;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@JsonPropertyOrder({"archive", "id", "login"})
public class Admin extends User {

    public Admin(UUID id, String login, String password) {
        super(id, login, password);
        this.role = Role.ADMIN;
    }
}
