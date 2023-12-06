package nbd.gV.data.datahandling.dto;

import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.util.Objects;

@Getter
@FieldDefaults(makeFinal = true)
public class UserDTO implements DTO {
    @BsonProperty("_id")
    private String id;
    @BsonProperty("login")
    private String login;
    @BsonProperty("archive")
    private boolean archive;

    @BsonCreator
    public UserDTO(@BsonProperty("_id") String id,
                   @BsonProperty("login") String login,
                   @BsonProperty("archive") boolean archive) {
        this.id = id;
        this.login = login;
        this.archive = archive;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDTO userDTO = (UserDTO) o;
        return Objects.equals(id, userDTO.id) && Objects.equals(login, userDTO.login);
    }

}
