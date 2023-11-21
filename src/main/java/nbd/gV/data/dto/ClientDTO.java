package nbd.gV.data.dto;

import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.util.Objects;

@Getter
@FieldDefaults(makeFinal = true)
public class ClientDTO {
    @BsonProperty("_id")
    private String clientID;
    @BsonProperty("firstname")
    private String firstName;
    @BsonProperty("lastname")
    private String lastName;
    @BsonProperty("login")
    private String login;
    @BsonProperty("archive")
    private boolean archive;
    @BsonProperty("clienttype")
    private String clientType;

    @BsonCreator
    public ClientDTO(@BsonProperty("_id") String clientID,
                     @BsonProperty("firstname") String firstName,
                     @BsonProperty("lastname") String lastName,
                     @BsonProperty("login") String login,
                     @BsonProperty("archive") boolean archive,
                     @BsonProperty("clienttype") String clientType) {
        this.clientID = clientID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.login = login;
        this.archive = archive;
        this.clientType = clientType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientDTO that = (ClientDTO) o;
        return archive == that.archive && Objects.equals(clientID, that.clientID) && Objects.equals(firstName,
                that.firstName) && Objects.equals(lastName, that.lastName)
                && Objects.equals(login, that.login) && Objects.equals(clientType, that.clientType);
    }
}
