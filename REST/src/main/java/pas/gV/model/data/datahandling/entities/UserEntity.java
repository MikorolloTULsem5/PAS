package pas.gV.model.data.datahandling.entities;

import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.util.Objects;

@Getter
@FieldDefaults(makeFinal = true)
public class UserEntity implements Entity {
    @BsonProperty("_id")
    private String id;
    @BsonProperty("login")
    private String login;
    @BsonProperty("archive")
    private boolean archive;
    @BsonProperty("password")
    private String password;
    @BsonCreator
    public UserEntity(@BsonProperty("_id") String id,
                      @BsonProperty("login") String login,
                      @BsonProperty("password") String password,
                      @BsonProperty("archive") boolean archive) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.archive = archive;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserEntity userDTO = (UserEntity) o;
        return Objects.equals(id, userDTO.id) && Objects.equals(login, userDTO.login);
    }

}
