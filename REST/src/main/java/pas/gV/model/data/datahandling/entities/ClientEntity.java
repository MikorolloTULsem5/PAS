package pas.gV.model.data.datahandling.entities;

import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.util.Objects;

@Getter
@FieldDefaults(makeFinal = true)
@BsonDiscriminator(key = "_clazz", value = "client")
public class ClientEntity extends UserEntity {
    @BsonProperty("firstname")
    private String firstName;
    @BsonProperty("lastname")
    private String lastName;
    @BsonProperty("clienttype")
    private String clientType;

    @BsonCreator
    public ClientEntity(@BsonProperty("_id") String id,
                        @BsonProperty("firstname") String firstName,
                        @BsonProperty("lastname") String lastName,
                        @BsonProperty("login") String login,
                        @BsonProperty("password") String password,
                        @BsonProperty("archive") boolean archive,
                        @BsonProperty("clienttype") String clientType) {
        super(id, login, password, archive);
        this.firstName = firstName;
        this.lastName = lastName;
        this.clientType = clientType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientEntity that = (ClientEntity) o;
        return isArchive() == that.isArchive() && Objects.equals(getId(), that.getId()) && Objects.equals(firstName,
                that.firstName) && Objects.equals(lastName, that.lastName)
                && Objects.equals(getLogin(), that.getLogin()) && Objects.equals(clientType, that.clientType);
    }
}
