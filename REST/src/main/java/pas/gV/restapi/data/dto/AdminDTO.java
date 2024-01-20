package pas.gV.restapi.data.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(makeFinal = true)
public class AdminDTO extends UserDTO {

    @JsonCreator
    public AdminDTO(@JsonProperty("id") String id,
                    @JsonProperty("login") String login,
                    @JsonProperty("password") String password,
                    @JsonProperty("archive") boolean archive) {
        super(id, login, password, archive);
    }
}
