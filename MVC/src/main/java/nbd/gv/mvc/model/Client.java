package nbd.gv.mvc.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import jakarta.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonPropertyOrder({"archive", "id", "login", "clientTypeName", "firstName", "lastName"})
public class Client extends User {
    @JsonProperty("firstName")
    @NotBlank
    private String firstName;
    @JsonProperty("lastName")
    @NotBlank
    private String lastName;
    @JsonProperty("clientTypeName")
    private String clientType;

    @JsonCreator
    public Client(@JsonProperty("id") String id,
                  @JsonProperty("firstName") String firstName,
                  @JsonProperty("lastName") String lastName,
                  @JsonProperty("login") String login,
                  @JsonProperty("password") String password,
                  @JsonProperty("archive") boolean archive,
                  @JsonProperty("clientTypeName") String clientType) {
        super(id, login, password, archive);
        this.firstName = firstName;
        this.lastName = lastName;
        this.clientType = clientType;
    }
}
