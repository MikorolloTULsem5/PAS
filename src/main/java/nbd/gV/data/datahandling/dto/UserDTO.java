package nbd.gV.data.datahandling.dto;

import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.util.Objects;

@Getter
@FieldDefaults(makeFinal = true)
public class UserDTO {
    @BsonProperty("_id")
    private String id;
    @BsonProperty
    private String login;

    @BsonCreator
    public UserDTO(@BsonProperty("_id") String id,
                   @BsonProperty("login") String login) {
        this.id = id;
        this.login = login;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDTO userDTO = (UserDTO) o;
        return Objects.equals(id, userDTO.id) && Objects.equals(login, userDTO.login);
    }

}
