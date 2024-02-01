package pas.gV.restapi.security.dto;

import com.fasterxml.jackson.annotation.JsonCreator;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import pas.gV.restapi.data.dto.ClientDTO;

@Getter
public class ClientRegisterDTORequest extends ClientDTO {
    @JsonCreator
    public ClientRegisterDTORequest(@JsonProperty("firstName") String firstName,
                                    @JsonProperty("lastName") String lastName,
                                    @JsonProperty("login") String login,
                                    @JsonProperty("password") String password) {
        super(null, firstName, lastName, login, password, false, "normal");
    }
}
