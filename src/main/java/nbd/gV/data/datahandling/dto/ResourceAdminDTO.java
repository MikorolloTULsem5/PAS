package nbd.gV.data.datahandling.dto;


import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonProperty;

@Getter
@FieldDefaults(makeFinal = true)
@BsonDiscriminator(key = "_clazz", value = "resourceadmin")
public class ResourceAdminDTO extends UserDTO {

    @BsonCreator
    public ResourceAdminDTO(@BsonProperty("_id") String id,
                            @BsonProperty("login") String login,
                            @BsonProperty("archive") boolean archive) {
        super(id, login, archive);
    }
}
