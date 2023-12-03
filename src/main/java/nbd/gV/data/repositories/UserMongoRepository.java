package nbd.gV.data.repositories;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.CreateCollectionOptions;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ValidationOptions;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import nbd.gV.data.datahandling.dto.ClientDTO;
import nbd.gV.data.datahandling.dto.UserDTO;
import nbd.gV.data.datahandling.mappers.AdminMapper;
import nbd.gV.data.datahandling.mappers.ClientMapper;
import nbd.gV.data.datahandling.mappers.ResourceAdminMapper;
import nbd.gV.exceptions.UserException;
import nbd.gV.exceptions.UserLoginException;
import nbd.gV.model.users.Admin;
import nbd.gV.model.users.Client;
import nbd.gV.model.users.ResourceAdmin;
import nbd.gV.model.users.User;
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
//        return this.read(Filters.empty(), clazz);
        String name = clazz.getSimpleName().toLowerCase();
        return this.read(Filters.eq("_clazz", name.substring(0, name.length() - 3)), clazz);
    }

    public UserDTO readByUUID(UUID uuid, Class<? extends UserDTO> clazz) {
        Bson filter = Filters.eq("_id", uuid.toString());
        var list = this.read(filter, clazz);
        return !list.isEmpty() ? list.get(0) : null;
    }

    public User createNew(UserDTO userDTO) {
        ClientDTO clientDTO = (ClientDTO) userDTO;
        Client newClient = new Client(UUID.randomUUID(), clientDTO.getFirstName(), clientDTO.getLastName(),
                clientDTO.getLogin(), clientDTO.getClientType());
        if (!read(Filters.eq("login", clientDTO.getLogin()), ClientDTO.class).isEmpty()) {
            throw new UserLoginException("Nie udalo sie zarejestrowac klienta w bazie! - klient o tym loginie " +
                    "znajduje sie juz w bazie");
        }

        if (!super.create(ClientMapper.toMongoUser(newClient))) {
            throw new UserException("Nie udalo sie zarejestrowac klienta w bazie! - brak odpowiedzi");
        }
        return newClient;
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

        create(ResourceAdminMapper.toMongoUser(new ResourceAdmin(UUID.fromString("83b29a7a-aa96-4ff2-823d-f3d0d6372c94"), "admRes1@test")));
        create(ResourceAdminMapper.toMongoUser(new ResourceAdmin(UUID.fromString("a2f6cb49-5e9d-4069-ab91-f337224e833a"), "admRes2@test")));
    }

    @PreDestroy
    private void destroy() {
        getCollection().deleteMany(Filters.empty());
    }
}
