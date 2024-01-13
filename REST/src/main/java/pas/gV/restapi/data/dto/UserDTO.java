package pas.gV.restapi.data.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.Objects;

///TODO dodac DTOsy dla pozostalych klas

@Getter
@FieldDefaults(makeFinal = true)
@JsonPropertyOrder({"archive", "id", "login"})
public class UserDTO implements DTO {
    @JsonProperty("id")
    private String id;
    @JsonProperty("login")
    @NotBlank
    private String login;
    @JsonProperty("archive")
    private boolean archive;
    @JsonProperty(value = "password", access = JsonProperty.Access.WRITE_ONLY)
    @NotBlank
    private String password;
    @JsonCreator
    public UserDTO(@JsonProperty("id") String id,
                   @JsonProperty("login") String login,
                   @JsonProperty("password") String password,
                   @JsonProperty("archive") boolean archive) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.archive = archive;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserDTO userDTO = (UserDTO) o;

        if (!Objects.equals(id, userDTO.id)) return false;
        if (archive != userDTO.archive) return false;
        if (!Objects.equals(login, userDTO.login)) return false;
        return Objects.equals(password, userDTO.password);
    }
}