package nbd.gV.data.repositories;

import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.CreateCollectionOptions;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ValidationOptions;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.UnexpectedTypeException;
import nbd.gV.data.datahandling.dto.AdminDTO;
import nbd.gV.data.datahandling.dto.ClientDTO;
import nbd.gV.data.datahandling.dto.DTO;
import nbd.gV.data.datahandling.dto.ResourceAdminDTO;
import nbd.gV.data.datahandling.dto.UserDTO;
import nbd.gV.data.datahandling.mappers.AdminMapper;
import nbd.gV.data.datahandling.mappers.ClientMapper;
import nbd.gV.data.datahandling.mappers.ResourceAdminMapper;
import nbd.gV.exceptions.MyMongoException;
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
public class UserMongoRepository extends AbstractMongoRepositoryNew<User> {

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

    private boolean createNew(UserDTO dto) {
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
                    client.getLogin(), client.getClientTypeName());
            if (!read(Filters.eq("login", client.getLogin()), Client.class).isEmpty()) {
                throw new UserLoginException("Nie udalo sie zarejestrowac klienta w bazie! - klient o tym loginie " +
                        "znajduje sie juz w bazie");
            }

            if (!createNew(ClientMapper.toMongoUser((Client) newUser))) {
                throw new UserException("Nie udalo sie zarejestrowac klienta w bazie! - brak odpowiedzi");
            }
        } else if (initUser instanceof Admin admin) {
            newUser = new Admin(UUID.randomUUID(), admin.getLogin());
            if (!read(Filters.eq("login", admin.getLogin()), Admin.class).isEmpty()) {
                throw new UserLoginException("Nie udalo sie zarejestrowac administratora w bazie! - admin o tym loginie " +
                        "znajduje sie juz w bazie");
            }

            if (!createNew(AdminMapper.toMongoUser((Admin) newUser))) {
                throw new UserException("Nie udalo sie zarejestrowac administratora w bazie! - brak odpowiedzi");
            }
        } else if (initUser instanceof ResourceAdmin resourceAdmin) {
            newUser = new ResourceAdmin(UUID.randomUUID(), resourceAdmin.getLogin());
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
        Class<? extends UserDTO> clazzDTO = switch (clazz.getSimpleName().toLowerCase()) {
            case "client" -> ClientDTO.class;
            case "admin" -> AdminDTO.class;
            case "resourceadmin" -> ResourceAdminDTO.class;
            default -> {
                throw new UnexpectedTypeException("Typ danego uzytkownika nie pasuje do zadnego z obslugiwanych!");
            }
        };

        List<User> list = new ArrayList<>();
        for (var userDTO : this.getDatabase().getCollection(COLLECTION_NAME, clazzDTO).find(filter).into(new ArrayList<>())) {
            if (userDTO instanceof ClientDTO clientDTO) {
                list.add(ClientMapper.fromMongoUser(clientDTO));
            } else if (userDTO instanceof AdminDTO adminDTO) {
                list.add(AdminMapper.fromMongoUser(adminDTO));
            } else if (userDTO instanceof ResourceAdminDTO resourceAdminDTO) {
                list.add(ResourceAdminMapper.fromMongoUser(resourceAdminDTO));
            }
        }
        return list;
    }

    public List<User> readAll(Class<? extends User> clazz) {
        String name = clazz.getSimpleName().toLowerCase();
        return this.read(Filters.eq("_clazz", name.substring(0, name.length() - 3)), clazz);
    }

    public User readByUUID(UUID uuid, Class<? extends User> clazz) {
        Bson filter = Filters.eq("_id", uuid.toString());
        var list = this.read(filter, clazz);
        return !list.isEmpty() ? list.get(0) : null;
    }

    @Override
    public boolean updateByReplace(UUID uuid, User user) {
        Bson filter = Filters.eq("_id", uuid.toString());
        UpdateResult result;

        if (user instanceof Client client) {
            result = getCollection().replaceOne(filter, ClientMapper.toMongoUser(client));;
        } else if (user instanceof Admin admin) {
            result = getCollection().replaceOne(filter, AdminMapper.toMongoUser(admin));;
        } else if (user instanceof ResourceAdmin resourceAdmin) {
            result = getCollection().replaceOne(filter, ResourceAdminMapper.toMongoUser(resourceAdmin));;
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

//    @Override
//    public boolean delete(UUID uuid) {
//        throw new UnsupportedOperationException();
//    }

    @PostConstruct
    private void init() {
        destroy();

        createNew(ClientMapper.toMongoUser(new Client(UUID.fromString("80e62401-6517-4392-856c-e22ef5f3d6a2"), "Johnny", "Brown", "login", "normal")));
        createNew(ClientMapper.toMongoUser(new Client(UUID.fromString("b6f5bcb8-7f01-4470-8238-cc3320326157"), "Rose", "Tetris", "login15", "athlete")));
        createNew(ClientMapper.toMongoUser(new Client(UUID.fromString("6dc63417-0a21-462c-a97a-e0bf6055a3ea"), "John", "Lee", "leeJo15", "coach")));
        createNew(ClientMapper.toMongoUser(new Client(UUID.fromString("3a722080-9668-42a2-9788-4695a4b9f5a7"), "Krzysztof", "Scala", "scKrzy", "normal")));
        createNew(ClientMapper.toMongoUser(new Client(UUID.fromString("126778af-0e19-46d4-b329-0b6b92548f9a"), "Adam", "Scout", "scAdam", "normal")));

        createNew(AdminMapper.toMongoUser(new Admin(UUID.fromString("3b197615-6931-4aad-941a-44f78f527053"), "mainAdmin1@example")));
        createNew(AdminMapper.toMongoUser(new Admin(UUID.fromString("4844c398-5cf1-44e0-a6d8-34c8a939d2ea"), "secondAdmin2@example")));

        createNew(ResourceAdminMapper.toMongoUser(new ResourceAdmin(UUID.fromString("83b29a7a-aa96-4ff2-823d-f3d0d6372c94"), "admRes1@test")));
        createNew(ResourceAdminMapper.toMongoUser(new ResourceAdmin(UUID.fromString("a2f6cb49-5e9d-4069-ab91-f337224e833a"), "admRes2@test")));
    }

    @PreDestroy
    private void destroy() {
        getCollection().deleteMany(Filters.empty());
    }
}
