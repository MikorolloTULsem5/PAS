package pas.gV.restapi.data.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.Objects;


@Getter
@FieldDefaults(makeFinal = true)
public class ClientDTO extends UserDTO {
    @JsonProperty("firstname")
    private String firstName;
    @JsonProperty("lastname")
    private String lastName;
    @JsonProperty("clienttype")
    private String clientType;

    @JsonCreator
    public ClientDTO(@JsonProperty("_id") String id,
                     @JsonProperty("firstname") String firstName,
                     @JsonProperty("lastname") String lastName,
                     @JsonProperty("login") String login,
                     @JsonProperty("password") String password,
                     @JsonProperty("archive") boolean archive,
                     @JsonProperty("clienttype") String clientType) {
        super(id, login, password, archive);
        this.firstName = firstName;
        this.lastName = lastName;
        this.clientType = clientType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientDTO that = (ClientDTO) o;
        return isArchive() == that.isArchive() && Objects.equals(getId(), that.getId()) && Objects.equals(firstName,
                that.firstName) && Objects.equals(lastName, that.lastName)
                && Objects.equals(getLogin(), that.getLogin()) && Objects.equals(clientType, that.clientType);
    }
}
