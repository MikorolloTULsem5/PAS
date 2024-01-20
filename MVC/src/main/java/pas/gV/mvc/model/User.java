package pas.gV.mvc.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import jakarta.validation.constraints.NotBlank;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonPropertyOrder({"archive", "id", "login"})
public class User {
    @JsonProperty("id")
    private String id;
    @JsonProperty("login")
    @NotBlank
    @Size(min = 3, max = 15)
    private String login;
    @JsonProperty("archive")
    private boolean archive;
    @JsonProperty(value = "password", access = JsonProperty.Access.WRITE_ONLY)
    @NotBlank
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{5,}$")
    private String password;
    @JsonCreator
    public User(@JsonProperty("id") String id,
                @JsonProperty("login") String login,
                @JsonProperty("password") String password,
                @JsonProperty("archive") boolean archive) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.archive = archive;
    }

}