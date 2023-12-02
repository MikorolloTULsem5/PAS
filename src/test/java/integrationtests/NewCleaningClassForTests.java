package integrationtests;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import nbd.gV.data.repositories.CourtMongoRepository;
import nbd.gV.data.repositories.ReservationMongoRepository;
import nbd.gV.data.repositories.UserMongoRepository;
import nbd.gV.model.courts.Court;
import nbd.gV.model.users.Client;
import nbd.gV.model.reservations.Reservation;
import nbd.gV.restapi.services.CourtService;
import nbd.gV.restapi.services.ReservationService;
import nbd.gV.restapi.services.userservice.ClientService;
import org.bson.UuidRepresentation;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.Conventions;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.Month;
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

    static Client client1;
    static Client client2;
    static Client client3;

    static Court court1;
    static Court court2;
    static Court court3;
    static Court court4;
    static Court court5;

    static Reservation reservation1;
    static Reservation reservation2;
    static Reservation reservation3;
    static Reservation reservation4;
    static Reservation reservation5;
    static Reservation reservation6;
    static Reservation reservation7;

    static final LocalDateTime dataStart = LocalDateTime.of(2023, Month.NOVEMBER, 30, 14, 20);

    static void cleanAll() {
        cleanReservations();
        cleanUsers();
        cleanCourts();
    }

    static void initClients() {
        ClientService clientServiceTest = new ClientService(new UserMongoRepository());
        cleanUsers();
        client1 = clientServiceTest.registerClient("Adam", "Smith", "loginek", "normal");
        client2 = clientServiceTest.registerClient("Eva", "Braun", "loginek13", "athlete");
        client3 = clientServiceTest.registerClient("Michal", "Pi", "michas13", "coach");
    }

    static void initCourts() {
        CourtService courtServiceTest = new CourtService(new CourtMongoRepository());
        cleanCourts();
        court1 = courtServiceTest.registerCourt(100, 100, 1);
        court2 = courtServiceTest.registerCourt(100, 200, 2);
        court3 = courtServiceTest.registerCourt(300, 200, 3);
        court4 = courtServiceTest.registerCourt(300, 200, 4);
        court5 = courtServiceTest.registerCourt(300, 200, 6);
    }

    static void initReservations() {
        ReservationService reservationServiceTest = new ReservationService(new ReservationMongoRepository());
        cleanAll();
        initClients();
        initCourts();
        reservation1 = reservationServiceTest.makeReservation(client1.getId(), court1.getId(), dataStart);
        reservation2 = reservationServiceTest.makeReservation(client2.getId(), court2.getId(), dataStart);
        reservation3 = reservationServiceTest.makeReservation(client3.getId(), court3.getId(), LocalDateTime.of(2023, Month.NOVEMBER, 28, 14, 20));
        reservationServiceTest.returnCourt(court3.getId(), dataStart);

        //Extra for getters
        reservation4 = reservationServiceTest.makeReservation(client2.getId(), court3.getId(), LocalDateTime.of(2023, Month.NOVEMBER, 28, 15, 0));
        reservationServiceTest.returnCourt(court3.getId(), LocalDateTime.of(2023, Month.DECEMBER, 2, 12, 20));
        reservation5 = reservationServiceTest.makeReservation(client3.getId(), court4.getId(), dataStart);
        reservationServiceTest.returnCourt(court4.getId(), LocalDateTime.of(2023, Month.DECEMBER, 1, 14, 20));
        reservation6 = reservationServiceTest.makeReservation(client1.getId(), court3.getId(), LocalDateTime.of(2023, Month.DECEMBER, 15, 10,0));
        reservation7 = reservationServiceTest.makeReservation(client3.getId(), court5.getId(),  LocalDateTime.of(2023, Month.DECEMBER, 16, 10,0));
    }

    @Test
    void test() {
//        cleanAll();
    }
}
