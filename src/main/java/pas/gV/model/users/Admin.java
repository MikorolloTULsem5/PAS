package pas.gV.model.users;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@JsonPropertyOrder({"archive", "id", "login"})
public class Admin extends User {

    public Admin(UUID id, String login) {
        super(id, login);
    }

    public Admin(String login) {
        super(UUID.randomUUID(), login);
    }
}
