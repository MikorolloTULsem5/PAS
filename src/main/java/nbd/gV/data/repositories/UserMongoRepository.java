package nbd.gV.data.repositories;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.CreateCollectionOptions;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ValidationOptions;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import nbd.gV.data.datahandling.dto.UserDTO;
import nbd.gV.data.datahandling.mappers.AdminMapper;
import nbd.gV.data.datahandling.mappers.ClientMapper;
import nbd.gV.model.users.Admin;
import nbd.gV.model.users.Client;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
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

    @PostConstruct
    private void init() {
        destroy();

        create(ClientMapper.toMongoUser(new Client(UUID.fromString("80e62401-6517-4392-856c-e22ef5f3d6a2"), "Johnny", "Brown", "login", "normal")));
        create(ClientMapper.toMongoUser(new Client(UUID.fromString("b6f5bcb8-7f01-4470-8238-cc3320326157"), "Rose", "Tetris", "login15", "athlete")));
        create(ClientMapper.toMongoUser(new Client(UUID.fromString("6dc63417-0a21-462c-a97a-e0bf6055a3ea"), "John", "Lee", "leeJo15", "coach")));
        create(ClientMapper.toMongoUser(new Client(UUID.fromString("3a722080-9668-42a2-9788-4695a4b9f5a7"), "Krzysztof", "Scala", "scKrzy", "normal")));
        create(ClientMapper.toMongoUser(new Client(UUID.fromString("126778af-0e19-46d4-b329-0b6b92548f9a"), "Adam", "Scout", "scAdam", "normal")));

        create(AdminMapper.toMongoUser(new Admin(UUID.fromString("3b197615-6931-4aad-941a-44f78f527053"), "mainAdmin1@example")));
        create(AdminMapper.toMongoUser(new Admin(UUID.fromString("4844c398-5cf1-44e0-a6d8-34c8a939d2ea"), "secondAdmin2@example")));
    }

    @PreDestroy
    private void destroy() {
        getCollection().deleteMany(Filters.empty());
    }
}
