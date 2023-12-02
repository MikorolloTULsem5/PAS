package integrationtests;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import nbd.gV.data.repositories.UserMongoRepository;
import nbd.gV.restapi.services.userservice.ClientService;
import org.bson.UuidRepresentation;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.Conventions;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.junit.jupiter.api.Test;

import java.util.List;

public class NewCleaningClassForTests {
    private static final CodecRegistry pojoCodecRegistry = CodecRegistries.fromProviders(PojoCodecProvider.builder()
            .automatic(true)
            .conventions(List.of(Conventions.ANNOTATION_CONVENTION))
            .build());

    private static final MongoClientSettings settings = MongoClientSettings.builder()
            .credential(MongoCredential.createCredential("admin", "admin", "adminpassword".toCharArray()))
            .applyConnectionString(new ConnectionString("mongodb+srv://Michal:ZvDI3RNUGeTKjHTU@atlascluster.pweqkng.mongodb.net/"))
            .uuidRepresentation(UuidRepresentation.STANDARD)
            .codecRegistry(CodecRegistries.fromRegistries(
                    MongoClientSettings.getDefaultCodecRegistry(),
                    pojoCodecRegistry
            ))
            .build();
    private static final MongoDatabase mongoDatabase = MongoClients.create(settings).getDatabase("reserveACourt");

    static void cleanUsers() {
        mongoDatabase.getCollection("users").deleteMany(Filters.empty());
    }

    static void cleanCourts() {
        mongoDatabase.getCollection("courts").deleteMany(Filters.empty());
    }

    static void cleanReservations() {
        mongoDatabase.getCollection("reservations").deleteMany(Filters.empty());
    }

    static void cleanAll() {
        cleanReservations();
        cleanUsers();
        cleanCourts();
    }

    static void initClients() {
        ClientService clientServiceTest = new ClientService(new UserMongoRepository());
        cleanUsers();
        clientServiceTest.registerClient("Adam", "Smith", "loginek", "normal");
        clientServiceTest.registerClient("Eva", "Braun", "loginek13", "athlete");
        clientServiceTest.registerClient("Michal", "Pi", "michas13", "coach");
    }

    @Test
    void test() {
        cleanAll();
    }
}
