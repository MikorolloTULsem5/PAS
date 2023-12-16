package pas.gV.model.users;

import lombok.AccessLevel;
import org.codehaus.jackson.annotate.JsonPropertyOrder;
import jakarta.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnore;

import java.util.UUID;

@Getter
@NoArgsConstructor
@JsonPropertyOrder({"archive", "id", "login"})
public abstract class User {

    private UUID id;
    @Setter
    @NotBlank
    private String login;
    ///TODO zapis hasla do bazy i jakos to obsluzyc zeby nie wyswietlalo :<
    @Getter(AccessLevel.NONE)
    private String password;
    @Setter
    private boolean archive = false;

    public User(UUID id, String login, String password) {
        this.id = id;
        this.login = login;
        this.password = password;
    }

}
