package nbd.gV.data.repositories;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.CreateCollectionOptions;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ValidationOptions;
import nbd.gV.data.datahandling.dto.UserDTO;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserMongoRepository extends AbstractMongoRepository<UserDTO> {

    static final String COLLECTION_NAME = "users";

    public UserMongoRepository() {
        boolean collectionExists = getDatabase().listCollectionNames().into(new ArrayList<>()).contains(COLLECTION_NAME);
        if (!collectionExists) {
            ValidationOptions validationOptions = new ValidationOptions().validator(
                    Document.parse("""
                            {
                                "$jsonSchema": {
                                    "bsonType": "object",
                                    "required": [
                                        "login"
                                    ],
                                }
                            }
                            """));
            CreateCollectionOptions createCollectionOptions = new CreateCollectionOptions()
                    .validationOptions(validationOptions);
            getDatabase().createCollection(COLLECTION_NAME, createCollectionOptions);
        }
    }

    @Override
    protected MongoCollection<UserDTO> getCollection() {
        return getDatabase().getCollection(COLLECTION_NAME, UserDTO.class);
    }

    @Override
    public String getCollectionName() {
        return COLLECTION_NAME;
    }

    public List<UserDTO> read(Bson filter, Class<? extends UserDTO> clazz) {
        return this.getDatabase().getCollection(COLLECTION_NAME, clazz).find(filter).into(new ArrayList<>());
    }


    public List<UserDTO> readAll(Class<? extends UserDTO> clazz) {
        return this.read(Filters.empty(), clazz);
    }

    public UserDTO readByUUID(UUID uuid, Class<? extends UserDTO> clazz) {
        Bson filter = Filters.eq("_id", uuid.toString());
        var list = this.read(filter, clazz);
        return !list.isEmpty() ? list.get(0) : null;
    }


    @Override
    public List<UserDTO> read(Bson filter) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<UserDTO> readAll() {
        throw new UnsupportedOperationException();
    }

    @Override
    public UserDTO readByUUID(UUID uuid) {
        throw new UnsupportedOperationException();
    }

//    @Override
//    public boolean delete(UUID uuid) {
//        throw new UnsupportedOperationException();
//    }
}
