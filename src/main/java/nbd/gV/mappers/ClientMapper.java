package nbd.gV.mappers;

import lombok.Getter;
import lombok.experimental.FieldDefaults;
import nbd.gV.clients.clienttype.Athlete;
import nbd.gV.clients.Client;
import nbd.gV.clients.clienttype.ClientType;
import nbd.gV.clients.clienttype.Coach;
import nbd.gV.clients.clienttype.Normal;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.util.Objects;
import java.util.UUID;

@Getter
@FieldDefaults(makeFinal = true)
public class ClientMapper {
    @BsonProperty("_id")
    private String clientID;
    @BsonProperty("firstname")
    private String firstName;
    @BsonProperty("lastname")
    private String lastName;
    @BsonProperty("personalid")
    private String personalId;
    @BsonProperty("archive")
    private boolean archive;
    @BsonProperty("clienttype")
    private String clientType;

    @BsonCreator
    public ClientMapper(@BsonProperty("_id") String clientID,
                        @BsonProperty("firstname") String firstName,
                        @BsonProperty("lastname") String lastName,
                        @BsonProperty("personalid") String personalId,
                        @BsonProperty("archive") boolean archive,
                        @BsonProperty("clienttype") String clientType) {
        this.clientID = clientID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.personalId = personalId;
        this.archive = archive;
        this.clientType = clientType;
    }

    public static ClientMapper toMongoClient(Client client) {
        return new ClientMapper(client.getClientId().toString(), client.getFirstName(),
                client.getLastName(), client.getPersonalId(), client.isArchive(),
                client.getClientType().getClientTypeName());
    }

    public static Client fromMongoClient(ClientMapper clientMapper) {
        ClientType type = switch (clientMapper.getClientType()) {
            case "Normal" -> new Normal();
            case "Athlete" -> new Athlete();
            case "Coach" -> new Coach();
            default -> null;
        };

        Client clientModel = new Client(UUID.fromString(clientMapper.getClientID()), clientMapper.getFirstName(),
                clientMapper.getLastName(), clientMapper.getPersonalId(), type);
        clientModel.setArchive(clientMapper.isArchive());
        return clientModel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientMapper that = (ClientMapper) o;
        return archive == that.archive && Objects.equals(clientID, that.clientID) && Objects.equals(firstName,
                that.firstName) && Objects.equals(lastName, that.lastName)
                && Objects.equals(personalId, that.personalId) && Objects.equals(clientType, that.clientType);
    }
}
