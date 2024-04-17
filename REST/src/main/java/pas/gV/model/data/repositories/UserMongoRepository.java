package pas.gV.model.data.repositories;

import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.CreateCollectionOptions;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ValidationOptions;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import jakarta.validation.UnexpectedTypeException;

import org.springframework.stereotype.Component;

import pas.gV.model.data.datahandling.entities.AdminEntity;
import pas.gV.model.data.datahandling.entities.ClientEntity;
import pas.gV.model.data.datahandling.entities.ResourceAdminEntity;
import pas.gV.model.data.datahandling.entities.UserEntity;
import pas.gV.model.data.datahandling.mappers.AdminMapper;
import pas.gV.model.data.datahandling.mappers.ClientMapper;
import pas.gV.model.data.datahandling.mappers.ResourceAdminMapper;
import pas.gV.model.exceptions.MyMongoException;
import pas.gV.model.exceptions.UserException;
import pas.gV.model.exceptions.UserLoginException;
import pas.gV.model.logic.users.Admin;
import pas.gV.model.logic.users.Client;
import pas.gV.model.logic.users.ResourceAdmin;
import pas.gV.model.logic.users.User;

import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class UserMongoRepository extends AbstractMongoRepository<User> {

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
    protected MongoCollection<UserEntity> getCollection() {
        return getDatabase().getCollection(COLLECTION_NAME, UserEntity.class);
    }

    @Override
    public String getCollectionName() {
        return COLLECTION_NAME;
    }

    private boolean createNew(UserEntity dto) {
        InsertOneResult result;
        try {
            result = this.getCollection().insertOne(dto);
        } catch (MongoWriteException e) {
            throw new MyMongoException(e.getMessage());
        }
        return result.wasAcknowledged();
    }

    @Override
    public User create(User initUser) {
        User newUser;

        if (initUser instanceof Client client) {
            newUser = new Client(UUID.randomUUID(), client.getFirstName(), client.getLastName(),
                    client.getLogin(), client.getPassword(), client.getClientTypeName());
            if (!read(Filters.eq("login", client.getLogin()), Client.class).isEmpty()) {
                throw new UserLoginException("Nie udalo sie zarejestrowac klienta w bazie! - klient o tym loginie " +
                        "znajduje sie juz w bazie");
            }

            if (!createNew(ClientMapper.toMongoUser((Client) newUser))) {
                throw new UserException("Nie udalo sie zarejestrowac klienta w bazie! - brak odpowiedzi");
            }
        } else if (initUser instanceof Admin admin) {
            newUser = new Admin(UUID.randomUUID(), admin.getLogin(), admin.getPassword());
            if (!read(Filters.eq("login", admin.getLogin()), Admin.class).isEmpty()) {
                throw new UserLoginException("Nie udalo sie zarejestrowac administratora w bazie! - admin o tym loginie " +
                        "znajduje sie juz w bazie");
            }

            if (!createNew(AdminMapper.toMongoUser((Admin) newUser))) {
                throw new UserException("Nie udalo sie zarejestrowac administratora w bazie! - brak odpowiedzi");
            }
        } else if (initUser instanceof ResourceAdmin resourceAdmin) {
            newUser = new ResourceAdmin(UUID.randomUUID(), resourceAdmin.getLogin(), resourceAdmin.getPassword());
            if (!read(Filters.eq("login", resourceAdmin.getLogin()), ResourceAdmin.class).isEmpty()) {
                throw new UserLoginException("Nie udalo sie zarejestrowac administratora w bazie! - admin o tym loginie " +
                        "znajduje sie juz w bazie");
            }

            if (!createNew(ResourceAdminMapper.toMongoUser((ResourceAdmin) newUser))) {
                throw new UserException("Nie udalo sie zarejestrowac administratora w bazie! - brak odpowiedzi");
            }
        } else {
            throw new UnexpectedTypeException("Typ danego uzytkownika nie pasuje do zadnego z obslugiwanych!");
        }

        return newUser;
    }

    public List<User> read(Bson filter, Class<? extends User> clazz) {
        Class<? extends UserEntity> clazzDTO = switch (clazz.getSimpleName().toLowerCase()) {
            case "client" -> ClientEntity.class;
            case "admin" -> AdminEntity.class;
            case "resourceadmin" -> ResourceAdminEntity.class;
            default ->
                    throw new UnexpectedTypeException("Typ danego uzytkownika nie pasuje do zadnego z obslugiwanych!");
        };

        List<User> list = new ArrayList<>();
        for (var userDTO : this.getDatabase().getCollection(COLLECTION_NAME, clazzDTO).find(filter).into(new ArrayList<>())) {
            if (userDTO instanceof ClientEntity clientEntity) {
                list.add(ClientMapper.fromMongoUser(clientEntity));
            } else if (userDTO instanceof AdminEntity adminDTO) {
                list.add(AdminMapper.fromMongoUser(adminDTO));
            } else if (userDTO instanceof ResourceAdminEntity resourceAdminDTO) {
                list.add(ResourceAdminMapper.fromMongoUser(resourceAdminDTO));
            }
        }
        return list;
    }

    public List<User> readAll(Class<? extends User> clazz) {
        return this.read(Filters.empty(), clazz);
    }

    public User readByUUID(UUID uuid, Class<? extends User> clazz) {
        Bson filter = Filters.eq("_id", uuid.toString());
        var list = this.read(filter, clazz);
        return !list.isEmpty() ? list.get(0) : null;
    }

    @Override
    public boolean updateByReplace(UUID uuid, User user) {
        if (user.getPassword() == null || user.getPassword().isBlank()) {
            var list = getCollection().find(Filters.eq("_id", uuid.toString())).into(new ArrayList<>());
            user.setPassword(list.isEmpty() ? "bezHasla123" : list.get(0).getPassword());
        }

        Bson filter = Filters.eq("_id", uuid.toString());
        UpdateResult result;

        if (user instanceof Client client) {
            result = getCollection().replaceOne(filter, ClientMapper.toMongoUser(client));
        } else if (user instanceof Admin admin) {
            result = getCollection().replaceOne(filter, AdminMapper.toMongoUser(admin));
        } else if (user instanceof ResourceAdmin resourceAdmin) {
            result = getCollection().replaceOne(filter, ResourceAdminMapper.toMongoUser(resourceAdmin));
        } else {
            throw new UnexpectedTypeException("Typ danego uzytkownika nie pasuje do zadnego z obslugiwanych!");
        }

        return result.getModifiedCount() != 0;
    }

    /* Switch off reading methods from super */
    @Override
    public List<User> read(Bson filter) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<User> readAll() {
        throw new UnsupportedOperationException();
    }

    @Override
    public User readByUUID(UUID uuid) {
        throw new UnsupportedOperationException();
    }

    @PostConstruct
    private void init() {
        destroy();

        createNew(ClientMapper.toMongoUser(new Client(UUID.fromString("80e62401-6517-4392-856c-e22ef5f3d6a2"), "Johnny", "Brown", "login", "$2a$10$Hs5/PjCvwqCaQ5r9HrQMgOSvu2DI9xOQr9sm6EfodaFYnFFLfyU3W", "normal")));
        createNew(ClientMapper.toMongoUser(new Client(UUID.fromString("b6f5bcb8-7f01-4470-8238-cc3320326157"), "Rose", "Tetris", "login15", "$2a$10$Hs5/PjCvwqCaQ5r9HrQMgOSvu2DI9xOQr9sm6EfodaFYnFFLfyU3W", "athlete")));
        createNew(ClientMapper.toMongoUser(new Client(UUID.fromString("6dc63417-0a21-462c-a97a-e0bf6055a3ea"), "John", "Lee", "leeJo15", "$2a$10$Hs5/PjCvwqCaQ5r9HrQMgOSvu2DI9xOQr9sm6EfodaFYnFFLfyU3W", "coach")));
        createNew(ClientMapper.toMongoUser(new Client(UUID.fromString("3a722080-9668-42a2-9788-4695a4b9f5a7"), "Krzysztof", "Scala", "scKrzy", "$2a$10$Hs5/PjCvwqCaQ5r9HrQMgOSvu2DI9xOQr9sm6EfodaFYnFFLfyU3W", "normal")));
        createNew(ClientMapper.toMongoUser(new Client(UUID.fromString("126778af-0e19-46d4-b329-0b6b92548f9a"), "Adam", "Scout", "scAdam", "$2a$10$Hs5/PjCvwqCaQ5r9HrQMgOSvu2DI9xOQr9sm6EfodaFYnFFLfyU3W", "normal")));

        createNew(AdminMapper.toMongoUser(new Admin(UUID.fromString("3b197615-6931-4aad-941a-44f78f527053"), "mainAdmin1@example", "$2a$10$Hs5/PjCvwqCaQ5r9HrQMgOSvu2DI9xOQr9sm6EfodaFYnFFLfyU3W")));
        createNew(AdminMapper.toMongoUser(new Admin(UUID.fromString("4844c398-5cf1-44e0-a6d8-34c8a939d2ea"), "secondAdmin2@example", "$2a$10$Hs5/PjCvwqCaQ5r9HrQMgOSvu2DI9xOQr9sm6EfodaFYnFFLfyU3W")));

        createNew(ResourceAdminMapper.toMongoUser(new ResourceAdmin(UUID.fromString("83b29a7a-aa96-4ff2-823d-f3d0d6372c94"), "admRes1@test", "$2a$10$Hs5/PjCvwqCaQ5r9HrQMgOSvu2DI9xOQr9sm6EfodaFYnFFLfyU3W")));
        createNew(ResourceAdminMapper.toMongoUser(new ResourceAdmin(UUID.fromString("a2f6cb49-5e9d-4069-ab91-f337224e833a"), "admRes2@test", "$2a$10$Hs5/PjCvwqCaQ5r9HrQMgOSvu2DI9xOQr9sm6EfodaFYnFFLfyU3W")));
    }

    @PreDestroy
    private void destroy() {
        getCollection().deleteMany(Filters.empty());
    }
}
